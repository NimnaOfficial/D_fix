with open('build.gradle.kts', 'r', encoding='utf-8') as f:
    content = f.read()
content = content.replace('id("com.google.gms.google-services") version "4.5.0" apply false', '')
with open('build.gradle.kts', 'w', encoding='utf-8') as f:
    f.write(content)

with open('app/build.gradle.kts', 'r', encoding='utf-8') as f:
    content2 = f.read()
content2 = content2.replace('id("com.google.gms.google-services")', '')
with open('app/build.gradle.kts', 'w', encoding='utf-8') as f:
    f.write(content2)

print("Reverted Google Services plugin, kept dependencies")
