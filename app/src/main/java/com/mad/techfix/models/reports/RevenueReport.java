package com.mad.techfix.models.reports;
import com.mad.techfix.models.Payment;
import java.util.List;
public class RevenueReport {
    private List<Payment> transactions;
    private double total_revenue;
    private double pending_revenue;
    public List<Payment> getTransactions() { return transactions; }
    public double getTotal_revenue() { return total_revenue; }
    public double getPending_revenue() { return pending_revenue; }
}
