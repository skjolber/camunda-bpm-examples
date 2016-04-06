package com.camunda.demo.plugin.tasklist.loginterceptor;

import java.lang.reflect.Method;

import javax.ws.rs.core.Request;

import org.camunda.bpm.engine.rest.sub.task.impl.TaskResourceImpl;
import org.junit.Assert;
import org.junit.Test;

import com.camunda.demo.plugin.tasklist.loginterceptor.LogInterceptorProvider;

public class LogInterceptorProviderTest {

	@Test
	public void filter_ResourceMethod_accepted() throws Exception {
		LogInterceptorProvider interceptor = new LogInterceptorProvider();
		
		Class declaring = TaskResourceImpl.class;
		Method method = declaring.getMethod("getTask", Request.class);
		
		Assert.assertTrue(interceptor.accept(declaring, method));
	}
	
}
