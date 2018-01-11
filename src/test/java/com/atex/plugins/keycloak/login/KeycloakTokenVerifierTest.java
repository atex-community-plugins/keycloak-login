package com.atex.plugins.keycloak.login;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.keycloak.exceptions.TokenNotActiveException;

import com.polopoly.cm.client.CMException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * KeycloakTokenVerifierTest
 *
 * @author mnova
 */
public class KeycloakTokenVerifierTest {

    @Test
    public void testValidToken() throws Exception {
        String realmUrl = "http://172.17.8.101.xip.io:8080/auth/realms/GONG";
        String rsaPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoM3SPPEWRLVU8LiCuZ3J9CfQq1oHxUzFdy5PHYwrM8SVOKJAr2roRWM81fk8l51fd+xX/bRxKjC7+GMdrl+BSufL7Hs5C8GHkC+N52PLKDsMYXnWiiPI6SeLGdPKjErX18TeSWg+BNhui4oS1HPqXhujfz2Eh9yGuWuRYHDXAOpkY8i996jtLXCWTRjxqZIIZOkaYMm/+ZoIPOJFETEvpk5k4k0IVHT7Vo5ABip5CJrGdOXAh/aryS6TwYx5Qbo1MlsGFyh7tT+V3hojgSc0g4BfY+BL9FODTDyC7onlJfMqCMZeUWxnEs5TOvlzNbu9P4r3T7Ey/u1mEvHnGytHNwIDAQAB";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCY2RjbTRmTThHZFlsSmZRLUw0MnQ4WjBudEc0LUszNVdZNGZwNlI0U09FIn0.eyJqdGkiOiI4ZmY2OTgzZS1jMzVkLTRkYWEtYWYzNy0xMjdlOWRkY2IwNjciLCJleHAiOjE1MTU0OTczMjMsIm5iZiI6MCwiaWF0IjoxNTE1NDk3MDIzLCJpc3MiOiJodHRwOi8vMTcyLjE3LjguMTAxLnhpcC5pbzo4MDgwL2F1dGgvcmVhbG1zL0dPTkciLCJhdWQiOiJmcm9udGVuZCIsInN1YiI6IjQyNTczOGI4LWEyMjMtNGRiOC04MmFmLTE4ZDFiMjNjMTM3YSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImZyb250ZW5kIiwibm9uY2UiOiI2NDZjMTc2OC01YThiLTQ1YTgtOThjYi1hNzc0YWYwZDA3MDQiLCJhdXRoX3RpbWUiOjE1MTU0OTcwMjIsInNlc3Npb25fc3RhdGUiOiI2NGQyODA4Zi02MzM5LTQ0ODAtOGNlYy0xYmEwNTY2MTIxY2UiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6ODAwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoiTWFyY28gTm92YSIsInByZWZlcnJlZF91c2VybmFtZSI6Im1ub3ZhIiwiZ2l2ZW5fbmFtZSI6Ik1hcmNvIiwiZmFtaWx5X25hbWUiOiJOb3ZhIiwiZW1haWwiOiJub3ZhbWFyY29AaG90bWFpbC5jb20ifQ.UnepJuVAHQuwShgq5z2VjXnUnJ0sy0e2HUnjThLJVHVXh87ABUjk8CLx8CtDXrrmX_b9m6YlFw9TpFjsBtzYW0MqYeB-4ejuyWNR2-7Kqcnf7FGIts_W5ceh61kSBj9y_Hy5p2F7R3JcDqV32ny7CrZsNrSOfOtLE72brZ3uimaqaGMb9BDudA1uJfLmw2x-Ddg2QUvFFRGrisVnxieb33w-7X5xvp93wwdtM9jKsy5uXNl_gG-e_vmS_GfQk2KcQoXXyJXVlpwo8695HHgBRSQFNGCAcp4eu7yLco0kZOlnLzdrkFoa1uEILy9sWBqIYSvAP4TpPKeKBLqy2RJrKQ";

        final KeycloakTokenVerifier tokenVerifier = new KeycloakTokenVerifier(realmUrl, rsaPubKey);

        final Date cal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2018-01-09T12:28:43");

        final Jws jwtClaims = Jwts
                .parser()
                .setSigningKey(tokenVerifier.convertToPublicKey(rsaPubKey))
                .setClock(() -> cal)
                .parseClaimsJws(token);
    }

