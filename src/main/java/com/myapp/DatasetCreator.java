package com.myapp;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
// using org.bytedeco.opencv.global.opencv_highgui functions

import java.io.File;

public class DatasetCreator {
    // Capture N images and store under dataset/<username>/
    public static void capture(String username, int N) {
        // Loader.load(opencv_core.class); // native loading handled by opencv-platform
        String path = "dataset/" + username;
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haarcascade_frontalface_default.xml");
        VideoCapture cam = new VideoCapture(0);
        if (!cam.isOpened()) { System.out.println("Camera not opened"); return; }

        Mat frame = new Mat();
        int count = 0;
        while (count < N) {
            cam.read(frame);
            if (frame.empty()) continue;
            Mat gray = new Mat();
            opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(gray, faces);
            for (int i = 0; i < faces.size(); i++) {
                Rect r = faces.get(i);
                Mat face = new Mat(gray, r);
                String file = path + "/" + (count+1) + ".jpg";
                opencv_imgcodecs.imwrite(file, face);
                System.out.println("Saved: " + file);
                count++;
                    opencv_imgproc.rectangle(frame, r, new Scalar(0,255,0,0));
                }
                opencv_highgui.imshow("Capture - " + username, frame);
                if (opencv_highgui.waitKey(30) == 'q') break;
        }
        cam.release();
        opencv_highgui.destroyAllWindows();
    }
}
