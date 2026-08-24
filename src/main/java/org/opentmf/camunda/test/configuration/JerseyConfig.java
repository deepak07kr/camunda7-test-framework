package org.opentmf.camunda.test.configuration;

import org.opentmf.camunda.test.chaos.EngineOutageFilter;
import jakarta.ws.rs.ApplicationPath;
import java.util.logging.Level;
import org.cibseven.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.context.annotation.Configuration;

/**
 * Sets the application path to "/engine-rest" and configures request/response logging for Camunda.
 *
 * <p>To enable request / response logging, add this to your logging configuration:
 *
 * <pre>{@code
 * <logger name="org.glassfish.jersey.logging.LoggingFeature" level="DEBUG" />
 * }</pre>
 *
 * @author Gokhan Demir
 */
@Configuration
@ApplicationPath("/engine-rest")
public class JerseyConfig extends CamundaJerseyResourceConfig {

  @Override
  protected void registerAdditionalResources() {
    super.registerAdditionalResources();

    // The chaos valve: a no-op counter until a test arms an EngineOutage — see its javadoc.
    register(EngineOutageFilter.class);

    register(LoggingFeature.class)
        .property(LoggingFeature.DEFAULT_LOGGER_LEVEL, Level.INFO.getName())
        .property(
            LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY)
        .property(
            LoggingFeature.LOGGING_FEATURE_VERBOSITY_SERVER, LoggingFeature.Verbosity.PAYLOAD_ANY)
        .property(LoggingFeature.LOGGING_FEATURE_MAX_ENTITY_SIZE, 8192);
  }
}
