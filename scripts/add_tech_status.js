const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");

// We need to add t.availability_status AS technician_status to the baseQuery in /api/appointments
code = code.replace(
    /END AS technician_name/g,
    "END AS technician_name,\n         t.availability_status AS technician_status"
);

fs.writeFileSync("cloudflare-backend/worker.js", code);
console.log("Updated worker.js with technician_status");

