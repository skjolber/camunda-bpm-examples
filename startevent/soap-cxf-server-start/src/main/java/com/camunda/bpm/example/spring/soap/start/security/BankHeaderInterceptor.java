package com.camunda.bpm.example.spring.soap.start.security;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BankHeaderInterceptor extends AbstractSoapInterceptor {
  
  private static final QName HEADER_TYPE = new QName("http://start.soap.spring.example.bpm.camunda.com/v1", "bankRequestHeader");

  private Logger logger = LoggerFactory.getLogger(BankHeaderInterceptor.class.getName());

  @Value("secret")
  private String secret;
  
  public BankHeaderInterceptor() {
      super(Phase.PRE_PROTOCOL);
  }

  @Override
  public void handleMessage(SoapMessage soapMessage) throws Fault {
    
    String headerSecret = getSecret(soapMessage);
    if(headerSecret == null || !headerSecret.equals(secret)) {
      Fault fault = new Fault(new SecurityException("Unable to verify header"));
      fault.setStatusCode(401);
      throw fault;
    }
    
  }
  
  public String getSecret(SoapMessage soapMessage) {
    Header header = soapMessage.getHeader(HEADER_TYPE);

    if(header != null) {
      // parse header. consider doing a JAXB binding if you don't like w3c DOM trees
      Element headerElement = (Element) header.getObject();

      Node child = headerElement.getFirstChild();
      while(child != null) {
        
        if(child instanceof Element) {
          Element element = (Element)child;
          
          if(element.getLocalName().equals("secret")) {
            return element.getTextContent();
          }
        }
        
        child = child.getNextSibling();
      }
      
    }
    return null;
    
  }
}
 