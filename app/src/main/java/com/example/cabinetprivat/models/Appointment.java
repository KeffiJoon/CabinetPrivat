package com.example.cabinetprivat.models;

import com.google.firebase.Timestamp; // Adaugă acest import!

public class Appointment {
    private String appointmentId;
    private String userId;
    private String userEmail;
    private String serviceType;
    private String doctorName;
    private String formattedDate;
    private String time;
    private String status;
    private String adminMessage;
    private Long dateInMillis;    // Acest câmp poate rămâne Long (sau int, în funcție de cum îl generezi)
    private Timestamp timestamp;  // SCHIMBĂ ACEST TIP DE LA Long LA Timestamp!

    public Appointment() {
        // Public no-argument constructor needed for Firebase Firestore's toObject()
    }

    // Constructor cu toate câmpurile (actualizează tipul pentru 'timestamp')
    public Appointment(String userId, String userEmail, String serviceType, String doctorName,
                       String formattedDate, String time, String status, String adminMessage,
                       Long dateInMillis, Timestamp timestamp) { // Tipul este acum Timestamp
        this.userId = userId;
        this.userEmail = userEmail;
        this.serviceType = serviceType;
        this.doctorName = doctorName;
        this.formattedDate = formattedDate;
        this.time = time;
        this.status = status;
        this.adminMessage = adminMessage;
        this.dateInMillis = dateInMillis;
        this.timestamp = timestamp;
    }

    // Getteri (actualizează tipul returnat pentru 'timestamp')
    public String getAppointmentId() { return appointmentId; }
    public String getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getServiceType() { return serviceType; }
    public String getDoctorName() { return doctorName; }
    public String getFormattedDate() { return formattedDate; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getAdminMessage() { return adminMessage; }
    public Long getDateInMillis() { return dateInMillis; }
    public Timestamp getTimestamp() { return timestamp; } // SCHIMBĂ ACEST TIPUL RETURNAT!

    // Setteri (actualizează tipul parametrului pentru 'timestamp')
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setFormattedDate(String formattedDate) { this.formattedDate = formattedDate; }
    public void setTime(String time) { this.time = time; }
    public void setStatus(String status) { this.status = status; }
    public void setAdminMessage(String adminMessage) { this.adminMessage = adminMessage; }
    public void setDateInMillis(Long dateInMillis) { this.dateInMillis = dateInMillis; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; } // SCHIMBĂ ACEST TIPUL PARAMETRULUI!
}
