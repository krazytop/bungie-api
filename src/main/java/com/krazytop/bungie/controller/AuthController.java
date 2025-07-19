package com.krazytop.bungie.controller;

import com.krazytop.bungie.api.generated.AuthentificationApi;
import com.krazytop.bungie.model.generated.AuthTokensDTO;
import com.krazytop.bungie.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController implements AuthentificationApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @Override
    public ResponseEntity<AuthTokensDTO> getPlayerTokens(String code) {
        LOGGER.info("Retrieving player tokens with code");
        AuthTokensDTO playerTokens = authService.getPlayerTokens(code);
        LOGGER.info("Player tokens retrieved");
        return new ResponseEntity<>(playerTokens, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthTokensDTO> updatePlayerTokens(AuthTokensDTO tokens) {
        LOGGER.info("Updating player tokens with refresh token");
        AuthTokensDTO newTokens = authService.updatePlayerTokens(tokens.getRefreshToken());
        LOGGER.info("Player tokens refreshed");
        return new ResponseEntity<>(newTokens, HttpStatus.OK);
    }

}