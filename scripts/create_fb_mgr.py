import os

code = """package com.mad.techfix.utils;

import android.net.Uri;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirebaseChatManager {

    private static final String TAG = "FirebaseChatManager";
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;

    public FirebaseChatManager() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void ensureAuthenticated(OnAuthListener listener) {
        if (auth.getCurrentUser() != null) {
            listener.onSuccess();
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener(authResult -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError("Firebase Auth Failed: " + e.getMessage()));
        }
    }

    public void sendImageMessage(Uri imageUri, String optionalText, String appointmentId, String senderId, OnMessageSentListener listener) {
        if (auth.getCurrentUser() == null) {
            listener.onError("User not authenticated.");
            return;
        }

        long timestamp = System.currentTimeMillis();
        
        // Path formatted as: chats/{userId}/{timestamp}.jpg
        String storagePath = "chats/" + senderId + "/" + timestamp + ".jpg";
        StorageReference imageRef = storage.getReference().child(storagePath);

        // 1. Upload Image (Cloud Storage)
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 2. Retrieve Download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        saveMessageToFirestore(senderId, appointmentId, downloadUri.toString(), optionalText, timestamp, listener);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed", e);
                    listener.onError("Image upload failed: " + e.getMessage());
                });
    }

    public void sendTextMessage(String text, String appointmentId, String senderId, OnMessageSentListener listener) {
        saveMessageToFirestore(senderId, appointmentId, "", text, System.currentTimeMillis(), listener);
    }

    private void saveMessageToFirestore(String senderId, String appointmentId, String imageUrl, String text, long timestamp, OnMessageSentListener listener) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender_id", senderId);
        messageData.put("appointment_id", appointmentId);
        messageData.put("timestamp", timestamp);
        messageData.put("image_url", imageUrl);
        messageData.put("message", text != null ? text : "");

        firestore.collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError("Failed to save message: " + e.getMessage()));
    }

    public void listenForMessages(String appointmentId, OnMessagesUpdatedListener listener) {
        if (auth.getCurrentUser() == null) return;

        firestore.collection("messages")
                .whereEqualTo("appointment_id", appointmentId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshots != null) {
                        listener.onMessagesUpdated(snapshots.getDocuments());
                    }
                });
    }

    public interface OnAuthListener {
        void onSuccess();
        void onError(String error);
    }

    public interface OnMessageSentListener {
        void onSuccess();
        void onError(String error);
    }

    public interface OnMessagesUpdatedListener {
        void onMessagesUpdated(List<DocumentSnapshot> documents);
    }
}
"""

with open('app/src/main/java/com/mad/techfix/utils/FirebaseChatManager.java', 'w', encoding='utf-8') as f:
    f.write(code)
print("Created FirebaseChatManager.java")
