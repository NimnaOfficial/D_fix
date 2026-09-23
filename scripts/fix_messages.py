import re

path = 'app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('''    }
        return null;
    }''', '    }')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Syntax error fixed")
