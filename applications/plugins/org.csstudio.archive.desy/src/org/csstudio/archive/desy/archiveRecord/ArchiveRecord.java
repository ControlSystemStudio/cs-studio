package org.csstudio.archive.desy.archiveRecord;
import de.desy.epics.singletonEpics.EpicsSingleton;
/** Handles the "archiveRecord" low-level staff 
 *  @author Albert Kagarmanov
 */
//
// Main class for ArchiveRecord package
//
public class ArchiveRecord {
	private final static String ARsuffix = "_h";    // Suffix for archiveRecord
	private final static String numStr = ".NVAL";   // Field for # of point
	private final static String valStr = ".VAL";    // Field for value
	private final static String timStr = ".TIM";    // Field for time in sec    
	private final static String nscStr = ".NSC";    // Field for time nanosec within last sec.
	private final static String sevrStr = ".SVYS";  // Field for severuty  
	private final static String flushStr = ".FLSH"; // Field for FLUSH archiveRecord
	private final static String pcabStr = ".PCAB";   // Field for PCAB archiveRecord (Absolute or Percent)
	private final static String rvarStr = ".RVAR";   // Field for RVAR (Diff. last-curr. in %)
	private final static String avarStr = ".AVAR";   // Field for AVAR (Diff. last-curr. abs)
	private String PVname;                           // PVname 
	private String archivePVname;                    //  archiveRecord
	private String nvalName;                         // Field for # of point
	private String valName;                          // Field for value
	private String timeName;                         //	 Field for time in sec    
	private String nsecName;                         // Field for time nanosec within last sec.
	private String sevrName;                          // Field for severuty
	private String flushName;                        // Field for FLUSH archiveRecord
	private String pcabName;                         // Field for PCAB archiveRecord (Absolute or Percent)
	private String rvarName;                         // Field for RVAR (Diff. last-curr. in %)
	private String avarName;                         // Field for AVAR (Diff. last-curr. abs)
	private int dim;                                 // length of all our arrays
	private double valArr[];                         // unsorted arary of values   
	private long  timeArr[];                         // unsorted arary of sec.
	private long  nsecArr[];                         // unsorted arary of nsec.
	private long  statArr[];                         // unsorted arary of status (dummy)
	private long  sevrArr[];                         // unsorted arary of severuty
    EpicsSingleton casing;
	
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
		casing = EpicsSingleton.getInstance();
	}
	
	public int getDimension() {
		dim = -1;
		String stringInt = casing.getValue(nvalName);	
		try {
			dim = Integer.parseInt(stringInt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println ("Bad Epics Integer for" + nvalName);
		}
 		return dim;
	}
	public int getAllFromCA()  { 
		// get VALUE field:
		String stringValue = casing.getValue(valName);
		String[] fieldsValue = stringValue.split(" ");
		int valueLen=fieldsValue.length;
		valArr = new double[valueLen]; 
		for (int i=0;i<valueLen;i++) {
			try {
				valArr[i]=Double.parseDouble(fieldsValue[i]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println ("Bad Epics Double for" + valName);
				return -1;
			}
		}
		
		// get TIME field:
		String stringTime = casing.getValue(timeName);
		String[] fieldsTime = stringTime.split(" ");
		int timeLen=fieldsTime.length;
		timeArr = new long[timeLen]; 
		for (int i=0;i<timeLen;i++) {
			try {
				timeArr[i]=(long) Double.parseDouble(fieldsTime[i]);
				(timeArr[i]) += 631152000L ;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println ("Bad Epics Long for" + timeName);
				return -1;
			}
		}
		// get Nsec field:
		String stringNsec = casing.getValue(nsecName);
		String[] fieldsNsec = stringNsec.split(" ");
		int nsecLen=fieldsNsec.length;
		nsecArr = new long[nsecLen]; 
		for (int i=0;i<nsecLen;i++) {
			try {
				nsecArr[i]=(long) Double.parseDouble(fieldsNsec[i]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println ("Bad Epics Long for" + nsecName);
				return -1;
			}
		}
		
		// get sevr field: Not implemented yet
		int sevrLen=dim;
		sevrArr = new long[sevrLen];
		for (int i=0;i<sevrLen;i++) sevrArr[i]=0;
		/*   Not implemented yet
		String stringSevr = casing.getValue(sevrName);
		String[] fieldsSevr = stringSevr.split(" ");
		int sevrLen=fieldsSevr.length;
		sevrArr = new long[sevrLen]; 
		for (int i=0;i<sevrLen;i++) {
			try {
				sevrArr[i]=Long.parseLong(fieldsSevr[i]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println ("Bad Epics Long for" + sevrName);
			}
		}
		*/
		
		int commMax = Math.max(dim,Math.max(valueLen,Math.max(timeLen, Math.max(nsecLen,sevrLen))));
		int commMin = Math.min(dim,Math.min(valueLen,Math.min(timeLen, Math.min(nsecLen,sevrLen))));
		if (commMax != commMin) {
			System.out.println (archivePVname+" different Dimmensions for " + nvalName+ " " + valName+ " " + timeName+ " " + nsecName+ " " + sevrName );
			System.out.println("is " +dim+ " " +valueLen+ " " +timeLen+ " " +nsecLen+ " " +sevrLen);
			return -1;  // TODO commMin ?
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
