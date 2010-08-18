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
import java.io.RandomAccessFile;
import java.util.Map;

import org.csstudio.platform.libs.dcf.actions.ActionExecutor;
import org.csstudio.platform.libs.dcf.actions.IActionResponseReceived;
import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.utils.Base64Coder;

/**
 * The thread in which the file transfer is managed. 
 * In this class all the requests for the required 
 * pieces of the file are made, and the file is put back together.
 * 
 * @param chk the array that specefies which chunks have to be transfered
 * @param param the parameters of the current file transfer
 * @param org the requestor of the file transfer
 * @param dest the destination folder in which to save the file
 * @param pr the object to inform on the progress of the file transfer
 * 
 * @author kstrnisa
 *
 */
public class FileTransferThread extends Thread {
	
	private static final int _maxNumberOfTries = 3;
	
	private Map<String, Object> _paramMap;
	private boolean[] _chunks;
	private ContactElement _origin;
	private File _destination;
	private int _numberOfTries;
	private RandomAccessFile _file;
	
	public FileTransferThread(RandomAccessFile file, boolean[] chunks, 
			Map<String, Object> paramMap, ContactElement origin,
			File destination){
		
		super("FileTransferThread");
		_file = file;
		_paramMap = paramMap;
		_chunks = chunks;
		_origin = origin;
		_destination = destination;
		_numberOfTries = 0;
	}
	
	public void run(){
		
		try{
			for(int i = 0;i < _chunks.length;i++){
				if(!_chunks[i]){
					_paramMap.put("chunkNo", Integer.toString(i));
					_paramMap.put("progress", Integer.toString(100*(i + 1)/_chunks.length));
					
					//the request for the chunk is sent
					Object response = 
						ActionExecutor.executeObjectSynchronous(
								ActionRequestChunk.ACTION_ID, _paramMap, _origin);
					
					//checks if the response is of the type expected
					//if ti is not it tries again a couple of times
					if(!(response instanceof Map)){
						System.out.println("corrupted chunk");
						if(_numberOfTries < _maxNumberOfTries){
							_numberOfTries++;
							i--;
							continue;
						}else{
							fail();
							return;
						}
					}
					
					//if the response has the appropriate data, it is written into the file
					Map resultMap = (Map)response;
					
					int chunkNo = Integer.parseInt((String)resultMap.get("chunkNo"));
					char[] byteArrayEncoded = (char[])resultMap.get("data");
					byte[] byteArray = Base64Coder.decode(byteArrayEncoded);
					
					_file.seek(chunkNo * ActionFileTransfer.CHUNK_SIZE);
					_file.write(byteArray);
					
					_chunks[i] = true;
					_numberOfTries = 0;
					int progress = 100*(chunkNo + 1)/_chunks.length;
					ActionFileTransfer.getProgressListener().reportProgress(progress);
					ActionFileTransfer.getProgressListener().reportState(
							progress + " %  done", _destination.getName());
				}
			
			}
			
			_file.close();
			_paramMap.put("progress", Integer.toString(ActionFileTransfer.COMPLETED));
			ActionExecutor.execute(ActionRequestChunk.ACTION_ID, _paramMap, _origin,
					new IActionResponseReceived(){
						public void notifyResponseReceived(Object response) { }
			});
			ActionFileTransfer.getProgressListener().reportComplete(
					ActionFileTransfer.getProgressListener());
			ActionFileTransfer.getFileTransferListener().autoAction(_destination.getName(), _origin);
		
		}catch(Exception e) {
			System.out.println("Exception: " + e.getMessage() + " in " + this.getClass().toString());
			fail();
		}
		
	}
	
	private void fail(){
		_paramMap.put("progress", Integer.toString(ActionFileTransfer.FAILED));
		ActionExecutor.executeObjectSynchronous(
				ActionRequestChunk.ACTION_ID, _paramMap, _origin);
		ActionFileTransfer.getProgressListener().reportState(
				"failed", _destination.getName());
	}
}
