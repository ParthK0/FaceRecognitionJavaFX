package com.myapp;

public class RunRecognizer {
    public static void main(String[] args) {
        System.out.println("Starting camera recognition (press 'q' in the window to cancel)...");
        String res = Recognizer.predictFromCamOnce();
        if (res != null) System.out.println("Recognized: " + res);
        else System.out.println("No face recognized or cancelled.");
        System.exit(0);
    }
}
