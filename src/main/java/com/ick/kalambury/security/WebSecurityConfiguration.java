package com.ick.kalambury.security;

import com.ick.kalambury.config.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
            "/v1/account/register",
            "/v1/account/login",
            "/v1/account/resetPassword",
            "/v1/account/changePassword"
    };

    private final Environment environment;
    private final Parameters parameters;
    private final KalamburyUserService userService;

    @Autowired
    public WebSecurityConfiguration(Environment environment, Parameters parameters, KalamburyUserService userService) {
        this.environment = environment;
        this.parameters = parameters;
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(parameters.getAdminUser().getLogin())
                    .password(encoder().encode(parameters.getAdminUser().getPassword()))
                    .roles(Role.ADMIN.name());
        auth.authenticationProvider(authProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requiresChannel()
                .antMatchers("/actuator/**").requiresInsecure()
                .anyRequest().requires(getActiveProfile().equals("prod") ? "REQUIRES_SECURE_CHANNEL" : "REQUIRES_INSECURE_CHANNEL")
            .and().authorizeRequests()
                .antMatchers("/v1/admin/**").hasRole(Role.ADMIN.name())
                .anyRequest().permitAll()
            .and().httpBasic()
                .authenticationEntryPoint((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
            .and().formLogin()
                .loginProcessingUrl("/v1/account/login")
                .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                .failureHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getLocalizedMessage()))
            .and().logout()
                .logoutUrl("/v1/account/logout")
                .logoutSuccessHandler((rq, rs, a) -> rs.setStatus(HttpServletResponse.SC_OK))
            .and().exceptionHandling()
                .accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getLocalizedMessage()))
                .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getLocalizedMessage()))
            .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().cors()
            .and().csrf()
                .disable();
    }

    private String getActiveProfile() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 1) {
            return profiles[0];
        } else {
            throw new RuntimeException("More than one or no active profile set: " + Arrays.toString(profiles));
        }
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
