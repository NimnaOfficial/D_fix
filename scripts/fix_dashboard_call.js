const fs = require("fs");

let sheet = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AppointmentDetailBottomSheet.java", "utf8");
sheet = sheet.replace(
    /return newInstance\(id, number, status, date, time, customer, branch, null, null, null\);/,
    "return newInstance(id, number, status, date, time, customer, branch, null, null, null, null, null);"
);
fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AppointmentDetailBottomSheet.java", sheet);

let dash = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AdminDashboardFragment.java", "utf8");
dash = dash.replace(
    /appointment\.getService_name\(\),\s*appointment\.getBranch_name\(\)\s*\);/g,
    `appointment.getService_name(),
                      appointment.getBranch_name(),
                      appointment.getTechnician_name(),
                      appointment.getTechnician_status()
              );`
);
fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/dashboard/AdminDashboardFragment.java", dash);

console.log("Fixed dashboard call and bottom sheet overloaded method");

