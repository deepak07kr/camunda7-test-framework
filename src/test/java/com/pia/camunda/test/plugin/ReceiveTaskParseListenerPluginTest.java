package com.pia.camunda.test.plugin;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

import com.pia.camunda.test.listener.ReceiveTaskParseListener;
import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceiveTaskParseListenerPluginTest {

    @Mock
    private ProcessEngineConfigurationImpl processEngineConfiguration;
    private ReceiveTaskParseListenerPlugin receiveTaskParseListenerPlugin;

    @BeforeEach
    void setUp() {
        receiveTaskParseListenerPlugin = new ReceiveTaskParseListenerPlugin();
    }

    @Test
    void preInitTest() {
        List<BpmnParseListener> mockedList = new ArrayList<>();
        when(processEngineConfiguration.getCustomPreBPMNParseListeners()).thenReturn(mockedList);

        receiveTaskParseListenerPlugin.preInit(processEngineConfiguration);

        assertInstanceOf(ReceiveTaskParseListener.class, mockedList.get(0));
    }
}
