package org.csstudio.alarm.treeView.views.models;

public class NodeNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7812009173365780447L;

	public String toString(){
		return "Node cannot be found!";
	}
}
