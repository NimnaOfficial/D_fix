content = """// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.gms.google-services") version "4.4.1" apply false
}"""
with open('build.gradle.kts', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated build.gradle.kts")
