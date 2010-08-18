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

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.RandomAccessFile;

import org.csstudio.platform.libs.dcf.utils.Base64Coder;
import org.csstudio.platform.libs.dcf.actions.IAction;

/**
 * This class is the part of the file transfer mechanism that
 * actually sends a piece of the file needed to be transfered.
 * One instance is created for the sending of each piece.
 * 
 * @author kstrnisa
 *
 */

public class ActionRequestChunk implements IAction {
	
	public static final String ACTION_ID = "org.csstudio.platform.libs.dcf.requestChunk";
	private static Map<String, RandomAccessFile> _filehashMap = new HashMap<String, RandomAccessFile>();
	private static Map<String, IProgressListener> _progressListeners = new HashMap<String, IProgressListener>();

	public Object run(Object param) {
		
		if(!(param instanceof Map)) {
			return null;
		}
		
		Map paramMap = (Map)param;
		
		String filepath = (String)paramMap.get("filepath");
		String filehash = (String)paramMap.get("filehash");
		int chunkNo = Integer.parseInt((String)paramMap.get("chunkNo"));		
		int chunkSize = Integer.parseInt((String)paramMap.get("chunkSize"));
		int progress = Integer.parseInt((String)paramMap.get("progress"));
		
		try{
			
			RandomAccessFile file;
			//checks if the file transfer is active
			//in which case the file is already opened
			if(_filehashMap.containsKey(filehash)){
				file = _filehashMap.get(filehash);
			}else{
				file = new RandomAccessFile(filepath, "r");
				_filehashMap.put(filehash, file);
			}
			//checks if maybe it is the closing command
			if(progress == ActionFileTransfer.COMPLETED){
				file.close();
				notifyCompleted(filehash);
				_filehashMap.remove(filehash);
				_progressListeners.remove(filehash);
				return null;
			}
			//checks if maybe the transfer failed
			if(progress == ActionFileTransfer.FAILED){
				file.close();
				notifyState(filehash, "failed", filepath);
				_filehashMap.remove(filehash);
				_progressListeners.remove(filehash);
				return null;
			}
			//checks if maybe the transfer was rejected
			if(progress == ActionFileTransfer.REJECTED){
				System.out.println("rejection reported");
				file.close();
				notifyState(filehash, "rejected", filepath);
				_filehashMap.remove(filehash);
				_progressListeners.remove(filehash);
				return null;
			}
			
			//reads the requested part of the file and sends it back
			file.seek(chunkNo*chunkSize);
			
			long remaining = file.length() - file.getFilePointer();
			byte[] byteArray = chunkSize < remaining ? (new byte[chunkSize]) : (new byte[(int)remaining]);
			
			file.read(byteArray);
			char[] byteArrayEncoded = Base64Coder.encode(byteArray);
			
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("chunkNo", Integer.toString(chunkNo));
			resultMap.put("data", byteArrayEncoded);
			
			notifyProgress(filehash, progress, progress + " %  done", filepath);
			
			return resultMap;
		}catch(Exception e){ 
			System.out.println("Exception: " + e.getMessage() + " in " + this.getClass().toString());
		}
		
		return null;
	}
	
	/**
	 * Adds a progress listener for a transfer of a specific file.
	 * 
	 * @param filehash the hash of the file that we want to recieve
	 * 	progress reports for
	 * @param pr the object to inform on the progress of the file transfer
	 */
	
	public static void addProgressListener(String filehash, IProgressListener pr){
		_progressListeners.put(filehash, pr);
	}
	
	/**
	 * Notifies the appropriate object about the process of the file transfer
	 * 
	 * @param filehash file that is being transfered
	 * @param progress progress of the file transfer ( in %)
	 */
	
	private void notifyProgress(String filehash, int progress, String state, String filepath){
		if(_progressListeners.containsKey(filehash)){
			_progressListeners.get(filehash).reportProgress(progress);
			_progressListeners.get(filehash).reportState(state, (new File(filepath)).getName());
		}
	}
	
	/**
	 * Notifies the appropriate object that the file transfer is completed
	 * 
	 * @param filehash file that is being transfered
	 * @param progress progress of the file transfer ( in %)
	 */
	
	private void notifyCompleted(String filehash){
		if(_progressListeners.containsKey(filehash)){
			_progressListeners.get(filehash).reportComplete(_progressListeners.get(filehash));
		}
	}
	
	/**
	 * Notifies the appropriate object about the state of the file transfer
	 * 
	 * @param filehash file that is being transfered
	 */
	
	private void notifyState(String filehash, String state, String filepath){
		if(_progressListeners.containsKey(filehash)){
			_progressListeners.get(filehash).reportState(state, (new File(filepath)).getName());
		}
	}

}
