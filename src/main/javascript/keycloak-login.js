(function($) {
  window.kclogin = {

    _keycloak: Keycloak('/keycloak/keycloak.json'),

    init: function() {
      var self = this;
      return this._keycloak.init({
        onLoad: 'check-sso'
      }).success(function(authenticated) {
        if (authenticated && !currentUser.data) {
          return self.onLogin(authenticated);
        }
      }).error(function () {
        return self.onError();
      });
    },

    onLogin: function(authenticated) {
      if (authenticated) {
        return this.doSiteLogin();
      } else {
        console.log('no auth');
      }
    },

    onError: function() {
      //alert('error ' + e);
    },

    doLogin: function(options) {
      return this._keycloak.login(options);
    },

    doLogout: function(options) {
      return this._keycloak.logout(options);
    },

    doAjaxLogout: function(options) {
      var self = this;
      var deferred = $.Deferred();
      var url = self._keycloak.createLogoutUrl(options);
      $.ajax({
        type: 'GET',
        url: url,
        async: false,
        contentType: "application/json",
        success: function(nullResponse) {
          deferred.resolve();
        },
        error: function(e) {
          console.log('error revoking ',e);
          deferred.reject(e);
        }
      });
      return deferred.promise();
    },

    doSiteLogin: function() {
      var self = this;
      $.ajax({
        type: 'POST',
        url: '/keycloak/login',
        beforeSend: function(xhr){
          xhr.setRequestHeader('Authorization', 'bearer ' + self._keycloak.token);
        },
        success: function(res, status, xhr) {
          // logout from keycloak since we are now logged in polopoly.
          self.doLogout();
        },
        error: function(xhr, status, err) {
          alert("Login failure: " + err);
          self.doLogout();
        }
      });
    }

  };

  socialauth.providers['keycloak'] = {
    logout: function() {
      return window.kclogin.doAjaxLogout();
    }
  };

  $(document).ready(function() {

    // only if the login is enabled (i.e. the button is present...)
    // then we will check for the login and eventually init the
    // login flow.

    if ($('.keycloak-login').length > 0) {
      window.currentUser.isLogged().always(function (data) {
        if (!data) {
          window.kclogin.init();
        }
      });
    }
  })

})(jQuery);