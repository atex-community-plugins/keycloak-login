package com.atex.plugins.keycloak.login;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Strings;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.model.Model;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;

/**
 * LoginLogoutController
 *
 * @author mnova
 */
public class LoginLogoutController extends RenderControllerBase {

    private static final Logger LOGGER = Logger.getLogger(LoginLogoutController.class.getName());
    private static final String DEFAULT_SIGNIN_TEXT = "Sign in with Keycloak";

    @Override
    public void populateModelBeforeCacheKey(final RenderRequest request, final TopModel m, final ControllerContext context) {

        super.populateModelBeforeCacheKey(request, m, context);

        final Model model = m.getLocal();

        boolean enabled = false;
        try {
            final CmClient cmClient = getCmClient(context);
            if (cmClient != null) {
                final PolicyCMServer cmServer = cmClient.getPolicyCMServer();
                final ConfigPolicy config = (ConfigPolicy) cmServer.getPolicy(ConfigPolicy.ID);
                enabled = config.isEnabled();
                if (enabled) {
                    final String locale = config.getLocale();
                    if (!Strings.isNullOrEmpty(locale)) {
                        final String json = String.format("{locale: '%s'}", locale);
                        ModelPathUtil.set(model, "locale", json);
                    }
                    final String text = Optional
                            .ofNullable(Strings.emptyToNull(config.getText()))
                            .orElse(DEFAULT_SIGNIN_TEXT);
                    ModelPathUtil.set(model, "buttonText", text);
                }
            }
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        ModelPathUtil.set(model, "enabled", enabled);
    }
}
