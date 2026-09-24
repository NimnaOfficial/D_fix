
const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

const assignRegex = /const availableTech = await env\.DB\.prepare\(\`[\s\S]*?LIMIT 1\n\s*\`\)\.bind\(branch_id, service_id\)\.first\(\);/g;

const assignRepl = `let availableTech = await env.DB.prepare(\`
            SELECT t.id 
            FROM technicians t 
            INNER JOIN technician_services ts ON t.id = ts.technician_id
            WHERE t.branch_id = ? AND ts.service_id = ? AND t.availability_status = "AVAILABLE" 
            LIMIT 1
        \`).bind(branch_id, service_id).first();

        if (!availableTech) {
            availableTech = await env.DB.prepare(\`
                SELECT id 
                FROM technicians 
                WHERE branch_id = ? AND availability_status = "AVAILABLE" 
                LIMIT 1
            \`).bind(branch_id).first();
        }`;

code = code.replace(assignRegex, assignRepl);

fs.writeFileSync("cloudflare-backend/worker.js", code);
console.log("Regex auto assign fix applied!");

