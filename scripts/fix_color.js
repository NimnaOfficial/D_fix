const fs = require("fs");
let xml = fs.readFileSync("app/src/main/res/layout/bottom_sheet_appointment_detail.xml", "utf8");

xml = xml.replace(/@color\/error_red/g, "@color/app_error");

fs.writeFileSync("app/src/main/res/layout/bottom_sheet_appointment_detail.xml", xml);
console.log("Color fixed!");

