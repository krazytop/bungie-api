package com.krazytop.bungie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krazytop.bungie.entity.AuthTokens;
import com.krazytop.bungie.exception.CustomException;
import com.krazytop.bungie.mapper.AuthTokensMapper;
import com.krazytop.bungie.model.generated.AuthTokensDTO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

import static com.krazytop.bungie.exception.ApiErrorEnum.BUNGIE_AUTH_ERROR;

@Service
public class AuthService {

    @Value("${bungie.client_id:'XXX'}")
    private String clientId;
    @Value("${bungie.client_secret:'XXX'}")
    private String clientSecret;
    private final ObjectMapper objectMapper;
    private final AuthTokensMapper authTokensMapper;

    @Autowired
    public AuthService(ObjectMapper objectMapper, AuthTokensMapper authTokensMapper){
        this.objectMapper = objectMapper;
        this.authTokensMapper = authTokensMapper;
    }

    public AuthTokensDTO getPlayerTokens(String playerCode) {
        return getPlayerTokensRequest("grant_type=authorization_code&code=" + playerCode);
    }

    public AuthTokensDTO updatePlayerTokens(String refreshPlayerToken) {
        return getPlayerTokensRequest("grant_type=refresh_token&refresh_token=" + refreshPlayerToken);
    }

    private AuthTokensDTO getPlayerTokensRequest(String requestBody) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://www.bungie.net/Platform/App/OAuth/Token/");
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_FORM_URLENCODED));
            String auth = String.format("%s:%s", clientId, clientSecret);
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpPost.addHeader("Authorization", "Basic " + encodedAuth);
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    return authTokensMapper.toDTO(objectMapper.readValue(EntityUtils.toString(response.getEntity()), AuthTokens.class));
                } else {
                    throw new CustomException(BUNGIE_AUTH_ERROR);
                }
            }
        } catch (IOException ex) {
            throw new CustomException(BUNGIE_AUTH_ERROR, ex);
        }
    }
}
