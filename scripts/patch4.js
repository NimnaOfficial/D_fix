const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");
let msgBlock = `return json({ success: false, message: "Appointment not found" }, 404);
              }`;

let idx = code.indexOf(msgBlock);
if (idx > -1) {
    code = code.replace(msgBlock, msgBlock + `\n              if (appt.status === "SUSPENDED") {
                return json({ success: false, message: "Connection is on hold. Appointment is suspended." }, 403);
              }`);
    fs.writeFileSync("cloudflare-backend/worker.js", code);
    console.log("Replaced using block");
} else {
    console.log("msgBlock not found");
}

