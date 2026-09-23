package com.mad.techfix.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusHistoryFragment extends Fragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";

    private String appointmentId;
    private RecyclerView rvStatusHistory;
    private ProgressBar progressBar;
    private ApiService apiService;
    private TokenManager tokenManager;

    public static StatusHistoryFragment newInstance(String appointmentId) {
        StatusHistoryFragment fragment = new StatusHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            appointmentId = getArguments().getString(ARG_APPOINTMENT_ID);
        }

        rvStatusHistory = view.findViewById(R.id.rv_status_history_full);
        progressBar = view.findViewById(R.id.progress_bar_status);

        view.findViewById(R.id.btn_back_status).setOnClickListener(v -> requireActivity().onBackPressed());

        rvStatusHistory.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());

        fetchStatusHistory();
    }

    private void fetchStatusHistory() {
        String token = tokenManager.getToken();
        if (token == null || appointmentId == null) return;

        progressBar.setVisibility(View.VISIBLE);

        apiService.getAppointmentHistory("Bearer " + token, appointmentId).enqueue(new Callback<ApiResponse<List<AppointmentDetail.StatusHistory>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<AppointmentDetail.StatusHistory>>> call, @NonNull Response<ApiResponse<List<AppointmentDetail.StatusHistory>>> response) {
                progressBar.setVisibility(View.GONE);
                View emptyView = getView() != null ? getView().findViewById(R.id.tv_empty_status) : null;
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<AppointmentDetail.StatusHistory> history = response.body().getData();
                    if (history != null && !history.isEmpty()) {
                        StatusHistoryAdapter adapter = new StatusHistoryAdapter();
                        adapter.updateList(history);
                        rvStatusHistory.setAdapter(adapter);
                        rvStatusHistory.setVisibility(View.VISIBLE);
                        if (emptyView != null) emptyView.setVisibility(View.GONE);
                    } else {
                        rvStatusHistory.setVisibility(View.GONE);
                        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load status history", Toast.LENGTH_SHORT).show();
                    rvStatusHistory.setVisibility(View.GONE);
                    if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<AppointmentDetail.StatusHistory>>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                View emptyView = getView() != null ? getView().findViewById(R.id.tv_empty_status) : null;
                rvStatusHistory.setVisibility(View.GONE);
                if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
            }
        });
    }
}
