package com.mad.techfix.ui.messages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.Message;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerMessages;
    private EditText etMessage;
    private ImageButton btnSendMessage;
    private ImageButton btnSendImage;
    private MessageAdapter adapter;
    private String appointmentId;
    private SessionManager sessionManager;
    private ApiService apiService;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private static final int POLL_INTERVAL = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        appointmentId = getIntent().getStringExtra("appointment_id");
        if (appointmentId == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar_messages);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Messages");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService();

        recyclerMessages = findViewById(R.id.recycler_messages);
        etMessage = findViewById(R.id.et_message);
        btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendImage = findViewById(R.id.btn_send_image);

        String userId = sessionManager.getUserId();

        adapter = new MessageAdapter(userId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(adapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            File file = getFileFromUri(imageUri);
                            if (file != null && file.exists()) {
                                uploadImageToCloudinary(file);
                            } else {
                                Toast.makeText(this, "Could not read image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        btnSendMessage.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text, "");
                etMessage.setText("");
            }
        });

        btnSendImage.setOnClickListener(v -> {
            CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
            new android.app.AlertDialog.Builder(this)
                .setTitle("Send Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        com.mad.techfix.ui.camera.CameraFragment cameraFragment = new com.mad.techfix.ui.camera.CameraFragment();
                        Bundle args = new Bundle();
                        args.putString("appointment_id", appointmentId);
                        args.putBoolean("return_url_only", true);
                        cameraFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, cameraFragment)
                                .addToBackStack(null)
                                .commit();
                    } else if (which == 1) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imagePickerLauncher.launch(intent);
                    }
                })
                .show();
        });

        getSupportFragmentManager().setFragmentResultListener("camera_request", this, (requestKey, bundle) -> {
            String localPath = bundle.getString("local_file_path");
            if (localPath != null && !localPath.isEmpty()) {
                File file = new File(localPath);
                if (file.exists()) {
                    uploadImageToCloudinary(file);
                } else {
                    Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                String imageUrl = bundle.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    sendMessage("Sent an image", imageUrl);
                }
            }
        });

        pollingHandler = new Handler(Looper.getMainLooper());
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                pollingHandler.postDelayed(this, POLL_INTERVAL);
            }
        };
        pollingHandler.post(pollingRunnable);
    }

    private void loadMessages() {
        apiService.getMessages(sessionManager.getBearerToken(), appointmentId).enqueue(new Callback<ApiResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Message>>> call, Response<ApiResponse<List<Message>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Message> msgs = response.body().getData();
                    int previousSize = adapter.getItemCount();
                    adapter.setMessages(msgs);
                    if (msgs.size() > previousSize && msgs.size() > 0) {
                        recyclerMessages.smoothScrollToPosition(msgs.size() - 1);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Message>>> call, Throwable t) {
                Log.e("MessagesActivity", "Failed to load messages", t);
            }
        });
    }

    private void sendMessage(String text, String imageUrl) {
        Map<String, String> body = new HashMap<>();
        body.put("message", text);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            body.put("image_url", imageUrl);
        }

        apiService.sendMessage(sessionManager.getBearerToken(), appointmentId, body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    loadMessages();
                } else {
                    Toast.makeText(MessagesActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(MessagesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToCloudinary(File file) {
        Toast.makeText(this, "Uploading image to Cloudinary...", Toast.LENGTH_SHORT).show();
        
        MediaManager.get().upload(file.getAbsolutePath())
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload started: " + requestId);
                    }
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = (String) resultData.get("secure_url");
                        sendMessage("📸 Image", url);
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary", "Upload failed: " + error.getDescription());
                        Toast.makeText(MessagesActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private File getFileFromUri(Uri uri) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }
}
