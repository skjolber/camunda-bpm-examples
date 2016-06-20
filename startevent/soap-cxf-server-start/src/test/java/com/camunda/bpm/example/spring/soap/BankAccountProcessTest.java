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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.camunda.bpm.example.spring.soap.start.v1.BankAccountServicePortType;
import com.camunda.bpm.example.spring.soap.start.v1.BankRequestHeader;
import com.camunda.bpm.example.spring.soap.start.v1.InvalidValueException;
import com.camunda.bpm.example.spring.soap.start.v1.InvalidValueException_Exception;
import com.camunda.bpm.example.spring.soap.start.v1.SetAccountNameRequest;
import com.camunda.bpm.example.spring.soap.start.v1.SetAccountNameResponse;
import com.camunda.bpm.example.spring.soap.start.v1.StatusType;
/**
 * 
 * Test process
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "classpath:/spring/engine-test.xml", "classpath:/spring/beans.xml", "classpath:/spring/beans-test.xml"})

public class BankAccountProcessTest {
	
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
	
	@Value("secret")
	private String secret;
	
	@Autowired
	@Qualifier("bankAccountServiceClient")
	protected BankAccountServicePortType client;

	@Test
	public void call_service_returnSuccess() throws Exception {
	  SetAccountNameRequest request = new SetAccountNameRequest();
	  request.setAccountNumber("1");
	  request.setAccountName("myAccount");
	  
    BankRequestHeader header = new BankRequestHeader();
    header.setSecret(secret);
    
    SetAccountNameResponse response = client.setAccountName(request, header);
    
    Assert.assertThat(response.getStatus(), is(StatusType.SUCCESS));
	}
	
	 @Test
	  public void call_serviceWithInvalidAccountNumber_returnAccountNumberInvalidValueException() throws Exception {
	    SetAccountNameRequest request = new SetAccountNameRequest();
	    request.setAccountNumber("ABC");
	    request.setAccountName("myAccount");
	    
	    BankRequestHeader header = new BankRequestHeader();
	    header.setSecret(secret);

	    try {
	      client.setAccountName(request, header);
	      
	      Assert.fail();
	    } catch(InvalidValueException_Exception e) {
	      InvalidValueException faultInfo = e.getFaultInfo();
	      
	      Assert.assertThat(faultInfo.getName(), is("accountNumber"));
	    }
	  }

	  @Test
	  public void call_serviceWithInvalidSecret_returnError() throws Exception {
	    SetAccountNameRequest request = new SetAccountNameRequest();
	    request.setAccountNumber("1");
	    request.setAccountName("myAccount");
	    
	    BankRequestHeader header = new BankRequestHeader();
	    header.setSecret("noSecret");
	    
	    SetAccountNameResponse response = client.setAccountName(request, header);
	    
	    Assert.assertThat(response.getStatus(), is(StatusType.SUCCESS));
	  }	 
}
