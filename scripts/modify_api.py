with open('app/src/main/java/com/mad/techfix/network/ApiService.java', 'r', encoding='utf-8') as f:
    content = f.read()

injection = """
    // ==========================================
    // MESSAGING & R2 UPLOAD
    // ==========================================

    @POST("api/appointments/{id}/messages")
    Call<ApiResponse<Object>> sendMessage(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId,
            @Body java.util.Map<String, String> body
    );

    @GET("api/appointments/{id}/messages")
    Call<ApiResponse<java.util.List<com.mad.techfix.models.Message>>> getMessages(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId
    );

    @retrofit2.http.Multipart
    @POST("api/upload")
    Call<java.util.Map<String, Object>> uploadFile(
            @Header("Authorization") String auth,
            @retrofit2.http.Part okhttp3.MultipartBody.Part file
    );

}
"""

content = content.replace('}', injection)

with open('app/src/main/java/com/mad/techfix/network/ApiService.java', 'w', encoding='utf-8') as f:
    f.write(content)
