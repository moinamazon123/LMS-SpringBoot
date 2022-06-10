package com.maps.yolearn;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManagerFactory;

/**
 * @author PREMNATH
 */
//@Configuration
@ComponentScan(basePackages = {"com.maps.yolearn"})
@EntityScan(basePackages = {"com.maps.yolearn"})
public class SpringConfiguration {

    @Bean
    public SessionFactory sessionFactory(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        return sessionFactory;
    }

}
