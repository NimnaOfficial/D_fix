const fs = require('fs');
let code = fs.readFileSync('cloudflare-backend/worker.js', 'utf8');

// fix the malformed backticks
code = code.replace(/let availableTech = await env\.DB\.prepare\(            SELECT t\.id/g, 'let availableTech = await env.DB.prepare(\SELECT t.id');
code = code.replace(/LIMIT 1\s*\\\)\.bind\(branch_id, service_id\)\.first\(\);/g, 'LIMIT 1\).bind(branch_id, service_id).first();');

code = code.replace(/availableTech = await env\.DB\.prepare\(                SELECT id/g, 'availableTech = await env.DB.prepare(\SELECT id');
code = code.replace(/LIMIT 1\s*\\\)\.bind\(branch_id\)\.first\(\);/g, 'LIMIT 1\).bind(branch_id).first();');

fs.writeFileSync('cloudflare-backend/worker.js', code);
console.log('Fixed js syntax!');

