package com.camunda.bpm.example.spring.soap;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jws.WebService;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.camunda.bpm.example.spring.soap.v1.BankCustomerServicePortType;
import com.camunda.bpm.example.spring.soap.v1.BankException_Exception;
import com.camunda.bpm.example.spring.soap.v1.SetAccountRequest;
import com.camunda.bpm.example.spring.soap.v1.SetAccountResponse;
import com.camunda.bpm.example.spring.soap.v1.StatusType;

/**
 * 
 * Webservice for starting a process via a start node form
 * 
 */

@WebService(endpointInterface = "com.camunda.bpm.example.spring.soap.v1.BankCustomerServicePortType")

public class BankCustomerService implements BankCustomerServicePortType {

  private Logger logger = LoggerFactory.getLogger(BankCustomerService.class.getName());
  
  @Autowired
  private ProcessEngine processEngine;
  
  private FormService formService;
  
  @PostConstruct
  protected void init() {
    formService = processEngine.getFormService();
  }
  
  @Override
  public SetAccountResponse setAccount(SetAccountRequest request) throws BankException_Exception {
    logger.info("Set account " + request.getAccountNumber() + " name " + request.getAccountName() + " for customer number " + request.getCustomerNumber());
    
    Map<String, Object> properties = new HashMap<>();
    properties.put("accountNumber", request.getAccountNumber());
    properties.put("accountName", request.getAccountName());
    properties.put("customerNumber", request.getCustomerNumber());
    
    ProcessDefinition lastestProcessDefinition = getLastestProcessDefinition("accountProcess");
    SetAccountResponse response = new SetAccountResponse();

    if(lastestProcessDefinition != null) {
      try {
        formService.submitStartForm(lastestProcessDefinition.getId(), properties);
        
        response.setStatus(StatusType.OK);
      } catch(Exception e) {
        logger.warn("Unable to start process", e);
        
        response.setStatus(StatusType.INVALID_FORM_VALUES);
      }
    } else {
      response.setStatus(StatusType.PROCESS_NOT_FOUND);
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

