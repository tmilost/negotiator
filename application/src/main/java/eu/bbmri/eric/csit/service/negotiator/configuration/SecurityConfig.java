package eu.bbmri.eric.csit.service.negotiator.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Profile({"dev", "prod"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("admin")
        .password(passwordEncoder().encode("admin"))
        .roles("ADMIN")
        .and()
        .withUser("directory")
        .password(passwordEncoder().encode("directory"))
        .roles("EXT_SERV")
        .and()
        .withUser("perun")
        .password(passwordEncoder().encode("perun"))
        .roles("PERUN_USER");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .cors()
        .and()
        .csrf()
        .disable();

    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/v3/projects/**")
        .hasAuthority("SCOPE_openid")
        .and()

        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/v3/queries/**")
        .hasAnyRole("ADMIN", "EXT_SERV")

        .and()
        .authorizeRequests()
        .anyRequest()
        .permitAll()

        .and()
        .httpBasic()
        .and()
        .oauth2ResourceServer()
        .jwt();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
