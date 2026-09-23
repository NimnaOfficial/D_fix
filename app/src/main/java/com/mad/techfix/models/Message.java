package com.mad.techfix.models;

public class Message {
    private String id;
    private String appointment_id;
    private String sender_id;
    private String receiver_id;
    private String message;
    private String image_url;
    private String created_at;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAppointment_id() { return appointment_id; }
    public void setAppointment_id(String appointment_id) { this.appointment_id = appointment_id; }
    
    public String getSender_id() { return sender_id; }
    public void setSender_id(String sender_id) { this.sender_id = sender_id; }
    
    public String getReceiver_id() { return receiver_id; }
    public void setReceiver_id(String receiver_id) { this.receiver_id = receiver_id; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
    
    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}
