package site.roombook.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import site.roombook.global.springsecurity.filter.CustomAuthenticationEntryPoint;
import site.roombook.global.springsecurity.filter.JsonUsernamePasswordAuthenticationFilter;
import site.roombook.global.springsecurity.filter.ResourceExistenceFilter;
import site.roombook.global.springsecurity.handler.CustomAccessDeniedHandler;
import site.roombook.global.springsecurity.filter.JWTAuthenticationFilter;
import site.roombook.global.springsecurity.handler.SignInFailureHandler;
import site.roombook.global.springsecurity.handler.SignInSuccessJWTProvideHandler;
import site.roombook.service.JwtService;
import site.roombook.service.UserDetailServiceImpl;

import javax.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailServiceImpl userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler())
                .and()
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionTranslationFilter(), JWTAuthenticationFilter.class)
                .addFilterAfter(jsonUsernamePasswordSignInFilter(), JWTAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/js/**", "/css/**", "/img/**").permitAll()
                        .requestMatchers("/favicon/**").permitAll()
                        .requestMatchers("/", "/signin/**", "/api/signin/**", "/signup/**", "/api/signup/**", "/password-reset/**", "/id-inquiry/**", "/error/**", "/invalid-access/**", "/not-found/**", "/api/empls/*/dupcheck").permitAll()
                        .requestMatchers( "/spaces/**", "/book/spaces/**", "/api/spaces/**", "/api/book/**", "/api/mybook/**", "/mybook/**").access(userAuthorityAuthorizationManager())
                        .requestMatchers("/admin/spaces/**", "/api/admin/spaces/**").access(rscAdminAuthorityAuthorizationManager())
                        .requestMatchers("/dept/**", "/api/admin/empls/**").access(emplAdminAuthorityAuthorizationManager())
                        .requestMatchers("/admin/empls/**").access(superAdminAuthorityAuthorizationManager())
                        .anyRequest().authenticated())
                .logout(logout ->
                        logout.logoutSuccessUrl("/").invalidateHttpSession(true).permitAll()
                                .logoutUrl("/signout")
                                .addLogoutHandler(cookieClearingLogoutHandler()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public ResourceExistenceFilter resourceExistenceFilter() {
        return new ResourceExistenceFilter();
    }

    @Bean
    public ExceptionTranslationFilter exceptionTranslationFilter() {
        return new ExceptionTranslationFilter(customAuthenticationEntryPoint());
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = daoAuthenticationProvider();
        return new ProviderManager(provider);
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordSignInFilter() {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(new ObjectMapper());
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(signInSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(signInFailureHandler());
        return jsonUsernamePasswordLoginFilter;
    }

    @Bean
    public SignInSuccessJWTProvideHandler signInSuccessJWTProvideHandler() {
        return new SignInSuccessJWTProvideHandler();
    }

    @Bean
    public SignInFailureHandler signInFailureHandler() {
        return new SignInFailureHandler();
    }

    @Bean
    public CookieClearingLogoutHandler cookieClearingLogoutHandler() {
        return new CookieClearingLogoutHandler(jwtService.getAccessCookieName(), jwtService.getRefreshCookieName());
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
                ROLE_SUPER_ADMIN > ROLE_RSC_ADMIN > ROLE_USER
                ROLE_SUPER_ADMIN > ROLE_EMPL_ADMIN > ROLE_USER
                """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public AuthorityAuthorizationManager<RequestAuthorizationContext> userAuthorityAuthorizationManager() {
        AuthorityAuthorizationManager<RequestAuthorizationContext> userAuthManager = AuthorityAuthorizationManager.hasRole("USER");
        userAuthManager.setRoleHierarchy(roleHierarchy());
        return userAuthManager;
    }

    @Bean
    public AuthorityAuthorizationManager<RequestAuthorizationContext> rscAdminAuthorityAuthorizationManager() {
        AuthorityAuthorizationManager<RequestAuthorizationContext> rscAdminAuthManager = AuthorityAuthorizationManager.hasRole("RSC_ADMIN");
        rscAdminAuthManager.setRoleHierarchy(roleHierarchy());
        return rscAdminAuthManager;
    }

    @Bean
    public AuthorityAuthorizationManager<RequestAuthorizationContext> emplAdminAuthorityAuthorizationManager() {
        AuthorityAuthorizationManager<RequestAuthorizationContext> emplAdminAuthManager = AuthorityAuthorizationManager.hasRole("EMPL_ADMIN");
        emplAdminAuthManager.setRoleHierarchy(roleHierarchy());
        return emplAdminAuthManager;
    }

    @Bean
    public AuthorityAuthorizationManager<RequestAuthorizationContext> superAdminAuthorityAuthorizationManager() {
        AuthorityAuthorizationManager<RequestAuthorizationContext> superAdminAuthManager = AuthorityAuthorizationManager.hasRole("SUPER_ADMIN");
        superAdminAuthManager.setRoleHierarchy(roleHierarchy());
        return superAdminAuthManager;
    }

}
