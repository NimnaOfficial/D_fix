import re

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

replacement = """
                if (getArguments() != null && getArguments().getBoolean("return_url_only", false)) {
                    requireActivity().runOnUiThread(() -> {
                        android.os.Bundle result = new android.os.Bundle();
                        result.putString("local_file_path", capturedFile.getAbsolutePath());
                        getParentFragmentManager().setFragmentResult("camera_request", result);
                        getParentFragmentManager().popBackStack();
                    });
                    return;
                }

                // Show preview in UI immediately
"""

content = content.replace("// Show preview in UI immediately", replacement.strip())

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated CameraFragment to return immediately on capture")
