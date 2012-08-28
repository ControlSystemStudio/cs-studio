package org.csstudio.utility.toolbox.guice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class PersistenceContextClearer implements MethodInterceptor {

	public static PersistenceContextClearer  persistenceContextClearer = new PersistenceContextClearer();
	
	private EntityManagerWrapper emWrapper;

	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (emWrapper != null) {
			Object result =  invocation.proceed();
			emWrapper.getEm().clear();
			return result;
		} else {
			return invocation.proceed();
		}
	}

	public void setEm(EntityManagerWrapper emWrapper) {
		this.emWrapper = emWrapper;
	}

}