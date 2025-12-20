"""
Dataset Generator for Face Recognition
Generates 500 sample face images for each person using data augmentation
"""

import cv2
import numpy as np
import os
from pathlib import Path
import random

class DatasetGenerator:
    def __init__(self, base_dataset_path="dataset"):
        self.base_path = Path(base_dataset_path)
        self.target_count = 500
        
    def augment_image(self, image):
        """Apply various augmentation techniques to create variations"""
        augmented_images = []
        
        # Original image
        augmented_images.append(image.copy())
        
        # Brightness variations (5 variations)
        for alpha in [0.7, 0.85, 1.0, 1.15, 1.3]:
            adjusted = cv2.convertScaleAbs(image, alpha=alpha, beta=0)
            augmented_images.append(adjusted)
        
        # Rotation variations (8 angles)
        h, w = image.shape[:2]
        center = (w // 2, h // 2)
        for angle in [-15, -10, -5, 0, 5, 10, 15, 20]:
            M = cv2.getRotationMatrix2D(center, angle, 1.0)
            rotated = cv2.warpAffine(image, M, (w, h))
            augmented_images.append(rotated)
        
        # Flip horizontal
        flipped = cv2.flip(image, 1)
        augmented_images.append(flipped)
        
        # Scale variations (3 variations)
        for scale in [0.9, 1.0, 1.1]:
            new_w = int(w * scale)
            new_h = int(h * scale)
            scaled = cv2.resize(image, (new_w, new_h))
            # Crop or pad to original size
            if scale > 1.0:
                # Crop from center
                start_x = (new_w - w) // 2
                start_y = (new_h - h) // 2
                scaled = scaled[start_y:start_y+h, start_x:start_x+w]
            else:
                # Pad
                pad_h = (h - new_h) // 2
                pad_w = (w - new_w) // 2
                scaled = cv2.copyMakeBorder(scaled, pad_h, h-new_h-pad_h, 
                                           pad_w, w-new_w-pad_w, 
                                           cv2.BORDER_REPLICATE)
            augmented_images.append(scaled)
        
        # Gaussian noise (3 variations)
        for sigma in [5, 10, 15]:
            noise = np.random.normal(0, sigma, image.shape).astype(np.uint8)
            noisy = cv2.add(image, noise)
            augmented_images.append(noisy)
        
        # Gaussian blur (2 variations)
        for kernel in [(3,3), (5,5)]:
            blurred = cv2.GaussianBlur(image, kernel, 0)
            augmented_images.append(blurred)
        
        # Contrast variations (3 variations)
        for beta in [-20, 0, 20]:
            adjusted = cv2.convertScaleAbs(image, alpha=1.0, beta=beta)
            augmented_images.append(adjusted)
        
        # Translation (4 variations)
        for tx, ty in [(5, 5), (-5, -5), (5, -5), (-5, 5)]:
            M = np.float32([[1, 0, tx], [0, 1, ty]])
            translated = cv2.warpAffine(image, M, (w, h))
            augmented_images.append(translated)
        
        return augmented_images
    
    def generate_from_existing(self, person_folder):
        """Generate 500 images from existing images in the folder"""
        person_path = self.base_path / person_folder
        
        if not person_path.exists():
            print(f"❌ Folder not found: {person_path}")
            return False
        
        # Get existing images
        existing_images = []
        for ext in ['*.jpg', '*.jpeg', '*.png']:
            existing_images.extend(list(person_path.glob(ext)))
        
        if len(existing_images) == 0:
            print(f"❌ No images found in {person_folder}")
            return False
        
        print(f"\n📁 Processing: {person_folder}")
        print(f"   Found {len(existing_images)} existing images")
        
        # Clear old generated images (keep originals)
        print(f"   Generating {self.target_count} images...")
        
        generated_count = 0
        img_index = 0
        
        while generated_count < self.target_count:
            # Cycle through existing images
            img_path = existing_images[img_index % len(existing_images)]
            img = cv2.imread(str(img_path))
            
            if img is None:
                print(f"   ⚠️ Could not read: {img_path}")
                img_index += 1
                continue
            
            # Generate augmented versions
            augmented = self.augment_image(img)
            
            # Save augmented images
            for aug_img in augmented:
                if generated_count >= self.target_count:
                    break
                
                output_path = person_path / f"{person_folder}_{generated_count+1:04d}.jpg"
                cv2.imwrite(str(output_path), aug_img)
                generated_count += 1
                
                if generated_count % 50 == 0:
                    print(f"   Progress: {generated_count}/{self.target_count}")
            
            img_index += 1
        
        print(f"   ✅ Generated {generated_count} images for {person_folder}")
        return True
    
    def generate_all(self):
        """Generate 500 images for all persons in dataset folder"""
        if not self.base_path.exists():
            print(f"❌ Dataset folder not found: {self.base_path}")
            return
        
        person_folders = [f.name for f in self.base_path.iterdir() if f.is_dir()]
        
        if len(person_folders) == 0:
            print("❌ No person folders found in dataset/")
            return
        
        print(f"\n{'='*60}")
        print(f"  DATASET GENERATOR - 500 IMAGES PER PERSON")
        print(f"{'='*60}")
        print(f"Found {len(person_folders)} persons: {', '.join(person_folders)}")
        
        success_count = 0
        for person in person_folders:
            if self.generate_from_existing(person):
                success_count += 1
        
        print(f"\n{'='*60}")
        print(f"  GENERATION COMPLETE!")
        print(f"{'='*60}")
        print(f"Successfully processed: {success_count}/{len(person_folders)} persons")
        print(f"Total images generated: {success_count * self.target_count}")
        print(f"{'='*60}\n")


def main():
    """Main function"""
    print("\n╔════════════════════════════════════════════════════════╗")
    print("║  Face Recognition Dataset Generator                   ║")
    print("║  Creates 500 images per person using augmentation     ║")
    print("╚════════════════════════════════════════════════════════╝\n")
    
    generator = DatasetGenerator()
    generator.generate_all()
    
    print("\n✅ Dataset generation complete!")
    print("📊 You can now train the face recognition model with 500 images per person")
    print("🚀 Use the GUI 'Train Students' feature to train the model\n")


if __name__ == "__main__":
    main()
