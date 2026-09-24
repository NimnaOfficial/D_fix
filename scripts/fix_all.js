
const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

// 1. Fix t.is_active to u.is_active
code = code.replace(/t\.is_active/g, "u.is_active");

// 2. Fix /api/auth/me to include technician specialization
let authMeTarget = "SELECT u.id, u.first_name, u.last_name, u.email, u.phone, u.role, u.profile_image_url, c.city, c.address, c.loyalty_points FROM users u LEFT JOIN customers c ON u.id = c.user_id WHERE u.id = ? LIMIT 1";
let authMeRepl = "SELECT u.id, u.first_name, u.last_name, u.email, u.phone, u.role, u.profile_image_url, c.city, c.address, c.loyalty_points, t.specialization, t.employee_code, t.branch_id FROM users u LEFT JOIN customers c ON u.id = c.user_id LEFT JOIN technicians t ON u.id = t.user_id WHERE u.id = ? LIMIT 1";
code = code.replace(authMeTarget, authMeRepl);

// 3. Fix auto assignment logic to include fallback
let assignTarget = `const availableTech = await env.DB.prepare(\`
            SELECT t.id 
            FROM technicians t 
            INNER JOIN technician_services ts ON t.id = ts.technician_id
            WHERE t.branch_id = ? AND ts.service_id = ? AND t.availability_status = "AVAILABLE" 
            LIMIT 1
        \`).bind(branch_id, service_id).first();`;

let assignRepl = `let availableTech = await env.DB.prepare(\`
            SELECT t.id 
            FROM technicians t 
            INNER JOIN technician_services ts ON t.id = ts.technician_id
            WHERE t.branch_id = ? AND ts.service_id = ? AND t.availability_status = "AVAILABLE" 
            LIMIT 1
        \`).bind(branch_id, service_id).first();

        // Fallback: If no technician with specific skills is free, pick ANY available technician at that branch
        if (!availableTech) {
            availableTech = await env.DB.prepare(\`
                SELECT id 
                FROM technicians 
                WHERE branch_id = ? AND availability_status = "AVAILABLE" 
                LIMIT 1
            \`).bind(branch_id).first();
        }`;
code = code.replace(assignTarget, assignRepl);

fs.writeFileSync("cloudflare-backend/worker.js", code);
console.log("All fixes applied!");

