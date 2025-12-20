package com.myapp.ml;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_dnn;
import org.bytedeco.opencv.global.opencv_imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Deep Neural Network based face detector using OpenCV DNN module
 * Uses ResNet-based SSD model (more accurate than Haar Cascades)
 */
public class DNNFaceDetector {
    private Net net;
    private static final float CONFIDENCE_THRESHOLD = 0.5f;
    private static final int INPUT_WIDTH = 300;
    private static final int INPUT_HEIGHT = 300;
    
    // Pre-trained Caffe models for face detection
    private static final String PROTOTXT_URL = 
        "https://raw.githubusercontent.com/opencv/opencv/master/samples/dnn/face_detector/deploy.prototxt";
    private static final String MODEL_URL = 
        "https://github.com/opencv/opencv_3rdparty/raw/dnn_samples_face_detector_20170830/res10_300x300_ssd_iter_140000.caffemodel";
    
    private String prototxtPath;
    private String modelPath;

    /**
     * Initialize the DNN face detector
     */
    public DNNFaceDetector() throws Exception {
        initializeModel();
        loadModel();
    }

    /**
     * Download and initialize model files if not present
     */
    private void initializeModel() throws Exception {
        File modelDir = new File("models/face_detector");
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        prototxtPath = "models/face_detector/deploy.prototxt";
        modelPath = "models/face_detector/res10_300x300_ssd_iter_140000.caffemodel";

        File prototxtFile = new File(prototxtPath);
        File modelFile = new File(modelPath);

        // Download prototxt if not exists
        if (!prototxtFile.exists()) {
            System.out.println("Downloading face detector prototxt...");
            downloadFile(PROTOTXT_URL, prototxtPath);
        }

        // Download model if not exists (this is a large file ~10MB)
        if (!modelFile.exists()) {
            System.out.println("Downloading face detector model (this may take a few minutes)...");
            downloadFile(MODEL_URL, modelPath);
        }
    }

    /**
     * Load the DNN model
     */
    private void loadModel() throws Exception {
        try {
            net = opencv_dnn.readNetFromCaffe(prototxtPath, modelPath);
            if (net.empty()) {
                throw new Exception("Failed to load DNN model");
            }
            System.out.println("✓ DNN face detector loaded successfully");
        } catch (Exception e) {
            throw new Exception("Failed to initialize DNN face detector: " + e.getMessage());
        }
    }

    /**
     * Detect faces in an image using DNN
     * @param image Input image (BGR format)
     * @return List of detected face rectangles
     */
    public List<Rect> detectFaces(Mat image) {
        List<Rect> faces = new ArrayList<>();
        
        if (image.empty()) {
            return faces;
        }

        int imageHeight = image.rows();
        int imageWidth = image.cols();

        // Prepare input blob
        Mat blob = opencv_dnn.blobFromImage(
            image,
            1.0,
            new Size(INPUT_WIDTH, INPUT_HEIGHT),
            new Scalar(104.0, 177.0, 123.0, 0),
            false,
            false,
            opencv_core.CV_32F
        );

        // Forward pass
        net.setInput(blob);
        Mat detection = net.forward();

        // Detection output is a 4D matrix: [1, 1, N, 7]
        // where N is the number of detections and each detection is:
        // [batchId, classId, confidence, left, top, right, bottom]
        Mat detectionMat = new Mat(detection.size(2), detection.size(3), opencv_core.CV_32F, detection.ptr(0, 0));

        for (int i = 0; i < detectionMat.rows(); i++) {
            float confidence = detectionMat.ptr(i, 2).getFloat();

            if (confidence > CONFIDENCE_THRESHOLD) {
                int x1 = (int) (detectionMat.ptr(i, 3).getFloat() * imageWidth);
                int y1 = (int) (detectionMat.ptr(i, 4).getFloat() * imageHeight);
                int x2 = (int) (detectionMat.ptr(i, 5).getFloat() * imageWidth);
                int y2 = (int) (detectionMat.ptr(i, 6).getFloat() * imageHeight);

                // Ensure coordinates are within image bounds
                x1 = Math.max(0, x1);
                y1 = Math.max(0, y1);
                x2 = Math.min(imageWidth - 1, x2);
                y2 = Math.min(imageHeight - 1, y2);

                int width = x2 - x1;
                int height = y2 - y1;

                if (width > 0 && height > 0) {
                    faces.add(new Rect(x1, y1, width, height));
                }
            }
        }

        blob.close();
        detection.close();
        detectionMat.close();

        return faces;
    }

    /**
     * Detect the largest/most prominent face in an image
     */
    public Rect detectLargestFace(Mat image) {
        List<Rect> faces = detectFaces(image);
        if (faces.isEmpty()) {
            return null;
        }

        // Return the largest face by area
        Rect largestFace = faces.get(0);
        int maxArea = largestFace.width() * largestFace.height();

        for (Rect face : faces) {
            int area = face.width() * face.height();
            if (area > maxArea) {
                maxArea = area;
                largestFace = face;
            }
        }

        return largestFace;
    }

    /**
     * Download a file from URL
     */
    private void downloadFile(String urlString, String outputPath) throws Exception {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new Exception("Failed to download file: HTTP " + connection.getResponseCode());
        }

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(outputPath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * Clean up resources
     */
    public void close() {
        if (net != null && !net.isNull()) {
            net.close();
        }
    }
}
