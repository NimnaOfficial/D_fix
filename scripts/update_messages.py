import re

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = r'''        btnSendImage\.setOnClickListener\(v -> \{
            com\.mad\.techfix\.ui\.camera\.CameraFragment cameraFragment = new com\.mad\.techfix\.ui\.camera\.CameraFragment\(\);
            android\.os\.Bundle args = new android\.os\.Bundle\(\);
            args\.putString\("appointment_id", appointmentId\);
            args\.putBoolean\("return_url_only", true\);
            cameraFragment\.setArguments\(args\);
            getSupportFragmentManager\(\)\.beginTransaction\(\)
                    \.replace\(R\.id\.fragment_container, cameraFragment\)
                    \.addToBackStack\(null\)
                    \.commit\(\);
        \}\);'''

replacement = r'''        btnSendImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });'''

if re.search(target, content):
    content = re.sub(target, replacement, content)
    with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success updating MessagesActivity to use imagePickerLauncher")
else:
    print("Failed to find target in MessagesActivity")
