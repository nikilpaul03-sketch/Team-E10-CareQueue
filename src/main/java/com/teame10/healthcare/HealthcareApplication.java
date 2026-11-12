
package com.teame10.healthcare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Team E10 - Smart Public Healthcare Appointment and Queue Management System
 *
 * Main Spring Boot Application Entry Point.
 * Phase 1: Baseline Architecture and Project Initialization.
 */
@SpringBootApplication
public class HealthcareApplication {

    private static final Logger logger =
            LoggerFactory.getLogger(HealthcareApplication.class);

    public static void main(String[] args) {

        ConfigurableApplicationContext context =
                SpringApplication.run(HealthcareApplication.class, args);

        String port = context.getEnvironment()
                .getProperty("server.port", "8080");

        String contextPath = context.getEnvironment()
                .getProperty("server.servlet.context-path", "");

        logger.info("==============================================================");
        logger.info(" Team E10: Smart Public Healthcare System");
        logger.info(" CareQueue Application Initialized Successfully!");
        logger.info(" Application URL: http://localhost:{}{}",
                port, contextPath);
        logger.info("==============================================================");
    }
}
