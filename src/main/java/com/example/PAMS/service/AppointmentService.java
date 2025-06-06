package com.example.PAMS.service;

import com.example.PAMS.entities.Appointment;
import com.example.PAMS.entities.Doctor;
import com.example.PAMS.entities.Patient;
import com.example.PAMS.repository.AppointmentRepository;
import com.example.PAMS.repository.DoctorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Transactional
    public boolean isSlotAvailable(Appointment appointment) {
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DayOfWeek dayOfWeek = appointment.getAppointmentDate().getDayOfWeek();
        String day = dayOfWeek.toString().toUpperCase(); // Ensure uppercase match

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> availability =
                    mapper.readValue(doctor.getAvailability(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Map<String, String>>>() {});

            // Check if doctor is available on this day
            if (!availability.containsKey(day)) {
                throw new RuntimeException("Doctor is not available on " + dayOfWeek);
            }

            Map<String, String> dayAvailability = availability.get(day);
            LocalTime startTime = LocalTime.parse(dayAvailability.get("start"));
            LocalTime endTime = LocalTime.parse(dayAvailability.get("end"));
            LocalTime requestedTime = appointment.getTimeSlot();

            // Check if requested time is within working hours (with 30 minute buffer)
            if (requestedTime.isBefore(startTime) || requestedTime.isAfter(endTime.minusMinutes(30))) {
                throw new RuntimeException("Please select a time between " + startTime + " and " + endTime.minusMinutes(30));
            }

            // Check for existing appointments at this time
            List<Appointment> existingAppointments = appointmentRepository
                    .findByDoctorAndAppointmentDateAndTimeSlot(
                            doctor,
                            appointment.getAppointmentDate(),
                            appointment.getTimeSlot()
                    );

            if (!existingAppointments.isEmpty()) {
                throw new RuntimeException("This time slot is already booked");
            }

            return true;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error checking doctor availability");
        }
    }

    @Transactional
    public void bookAppointment(Appointment appointment) {
        // Verify doctor exists
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check if slot is still available
        List<Appointment> existing = appointmentRepository.findByDoctorAndAppointmentDateAndTimeSlot(
                doctor,
                appointment.getAppointmentDate(),
                appointment.getTimeSlot());

        if (!existing.isEmpty()) {
            throw new RuntimeException("This time slot was just booked by another patient");
        }

        appointment.setStatus(Appointment.AppointmentStatus.BOOKED);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(Patient patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDescTimeSlotDesc(patient);
    }

    @Transactional
    public void cancelAppointment(Integer appointmentId, String patientEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getEmail().equals(patientEmail)) {
            throw new RuntimeException("You can only cancel your own appointments");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.BOOKED) {
            throw new RuntimeException("Only booked appointments can be canceled");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel past appointments");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
    }
}