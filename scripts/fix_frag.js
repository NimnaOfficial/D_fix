const fs = require("fs");
let code = fs.readFileSync("app/src/main/java/com/mad/techfix/ui/admin/profile/ManagerProfileFragment.java", "utf8");

code = code.replace(
    /apiService = RetrofitClient\.getClient\(requireContext\(\)\)\.create\(ApiService\.class\);/,
    "apiService = RetrofitClient.getClient().create(ApiService.class);"
);

code = code.replace(
    /User user = sessionManager\.getUser\(\);\s*if \(user != null\) \{\s*etEmail\.setText\(user\.getEmail\(\)\);\s*etFirstName\.setText\(user\.getFirst_name\(\)\);\s*etLastName\.setText\(user\.getLast_name\(\)\);\s*\}/,
    `etEmail.setText(sessionManager.getUserEmail());
        String fullName = sessionManager.getUserName();
        if (fullName != null) {
            String[] parts = fullName.split(" ");
            if (parts.length > 0) etFirstName.setText(parts[0]);
            if (parts.length > 1) etLastName.setText(parts[1]);
        }`
);

code = code.replace(
    /User u = sessionManager\.getUser\(\);\s*u\.setFirst_name\(fName\);\s*u\.setLast_name\(lName\);\s*sessionManager\.saveAuthSession\(sessionManager\.getBearerToken\(\), u\);/,
    `sessionManager.saveUserSession(
                        sessionManager.getUserId(), 
                        sessionManager.getUserEmail(), 
                        sessionManager.getUserRole(), 
                        sessionManager.getBearerToken(), 
                        fName + " " + lName, 
                        sessionManager.getUserPhone()
                    );`
);

fs.writeFileSync("app/src/main/java/com/mad/techfix/ui/admin/profile/ManagerProfileFragment.java", code);

