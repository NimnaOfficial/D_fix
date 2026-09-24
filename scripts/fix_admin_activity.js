const fs = require("fs");
let code = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/AdminActivity.java", "utf8");

code = code.replace(
    /else if \(id == R\.id\.nav_reports\) \{\s*loadFragment\(new com\.mad\.techfix\.ui\.admin\.dashboard\.ReportsFragment\(\)\);\s*return true;\s*\}/,
    `else if (id == R.id.nav_profile) {
                loadFragment(new com.mad.techfix.ui.admin.profile.ManagerProfileFragment());
                return true;
            }`
);

fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/AdminActivity.java", code);
console.log("AdminActivity updated!");

