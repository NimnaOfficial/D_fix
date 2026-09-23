import re

path = 'app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

imports = """import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
"""
content = content.replace('import android.os.Looper;', 'import android.os.Looper;\n' + imports)

launcher = """    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private File getFileFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return new File(path);
        }
        return null;
    }

    private void uploadImage(File file) {
        String token = sessionManager.getBearerToken();
        if (token == null) return;
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();
        
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        apiService.uploadFile("Bearer " + token, filePart).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null && Boolean.TRUE.equals(response.body().get("success"))) {
                    String url = (String) response.body().get("url");
                    sendMessage("Sent an image", url);
                } else {
                    Toast.makeText(MessagesActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(MessagesActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
"""

content = content.replace('private Runnable fetchRunnable;', 'private Runnable fetchRunnable;\n' + launcher)

setup = """
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            File file = getFileFromUri(imageUri);
                            if (file != null && file.exists()) {
                                uploadImage(file);
                            } else {
                                Toast.makeText(this, "Could not read image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
"""

content = content.replace('adapter = new MessageAdapter(userId);', setup + '\n        adapter = new MessageAdapter(userId);')

click_listener = """btnSendImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });"""

content = re.sub(r'btnSendImage\.setOnClickListener\([^}]+\}\);', click_listener, content)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("MessagesActivity image picker added")
