import re

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = """
        if (getArguments() != null && getArguments().getBoolean("return_url_only", false)) {
            android.os.Bundle result = new android.os.Bundle();
            result.putString("local_file_path", capturedFile.getAbsolutePath());
            getParentFragmentManager().setFragmentResult("camera_request", result);
            getParentFragmentManager().popBackStack();
            return;
        }

        btnUpload.setEnabled(false);
"""

content = content.replace("btnUpload.setEnabled(false);", replacement.strip())

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated CameraFragment to return local file path for chat")
