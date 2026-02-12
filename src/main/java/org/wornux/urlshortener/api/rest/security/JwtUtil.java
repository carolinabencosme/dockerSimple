package org.wornux.urlshortener.api.rest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.wornux.urlshortener.dto.Authentication;
import org.wornux.urlshortener.enums.SecurityConstants;
import org.wornux.urlshortener.exception.AuthenticationCredentialsNotFoundException;

public class JwtUtil {

  public String generateToken(Map<String, String> claims, Authentication authentication) {
    String username = authentication.username();
    Date currentDate = new Date(System.currentTimeMillis());
    long expirationTime = (long) SecurityConstants.JWT_EXPIRATION_TIME.getValue();
    Date expirationDate = new Date(currentDate.getTime() + expirationTime);

    return Jwts.builder()
        .subject(username)
        .claims(claims)
        .issuedAt(currentDate)
        .expiration(expirationDate)
        .signWith(getSigningKey())
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      SecretKey key = getSigningKey();
      Jwts.parser()
          .verifyWith(key) // ✅ Verifica firma
          .build()
          .parseSignedClaims(token); // ✅ Asegura que es JWS (token firmado)
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      throw new AuthenticationCredentialsNotFoundException("JWT fue modificado o es inválido.");
    } catch (ExpiredJwtException e) {
      throw new AuthenticationCredentialsNotFoundException("JWT expirado.");
    } catch (UnsupportedJwtException e) {
      throw new AuthenticationCredentialsNotFoundException("JWT no soportado.");
    } catch (IllegalArgumentException e) {
      throw new AuthenticationCredentialsNotFoundException("JWT inválido.");
    }
  }

  public String getUsernameFromToken(String token) {
    return extractAllClaims(token).getSubject();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSigningKey() {
    String SECRET_KEY = (String) SecurityConstants.JWT_SECRET.getValue();
    byte[] keyBytes = SECRET_KEY.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
