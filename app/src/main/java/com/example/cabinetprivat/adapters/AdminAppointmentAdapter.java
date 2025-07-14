package com.example.cabinetprivat.adapters; // Asigură-te că pachetul este corect

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetprivat.AdminActivity;
import com.example.cabinetprivat.R;
import com.example.cabinetprivat.models.Appointment; // Asigură-te că acest import este corect
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class AdminAppointmentAdapter extends RecyclerView.Adapter<AdminAppointmentAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;
    private FirebaseFirestore db; // Instanța Firestore pentru actualizări

    // Posibile statusuri pentru programări
    private final String[] STATUS_OPTIONS = {"Pending", "Approved", "Rejected", "Rescheduled", "Completed", "Cancelled"};

    public AdminAppointmentAdapter(Context context, List<Appointment> appointmentList, FirebaseFirestore db) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.userEmailTextView.setText(String.format(Locale.getDefault(), "Utilizator: %s", appointment.getUserEmail()));
        holder.doctorTextView.setText(String.format(Locale.getDefault(), "Doctor: %s", appointment.getDoctorName()));
        holder.dateTimeTextView.setText(String.format(Locale.getDefault(), "Data & Ora: %s la %s", appointment.getFormattedDate(), appointment.getTime()));
        holder.currentStatusTextView.setText(String.format(Locale.getDefault(), "Status Curent: %s", appointment.getStatus()));
        holder.adminMessageEditText.setText(appointment.getAdminMessage()); // Setează mesajul existent al adminului

        // Configurează Spinner-ul de status
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, STATUS_OPTIONS);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusSpinner.setAdapter(statusAdapter);

        // Setează selecția curentă în Spinner, bazându-se pe statusul programării
        int currentStatusPosition = -1;
        for (int i = 0; i < STATUS_OPTIONS.length; i++) {
            if (STATUS_OPTIONS[i].equalsIgnoreCase(appointment.getStatus())) {
                currentStatusPosition = i;
                break;
            }
        }
        if (currentStatusPosition != -1) {
            holder.statusSpinner.setSelection(currentStatusPosition);
        }

        // Listener pentru butonul de salvare
        holder.saveButton.setOnClickListener(v -> {
            String selectedStatus = holder.statusSpinner.getSelectedItem().toString();
            String adminMessage = holder.adminMessageEditText.getText().toString().trim();

            // Verifică dacă contextul este o instanță de AdminActivity și apelează metoda publică
            if (context instanceof AdminActivity) {
                ((AdminActivity) context).updateAppointmentStatusInFirestore(
                        appointment.getAppointmentId(), selectedStatus, adminMessage);
            } else {
                Toast.makeText(context, "Eroare: Contextul nu este AdminActivity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userEmailTextView;
        TextView doctorTextView;
        TextView dateTimeTextView;
        TextView currentStatusTextView;
        Spinner statusSpinner;
        EditText adminMessageEditText;
        Button saveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmailTextView = itemView.findViewById(R.id.text_admin_appointment_user_email);
            doctorTextView = itemView.findViewById(R.id.text_admin_appointment_doctor);
            dateTimeTextView = itemView.findViewById(R.id.text_admin_appointment_date_time);
            currentStatusTextView = itemView.findViewById(R.id.text_admin_appointment_status_current);
            statusSpinner = itemView.findViewById(R.id.spinner_admin_appointment_status);
            adminMessageEditText = itemView.findViewById(R.id.edit_text_admin_message);
            saveButton = itemView.findViewById(R.id.button_admin_save_status);
        }
    }
}
