import re

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'r', encoding='utf-8') as f:
    content = f.read()

# We will replace from private void uploadImage() { to private void fetchImages() {

new_content = '''    private void uploadImage() {
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

        apiService.getCloudinarySignature("Bearer " + token).enqueue(new retrofit2.Callback<CloudinarySignatureResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<CloudinarySignatureResponse> call, @NonNull retrofit2.Response<CloudinarySignatureResponse> response) {
                if (!isUiAvailable()) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    CloudinarySignatureResponse.CloudinaryData data = response.body().getData();
                    uploadToCloudinary(data, appointmentId);
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to get upload signature", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<CloudinarySignatureResponse> call, @NonNull Throwable t) {
                if (!isUiAvailable()) return;
                progressBar.setVisibility(View.GONE);
                btnUpload.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToCloudinary(CloudinarySignatureResponse.CloudinaryData data, String appointmentId) {
        okhttp3.MediaType MEDIA_TYPE_JPG = okhttp3.MediaType.parse("image/jpeg");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MEDIA_TYPE_JPG, capturedFile);

        okhttp3.MultipartBody.Builder builder = new okhttp3.MultipartBody.Builder()
                .setType(okhttp3.MultipartBody.FORM)
                .addFormDataPart("file", capturedFile.getName(), requestBody)
                .addFormDataPart("api_key", data.getApiKey())
                .addFormDataPart("timestamp", String.valueOf(data.getTimestamp()))
                .addFormDataPart("signature", data.getSignature())
                .addFormDataPart("folder", data.getFolder())
                .addFormDataPart("upload_preset", data.getUploadPreset());

        okhttp3.RequestBody cloudinaryBody = builder.build();
        String cloudinaryUrl = "https://api.cloudinary.com/v1_1/" + data.getCloudName() + "/image/upload";

        okhttp3.Request cloudinaryRequest = new okhttp3.Request.Builder()
                .url(cloudinaryUrl)
                .post(cloudinaryBody)
                .build();

        okHttpClient.newCall(cloudinaryRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(jsonResponse);
                        String secureUrl = jsonObject.getString("secure_url");

                        if (!isUiAvailable()) return;
                        requireActivity().runOnUiThread(() -> {
                            if (getArguments() != null && getArguments().getBoolean("return_url_only", false)) {
                                android.os.Bundle result = new android.os.Bundle();
                                result.putString("image_url", secureUrl);
                                getParentFragmentManager().setFragmentResult("camera_request", result);
                                getParentFragmentManager().popBackStack();
                                return;
                            }
                            saveImageUrlToBackend(appointmentId, secureUrl);
                        });

                    } catch (org.json.JSONException e) {
                        if (!isUiAvailable()) return;
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to parse Cloudinary response", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnUpload.setEnabled(true);
                        });
                    }
                } else {
                    if (!isUiAvailable()) return;
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Cloudinary upload failed", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnUpload.setEnabled(true);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                if (!isUiAvailable()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Cloudinary error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                });
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

    private void fetchImages() {'''

content = re.sub(r'    private void uploadImage\(\) \{.*?    private void fetchImages\(\) \{', new_content, content, flags=re.DOTALL)

with open('app/src/main/java/com/mad/techfix/ui/camera/CameraFragment.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched!")
