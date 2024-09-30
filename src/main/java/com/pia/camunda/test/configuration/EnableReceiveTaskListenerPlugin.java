package com.pia.camunda.test.configuration;

import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

/**
 *
 *
 * <pre>@EnableReceiveTaskListenerPlugin</pre>
 *
 * is a custom annotation used to easily integrate the Receive Task Listener within Spring context.
 * This annotation simplifies the process of hooking up your listener to any Spring Boot
 * application.
 *
 * @author Yusuf BOZKURT
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ReceiveTaskParseListenerPluginSelector.class)
public @interface EnableReceiveTaskListenerPlugin {}
