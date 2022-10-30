package com.student.portal.model;

public class StudentLoginResponse {
	private String authenticationtoken;
	private StudentData studentData;
	private Boolean status;

	public String getAuthenticationtoken() {
		return authenticationtoken;
	}

	public void setAuthenticationtoken(String authenticationtoken) {
		this.authenticationtoken = authenticationtoken;
	}

	public StudentData getStudentData() {
		return studentData;
	}

	public void setStudentData(StudentData studentData) {
		this.studentData = studentData;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

}
