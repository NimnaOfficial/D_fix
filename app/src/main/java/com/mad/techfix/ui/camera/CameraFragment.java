package com.mad.techfix.ui.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.CloudinarySignatureResponse;
import com.mad.techfix.models.ImageUploadRequest;
import com.mad.techfix.models.RepairImage;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.customer.booking.RepairBookingFragment;
import com.mad.techfix.utils.TokenManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Callback;
import retrofit2.Callback;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CameraFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "CameraFragment";

    private PreviewView previewView;
    private MaterialAutoCompleteTextView etAppointmentId;
    private MaterialButton btnCapture, btnUpload, btnBookAppointment;
    private ProgressBar progressBar;
    private RecyclerView rvImages;

    private ImageCapture imageCapture;
    private File capturedFile;
    private ImageAdapter imageAdapter;
    private ApiService apiService;
    private TokenManager tokenManager;
    private boolean isCameraReady = false;
    private OkHttpClient okHttpClient;
    private String selectedAppointmentId;
    private final List<Appointment> availableAppointments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        previewView = view.findViewById(R.id.preview_view);
        etAppointmentId = view.findViewById(R.id.et_appointment_id);
        btnCapture = view.findViewById(R.id.btn_capture);
        btnUpload = view.findViewById(R.id.btn_upload);
        btnBookAppointment = view.findViewById(R.id.btn_book_appointment);
        progressBar = view.findViewById(R.id.progress_bar);
        rvImages = view.findViewById(R.id.rv_images);
        etAppointmentId.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position < availableAppointments.size()) {
                selectedAppointmentId = availableAppointments.get(position).getId();
                fetchImages();
            }
        });
        etAppointmentId.setOnClickListener(v -> etAppointmentId.showDropDown());

        btnCapture.setEnabled(false);

        // Setup RecyclerView
        rvImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(RepairImage image) {
                Toast.makeText(getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(RepairImage image) {
                deleteImage(image);
            }
        });
        rvImages.setAdapter(imageAdapter);

        // Init helpers
        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());
        okHttpClient = new OkHttpClient();
        loadCustomerAppointments();

        if (getArguments() != null && getArguments().getString("appointment_id") != null) {
            selectedAppointmentId = getArguments().getString("appointment_id");
            etAppointmentId.setText(selectedAppointmentId, false);
            fetchImages();
        }

        // Check permissions
        if (hasCameraPermission()) {
            startCamera();
        } else {
            requestPermissions();
        }

        // Capture button
        btnCapture.setOnClickListener(v -> capturePhoto());

        // Upload button
        btnUpload.setOnClickListener(v -> uploadImage());
        btnBookAppointment.setOnClickListener(v -> openBooking());

        View btnCloseCamera = view.findViewById(R.id.btn_close_camera);
        if (btnCloseCamera != null) {
            btnCloseCamera.setOnClickListener(v -> {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }

        if (getArguments() != null && getArguments().getBoolean("return_url_only", false)) {
            btnBookAppointment.setVisibility(View.GONE);
            view.findViewById(R.id.et_appointment_id).setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (apiService != null && tokenManager != null) {
            loadCustomerAppointments();
        }
    }

    private void loadCustomerAppointments() {
        String token = tokenManager.getToken();
        if (token == null || token.trim().isEmpty()) {
            Log.e(TAG, "❌ Cannot load appointments: authentication token is missing");
            return;
        }

        apiService.getAppointments("Bearer " + token).enqueue(
                new retrofit2.Callback<ApiResponse<List<Appointment>>>() {
                    @Override
                    public void onResponse(
                            @NonNull retrofit2.Call<ApiResponse<List<Appointment>>> call,
                            @NonNull retrofit2.Response<ApiResponse<List<Appointment>>> response
                    ) {
                        if (!isUiAvailable()) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null
                                || !response.body().isSuccess()
                                || response.body().getData() == null) {
                            Toast.makeText(getContext(), "Unable to load appointments", Toast.LENGTH_LONG).show();
                            return;
                        }

                        availableAppointments.clear();
                        availableAppointments.addAll(response.body().getData());
                        Log.d(TAG, "✅ Loaded " + availableAppointments.size() + " customer appointments");

                        List<String> labels = new ArrayList<>();
                        for (Appointment appointment : availableAppointments) {
                            String number = appointment.getAppointment_number();
                            String status = appointment.getStatus();
                            labels.add((number == null || number.trim().isEmpty()
                                    ? appointment.getId()
                                    : number) + " - " + (status == null ? "UNKNOWN" : status));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                labels
                        );
                        etAppointmentId.setAdapter(adapter);
                        if (availableAppointments.size() == 1) {
                            etAppointmentId.setText(labels.get(0), false);
                            selectedAppointmentId = availableAppointments.get(0).getId();
                            fetchImages();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull retrofit2.Call<ApiResponse<List<Appointment>>> call,
                            @NonNull Throwable throwable
                    ) {
                        if (isUiAvailable()) {
                            Toast.makeText(getContext(), "Unable to load appointments: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void openBooking() {
        if (!isUiAvailable()) {
            return;
        }

        getParentFragmentManager()
                .beginTransaction()
                .replace(getId(), new RepairBookingFragment())
                .addToBackStack(null)
                .commit();
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

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
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);

                isCameraReady = true;
                btnCapture.setEnabled(true);

            } catch (Exception e) {
                Log.e(TAG, "Camera initialization failed: " + e.getMessage(), e);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Camera error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void capturePhoto() {
        if (!isCameraReady || imageCapture == null) {
            Toast.makeText(getContext(), "⏳ Camera is initializing... Please wait.", Toast.LENGTH_SHORT).show();
            btnCapture.postDelayed(this::capturePhoto, 1000);
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File outputDirectory = requireContext().getCacheDir();
        capturedFile = new File(outputDirectory, "IMG_" + timestamp + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(capturedFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Toast.makeText(getContext(), "📸 Photo captured", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "📸 Photo saved to: " + capturedFile.getAbsolutePath());
                
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
                if (isUiAvailable()) {
                    requireActivity().runOnUiThread(() -> {
                        RepairImage dummy = new RepairImage();
                        dummy.setImage_url(capturedFile.toURI().toString());
                        List<RepairImage> current = new java.util.ArrayList<>(imageAdapter.getImageList());
                        current.add(0, dummy);
                        imageAdapter.updateList(current);
                    });
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "❌ Capture error: " + exception.getMessage(), exception);
                Toast.makeText(getContext(), "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // UPLOAD IMAGE VIA CLOUDINARY
    // ==========================================
    private void uploadImage() {
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

        if (getArguments() != null && getArguments().getBoolean("return_url_only", false)) {
            android.os.Bundle result = new android.os.Bundle();
            result.putString("local_file_path", capturedFile.getAbsolutePath());
            getParentFragmentManager().setFragmentResult("camera_request", result);
            getParentFragmentManager().popBackStack();
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

    private void fetchImages() {
        if (!isUiAvailable()) {
            return;
        }

        String appointmentId = selectedAppointmentId;
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            imageAdapter.updateList(null);
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getAppointmentImages("Bearer " + token, appointmentId).enqueue(new retrofit2.Callback<ApiResponse<List<RepairImage>>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ApiResponse<List<RepairImage>>> call, @NonNull retrofit2.Response<ApiResponse<List<RepairImage>>> response) {
                if (!isUiAvailable()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<RepairImage> images = response.body().getData();
                    if (images != null && !images.isEmpty()) {
                        imageAdapter.updateList(images);
                        Toast.makeText(getContext(), "✅ Loaded " + images.size() + " images", Toast.LENGTH_SHORT).show();
                    } else {
                        imageAdapter.updateList(null);
                        Toast.makeText(getContext(), "No images found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch images", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ApiResponse<List<RepairImage>>> call, @NonNull Throwable t) {
                if (!isUiAvailable()) {
                    return;
                }

                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // DELETE IMAGE
    // ==========================================
    private void deleteImage(RepairImage image) {
        if (!isUiAvailable()) {
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.deleteImage("Bearer " + token, image.getAppointment_id(), image.getId()).enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ApiResponse<Object>> call, @NonNull retrofit2.Response<ApiResponse<Object>> response) {
                if (!isUiAvailable()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Image deleted", Toast.LENGTH_SHORT).show();
                    fetchImages();
                } else {
                    Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                if (!isUiAvailable()) {
                    return;
                }

                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runOnUiThreadSafely(Runnable action) {
        if (!isUiAvailable()) {
            return;
        }

        requireView().post(() -> {
            if (isUiAvailable()) {
                action.run();
            }
        });
    }

    private boolean isUiAvailable() {
        return isAdded() && getView() != null && getActivity() != null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void resetUploadState() {
        runOnUiThreadSafely(() -> {
            progressBar.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
        });
    }

    private void showUploadError(String message) {
        runOnUiThreadSafely(() -> {
            progressBar.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        });
    }
}