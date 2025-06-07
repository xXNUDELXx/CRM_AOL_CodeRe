package pl.coderslab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringDataUserDetailsService customUserDetailsService() {
		return new SpringDataUserDetailsService();
	}

@Override
protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/").hasAnyRole(getAllRoles())
        .antMatchers("/client/**", "/contract/**", "/event/**", "/import/**", "/employeeSearch/**")
        .hasAnyRole("OWNER", "EMPLOYEE", "MANAGER")
        .antMatchers("/managerSearch/**").hasAnyRole("OWNER", "MANAGER")
        .antMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().permitAll()
        .and().formLogin()
        .and().httpBasic();
}

private String[] getAllRoles() {
    return new String[]{"ADMIN", "OWNER", "EMPLOYEE", "MANAGER"};
}

