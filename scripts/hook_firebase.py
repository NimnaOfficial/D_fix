import re

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace variables
target_vars = r'''    private ActivityResultLauncher<Intent> imagePickerLauncher;'''
replacement_vars = r'''    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private com.mad.techfix.utils.FirebaseChatManager chatManager;'''
content = content.replace(target_vars, replacement_vars)

# Replace onCreate logic
target_oncreate = r'''        adapter = new MessageAdapter\(userId\);'''
replacement_oncreate = r'''        adapter = new MessageAdapter(userId);
        chatManager = new com.mad.techfix.utils.FirebaseChatManager(this);
        chatManager.ensureAuthenticated(new com.mad.techfix.utils.FirebaseChatManager.OnAuthListener() {
            @Override
            public void onSuccess() {
                chatManager.listenForMessages(appointmentId, new com.mad.techfix.utils.FirebaseChatManager.OnMessagesUpdatedListener() {
                    @Override
                    public void onMessagesUpdated(java.util.List<com.google.firebase.firestore.DocumentSnapshot> documents) {
                        java.util.List<com.mad.techfix.models.Message> msgs = new java.util.ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : documents) {
                            com.mad.techfix.models.Message m = new com.mad.techfix.models.Message();
                            m.setId(doc.getId());
                            m.setAppointmentId(doc.getString("appointment_id"));
                            m.setSenderId(doc.getString("sender_id"));
                            m.setMessage(doc.getString("message"));
                            m.setImageUrl(doc.getString("image_url"));
                            m.setCreatedAt(doc.getLong("timestamp") + "");
                            msgs.add(m);
                        }
                        adapter.setMessages(msgs);
                        if (!msgs.isEmpty()) {
                            recyclerMessages.smoothScrollToPosition(msgs.size() - 1);
                        }
                    }
                });
            }
            @Override
            public void onError(String error) {
                Toast.makeText(MessagesActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });'''
content = re.sub(target_oncreate, replacement_oncreate, content)

# Remove the handler polling
target_handler = r'''        handler = new Handler\(Looper\.getMainLooper\(\)\);[\s\S]*?handler\.postDelayed\(this, 5000\);[\s\S]*?\}\);'''
content = re.sub(target_handler, r'''// Removed HTTP polling in favor of Firebase realtime sync''', content)

# Remove fetchMessages
target_fetch = r'''    private void fetchMessages\(\) \{[\s\S]*?\}\);[\s\n]*\}'''
content = re.sub(target_fetch, r'''// fetchMessages removed''', content)

# Replace sendMessage
target_send = r'''    private void sendMessage\(String text, String imageUrl\) \{[\s\S]*?\}\);[\s\n]*\}'''
replacement_send = r'''    private void sendMessage(String text, String imageUrl) {
        // Not used directly anymore, replaced by chatManager methods
    }'''
content = re.sub(target_send, replacement_send, content)

# Update btnSendMessage click listener
target_btn = r'''        btnSendMessage\.setOnClickListener\(v -> \{
            String text = etMessage\.getText\(\)\.toString\(\)\.trim\(\);
            if \(\!text\.isEmpty\(\)\) \{
                sendMessage\(text, null\);
            \}
        \}\);'''
replacement_btn = r'''        btnSendMessage.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                btnSendMessage.setEnabled(false);
                chatManager.sendTextMessage(text, appointmentId, sessionManager.getUserId(), new com.mad.techfix.utils.FirebaseChatManager.OnMessageSentListener() {
                    @Override
                    public void onSuccess() {
                        btnSendMessage.setEnabled(true);
                        etMessage.setText("");
                    }
                    @Override
                    public void onError(String error) {
                        btnSendMessage.setEnabled(true);
                        Toast.makeText(MessagesActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });'''
content = re.sub(target_btn, replacement_btn, content)

# Update uploadImage for picker
target_upload = r'''    private void uploadImage\(File file\) \{[\s\S]*?\}\);[\s\n]*\}'''
replacement_upload = r'''    private void uploadImage(File file) {
        Toast.makeText(this, "Uploading image to Firebase...", Toast.LENGTH_SHORT).show();
        chatManager.sendImageMessage(Uri.fromFile(file), etMessage.getText().toString().trim(), appointmentId, sessionManager.getUserId(), new com.mad.techfix.utils.FirebaseChatManager.OnMessageSentListener() {
            @Override
            public void onSuccess() {
                etMessage.setText("");
            }
            @Override
            public void onError(String error) {
                Toast.makeText(MessagesActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }'''
content = re.sub(target_upload, replacement_upload, content)

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("Success updating MessagesActivity")
