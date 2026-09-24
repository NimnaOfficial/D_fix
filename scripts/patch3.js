const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

const oldMsg = `              if (!appt) {
                return json({ success: false, message: "Appointment not found" }, 404);
              }`;

const newMsg = `              if (!appt) {
                return json({ success: false, message: "Appointment not found" }, 404);
              }
              if (appt.status === "SUSPENDED") {
                return json({ success: false, message: "Connection is on hold. Appointment is suspended." }, 403);
              }`;

if (code.includes(oldMsg)) {
    code = code.replace(oldMsg, newMsg);
    fs.writeFileSync("cloudflare-backend/worker.js", code);
    console.log("Messages API patched!");
} else {
    console.log("oldMsg not found!");
}

