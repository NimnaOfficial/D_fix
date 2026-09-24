
const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

const oldCancel = `if (apt.status === "CANCELLED") return json({ success: false, message: "Cannot send messages for cancelled appointments" }, 403);`;
const oldCancel2 = `if (apt.status === \x27CANCELLED\x27) return json({ success: false, message: "Cannot send messages for cancelled appointments" }, 403);`;
const newCancel = `if (apt.status === \x27CANCELLED\x27) return json({ success: false, message: "Cannot send messages for cancelled appointments" }, 403);
          if (apt.status === \x27SUSPENDED\x27) return json({ success: false, message: "Connection is on hold. Appointment is suspended." }, 403);`;

if (code.includes(oldCancel)) {
    code = code.replace(oldCancel, newCancel);
} else if (code.includes(oldCancel2)) {
    code = code.replace(oldCancel2, newCancel);
}

const oldCust = `if (user.role === "CUSTOMER" && appointment.customer_id !== user.id) {`;
const newCust = `if (appointment.status === \x27SUSPENDED\x27) return json({ success: false, message: "Cannot update status of a suspended appointment" }, 403);
          if (user.role === "CUSTOMER" && appointment.customer_id !== user.id) {`;

if (code.includes(oldCust)) {
    code = code.replace(oldCust, newCust);
}

fs.writeFileSync("cloudflare-backend/worker.js", code);
console.log("Done");

