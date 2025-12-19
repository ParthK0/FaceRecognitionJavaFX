package com.myapp;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainerMulti {
    public static void main(String[] args) {
        trainAll();
    }

    public static void trainAll() {
        String datasetRoot = "dataset";
        File root = new File(datasetRoot);
        if (!root.exists() || !root.isDirectory()) {
            System.out.println("Dataset root not found: " + datasetRoot);
            return;
        }

        Map<Integer, String> labels = new HashMap<>();
        List<Mat> faceList = new ArrayList<>();
        List<Integer> faceLabels = new ArrayList<>();

        int nextLabel = 1;

        String cascadePath = ensureCascadeAvailable();
        CascadeClassifier faceDetector = (cascadePath != null) ? new CascadeClassifier(cascadePath) : new CascadeClassifier();

        File[] persons = root.listFiles(File::isDirectory);
        if (persons == null || persons.length == 0) {
            System.out.println("No person subfolders found under dataset/");
            return;
        }

        for (File personDir : persons) {
            String personName = personDir.getName();
            System.out.println("Processing person: " + personName);
            File[] imgs = personDir.listFiles((d, name) -> {
                String n = name.toLowerCase();
                return n.endsWith(".jpg") || n.endsWith(".png") || n.endsWith(".jpeg");
            });
            if (imgs == null) continue;
            int label = nextLabel++;
            labels.put(label, personName);

            for (File imgFile : imgs) {
                Mat img = opencv_imgcodecs.imread(imgFile.getAbsolutePath());
                if (img == null || img.empty()) {
                    System.out.println("Skipping unreadable: " + imgFile.getName());
                    continue;
                }
                Mat gray = new Mat();
                opencv_imgproc.cvtColor(img, gray, opencv_imgproc.COLOR_BGR2GRAY);
                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(gray, faces);
                if (faces.size() == 0) {
                    System.out.println("No face detected in " + imgFile.getName() + " — skipping");
                    continue;
                }
                Rect r = faces.get(0);
                Mat face = new Mat(gray, r).clone();
                Mat resized = new Mat();
                opencv_imgproc.resize(face, resized, new org.bytedeco.opencv.opencv_core.Size(200,200));
                opencv_imgproc.equalizeHist(resized, resized);
                faceList.add(resized);
                faceLabels.add(label);
                System.out.println("Added face from " + imgFile.getName());
            }
        }

        if (faceList.isEmpty()) {
            System.out.println("No faces collected for training.");
            return;
        }

        MatVector images = new MatVector(faceList.size());
        Mat labelsMat = new Mat(faceList.size(), 1, opencv_core.CV_32SC1);
        for (int i = 0; i < faceList.size(); i++) {
            images.put(i, faceList.get(i));
            labelsMat.ptr(i).putInt(faceLabels.get(i));
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.train(images, labelsMat);

        File trainerDir = new File("trainer");
        if (!trainerDir.exists()) trainerDir.mkdirs();
        recognizer.save("trainer/multi.yml");

        // write labels.txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(trainerDir, "labels.txt")))) {
            for (Map.Entry<Integer, String> e : labels.entrySet()) {
                bw.write(e.getKey() + "=" + e.getValue());
                bw.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Training complete — model saved to trainer/multi.yml and labels.txt created.");
    }

    // same cascade helper as in CameraUI/Recognizer
    private static String ensureCascadeAvailable() {
        final String resourcePath = "/haarcascade_frontalface_default.xml";
        final String remote = "https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_default.xml";
        try (InputStream is = TrainerMulti.class.getResourceAsStream(resourcePath)) {
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
            URL url = URI.create(remote).toURL();
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
