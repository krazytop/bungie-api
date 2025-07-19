package com.krazytop.bungie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krazytop.bungie.entity.AuthTokens;
import com.krazytop.bungie.exception.CustomException;
import com.krazytop.bungie.mapper.AuthTokensMapper;
import com.krazytop.bungie.model.generated.AuthTokensDTO;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64; // Nécessaire si votre service utilise Base64

import static com.krazytop.bungie.exception.ApiErrorEnum.BUNGIE_AUTH_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mapstruct.factory.Mappers;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String TEST_PLAYER_CODE = "player";
    private static final String TEST_REFRESH_TOKEN = "refreshToken";
    private static final String AUTH_TOKEN_JSON = "{\"access_token\":\"access123\",\"refresh_token\":\"refresh456\",\"expires_in\":3600,\"refresh_expires_in\":7200}";
    private static final OffsetDateTime FIXED_TEST_TIME = OffsetDateTime.of(2025, 7, 13, 10, 0, 0, 0, ZoneOffset.UTC);
    private final AuthTokensDTO expectedAuthTokensDTO = new AuthTokensDTO("access123", FIXED_TEST_TIME.plusSeconds(3600), "refresh456", FIXED_TEST_TIME.plusSeconds(7200));

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "clientId", CLIENT_ID);
        ReflectionTestUtils.setField(authService, "clientSecret", CLIENT_SECRET);
        ReflectionTestUtils.setField(authService, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(authService, "authTokensMapper", Mappers.getMapper(AuthTokensMapper.class));
    }

    @Test
    void getPlayerTokens_OK() throws IOException {
        // Given
        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class);
             MockedStatic<OffsetDateTime> mockedOffsetDateTime = mockStatic(OffsetDateTime.class)) {

            mockedOffsetDateTime.when(() -> OffsetDateTime.now(any(ZoneOffset.class))).thenReturn(FIXED_TEST_TIME);
            mockedOffsetDateTime.when(() -> OffsetDateTime.now(any(ZoneId.class))).thenReturn(FIXED_TEST_TIME);

            CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
            CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
            StatusLine mockStatusLine = mock(StatusLine.class);
            HttpEntity mockHttpEntity = mock(HttpEntity.class);

            mockedHttpClients.when(HttpClients::createDefault).thenReturn(mockHttpClient);
            when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
            when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
            when(mockStatusLine.getStatusCode()).thenReturn(200); // Statut OK
            when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
            mockedEntityUtils.when(() -> EntityUtils.toString(mockHttpEntity)).thenReturn(AUTH_TOKEN_JSON);

            // When
            AuthTokensDTO result = authService.getPlayerTokens(TEST_PLAYER_CODE);

            // Then
            assertEquals(expectedAuthTokensDTO, result);

            verify(mockHttpClient).execute(any(HttpPost.class));
            verify(mockHttpResponse).close();
            verify(mockHttpClient).close();
        }
    }

    @Test
    void updatePlayerTokens_OK() throws IOException {
        // Given
        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class);
             MockedStatic<OffsetDateTime> mockedOffsetDateTime = mockStatic(OffsetDateTime.class)) {

            mockedOffsetDateTime.when(() -> OffsetDateTime.now(any(ZoneOffset.class))).thenReturn(FIXED_TEST_TIME);
            mockedOffsetDateTime.when(() -> OffsetDateTime.now(any(ZoneId.class))).thenReturn(FIXED_TEST_TIME);

            CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
            CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
            StatusLine mockStatusLine = mock(StatusLine.class);
            HttpEntity mockHttpEntity = mock(HttpEntity.class);

            mockedHttpClients.when(HttpClients::createDefault).thenReturn(mockHttpClient);
            when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
            when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
            when(mockStatusLine.getStatusCode()).thenReturn(200);
            when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
            mockedEntityUtils.when(() -> EntityUtils.toString(mockHttpEntity)).thenReturn(AUTH_TOKEN_JSON);

            // When
            AuthTokensDTO result = authService.updatePlayerTokens(TEST_REFRESH_TOKEN);

            // Then
            assertEquals(expectedAuthTokensDTO, result);

            verify(mockHttpClient).execute(any(HttpPost.class));
            verify(mockHttpResponse).close();
            verify(mockHttpClient).close();
        }
    }

    @Test
    void getPlayerTokens_KO_IOException() throws IOException {
        // Given
        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class)) {
            CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(mockHttpClient);
            when(mockHttpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Network error"));

            // When / Then
            CustomException thrown = assertThrows(CustomException.class, () -> authService.getPlayerTokens(TEST_PLAYER_CODE));
            assertEquals(BUNGIE_AUTH_ERROR, thrown.getErrorEnum());

            verify(mockHttpClient).execute(any(HttpPost.class));
            verify(mockHttpClient).close();
        }
    }

    @Test
    void getPlayerTokens_KO_NOT200() throws IOException {
        // Given
        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class)) {
            CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
            CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
            StatusLine mockStatusLine = mock(StatusLine.class);

            mockedHttpClients.when(HttpClients::createDefault).thenReturn(mockHttpClient);
            when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
            when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
            when(mockStatusLine.getStatusCode()).thenReturn(400);

            // When / Then
            CustomException thrown = assertThrows(CustomException.class, () -> authService.getPlayerTokens(TEST_PLAYER_CODE));
            assertEquals(BUNGIE_AUTH_ERROR, thrown.getErrorEnum());

            verify(mockHttpClient).execute(any(HttpPost.class));
            verify(mockHttpResponse).close();
            verify(mockHttpClient).close();
        }
    }
}