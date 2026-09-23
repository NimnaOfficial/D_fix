import re

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = r'''        btnSendImage\.setOnClickListener\(v -> \{
            Intent intent = new Intent\(Intent\.ACTION_PICK, android\.provider\.MediaStore\.Images\.Media\.EXTERNAL_CONTENT_URI\);
            imagePickerLauncher\.launch\(intent\);
        \}\);'''

replacement = r'''        btnSendImage.setOnClickListener(v -> {
            CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
            new android.app.AlertDialog.Builder(this)
                .setTitle("Send Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Take Photo
                        com.mad.techfix.ui.camera.CameraFragment cameraFragment = new com.mad.techfix.ui.camera.CameraFragment();
                        android.os.Bundle args = new android.os.Bundle();
                        args.putString("appointment_id", appointmentId);
                        args.putBoolean("return_url_only", true);
                        cameraFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, cameraFragment)
                                .addToBackStack(null)
                                .commit();
                    } else if (which == 1) {
                        // Choose from Gallery
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imagePickerLauncher.launch(intent);
                    }
                })
                .show();
        });'''

if re.search(target, content):
    content = re.sub(target, replacement, content)
    with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success adding AlertDialog to MessagesActivity")
else:
    print("Failed to find btnSendImage listener")
