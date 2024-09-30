package com.pia.camunda.test.integration;

import java.util.Collections;
import java.util.Map;

/**
 * BpmIT interface defines the methods to be implemented by all BPM integration tests.
 * It ensures a contract for the setup of tests, such as providing the start process variables.
 *
 * @author Yusuf Bozkurt
 */
public interface BpmIT {

  default Map<String, Object> getStartProcessVariables() {
    return Collections.emptyMap();
  }
}
