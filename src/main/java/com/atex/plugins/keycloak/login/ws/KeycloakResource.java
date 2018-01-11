package com.atex.plugins.keycloak.login.ws;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.keycloak.representations.AccessToken;
import org.keycloak.representations.account.UserRepresentation;

import com.atex.onecms.content.ContentId;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.IdUtil;
import com.atex.onecms.ws.service.ErrorResponseException;
import com.atex.onecms.ws.service.WebServiceUtil;
import com.atex.plugins.keycloak.login.ConfigPolicy;
import com.atex.plugins.keycloak.login.KeycloakTokenVerifier;
import com.atex.plugins.users.AuthenticationManager;
import com.atex.plugins.users.Gravatar;
import com.atex.plugins.users.User;
import com.atex.plugins.users.UserUtil;
import com.atex.plugins.users.WebClientUtil;
import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * KeycloakResource
 *
 * @author mnova
 */
@Path("")
public class KeycloakResource {

    private static final Logger LOGGER = Logger.getLogger(KeycloakResource.class.getName());
    private static final String KEYCLOAK_AUTHMETHOD = "keycloak";

    @Context
    private WebServiceUtil webServiceUtil;

    @Context
    private CmClient cmClient;

    @Path("keycloak.json")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getJson() throws ErrorResponseException {
        try {
            final ConfigPolicy policy = getConfig();
            final String json = Strings.nullToEmpty(policy.getJson());
            final String eTag = webServiceUtil.generateETag(IdUtil.contentVersionForPolicy(policy.getContentId(), webServiceUtil.getContentManager()));

            return Response
                    .ok(json)
                    .tag(eTag)
                    .build();

        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw webServiceUtil.errorInternal(e.getMessage());
        }
    }

    @Path("login")
    @POST
    public Response login(
            @Context final HttpServletRequest request,
            @Context final HttpServletResponse response) throws ErrorResponseException {

        final String authz = request.getHeader("Authorization");
        if (Strings.isNullOrEmpty(authz)) {
            throw webServiceUtil.errorForbidden("no authorization");
        }

        if (!authz.toLowerCase().startsWith("bearer ")) {
            throw webServiceUtil.errorForbidden("no bearer");
        }

        try {
            final ConfigPolicy config = getConfig();
            final AccessToken token = verifyToken(config, authz.substring(7));
            final String userProfileUrl = config.getLoadProfileUrl();

            LOGGER.info("loading user profile from " + userProfileUrl + " for " + token.getPreferredUsername() + " and id " + token.getId());

            final Client client = createClient();
            final UserRepresentation kcUser = client
                    .resource(userProfileUrl)
                    .header("Authorization", authz)
                    .type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(UserRepresentation.class);

            final String email = kcUser.getEmail();
            final ContentManager cm = webServiceUtil.getContentManager();
            final AuthenticationManager authMgr = new AuthenticationManager(cmClient, cm);
            ContentId userId = authMgr.getUserId(KEYCLOAK_AUTHMETHOD, email);
            if (userId == null) {
                final User user = new User();
                user.setUsername(kcUser.getUsername());
                user.setEmail(email);
                user.setFirstName(kcUser.getFirstName());
                user.setLastName(kcUser.getLastName());
                String displayName = email.substring(0, email.indexOf("@"));
                user.setDisplayName(displayName);
                user.setImageUrl(Gravatar.getPictureUrl(email, 64, Gravatar.DefaultProfilePicture.RETRO.toString()));
                user.setProfileUrl(Gravatar.getProfileUrl(email));
                userId = authMgr.createUser(KEYCLOAK_AUTHMETHOD, email, user);
            }
            final com.atex.plugins.users.AccessToken cmsToken = authMgr.getToken(userId);
            WebClientUtil.onLogin(userId, cmsToken, response, cmClient, cm);

            final User user = new UserUtil(userId, cm).getUser();
            final String json = new GsonBuilder().create().toJson(user);

            return Response.ok(json).build();
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw webServiceUtil.errorInternal(e.getMessage());
        }
    }

    private Client createClient() {
        final ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        return Client.create(clientConfig);
    }

    private AccessToken verifyToken(final ConfigPolicy config, final String token) throws ErrorResponseException {
        try {
            return createTokenVerifier(config)
                    .verify(token);
        } catch (CMException e) {
            throw webServiceUtil.errorForbidden(e.getCause().getMessage());
        }
    }

    private KeycloakTokenVerifier createTokenVerifier(final ConfigPolicy config) {
        return new KeycloakTokenVerifier(config.getRealmUrl(), config.getPubKey());
    }

    private ConfigPolicy getConfig() throws CMException {
        final PolicyCMServer cmServer = cmClient.getPolicyCMServer();
        return (ConfigPolicy) cmServer.getPolicy(ConfigPolicy.ID);
    }

}
