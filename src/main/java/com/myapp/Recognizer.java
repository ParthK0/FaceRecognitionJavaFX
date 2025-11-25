package com.myapp;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.DoublePointer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Recognizer {
    private LBPHFaceRecognizer recognizer;
    private Map<Integer,String> labels = new HashMap<>();

    public Recognizer() {
        Loader.load(opencv_core.class);
        recognizer = LBPHFaceRecognizer.create();
        File f = new File("trainer/multi.yml");
        if (f.exists()) {
            recognizer.read("trainer/multi.yml");
        } else {
            File single = new File("trainer/parth.yml");
            if (single.exists()) recognizer.read("trainer/parth.yml");
        }
        try (BufferedReader br = new BufferedReader(new FileReader("trainer/labels.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                labels.put(Integer.parseInt(parts[0]), parts[1]);
            }
        } catch (Exception ignored) {}

        // If no labels file was provided but a single-person trainer exists,
        // create a fallback mapping so the recognizer can report a name.
        if (labels.isEmpty()) {
            File single = new File("trainer/parth.yml");
            if (single.exists()) {
                labels.put(1, "parth");
            }
        }
    }

    // return matched username or null
    public String predict(Mat face) {
        IntPointer label = new IntPointer(1);
        DoublePointer conf = new DoublePointer(1);
        try {
            recognizer.predict(face, label, conf);
        } catch (RuntimeException ex) {
            // model not loaded or not trained; return null to indicate unknown
            return null;
        }
        int lab = label.get();
        double c = conf.get();
        String name = labels.get(lab);
        if (name == null && labels.isEmpty() && lab == 1) {
            // fallback when only single-person trainer is present
            name = "parth";
        }
        // LBPH lower confidence is better. Allow a generous threshold so
        // partial matches still surface; tune this value if you retrain.
        if (name != null && c < 100.0) {
            return name + ":" + c; // username:confidence
        }
        return null;
    }

    // helper used in UI flows to capture one frame and recognize
    public static String predictFromCamOnce() {
        String cascadePath = ensureCascadeAvailable();
        if (cascadePath == null) return null;
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);
        VideoCapture cam = new VideoCapture(0);
        if (!cam.isOpened()) {
            try { faceDetector.close(); } catch (Exception ignored) {}
            return null;
        }
        Mat frame = new Mat();
        try {
            while (true) {
                cam.read(frame);
                if (frame.empty()) continue;
                Mat gray = new Mat();
                opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(gray, faces);
                if (faces.size() > 0) {
                    Rect r = faces.get(0);
                    Mat face = new Mat(gray, r);
                    Recognizer rec = new Recognizer();
                    String res = rec.predict(face);
                    return res;
                }
                opencv_highgui.imshow("Scan - Press q to cancel", frame);
                if (opencv_highgui.waitKey(30) == 'q') break;
            }
            return null;
        } finally {
            try { cam.release(); } catch (Exception ignored) {}
            try { faceDetector.close(); } catch (Exception ignored) {}
            opencv_highgui.destroyAllWindows();
        }
    }

    // Ensure cascade is available: try classpath resource first, fall back to downloading official cascade.
    private static String ensureCascadeAvailable() {
        final String resourcePath = "/haarcascade_frontalface_default.xml";
        final String remote = "https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_default.xml";
        try (InputStream is = Recognizer.class.getResourceAsStream(resourcePath)) {
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

        // Download fallback
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
}
