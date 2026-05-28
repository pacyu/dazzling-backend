package com.dazzling.blog.config;

import com.dazzling.blog.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		CookieCsrfTokenRepository csrfRepository = new CookieCsrfTokenRepository();
    csrfRepository.setCookiePath("/");
    csrfRepository.setCookieCustomizer(cookie -> {
			cookie.sameSite("None");
			cookie.secure(true);
			cookie.httpOnly(false);
		});
		
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf
						.csrfTokenRepository(csrfRepository)
						.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
				)
//				.authorizeHttpRequests(auth -> auth
//						.requestMatchers("/", "/index", "/articles**", "/category**", "/article**", "/about**", "/search**", "/lab**").permitAll()
//						.requestMatchers("/api/liked/**", "/api/viewed/**", "/api/comment", "/api/feedback").permitAll()
//						.requestMatchers("/blog/**", "/webjars/**").permitAll()
//						.anyRequest().authenticated()
//				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/login", "/api/logout", "/api/user/session", "/api/feedback", "/api/category**", "/api/tag", "/api/article**", "/api/category**", "/api/comment**", "/api/liked**", "/api/viewed**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated()
				)
//				.formLogin(form -> form
//						.loginPage("/login")
//						.successHandler(successHandler())
//						.defaultSuccessUrl("/manager")
//						.failureUrl("/login?error=true")
//						.permitAll()
//				)
				.formLogin(AbstractHttpConfigurer::disable)  // 禁用默认表单登录
				.httpBasic(AbstractHttpConfigurer::disable)
				.logout(logout -> logout
						.logoutUrl("/api/logout")
						.deleteCookies("JSESSIONID")
//						.logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
						.logoutSuccessUrl("/login")
						.permitAll()
				);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 12);
	}

	@Bean
	public AuthenticationSuccessHandler successHandler() {
		SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
		handler.setUseReferer(true);
		return handler;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("https://pacyu.github.io")); // "http://localhost:5173", "http://localhost:4000"
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600 * 24L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}