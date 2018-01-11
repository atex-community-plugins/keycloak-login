package com.atex.plugins.keycloak.login;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.keycloak.RSATokenVerifier;
import org.keycloak.representations.AccessToken;

import com.google.common.base.Strings;
import com.polopoly.cm.client.CMException;

/**
 * KeycloakTokenVerifier
 *
 * @author mnova
 */
public class KeycloakTokenVerifier {

    private static final Logger LOGGER = Logger.getLogger(KeycloakTokenVerifier.class.getName());

    private final String realmUrl;
    private final String rsaPublicKey;

    public KeycloakTokenVerifier(final String realmUrl, final String rsaPublicKey) {
        this.realmUrl = Strings.nullToEmpty(realmUrl);
        this.rsaPublicKey = Strings.nullToEmpty(rsaPublicKey);
    }

    public AccessToken verify(final String token) throws CMException {
        try {
            final RSATokenVerifier verifier = RSATokenVerifier.create(token);
            return verifier
                    .realmUrl(realmUrl)
                    .publicKey(convertToPublicKey(rsaPublicKey))
                    .verify()
                    .getToken();
        } catch (Exception e) {
            throw new CMException(e);
        }
    }

    AccessToken getToken(final String token) throws CMException {
        try {
            final RSATokenVerifier verifier = RSATokenVerifier.create(token);
            return verifier
                    .realmUrl(realmUrl)
                    .publicKey(convertToPublicKey(rsaPublicKey))
                    .getToken();
        } catch (Exception e) {
            throw new CMException(e);
        }
    }

    PublicKey convertToPublicKey(final String value) throws Exception {
        final byte[] bytes = value.getBytes("UTF-8");
        final byte[] decoded = Base64.decodeBase64(bytes);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return kf.generatePublic(keySpec);
    }

}
