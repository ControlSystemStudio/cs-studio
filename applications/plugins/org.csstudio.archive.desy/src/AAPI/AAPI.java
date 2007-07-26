package AAPI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

//
// Main class for AAPI package
//
// AAPI-client use AAPI-protocol which was development
// in discussion between DESY and Kay Kasemir(LANL)
// see http://www-kryo.desy.de/documents/EPICS/DESY/General/Archiver/AAPI/
// for details.

public class AAPI {
	public static String		AAPI_HOST					= "krynfs.desy.de";

	public static int			AAPI_PORT					= 4053;
	public final static String dummyDescription = "AAPI server DESY Hamburg Germany";
	// AAPI Command number enum:
	public final static int		VERSION_CMD					= 1;

	public final static int		DATA_REQUEST_CMD			= 2;

	public final static int		CHANNEL_INFO_CMD			= 3;

	public final static int		CHANNEL_LIST_CMD			= 4;

	public final static int		HIERARCHY_CHANNEL_LIST_CMD	= 5;

	public final static int		FILTER_LIST_CMD				= 6;
	
	public final static int		REGEXP_LIST_CMD				= 7;
    public final static int     HIERARCHY_SKELETON_CMD      = 8;

	public final static int		MAX_FUNCT_NUMBER			= HIERARCHY_SKELETON_CMD;

	// Data reduction (filtering) methods for cmd=DATA_REQUEST_CMD :
	public final static int		AVERAGE_METHOD				= 1;//Albert was 1
	public final static int		TAIL_RAW_METHOD				= 2;
	public final static int		SHARP_METHOD				= 3;
	public final static int		SPLINE_METHOD				= 4; //Albert was 4
	public final static int		FFT_METHOD					= 5;
	public final static int		NO_FILTERING_METHOD			= 6;
	public final static int		MIN_MAX_AVERAGE_METHOD		= 7;
	public final static int		LINEAR_INT_METHOD			= 8;
	public final static int		STEP_FUNCTION_METHOD		= 9;
	public final static int		LINEAR_AND_STEP_METHOD		= 10;
	public final static int		MAX_METHOD_NUMBER			= LINEAR_AND_STEP_METHOD;
	
	public final static int		AVERAGE_M			= 1;//Albert was 1
	public final static int		NO_FILTERING_M		= 6;
	public final static int		MIN_MAX_AVERAGE_M	= 7;
	public final static int		SHARP_M			    = 3;
	public final static int		SPLINE_M			= 4; //Albert was 1
	public final static int		FFT_M				= 5;
	public final static int		TAIL_RAW_M			= 2;
	
	

//	 Next stringArrray corespondent with data reduction methods list:
	
	//19.1.07 AVERAGE changed to plot-binning
//	public final static String requestedTypeList[]={"plot-binning","RAW","SHARP",
//		"SPLINE","FFT","NO_FILTERING","MIN_MAX_AVERAGE","LINEAR_INT","STEP_FUNCTION","LINEAR_AND_STEP"};
	
//	public final static String requestedTypeList[]={
//		"AVERAGE","RAW","MIN_MAX_AVERAGE","SHARP","SPLINE"};
	public final static String requestedTypeList[]={
		"SHARP","RAW","MIN_MAX_AVERAGE","AVERAGE","SPLINE"};		
	public final static String severityList[]={"NO_ALARM","MINOR","MAJOR","INVALID","UNDEF"};
	public final static String alarmStatusString[]={
			"NO_ALARM",  // This list coming from  $Epics/base/include/alarmString.h
			"READ",
			"WRITE",
			"HIHI",
			"HIGH",
			"LOLO",
			"LOW",
			"STATE",
			"COS",
			"COMM",
			"TIMEOUT",
			"HWLIMIT",
			"CALC",
			"SCAN",
			"LINK",
			"SOFT",
			"BAD_SUB",
			"UDF",
			"DISABLE",
			"SIMM",
			"READ_ACCESS",
			"WRITE_ACCESS", "UNDEF"};
	public final static int TCP_READ_ERROR          =1;
	public final static int NO_MEMORY               =2;
	public final static int BAD_CMD_NUM             =3;
	public final static int CANT_READ_LENGTH        =4;
	public final static int CANT_READ_PACKET        =5;
	public final static int DESERIALISATION_PROBLEM =6;
	public final static int BAD_CMD                 =7;
	public final static int CANT_DESERIAL           =8;
	public final static int ADD_HEADER_PROBLEM      =9;
	public final static int BAD_DATA_HANDLE         =10;
	public final static int NO_SUCH_METHOD          =11;
	public final static int FROM_MORE_THEN_TO       =12;
	public final static int BAD_MAX_NUM             =13;
	public final static int BAD_AVER_METHOD         =14; 
	public final static int BAD_TIME                =15; 
	public final static int CAN_T_OPEN_FILE         =16; 
	public final static int BAD_FGETS               =17; 
	public final static int BAD_HR_FILE             =18; 
	public final static int BAD_GET_CHANNEL_INFO    =19;  
	public final static int BAD_GET_CHANNEL_LIST    =20; 
	public final static int BAD_GET_HIERARCHY       =21;
	public final static int BAD_RAW_METHOD          =22; 
	public final static int BAD_GET_FILTER_LIST     =23;
	public final static int BAD_NO_FILTER_METHOD    =24; 
	public final static int NO_FILTER_BIG           =25;
	public final static int BAD_FFT_METHOD          =26;
	public final static int BAD_GET_REG_EXP         =27;
	public final static int BAD_GET_SKELETON_INFO   =28; 
	public final static int BAD_MMA_METHOD          =29;
	public final static int MAX_SERVER_ERR          =BAD_MMA_METHOD+1;
	
