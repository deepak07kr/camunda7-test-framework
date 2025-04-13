package org.opentmf.camunda.test.plugin;

import org.opentmf.camunda.test.listener.BpmnTaskParseListener;

import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Component;

/**
 * The ReceiveTaskParseListenerPlugin class is a Camunda Process Engine plugin
 * that add custom listener {@link BpmnTaskParseListener ReceiveTaskParseListener} to the list of parse listeners.
 *
 * @author Yusuf Bozkurt
 */
@Component
public class BpmnTaskListenerPlugin extends AbstractProcessEnginePlugin {

  /**
   * This method is called during the construction of the ProcessEngineConfiguration (Pre-Initialization phase).
   * It initializes a ReceiveTaskParseListener and adds it to the pre-parse listeners of the ProcessEngineConfiguration.
   *
   * @param processEngineConfiguration The configuration object of the Process Engine.
   */
  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    List<BpmnParseListener> preParseListeners = processEngineConfiguration.getCustomPreBPMNParseListeners();
    if(preParseListeners == null) {
      preParseListeners = new ArrayList<>();
      processEngineConfiguration.setCustomPreBPMNParseListeners(preParseListeners);
    }
    preParseListeners.add(new BpmnTaskParseListener());
  }
}
