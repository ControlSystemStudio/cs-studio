package org.csstudio.alarm.treeView.edswrapper;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class EDSInitialContextFactory implements InitialContextFactory {

	public Context getInitialContext(Hashtable<?, ?> env)
			throws NamingException {
		// TODO Auto-generated method stub
		return new EDSDirContext(env);
	}

}
