import re
with open('app/src/main/java/com/mad/techfix/utils/FirebaseChatManager.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = r'''    public FirebaseChatManager\(\) \{'''
replacement = r'''    public FirebaseChatManager(android.content.Context context) {
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseOptions options = new com.google.firebase.FirebaseOptions.Builder()
                .setProjectId("madproj-f84e3")
                .setApplicationId("1:157547296829:android:e542fca2e790bb81dd5254")
                .setApiKey("AIzaSyB_Fy4WJZWVu2WEahxXE-86Vj3gspf2g6o")
                .setStorageBucket("madproj-f84e3.firebasestorage.app")
                .build();
            com.google.firebase.FirebaseApp.initializeApp(context, options);
        }'''

if re.search(target, content):
    content = re.sub(target, replacement, content)
    with open('app/src/main/java/com/mad/techfix/utils/FirebaseChatManager.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success updating FirebaseChatManager with manual init")
else:
    print("Failed to find constructor")
