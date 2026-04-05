package com.application.utils;

import com.application.configuration.custom.CustomUserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String llavePrivada;

    @Value("${security.jwt.user.generator}")
    private String emisorToken;

    /**
     * Método para la creación de un token
     * @param authentication parámetro para la obtención de los usuarios y sus permisos
     * @return jwtToken (codificado)
     */
    public String createToken(Authentication authentication) {

        Algorithm algorithm = Algorithm.HMAC256(llavePrivada);

        CustomUserPrincipal usuario = (CustomUserPrincipal) authentication.getPrincipal(); // obtenemos al usuario de la sesión
        String correo =  usuario.getCorreo();

        String permisos = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(emisorToken) // usuario emisor que generara el token
                .withSubject(correo) // sujeto al que se le genera el token (destinatario)
                .withClaim("authorities", permisos) // generación del claim con los permisos del usuario
                .withIssuedAt(new Date(System.currentTimeMillis())) // fecha de creación del token
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // fecha de vigencia del token (hora de generación actual en milisegundos + los milisegundos para la expiración {1 día})
                .withNotBefore(new Date()) // especifica el momento en el que el token se considera válido (en este caso, desde su generación)
                .withJWTId(UUID.randomUUID().toString()) // generación id único del token
                .sign(algorithm);
    }

    /**
     * Método para decodificar y validar el token
     * @param token parámetro para la obtención del token de usuario
     * @return el token decodificado o una excepción si el token es inválido
     */
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(llavePrivada);

            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(emisorToken) // usuario que genero el token (emisor)
                    .build(); // reusable verifier instance

            return jwtVerifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token Invalido, NO Autorizado");
        }
    }

    /**
     * Método para obtener el usuario {subject} que viene del token
     * @param decodedJWT parámetro para la obtención del token decodificado (incluye al sujeto)
     * @return el correo del usuario que genero el token
     */
    public String getSubject(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    /**
     * Método para obtener un Claim por su nombre en el token
     * @param decodedJWT parámetro para la obtención del token decodificado (incluye el claim)
     * @param claimName parámetro con el nombre del Claim a obtener
     * @return el Claim solicitado
     */
    public Claim getClaimByName(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    /**
     * Método para obtener todos los Claims
     * @param decodedJWT parámetro para la obtención del token decodificado (incluye los claims)
     * @return un mapa con todos los Claims del token
     */
    public Map<String, Claim> getClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
}
