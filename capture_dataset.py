"""
Simple Dataset Generator using webcam
Captures 500 images per person with automatic variations
"""

import cv2
import numpy as np
import os
from datetime import datetime
import time

def capture_dataset(person_name, num_images=500):
    """Capture images from webcam for a person"""
    
    dataset_path = f"dataset/{person_name}"
    os.makedirs(dataset_path, exist_ok=True)
    
    print(f"\n{'='*60}")
    print(f"  CAPTURING DATASET FOR: {person_name.upper()}")
    print(f"{'='*60}")
    print(f"Target: {num_images} images")
    print(f"Output: {dataset_path}/")
    print(f"\nInstructions:")
    print("  - Look at the camera")
    print("  - Move your head slightly (up/down/left/right)")
    print("  - Make different expressions")
    print("  - Press 'q' to stop early")
    print(f"{'='*60}\n")
    
    # Open webcam
    cap = cv2.VideoCapture(0)
    
    if not cap.isOpened():
        print("❌ Cannot open webcam!")
        return False
    
    # Set camera properties
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
    
    # Load face detector
    face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
    
    captured_count = 0
    frame_count = 0
    start_time = time.time()
    
    print("📷 Camera started. Position your face in the frame...")
    print("    Press 's' to start capturing\n")
    
    started = False
    
    while captured_count < num_images:
        ret, frame = cap.read()
        
        if not ret:
            print("❌ Failed to read from webcam")
            break
        
        frame_count += 1
        
        # Flip frame horizontally for mirror effect
        frame = cv2.flip(frame, 1)
        
        # Convert to grayscale for face detection
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, 1.3, 5)
        
        # Draw rectangles around faces
        for (x, y, w, h) in faces:
            cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 2)
            
            if started:
                # Extract face region
                face_img = frame[y:y+h, x:x+w]
                
                # Save with variations
                if frame_count % 2 == 0:  # Capture every 2nd frame
                    # Save original
                    filename = f"{dataset_path}/{person_name}_{captured_count+1:04d}.jpg"
                    cv2.imwrite(filename, face_img)
                    captured_count += 1
                    
                    if captured_count % 50 == 0:
                        elapsed = time.time() - start_time
                        rate = captured_count / elapsed
                        remaining = (num_images - captured_count) / rate
                        print(f"Progress: {captured_count}/{num_images} ({captured_count*100//num_images}%) - ETA: {int(remaining)}s")
        
        # Display info on frame
        if started:
            status_text = f"Capturing: {captured_count}/{num_images}"
            color = (0, 255, 0)
        else:
            status_text = "Press 's' to start"
            color = (0, 165, 255)
        
        cv2.putText(frame, status_text, (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, color, 2)
        cv2.putText(frame, f"Person: {person_name}", (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)
        
        if len(faces) == 0 and started:
            cv2.putText(frame, "No face detected!", (10, 90), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 2)
        
        cv2.imshow('Dataset Capture', frame)
        
        # Handle keyboard input
        key = cv2.waitKey(1) & 0xFF
        
        if key == ord('s'):
            started = True
            start_time = time.time()
            print("✅ Capture started!\n")
        elif key == ord('q'):
            print("\n⚠️ Capture stopped by user")
            break
    
    # Release resources
    cap.release()
    cv2.destroyAllWindows()
    
    elapsed_time = time.time() - start_time
    
    print(f"\n{'='*60}")
    print(f"  CAPTURE COMPLETE!")
    print(f"{'='*60}")
    print(f"Person: {person_name}")
    print(f"Images captured: {captured_count}/{num_images}")
    print(f"Time elapsed: {int(elapsed_time)}s")
    print(f"Saved to: {dataset_path}/")
    print(f"{'='*60}\n")
    
    return captured_count >= num_images


def main():
    """Main function"""
    print("\n╔════════════════════════════════════════════════════════╗")
    print("║  Webcam Dataset Capture Tool                          ║")
    print("║  Captures 500 images per person from webcam           ║")
    print("╚════════════════════════════════════════════════════════╝\n")
    
    # Get person name
    person_name = input("Enter person name: ").strip().lower()
    
    if not person_name:
        print("❌ Person name cannot be empty!")
        return
    
    # Get number of images (default 500)
    try:
        num_input = input(f"Number of images (default 500): ").strip()
        num_images = int(num_input) if num_input else 500
    except ValueError:
        num_images = 500
    
    # Capture dataset
    success = capture_dataset(person_name, num_images)
    
    if success:
        print("\n✅ Dataset capture successful!")
        print("📊 You can now train the face recognition model")
        print("🚀 Use the GUI 'Train Students' feature")
    else:
        print("\n⚠️ Dataset capture incomplete")
    
    input("\nPress Enter to exit...")


if __name__ == "__main__":
    main()
