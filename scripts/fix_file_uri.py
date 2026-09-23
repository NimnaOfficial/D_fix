import re

path = 'app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

new_get_file = """    private File getFileFromUri(Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            File tempFile = File.createTempFile("upload_", ".jpg", getCacheDir());
            java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }"""

content = re.sub(r'    private File getFileFromUri\(Uri uri\) \{.*?    \}', new_get_file, content, flags=re.DOTALL)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Robust file extractor added")
