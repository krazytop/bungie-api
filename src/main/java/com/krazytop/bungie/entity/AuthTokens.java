package com.krazytop.bungie.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@Accessors(chain=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthTokens {

    @JsonAlias("access_token")
    private String accessToken;
    @JsonAlias("refresh_token")
    private String refreshToken;
    private OffsetDateTime accessTokenExpiresAt;
    private OffsetDateTime refreshTokenExpiresAt;

    @JsonProperty("expires_in")
    private void unpackExpiresIn(Integer expiresIn) {
        this.accessTokenExpiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(expiresIn);
    }

    @JsonProperty("refresh_expires_in")
    private void unpackRefreshExpiresIn(Integer expiresIn) {
        this.refreshTokenExpiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(expiresIn);
    }
}
