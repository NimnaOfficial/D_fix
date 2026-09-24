const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

let deleteLogic = `await env.DB.prepare("DELETE FROM appointments WHERE id = ?").bind(aptId).run();`;
let batchDeleteLogic = `await env.DB.batch([
            env.DB.prepare("DELETE FROM messages WHERE appointment_id = ?").bind(aptId),
            env.DB.prepare("DELETE FROM repair_status_history WHERE appointment_id = ?").bind(aptId),
            env.DB.prepare("DELETE FROM payments WHERE appointment_id = ?").bind(aptId),
            env.DB.prepare("DELETE FROM repair_images WHERE appointment_id = ?").bind(aptId),
            env.DB.prepare("DELETE FROM notifications WHERE appointment_id = ?").bind(aptId),
            env.DB.prepare("DELETE FROM appointment_parts WHERE appointment_id = ?").bind(aptId),
            env.DB.prepare("DELETE FROM appointments WHERE id = ?").bind(aptId)
        ]);`;

if (code.includes(deleteLogic)) {
    code = code.replace(deleteLogic, batchDeleteLogic);
    fs.writeFileSync("cloudflare-backend/worker.js", code);
    console.log("Replaced DELETE logic with batch cascade delete!");
} else {
    console.log("Could not find the DELETE logic in worker.js!");
}

