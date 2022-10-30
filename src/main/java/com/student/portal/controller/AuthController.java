package com.student.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.student.portal.model.StudentLoginRequest;
import com.student.portal.model.StudentLoginResponse;
import com.student.portal.model.StudentRegisterRequest;
import com.student.portal.model.StudentRegisterResponse;
import com.student.portal.service.AuthService;

@RestController
@RequestMapping("/api/authservice")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping("/studentregister")
	public StudentRegisterResponse signup(@RequestBody StudentRegisterRequest request) {

		return authService.registerStudent(request);
	}

	@PostMapping("/studentlogin")
	public StudentLoginResponse login(@RequestBody StudentLoginRequest request) {

		return authService.loginStudent(request);
	}
}
