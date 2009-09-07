/*
 * Created on Jul 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cosylab.vdct.undo;

import com.cosylab.vdct.graphics.objects.Connector;

/**
 * @author ilist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreateConnectorAction extends DeleteConnectorAction {

	/**
	 * @param object
	 * @param inlink
	 * @param outlink
	 */
	public CreateConnectorAction(Connector object, String inlink, String outlink) {
		super(object, inlink, outlink);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3.5.2001 16:26:04)
	 * @return java.lang.String
	 */
	public String getDescription() {
		return "CreateConnector ["+object+"]()";
	}
	/**
	 * This method was created in VisualAge.
	 */
	protected void redoAction() {
		super.undoAction();
	}
	/**
	 * This method was created in VisualAge.
	 */
	protected void undoAction() {
		super.redoAction();
	}
}
