package org.opentmf.camunda.test.plugin;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.cibseven.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.cibseven.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentmf.camunda.test.listener.BpmnTaskParseListener;

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

  @Test
  void preInitTest_withNullListeners() {
    when(processEngineConfiguration.getCustomPreBPMNParseListeners()).thenReturn(null);

    bpmnTaskListenerPlugin.preInit(processEngineConfiguration);

    ArgumentCaptor<List<BpmnParseListener>> captor = ArgumentCaptor.forClass(List.class);
    verify(processEngineConfiguration).setCustomPreBPMNParseListeners(captor.capture());
    List<BpmnParseListener> createdList = captor.getValue();
    assertInstanceOf(BpmnTaskParseListener.class, createdList.get(0));
  }
}
