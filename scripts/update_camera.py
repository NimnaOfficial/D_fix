import re

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = r'''    private void uploadImage\(\) \{[\s\S]*?private void saveImageUrlToBackend'''

replacement = r'''    private void uploadImage() {
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

        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), capturedFile);
        okhttp3.MultipartBody.Part filePart = okhttp3.MultipartBody.Part.createFormData("file", capturedFile.getName(), requestBody);

        apiService.uploadFile("Bearer " + token, filePart).enqueue(new retrofit2.Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<java.util.Map<String, Object>> call, @NonNull retrofit2.Response<java.util.Map<String, Object>> response) {
                if (!isUiAvailable()) return;
                
                if (response.isSuccessful() && response.body() != null && Boolean.TRUE.equals(response.body().get("success"))) {
                    String url = (String) response.body().get("url");
                    if (getArguments() != null && getArguments().getBoolean("return_url_only", false)) {
                        android.os.Bundle result = new android.os.Bundle();
                        result.putString("image_url", url);
                        getParentFragmentManager().setFragmentResult("camera_request", result);
                        getParentFragmentManager().popBackStack();
                        return;
                    }
                    saveImageUrlToBackend(appointmentId, url);
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
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

    private void saveImageUrlToBackend'''

if re.search(target, content):
    content = re.sub(target, replacement, content)
    with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success updating CameraFragment")
else:
    print("Failed to find uploadImage method")
