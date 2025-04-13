package org.opentmf.camunda.test.plugin;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.opentmf.camunda.test.listener.BpmnTaskParseListener;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BpmnTaskListenerPluginTest {

  @Mock private ProcessEngineConfigurationImpl processEngineConfiguration;
  private BpmnTaskListenerPlugin bpmnTaskListenerPlugin;

  @BeforeEach
  void setUp() {
    bpmnTaskListenerPlugin = new BpmnTaskListenerPlugin();
  }

  @Test
  void preInitTest() {
    List<BpmnParseListener> mockedList = new ArrayList<>();
    when(processEngineConfiguration.getCustomPreBPMNParseListeners()).thenReturn(mockedList);

    bpmnTaskListenerPlugin.preInit(processEngineConfiguration);

    assertInstanceOf(BpmnTaskParseListener.class, mockedList.get(0));
  }
}
