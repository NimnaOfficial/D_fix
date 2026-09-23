package com.mad.techfix.ui.messages;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Message;
import com.mad.techfix.network.ApiService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerMessages;
    private EditText etMessage;
    private ImageButton btnSendMessage;
    private ImageButton btnSendImage;
    private MessageAdapter adapter;
    private String appointmentId;
    private ApiService apiService;
    private SessionManager sessionManager;
    private Handler handler;
    private Runnable fetchRunnable;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
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

    private void uploadImage(File file) {
        String token = sessionManager.getBearerToken();
        if (token == null) return;
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();
        
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        apiService.uploadFile(token, filePart).enqueue(new Callback<Map<String, Object>>() {
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

        // Get current user id from session if stored, else we can pass it via intent
        // For now, let's assume sessionManager doesn't expose it directly, but wait!
        String userId = sessionManager.getUserId();

        
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

        adapter = new MessageAdapter(userId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(adapter);

        btnSendMessage.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text, "");
            }
        });
        
        btnSendImage.setOnClickListener(v -> {
            com.mad.techfix.ui.camera.CameraFragment cameraFragment = new com.mad.techfix.ui.camera.CameraFragment();
            android.os.Bundle args = new android.os.Bundle();
            args.putString("appointment_id", appointmentId);
            args.putBoolean("return_url_only", true);
            cameraFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, cameraFragment)
                    .addToBackStack(null)
                    .commit();
        });

        getSupportFragmentManager().setFragmentResultListener("camera_request", this, (requestKey, bundle) -> {
            String imageUrl = bundle.getString("image_url");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                sendMessage("Sent an image", imageUrl);
            }
        });

        handler = new Handler(Looper.getMainLooper());
        fetchRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMessages();
                handler.postDelayed(this, 5000);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(fetchRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(fetchRunnable);
    }

    private void fetchMessages() {
        String token = sessionManager.getBearerToken();
        if (token == null) return;

        apiService.getMessages(token, appointmentId).enqueue(new Callback<ApiResponse<List<Message>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Message>>> call, @NonNull Response<ApiResponse<List<Message>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Message> msgs = response.body().getData();
                    if (msgs != null) {
                        adapter.setMessages(msgs);
                        recyclerMessages.scrollToPosition(msgs.size() - 1);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Message>>> call, @NonNull Throwable t) {
            }
        });
    }

    private void sendMessage(String text, String imageUrl) {
        String token = sessionManager.getBearerToken();
        if (token == null) return;
        
        btnSendMessage.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("message", text != null ? text : "");
        body.put("image_url", (imageUrl != null) ? imageUrl : "");

        apiService.sendMessage(token, appointmentId, body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                btnSendMessage.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    etMessage.setText("");
                    fetchMessages();
                } else {
                    String err = "Failed to send";
                    try {
                        if (response.errorBody() != null) {
                            err = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    Toast.makeText(MessagesActivity.this, err, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                btnSendMessage.setEnabled(true);
                Toast.makeText(MessagesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
