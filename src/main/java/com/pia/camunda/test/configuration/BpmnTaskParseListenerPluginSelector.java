package com.pia.camunda.test.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * The ReceiveTaskParseListenerPluginSelector class uses Spring's ImportSelector interface to
 * conditionally import the {@link com.pia.camunda.test.plugin.BpmnTaskListenerPlugin
 * ReceiveTaskParseListenerPlugin } into the application context based on the presence of {@link
 * EnableBpmnTaskListenerPlugin @EnableReceiveTaskListenerPlugin } annotation.
 *
 * @author Yusuf BOZKURT
 */
public class BpmnTaskParseListenerPluginSelector implements ImportSelector {

  @Override
  public @NonNull String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
    return new String[] {"com.pia.camunda.test.plugin.BpmnTaskListenerPlugin"};
  }
}
