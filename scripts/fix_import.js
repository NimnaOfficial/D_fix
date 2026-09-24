const fs = require("fs");
let code = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/profile/ManagerProfileFragment.java", "utf8");
code = code.replace("import com.mad.techfix.utils.SessionManager;", "import com.mad.techfix.data.SessionManager;");
fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/profile/ManagerProfileFragment.java", code);

