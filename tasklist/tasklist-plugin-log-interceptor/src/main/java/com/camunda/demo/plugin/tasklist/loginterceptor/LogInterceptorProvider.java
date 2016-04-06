package com.camunda.demo.plugin.tasklist.loginterceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.hal.HalResource;
import org.camunda.bpm.engine.rest.hal.HalVariableValue;
import org.camunda.bpm.engine.rest.hal.task.HalTask;
import org.camunda.bpm.engine.rest.hal.task.HalTaskList;
import org.camunda.bpm.engine.rest.sub.VariableResource;
import org.camunda.bpm.engine.rest.sub.runtime.FilterResource;
import org.camunda.bpm.engine.rest.sub.task.TaskResource;
import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

/**
 * 
 * Filter on a selection of REST resources.
 * 
 */

@Provider
@ServerInterceptor
public class LogInterceptorProvider implements PreProcessInterceptor, AcceptedByMethod, PostProcessInterceptor {

  private static final Logger LOGGER = Logger.getLogger(LogInterceptorProvider.class.getSimpleName());

  @Override
  public boolean accept(Class declaring, Method method) {

    if (
        TaskResource.class.isAssignableFrom(declaring) || 
        FilterResource.class.isAssignableFrom(declaring) || 
        VariableResource.class.isAssignableFrom(declaring)
        ) {
      return true;
    }

    return false;
  }

  @Override
  public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
    LOGGER.log(Level.INFO, "Process request: " + request.getUri().getPath());
    
    return null;
  }

  @Override
  public void postProcess(ServerResponse response) {

    Class declaring = response.getResourceClass();
    LOGGER.log(Level.INFO, "Process response for " + declaring.getClass().getSimpleName() + "." + response.getResourceMethod().getName() + "(..)");

    if (TaskResource.class.isAssignableFrom(declaring)) {
      handleTaskResourceResponse(response);
    } else if (VariableResource.class.isAssignableFrom(declaring)) {
      handleVariableResourceResponse(response);
    } else if (FilterResource.class.isAssignableFrom(declaring)) {
      handleFilterResourceResponse(response);
    } else
      throw new IllegalArgumentException("Unexpected resource " + declaring);
  }

  private void handleTaskResourceResponse(ServerResponse response) {
    String methodName = response.getResourceMethod().getName();

    if (methodName.equals("getFormVariables")) {
      Map<String, Object> entity = (Map<String, Object>) response.getEntity();
      for (Map.Entry<String, Object> entry : entity.entrySet()) {
        VariableValueDto dto = (VariableValueDto) entry.getValue();
        LOGGER.log(Level.INFO, "Variable " + entry.getKey() + " type " + dto.getType() + " value " + dto.getValue());
      }
    }
  }

  private void handleVariableResourceResponse(ServerResponse response) {
    LOGGER.log(Level.INFO, "Process variable");
  }

  private void handleFilterResourceResponse(ServerResponse response) {
    String methodName = response.getResourceMethod().getName();

    if (methodName.equals("queryList")) {
      Object entity = response.getEntity();
      if (entity instanceof HalTaskList) {
        HalTaskList list = (HalTaskList) entity;

        List<HalResource<?>> embedded = (List<HalResource<?>>) list.getEmbedded("task");

        for (HalResource<?> resource : embedded) {
          if (resource instanceof HalTask) {
            HalTask task = (HalTask) resource;

            LOGGER.log(Level.INFO, "Task " + task.getName());
            List<HalVariableValue> valueList = (List<HalVariableValue>) task.getEmbedded("variable");

            for (HalVariableValue item : valueList) {
              LOGGER.log(Level.INFO, "Variable " + item.getName() + " type " + item.getType() + " value " + item.getValue());
            }
          }
        }
      }

    }
  }

}