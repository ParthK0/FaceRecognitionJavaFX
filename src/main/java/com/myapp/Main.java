package com.myapp;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    private static final String CASCADE_RESOURCE = "/haarcascade_frontalface_default.xml";
    private static final String CASCADE_URL = "https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_default.xml";

    public static void main(String[] args) {
        String cascadePath = ensureCascadeAvailable();
        if (cascadePath == null) {
            System.err.println("Failed to obtain Haar cascade file.");
            return;
        }

        CascadeClassifier faceCascade = new CascadeClassifier(cascadePath);
        if (faceCascade.empty()) {
            System.err.println("Failed to load cascade from: " + cascadePath);
            return;
        }

        VideoCapture cam = new VideoCapture(0);
        if (!cam.isOpened()) {
            System.out.println("Camera not detected!");
            return;
        }

        Mat frame = new Mat();
        Mat gray = new Mat();
        RectVector faces = new RectVector();

        try {
            while (cam.read(frame)) {
                // convert to grayscale and detect
                org.bytedeco.opencv.global.opencv_imgproc.cvtColor(frame, gray, org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY);
                faceCascade.detectMultiScale(gray, faces);

                for (int i = 0; i < faces.size(); i++) {
                    Rect r = faces.get(i);
                    org.bytedeco.opencv.global.opencv_imgproc.rectangle(frame, r, new Scalar(0, 255, 0, 0));
                }

                org.bytedeco.opencv.global.opencv_highgui.imshow("Camera", frame);
                int key = org.bytedeco.opencv.global.opencv_highgui.waitKey(30);
                if (key >= 0) break; // any key pressed will exit
            }
        } finally {
            cam.release();
            org.bytedeco.opencv.global.opencv_highgui.destroyAllWindows();
        }
    }

    private static String ensureCascadeAvailable() {
        try {
            // try resource on classpath first
            InputStream is = Main.class.getResourceAsStream(CASCADE_RESOURCE);
            if (is != null) {
                File tmp = File.createTempFile("haarcascade_frontalface_default", ".xml");
                try (FileOutputStream out = new FileOutputStream(tmp)) {
                    byte[] buf = new byte[8192];
                    int read;
                    while ((read = is.read(buf)) != -1) {
                        out.write(buf, 0, read);
                    }
                } finally {
                    try { is.close(); } catch (Exception ignored) {}
                }
                tmp.deleteOnExit();
                return tmp.getAbsolutePath();
            }

            // fallback: download from upstream to a temp file
            URL url = new URL(CASCADE_URL);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(15000);
            c.setRequestMethod("GET");
            int code = c.getResponseCode();
            if (code != 200) {
                System.err.println("Failed to download cascade, HTTP code=" + code);
                return null;
            }

            InputStream in = c.getInputStream();
            File tmp = File.createTempFile("haarcascade_frontalface_default", ".xml");
            try (FileOutputStream out = new FileOutputStream(tmp)) {
                byte[] buf = new byte[8192];
                int read;
                while ((read = in.read(buf)) != -1) {
                    out.write(buf, 0, read);
                }
            } finally {
                try { in.close(); } catch (Exception ignored) {}
            }
            tmp.deleteOnExit();
            return tmp.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
