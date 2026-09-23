import re

path = 'app/src/main/AndroidManifest.xml'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(' android:theme="@style/Theme.TechFix"', '')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Manifest theme fixed")
