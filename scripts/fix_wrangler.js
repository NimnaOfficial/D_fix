const fs = require("fs");
let code = fs.readFileSync("cloudflare-backend/wrangler.toml", "utf8");
code = code.replace(/STRIPE_SECRET_KEY = "sk_test_.*"/, "STRIPE_SECRET_KEY = \"\" # Set securely via wrangler secret put");
fs.writeFileSync("cloudflare-backend/wrangler.toml", code);

