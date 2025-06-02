package com.example.PAMS.service;

import com.example.PAMS.entities.Appointment;
import com.example.PAMS.entities.Doctor;
import com.example.PAMS.entities.Patient;
import com.example.PAMS.repository.AppointmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRegistrationService doctorService;

    public boolean isSlotAvailable(Appointment appointment) {
        // Check if doctor has availability at this time
        Doctor doctor = appointment.getDoctor();
        String dayOfWeek = appointment.getAppointmentDate().getDayOfWeek().toString();

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> availability =
                    mapper.readValue(doctor.getAvailability(), Map.class);

            Map<String, String> dayAvailability = availability.get(dayOfWeek);
            if (dayAvailability == null) {
                return false; // Doctor not available on this day
            }

            LocalTime startTime = LocalTime.parse(dayAvailability.get("start"));
            LocalTime endTime = LocalTime.parse(dayAvailability.get("end"));
            LocalTime requestedTime = appointment.getTimeSlot();

            if (requestedTime.isBefore(startTime) || requestedTime.isAfter(endTime)) {
                return false;
            }

            // Check if slot is already booked
            List<Appointment> existingAppointments = appointmentRepository
                    .findByDoctorAndAppointmentDateAndTimeSlot(
                            doctor,
                            appointment.getAppointmentDate(),
                            appointment.getTimeSlot()
                    );

            return existingAppointments.isEmpty();

        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public void bookAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(Patient patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDescTimeSlotDesc(patient);
    }

    public void cancelAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(Appointment.AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
    }
}