import re

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace the camera_request listener
replacement = """
        getSupportFragmentManager().setFragmentResultListener("camera_request", this, (requestKey, bundle) -> {
            String localPath = bundle.getString("local_file_path");
            if (localPath != null && !localPath.isEmpty()) {
                File file = new java.io.File(localPath);
                if (file.exists()) {
                    uploadImage(file);
                } else {
                    Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Fallback for any old logic returning image_url
                String imageUrl = bundle.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    sendMessage("Sent an image", imageUrl);
                }
            }
        });
"""

# Regex to find the existing block
pattern = r'getSupportFragmentManager\(\)\.setFragmentResultListener\("camera_request", this, \(requestKey, bundle\) -> \{.*?\}\);'

content = re.sub(pattern, replacement.strip(), content, flags=re.DOTALL)

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated MessagesActivity to use local_file_path")
