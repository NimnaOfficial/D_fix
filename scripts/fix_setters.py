import re

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('m.setAppointmentId(', 'm.setAppointment_id(')
content = content.replace('m.setSenderId(', 'm.setSender_id(')
content = content.replace('m.setImageUrl(', 'm.setImage_url(')
content = content.replace('m.setCreatedAt(', 'm.setCreated_at(')

with open('app/src/main/java/com/mad/techfix/ui/messages/MessagesActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Fixed setter names in MessagesActivity")
