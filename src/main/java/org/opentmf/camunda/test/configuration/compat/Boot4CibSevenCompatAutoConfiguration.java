package org.opentmf.camunda.test.configuration.compat;

import java.util.EnumSet;
import jakarta.servlet.DispatcherType;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Provides compatibility beans that bridge Spring Boot 3.x class locations (expected by CibSeven
 * 2.1) to their Spring Boot 4.x equivalents.
 *
 * <p>This configuration can be removed once CibSeven ships Spring Boot 4 compatible starters.
 */
@AutoConfiguration(
    afterName = "org.cibseven.bpm.spring.boot.starter.rest.CamundaBpmRestJerseyAutoConfiguration")
@ConditionalOnClass(name = "org.cibseven.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig")
public class Boot4CibSevenCompatAutoConfiguration {

  private static final String ENGINE_REST_PATH = "/engine-rest";

  @Bean
  @ConditionalOnMissingBean(JerseyApplicationPath.class)
  JerseyApplicationPath legacyJerseyApplicationPath() {
    return () -> ENGINE_REST_PATH;
  }

  @Bean
  FilterRegistrationBean<ServletContainer> cibsevenJerseyFilter(
      org.glassfish.jersey.server.ResourceConfig resourceConfig) {
    var registration = new FilterRegistrationBean<>(new ServletContainer(resourceConfig));
    registration.addUrlPatterns(ENGINE_REST_PATH + "/*");
    registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
    registration.setName("CibSevenJerseyFilter");
    registration.setOrder(0);
    return registration;
  }
}
