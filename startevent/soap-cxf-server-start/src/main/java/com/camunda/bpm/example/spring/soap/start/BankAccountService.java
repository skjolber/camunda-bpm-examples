package com.camunda.bpm.example.spring.soap.start;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jws.WebService;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.form.validator.FormFieldValidatorException;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.camunda.bpm.example.spring.soap.start.v1.BankAccountServicePortType;
import com.camunda.bpm.example.spring.soap.start.v1.BankRequestHeader;
import com.camunda.bpm.example.spring.soap.start.v1.InvalidValueException;
import com.camunda.bpm.example.spring.soap.start.v1.InvalidValueException_Exception;
import com.camunda.bpm.example.spring.soap.start.v1.SetAccountNameRequest;
import com.camunda.bpm.example.spring.soap.start.v1.SetAccountNameResponse;
import com.camunda.bpm.example.spring.soap.start.v1.StatusType;

/**
 * 
 * Webservice for starting a process via a start node form
 * 
 */

@WebService(endpointInterface = "com.camunda.bpm.example.spring.soap.start.v1.BankAccountServicePortType")
public class BankAccountService implements BankAccountServicePortType {

  private Logger logger = LoggerFactory.getLogger(BankAccountService.class.getName());
  
  @Autowired
  private ProcessEngine processEngine;
  
  private FormService formService;
  
  @PostConstruct
  protected void init() {
    formService = processEngine.getFormService();
  }
  
  @Override
  public SetAccountNameResponse setAccountName(SetAccountNameRequest request, BankRequestHeader header) throws InvalidValueException_Exception {
    logger.info("Set account " + request.getAccountNumber() + " name " + request.getAccountName());
    
    Map<String, Object> properties = new HashMap<>();
    properties.put("accountNumber", request.getAccountNumber());
    properties.put("accountName", request.getAccountName());
    
    ProcessDefinition lastestProcessDefinition = getLastestProcessDefinition("setAccountNameProcess");
    SetAccountNameResponse response = new SetAccountNameResponse();

    if(lastestProcessDefinition != null) {
      try {
        formService.submitStartForm(lastestProcessDefinition.getId(), properties);
        
        response.setStatus(StatusType.SUCCESS);
        
        logger.info("Successfully set account name");
      } catch(FormFieldValidatorException e) {
        logger.warn("Invalid value for field " + e.getId(), e);
        
        InvalidValueException exception = new InvalidValueException();
        exception.setName(e.getId());
        
        throw new InvalidValueException_Exception("Invalid value for field " + e.getId(), exception);
      } catch(Exception e) {
        logger.warn("Unable to start process", e);
        
        response.setStatus(StatusType.FAILURE);
      }
    } else {
      response.setStatus(StatusType.FAILURE);
    }
    
    return response;
  }

  
  public ProcessDefinition getLastestProcessDefinition(String process) {
    Set<String> registeredDeployments = processEngine.getManagementService().getRegisteredDeployments();
    
    ProcessDefinition processDefintion = null;
    for(String deploymentId : registeredDeployments) {
      List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().deploymentId(deploymentId).processDefinitionKey(process).list();

      for(ProcessDefinition p : list) {
        if(processDefintion == null || processDefintion.getVersion() < p.getVersion()) {
          processDefintion = p;
        }
      }
    }
    return processDefintion;
  }

  
}

