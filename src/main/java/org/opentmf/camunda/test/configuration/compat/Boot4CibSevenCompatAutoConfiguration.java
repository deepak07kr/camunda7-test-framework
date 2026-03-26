package org.opentmf.camunda.test.configuration.compat;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath;
import org.springframework.context.annotation.Bean;

/**
 * Provides compatibility beans that bridge Spring Boot 3.x class locations (expected by CibSeven
 * 2.1) to their Spring Boot 4.x equivalents.
 *
 * <p>This configuration can be removed once CibSeven ships Spring Boot 4 compatible starters.
 */
@AutoConfiguration
public class Boot4CibSevenCompatAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(JerseyApplicationPath.class)
  JerseyApplicationPath legacyJerseyApplicationPath(
      org.springframework.boot.jersey.autoconfigure.JerseyApplicationPath delegate) {
    return delegate::getPath;
  }
}
