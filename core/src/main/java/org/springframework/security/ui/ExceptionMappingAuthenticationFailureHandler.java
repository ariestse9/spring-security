package org.springframework.security.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.AuthenticationException;
import org.springframework.security.util.RedirectUtils;
import org.springframework.security.util.UrlUtils;
import org.springframework.util.Assert;

/**
 * Uses the internal map of exceptions types to URLs to determine the destination on authentication failure. The keys
 * are the full exception class names.
 * <p>
 * If a match isn't found, falls back to the behaviour of the parent class,
 * {@link SimpleUrlAuthenticationFailureHandler}.
 *
 * @author Luke Taylor
 * @version $Id$
 * @since 2.5
 */
public class ExceptionMappingAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private Map<String, String> failureUrlMap = new HashMap<String, String>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String url = failureUrlMap.get(exception.getClass().getName());

        if (url != null) {
            RedirectUtils.sendRedirect(request, response, url, isUseRelativeContext());
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    /**
     * Sets the map of exception types (by name) to URLs.
     *
     * @param failureUrlMap the map keyed by the fully-qualified name of the exception class, with the corresponding
     * failure URL as the value.
     *
     * @throws IllegalArgumentException if the entries are not Strings or the URL is not valid.
     */
    public void setExceptionMappings(Map<?,?> failureUrlMap) {
        this.failureUrlMap.clear();
        for (Map.Entry<?,?> entry : failureUrlMap.entrySet()) {
            Object exception = entry.getKey();
            Object url = entry.getValue();
            Assert.isInstanceOf(String.class, exception, "Exception key must be a String (the exception classname).");
            Assert.isInstanceOf(String.class, url, "URL must be a String");
            Assert.isTrue(UrlUtils.isValidRedirectUrl((String)url), "Not a valid redirect URL: " + url);
            this.failureUrlMap.put((String)exception, (String)url);
        }
    }
}