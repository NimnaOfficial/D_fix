const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");
let idx = code.indexOf(`if (path === "/api/appointments" && request.method === "GET") {`);
if (idx > -1) {
    console.log(code.substring(idx, idx + 2500));
}

