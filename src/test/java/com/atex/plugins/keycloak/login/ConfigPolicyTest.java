package com.atex.plugins.keycloak.login;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * ConfigPolicyTest
 *
 * @author mnova
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigPolicyTest {

    ConfigPolicy policy;

    @Before
    public void before() {
        policy = Mockito.spy(new ConfigPolicy());
    }

    @Test
    public void verifyJson() {
        final String json = "{\n" +
                "  \"realm\": \"GONG\",\n" +
                "  \"auth-server-url\": \"http://172.17.8.101.xip.io:8080/auth\",\n" +
                "  \"ssl-required\": \"external\",\n" +
                "  \"resource\": \"frontend\",\n" +
                "  \"public-client\": true,\n" +
                "  \"confidential-port\": 0\n" +
                "}";
        Mockito.doReturn(json).when(policy).getChildValue(Mockito.eq("json"), Mockito.anyString());

        Assert.assertEquals("http://172.17.8.101.xip.io:8080/auth", policy.getAuthUrl());
        Assert.assertEquals("GONG", policy.getRealm());
        Assert.assertEquals("http://172.17.8.101.xip.io:8080/auth/realms/GONG", policy.getRealmUrl());
    }

    @Test
    public void verifyPubKey() {
        final String pubKey = "This is my public key";
        Mockito.doReturn(pubKey).when(policy).getChildValue(Mockito.eq("pubKey"), Mockito.anyString());

        Assert.assertEquals(pubKey, policy.getPubKey());
    }

}