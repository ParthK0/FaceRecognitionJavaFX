package com.myapp.ml;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.Net;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_dnn;
import org.bytedeco.opencv.global.opencv_imgproc;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * FaceNet-based face embedding generator
 * Generates 128-dimensional face embeddings for recognition
 * Uses a pre-trained FaceNet model via OpenCV DNN
 */
public class FaceNetEmbeddingGenerator {
    private Net net;
    private static final int EMBEDDING_SIZE = 128;
    private static final int INPUT_WIDTH = 160;
    private static final int INPUT_HEIGHT = 160;
    
    // Pre-trained FaceNet model (OpenFace implementation)
    private static final String MODEL_URL = 
        "https://github.com/pyannote/pyannote-data/raw/master/openface.nn4.small2.v1.t7";
    
    private String modelPath;

    /**
     * Initialize the FaceNet embedding generator
     */
    public FaceNetEmbeddingGenerator() throws Exception {
        initializeModel();
        loadModel();
    }

    /**
     * Download and initialize model file if not present
     */
    private void initializeModel() throws Exception {
        File modelDir = new File("models/facenet");
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        modelPath = "models/facenet/openface.nn4.small2.v1.t7";
        File modelFile = new File(modelPath);

        // Download model if not exists (this is ~30MB)
        if (!modelFile.exists()) {
            System.out.println("Downloading FaceNet model (this may take a few minutes)...");
            downloadFile(MODEL_URL, modelPath);
        }
    }

    /**
     * Load the FaceNet model
     */
    private void loadModel() throws Exception {
        try {
            net = opencv_dnn.readNetFromTorch(modelPath);
            if (net.empty()) {
                throw new Exception("Failed to load FaceNet model");
            }
            System.out.println("✓ FaceNet embedding generator loaded successfully");
        } catch (Exception e) {
            throw new Exception("Failed to initialize FaceNet: " + e.getMessage());
        }
    }

    /**
     * Generate face embedding from a face image
     * @param faceImage Face image (should be aligned and cropped to just the face)
     * @return 128-dimensional embedding vector
     */
    public float[] generateEmbedding(Mat faceImage) {
        if (faceImage.empty()) {
            return null;
        }

        // Resize face to model input size
        Mat resizedFace = new Mat();
        opencv_imgproc.resize(faceImage, resizedFace, new Size(INPUT_WIDTH, INPUT_HEIGHT));

        // Convert to RGB if needed
        Mat rgbFace = new Mat();
        if (resizedFace.channels() == 1) {
            opencv_imgproc.cvtColor(resizedFace, rgbFace, opencv_imgproc.COLOR_GRAY2RGB);
        } else {
            opencv_imgproc.cvtColor(resizedFace, rgbFace, opencv_imgproc.COLOR_BGR2RGB);
        }

        // Create input blob (normalize to [0, 1])
        Mat blob = opencv_dnn.blobFromImage(
            rgbFace,
            1.0 / 255.0,
            new Size(INPUT_WIDTH, INPUT_HEIGHT),
            new Scalar(0, 0, 0, 0),
            true,
            false,
            opencv_core.CV_32F
        );

        // Forward pass
        net.setInput(blob);
        Mat embedding = net.forward();

        // Extract embedding as float array
        float[] embeddingArray = new float[EMBEDDING_SIZE];
        FloatBuffer buffer = embedding.createBuffer();
        buffer.get(embeddingArray);

        // Normalize the embedding (L2 normalization)
        embeddingArray = normalizeEmbedding(embeddingArray);

        // Clean up
        resizedFace.close();
        rgbFace.close();
        blob.close();
        embedding.close();

        return embeddingArray;
    }

    /**
     * L2 normalize the embedding vector
     */
    private float[] normalizeEmbedding(float[] embedding) {
        float norm = 0.0f;
        for (float val : embedding) {
            norm += val * val;
        }
        norm = (float) Math.sqrt(norm);

        if (norm > 0) {
            float[] normalized = new float[embedding.length];
            for (int i = 0; i < embedding.length; i++) {
                normalized[i] = embedding[i] / norm;
            }
            return normalized;
        }
        return embedding;
    }

    /**
     * Calculate cosine similarity between two embeddings
     * Returns value between -1 and 1 (higher is more similar)
     */
    public static double calculateSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1 == null || embedding2 == null || 
            embedding1.length != embedding2.length) {
            return -1.0;
        }

        double dotProduct = 0.0;
        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
        }

        return dotProduct;
    }

    /**
     * Calculate Euclidean distance between two embeddings
     * Returns value >= 0 (lower is more similar)
     */
    public static double calculateDistance(float[] embedding1, float[] embedding2) {
        if (embedding1 == null || embedding2 == null || 
            embedding1.length != embedding2.length) {
            return Double.MAX_VALUE;
        }

        double distance = 0.0;
        for (int i = 0; i < embedding1.length; i++) {
            double diff = embedding1[i] - embedding2[i];
            distance += diff * diff;
        }

        return Math.sqrt(distance);
    }

    /**
     * Convert embedding array to byte array for database storage
     */
    public static byte[] embeddingToBytes(float[] embedding) {
        ByteBuffer buffer = ByteBuffer.allocate(embedding.length * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float value : embedding) {
            buffer.putFloat(value);
        }
        return buffer.array();
    }

    /**
     * Convert byte array from database to embedding array
     */
    public static float[] bytesToEmbedding(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        float[] embedding = new float[bytes.length / 4];
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = buffer.getFloat();
        }
        return embedding;
    }

    /**
     * Download a file from URL
     */
    private void downloadFile(String urlString, String outputPath) throws Exception {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(60000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (connection.getResponseCode() != 200) {
            throw new Exception("Failed to download file: HTTP " + connection.getResponseCode());
        }

        long fileSize = connection.getContentLengthLong();
        System.out.println("Downloading " + fileSize / (1024 * 1024) + " MB...");

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(outputPath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;
            int lastPercent = -1;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                
                if (fileSize > 0) {
                    int percent = (int) ((totalRead * 100) / fileSize);
                    if (percent != lastPercent && percent % 10 == 0) {
                        System.out.println(percent + "% downloaded...");
                        lastPercent = percent;
                    }
                }
            }
        }
        System.out.println("Download complete!");
    }

    /**
     * Get embedding size
     */
    public int getEmbeddingSize() {
        return EMBEDDING_SIZE;
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