    @Test
    public void invalidToken() throws Exception {
        String realmUrl = "http://172.17.8.101.xip.io:8080/auth/realms/GONG";
        String rsaPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoM3SPPEWRLVU8LiCuZ3J9CfQq1oHxUzFdy5PHYwrM8SVOKJAr2roRWM81fk8l51fd+xX/bRxKjC7+GMdrl+BSufL7Hs5C8GHkC+N52PLKDsMYXnWiiPI6SeLGdPKjErX18TeSWg+BNhui4oS1HPqXhujfz2Eh9yGuWuRYHDXAOpkY8i996jtLXCWTRjxqZIIZOkaYMm/+ZoIPOJFETEvpk5k4k0IVHT7Vo5ABip5CJrGdOXAh/aryS6TwYx5Qbo1MlsGFyh7tT+V3hojgSc0g4BfY+BL9FODTDyC7onlJfMqCMZeUWxnEs5TOvlzNbu9P4r3T7Ey/u1mEvHnGytHNwIDAQAB";
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCY2RjbTRmTThHZFlsSmZRLUw0MnQ4WjBudEc0LUszNVdZNGZwNlI0U09FIn0.eyJqdGkiOiI4ZmY2OTgzZS1jMzVkLTRkYWEtYWYzNy0xMjdlOWRkY2IwNjciLCJleHAiOjE1MTU0OTczMjMsIm5iZiI6MCwiaWF0IjoxNTE1NDk3MDIzLCJpc3MiOiJodHRwOi8vMTcyLjE3LjguMTAxLnhpcC5pbzo4MDgwL2F1dGgvcmVhbG1zL0dPTkciLCJhdWQiOiJmcm9udGVuZCIsInN1YiI6IjQyNTczOGI4LWEyMjMtNGRiOC04MmFmLTE4ZDFiMjNjMTM3YSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImZyb250ZW5kIiwibm9uY2UiOiI2NDZjMTc2OC01YThiLTQ1YTgtOThjYi1hNzc0YWYwZDA3MDQiLCJhdXRoX3RpbWUiOjE1MTU0OTcwMjIsInNlc3Npb25fc3RhdGUiOiI2NGQyODA4Zi02MzM5LTQ0ODAtOGNlYy0xYmEwNTY2MTIxY2UiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6ODAwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJuYW1lIjoiTWFyY28gTm92YSIsInByZWZlcnJlZF91c2VybmFtZSI6Im1ub3ZhIiwiZ2l2ZW5fbmFtZSI6Ik1hcmNvIiwiZmFtaWx5X25hbWUiOiJOb3ZhIiwiZW1haWwiOiJub3ZhbWFyY29AaG90bWFpbC5jb20ifQ.UnepJuVAHQuwShgq5z2VjXnUnJ0sy0e2HUnjThLJVHVXh87ABUjk8CLx8CtDXrrmX_b9m6YlFw9TpFjsBtzYW0MqYeB-4ejuyWNR2-7Kqcnf7FGIts_W5ceh61kSBj9y_Hy5p2F7R3JcDqV32ny7CrZsNrSOfOtLE72brZ3uimaqaGMb9BDudA1uJfLmw2x-Ddg2QUvFFRGrisVnxieb33w-7X5xvp93wwdtM9jKsy5uXNl_gG-e_vmS_GfQk2KcQoXXyJXVlpwo8695HHgBRSQFNGCAcp4eu7yLco0kZOlnLzdrkFoa1uEILy9sWBqIYSvAP4TpPKeKBLqy2RJrKQ";

        final KeycloakTokenVerifier tokenVerifier = new KeycloakTokenVerifier(realmUrl, rsaPubKey);

        try {
            final Jws jwtClaims = Jwts
                    .parser()
                    .setSigningKey(tokenVerifier.convertToPublicKey(rsaPubKey))
                    .parseClaimsJws(token);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExpiredJwtException);
        }

        try {
            tokenVerifier.verify(token);
        } catch (CMException e) {
            Assert.assertTrue(e.getCause() instanceof TokenNotActiveException);
        }
    }

}