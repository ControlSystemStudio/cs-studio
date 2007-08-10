/*
 * Copyright (c) 2006 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */
/**
 * 
 */
package de.desy.css.dal.tine;

import com.cosylab.util.CommonException;


/**
 * This exception signals that connection to remote system has failed for some reason.
 * 
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class ConnectionFailed extends CommonException {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Creates new instance of ConnectionFailed.
	 */
	public ConnectionFailed() {
		super(null,"Connection failed for unknown reason.");
	}
	/**
	 * Creates new instance of ConnectionFailed.
	 */
	public ConnectionFailed(String name) {
		super(name,"Connection to '"+name+"' failed for unknown reason.");
	}
	/**
	 * Creates new instance of ConnectionFailed.
	 */
	public ConnectionFailed(String message,Throwable t) {
		super(null,message,t);
	}
}
