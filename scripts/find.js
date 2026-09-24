
const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/worker.js", "utf8");
let m = code.match(/if \([\s\S]*?endsWith\("\/status"\)[\s\S]*?return json\(\{ success: true, message: .*?\} \);[\s\S]*?\}/);
if (m) console.log("Matched!"); else console.log("Not matched");

