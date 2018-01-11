package com.atex.plugins.keycloak.login;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.keycloak.representations.adapters.config.AdapterConfig;
import org.keycloak.representations.adapters.config.BaseRealmConfig;
import org.keycloak.util.JsonSerialization;

import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.google.common.base.Strings;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.app.policy.CheckboxPolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;

public class ConfigPolicy extends BaselinePolicy {

    private static final Logger LOGGER = Logger.getLogger(ConfigPolicy.class.getName());

    public static final ExternalContentId ID = new ExternalContentId("plugins.com.atex.plugins.keycloak-login.Config");

    private static final String ENABLED = "enabled";
    private static final String JSON = "json";
    private static final String PUBKEY = "pubKey";
    private static final String LOCALE = "locale";
    private static final String TEXT = "text";

    public boolean isEnabled() {
        try {
            final Policy policy = getChildPolicy(ENABLED);
            if (policy instanceof CheckboxPolicy) {
                return ((CheckboxPolicy) policy).getBooleanValue();
            }
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    public String getJson() {
        return getChildValue(JSON, null);
    }

    public String getAuthUrl() {
        return getKeycloakConfig()
                .map(BaseRealmConfig::getAuthServerUrl)
                .orElse(null);
    }

    public String getRealm() {
        return getKeycloakConfig()
                .map(BaseRealmConfig::getRealm)
                .orElse(null);
    }

    public String getPubKey() {
        return getChildValue(PUBKEY, null);
    }

    public String getLocale() {
        return getChildValue(LOCALE, null);
    }

    public String getText() {
        return getChildValue(TEXT, null);
    }

    public Optional<AdapterConfig> getKeycloakConfig() {
        final String json = getJson();
        if (!Strings.isNullOrEmpty(json)) {
            try {
                final AdapterConfig adapterConfig = JsonSerialization.readValue(json, AdapterConfig.class);
                return Optional.of(adapterConfig);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Optional.empty();
    }

    public String getRealmUrl() {
        final String authUrl = getAuthUrl();
        final String realm = getRealm();
        if (!Strings.isNullOrEmpty(authUrl) && !Strings.isNullOrEmpty(realm)) {
            return UriBuilder.fromUri(getAuthUrl())
                             .path("realms")
                             .path(realm)
                             .build()
                             .toASCIIString();
        }
        return null;
    }

    public String getLoadProfileUrl() {
        final String url = getRealmUrl();
        if (!Strings.isNullOrEmpty(url)) {
            return UriBuilder
                    .fromPath(url)
                    .path("account")
                    .build()
                    .toASCIIString();
        }
        return null;
    }

}
