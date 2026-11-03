package com.mycompany.myapp.security.oauth2;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Đăng nhập ngầm qua SSO: {@code /oauth2/authorization/oidc?silent=true} gửi {@code prompt=none}
 * sang Keycloak — còn phiên SSO thì đăng nhập luôn, chưa có thì Keycloak trả lỗi và quay về
 * {@code /?sso=none} (trang chủ, người dùng tự bấm Đăng nhập) thay vì hiện form đăng nhập.
 */
public final class SilentSsoLoginSupport {

    private static final String SILENT_PARAM = "silent";
    private static final String PROMPT_PARAM = "prompt";
    private static final String PROMPT_NONE = "none";
    private static final String NO_SSO_SESSION_URL = "/?sso=none";
    private static final String DEFAULT_FAILURE_URL = "/login?error";

    /** Lỗi OIDC của prompt=none khi không đăng nhập ngầm được */
    private static final Set<String> NO_SSO_SESSION_ERRORS = Set.of(
        "login_required",
        "interaction_required",
        "consent_required",
        "account_selection_required"
    );

    private SilentSsoLoginSupport() {}

    public static OAuth2AuthorizationRequestResolver authorizationRequestResolver(
        ClientRegistrationRepository clientRegistrationRepository
    ) {
        DefaultOAuth2AuthorizationRequestResolver delegate =
            new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
            );
        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(
                HttpServletRequest request
            ) {
                return customize(request, delegate.resolve(request));
            }

            @Override
            public OAuth2AuthorizationRequest resolve(
                HttpServletRequest request,
                String clientRegistrationId
            ) {
                return customize(
                    request,
                    delegate.resolve(request, clientRegistrationId)
                );
            }
        };
    }

    public static AuthenticationFailureHandler failureHandler() {
        SimpleUrlAuthenticationFailureHandler defaultHandler =
            new SimpleUrlAuthenticationFailureHandler(DEFAULT_FAILURE_URL);
        SimpleUrlAuthenticationFailureHandler noSessionHandler =
            new SimpleUrlAuthenticationFailureHandler(NO_SSO_SESSION_URL);
        return (request, response, exception) -> {
            if (isNoSsoSession(exception)) {
                noSessionHandler.onAuthenticationFailure(
                    request,
                    response,
                    exception
                );
            } else {
                defaultHandler.onAuthenticationFailure(
                    request,
                    response,
                    exception
                );
            }
        };
    }

    private static OAuth2AuthorizationRequest customize(
        HttpServletRequest request,
        OAuth2AuthorizationRequest authorizationRequest
    ) {
        if (
            authorizationRequest == null ||
            !"true".equals(request.getParameter(SILENT_PARAM))
        ) {
            return authorizationRequest;
        }
        return OAuth2AuthorizationRequest.from(authorizationRequest)
            .additionalParameters(params ->
                params.put(PROMPT_PARAM, PROMPT_NONE)
            )
            .build();
    }

    private static boolean isNoSsoSession(AuthenticationException exception) {
        return (
            exception instanceof OAuth2AuthenticationException &&
            NO_SSO_SESSION_ERRORS.contains(
                ((OAuth2AuthenticationException) exception).getError().getErrorCode()
            )
        );
    }
}
