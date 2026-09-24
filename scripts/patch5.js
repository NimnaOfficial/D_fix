const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

let msgRegex = /if \(\!appt\)\s*\{\s*return json\(\{ success: false, message: "Appointment not found" \}, 404\);\s*\}/g;
if (msgRegex.test(code)) {
    code = code.replace(msgRegex, `if (!appt) {
                return json({ success: false, message: "Appointment not found" }, 404);
              }
              if (appt.status === "SUSPENDED") {
                return json({ success: false, message: "Connection is on hold. Appointment is suspended." }, 403);
              }`);
    fs.writeFileSync("cloudflare-backend/worker.js", code);
    console.log("Patched via regex!");
} else {
    console.log("Regex not found");
}

