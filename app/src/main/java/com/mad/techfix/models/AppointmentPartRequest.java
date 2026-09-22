package com.mad.techfix.models;
public class AppointmentPartRequest {
    private String spare_part_id;
    private int quantity;
    private double unit_price;
    public AppointmentPartRequest(String spare_part_id, int quantity, double unit_price) {
        this.spare_part_id = spare_part_id;
        this.quantity = quantity;
        this.unit_price = unit_price;
    }
}
