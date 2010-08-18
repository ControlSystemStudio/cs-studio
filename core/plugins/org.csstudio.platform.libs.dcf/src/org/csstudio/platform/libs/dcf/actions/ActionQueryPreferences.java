/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.platform.libs.dcf.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.filetransfer.ActionFileTransfer;

/**
 * This action returnes the preference file via file transfer
 * 
 * @author kstrnisa
 *
 */

public class ActionQueryPreferences implements IAction {
	
	public static final String ACTION_ID = "org.csstudio.platform.libs.dcf.queryPreferences";
	
	public Object run(Object param) {
		
		if(!(param instanceof ContactElement)){
			return null;
		}
		
		ContactElement origin = (ContactElement)param;
		
		File prefFile = ActionFileTransfer.getRuntimePath().append("plugin_customization.ini").toFile();
		
		if(!prefFile.exists() || !prefFile.canRead() ||!prefFile.isFile()){
			System.out.println(prefFile + "doesn't exist or can't be read");
			return null;
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("filepath", prefFile.toString());
		paramMap.put("filename", prefFile.getName());
		paramMap.put("filehash", Integer.toString(prefFile.hashCode()));
		paramMap.put("size", Integer.toString((int)(prefFile.length())));
		
		ActionExecutor.execute(ActionFileTransfer.ACTION_ID, paramMap, origin, new IActionResponseReceived(){
			public void notifyResponseReceived(Object response){}
		});
		
		return null;
	}

}
