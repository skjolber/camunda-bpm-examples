package com.camunda.bpm.example.spring.soap.start;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Dummy bank service. This service acts as the internal implementation / fulfillment 
 * of the single service task within the process.
 * 
 * @author Thomas Skjolberg
 *
 */

@Service("bankService")
public class BankService {

  private Logger logger = LoggerFactory.getLogger(BankService.class.getName());

  public void setAccountName(DelegateExecution execution) {
    
    String accountName = (String) execution.getVariable("accountName");
    String accountNumber  = (String) execution.getVariable("accountNumber");
    
    logger.info("Mock set account name " + accountName + " for account number " + accountNumber);
  }
  
}
