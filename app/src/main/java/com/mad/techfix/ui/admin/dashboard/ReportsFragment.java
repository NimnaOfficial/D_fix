package com.mad.techfix.ui.admin.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.reports.ReportSummary;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {

    private TextView tvTotalRevenue, tvPaidPending, tvPartsUsed, tvStockAlerts;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports_dashboard, container, false);
        
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        tvPaidPending = view.findViewById(R.id.tv_paid_pending);
        tvPartsUsed = view.findViewById(R.id.tv_parts_used);
        tvStockAlerts = view.findViewById(R.id.tv_stock_alerts);
        
        sessionManager = new SessionManager(requireContext());
        apiService = RetrofitClient.getApiService();
        
        loadReportSummary();
        
        return view;
    }

    private void loadReportSummary() {
        String token = sessionManager.getAuthToken();
        if (token == null) return;
        
        apiService.getReportSummary("Bearer " + token).enqueue(new Callback<ApiResponse<ReportSummary>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ReportSummary>> call, @NonNull Response<ApiResponse<ReportSummary>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ReportSummary summary = response.body().getData();
                    if (summary != null) {
                        tvTotalRevenue.setText(String.format("LKR %.2f", summary.getTotal_revenue()));
                        tvPaidPending.setText(summary.getPaid_count() + " / " + summary.getPending_count());
                        tvPartsUsed.setText(String.valueOf(summary.getParts_used()));
                        tvStockAlerts.setText("Low: " + summary.getLow_stock_count() + " | Out: " + summary.getOut_of_stock_count());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ReportSummary>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load reports", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
