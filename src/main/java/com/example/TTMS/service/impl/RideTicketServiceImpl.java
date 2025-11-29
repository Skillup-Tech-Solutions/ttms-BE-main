package com.example.TTMS.service.impl;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.TTMS.config.JwtHelper;
import com.example.TTMS.dto.RideTicketDto;
import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;
import com.example.TTMS.entity.LocationCostDetails;
import com.example.TTMS.entity.RideTicket;
import com.example.TTMS.entity.Status;
import com.example.TTMS.entity.Transport;
import com.example.TTMS.entity.TransportStatus;
import com.example.TTMS.entity.User;
import com.example.TTMS.repository.CityRepo;
import com.example.TTMS.repository.LocationCostRepo;
import com.example.TTMS.repository.LocationRepo;
import com.example.TTMS.repository.RideTicketRepo;
import com.example.TTMS.repository.TransportRepo;
import com.example.TTMS.repository.UserRepo;
import com.example.TTMS.service.MailService;
import com.example.TTMS.service.MailTemplateService;
import com.example.TTMS.service.RideTicketService;

import jakarta.mail.MessagingException;

@Service
public class RideTicketServiceImpl implements RideTicketService {

    private final RideTicketRepo rideTicketRepo;
    private final TransportRepo transportRepo;
    private final CityRepo cityRepo;
    private final LocationRepo locationRepo;
    private final LocationCostRepo locationCostRepo;
    private final MongoTemplate mongoTemplate;
    private final JwtHelper jwtHelper;
    private final MailTemplateService mailTemplateService;
    private final MailService mailService;
    private final UserRepo userRepo;

    public RideTicketServiceImpl(RideTicketRepo rideTicketRepo, TransportRepo transportRepo, CityRepo cityRepo,
            LocationRepo locationRepo, LocationCostRepo locationCostRepo, MongoTemplate mongoTemplate,
            JwtHelper jwtHelper, MailTemplateService mailTemplateService, MailService mailService, UserRepo userRepo) {
        this.rideTicketRepo = rideTicketRepo;
        this.transportRepo = transportRepo;
        this.cityRepo = cityRepo;
        this.locationRepo = locationRepo;
        this.locationCostRepo = locationCostRepo;
        this.mongoTemplate = mongoTemplate;
        this.jwtHelper = jwtHelper;
        this.mailTemplateService = mailTemplateService;
        this.mailService = mailService;
        this.userRepo = userRepo;
    }

