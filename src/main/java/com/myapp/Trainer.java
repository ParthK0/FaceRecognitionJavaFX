package com.myapp;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import java.io.File;

public class Trainer {
    public static void main(String[] args) {

        String datasetPath = "dataset/parth";
        File datasetDir = new File(datasetPath);
        File[] files = datasetDir.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("❌ Dataset empty or not found!");
            return;
        }

        MatVector images = new MatVector(files.length);
        Mat labels = new Mat(files.length, 1, org.bytedeco.opencv.global.opencv_core.CV_32SC1);

        int label = 1;  // 1 = Parth
        int counter = 0;

        for (File f : files) {
            if (!f.getName().toLowerCase().endsWith(".jpg")) continue;

            Mat img = opencv_imgcodecs.imread(f.getAbsolutePath(), 0);

            if (img.empty()) {
                System.out.println("Skipping: " + f.getName());
                continue;
            }

            images.put(counter, img);
            labels.ptr(counter).putInt(label);
            counter++;

            System.out.println("Added: " + f.getName());
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.train(images, labels);

        File trainerDir = new File("trainer");
        if (!trainerDir.exists()) trainerDir.mkdirs();

        recognizer.save("trainer/parth.yml");

        System.out.println("\n🎉 Training complete!");
        System.out.println("➡ Model saved as: trainer/parth.yml");
    }
}
