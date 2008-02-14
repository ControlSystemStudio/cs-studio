/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.diag.IOCremoteManagement.ui;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import org.csstudio.utility.ioc_socket_communication.IOCAnswer;
import org.csstudio.utility.ioc_socket_communication.RMTControl;
/**
 * @author Albert Kagarmanov
 *
 */
public abstract class Node implements Observer {
	protected final static boolean debug=true;
	protected final static boolean debugStrong=false;
	protected final static boolean debugStatus=false;
	public enum typeOfHost {master,slave,unresolve,host,knot,finalVar,finalSatateSet,otherLeaf};
	protected Object parent;
	public ArrayList child = new ArrayList();
	protected String name;
	protected String host;
	protected Request parentRequest;
	protected Request request;
	protected XMLDataSingle parentData;
	protected IOCAnswer iocAnswer;
	protected Display disp;
	protected TreeViewer viewer;
	protected PropertyPart propertyPart;
	protected typeOfHost type;
	protected Node IProot;
	
	abstract protected Request createNewRequest(Request request, XMLDataSingle data);
	abstract protected  typeOfHost nextLevelType(XMLDataSingle data);
	public Node(String name){this.name = name;}
	public String toString(){return name;}
	public typeOfHost getType() { return this.type;}
	public Node(String name,String host,Object parent,TreeViewer viewer,PropertyPart property,Request req, XMLDataSingle data,Node root,typeOfHost type ) {
		this.name = name;
		this.host = host;
		this.parent=parent;
		this.type = type;
		this.viewer=viewer;
		this.propertyPart=property;	
		this.iocAnswer=new IOCAnswer();
		this.iocAnswer.addObserver(this);
		this.disp=viewer.getTree().getDisplay();
		this.parentRequest=req;
		this.parentData=data;
		if(debugStrong) org.csstudio.diag.IOCremoteManagement.Activator.errorPrint("Constr type=",type.toString(),"name=",name);
		this.request=createNewRequest(parentRequest, parentData);
		if (root==null)this.IProot=this; 
		else {
			this.IProot=root;
			checkRootStatus(parentData.isMaster);
		}
	}
	public  void checkRootStatus (boolean var) {
		if (var) {
			if(debugStatus) System.out.println("checkRootStatus=isMaster");
			IProot.type=typeOfHost.master;
		}else {
			if(debugStatus) System.out.println("checkRootStatus=isSlave");
			IProot.type=typeOfHost.slave;	
		}
		//viewer.refresh();
		//disp.update();
	}
	
	public int askNextLevel(PropertyPart p) {
		if (p!=null) propertyPart = p;
		final RMTControl iocContr = RMTControl.getInstance();
		if(debug) System.out.println("RMT req="+request.getDocument());
		if(debugStatus) System.out.println("askNextLevel");
		iocContr.send(host,request.getDocument(), iocAnswer);
		return 0;
	}
	public void update(final Observable arg0, final Object arg1) {
		disp.syncExec(new Runnable() {
			public void run() {
				final String text = iocAnswer.getAnswer();
				if(debug) System.out.println("RMT ans="+text);
				parsing(text);	
				if(debugStatus) System.out.println("update");
				viewer.refresh();
				disp.update();
			}
		});
	}
	protected int indexOf(String name,ArrayList child) {
		String elementAsString=null;
		
		for (int i=0;i<child.size();i++) {
			if(debugStrong) org.csstudio.diag.IOCremoteManagement.Activator.errorPrint(" indexOf");
			Object obj = child.get(i); 
			if (obj instanceof Node) elementAsString=((Node) obj).name; 
			else if (obj instanceof HostIP) elementAsString=((HostIP) obj).name; 
			else if (obj instanceof Knot) elementAsString=((Knot) obj).name; 
			else org.csstudio.diag.IOCremoteManagement.Activator.errorPrint("Node:IndexOf bad class ");

			if(debugStrong) org.csstudio.diag.IOCremoteManagement.Activator.errorPrint("name=",name," childName",elementAsString);
			if ( name.compareTo(elementAsString) == 0) {
				if(debugStrong) org.csstudio.diag.IOCremoteManagement.Activator.errorPrint(" i="+i);
				return i;
			}
		}
		return -1;
	}
	
	protected int parsing(String text) {
		if (propertyPart==null) {
			org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("IOCremoteManagement Error:Node:propertyPart"); //TODO
			return -1;
		}
		typeOfHost tHost;
		Parsing parser = new Parsing(text);
		XMLData data=parser.Parse();
		checkRootStatus(data.isMaster);
		int size;
		if (data != null) size = data.data.length;
		else size =0;
		if(debugStrong) System.out.println("size="+size+" this.name="+this.name);
		if ((type == typeOfHost.finalVar) || (type == typeOfHost.finalSatateSet)|| (type == typeOfHost.otherLeaf) ) {
			propertyPart.setActualData (host,request);
			propertyPart.createFinalLevelScreen(data,(Node) this);
			return 0;
		}
		boolean needFinalLevelScreen=false;
		for (int i=0;i<size;i++) {
			String name = data.data[i].tagValue;
			if (indexOf(name,child) < 0){  
				tHost=nextLevelType(data.data[i]);
				if(tHost==null){needFinalLevelScreen=true; continue;}
				switch(tHost) {
	            case knot:
	            	child.add (new  Knot (name,host,this,viewer,propertyPart,request,data.data[i],IProot,typeOfHost.knot));
            	break;
	            case finalVar:
	            	child.add(new EndNode(name,host,this,viewer,propertyPart,request,data.data[i],IProot,typeOfHost.finalVar));
	            	break;
	            case finalSatateSet:
	            	child.add(new EndNode(name,host,this,viewer,propertyPart,request,data.data[i],IProot,typeOfHost.finalSatateSet));
	            	break;	
	            case otherLeaf:
	            	child.add(new EndNode(name,host,this,viewer,propertyPart,request,data.data[i],IProot,typeOfHost.otherLeaf));
	            	break;	
	            default:
	            	org.csstudio.diag.IOCremoteManagement.Activator.errorPrint("Node:parsing wrong case");
	                break;
				}
			}
		}
		
		viewer.expandToLevel(this, 1);
		
		if (!needFinalLevelScreen){
		propertyPart.createInfoTabForVariable(data.infoResult,data.infoName, data.infoStatus,data.internalStatus);
		} else {
			propertyPart.setActualData (host,request);
			propertyPart.createFinalLevelScreen(data,(Node) this);
		}
		return 0;
	}
	
}
