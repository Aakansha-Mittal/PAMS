package com.example.PAMS.service;

import com.example.PAMS.entities.Appointment;
import com.example.PAMS.entities.Doctor;
import com.example.PAMS.entities.Patient;
import com.example.PAMS.repository.AppointmentRepository;
import com.example.PAMS.repository.DoctorRepository;
import com.example.PAMS.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;


    public void sendAppointmentConfirmationEmail(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        Patient patient = patientRepository.findById(appointment.getPatient().getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor not found"));

        String subject = "Appointment Confirmation";
        String text = String.format("Dear %s,\n\nYour appointment with Dr. %s is confirmed for %s at %s.\n\nThank you,\nHealthcare Team",
                patient.getName(), doctor.getName(), appointment.getAppointmentDate(), appointment.getTimeSlot());

        sendEmail(patient.getEmail(), subject, text);
    }

    public void sendEmailTest(){
        try{
            sendEmail("killerwoor2003@gmail.com", "subject", "text");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendAppointmentReminderEmails() {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentRepository.findByAppointmentDate(today);

        for (Appointment appointment : appointments) {
            Patient patient = patientRepository.findById(appointment.getPatient().getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor not found"));

            String subject = "Appointment Reminder";
            String text = String.format("Dear %s,\n\nThis is a reminder for your appointment with Dr. %s today at %s.\n\nThank you,\nHealthcare Team",
                    patient.getName(), doctor.getName(), appointment.getTimeSlot());

            sendEmail(patient.getEmail(), subject, text);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
