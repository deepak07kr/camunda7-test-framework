package com.pia.camunda.test.util;

import java.util.HashMap;
import org.camunda.bpm.client.variable.impl.TypedValueField;

/**
 * @author Gokhan Demir
 */
public class VariableUtil {

  /**
   * If the sent string is null or less than 4000 bytes, returns the string, otherwise
   * creates a typedValueField and encapsulates the string within that field
   * so that it can be persisted as a Camunda WFF variable with the help of the Spin plugin.
   * @param s The requested string value of a Camunda variable.
   * @return either the string itself or encapsulated form inside a TypedValueField.
   * @see TypedValueField
   * @see org.camunda.spin.plugin.impl.SpinProcessEnginePlugin
   */
  public static Object stringVariable(String s) {
    if (s == null || s.length() <= 4000) {
      return s;
    }
    var typedValueField = new TypedValueField();
    typedValueField.setType("Object");
    typedValueField.setValue(s);
    typedValueField.setValueInfo(new HashMap<>() {{
      put("objectTypeName", String.class.getName());
      put("serializationDataFormat", "application/json");
    }});
    return typedValueField;
  }
}
