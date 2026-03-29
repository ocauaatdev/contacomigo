package com.ocauaatdev.contacomigo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ocauaatdev.contacomigo.entity.User;
import com.ocauaatdev.contacomigo.exception.TokenGenerationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("contacomigo") //Emissor do token
                    .withSubject(user.getEmail()) //usuario que recebe o token
                    .withExpiresAt(generationExpirationDate())
                    .sign(algorithm); //assinatura e geracao final

            return token;
        } catch (JWTCreationException e) {
            throw new TokenGenerationException("Error while generating token");
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            //Descriptografando o token e pegando o "Subject"
            return JWT.require(algorithm)
                    .withIssuer("contacomigo")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            return "";
        }
    }

    private Instant generationExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
