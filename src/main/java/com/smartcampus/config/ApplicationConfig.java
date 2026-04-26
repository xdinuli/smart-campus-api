package com.smartcampus.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Subclass of {@link javax.ws.rs.core.Application} (via {@link ResourceConfig}) with a
 * versioned entry point per coursework. Tomcat 9 / Servlet 4 ({@code javax.*}).
 */
@ApplicationPath("/api/v1")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages(
                "com.smartcampus.resources",
                "com.smartcampus.exception.mapper",
                "com.smartcampus.filter"
        );
        register(JacksonFeature.class);
    }
}
