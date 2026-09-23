package com.mad.techfix.ui.messages;

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
