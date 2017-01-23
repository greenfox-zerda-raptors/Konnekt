package com.greenfoxacademy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ComponentScan
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource securityDataSource;

    @Autowired
    public WebSecurityConfig(DataSource securityDataSource) {
        this.securityDataSource = securityDataSource;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .formLogin()
                .and()
                .logout()
                .and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(securityDataSource)
                .usersByUsernameQuery("select user_name, user_password, enabled from konnekt.user where user_name=?")
                .authoritiesByUsernameQuery("select user_name, user_role from konnekt.user where user_name=?");
    }

//    @Bean
//    public FilterChainProxy springSecurityFilterChain()
//            throws ServletException, Exception {
//        List<SecurityFilterChain> securityFilterChains = new ArrayList<SecurityFilterChain>();
//        securityFilterChains.add(new DefaultSecurityFilterChain(
//                new AntPathRequestMatcher("/login**")));
//        securityFilterChains.add(new DefaultSecurityFilterChain(
//                new AntPathRequestMatcher("/resources/**")));
//        securityFilterChains.add(new DefaultSecurityFilterChain(
//                new AntPathRequestMatcher("/**"),
//                securityContextPersistenceFilter(),
//                logoutFilter(),
//                usernamePasswordAuthenticationFilter(),
//                exceptionTranslationFilter(),
    //filterSecurityInterceptor()
    //  ));
//        return new FilterChainProxy(securityFilterChains);
    // }


}