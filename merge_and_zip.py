import os
import shutil

def merge_and_zip():
    print("=" * 60)
    print(" MERGING AND ZIPPING DATASET FOR GOOGLE COLAB")
    print("=" * 60)
    
    daun_dir = r"C:\CornAI\DATA SEMUA\Dataset_Revisi (+google)\Daun"
    tongkol_dir = r"C:\CornAI\DATA SEMUA\Dataset_Revisi (+google)\Tongkol"
    merged_dir = r"C:\CornAI\DATA SEMUA\Dataset_Merged"
    zip_output = r"C:\CornAI\DATA SEMUA\dataset_merged"
    
    if not os.path.exists(daun_dir):
        print(f"[ERROR] Leaf dataset folder not found at: {daun_dir}")
        return
    if not os.path.exists(tongkol_dir):
        print(f"[ERROR] Cob dataset folder not found at: {tongkol_dir}")
        return
        
    # If merged dir exists, clean it
    if os.path.exists(merged_dir):
        print(f"Cleaning existing directory: {merged_dir}...")
        shutil.rmtree(merged_dir)
        
    splits_map = {
        "train": "train",
        "valid": "val",
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
                    print(f"Merging Daun [{src_split}] -> {class_name}...")
                    shutil.copytree(src_class_path, dest_class_path)
                    
        # Copy Tongkol classes
        src_tongkol_split = os.path.join(tongkol_dir, src_split)
        if os.path.exists(src_tongkol_split):
            for class_name in os.listdir(src_tongkol_split):
                src_class_path = os.path.join(src_tongkol_split, class_name)
                dest_class_path = os.path.join(dest_split_dir, class_name)
                if os.path.isdir(src_class_path):
                    print(f"Merging Tongkol [{src_split}] -> {class_name}...")
                    shutil.copytree(src_class_path, dest_class_path)
                    
    print("\nDataset successfully merged locally!")
    
    # Zip the merged directory
    print(f"\nZipping dataset to {zip_output}.zip...")
    shutil.make_archive(zip_output, 'zip', merged_dir)
    print(f"[SUCCESS] Zip file created at: {zip_output}.zip")
    print("This zip file is ready to be uploaded to Google Colab!")
    print("=" * 60)

if __name__ == "__main__":
    merge_and_zip()