    @Override
    public RideTicket createRideTicket(RideTicketDto rideTicketDto) {

        Map<String, Object> userDetails = jwtHelper.getUserDetails();
        String id = (String) userDetails.get("_id");
        String userId = (String) userDetails.get("userId");
        String username = (String) userDetails.get("username");
        String role = (String) userDetails.get("role");
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LocationCostDetails cost = locationCostRepo.findByCityAndPickUpAndDropLocation(rideTicketDto.getCity(),
                rideTicketDto.getPickupLocation(),
                rideTicketDto.getDropLocation(), mongoTemplate);
        if (cost == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pick up and Drop location not found");
        }
        Location pickupLocation = locationRepo.findById(rideTicketDto.getPickupLocation())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pickup location not found"));
        Location dropLocation = locationRepo.findById(rideTicketDto.getDropLocation())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drop location not found"));
        Transport transport = transportRepo.findById(rideTicketDto.getTransport())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transport not found"));
        if (!transport.getStatus().equals(TransportStatus.AVAILABLE.getLabel())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transport not available");
        }
        City city = cityRepo.findById(rideTicketDto.getCity())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "city not found"));

        RideTicket rideTicket = new RideTicket();
        rideTicket.setUserId(userId);
        rideTicket.setUserName(username);
        rideTicket.setEmail(user.getEmail());
        rideTicket.setMobileNo(user.getMobileNo());
        // rideTicket.setLocationCost(cost);
        rideTicket.setTransport(transport);
        rideTicket.setCity(city);
        rideTicket.setPickupLocation(pickupLocation);
        rideTicket.setDropLocation(dropLocation);
        rideTicket.setCost(cost.getCost());
        rideTicket.setStatus(Status.PENDING.getLabel());
        rideTicket.setCreatedBy(id);
        rideTicket.setCreatedAt(LocalDateTime.now());
        rideTicket.setUpdatedBy(null);
        rideTicket.setUpdatedAt(null);
        rideTicket.setCreatedRole(role);
        transportRepo.updateTransportStatus(rideTicketDto.getTransport(), TransportStatus.ASSIGNED.getLabel(),
                mongoTemplate);

        return rideTicketRepo.save(rideTicket);

    }

    @Override
    public List<RideTicket> getMyTickets(String search, Authentication authentication) {

        Map<String, Object> userDetails = jwtHelper.getUserDetails();
        String id = (String) userDetails.get("_id");
        String userId = (String) userDetails.get("userId");

        String role = (String) userDetails.get("role");

        Query query = new Query();

        switch (role.toUpperCase()) {
            case "VENDOR":
                query.addCriteria(Criteria.where("transport.vendorId").is(id));
                break;
            case "TRANSPORT":
                query.addCriteria(Criteria.where("transport.id").is(id));
                break;
            case "USER":
                query.addCriteria(Criteria.where("userId").is(userId));
                break;
            case "SUPERADMIN":
                break;
            default:
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role");
        }

        if (search != null && !search.isBlank()) {

            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("status").regex(search, "i"),
                    Criteria.where("vendor.vendorName").regex(search, "i"));
            query.addCriteria(searchCriteria);
        }

        return mongoTemplate.find(query, RideTicket.class);
    }

    @Override
    public void createRide(User user, LocalDate pickupDate) {

        Map<String, Object> userDetails = jwtHelper.getUserDetails();
        String id = (String) userDetails.get("_id");
        String role = (String) userDetails.get("role");

        RideTicket rideTicket = new RideTicket();
        rideTicket.setUserId(user.getUserId());
        rideTicket.setUserName(user.getUsername());
        rideTicket.setEmail(user.getEmail());
        rideTicket.setMobileNo(user.getMobileNo());
        rideTicket.setTransport(user.getTransport());
        rideTicket.setCity(user.getCity());
        rideTicket.setPickupLocation(user.getPickupLocation());
        rideTicket.setPickupDate(pickupDate);
        rideTicket.setStatus(Status.PENDING.getLabel());
        rideTicket.setCreatedBy(id);
        rideTicket.setCreatedAt(LocalDateTime.now());
        rideTicket.setUpdatedBy(null);
        rideTicket.setUpdatedAt(null);
        rideTicket.setCreatedRole(role);
        rideTicketRepo.save(rideTicket);

    }

    @Override
    public void sendOtp(String id) {

        RideTicket rideTicket = rideTicketRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride ticket not found"));
        User user = userRepo.findByUserId(rideTicket.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String otp = "1234";
        String content = mailTemplateService.sendOtpMail(otp, user.getUsername());

        rideTicket.setOtp(otp);
        rideTicket.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        rideTicket.setOtpSent(true);
        rideTicket.setUpdatedAt(LocalDateTime.now());
        rideTicketRepo.save(rideTicket);

        try {
            mailService.sendMail(user.getEmail(), "Your Ride OTP", content);
        } catch (MessagingException e) {
            // Email failed but OTP is still saved
        }

        // if (rideTicket.isOtpSent() && rideTicket.getOtpExpiryTime() != null &&
        // rideTicket.getOtpExpiryTime().isAfter(LocalDateTime.now())) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "OTP already sent. Please wait before requesting a new one.");
        // }

        // SecureRandom random = new SecureRandom();
        // String otp = String.valueOf(1000 + random.nextInt(9000));
        // String content = mailTemplateService.sendOtpMail(otp, user.getUsername());
        // try {
        // mailService.sendMail(user.getEmail(), "Your Ride OTP", content);

        // rideTicket.setOtp(otp);
        // rideTicket.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        // rideTicket.setOtpSent(true);
        // rideTicket.setUpdatedAt(LocalDateTime.now());
        // rideTicketRepo.save(rideTicket);

        // } catch (MessagingException e) {
        // e.printStackTrace();
        // throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
        // "Failed to send OTP email. Please try again.");
        // }
    }

    @Override
    public void verifyOtp(String ticketId, String enteredOtp, String dropLocation) {

        RideTicket rideTicket = rideTicketRepo.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride ticket not found"));

        // if (!rideTicket.isOtpSent() || rideTicket.getOtp() == null ||
        // rideTicket.getOtpExpiryTime() == null) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not generated
        // for this ticket");
        // }

        // if (rideTicket.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired");
        // }

        // if (!rideTicket.getOtp().equals(enteredOtp)) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        // }
        if (!"1234".equals(enteredOtp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        if (dropLocation != null && !dropLocation.isBlank()) {
            Location dropLoc = locationRepo.findById(dropLocation)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drop location not found"));
            rideTicket.setDropLocation(dropLoc);

            LocationCostDetails cost = locationCostRepo.findByCityAndPickUpAndDropLocation(
                    rideTicket.getCity().getId(),
                    rideTicket.getPickupLocation().getId(),
                    dropLocation,
                    mongoTemplate);

            if (cost == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pick up and Drop location not found");
            }
            rideTicket.setCost(cost.getCost());
        }

        rideTicket.setStatus(Status.RIDE_STARTED.getLabel());
        rideTicket.setRideStartTime(LocalDateTime.now());
        rideTicket.setOtpSent(false);
        rideTicket.setUpdatedAt(LocalDateTime.now());
        rideTicketRepo.save(rideTicket);
        transportRepo.updateTransportStatus(rideTicket.getTransport().getId(), TransportStatus.ON_TRIP.getLabel(),
                mongoTemplate);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void assignTransportForToday() {
        LocalDate today = LocalDate.now();

        List<RideTicket> todayRides = rideTicketRepo.findByPickupDate(today);

        for (RideTicket ride : todayRides) {
            Transport transport = ride.getTransport();
            if (transport != null && !(transport.getStatus().equals(TransportStatus.ON_TRIP.getLabel())
                    || transport.getStatus().equals(TransportStatus.ASSIGNED.getLabel()))) {
                transport.setStatus(TransportStatus.ASSIGNED.getLabel());
                transportRepo.save(transport);
            }
        }
    }

    @Override
    public RideTicket updateRemarks(String id, String remarks, String status) {

        RideTicket rideTicket = rideTicketRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride ticket not found"));

        if (remarks != null) {
            rideTicket.setRemarks(remarks);
        }

        rideTicket.setStatus(status);
        rideTicket.setRideEndTime(LocalDateTime.now());
        rideTicket.setUpdatedAt(LocalDateTime.now());
        transportRepo.updateTransportStatus(rideTicket.getTransport().getId(), TransportStatus.AVAILABLE.getLabel(),
                mongoTemplate);
        return rideTicketRepo.save(rideTicket);
    }

}
