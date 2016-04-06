package com.camunda.demo.plugin.tasklist.loginterceptor;

import java.util.HashSet;
import java.util.Set;

import org.camunda.bpm.tasklist.plugin.spi.impl.AbstractTasklistPlugin;

import com.camunda.demo.plugin.tasklist.loginterceptor.resources.LogInterceptorPluginRootResource;

public class LogInterceptorPlugin extends AbstractTasklistPlugin {

  public static final String ID = "log-interceptor-plugin";

  public String getId() {
    return ID;
  }

  @Override
  public Set<Class<?>> getResourceClasses() {
    Set<Class<?>> classes = new HashSet<Class<?>>();

    classes.add(LogInterceptorProvider.class);
    classes.add(LogInterceptorPluginRootResource.class);
    return classes;

  }

}
