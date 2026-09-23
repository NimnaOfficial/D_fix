import re

path1 = 'app/src/main/res/layout/fragment_technician_repair_detail.xml'
with open(path1, 'r', encoding='utf-8') as f:
    content1 = f.read()

btn1 = """        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_messages"
            style="@style/Widget.MaterialComponents.Button.OutlinedButton"
            android:layout_width="match_parent"
            android:layout_height="54dp"
            android:layout_marginTop="10dp"
            android:text="Messages"
            app:cornerRadius="14dp" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btn_view_repair_history_technician" """
content1 = content1.replace('<com.google.android.material.button.MaterialButton\n            android:id="@+id/btn_view_repair_history_technician"', btn1)
with open(path1, 'w', encoding='utf-8') as f:
    f.write(content1)

path2 = 'app/src/main/res/layout/fragment_customer_appointment_detail.xml'
# Let's check if fragment_customer_appointment_detail.xml exists, wait, earlier we found CustomerAppointmentDetailBottomSheet
