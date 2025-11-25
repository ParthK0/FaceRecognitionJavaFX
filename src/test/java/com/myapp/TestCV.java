package com.myapp;

import org.bytedeco.opencv.global.opencv_core;

public class TestCV {
    public static void main(String[] args) {
        try {
            System.out.println("OpenCV Version: " + opencv_core.CV_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
