package com.pia.camunda.test.listener;

import com.pia.camunda.test.helper.ReceiveTaskManager;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;

/**
 * The ReceiveTaskParseListener class is a listener that gets triggered during the parsing of a BPMN
 * process. Specifically, it is triggered when a receive task is encountered in the process.
 *
 * @author Yusuf Bozkurt
 */
public class BpmnTaskParseListener extends AbstractBpmnParseListener {

  /**
   * Method invoked when a receive task is encountered during the parsing of a BPMN process. It
   * initializes a {@link ReceiveTaskListener ReceiveTaskListener} and adds it to the receive task.
   * It also prepares a helper object for the receive task and stores it in a global map.
   *
   * @param userTaskElement The XML element corresponding to the user task.
   * @param scope The scope of the process.
   * @param activity The activity in the BPMN process.
   */
  @Override
  public void parseReceiveTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
    var listener = new CustomTaskExecutionListener();
    activity.addListener(ExecutionListener.EVENTNAME_START, listener);
    ReceiveTaskManager.getInstance().registerReceiveTask(activity.getId());
  }

  @Override
  public void parseServiceTask(Element serviceTaskElement, ScopeImpl scope, ActivityImpl activity) {
    var listener = new CustomTaskExecutionListener();
    activity.addListener(ExecutionListener.EVENTNAME_START, listener);
    activity.addListener(ExecutionListener.EVENTNAME_END, listener);
  }
}
