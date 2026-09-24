const fs = require("fs");
let code = fs.readFileSync("app/src/main/java/com/mad/techfix/models/Appointment.java", "utf8");

code = code.replace(
    /private String technician_name;/,
    "private String technician_name;\n    private String technician_status;"
);

code = code.replace(
    /public String getTechnician_name\(\) \{/,
    `public String getTechnician_status() {
        return technician_status;
    }
    public void setTechnician_status(String technician_status) {
        this.technician_status = technician_status;
    }

    public String getTechnician_name() {`
);

fs.writeFileSync("app/src/main/java/com/mad/techfix/models/Appointment.java", code);
console.log("Appointment.java updated");

