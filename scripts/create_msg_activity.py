import os

os.makedirs('app/src/main/java/com/mad/techfix/ui/messages', exist_ok=True)

msg_adapter = """package com.mad.techfix.ui.messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mad.techfix.R;
import com.mad.techfix.models.Message;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private List<Message> messages = new ArrayList<>();
    private String currentUserId;

    public MessageAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        if (msg.getSender_id() != null && msg.getSender_id().equals(currentUserId)) {
            return TYPE_SENT;
        }
        return TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        
        TextView tvText;
        ImageView ivImage;
        
        if (holder.getItemViewType() == TYPE_SENT) {
            tvText = ((SentMessageHolder) holder).tvText;
            ivImage = ((SentMessageHolder) holder).ivImage;
        } else {
            tvText = ((ReceivedMessageHolder) holder).tvText;
            ivImage = ((ReceivedMessageHolder) holder).ivImage;
        }

        if (msg.getMessage() != null && !msg.getMessage().trim().isEmpty()) {
            tvText.setText(msg.getMessage());
            tvText.setVisibility(View.VISIBLE);
        } else {
            tvText.setVisibility(View.GONE);
        }

        if (msg.getImage_url() != null && !msg.getImage_url().isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            Glide.with(ivImage.getContext()).load(msg.getImage_url()).into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageView ivImage;
        SentMessageHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_msg_text);
            ivImage = itemView.findViewById(R.id.iv_msg_image);
        }
    }

    static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageView ivImage;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_msg_text);
            ivImage = itemView.findViewById(R.id.iv_msg_image);
        }
    }
}
"""
with open('app/src/main/java/com/mad/techfix/ui/messages/MessageAdapter.java', 'w') as f:
    f.write(msg_adapter)


msg_activity = """package com.mad.techfix.ui.messages;

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
import com.mad.techfix.api.RetrofitClient;
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
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        recyclerMessages = findViewById(R.id.recycler_messages);
        etMessage = findViewById(R.id.et_message);
        btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendImage = findViewById(R.id.btn_send_image);

        // Get current user id from session if stored, else we can pass it via intent
        // For now, let's assume sessionManager doesn't expose it directly, but wait!
        String userId = sessionManager.getUser() != null ? sessionManager.getUser().getId() : "";

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
            // Open camera fragment or similar
            Toast.makeText(this, "Camera integration needed here", Toast.LENGTH_SHORT).show();
            // Optional: Start CameraFragment in a dialog or activity
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
        body.put("message", text);
        if (!imageUrl.isEmpty()) body.put("image_url", imageUrl);

        apiService.sendMessage(token, appointmentId, body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                btnSendMessage.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    etMessage.setText("");
                    fetchMessages();
                } else {
                    Toast.makeText(MessagesActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
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
"""
with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'w') as f:
    f.write(msg_activity)

print("MessagesActivity and adapter created")
