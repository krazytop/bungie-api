package com.krazytop.bungie.controller;

import com.krazytop.bungie.model.generated.AuthTokensDTO;
import com.krazytop.bungie.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void getPlayerTokens_shouldReturnTokensAndOkStatus() {
        // Given
        String testCode = "playerCode";
        AuthTokensDTO expectedTokens = new AuthTokensDTO().setAccessToken("accessToken").setRefreshToken("refreshToken");
        when(authService.getPlayerTokens(testCode)).thenReturn(expectedTokens);

        // When
        ResponseEntity<AuthTokensDTO> response = authController.getPlayerTokens(testCode);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTokens, response.getBody());
        verify(authService).getPlayerTokens(testCode);
    }

    @Test
    void updatePlayerTokens_shouldReturnNewTokensAndOkStatus() {
        // Given
        AuthTokensDTO inputTokens = new AuthTokensDTO().setAccessToken("oldAccessToken").setRefreshToken("newRefreshToken");
        AuthTokensDTO newTokens = new AuthTokensDTO().setAccessToken("newAccessToken").setRefreshToken("newRefreshToken");
        when(authService.updatePlayerTokens(inputTokens.getRefreshToken())).thenReturn(newTokens);

        // When
        ResponseEntity<AuthTokensDTO> response = authController.updatePlayerTokens(inputTokens);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newTokens, response.getBody());
        verify(authService).updatePlayerTokens(inputTokens.getRefreshToken());
    }
}