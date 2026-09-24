const fs = require("fs");

let api = fs.readFileSync("app/src/main/java/com/mad/techfix/network/AdminApiService.java", "utf8");
api = api.replace(
    /@PUT\("api\/appointments\/\{id\}\/assign"\)\s*@PUT\("api\/appointments\/\{id\}\/suspend"\)\s*Call<ApiResponse<Object>> suspendAppointment/,
    `@PUT("api/appointments/{id}/suspend")
    Call<ApiResponse<Object>> suspendAppointment`
);

api = api.replace(
    /Call<ApiResponse<Object>> assignTechnician\(@Header\("Authorization"\) String auth, @Path\("id"\) String appointmentId, @Body AssignTechnicianRequest request\);/,
    `@PUT("api/appointments/{id}/assign")
    Call<ApiResponse<Object>> assignTechnician(@Header("Authorization") String auth, @Path("id") String appointmentId, @Body AssignTechnicianRequest request);`
);

fs.writeFileSync("app/src/main/java/com/mad/techfix/network/AdminApiService.java", api);

let vm = fs.readFileSync("app/src/main/java/com/mad/techfix/viewmodel/AdminViewModel.java", "utf8");
vm = vm.replace(/errorLiveData\.setValue\(error\)/g, "errorMessage.setValue(error)");
fs.writeFileSync("app/src/main/java/com/mad/techfix/viewmodel/AdminViewModel.java", vm);

console.log("Fixed compile errors!");