	public final static String aapiServerSideErrorString[]={	
	"NO ERROR",
	"TCP_READ_ERROR: Try (re)start server or Network Problem",
	"NO_MEMORY:No enough memory in server side",
	"BAD_CMD_NUM:Server or Network Problem",
	"CANT_READ_LENGTH:Server or Network Problem",
	"CANT_READ_PACKET:Server or Network Problem:",
	"DESERIALISATION_PROBLEM:Server or Network Problem",
	"BAD_CMD:Server or Network Problem",
	"CANT_DESERIAL:Server or Network Problem",
	"ADD_HEADER_PROBLEM:Server or Network Problem",
	"BAD_DATA_HANDLE:Server Problem",
	"NO_SUCH_METHOD:Server Problem",
	"FROM_MORE_THEN_TO:Bad region choosen",
	"BAD_MAX_NUM: Problem with number of points in server side",
	"BAD_AVER_METHOD:Server Problem with average Method",
	"BAD_TIME:Bad time region choose",
	"CAN_T_OPEN_FILE:Server side file reading problem",
	"BAD_FGETS:Server side file reading problem",
	"BAD_HR_FILE:Server side file reading problem",
	"BAD_GET_CHANNEL_INFO:Server side get info about channels problem",
	"BAD_GET_CHANNEL_LIST:Server side get list of channels problem",
	"BAD_GET_HIERARCHY:Server side get hierarchy list of channels problem",
	"BAD_RAW_METHOD:Server Problem with raw Method",
	"BAD_GET_FILTER_LIST:Server side get list of method problem",
	"BAD_NO_FILTER_METHOD:Server Problem with RAW Method",
	"Server: Number of return points exeed limit, decrease plot region",
	"BAD_FFT_METHOD:Server Problem with FFT Method",
	"BAD_GET_REG_EXP:Server side get regExp list of channels problem",
	"BAD_GET_SKELETON_INFO:Server Problem with Get Skeleton Info Method",
	"BAD_MMA_METHOD:Server Problem with Min/MAX/Average Method"
	};
	
	
	// Next 10 varaibles not used in current version of AAPI
	//because only DOUBLE_DATA implemented, but agreement
	//about this reservation was created between DESY BESSY and LANL
	//Should used in next AAPI-generation
	public final static int		SHORT_TYPE					= 0x01;

	public final static int		FLOAT_TYPE					= 0x02;

	public final static int		ENUM_TYPE					= 0x03;

	public final static int		CHAR_TYPE					= 0x04;

	public final static int		LONG_TYPE					= 0x05;

	public final static int		DOUBLE_TYPE					= 0x06;

	public final static int		WF_FLAG						= 0x80;

	public final static int		NUMERIC_INFO				= 0x100;

	public final static int		ENNUMERATED_INFO			= 0x101;

	public final static double			DEADBAND_PARAM				= 0.0;

	final static int			errorTag					= 0;

