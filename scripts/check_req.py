with open('app/src/main/java/com/mad/techfix/ui/customer/booking/BookingReviewFragment.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'apiService.' in line or 'new CreateAppointmentRequest' in line:
        for j in range(-5, 15):
            if i+j >= 0 and i+j < len(lines):
                print(lines[i+j].strip('\n'))
        break
