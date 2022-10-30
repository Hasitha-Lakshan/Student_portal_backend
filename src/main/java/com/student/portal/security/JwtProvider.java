package com.student.portal.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.annotation.PostConstruct;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.student.portal.exception.CommonException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtProvider {

	private KeyStore keyStore;

	@PostConstruct
	public void init() {

		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream resourceAsStream = getClass().getResourceAsStream("/studentPortal.jks");
			keyStore.load(resourceAsStream, "keystoretestkey".toCharArray());

		} catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {

			throw new CommonException("Exception occured while loading keystore");
		}

	}

	public String generateToken(Authentication authentication) {

		User princlipal = (User) authentication.getPrincipal();

		return Jwts.builder().setSubject(princlipal.getUsername()).claim("authorities", princlipal.getAuthorities())
				.signWith(getPrivateKey()).compact();
	}

	private PrivateKey getPrivateKey() {

		try {
			return (PrivateKey) keyStore.getKey("studentPortal", "keystoretestkey".toCharArray());

		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {

			throw new CommonException("Exception occured while retrieving private key from keystore");
		}
	}

	public boolean validateToken(String jwt) {

		Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);

		return true;
	}

	private PublicKey getPublicKey() {

		try {
			return keyStore.getCertificate("studentPortal").getPublicKey();

		} catch (KeyStoreException e) {

			throw new CommonException("Exception occured while retrieving public key from keystore");
		}
	}

	public String getUsernameFromJwt(String token) {

		Claims claims = Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

}
