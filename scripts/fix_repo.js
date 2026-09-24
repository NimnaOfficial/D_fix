const fs = require("fs");
let code = fs.readFileSync("app/src/main/java/com/mad/techfix/repository/AdminRepository.java", "utf8");

code = code.replace(
    /apiService\.suspendAppointment\("Bearer " \+ token, aptId\)/,
    "apiService.suspendAppointment(token, aptId)"
);

code = code.replace(
    /apiService\.resumeAppointment\("Bearer " \+ token, aptId\)/,
    "apiService.resumeAppointment(token, aptId)"
);

code = code.replace(
    /apiService\.removeAppointment\("Bearer " \+ token, aptId\)/,
    "apiService.removeAppointment(token, aptId)"
);

fs.writeFileSync("app/src/main/java/com/mad/techfix/repository/AdminRepository.java", code);
console.log("Fixed AdminRepository token formatting!");