	final static int			AAPI_VERSION				= 0x22;

	final static int			headerSize					= 4;

	final static int			byteSize					= 4;

	final int					HEADER_LENGTH				= headerSize
																* byteSize;

	final static int			maxNameLength				= 1024;

	Socket						socket;

	String						host;

	int							port;
	private boolean debug=false;
	private boolean debugOut=false;
	private boolean debugUrgent=false;
	//
	// Dummy constructor
	//

	public AAPI(String Host, int Port) {
		host = Host;
		port = Port;
	}

	public void setPort(String port) {
		if(debugOut) System.out.println(port);
		if(port != null) {
			this.port = Integer.parseInt(port);
		}
	}

	public String getPort() {
		return new Integer(AAPI_PORT).toString();
	}

	public void setHost(String host) {
		if(debugOut)  System.out.println(host);
		if(host != null) {
			this.host = host;
		}
	}

	public String getHost() {
		return AAPI_HOST;
	}

	
	//
	//  get AAPI-server version #
	//

	public int getVersion() {
		int cmd = VERSION_CMD;
		int len = 0; //parameters length in bytes
		byte[] header, answer;
		if ((header = createConnectionAndHeader(cmd, len)) == null) return -1;
		if ((answer = sendReceivedPacket(header, null)) == null) return -1;
		return interpreterAnswer(answer);
	}
	//
	//  get AAPI-server description (storing localy)
	//

	public String getDescription() {
		return dummyDescription ;
	}
	//
	//  get AAPI-server Requested Types string aliases (storing localy)
	//
	public String[] getRequestedTypeList() {
		return requestedTypeList ;
	}
	
	//
	//  get AAPI-server Severity string aliases (storing localy)
	//
	public String getSeverityList(int i) {
		if((i<0)||(i>severityList.length - 1)) return severityList[severityList.length - 1] ;
		return severityList[i] ;
	}
	
	public int getMaxEpicsSeverity() {
		return severityList.length;
	}
	//
	//  get AAPI-server Status string aliases (storing localy)
	//
	public String getStatusList(int i) {
		if((i<0)||(i>alarmStatusString.length - 1)) 
			return alarmStatusString[alarmStatusString.length - 1] ;
		return alarmStatusString[i] ;
	}
	
	public int getMaxEpicsStatus() {
		return alarmStatusString.length;
	}
	

	                  
	//
	//  get control-system (i.e. EPICS) data from AAPI-server
	//   this is our main call.
	//

	public AnswerData getData(RequestData in) {
		if(debugOut) System.out.println(AAPI_HOST + AAPI_PORT);
		int cmd = DATA_REQUEST_CMD;
		if (debug){
		System.out.println("\n\nAAPI from="+ new java.util.Date ((long) ((long) (in.getFrom())) *1000 ) ) ; 
		System.out.println("AAPI to="+ new java.util.Date ( (long) ( (long)  in.getTo())*1000 )) ; 
		}
		int len = in.lenCalculate(); //parameters length in bytes
		byte[] header, answer;
		if ((header = createConnectionAndHeader(cmd, len)) == null)
																	return null;
		if ((answer = sendReceivedPacket(header, in.buildPacketFromData(cmd))) == null)
																						return null;
		AnswerData ret = interpreterAnswer(answer, cmd);
		return ret;
	}

	//
	//  get ChannelInfo about channel (not really used)
	//

	public AnswerChannelInfo getChannelInfo(String chName) {
		int cmd = CHANNEL_INFO_CMD;
		int len = chName.length(); //parameters length in bytes
		byte[] header, answer;
		if(debugOut) System.err.println("\t--getChannelInfo name= " + chName +";");
		if ((header = createConnectionAndHeader(cmd, len+1)) == null)
		  return null;
		if ((answer = sendReceivedPacket(header, buildPacketFromString(chName, cmd))) == null)	
		  return null;
		AnswerChannelInfo ret = interpreterAns(answer, cmd);
		return ret;
	}

	//
	//  get ChannelList It's huge! (use it carefully)
	//  In a future it's better to use next getHierarchyChannelList
	//

	public String[] getChannelList() {
		int cmd = CHANNEL_LIST_CMD;
		int len = 0; //parameters length in bytes
		byte[] header, answer;
		if ((header = createConnectionAndHeader(cmd, len)) == null)
																	return null;
		if ((answer = sendReceivedPacket(header, null)) == null) return null;
		return interpreterAnswerAsString(answer);
	}

