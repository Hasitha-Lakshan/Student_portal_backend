package com.student.portal.service;

import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.student.portal.entity.Student;
import com.student.portal.model.StudentData;
import com.student.portal.model.StudentLoginRequest;
import com.student.portal.model.StudentLoginResponse;
import com.student.portal.model.StudentRegisterRequest;
import com.student.portal.model.StudentRegisterResponse;
import com.student.portal.repository.StudentRepository;
import com.student.portal.security.JwtProvider;

@Service
public class AuthService {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtProvider jwtProvider;
	private final Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).+$");

	public StudentRegisterResponse registerStudent(StudentRegisterRequest request) {
		StudentRegisterResponse studentRegisterResponse = new StudentRegisterResponse();

		if (request != null) {
			Student studentFromDB = studentRepository.findByUsername(request.getUsername());

			if (studentFromDB != null) {
				studentRegisterResponse.setError("Given username already exist");
				studentRegisterResponse.setStatus(false);
				return studentRegisterResponse;

			} else {
				if (isValidPassword(request.getPassword())) {
					Student newStudent = new Student();
					String uniqueId = UUID.randomUUID().toString().replace("-", "");

					while (studentRepository.existsById(uniqueId)) {
						uniqueId = UUID.randomUUID().toString().replace("-", "");
					}

					newStudent.setStudentId(uniqueId);
					newStudent.setFirstName(request.getFirstName());
					newStudent.setLastName(request.getLastName());
					newStudent.setUsername(request.getUsername());
					newStudent.setPassword(encodePassword(request.getPassword()));
					studentRepository.save(newStudent);

					studentRegisterResponse.setError("Registration Completed");
					studentRegisterResponse.setStatus(true);
					return studentRegisterResponse;

				} else {
					studentRegisterResponse.setError("Invalid password");
					studentRegisterResponse.setStatus(false);
					return studentRegisterResponse;
				}
			}
		} else {
			studentRegisterResponse.setError("Invalid request");
			studentRegisterResponse.setStatus(false);
			return studentRegisterResponse;
		}
	}

	private boolean isValidPassword(String password) {
		return this.passwordPattern.matcher(password).matches() && password.length() == 8;
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public StudentLoginResponse loginStudent(StudentLoginRequest request) {
		if (request != null) {
			Student studentFromDB = studentRepository.findByUsername(request.getUsername());

			if (studentFromDB != null) {
				StudentLoginResponse response = new StudentLoginResponse();

				Authentication authenticate = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
				SecurityContextHolder.getContext().setAuthentication(authenticate);

				response.setAuthenticationtoken(jwtProvider.generateToken(authenticate));
				response.setStudentData(new StudentData(studentFromDB.getFirstName(), studentFromDB.getLastName(),
						studentFromDB.getUsername()));
				response.setStatus(true);

				return response;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
