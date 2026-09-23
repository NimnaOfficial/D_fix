package com.mad.techfix.ui.customer.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.repository.BookingRepository;
import com.mad.techfix.viewmodel.RepairBookingViewModel;

import java.util.Calendar;
import java.util.Locale;

public class RepairBookingFragment extends Fragment {

    private String pendingSelectDeviceId;

    private RecyclerView recyclerDevices;
    private AutoCompleteTextView actBookingService;
    private TextView tvSelectedDevice;
    private TextView tvSelectedService;
    private MaterialButton btnAddDevice;
    private MaterialButton btnContinue;

    private BookingDeviceAdapter deviceAdapter;

    private RepairBookingViewModel viewModel;

    private Device selectedDevice;
    private Service selectedService;
    private String selectedDate;
    private String selectedTime;


    public RepairBookingFragment() {
        // Required empty constructor
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_repair_booking,
                container,
                false
        );
    }


    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        bindViews(view);

        setupRecyclerViews();

        setupListeners();

        setupViewModel();

        observeViewModel();

        viewModel.loadBookingData();
    }


    // ==========================================
    // VIEW BINDING
    // ==========================================

    private void bindViews(
            View view
    ) {

        recyclerDevices =
                view.findViewById(
                        R.id.recycler_booking_devices
                );

        actBookingService =
                view.findViewById(
                        R.id.act_booking_service
                );

        tvSelectedDevice =
                view.findViewById(
                        R.id.tv_selected_device
                );

        tvSelectedService =
                view.findViewById(
                        R.id.tv_selected_service
                );

        btnAddDevice =
                view.findViewById(
                        R.id.btn_add_device
                );

        btnContinue =
                view.findViewById(
                        R.id.btn_continue_booking
                );
    }


    // ==========================================
    // RECYCLER VIEWS
    // ==========================================

    private void setupRecyclerViews() {

        deviceAdapter =
                new BookingDeviceAdapter(
                        device -> {

                            selectedDevice =
                                    device;

                            String name =
                                    device.getDisplayName();

                            if (name == null
                                    || name.trim().isEmpty()) {

                                name =
                                        "Selected device";
                            }

                            tvSelectedDevice.setText(
                                    "Device: " + name
                            );
                        }
                );


        recyclerDevices.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerDevices.setAdapter(
                deviceAdapter
        );
    }


    // ==========================================
    // VIEW MODEL
    // ==========================================

    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(
                                RepairBookingViewModel.class
                        );
    }


    private void observeViewModel() {

        viewModel
                .getDevices()
                .observe(
                        getViewLifecycleOwner(),
                        devices -> {

                            deviceAdapter
                                    .setDevices(
                                            devices
                                    );

                            if (pendingSelectDeviceId != null) {
                                deviceAdapter.selectDeviceById(pendingSelectDeviceId);
                                pendingSelectDeviceId = null;
                            } else if (selectedDevice != null) {
                                deviceAdapter.selectDeviceById(selectedDevice.getId());
                            }
                        }
                );


        viewModel
                .getServices()
                .observe(
                        getViewLifecycleOwner(),
                        services -> {

                            if (services == null) return;

                            String[] serviceNames = new String[services.size()];
                            for (int i = 0; i < services.size(); i++) {
                                serviceNames[i] = services.get(i).getName();
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    requireContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    serviceNames
                            ) {
                                @NonNull
                                @Override
                                public android.widget.Filter getFilter() {
                                    return new android.widget.Filter() {
                                        @Override
                                        protected FilterResults performFiltering(CharSequence constraint) {
                                            FilterResults results = new FilterResults();
                                            results.values = serviceNames;
                                            results.count = serviceNames.length;
                                            return results;
                                        }
                                        @Override
                                        protected void publishResults(CharSequence constraint, FilterResults results) {
                                            notifyDataSetChanged();
                                        }
                                    };
                                }
                            };
                            
                            actBookingService.setAdapter(adapter);
                            
                            actBookingService.setOnItemClickListener((parent, view, position, id) -> {
                                selectedService = services.get(position);
                                String name = selectedService.getName();
                                if (name == null || name.trim().isEmpty()) {
                                    name = "Selected service";
                                }
                                tvSelectedService.setText("Service: " + name);
                            });
                        }
                );


        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            boolean isLoading =
                                    loading != null
                                            && loading;

                            setLoadingState(
                                    isLoading
                            );
                        }
                );


        viewModel
                .getErrorMessage()
                .observe(
                        getViewLifecycleOwner(),
                        message -> {

                            if (message == null
                                    || message.trim().isEmpty()) {

                                return;
                            }

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            viewModel.clearError();
                        }
                );
    }


    private void setLoadingState(
            boolean loading
    ) {

        btnContinue.setEnabled(
                !loading
        );

        btnAddDevice.setEnabled(
                !loading
        );


        if (loading) {

            btnContinue.setText(
                    "Loading..."
            );

        } else {

            btnContinue.setText(
                    "Continue"
            );
        }
    }


    // ==========================================
    // BUTTON LISTENERS
    // ==========================================

    private void setupListeners() {

        btnContinue.setOnClickListener(
                v -> {

                    if (!validateSelections()) {
                        return;
                    }

                    showDateTimeDialog();
                }
        );


        btnAddDevice.setOnClickListener(v -> showAddDeviceDialog());
    }


    // ==========================================
    // ADD NEW DEVICE DIALOG
    // ==========================================

    private void showAddDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_device_form, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        AutoCompleteTextView actCategory = view.findViewById(R.id.actCategory);
        TextInputEditText etBrand = view.findViewById(R.id.etBrand);
        TextInputEditText etModel = view.findViewById(R.id.etModel);
        TextInputEditText etSerialNumber = view.findViewById(R.id.etSerialNumber);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        tvTitle.setText("Add New Device");

        String[] categories = {"Smartphone", "Laptop", "Tablet", "Desktop", "Smartwatch"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
        ) {
            @NonNull
            @Override
            public android.widget.Filter getFilter() {
                return new android.widget.Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = categories;
                        results.count = categories.length;
                        return results;
                    }
                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        notifyDataSetChanged();
                    }
                };
            }
        };
        actCategory.setAdapter(adapter);
        actCategory.setText("Smartphone", false);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String brand = etBrand.getText() != null ? etBrand.getText().toString().trim() : "";
            String model = etModel.getText() != null ? etModel.getText().toString().trim() : "";
            String serial = etSerialNumber.getText() != null ? etSerialNumber.getText().toString().trim() : "";
            String selectedCategory = actCategory.getText() != null ? actCategory.getText().toString().trim() : "";

            if (brand.isEmpty()) {
                etBrand.setError("Brand is required");
                etBrand.requestFocus();
                return;
            }

            if (model.isEmpty()) {
                etModel.setError("Model is required");
                etModel.requestFocus();
                return;
            }

            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
            btnSave.setText("Saving...");

            Device device = new Device();
            device.setBrand(brand);
            device.setModel(model);
            if (!serial.isEmpty()) {
                device.setSerialNumber(serial);
            }
            device.setCategoryId(mapCategoryToId(selectedCategory));

            viewModel.addDevice(device, new BookingRepository.BookingCallback<Device>() {
                @Override
                public void onSuccess(Device createdDevice) {
                    Toast.makeText(
                            requireContext(),
                            "Device added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    if (createdDevice != null && createdDevice.getId() != null) {
                        pendingSelectDeviceId = createdDevice.getId();
                    }

                    dialog.dismiss();
                }

                @Override
                public void onError(String message) {
                    btnSave.setEnabled(true);
                    btnCancel.setEnabled(true);
                    btnSave.setText("Save");

                    Toast.makeText(
                            requireContext(),
                            message != null ? message : "Failed to add device",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        dialog.show();
    }

    private String mapCategoryToId(String categoryName) {
        if (categoryName == null) return "CAT-003";
        switch (categoryName.trim().toLowerCase(Locale.ROOT)) {
            case "laptop":
                return "CAT-001";
            case "desktop":
            case "desktop pc":
                return "CAT-002";
            case "smartphone":
            case "mobile phone":
            case "mobile":
                return "CAT-003";
            case "tablet":
                return "CAT-004";
            case "smartwatch":
                return "CAT-005";
            default:
                return "CAT-003";
        }
    }


    // ==========================================
    // VALIDATION
    // ==========================================

    private boolean validateSelections() {

        if (selectedDevice == null) {

            Toast.makeText(
                    requireContext(),
                    "Please select a device",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        if (selectedService == null) {

            Toast.makeText(
                    requireContext(),
                    "Please select a service",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        if (false) {

            Toast.makeText(
                    requireContext(),
                    "Please select a branch",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        return true;
    }


    // ==========================================
    // DATE / TIME DIALOG
    // ==========================================

    private void showDateTimeDialog() {

        View dialogView =
                LayoutInflater
                        .from(
                                requireContext()
                        )
                        .inflate(
                                R.layout.dialog_booking_datetime,
                                null,
                                false
                        );


        TextView tvDate =
                dialogView.findViewById(
                        R.id.tv_selected_date
                );

        TextView tvTime =
                dialogView.findViewById(
                        R.id.tv_selected_time
                );

        View cardDate =
                dialogView.findViewById(
                        R.id.card_select_date
                );

        View cardTime =
                dialogView.findViewById(
                        R.id.card_select_time
                );

        MaterialButton btnCancel =
                dialogView.findViewById(
                        R.id.btn_cancel_datetime
                );

        MaterialButton btnConfirm =
                dialogView.findViewById(
                        R.id.btn_confirm_datetime
                );


        AlertDialog dialog =
                new AlertDialog.Builder(
                        requireContext()
                )
                        .setView(
                                dialogView
                        )
                        .create();


        cardDate.setOnClickListener(
                v -> showDatePicker(
                        tvDate
                )
        );


        cardTime.setOnClickListener(
                v -> showTimePicker(
                        tvTime
                )
        );


        btnCancel.setOnClickListener(
                v -> dialog.dismiss()
        );


        btnConfirm.setOnClickListener(
                v -> {

                    if (selectedDate == null
                            || selectedDate.trim().isEmpty()) {

                        Toast.makeText(
                                requireContext(),
                                "Please select a date",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    if (selectedTime == null
                            || selectedTime.trim().isEmpty()) {

                        Toast.makeText(
                                requireContext(),
                                "Please select a time",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    dialog.dismiss();

                    openBookingReview();
                }
        );


        dialog.show();
    }


    // ==========================================
    // DATE PICKER
    // ==========================================

    private void showDatePicker(
            TextView tvDate
    ) {

        Calendar calendar =
                Calendar.getInstance();

        int year =
                calendar.get(
                        Calendar.YEAR
                );

        int month =
                calendar.get(
                        Calendar.MONTH
                );

        int day =
                calendar.get(
                        Calendar.DAY_OF_MONTH
                );


        DatePickerDialog picker =
                new DatePickerDialog(
                        requireContext(),

                        (view,
                         selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            selectedDate =
                                    String.format(
                                            Locale.getDefault(),
                                            "%04d-%02d-%02d",
                                            selectedYear,
                                            selectedMonth + 1,
                                            selectedDay
                                    );


                            tvDate.setText(
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d/%02d/%04d",
                                            selectedDay,
                                            selectedMonth + 1,
                                            selectedYear
                                    )
                            );
                        },

                        year,
                        month,
                        day
                );


        picker.getDatePicker()
                .setMinDate(
                        System.currentTimeMillis()
                                - 1000
                );


        picker.show();
    }


    // ==========================================
    // TIME PICKER
    // ==========================================

    private void showTimePicker(
            TextView tvTime
    ) {

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(
                        Calendar.HOUR_OF_DAY
                );

        int minute =
                calendar.get(
                        Calendar.MINUTE
                );


        TimePickerDialog picker =
                new TimePickerDialog(
                        requireContext(),

                        (view,
                         selectedHour,
                         selectedMinute) -> {

                            selectedTime =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d",
                                            selectedHour,
                                            selectedMinute
                                    );


                            String displayTime =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d %s",
                                            selectedHour % 12 == 0
                                                    ? 12
                                                    : selectedHour % 12,
                                            selectedMinute,
                                            selectedHour < 12
                                                    ? "AM"
                                                    : "PM"
                                    );


                            tvTime.setText(
                                    displayTime
                            );
                        },

                        hour,
                        minute,
                        false
                );


        picker.show();
    }


    // ==========================================
    // NAVIGATION TO BOOKING REVIEW
    // ==========================================

    private void openBookingReview() {

        if (selectedDevice == null
                || selectedService == null
                ) {

            Toast.makeText(
                    requireContext(),
                    "Booking information is incomplete",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        BookingReviewFragment reviewFragment =
                BookingReviewFragment
                        .newInstance(
                                selectedDevice.getId(),
                                selectedDevice.getDisplayName(),

                                selectedService.getId(),
                                selectedService.getName(),
                                selectedService.getBasePrice(),

                                "auto",
                                "Auto Assigned",

                                selectedDate,
                                selectedTime
                        );


        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        getId(),
                        reviewFragment
                )
                .addToBackStack(
                        null
                )
                .commit();
    }
}