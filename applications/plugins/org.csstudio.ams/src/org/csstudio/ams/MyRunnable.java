
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

package org.csstudio.ams;

//synch methods
@SuppressWarnings("hiding")
public class MyRunnable implements Runnable
{
	public Object objGui;
	public Object objData;
	public Object obj3;
	public Object obj4;
	public Object obj5;
	public Object obj6;
	public Object objRet;
	public Object objRetAr[];
	
	public MyRunnable() {
	    // Nothing to do
	}
	
    public MyRunnable(Object objGui) {
		this.objGui = objGui;
	}
	
	public MyRunnable(Object objGui, Object objData) {
		this.objGui = objGui;
		this.objData = objData;
	}
	
	public MyRunnable(Object objGui, Object objData, Object obj3, Object obj4) {
		this.objGui = objGui;
		this.objData = objData;
		this.obj3 = obj3;
		this.obj4 = obj4;
	}
	
	public MyRunnable(Object objGui, Object objData,
	                  Object obj3, Object obj4,
	                  Object obj5, Object obj6) {
		
	    this.objGui = objGui;
		this.objData = objData;
		this.obj3 = obj3;
		this.obj4 = obj4;
		this.obj5 = obj5;
		this.obj6 = obj6;
	}
	
	@Override
    public void run() {
	    // Nothing to do
	}
}
