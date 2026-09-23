import re

with open('app/build.gradle.kts', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('alias(libs.plugins.android.application)', 'alias(libs.plugins.android.application)\n    id("com.google.gms.google-services")')

deps = """
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
"""

content = content.replace('dependencies {', 'dependencies {' + deps)

with open('app/build.gradle.kts', 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated app/build.gradle.kts")
