package com.mad.techfix.models.reports;
public class ReportSummary {
    private double total_revenue;
    private int paid_count;
    private int pending_count;
    private int parts_used;
    private int low_stock_count;
    private int out_of_stock_count;
    
    public double getTotal_revenue() { return total_revenue; }
    public int getPaid_count() { return paid_count; }
    public int getPending_count() { return pending_count; }
    public int getParts_used() { return parts_used; }
    public int getLow_stock_count() { return low_stock_count; }
    public int getOut_of_stock_count() { return out_of_stock_count; }
}
