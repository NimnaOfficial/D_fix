import re

with open('cloudflare-backend/worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

pattern = r'// Cancel Appointment\s+if \(path\.startsWith\("/api/appointments/"\) && path\.endsWith\("/cancel"\).*?return json\(\{ success: true, message: "Appointment cancelled" \}\);\s+\}'

new_code = '''// Cancel Appointment
      if (path.startsWith("/api/appointments/") && path.endsWith("/cancel") && request.method === "PUT") {
        const user = await authenticate(request, env);
        if (!user) return json({ success: false, message: "Unauthorized" }, 401);
        const aptId = path.split("/")[3];
        
        // Verify ownership for customer
        const apt = await env.DB.prepare(SELECT id, status, customer_id, technician_id, branch_id FROM appointments WHERE id = ?).bind(aptId).first();
        if (!apt) return json({ success: false, message: "Appointment not found" }, 404);

        if (user.role === "CUSTOMER") {
          if (apt.customer_id !== user.id) return json({ success: false, message: "Access denied" }, 403);
          if (apt.status !== "REQUESTED" && apt.status !== "ASSIGNED") return json({ success: false, message: "Only REQUESTED or ASSIGNED appointments can be cancelled" }, 400);
        }
        
        await env.DB.batch([
            env.DB.prepare(UPDATE appointments SET status = 'CANCELLED', updated_at = CURRENT_TIMESTAMP WHERE id = ?).bind(aptId),
            env.DB.prepare(INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, 'CANCELLED', 'Cancelled by user', ?)).bind(crypto.randomUUID(), aptId, user.id)
        ]);
        
        // If technician was assigned, handle freeing them up
        if (apt.technician_id && (apt.status === "REQUESTED" || apt.status === "ASSIGNED")) {
            // Find next waiting appointment for this branch
            const pendingApt = await env.DB.prepare(
              SELECT a.id FROM appointments a WHERE a.status = 'REQUESTED' AND a.branch_id = ? AND a.technician_id IS NULL ORDER BY a.created_at ASC LIMIT 1
            ).bind(apt.branch_id).first();

            if (pendingApt) {
              await env.DB.prepare(UPDATE appointments SET technician_id = ?, status = 'ASSIGNED', updated_at = CURRENT_TIMESTAMP WHERE id = ?).bind(apt.technician_id, pendingApt.id).run();
              await env.DB.prepare(INSERT INTO repair_status_history (id, appointment_id, status, note, changed_by) VALUES (?, ?, 'ASSIGNED', 'System auto-assigned to freed technician', ?)).bind(crypto.randomUUID(), pendingApt.id, user.id).run();
            } else {
              await env.DB.prepare(UPDATE technicians SET availability_status = 'AVAILABLE' WHERE id = ?).bind(apt.technician_id).run();
            }
        }
        
        return json({ success: true, message: "Appointment cancelled" });
      }'''

if re.search(pattern, content, re.DOTALL):
    content = re.sub(pattern, new_code, content, flags=re.DOTALL)
    with open('cloudflare-backend/worker.js', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Success")
else:
    print("Failed to find pattern")
