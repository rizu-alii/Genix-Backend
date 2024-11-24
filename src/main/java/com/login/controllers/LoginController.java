package com.login.controllers;

import com.login.dao.ActiveSessionRepository;
import com.login.dao.UsersRepo;
import com.login.entities.ActiveSessionEntity;
import com.login.entities.UsersEntity;
import com.login.entities.VpnDetectionResponseEntity;
import com.login.exceptions.UserAlreadyExistsException;
import com.login.security.IpInfoService;
import com.login.security.JWTService;
import com.login.security.MyUserDetailService;
import com.login.services.VPNDetectionService_1;
import com.login.services.VpnDetectionService;
import jakarta.servlet.http.HttpServletRequest;

//import jakarta.transaction.Transacxtional;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private IpInfoService ipInfoService;
    @Autowired
    private VpnDetectionService vpnDetectionService;
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private ActiveSessionRepository activeSessionRepository;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private VPNDetectionService_1 vpnDetectionService1;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @PostMapping("/register")
    public ResponseEntity<UsersEntity> register(@Valid @RequestBody UsersEntity users) throws RoleNotFoundException {
        if (usersRepo.findByUsername(users.getUsername()) == null) {
            users.setPassword(bCryptPasswordEncoder.encode(users.getPassword()));
            UsersEntity savedUser = usersRepo.saveAndFlush(users);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Move to this page", "http://localhost:8080/meditrack/login")
                    .body(savedUser);
        } else {
            throw new UserAlreadyExistsException("User with username " + users.getUsername() + " already exists.");
        }
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity <Map<String,Object>> login(@RequestBody UsersEntity users, HttpServletRequest request) {
        try {
            // Step 1: Check if the username exists
            Optional<UsersEntity> existingUser = Optional.ofNullable(usersRepo.findByUsername(users.getUsername()));
            if (existingUser.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "Not Found");
                response.put("message", "Username not found");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            UsersEntity user = existingUser.get();

            // Step 2: Check if the user is banned
            if (user.getIsBanned()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "FORBIDDEN");
                response.put("message", "Your Account is Banned");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Step 3: Validate the password
            if (!bCryptPasswordEncoder.matches(users.getPassword(), user.getPassword())) {
                int totalAttempts = 3;
                int count = user.getBan_count() + 1;
                user.setBan_count(count);
                int attemptsLeft = totalAttempts - count;

                if (attemptsLeft <= 0) {
                    user.setIsBanned(true);
                    usersRepo.save(user);
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "FORBIDDEN");
                    response.put("message", "Your account is banned due to multiple wrong attempts.");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }

                usersRepo.save(user);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "UNAUTHORIZED");
                response.put("message","Invalid password, you have " + attemptsLeft + " attempts left.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Step 4: Get VPN and Mobile detection info
            VpnDetectionResponseEntity vpnAndMobileInfo = vpnDetectionService.getVpnAndMobileStatus(ipInfoService.getPublicIpAddress());

            if (vpnAndMobileInfo == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "Internal_Server_Error");
                response.put("message", "Failed to fetch vpn or mobile detection");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            System.out.println("IP Address: " + ipInfoService.getPublicIpAddress());
            System.out.println("VPN Status: " + vpnAndMobileInfo.isVpn());
            System.out.println("Mobile Status: " + vpnAndMobileInfo.isMobile());

//             Check for VPN usage
            if (vpnAndMobileInfo.isVpn()) {
                user.setIsBanned(true);
                usersRepo.save(user);
               Map<String, Object> response = new HashMap<>();
                response.put("status", "UNAVAILABLE_FOR_LEGAL_REASONS");
                response.put("message", "Your account has been  banned due to vpn detection");
                return ResponseEntity.status(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS).body(response);
            }

            // Check for mobile device usage
            if (vpnAndMobileInfo.isMobile()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "FORBIDDEN");
                response.put("message", "Login through mobile device is not allowed");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
//            if(vpnDetectionService1.isVPNConnected()) {
//              user.setIsBanned(true);
//              usersRepo.save(user);
//              Map<String, Object> response = new HashMap<>();
//               response.put("status", "UNAVAILABLE_FOR_LEGAL_REASONS");
//               response.put("message", "Your account has been  banned due to vpn detection");
//               return ResponseEntity.status(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS).body(response);
//            }

            // Step 5: Generate JWT token after successful login
            String jwtToken = jwtService.generateToken(user.getUsername());

            // Clear any previous active sessions
            activeSessionRepository.deleteByUsername(user.getUsername());

            // Create a new active session
            ActiveSessionEntity newSession = new ActiveSessionEntity();
            newSession.setUsername(user.getUsername());
            newSession.setToken(jwtToken);
            newSession.setCreatedAt(LocalDateTime.now());
            activeSessionRepository.save(newSession);

            // Reset the failed login attempt count
            user.setBan_count(0);
            usersRepo.save(user);

            // Construct the JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("message", "Logged in successfully");
            response.put("jwtToken", "Bearer "+jwtToken);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "Internal_Server_error");
            response.put("message", "Something went wrong");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Optional<ActiveSessionEntity> activeSession = activeSessionRepository.findByToken(token);

        if (activeSession.isPresent()) {
            activeSessionRepository.deleteById(activeSession.get().getId());
            return ResponseEntity.ok("Logged out successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid session.");
    }

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/welcome2")
    public ResponseEntity<String> welcome2() {
        return ResponseEntity.ok("Hello2");
    }
}
