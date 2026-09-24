const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

const profileApi = `      if (path === "/api/auth/profile" && request.method === "PUT") {
        const userReq = await authenticate(request, env);
        if (!userReq) return json({ success: false, message: "Unauthorized" }, 401);
        
        const { first_name, last_name, phone } = await request.json();
        if (!first_name || !last_name) return json({ success: false, message: "First and Last name required" }, 400);

        await env.DB.prepare("UPDATE users SET first_name = ?, last_name = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
          .bind(first_name.trim(), last_name.trim(), phone ? phone.trim() : null, userReq.id)
          .run();
          
        return json({ success: true, message: "Profile updated successfully" });
      }

      if (path === "/api/auth/password" && request.method === "PUT") {
        const userReq = await authenticate(request, env);
        if (!userReq) return json({ success: false, message: "Unauthorized" }, 401);

        const { current_password, new_password } = await request.json();
        if (!current_password || !new_password) return json({ success: false, message: "Passwords required" }, 400);
        if (new_password.length < 8) return json({ success: false, message: "New password must be at least 8 chars" }, 400);

        const user = await env.DB.prepare("SELECT password_hash FROM users WHERE id = ?").bind(userReq.id).first();
        if (!user) return json({ success: false, message: "User not found" }, 404);

        if (user.password_hash !== await hashPassword(current_password)) {
           return json({ success: false, message: "Incorrect current password" }, 400);
        }

        await env.DB.prepare("UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
          .bind(await hashPassword(new_password), userReq.id)
          .run();
          
        return json({ success: true, message: "Password updated successfully" });
      }

      if (path === "/api/auth/me" && request.method === "GET") {`;

code = code.replace(/if \(path === "\/api\/auth\/me" && request\.method === "GET"\) \{/, profileApi);
fs.writeFileSync("cloudflare-backend/worker.js", code);
console.log("Profile APIs added to worker.js");

