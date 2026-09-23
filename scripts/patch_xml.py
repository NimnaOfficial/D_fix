import re

with open('app/src/main/res/layout/fragment_camera.xml', 'r', encoding='utf-8') as f:
    content = f.read()

new_content = '''    <!-- Header -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:layout_marginBottom="4dp">
        
        <ImageButton
            android:id="@+id/btn_close_camera"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@android:drawable/ic_menu_close_clear_cancel"
            android:background="?attr/selectableItemBackgroundBorderless"
            app:tint="@color/white"
            android:layout_marginEnd="8dp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="&#128248; Repair Camera"
            android:textColor="@color/white"
            android:textSize="28sp"
            android:textStyle="bold" />
    </LinearLayout>'''

content = re.sub(r'    <!-- Header -->\s*<TextView.*?layout_marginBottom="4dp" />', new_content, content, flags=re.DOTALL)

with open('app/src/main/res/layout/fragment_camera.xml', 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched!")
