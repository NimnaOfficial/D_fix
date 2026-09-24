const fs = require("fs");

let apiService = fs.readFileSync("app/src/main/java/com/mad/techfix/network/AdminApiService.java", "utf8");
apiService = apiService.replace(
    /Call<ApiResponse<Object>> assignTechnician\(/,
    `@PUT("api/appointments/{id}/suspend")
    Call<ApiResponse<Object>> suspendAppointment(@Header("Authorization") String auth, @Path("id") String appointmentId);

    @PUT("api/appointments/{id}/resume")
    Call<ApiResponse<Object>> resumeAppointment(@Header("Authorization") String auth, @Path("id") String appointmentId);

    @DELETE("api/appointments/{id}")
    Call<ApiResponse<Object>> removeAppointment(@Header("Authorization") String auth, @Path("id") String appointmentId);

    Call<ApiResponse<Object>> assignTechnician(`
);
fs.writeFileSync("app/src/main/java/com/mad/techfix/network/AdminApiService.java", apiService);

let repo = fs.readFileSync("app/src/main/java/com/mad/techfix/repository/AdminRepository.java", "utf8");
repo = repo.replace(
    /public void assignTechnician\(/,
    `public void suspendAppointment(String token, String aptId, AdminCallback<Void> callback) {
        apiService.suspendAppointment("Bearer " + token, aptId).enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
            @Override public void onResponse(retrofit2.Call<ApiResponse<Object>> call, retrofit2.Response<ApiResponse<Object>> response) {
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to suspend");
            }
            @Override public void onFailure(retrofit2.Call<ApiResponse<Object>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void resumeAppointment(String token, String aptId, AdminCallback<Void> callback) {
        apiService.resumeAppointment("Bearer " + token, aptId).enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
            @Override public void onResponse(retrofit2.Call<ApiResponse<Object>> call, retrofit2.Response<ApiResponse<Object>> response) {
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to resume");
            }
            @Override public void onFailure(retrofit2.Call<ApiResponse<Object>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void removeAppointment(String token, String aptId, AdminCallback<Void> callback) {
        apiService.removeAppointment("Bearer " + token, aptId).enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
            @Override public void onResponse(retrofit2.Call<ApiResponse<Object>> call, retrofit2.Response<ApiResponse<Object>> response) {
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to remove");
            }
            @Override public void onFailure(retrofit2.Call<ApiResponse<Object>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void assignTechnician(`
);
fs.writeFileSync("app/src/main/java/com/mad/techfix/repository/AdminRepository.java", repo);

let vm = fs.readFileSync("app/src/main/java/com/mad/techfix/viewmodel/AdminViewModel.java", "utf8");
vm = vm.replace(
    /public void assignTechnician\(/,
    `public void suspendAppointment(String aptId) {
        String token = getToken(); if(token == null) return;
        isLoading.setValue(true);
        repository.suspendAppointment(token, aptId, new AdminRepository.AdminCallback<Void>() {
            @Override public void onSuccess(Void result) { isLoading.setValue(false); loadAllAppointments(); }
            @Override public void onError(String error) { isLoading.setValue(false); errorLiveData.setValue(error); }
        });
    }

    public void resumeAppointment(String aptId) {
        String token = getToken(); if(token == null) return;
        isLoading.setValue(true);
        repository.resumeAppointment(token, aptId, new AdminRepository.AdminCallback<Void>() {
            @Override public void onSuccess(Void result) { isLoading.setValue(false); loadAllAppointments(); }
            @Override public void onError(String error) { isLoading.setValue(false); errorLiveData.setValue(error); }
        });
    }

    public void removeAppointment(String aptId) {
        String token = getToken(); if(token == null) return;
        isLoading.setValue(true);
        repository.removeAppointment(token, aptId, new AdminRepository.AdminCallback<Void>() {
            @Override public void onSuccess(Void result) { isLoading.setValue(false); loadAllAppointments(); }
            @Override public void onError(String error) { isLoading.setValue(false); errorLiveData.setValue(error); }
        });
    }

    public void assignTechnician(`
);
fs.writeFileSync("app/src/main/java/com/mad/techfix/viewmodel/AdminViewModel.java", vm);

console.log("Updated Api, Repo, VM");

