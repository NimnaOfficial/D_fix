import json
import urllib.request
import urllib.parse
import os

url = "https://techfix-backend.codse251f-003.workers.dev/api/appointments/TEST-APT/messages"
req = urllib.request.Request(url, method="GET")

try:
    with urllib.request.urlopen(req) as response:
        print(response.read().decode())
except Exception as e:
    print(e)
