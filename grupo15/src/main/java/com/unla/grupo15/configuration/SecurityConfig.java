package com.unla.grupo15.configuration;

import com.unla.grupo15.entities.Usuario;
import com.unla.grupo15.repositories.IUsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final IUsuarioRepository usuarioRepository;

    public SecurityConfig(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/usuarios/login", "/unidades/registrar").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/cajero/**").hasRole("EMPLEADO")
                        .requestMatchers("/empleado/**").hasRole("EMPLEADO")
                        .requestMatchers("/responsable/**").hasRole("RESPONSABLE")
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/error/403")
                )
                .formLogin(form -> form
                        .loginPage("/usuarios/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("contrasenia")
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
                            boolean isResponsable = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE"));

                            if (isAdmin) {
                                response.sendRedirect("/admin");
                            } else if (isResponsable) {
                                response.sendRedirect("/responsable");
                            } else {
                                response.sendRedirect("/empleado/inicio");
                            }
                        })
                        .failureUrl("/usuarios/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            return User.withUsername(usuario.getEmail())
                    .password(usuario.getContrasenia())
                    .roles(usuario.getRol().name())
                    .disabled(!usuario.isActivo())
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}