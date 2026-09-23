import re

path = 'app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace uploadImage() and everything else. Wait, let's just find uploadImage().
start_idx = content.find('private void uploadImage() {')
if start_idx != -1:
    end_idx = content.find('private void fetchImages() {', start_idx)
    
    new_code = """private void uploadImage() {
        if (!isUiAvailable()) return;
        if (capturedFile == null || !capturedFile.exists()) {
            Toast.makeText(getContext(), "Please capture an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String appointmentId = selectedAppointmentId;
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            Toast.makeText(getContext(), "No appointment selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpload.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        okhttp3.MediaType MEDIA_TYPE = okhttp3.MediaType.parse("image/jpeg");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MEDIA_TYPE, capturedFile);
        okhttp3.MultipartBody.Part filePart = okhttp3.MultipartBody.Part.createFormData("file", capturedFile.getName(), requestBody);

        apiService.uploadFile("Bearer " + token, filePart).enqueue(new retrofit2.Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<java.util.Map<String, Object>> call, @NonNull retrofit2.Response<java.util.Map<String, Object>> response) {
                if (!isUiAvailable()) return;
                
                if (response.isSuccessful() && response.body() != null && Boolean.TRUE.equals(response.body().get("success"))) {
                    String url = (String) response.body().get("url");
                    saveImageUrlToBackend(appointmentId, url);
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<java.util.Map<String, Object>> call, @NonNull Throwable t) {
                if (!isUiAvailable()) return;
                progressBar.setVisibility(View.GONE);
                btnUpload.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageUrlToBackend(String appointmentId, String imageUrl) {
        if (!isUiAvailable()) return;
        String token = tokenManager.getToken();
        if (token == null) return;

        com.mad.techfix.models.ImageUploadRequest request = new com.mad.techfix.models.ImageUploadRequest(imageUrl, "BEFORE_REPAIR");
        apiService.uploadImage("Bearer " + token, appointmentId, request).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<com.mad.techfix.models.ApiResponse<Object>> call, @NonNull retrofit2.Response<com.mad.techfix.models.ApiResponse<Object>> response) {
                if (!isUiAvailable()) return;
                progressBar.setVisibility(View.GONE);
                btnUpload.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    capturedFile = null;
                    fetchImages();
                } else {
                    Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<com.mad.techfix.models.ApiResponse<Object>> call, @NonNull Throwable t) {
                if (!isUiAvailable()) return;
                progressBar.setVisibility(View.GONE);
                btnUpload.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    """
    content = content[:start_idx] + new_code + content[end_idx:]
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("CameraFragment updated")
else:
    print("uploadImage not found")
