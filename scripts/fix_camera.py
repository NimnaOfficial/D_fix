import re

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = """
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                try {
                    if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                    } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                    } else {
                        Toast.makeText(getContext(), "No camera available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA; // fallback
                }
"""

content = content.replace("CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;", replacement.strip())

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated CameraSelector logic")
