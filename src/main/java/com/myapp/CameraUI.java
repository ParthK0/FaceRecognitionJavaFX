package com.myapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CameraUI extends Application {
    private VideoCapture capture;
    private CascadeClassifier faceDetector;
    private ScheduledExecutorService timer;
    private ImageView imageView;
    private Label statusLabel;
    private Recognizer recognizer;

    @Override
    public void start(Stage primaryStage) {
        imageView = new ImageView();
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        imageView.setPreserveRatio(true);

        statusLabel = new Label("Camera stopped");
        Button startBtn = new Button("Start");
        Button stopBtn = new Button("Stop");

        startBtn.setOnAction(e -> startCamera());
        stopBtn.setOnAction(e -> stopCamera());

        HBox controls = new HBox(8, startBtn, stopBtn, statusLabel);

        BorderPane root = new BorderPane();
        root.setCenter(imageView);
        root.setBottom(controls);

        Scene scene = new Scene(root, 700, 560);
        primaryStage.setTitle("Face Recognition - Camera");
        primaryStage.setScene(scene);
        primaryStage.show();

        // prepare recognizer and detector but don't start capture yet
        recognizer = new Recognizer();
        String cascade = ensureCascadeAvailable();
        faceDetector = (cascade != null) ? new CascadeClassifier(cascade) : new CascadeClassifier();
    }

    private void startCamera() {
        if (timer != null && !timer.isShutdown()) return;
        capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            statusLabel.setText("Cannot open camera");
            return;
        }
        statusLabel.setText("Camera running");

        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(() -> {
            Mat frame = new Mat();
            if (!capture.read(frame) || frame.empty()) return;

            Mat gray = new Mat();
            opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
            RectVector faces = new RectVector();
            if (faceDetector != null) faceDetector.detectMultiScale(gray, faces);

            for (int i = 0; i < faces.size(); i++) {
                Rect r = faces.get(i);
                opencv_imgproc.rectangle(frame, r, new org.bytedeco.opencv.opencv_core.Scalar(0, 255, 0, 0));
                Mat face = new Mat(gray, r);
                String res = recognizer.predict(face);
                if (res != null) {
                    opencv_imgproc.putText(frame, res, new org.bytedeco.opencv.opencv_core.Point(r.x(), Math.max(r.y()-10, 10)), opencv_imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new org.bytedeco.opencv.opencv_core.Scalar(0,255,0,0));
                }
            }

            Image fxImage = matToImage(frame);
            if (fxImage != null) {
                Platform.runLater(() -> imageView.setImage(fxImage));
            }
        }, 0, 33, TimeUnit.MILLISECONDS); // ~30 FPS
    }

    private void stopCamera() {
        if (timer != null && !timer.isShutdown()) {
            timer.shutdown();
            try { timer.awaitTermination(100, TimeUnit.MILLISECONDS); } catch (InterruptedException ignored) {}
        }
        if (capture != null && capture.isOpened()) capture.release();
        Platform.runLater(() -> statusLabel.setText("Camera stopped"));
    }

    @Override
    public void stop() throws Exception {
        stopCamera();
        if (faceDetector != null) faceDetector.close();
        super.stop();
    }

    private Image matToImage(Mat frame) {
        try {
            Mat converted = new Mat();
            if (frame.channels() == 1) {
                opencv_imgproc.cvtColor(frame, converted, opencv_imgproc.COLOR_GRAY2BGR);
            } else {
                converted = frame;
            }
            int width = converted.cols();
            int height = converted.rows();
            int channels = converted.channels();
            byte[] sourcePixels = new byte[width * height * channels];
            converted.data().get(sourcePixels);

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
            return SwingFXUtils.toFXImage(img, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // copy of cascade helper (keeps Recognizer encapsulated)
    private static String ensureCascadeAvailable() {
        final String resourcePath = "/haarcascade_frontalface_default.xml";
        final String remote = "https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_default.xml";
        try (InputStream is = CameraUI.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                File tmp = File.createTempFile("haarcascade_frontalface_default", ".xml");
                try (FileOutputStream fos = new FileOutputStream(tmp)) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = is.read(buf)) != -1) fos.write(buf, 0, r);
                }
                if (tmp.length() > 50_000) {
                    tmp.deleteOnExit();
                    return tmp.getAbsolutePath();
                }
                try { tmp.delete(); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        try {
            URL url = new URL(remote);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(30000);
            c.setRequestMethod("GET");
            if (c.getResponseCode() != 200) return null;
            try (InputStream in = c.getInputStream()) {
                File tmp = File.createTempFile("haarcascade_frontalface_default", ".xml");
                try (FileOutputStream fos = new FileOutputStream(tmp)) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = in.read(buf)) != -1) fos.write(buf, 0, r);
                }
                if (tmp.length() > 50_000) {
                    tmp.deleteOnExit();
                    return tmp.getAbsolutePath();
                }
                try { tmp.delete(); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
