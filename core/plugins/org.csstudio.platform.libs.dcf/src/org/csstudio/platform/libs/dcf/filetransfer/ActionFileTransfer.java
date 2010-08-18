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
 package org.csstudio.platform.libs.dcf.filetransfer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import org.csstudio.platform.libs.dcf.DCFPlugin;
import org.csstudio.platform.libs.dcf.actions.ActionExecutor;
import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.osgi.framework.Bundle;

/**
 * This class represents the shell of file transfer.
 * In here the requiered chunks, destination folder
 * are determined and the FileTransferThread which
 * does the actual transfer is created and run.
 * 
 * @author kstrnisa
 *
 */

public class ActionFileTransfer implements IAction {
	
	public static final String ACTION_ID = "org.csstudio.platform.libs.dcf.fileTransfer";
	public static final int CHUNK_SIZE = 100000;
	public static final int COMPLETED = -1;
	public static final int FAILED = -2;
	public static final int REJECTED = -3;
	
	private static IFileTransferListener _fileTransferListener;
	private static IProgressListener _progressListener;
	
	private boolean[] _chunks;
	private Map<String, Object> _paramMap;
	private int _fileSize;
	private String _filename;
	private ContactElement _origin;

	public Object run(Object param) {
		
		if(!(param instanceof Map)) {
			return null;
		}
		
		_paramMap = (Map<String, Object>)param;
		
		_filename = (String)_paramMap.get("filename");
		_fileSize = Integer.parseInt((String)_paramMap.get("size"));
		_origin = (ContactElement)_paramMap.get("origin");
		_chunks = new boolean[_fileSize/CHUNK_SIZE + 1];
		
		_paramMap.put("chunkSize", Integer.toString(CHUNK_SIZE));
		_paramMap.put("chunkNo", Integer.toString(0));
		
		//preference check
		Preferences pref = DCFPlugin.getDefault().getPluginPreferences();
		String actionOnReceive = pref.getString(PreferenceConstants.RECIEVE_ACTION);
		File destination = new File(new File(pref.getString(PreferenceConstants.DEFAULT_PATH)),_filename);
		boolean prefAutoLoad = pref.getBoolean(PreferenceConstants.AUTOLOAD_PREFERENCES);
		
		//checks if maybe it is a preference file and if it should be automaticlaly loaded
		if(isPrefFile() && prefAutoLoad){
			destination = new File(new File(getRuntimePath().toString()),_filename);
			actionOnReceive = "default";
			System.out.println("autoloading prefs");
		}
		
		//gets the destination folder if so specified in the preferences
		if(_fileTransferListener != null && actionOnReceive == "browser"){
			destination = _fileTransferListener.getSaveFolder(_filename, destination, _origin.toString());
		}
		
		//checks if maybe the file transfer has been rejected
		if(destination == null){
			reject();
			return null;
		}
		
		try{
			RandomAccessFile file = new RandomAccessFile(destination, "rw");
				
			Thread ftt = new FileTransferThread(file, _chunks, _paramMap, _origin, destination);
			ftt.start();
		}catch(IOException e){
			System.out.println("Exception: " + e.getMessage() + " in " + this.getClass().toString());
			reject();
		}
		
		return null;
	}
	
	private void reject(){
		_paramMap.put("progress", Integer.toString(REJECTED));
		ActionExecutor.executeObjectSynchronous(ActionRequestChunk.ACTION_ID, _paramMap, _origin);
	}
	
	/**
	 * Adds the file transfer listener object to this action.
	 * This object will solicite the sving folder from the user
	 * if so specifed in the preferences.
	 * 
	 * @param ftl the file transfer listener object
	 */
	
	public static void setFileTransferListener(IFileTransferListener ftl){
		_fileTransferListener = ftl;
	}
	
	/**
	 * returns the file transfer listener object 
	 * asociated with file transfer
	 * 
	 * @return 
	 */
	
	public static IFileTransferListener getFileTransferListener(){
		return _fileTransferListener;
	}
	
	/**
	 * Adds the progress listener object to the file transfer action
	 * 
	 * @param pr
	 */
	
	public static void setProgressListener(IProgressListener pr){
		_progressListener = pr;
	}
	
	/**
	 * returns the progress listener object 
	 * asociated with the file transfer action
	 * 
	 * @return
	 */
	
	public static IProgressListener getProgressListener(){
		return _progressListener;
	}
	
	/**
	 * 
	 * @return current runtime path
	 */
	
	public static IPath getRuntimePath(){
		Bundle bundle = Platform.getBundle(DCFPlugin.PLUGIN_ID);
		IPath path = Platform.getStateLocation(bundle);
		path = path.removeLastSegments(4);
		System.out.println("runtime path: "+path.toString());
		return path;
	}
	
	/**
	 * determines if the file si a prefernce file
	 * 
	 * @param filename the file in question
	 * @return true if it is a preference file, false otherwise
	 */
	
	private boolean isPrefFile(){
		String ext = new Path(_filename).getFileExtension();
		return ext.equals("css-ps");
	}

}
