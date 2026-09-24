
const fs = require('fs');
let code = fs.readFileSync('cloudflare-backend/worker.js', 'utf8');
let idx = code.indexOf('/messages');
if (idx > -1) {
    let start = Math.max(0, code.lastIndexOf('if', idx));
    console.log(code.substring(start, start + 1500));
}

