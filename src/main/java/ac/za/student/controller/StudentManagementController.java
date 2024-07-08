package ac.za.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ac.za.student.dto.ReqRes;
import ac.za.student.service.StudentManagementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class StudentManagementController {
    @Autowired
    private StudentManagementService studentManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes req) {
        return ResponseEntity.ok(studentManagementService.register(req));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(studentManagementService.login(req));
    }

    @GetMapping("/student/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentNumber = authentication.getName();
        ReqRes response = studentManagementService.getMyInfo(studentNumber);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/student/verify-password")
    public ResponseEntity<ReqRes> verifyPassword(@RequestBody ReqRes req){
        return ResponseEntity.ok(studentManagementService.verifyPassword(req));
    }

    @PostMapping("/student/change-password")
    public ResponseEntity<ReqRes> changePassword(@RequestBody ReqRes req){
        return ResponseEntity.ok(studentManagementService.changePassword(req));
    }

    @PostMapping("/student/send-otp")
    public ResponseEntity<ReqRes> sendOTP(@RequestBody ReqRes req){
        return ResponseEntity.ok(studentManagementService.sendOTP(req));
    }

    @PostMapping("/student/verify-otp")
    public ResponseEntity<ReqRes> verifyOTP(@RequestBody ReqRes req){
        return ResponseEntity.ok(studentManagementService.verifyOTP(req));
    }
}