	//
	//  get ChannelListHierarchy
	//

	public String[] getChannelListHierarchy(String node) {
		int cmd = HIERARCHY_CHANNEL_LIST_CMD;
		int len = node.length(); //parameters length in bytes
		byte[] header, answer;
		if ((header = createConnectionAndHeader(cmd, len)) == null)
																	return null;
		if ((answer = sendReceivedPacket(header, buildPacketFromString(node,
																		cmd))) == null)
																						return null;
		return interpreterAnswerAsString(answer);
	}

	//
	//  getAlgoritmsList. Do not forget that also
	//  full description should return, see doc. in www-kryo.desy.de
	//

	public String[] getAlgoritmsList() {
		int cmd = FILTER_LIST_CMD;
		int len = 0; //parameters length in bytes
		byte[] header, answer;
		if ((header = createConnectionAndHeader(cmd, len)) == null)
																	return null;
		if ((answer = sendReceivedPacket(header, null)) == null) return null;
		return interpreterAnswerAsDoubleString(answer);
	}
	
	//added by nejc.kosnik@cosylab.com
	public String[] getRegExpChannelList(String regExp) {
		int cmd = REGEXP_LIST_CMD;
		int len = regExp.length(); //parameters length in bytes
		byte[] header, answer;
		if(debugUrgent) System.out.println("getRegExpChannelList.reg=" + regExp);
		if ((header = createConnectionAndHeader(cmd, len+1)) == null)
																	return null;
		if ((answer = sendReceivedPacket(header, buildPacketFromString(regExp,
																		cmd))) == null)
																						return null;
		return interpreterAnswerAsString(answer);
	}
	

	//
	// Preparing byteArray package from String for sending over TCP/IP
	//

