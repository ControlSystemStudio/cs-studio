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
 package org.csstudio.archive.reader.archiverecord;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
/** Handles the "archiveRecord" low-level staff 
 *  @author Albert Kagarmanov
 */
//
// Main class for ArchiveRecord package
//
public class ArchiveRecord {
	private static final boolean debug = false;
	private final static String ARsuffix = "_h";     // Suffix for archiveRecord
	private final static String numStr = ".NVAL";    // Field for # of point
	private final static String valStr = ".VAL";     // Field for value
	private final static String timStr = ".TIM";     // Field for time in sec    
	private final static String nscStr = ".NSC";     // Field for time nanosec within last sec.
	private final static String sevrStr = ".SVY";    // Field for severuty  
	private final static String flushStr = ".FLSH";  // Field for FLUSH archiveRecord
	private final static String pcabStr = ".PCAB";    // Field for PCAB archiveRecord (Absolute or Percent)
	private final static String rvarStr = ".RVAR";    // Field for RVAR (Diff. last-curr. in %)
	private final static String avarStr = ".AVAR";    // Field for AVAR (Diff. last-curr. abs)
	private String PVname;                            // PVname 
	private String archivePVname;                     //  archiveRecord
	private String nvalName;                          // Field for # of point
	private String valName;                           // Field for value
	private String timeName;                          //	 Field for time in sec    
	private String nsecName;                          // Field for time nanosec within last sec.
	private String sevrName;                          // Field for severuty
	private String flushName;                         // Field for FLUSH archiveRecord
	private String pcabName;                          // Field for PCAB archiveRecord (Absolute or Percent)
	private String rvarName;                          // Field for RVAR (Diff. last-curr. in %)
	private String avarName;                          // Field for AVAR (Diff. last-curr. abs)
	private int dim;                                  // length of all our arrays
	private double valArr[];                          // unsorted arary of values   
	private long  timeArr[];                          // unsorted arary of sec.
	private long  nsecArr[];                          // unsorted arary of nsec.
	private long  statArr[];                          // unsorted arary of status (dummy)
	private long  sevrArr[];                          // unsorted arary of severuty
	private int TIMEOUT_CA = 2000;                    // Epics CA timeout in miliSec
	private int THREAD_DELAY = 100;                   // delay in miliSec
	private int NUM_OF_ITER=TIMEOUT_CA / THREAD_DELAY;
	private long EPICS_TIME_SHIFT=631152000L;         // EPICS time starts from 01-01-1990
	
	public ArchiveRecord(String name)
	{
		this.PVname = name;
		int pos = name.indexOf(ARsuffix);
		if (pos >0) this.PVname = name.substring(0, pos);
		
		this.archivePVname = addSuffix(this.PVname,ARsuffix);
		this.nvalName      = addSuffix(this.archivePVname,numStr);
		this.valName       = addSuffix(this.archivePVname,valStr);
		this.timeName      = addSuffix(this.archivePVname,timStr);
		this.nsecName      = addSuffix(this.archivePVname,nscStr);
		this.sevrName      = addSuffix(this.archivePVname,sevrStr);
		this.flushName     = addSuffix(this.archivePVname,flushStr);
		this.pcabName      = addSuffix(this.archivePVname,pcabStr);
		this.rvarName      = addSuffix(this.archivePVname,rvarStr);
		this.avarName      = addSuffix(this.archivePVname,avarStr);		
	}
	
	public int getDimension() throws Exception {
		dim = -1;
		PV pvdim = PVFactory.createPV(nvalName);//new EPICS_V3_PV(nvalName);
		
		try {
			pvdim.start();
			for(int i=0;i<NUM_OF_ITER;i++) {
			    if (pvdim.isConnected()) break;
			        Thread.sleep(THREAD_DELAY);
			}
			double dbl = ((IDoubleValue) pvdim.getValue()).getValue();
			dim= (int )dbl;             // DBR-typr for NVAL is double
			if(debug)System.out.println("dim="+dim);
			pvdim.stop();
		} catch (InterruptedException e) {
			if(debug) System.out.println ("Bad first CAGET-command for archive record " + archivePVname +" InterruptedException:");
			if(debug) e.printStackTrace();
			return -1;
		} catch (Exception e) {
			if(debug) System.out.println ("Bad first CAGET-command for archive record " + archivePVname +" Exception:");
			if(debug) e.printStackTrace();
			return -1;
		}		
 		return dim;
	}
	
