import re

with open('worker.js', 'r', encoding='utf-8') as f:
    content = f.read()

messages_code = """
      // -- MESSAGES --
      if (path.startsWith("/api/appointments/") && path.includes("/messages")) {
        const user = await authenticate(request, env);
        if (!user) return json({ success: false, message: "Unauthorized" }, 401);
        const parts = path.split("/");
        const appointmentId = parts[3];

        if (request.method === "POST") {
          try {
              const { message, image_url } = await request.json();
              const appt = await env.DB.prepare(`SELECT * FROM appointments WHERE id = ?`).bind(appointmentId).first();
              if (!appt || (appt.status !== 'ASSIGNED' && appt.status !== 'DIAGNOSING' && appt.status !== 'REPAIRING' && appt.status !== 'TESTING')) {
                return json({ success: false, message: "Messages only allowed during active repair" }, 400);
              }
              
              let receiver_id = "";
              if (user.role === "CUSTOMER") {
                 let tech = await env.DB.prepare(`SELECT user_id FROM technicians WHERE id = ?`).bind(appt.technician_id).first();
                 if (!tech) return json({ success: false, message: "Technician not assigned" }, 400);
                 receiver_id = tech.user_id;
              } else {
                 receiver_id = appt.customer_id;
              }

              const msgId = crypto.randomUUID();
              await env.DB.prepare(
                `INSERT INTO messages (id, appointment_id, sender_id, receiver_id, message, image_url) VALUES (?, ?, ?, ?, ?, ?)`
              ).bind(msgId, appointmentId, user.id, receiver_id, message || "", image_url || "").run();

              return json({ success: true, message: "Message sent", data: { id: msgId, message, image_url, sender_id: user.id, created_at: new Date().toISOString() } }, 201);
          } catch(err) {
              return json({ success: false, message: err.message }, 500);
          }
        }

        if (request.method === "GET") {
          const msgs = await env.DB.prepare(
            `SELECT * FROM messages WHERE appointment_id = ? ORDER BY created_at ASC`
          ).bind(appointmentId).all();
          return json({ success: true, data: msgs.results });
        }
      }

      // -- UPLOAD (R2) --
      if (path === "/api/upload" && request.method === "POST") {
        const user = await authenticate(request, env);
        if (!user) return json({ success: false, message: "Unauthorized" }, 401);
        
        try {
            const formData = await request.formData();
            const file = formData.get("file");
            if (!file) return json({ success: false, message: "No file provided" }, 400);
            
            const ext = file.name ? file.name.split('.').pop() : 'jpg';
            const key = `uploads/${crypto.randomUUID()}.${ext}`;
            
            if (!env.BUCKET) return json({ success: false, message: "R2 Bucket not configured" }, 500);

            await env.BUCKET.put(key, file.stream(), {
              httpMetadata: { contentType: file.type || 'image/jpeg' }
            });
            
            // Return full url based on current request host
            const hostUrl = new URL(request.url).origin;
            const url = `${hostUrl}/api/images/${key}`;
            return json({ success: true, url });
        } catch(err) {
            return json({ success: false, message: err.message }, 500);
        }
      }

      // -- SERVE IMAGE (R2) --
      if (path.startsWith("/api/images/uploads/") && request.method === "GET") {
        const key = path.replace("/api/images/", "");
        if (!env.BUCKET) return new Response("Bucket not configured", { status: 500 });
        const object = await env.BUCKET.get(key);
        if (!object) return new Response("Not found", { status: 404 });
        
        const headers = new Headers();
        object.writeHttpMetadata(headers);
        headers.set("etag", object.httpEtag);
        return new Response(object.body, { headers });
      }
"""

content = content.replace('      // -- IMAGES --', messages_code + '\n      // -- IMAGES --')

with open('worker.js', 'w', encoding='utf-8') as f:
    f.write(content)

print("Done")
