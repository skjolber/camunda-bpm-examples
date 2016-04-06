package com.camunda.demo.plugin.tasklist.loginterceptor.resources;

import javax.ws.rs.Path;

import org.camunda.bpm.tasklist.resource.AbstractTasklistPluginRootResource;

import com.camunda.demo.plugin.tasklist.loginterceptor.LogInterceptorPlugin;

@Path("plugin/" + LogInterceptorPlugin.ID)
public class LogInterceptorPluginRootResource extends AbstractTasklistPluginRootResource {

  public LogInterceptorPluginRootResource() {
    super(LogInterceptorPlugin.ID);
  }

}
