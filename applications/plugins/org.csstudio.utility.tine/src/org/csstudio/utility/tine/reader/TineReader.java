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
/*
 * $Id$
 */
package org.csstudio.utility.tine.reader;

import java.util.ArrayList;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.ProcessVariable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.desy.tine.queryUtils.TQuery;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 15.05.2007
 */
public class TineReader extends Job {

	/** The name of {@link ControlSystemItem}.*/
	private String _name;
	/** List with the read elements. */
    private NameSpaceResultListTine _resaultList;
    /** Not used !? */
	private String _type;
     
	/**
	 * 
	 * @param name The name of {@link ControlSystemItem}
	 * @param type not used !?
	 * @param liste the list to put the result.
	 */
	public TineReader(final String name,final String type, final NameSpaceResultListTine liste) {
        super(name);
        if(name!=null){
    		_name = name.split(",")[name.split(",").length-1];
        }else{
        	_name="";
        }
        if(type!=null){
        	_type = type;
        }else{
        	_type="";
        }
        if(liste!=null){
        	_resaultList = liste;	
        }else{
        	_resaultList = new NameSpaceResultListTine();
        }
        CentralLogger.getInstance().debug(this,"name:"+_name);        
        CentralLogger.getInstance().debug(this,"Type:"+_type);
    }

    /** {@inheritDoc}*/
    @Override
	protected final IStatus run(final IProgressMonitor monitor) {
        read();
        return Status.OK_STATUS;
    }
    
    /**
     * Read the data from Tine Name Server.
     */
    public final void read(){
    	ArrayList<ControlSystemItem> csi = new ArrayList<ControlSystemItem>();
    	String[] content;
    	String[] path = _type.split(",");
    	CentralLogger.getInstance().debug(this,"Name: '"+_name+"'\t Type: '"+_type+"'\t länge: "+path.length);
        switch (path.length) {
		case 1:
	    	content = TQuery.getContexts();
	    	break;
		case 2:
    		content = TQuery.getDeviceSubsystems(_name);
    		break;
		case 3:
    		content = TQuery.getDeviceServers(path[path.length-2],path[path.length-1]);
    		break;
		case 4:
		    CentralLogger.getInstance().debug(this,"read: "+path[path.length-3]+","+path[path.length-1]);
			content = TQuery.getDeviceNames(path[path.length-3],path[path.length-1]);
			if(content==null){
			    CentralLogger.getInstance().debug(this,"Set default #0" );
				content = new String[]{"#0"};
			}
			break;
		case 5:
			CentralLogger.getInstance().debug(this,"read Properties: "+path[path.length-4]+","+path[path.length-2]+","+path[path.length-1]);
			content = TQuery.getDeviceProperties(path[path.length-4],path[path.length-2],path[path.length-1]);
			break;

		default:
    		content = null;
    	}
        
        String tine = ControlSystemEnum.DAL_TINE.getPrefix()+"://";
        if(content!=null&&(path.length==4||path.length==5)){
	    	for (String string : content) {
	    		String name = tine;
	    		String[] pathPV;
	    		if(_type.endsWith(",")||string.startsWith(",")){
                    pathPV = (_type+string).split(",");
                }else{
                    pathPV = (_type+","+string).split(",");
                }
	    		if(pathPV.length>1){
	    			for (int i = 0; i < pathPV.length; i++) {
	    				if(i!=0&&i!=2){
	    					name = name.concat(pathPV[i]);
    						name = name.concat("/");
	    				}
					}
	    		}else{
					name = string;
				}
	    		if(name.endsWith("/")){
	    		    name = name.substring(0, name.length()-1);
	    		}
	    		if(_type.endsWith(",")||string.startsWith(",")){
                    csi.add(new ProcessVariable(name,_type+string));
                }else{
                    csi.add(new ProcessVariable(name,_type+","+string));
                }
			}
        }else if(content!=null&&path.length<4){
            CentralLogger.getInstance().debug(this,""+content.length);
	    	for (String string : content) {
	    	    if(_type.endsWith(",")||string.startsWith(",")){
	    	        csi.add(new ControlSystemItem(string,_type+string));
	    	    }else{
	    	        csi.add(new ControlSystemItem(string,_type+","+string));
	    	    }
			}
    	}else{
    		CentralLogger.getInstance().warn(this,"No Elements found");
    	}
    	_resaultList.setResultList(csi);
    	tine=null;
    	content=null;
    	path=null;
    	csi = null;
    	
    	
    }

}
