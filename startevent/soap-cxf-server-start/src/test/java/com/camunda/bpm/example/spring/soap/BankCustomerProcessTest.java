package com.camunda.bpm.example.spring.soap;

import static org.hamcrest.Matchers.is;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.camunda.bpm.example.spring.soap.v1.BankCustomerServicePortType;
import com.camunda.bpm.example.spring.soap.v1.SetAccountRequest;
import com.camunda.bpm.example.spring.soap.v1.SetAccountResponse;
import com.camunda.bpm.example.spring.soap.v1.StatusType;
/**
 * 
 * This class tests three async processes. 
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "classpath:/spring/engine-test.xml", "classpath:/spring/beans.xml", "classpath:/spring/beans-test.xml"})

public class BankCustomerProcessTest {
	
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ProcessEngine processEngine;
	
	@Autowired
	@Qualifier("bankCustomerServiceClient")
	protected BankCustomerServicePortType client;

	@Test
	public void testProcess() throws Exception {
	  SetAccountRequest parameters = new SetAccountRequest();
    parameters.setCustomerNumber("1");
    parameters.setAccountNumber("1");
    parameters.setAccountName("myAccount");
    SetAccountResponse response = client.setAccount(parameters);
    
    Assert.assertThat(response.getStatus(), is(StatusType.OK));
	}

}