	public int getAllFromCA() throws Exception  {
		int len=dim;
		if(len<=1) {
			System.out.println("bad dimension for archiveRecord="+len);
			return -1;
		}
		// get VALUE field:
		PV pvval = PVFactory.createPV(valName);//new EPICS_V3_PV(valName);
		
		try {
			pvval.start();
			for(int i=0;i<NUM_OF_ITER;i++) {
			    if (pvval.isConnected()) break;
			        Thread.sleep(THREAD_DELAY);
			}
			valArr = ((IDoubleValue) pvval.getValue()).getValues();
			if(valArr.length != dim ) {
				System.out.println(valName+
						"warning: valArr-dimension ("+valArr.length+") not equal NVAL-dim ("+dim+")");
				dim =Math.min(dim,valArr.length);
				if( (len=dim) <= 1) {
					System.out.println("bad dimension for archiveRecord="+len);
					return -1;
				}
			}
			pvval.stop();
		} catch (InterruptedException e) {
			System.out.println ("Bad CAGET-command for archive field " + valName +" InterruptedException:");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println ("Bad CAGET-command for archive field " + valName +" Exception:");
			e.printStackTrace();
		}	
		
		// get TIME field:
		PV pvtime = PVFactory.createPV(timeName);//new EPICS_V3_PV(timeName);
		try {
			pvtime.start();
			for(int i=0;i<NUM_OF_ITER;i++) {
			    if (pvtime.isConnected()) break;
			        Thread.sleep(THREAD_DELAY);
			}
			double[] dblTime = ((IDoubleValue) pvtime.getValue()).getValues();
			if(dblTime.length != dim ) {
				System.out.println(timeName+
						"warning: valArr-dimension ("+dblTime.length+") not equal NVAL-dim ("+dim+")");
				dim =Math.min(dim,dblTime.length);
				if( (len=dim) <= 1) {
					System.out.println("bad dimension for archiveRecord="+len);
					return -1;
				}
			}
			timeArr = new long[dim];
			for(int i=0;i<dblTime.length;i++) {
	        	timeArr[i]= (int) (dblTime[i] + EPICS_TIME_SHIFT );
	        }
			pvtime.stop();
		} catch (InterruptedException e) {
			System.out.println ("Bad CAGET-command for archive field " + timeName +" InterruptedException:");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println ("Bad CAGET-command for archive field " + timeName +" Exception:");
			e.printStackTrace();
		}		
		
		// get Nsec field:
		
		PV pvntime = PVFactory.createPV(nsecName);// new EPICS_V3_PV(nsecName);
		try {
			pvntime.start();
			for(int i=0;i<NUM_OF_ITER;i++) {
			    if (pvntime.isConnected()) break;
			        Thread.sleep(THREAD_DELAY);
			}
			double[] dblTime = ((IDoubleValue) pvntime.getValue()).getValues();
			if(dblTime.length != dim ) {
				System.out.println(nsecName+
						"warning: valArr-dimension ("+dblTime.length+") not equal NVAL-dim ("+dim+")");
				dim =Math.min(dim,dblTime.length);
				if( (len=dim) <= 1) {
					System.out.println("bad dimension for archiveRecord="+len);
					return -1;
				}
			}
			nsecArr = new long[dim];
			for(int i=0;i<dblTime.length;i++) {
	        	nsecArr[i]= (int) dblTime[i];
	        }
			pvntime.stop();
		} catch (InterruptedException e) {
			System.out.println ("Bad CAGET-command for archive field " + nsecName +" InterruptedException:");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println ("Bad CAGET-command for archive field " + nsecName +" Exception:");
			e.printStackTrace();
		}
		
		// get sevr field:
		PV sevrname = PVFactory.createPV(sevrName);//new EPICS_V3_PV(sevrName);
		try {
			sevrname.start();
			for(int i=0;i<NUM_OF_ITER;i++) {
			    if (sevrname.isConnected()) break;
			        Thread.sleep(THREAD_DELAY);
			}
			
			long[] dblTime = ((ILongValue	) sevrname.getValue()).getValues();
			if(dblTime.length != dim ) {
				System.out.println(sevrName+
						"warning: valArr-dimension ("+dblTime.length+") not equal NVAL-dim ("+dim+")");
				dim =Math.min(dim,dblTime.length);
				if( (len=dim) <= 1) {
					System.out.println("bad dimension for archiveRecord="+len);
					return -1;
				}
			}
			sevrArr = new long[dim];
			for(int i=0;i<dblTime.length;i++) {
	        	sevrArr[i]= (long) dblTime[i];
	        }
			sevrname.stop();
		} catch (InterruptedException e) {
			System.out.println ("Bad CAGET-command for archive field " + sevrName +" InterruptedException:");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println ("Bad CAGET-command for archive field " + sevrName +" Exception:");
			e.printStackTrace();
		}
		return dim;
	}	
	private String addSuffix(String PV,String suffix) {
		return new String(PV+suffix);
	}		
	public double[] getVal() {return valArr; }
	public long[] getTime()  {return timeArr; }
	public long[] getNsec()  {return nsecArr; }
	public long[] getSevr()  {return sevrArr; }
	public int getDim() {return dim;}
} // eof class ArchiveRecord
