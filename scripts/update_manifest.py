import re

path = 'app/src/main/AndroidManifest.xml'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

activity = '        <activity android:name=".ui.messages.MessagesActivity" android:exported="false" android:theme="@style/Theme.TechFix" />\n    </application>'

content = content.replace('</application>', activity)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Manifest updated")
