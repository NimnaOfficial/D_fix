import re

path2 = 'app/src/main/res/layout/bottom_sheet_customer_appointment_detail.xml'
with open(path2, 'r', encoding='utf-8') as f:
    content2 = f.read()

btn2 = """        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_messages"
            style="@style/Widget.MaterialComponents.Button"
            android:layout_width="match_parent"
            android:layout_height="54dp"
            android:layout_marginTop="22dp"
            android:text="Messages"
            android:textAllCaps="false"
            app:cornerRadius="12dp" />
            
        <!-- Repair History -->"""

content2 = content2.replace('<!-- Repair History -->', btn2)
with open(path2, 'w', encoding='utf-8') as f:
    f.write(content2)

print("Updated bottom_sheet_customer_appointment_detail.xml")
