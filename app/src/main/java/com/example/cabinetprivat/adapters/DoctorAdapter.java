package com.example.cabinetprivat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cabinetprivat.R;
import com.example.cabinetprivat.models.Doctor;

import java.util.List;
import java.util.Locale;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctorsList;

    public DoctorAdapter(List<Doctor> doctorsList) {
        this.doctorsList = doctorsList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorsList.get(position);

        holder.nameTextView.setText(doctor.getName());
        holder.specialtyTextView.setText(doctor.getSpecialty());

        if (doctor.getRating() != null) {
            holder.ratingTextView.setText(String.format(Locale.getDefault(), "%.1f", doctor.getRating()));
        } else {
            holder.ratingTextView.setText("N/A");
        }

        if (doctor.getExperience() != null && !doctor.getExperience().isEmpty()) {
            holder.experienceTextView.setText(doctor.getExperience());
        } else {
            holder.experienceTextView.setText("N/A");
        }

        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(doctor.getImageUrl())
                    .placeholder(R.drawable.placeholder_doctor)
                    .error(R.drawable.profilut2)
                    .into(holder.doctorImageView);
        } else {
            holder.doctorImageView.setImageResource(R.drawable.placeholder_doctor);
        }
    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorImageView;
        TextView nameTextView;
        TextView specialtyTextView;
        TextView experienceTextView;
        TextView ratingTextView;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorImageView = itemView.findViewById(R.id.imageDoctor);
            nameTextView = itemView.findViewById(R.id.textName);
            specialtyTextView = itemView.findViewById(R.id.textSpecialty);
            experienceTextView = itemView.findViewById(R.id.textExperience);
            ratingTextView = itemView.findViewById(R.id.textRating);
        }
    }
}