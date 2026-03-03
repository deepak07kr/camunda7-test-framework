package org.opentmf.camunda.test.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BpmITTest {

  @Test
  void getStartProcessVariables_defaultImplementation_returnsEmptyMap() {
    BpmIT bpmIT = new BpmIT() {};
    var variables = bpmIT.getStartProcessVariables();
    assertNotNull(variables);
    assertTrue(variables.isEmpty());
  }
}
