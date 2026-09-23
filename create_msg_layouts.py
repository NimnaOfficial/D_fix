import os

layout_content = """<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/app_background">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar_messages"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="@color/white"
        app:layout_constraintTop_toTopOf="parent"
        app:title="Messages"
        app:titleTextColor="@color/black" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_messages"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:clipToPadding="false"
        android:padding="8dp"
        app:layout_constraintTop_toBottomOf="@id/toolbar_messages"
        app:layout_constraintBottom_toTopOf="@id/layout_input"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <LinearLayout
        android:id="@+id/layout_input"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="8dp"
        android:background="@color/white"
        android:gravity="center_vertical"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent">

        <EditText
            android:id="@+id/et_message"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="Type a message..."
            android:background="@drawable/bg_input"
            android:padding="12dp"
            android:maxLines="4" />

        <ImageButton
            android:id="@+id/btn_send_image"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@android:drawable/ic_menu_camera"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:layout_marginStart="8dp" />

        <ImageButton
            android:id="@+id/btn_send_message"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@android:drawable/ic_menu_send"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:layout_marginStart="8dp" />

    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
"""
with open('app/src/main/res/layout/activity_messages.xml', 'w') as f:
    f.write(layout_content)

item_msg_left = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="8dp">

    <TextView
        android:id="@+id/tv_msg_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_msg_received"
        android:padding="12dp"
        android:textColor="@color/black"
        android:textSize="16sp"
        android:maxWidth="280dp" />

    <ImageView
        android:id="@+id/iv_msg_image"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:layout_marginTop="4dp"
        android:visibility="gone" />

</LinearLayout>"""
with open('app/src/main/res/layout/item_message_received.xml', 'w') as f:
    f.write(item_msg_left)

item_msg_right = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="end"
    android:padding="8dp">

    <TextView
        android:id="@+id/tv_msg_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_msg_sent"
        android:padding="12dp"
        android:textColor="@color/white"
        android:textSize="16sp"
        android:maxWidth="280dp" />

    <ImageView
        android:id="@+id/iv_msg_image"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:layout_marginTop="4dp"
        android:visibility="gone" />

</LinearLayout>"""
with open('app/src/main/res/layout/item_message_sent.xml', 'w') as f:
    f.write(item_msg_right)

# Background drawables
with open('app/src/main/res/drawable/bg_input.xml', 'w') as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#F0F0F0"/>
    <corners android:radius="24dp"/>
</shape>""")

with open('app/src/main/res/drawable/bg_msg_sent.xml', 'w') as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/app_primary"/>
    <corners android:topLeftRadius="16dp" android:topRightRadius="16dp" android:bottomLeftRadius="16dp" android:bottomRightRadius="4dp"/>
</shape>""")

with open('app/src/main/res/drawable/bg_msg_received.xml', 'w') as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#E0E0E0"/>
    <corners android:topLeftRadius="16dp" android:topRightRadius="16dp" android:bottomLeftRadius="4dp" android:bottomRightRadius="16dp"/>
</shape>""")

print("Layouts and drawables created")
