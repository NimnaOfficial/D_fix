package com.mad.techfix.models;
public class StockAdjustmentRequest {
    private int quantity;
    private String reason;
    private String branch_id;
    public StockAdjustmentRequest(int quantity, String reason, String branch_id) {
        this.quantity = quantity;
        this.reason = reason;
        this.branch_id = branch_id;
    }
    public int getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public String getBranch_id() { return branch_id; }
}
