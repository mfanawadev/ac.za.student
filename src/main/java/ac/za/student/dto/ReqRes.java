package ac.za.student.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import ac.za.student.entity.Students;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {
	private int statusCode;
	private String error;
	private String message;
	private String token;
	private String refreshToken;
	private String expirationTime;
	private String studentNumber;
	private String firstName;
	private String lastName;
	private String email;
	private String mobile;
	private String password;
	private String role;
	private String otp;
	private Students students;
}
