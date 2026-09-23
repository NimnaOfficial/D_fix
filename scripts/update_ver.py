import re
with open('build.gradle.kts', 'r', encoding='utf-8') as f:
    content = f.read()
content = content.replace('version "4.4.1"', 'version "4.4.2"')
with open('build.gradle.kts', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated to 4.4.2")
