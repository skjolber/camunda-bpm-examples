package com.camunda.bpm.example.spring.soap.start.validator;

import org.camunda.bpm.engine.impl.form.validator.FormFieldValidator;
import org.camunda.bpm.engine.impl.form.validator.FormFieldValidatorContext;

public class AccountNumberFormFieldValidator implements FormFieldValidator {

  private static final String PATTERN = "[0-9]+";
  
  @Override
  public boolean validate(Object submittedValue, FormFieldValidatorContext validatorContext) {
    if(submittedValue instanceof String) {
      String string = (String)submittedValue;
      
      return string.matches(PATTERN);
    }
    
    return false;
  }

}
