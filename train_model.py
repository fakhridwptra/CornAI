import os
import shutil
import sys

def check_gpu():
    print("=" * 60)
    print(" CHECKING SYSTEM GPU & PYTORCH CONFIGURATION")
    print("=" * 60)
    try:
        import torch
        print(f"PyTorch Version: {torch.__version__}")
        cuda_available = torch.cuda.is_available()
        print(f"CUDA (GPU) Available: {cuda_available}")
        if cuda_available:
            print(f"GPU Device Name: {torch.cuda.get_device_name(0)}")
            return "0"
        else:
            print("\n[WARNING] PyTorch is running on CPU.")
            print("Training YOLO on CPU will be extremely slow (could take hours/days).")
            print("Since you have an NVIDIA GeForce RTX 3050 GPU, you can run training on GPU!")
            print("To enable GPU training, please run the following command in your terminal first:")
            print("\n    pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121\n")
            print("Continuing on CPU for now...")
            return "cpu"
    except ImportError:
        print("[ERROR] PyTorch is not installed. Please install it using:")
        print("pip install torch torchvision torchaudio")
        sys.exit(1)

def merge_datasets():
    print("\n" + "=" * 60)
    print(" PREPARING & MERGING DATASETS")
    print("=" * 60)
    
    daun_dir = r"C:\CornAI\DATA SEMUA\Dataset_Revisi (+google)\Daun"
    tongkol_dir = r"C:\CornAI\DATA SEMUA\Dataset_Revisi (+google)\Tongkol"
    merged_dir = r"C:\CornAI\DATA SEMUA\Dataset_Merged"
    
    if not os.path.exists(daun_dir):
        print(f"[ERROR] Leaf dataset folder not found at: {daun_dir}")
        sys.exit(1)
    if not os.path.exists(tongkol_dir):
        print(f"[ERROR] Cob dataset folder not found at: {tongkol_dir}")
        sys.exit(1)
        
    print(f"Source Daun (Leaf): {daun_dir}")
    print(f"Source Tongkol (Cob): {tongkol_dir}")
    print(f"Target Merged Path: {merged_dir}")
    
    # If merged dir exists, ask/recreate
    if os.path.exists(merged_dir):
        print(f"Target directory {merged_dir} already exists. Cleaning and recreating...")
        shutil.rmtree(merged_dir)
        
    splits_map = {
        "train": "train",
        "valid": "val",  # YOLO classification uses 'val' folder
        "test": "test"
    }
    
    # Copy files
    for src_split, dest_split in splits_map.items():
        dest_split_dir = os.path.join(merged_dir, dest_split)
        os.makedirs(dest_split_dir, exist_ok=True)
        
        # Copy Daun classes
        src_daun_split = os.path.join(daun_dir, src_split)
        if os.path.exists(src_daun_split):
            for class_name in os.listdir(src_daun_split):
                src_class_path = os.path.join(src_daun_split, class_name)
                dest_class_path = os.path.join(dest_split_dir, class_name)
                if os.path.isdir(src_class_path):
                    print(f"Copying Daun [{src_split}] -> {class_name}...")
                    shutil.copytree(src_class_path, dest_class_path)
                    
        # Copy Tongkol classes
        src_tongkol_split = os.path.join(tongkol_dir, src_split)
        if os.path.exists(src_tongkol_split):
            for class_name in os.listdir(src_tongkol_split):
                src_class_path = os.path.join(src_tongkol_split, class_name)
                dest_class_path = os.path.join(dest_split_dir, class_name)
                if os.path.isdir(src_class_path):
                    print(f"Copying Tongkol [{src_split}] -> {class_name}...")
                    shutil.copytree(src_class_path, dest_class_path)
                    
    print("\nDataset successfully merged!")
    print("Classes in train directory:")
    train_classes = os.listdir(os.path.join(merged_dir, "train"))
    for idx, cls in enumerate(sorted(train_classes)):
        print(f"  {idx}: {cls}")
    return merged_dir

