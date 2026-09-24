const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

const newEndpoints = `      // ==========================================
      // MANAGER APPOINTMENT CONTROLS
      // ==========================================
      if (path.startsWith("/api/appointments/") && path.endsWith("/suspend") && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user || user.role !== "MANAGER") return json({ success: false, message: "Access denied. Managers only." }, 403);
        const aptId = path.split("/")[3];
        const apt = await env.DB.prepare("SELECT branch_id FROM appointments WHERE id = ?").bind(aptId).first();
        if (!apt) return json({ success: false, message: "Not found" }, 404);
        if (apt.branch_id !== user.managerBranchId) return json({ success: false, message: "Access denied: Branch mismatch" }, 403);
        await env.DB.prepare("UPDATE appointments SET status = \x27SUSPENDED\x27, updated_at = CURRENT_TIMESTAMP WHERE id = ?").bind(aptId).run();
        await env.DB.prepare("INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, \x27SUSPENDED\x27, \x27Manager suspended appointment\x27, ?)").bind(crypto.randomUUID(), aptId, user.id).run();
        return json({ success: true, message: "Appointment suspended" });
      }

      if (path.startsWith("/api/appointments/") && path.endsWith("/resume") && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user || user.role !== "MANAGER") return json({ success: false, message: "Access denied. Managers only." }, 403);
        const aptId = path.split("/")[3];
        const apt = await env.DB.prepare("SELECT branch_id FROM appointments WHERE id = ?").bind(aptId).first();
        if (!apt) return json({ success: false, message: "Not found" }, 404);
        if (apt.branch_id !== user.managerBranchId) return json({ success: false, message: "Access denied: Branch mismatch" }, 403);
        const lastStatus = await env.DB.prepare("SELECT status FROM repair_status_history WHERE appointment_id = ? AND status != \x27SUSPENDED\x27 ORDER BY created_at DESC LIMIT 1").bind(aptId).first();
        const revertStatus = lastStatus ? lastStatus.status : "ASSIGNED";
        await env.DB.prepare("UPDATE appointments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?").bind(revertStatus, aptId).run();
        await env.DB.prepare("INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, ?, \x27Manager resumed appointment\x27, ?)").bind(crypto.randomUUID(), aptId, revertStatus, user.id).run();
        return json({ success: true, message: "Appointment resumed" });
      }

      if (path.startsWith("/api/appointments/") && path.split("/").length === 4 && request.method === "DELETE") {
        const user = await authenticate(request, env);
        if (!user || user.role !== "MANAGER") return json({ success: false, message: "Access denied. Managers only." }, 403);
        const aptId = path.split("/")[3];
        const apt = await env.DB.prepare("SELECT branch_id, technician_id FROM appointments WHERE id = ?").bind(aptId).first();
        if (!apt) return json({ success: false, message: "Not found" }, 404);
        if (apt.branch_id !== user.managerBranchId) return json({ success: false, message: "Access denied: Branch mismatch" }, 403);
        if (apt.technician_id) {
             await env.DB.prepare("UPDATE technicians SET availability_status = \x27AVAILABLE\x27 WHERE id = ?").bind(apt.technician_id).run();
        }
        await env.DB.prepare("DELETE FROM appointments WHERE id = ?").bind(aptId).run();
        return json({ success: true, message: "Appointment permanently removed" });
      }
`;

const markerRegex = /\/\/\s*==========================================\s*\r?\n\s*\/\/\s*8\.\s*NOTIFICATIONS/g;
if (markerRegex.test(code)) {
    code = code.replace(markerRegex, newEndpoints + "\n      // ==========================================\n      // 8. NOTIFICATIONS");
} else {
    console.log("marker 1 not found");
}

const msgRegex = /if \(\!apt\) return json\(\{ success: false, message: "Appointment not found" \}, 404\);\s*if \(apt\.status === \x27CANCELLED\x27\) return json\(\{ success: false, message: "Cannot send messages for cancelled appointments" \}, 403\);/g;
if (msgRegex.test(code)) {
    code = code.replace(msgRegex, `if (!apt) return json({ success: false, message: "Appointment not found" }, 404);
          if (apt.status === "CANCELLED") return json({ success: false, message: "Cannot send messages for cancelled appointments" }, 403);
          if (apt.status === "SUSPENDED") return json({ success: false, message: "Connection is on hold. Appointment is suspended." }, 403);`);
} else {
    console.log("msgRegex not found");
}

const statRegex = /if \(\!apt\) return json\(\{ success: false, message: "Appointment not found" \}, 404\);\s*if \(user\.role === "CUSTOMER" && apt\.customer_id !== user\.id\) return json\(\{ success: false, message: "Access denied" \}, 403\);/g;
if (statRegex.test(code)) {
    code = code.replace(statRegex, `if (!apt) return json({ success: false, message: "Appointment not found" }, 404);
        if (apt.status === "SUSPENDED") return json({ success: false, message: "Cannot update status of a suspended appointment" }, 403);
        if (user.role === "CUSTOMER" && apt.customer_id !== user.id) return json({ success: false, message: "Access denied" }, 403);`);
} else {
    console.log("statRegex not found");
}

fs.writeFileSync("cloudflare-backend/worker.js", code);
console.log("Worker patched successfully!");

