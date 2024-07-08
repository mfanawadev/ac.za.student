package ac.za.student.service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ac.za.student.dto.ReqRes;
import ac.za.student.entity.Students;
import ac.za.student.repository.StudentsRepo;

@Service
public class StudentManagementService {
    @Autowired
    private StudentsRepo studentsRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        try {
            Students students = new Students();
            students.setStudentNumber(generateUniqueStudentNumber());
            students.setFirstName(registrationRequest.getFirstName());
            students.setLastName(registrationRequest.getLastName());
            students.setEmail(registrationRequest.getEmail());
            students.setMobile(registrationRequest.getMobile());
            students.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            students.setRole("ROLE_USER");
            Students studentConfrimation = studentsRepo.save(students);
            if (studentConfrimation.getId() > 0) {
                resp.setStatusCode(200);
                resp.setStudents(studentConfrimation);
                resp.setMessage("Student registered successfully.");
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public ReqRes login(ReqRes loginRequest) {
        ReqRes resp = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getStudentNumber(),
                    loginRequest.getPassword()));
            Students student = studentsRepo.findByStudentNumber(loginRequest.getStudentNumber()).orElseThrow();
            String jwt = jwtUtils.generateToken(student);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), student);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setExpirationTime("24Hrs");
            resp.setMessage("Successful login");
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public ReqRes getMyInfo(String studentNumber) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Students> studentOptional = studentsRepo.findByStudentNumber(studentNumber);
            if (studentOptional.isPresent()) {
                resp.setStudents(studentOptional.get());
                resp.setStatusCode(200);
                resp.setMessage("successful");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("Student not found");
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error occurred while getting student info: " + e.getMessage());
        }
        return resp;

    }

    public ReqRes verifyPassword(ReqRes crendentialsRequest) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Students> studentOptional = studentsRepo
                    .findByStudentNumber(crendentialsRequest.getStudentNumber());
            if (studentOptional.isPresent()) {
                Students student = studentOptional.get();
                if (passwordEncoder.matches(crendentialsRequest.getPassword(), student.getPassword())) {
                    resp.setStatusCode(200);
                    resp.setMessage("Success");
                } else {
                    resp.setStatusCode(401);
                    resp.setMessage("Password not valid");
                }
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error occurred while getting student info: " + e.getMessage());
        }
        return resp;

    }

    public ReqRes changePassword(ReqRes crendentialsRequest) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Students> studentOptional = studentsRepo
                    .findByStudentNumber(crendentialsRequest.getStudentNumber());
            if (studentOptional.isPresent()) {
                Students student = studentOptional.get();
                student.setPassword(passwordEncoder.encode(crendentialsRequest.getPassword()));
                studentsRepo.save(student);
                resp.setStatusCode(200);
                resp.setMessage("Password updated successfully.");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error occurred while getting student info: " + e.getMessage());
        }
        return resp;

    }

    public ReqRes sendOTP(ReqRes crendentialsRequest) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Students> studentOptional = studentsRepo
                    .findByStudentNumber(crendentialsRequest.getStudentNumber());
            if (studentOptional.isPresent()) {
                Students student = studentOptional.get();
                String otp = generateOtp();
                student.setOtp(otp);
                studentsRepo.save(student);
                emailService.sendEmail(student.getEmail(), "Your OTP Code", "Your OTP code is: " + otp);
                resp.setStatusCode(200);
                resp.setMessage("OTP sent successfully.");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error occurred while sending OTP: " + e.getMessage());
        }
        return resp;

    }

    public ReqRes verifyOTP(ReqRes otpRequest) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Students> studentOptional = studentsRepo.findByStudentNumber(otpRequest.getStudentNumber());

            if (studentOptional.isPresent()) {
                Students student = studentOptional.get();
                System.out.println("Students: " + student);
                System.out.println("Request OTP: " + otpRequest.getOtp());
                System.out.println("Stored OTP: " + student.getOtp());
                if (student.getOtp().equals(otpRequest.getOtp())) {
                    resp.setStatusCode(200);
                    resp.setMessage("OTP verified successfully.");
                } else {
                    resp.setStatusCode(401);
                    resp.setMessage("OTP not valid");
                }
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error occurred while verifying OTP: " + e.getMessage());
        }
        return resp;
    }

    public String generateUniqueStudentNumber() {
        String studentNumber;
        do {
            studentNumber = generateRandom8DigitNumber();
        } while (studentsRepo.existsByStudentNumber(studentNumber));
        return studentNumber;
    }

    private String generateRandom8DigitNumber() {
        Random random = new Random();
        int number = 10000000 + random.nextInt(90000000);
        return String.valueOf(number);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}