def train_yolo(merged_dataset_path, device_setting):
    print("\n" + "=" * 60)
    print(" STARTING YOLOv11 CLASSIFICATION TRAINING")
    print("=" * 60)
    
    try:
        from ultralytics import YOLO
    except ImportError:
        print("[ERROR] Ultralytics is not installed. Please run: pip install ultralytics")
        sys.exit(1)
        
    # Load a pre-trained YOLOv11 classification model
    # It will auto-download 'yolo11n-cls.pt' if not present locally
    print("Loading pre-trained YOLOv11 classification weights (yolo11n-cls.pt)...")
    model = YOLO("yolo11n-cls.pt")
    
    epochs = 30 # User can increase this (e.g., 50 or 100) for better accuracy
    imgsz = 224
    batch_size = 32
    
    print(f"Training parameters:")
    print(f"  - Dataset Path: {merged_dataset_path}")
    print(f"  - Epochs: {epochs}")
    print(f"  - Image Size: {imgsz}x{imgsz}")
    print(f"  - Batch Size: {batch_size}")
    print(f"  - Device: {device_setting}")
    
    # Train
    results = model.train(
        data=merged_dataset_path,
        epochs=epochs,
        imgsz=imgsz,
        batch=batch_size,
        device=device_setting,
        workers=4,
        project="CornAI_Training",
        name="yolo11_classification"
    )
    
    print("\nTraining completed successfully!")
    
    # Path to best model
    best_weights = os.path.join("CornAI_Training", "yolo11_classification", "weights", "best.pt")
    if os.path.exists(best_weights):
        print(f"Best weights saved at: {os.path.abspath(best_weights)}")
        return best_weights
    else:
        print("[WARNING] Could not find best.pt weights path automatically.")
        return None

def export_and_deploy(best_weights_path):
    if not best_weights_path or not os.path.exists(best_weights_path):
        print("[ERROR] No valid weights to export.")
        return
        
    print("\n" + "=" * 60)
    print(" EXPORTING MODEL TO TFLITE & DEPLOYING TO APP")
    print("=" * 60)
    
    try:
        from ultralytics import YOLO
        model = YOLO(best_weights_path)
        print("Exporting model to TFLite format (224x224)...")
        # Exporting yolov11 best weights to TFLite
        export_path = model.export(format="tflite", imgsz=224)
        print(f"Export completed: {export_path}")
        
        # YOLO export usually creates a directory like best_saved_model/best_float32.tflite
        # Let's locate the generated .tflite file
        tflite_src = None
        if os.path.isdir(export_path):
            # If export_path is a directory
            for f in os.listdir(export_path):
                if f.endswith(".tflite"):
                    tflite_src = os.path.join(export_path, f)
                    break
        elif os.path.isfile(export_path) and export_path.endswith(".tflite"):
            tflite_src = export_path
            
        if not tflite_src:
            # Search manually in runs/project dir
            parent_dir = os.path.dirname(best_weights_path)
            # Find in parent directory
            for root, dirs, files in os.walk(parent_dir):
                for f in files:
                    if f.endswith(".tflite"):
                        tflite_src = os.path.join(root, f)
                        break
                if tflite_src:
                    break
                    
        if tflite_src and os.path.exists(tflite_src):
            print(f"Found TFLite file: {tflite_src}")
            # Target path in Android Assets
            assets_dir = r"C:\CornAI\app\src\main\assets"
            os.makedirs(assets_dir, exist_ok=True)
            
            dest_tflite = os.path.join(assets_dir, "model_cornai.tflite")
            print(f"Deploying model to: {dest_tflite}")
            shutil.copy2(tflite_src, dest_tflite)
            
            # Also deploy labels.txt
            dest_labels = os.path.join(assets_dir, "labels.txt")
            print(f"Updating labels file: {dest_labels}")
            
            # Get classes alphabetically
            train_dir = r"C:\CornAI\DATA SEMUA\Dataset_Merged\train"
            classes = sorted(os.listdir(train_dir))
            with open(dest_labels, "w") as lf:
                for cls in classes:
                    lf.write(f"{cls}\n")
            print("Model and labels successfully deployed to Android app assets!")
        else:
            print("[ERROR] Could not find the generated TFLite file.")
            
    except Exception as e:
        print(f"[ERROR] Failed during export or deployment: {e}")

if __name__ == "__main__":
    # 1. Check GPU
    device_setting = check_gpu()
    
    # 2. Merge Datasets
    merged_path = merge_datasets()
    
    # 3. Ask to start training
    ans = input("\nDo you want to start the training now? (y/n): ").strip().lower()
    if ans == 'y':
        best_weights = train_yolo(merged_path, device_setting)
        # 4. Export and Deploy to Assets
        export_and_deploy(best_weights)
        print("\nAll processes finished! Open Android Studio to build and run the app.")
    else:
        print("\nDataset has been prepared. You can start the training anytime by running this script and typing 'y'.")
