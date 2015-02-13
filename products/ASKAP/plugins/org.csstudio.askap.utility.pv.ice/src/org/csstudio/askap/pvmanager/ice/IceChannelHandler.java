package org.csstudio.askap.pvmanager.ice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.utility.icemanager.IceManager;
import org.csstudio.askap.utility.icemanager.MonitorPointListener;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayLong;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import askap.interfaces.TypedValue;
import askap.interfaces.TypedValueBool;
import askap.interfaces.TypedValueDouble;
import askap.interfaces.TypedValueDoubleSeq;
import askap.interfaces.TypedValueFloat;
import askap.interfaces.TypedValueFloatSeq;
import askap.interfaces.TypedValueInt;
import askap.interfaces.TypedValueIntSeq;
import askap.interfaces.TypedValueLong;
import askap.interfaces.TypedValueLongSeq;
import askap.interfaces.TypedValueString;
import askap.interfaces.TypedValueStringSeq;
import askap.interfaces.monitoring.MonitorPoint;

public class IceChannelHandler 
		extends MultiplexedChannelHandler<IceConnectionPayload, VType> 
		implements MonitorPointListener{

	private static final Logger logger = Logger.getLogger(IceChannelHandler.class.getName());

	String adaptorName = "";
	String pointName = "";
	
	IceConnectionPayload connectionPayload = new IceConnectionPayload();
	IceMessagePayload messagePayload = new IceMessagePayload();
	
	public IceChannelHandler(String channelName) {
		super(channelName);
		
		if (channelName!=null && channelName.trim().length()>0) {
			StringTokenizer tokenizer = new StringTokenizer(channelName, "/");
			
			if (tokenizer.countTokens()==2) {
				adaptorName = tokenizer.nextToken();
				pointName = tokenizer.nextToken();
				
				return;
			}
		}
		
	}

	@Override
	protected void connect() {
		try {
			IceManager.addPointListener(new String[]{pointName}, this, adaptorName);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not listen to " + adaptorName + "/" + pointName);
		}		
	}

	@Override
	protected void disconnect() {
		IceManager.removePointListener(new String[]{pointName}, this, adaptorName);
		connectionPayload.setIsConnected(false);
		processConnection(connectionPayload);
	}

	@Override
	protected void write(Object newValue, ChannelWriteCallback callback) {
        throw new UnsupportedOperationException("Can't write to ICE channel.");
	}

	@Override
	protected boolean isConnected(IceConnectionPayload payload) {
		return connectionPayload.isConnected();
	}

	@Override
	public void onUpdate(MonitorPoint point) {		
		if (!connectionPayload.isConnected()) {
			connectionPayload.setIsConnected(true);
			processConnection(connectionPayload);
		}
		
		VType value = convertToVType(point);		
		processMessage(value);
	}
	
	private VType convertToVType(MonitorPoint pointValue) {
		
		VType value = null;
		
		// timestamp
		long microSec = pointValue.timestamp;		
		long sec = (long) Math.floor(microSec/1000000);
		int nano = (int) (microSec*1000 - sec*1000000000);
		Timestamp timestamp = Timestamp.of(sec, nano);
		Time time = ValueFactory.newTime(timestamp);
		
		//severity
		Alarm alarm = null;
		switch (pointValue.status) {
			case OK:
				alarm = ValueFactory.newAlarm(AlarmSeverity.NONE, "NONE");
				break;
			case INVALID:
				alarm = ValueFactory.newAlarm(AlarmSeverity.INVALID, "INVALID");
				break;
			case MINORALARM:
				alarm = ValueFactory.newAlarm(AlarmSeverity.MINOR, "MINOR");
				break;
			case MAJORALARM:
				alarm = ValueFactory.newAlarm(AlarmSeverity.MAJOR, "MAJOR");
				break;
			default:
				alarm = ValueFactory.newAlarm(AlarmSeverity.NONE, "NONE");
				break;
		}
		
		
		/*
		 * point type
		 * 
		 * supported:
		 * ===============
		 * TypeNull, TypeFloat, TypeDouble, TypeInt, TypeLong,
         * TypeString, TypeBool,
         * TypeFloatSeq, TypeDoubleSeq, TypeIntSeq, TypeLongSeq,
         * TypeStringSeq,
         * 
         * Not supported:
         * ================
         *  TypeFloatComplex, TypeDoubleComplex,
         *  TypeDoubleComplexSeq, TypeFloatComplexSeq,
         *  TypeDirection, TypeDirectionSeq
         *  TypeBoolSeq
         * 
         */
		
		TypedValue typedValue = pointValue.value;		
		switch (typedValue.type) {
		case TypeNull:
			value = null;
			break;
		case TypeFloat:
			float fvalue = ((TypedValueFloat) typedValue).value;
			value = ValueFactory.newVDouble(new Double(fvalue), alarm, time, null);
			break;
		case TypeFloatSeq:
			float fvalues[] = ((TypedValueFloatSeq) typedValue).value;
			ArrayFloat floatList = new ArrayFloat(fvalues);			
			value = ValueFactory.newVFloatArray(floatList, alarm, time, null);
			break;
		case TypeDouble:
			double dvalue = ((TypedValueDouble) typedValue).value;
			value = ValueFactory.newVDouble(dvalue, alarm, time, null);
			break;
		case TypeDoubleSeq:
			double dvals[] = ((TypedValueDoubleSeq) typedValue).value;
			ArrayDouble doubleList = new ArrayDouble(dvals);
			value = ValueFactory.newVDoubleArray(doubleList, alarm, time, null);
					break;
		case TypeInt:
			int ivalue = ((TypedValueInt) typedValue).value;
			value = ValueFactory.newVInt(ivalue, alarm, time, null);
			break;
		case TypeIntSeq:
			int intvals[] = ((TypedValueIntSeq) typedValue).value;
			ArrayInt intList = new ArrayInt(intvals);
			value = ValueFactory.newVIntArray(intList, alarm, time, null);
			break;
		case TypeLong:
			long lvalue = ((TypedValueLong) typedValue).value;
			value = ValueFactory.newVNumber(new Long(lvalue), alarm, time, null);
			break;
		case TypeLongSeq:
			long lvalues[] = ((TypedValueLongSeq) typedValue).value;
			ArrayLong longList = new ArrayLong(lvalues);
			value = ValueFactory.newVNumberArray(longList, alarm, time, null);
			break;
		case TypeString:
			String strvalue = ((TypedValueString) typedValue).value;
			value = ValueFactory.newVString(strvalue, alarm, time);
			break;
		case TypeStringSeq:
			String strvalues[] = ((TypedValueStringSeq) typedValue).value;
			List<String> strList = new ArrayList<String>();
			Arrays.asList(strvalues);
			value = ValueFactory.newVStringArray(strList, alarm, time);
			break;
		case TypeBool:
			boolean bvalue = ((TypedValueBool) typedValue).value;
			value = ValueFactory.newVBoolean(bvalue, alarm, time);
			break;
		default:
			break;
		}

		return value;
		
	}

	@Override
	public void disconnected(String pointName) {
		connectionPayload.setIsConnected(false);
		processConnection(connectionPayload);
	}
	
	
}
