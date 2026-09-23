import re

path = 'app/src/main/res/layout/fragment_booking_confirmation.xml'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

new_btn = """        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_upload_photo"
            style="@style/Widget.MaterialComponents.Button"
            android:layout_width="match_parent"
            android:layout_height="54dp"
            android:layout_marginTop="10dp"
            android:text="Upload Photo (Optional)"
            android:textAllCaps="false"
            android:textSize="15sp"
            app:cornerRadius="16dp" />

        <!-- Back Home -->"""

content = content.replace('<!-- Back Home -->', new_btn)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("XML updated")
