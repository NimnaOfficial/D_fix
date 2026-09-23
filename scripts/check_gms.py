import urllib.request
try:
    url = "https://maven.google.com/com/google/gms/google-services/maven-metadata.xml"
    with urllib.request.urlopen(url) as response:
        print(response.read().decode())
except Exception as e:
    print(e)