	public byte[] buildPacketFromString(String str, int cmd) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			char[] strArr = str.toCharArray(); // and name as string second
			for (int i = 0; i < strArr.length; i++)
				dout.writeByte(strArr[i]);
			dout.writeChar('\0');
			// return the underlying byte array
			return bout.toByteArray();

		} catch (IOException e) {
			System.err.println("AAPI-client send error" + e);
			return null;
		}
	}

	//
	// TCP/IP connction
	//

	boolean connect() {
		try {
			socket = new Socket(host, port);
			return true;
		} catch (Exception e) {
			System.err.println("AAPI-server connection error " + e);
			socket = null;
			return false;
		}
	}

	//
	// TCP/IP data sending
	//

	boolean write(byte[] data) {
		try {
			OutputStream to_server = socket.getOutputStream();
			to_server.write(data);
			return true;
		} catch (Exception e) {
			System.err.println("AAPI-server send error" + e);
			return false;
		}
	}

	//
	// TCP/IP data receiving
	//

	byte[] receive() {
		byte[] data;
		try {
			byte[] firstLength = new byte[4];
			InputStream in = socket.getInputStream();
			int err;
			if ((err = in.read(firstLength)) <= 0) {
				System.err.println("Read Socket Error");
				return null;
			}

			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							firstLength));
			int num = readStream.readInt();
			if (num <= 0) {
				System.err.println("AAPI packet length is negative" + num);
				return null;
			}

			num -=4; // Exclude header
			data = new byte[num];
			int count=0;
			int availableBytes=8*1024;  // 8K per buff
			byte [] lastByte = new byte [availableBytes];

			int needLen=num;
			while (needLen > 0) {
			  if ((err = in.read(lastByte)) <= 0) {
			    System.err.println("Read Socket Error");
			    return null;
			  }
		      	  if (err > availableBytes) {
			    System.err.println("Read Socket Error overread");
			    return null;
			  }
			  needLen-=err;
			  for(int i=0;i<err;i++) data[count++]=lastByte[i];
			}

			in.close();
			if(count != num) {
			  System.err.println("Warn: ReadSocket incomplete?");
			  System.err.println("SZ="+count);
			  System.err.println("NM="+num);
			}

		} catch (Exception e) {
			System.err.println("AAPI-server received error" + e);
			return null;
		}
		return data;
	}

	//
	//Typical client-server request/answer function:
	//

	byte[] sendReceivedPacket(byte[] header, byte[] data) {
		if (!write(concatination(header, data))) return null;
		byte[] rawAnswer;
		if ((rawAnswer = receive()) == null) return null;
		return analyzeReturnHeader(rawAnswer);
	}

	//
	// AAPI first 4 bytes is header. Create it and try open TCP connection:
	//

	byte[] createConnectionAndHeader(int cmd, int len) {
		if (connect()) {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeInt(HEADER_LENGTH + len);
				dout.writeInt(cmd);
				dout.writeInt(errorTag);
				dout.writeInt(AAPI_VERSION);
				return bout.toByteArray();
			} catch (IOException e) {
				System.err
							.println("AAPI-client createConnectionAndHeader error"
										+ e);
				return null;
			}
		}
		return null;
	}

	//
	// AAPI first 4 bytes is header. Analyze it!
	//

	byte[] analyzeReturnHeader(byte[] rawAnswer) {
		try {
			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							rawAnswer));
			int cmd = readStream.readInt();
			int err = readStream.readInt();
			int ver = readStream.readInt();
			if (err != 0) {
				System.err.println("AAPI error: "+new String(rawAnswer));
				return null;
			}
			return rawAnswer;
		} catch (IOException e) {
			System.err.println("AAPI-client analyzeReturnHeader error" + e);
			return null;
		}
	}

	//
	// If answer is int (version only) recovery it from bytes
	//

	int interpreterAnswer(byte[] data) {
		try {
			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							data));
			for (int i = 0; i < headerSize - 1; i++)
				readStream.readInt();//Skip header
			int ver = readStream.readInt();
			return ver;
		} catch (IOException e) {
			System.err.println("AAPI-client interpreterAnswererror" + e);
			return -1;
		}
	}

	//
	// If answer is DataStructure (get_Data only) recovery it from bytes
	//

	AnswerData interpreterAnswer(byte[] data, int cmd) {
		try {
			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							data));
			for (int i = 0; i < headerSize - 1; i++)
				readStream.readInt();//Skip header
			AnswerData answerClass = new AnswerData();
			if ((answerClass.analyzeAnswer(data, cmd)) == null) return null;
			if(debug) {
		        System.out.println("count="+answerClass.getCount());
		        for(int j=0;j<answerClass.getCount();j++) {
		        	if(j != answerClass.getCount() -1  ) {
		        		if (j >3)continue;
		        	}
 		            System.out.print(j+" T U S D="+ new java.util.Date ((long)  1000*( (long)  answerClass.getTime()[j]))) ; 
		            System.out.print("; "+answerClass.getUtime()[j]); 
		            System.out.print("; "+answerClass.getStatus()[j]);
		            System.out.print("; "+answerClass.getData()[j] + " \n"); 
		        }
		        System.out.println("prec=" + answerClass.getPrecision());
		        System.out.println("MAX="  + answerClass.getDisplayHigh()); 
		        System.out.println("MIN="  + answerClass.getDisplayLow());
		        System.out.println("HIGH=" + answerClass.getHighAlarm());
		        System.out.println("HIHI=" + answerClass.getHighWarning());
		        System.out.println("LOW="  + answerClass.getLowAlarm());
		        System.out.println("LOLO=" + answerClass.getLowWarning());
		        System.out.println("EGU = "+ answerClass.getEgu());         
			}
			return answerClass;
		} catch (IOException e) {
			System.err.println("AAPI-client interpreterAnswererror" + e);
			return null;
		}
	}

	//
	// If answer is ChannelInfoStructure (get_ChannelInfo only) recovery it from
	// bytes
	//

	 AnswerHierarchySkeleton interpreterAnswerAsHierarchySkeleton(byte[] data, int cmd) {
		try {
			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							data));
			for (int i = 0; i < headerSize - 1; i++)
				readStream.readInt();//Skip header
			 AnswerHierarchySkeleton answerClass = new AnswerHierarchySkeleton();
			if ((answerClass.analyzeAnswer(data, cmd)) == null) return null;
			return answerClass;
		} catch (IOException e) {
			System.err.println("AAPI-client interpreterAnswererror" + e);
			return null;
		}
	}

	AnswerChannelInfo interpreterAns(byte[] data, int cmd) {
		try {
			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							data));
			for (int i = 0; i < headerSize - 1; i++)
				readStream.readInt();//Skip header
			AnswerChannelInfo answerClass = new AnswerChannelInfo();
			if ((answerClass.analyzeAnswer(data, cmd)) == null) return null;
			return answerClass;
		} catch (IOException e) {
			System.err.println("AAPI-client interpreterAnswererror" + e);
			return null;
		}
	}



	//
	// If answer is String[] (getChannelList, getHierarchy) recovery it from
	// bytes
	//

	String[] interpreterAnswerAsString(byte[] data) {
		try {
			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							data));
			for (int i = 0; i < headerSize - 1; i++) {
				readStream.readInt();//Skip header
			}
			int num = readStream.readInt(); // # of strings
			if (num <= 0) {
				System.err.println("AAPI-client interpreterAnswererror\n");
				return null;
			}
			ArrayList ret=new ArrayList();
			char[] answerAsArray = new char[maxNameLength];
			int j;
			for (int i = 0; i < num; i++) {
				for (j = 0; j < maxNameLength; j++) {
					if ((answerAsArray[j] = (char)readStream.readByte()) == '\0')
																					break;
					if (answerAsArray[j] == '\n') { // record with unexpected
													// carridgeReturn in the end
						answerAsArray[j] = '\0';
						readStream.readByte();
						break;
					}
				}
				if (j > (maxNameLength - 2)) {
					System.err.println(i + "-th name is unbelieveable long ="
										+ new String(answerAsArray));
					return null;
				}
				if (!(answerAsArray[0]==(char)0 || answerAsArray[1]==(char)0)) {
					ret.add(toString(answerAsArray));
				}
			}
			String[] retArr = new String[ret.size()];
			ret.toArray(retArr);
			if (debugOut) {
				for(int i=0; i<ret.size(); i++) 
			        System.out.println("\t"+i+"-th channelName is="+retArr[i]+";");
			}
			
			return retArr;

		} catch (IOException e) {
			System.err.println("AAPI-client interpreterAnswererror" + e);
			return null;
		}
	}

	//
	// If answer is String[]+String[] (getAlgorithmsList) recovery it from bytes
	//

	String[] interpreterAnswerAsDoubleString(byte[] data) {
		try {
			DataInputStream readStream = new DataInputStream(
																new ByteArrayInputStream(
																							data));
			for (int i = 0; i < headerSize - 1; i++)
				readStream.readInt();//Skip header
			int num = readStream.readInt(); // # of strings
			if (num <= 0) {
				System.err.println("AAPI-client interpreterAnswer error");
				return null;
			}
			num *= 2; // Double it
			String[] ret = new String[num];
			char[] answerAsArray = new char[maxNameLength];
			int j;
			for (int i = 0; i < num; i++) {
				for (j = 0; j < maxNameLength; j++) {
					if ((answerAsArray[j] = (char)readStream.readByte()) == '\0')
																					break;

				}
				if (j > (maxNameLength - 2)) {
					System.err.println(i + "-th name is unbelieveable long ="
										+ new String(answerAsArray));
					return null;
				}
				ret[i] = toString(answerAsArray);
			}
			return ret;

		} catch (IOException e) {
			System.err.println("AAPI-client interpreterAnswererror" + e);
			return null;
		}
	}
	
	private String toString(char[] array) {
				String cur = new String(array).trim();
				int zIdx=cur.indexOf(0);
				if (zIdx>=0) {
					cur=cur.substring(0,zIdx);
				}
				return cur;
	}

	//
	// Misc. function a + b for byteArrays :
	//
	byte[] concatination(byte[] a, byte[] b) {
		if (a == null) return null;
		if (b == null) return a;
		byte[] ret = new byte[a.length + b.length];
		int i, j;
		for (i = 0; i < a.length; i++)
			ret[i] = a[i];
		for (j = 0; j < b.length; j++)
			ret[i + j] = b[j];
		return ret;
	}

	public  AnswerHierarchySkeleton getHierarchySkeleton() {
		int cmd = HIERARCHY_SKELETON_CMD;
		int len = 0; //parameters length in bytes
		byte[] header, answer;
		if ((header = createConnectionAndHeader(cmd, len)) == null) return null;
		if ((answer = sendReceivedPacket(header, null)) == null) return null;
		return interpreterAnswerAsHierarchySkeleton(answer,cmd);
	}

} // eof class AAPI
