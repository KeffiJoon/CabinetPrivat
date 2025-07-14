package com.example.cabinetprivat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetprivat.R; // Asigură-te că importul e corect
import com.example.cabinetprivat.models.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;

    public AppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        TextView textAppointmentDate;
        TextView textAppointmentDoctor;
        TextView textAppointmentStatus;
        TextView textAppointmentAdminMessage;
        // Poți adăuga și alte TextView-uri dacă vrei să afișezi serviceType sau userEmail

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textAppointmentDate = itemView.findViewById(R.id.text_appointment_date);
            textAppointmentDoctor = itemView.findViewById(R.id.text_appointment_doctor);
            textAppointmentStatus = itemView.findViewById(R.id.text_appointment_status);
            textAppointmentAdminMessage = itemView.findViewById(R.id.text_appointment_admin_message);
        }

        public void bind(Appointment appointment) {
            // Combinăm data și ora pentru afișare
            String fullDate = appointment.getFormattedDate();
            String time = appointment.getTime();
            String dateTimeString = "Data: " + fullDate + " | Ora: " + (time != null ? time : "N/A");
            textAppointmentDate.setText(dateTimeString);

            // Afișează doctorul și tipul de serviciu (dacă vrei să le combini)
            String doctorService = "Doctor: " + (appointment.getDoctorName() != null ? appointment.getDoctorName() : "N/A");
            if (appointment.getServiceType() != null && !appointment.getServiceType().isEmpty()) {
                doctorService += " (" + appointment.getServiceType() + ")";
            }
            textAppointmentDoctor.setText(doctorService);

            // Afișează statusul
            textAppointmentStatus.setText("Status: " + (appointment.getStatus() != null ? appointment.getStatus() : "N/A"));

            // Afișează mesajul adminului doar dacă există
            if (appointment.getAdminMessage() != null && !appointment.getAdminMessage().isEmpty()) {
                textAppointmentAdminMessage.setText("Mesaj admin: " + appointment.getAdminMessage());
                textAppointmentAdminMessage.setVisibility(View.VISIBLE);
            } else {
                textAppointmentAdminMessage.setVisibility(View.GONE);
            }
        }
    }
}
