package org.example.krutishe_za_vsih.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Повністю вимикаємо CSRF для нашого API, щоб Postman міг кидати PATCH/POST/DELETE
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                .authorizeHttpRequests(auth -> auth
                        // Дозволяємо взагалі без авторизації доступ до всього API та Swagger
                        .requestMatchers("/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/", "/animators", "/css/**", "/images/**").permitAll()
                        // Кабінет залишаємо захищеним для локальних тестів сайту
                        .requestMatchers("/reports").hasAnyRole("ANIMATOR", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/reports", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails animator = User.withDefaultPasswordEncoder()
                .username("vanya")
                .password("1234")
                .roles("ANIMATOR")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("boss")
                .password("admin")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(animator, admin);
    }
}