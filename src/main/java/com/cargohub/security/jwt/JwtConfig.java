package com.cargohub.security.jwt;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@Getter
@ToString
public class JwtConfig {

    // Spring doesn't inject/autowire to "static" fields.
    // Link: https://stackoverflow.com/a/6897406
    @Value("${security.jwt.uri:/auth/**}")
    private String Uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer_}")
    private String prefix;

    @Value("${security.jwt.secret:cargohub}")
    private String secret;
}
