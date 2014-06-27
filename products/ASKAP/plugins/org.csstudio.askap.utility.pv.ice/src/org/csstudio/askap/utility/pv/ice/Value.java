package org.csstudio.askap.utility.pv.ice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.csstudio.askap.utility.icemanager.IceManager;
import org.csstudio.askap.utility.icemanager.MonitorPointListener;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

import askap.interfaces.TypedValue;
import askap.interfaces.TypedValueBool;
import askap.interfaces.TypedValueBoolSeq;
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

public class Value implements PV, MonitorPointListener {

	// Logger based on a name. Use current class name or plugin ID.
	private static final Logger logger = Logger.getLogger(Value.class.getName());

	private String adaptorName = "";
	private String pointName = "";
	private boolean isRunning = false;
	private IValue value = null;

	private boolean isConnected = false;
	
	private List<PVListener> listeners = new ArrayList<PVListener>();
	
	/**
	 * name should be in the formate of AdaptorName/PointName
	 * @param name
	 */
	public Value(String name) throws Exception {
		if (name!=null && name.trim().length()>0) {
			StringTokenizer tokenizer = new StringTokenizer(name, "/");
			
			if (tokenizer.countTokens()==2) {
				adaptorName = tokenizer.nextToken();
				pointName = tokenizer.nextToken();
				
				return;
			}
		}
		
		throw new Exception("Ice Monitor PV name is in wrong format.");
		
	}

	@Override
	public String getName() {
		return IceMonitorPVFactory.PREFIX + PVFactory.SEPARATOR + adaptorName + "/" + pointName;
	}

	@Override
	public IValue getValue(double timeout_seconds) throws Exception {
		return value;
	}

	@Override
	public void addListener(PVListener listener) {
		listeners.add(listener);
		// If we already have a value, inform new listener right away
		if (value != null)
			listener.pvValueUpdate(this);
	}

	@Override
	public void removeListener(PVListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void start() throws Exception {
		logger.info(getName() + " start()");

		IceManager.addPointListener(new String[]{pointName}, this, adaptorName);		
		isRunning = true;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public boolean isWriteAllowed() {
		return false;
	}

	@Override
	public String getStateInfo() {
		return isRunning ? "running" : "stopped";
	}

	@Override
	public void stop() {
		logger.info(getName() + " stop()");
		IceManager.removePointListener(new String[]{pointName}, this, adaptorName);		
		isRunning = false;
	}

	@Override
	public IValue getValue() {
		return value;
	}

	@Override
	public void setValue(Object new_value) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(MonitorPoint point) {
		isConnected = true;
		setIValue(point);
		
		for (Iterator<PVListener> iter = listeners.iterator(); iter.hasNext();) {
			PVListener listener = iter.next();
			listener.pvValueUpdate(this);
		}
	}

	protected void setIValue(MonitorPoint pointValue) {
		
		// timestamp
		long microSec = pointValue.timestamp;		
		long sec = (long) Math.floor(microSec/1000000);
		long nano = microSec*1000 - sec*1000000000;
		ITimestamp timestamp = TimestampFactory.createTimestamp(sec, nano);
		
		//severity
		ISeverity severity = null;
		switch (pointValue.status) {
			case OK:
				severity = ValueFactory.createOKSeverity();
				break;
			case INVALID:
				severity = ValueFactory.createInvalidSeverity();
				break;
			case MINORALARM:
				severity = ValueFactory.createMinorSeverity();
				break;
			case MAJORALARM:
				severity = ValueFactory.createMajorSeverity();
				break;
			default:
				severity = ValueFactory.createOKSeverity();
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
         * TypeStringSeq, TypeBoolSeq,
         * 
         * Not supported:
         * ================
         *  TypeFloatComplex, TypeDoubleComplex,
         *  TypeDoubleComplexSeq, TypeFloatComplexSeq,
         *  TypeDirection, TypeDirectionSeq
         * 
         */
		
		TypedValue typedValue = pointValue.value;		
		switch (typedValue.type) {
		case TypeNull:
			value = null;
			break;
		case TypeFloat:
			float fvalue = ((TypedValueFloat) typedValue).value;
			value = ValueFactory.createDoubleValue(timestamp, severity, "", null, Quality.Original, 
					new double[]{fvalue});
			break;
		case TypeFloatSeq:
			float fvalues[] = ((TypedValueFloatSeq) typedValue).value;
			double dvalues[] = new double[fvalues.length];
			for (int i=0; i<fvalues.length;i++) {
				dvalues[i] = fvalues[i];
			}
			value = ValueFactory.createDoubleValue(timestamp, severity, "", 
					null, Quality.Original, dvalues);
			break;
		case TypeDouble:
			double dvalue = ((TypedValueDouble) typedValue).value;
			value = ValueFactory.createDoubleValue(timestamp, severity, "", null, Quality.Original, 
					new double[]{dvalue});
			break;
		case TypeDoubleSeq:
			double dvals[] = ((TypedValueDoubleSeq) typedValue).value;
			value = ValueFactory.createDoubleValue(timestamp, severity, "", null, Quality.Original, dvals);
			break;
		case TypeInt:
			int ivalue = ((TypedValueInt) typedValue).value;
			value = ValueFactory.createLongValue(timestamp, severity, "", null, Quality.Original, 
					new long[]{ivalue});
			break;
		case TypeIntSeq:
			int intvals[] = ((TypedValueIntSeq) typedValue).value;
			long longVals[] = new long[intvals.length];
			for (int i=0; i<intvals.length; i++) {
				longVals[i] = intvals[i];
			}
			value = ValueFactory.createLongValue(timestamp, severity, "", null, Quality.Original, longVals);
			break;
		case TypeLong:
			long lvalue = ((TypedValueLong) typedValue).value;
			value = ValueFactory.createLongValue(timestamp, severity, "", null, Quality.Original, 
					new long[]{lvalue});
			break;
		case TypeLongSeq:
			long lvalues[] = ((TypedValueLongSeq) typedValue).value;
			value = ValueFactory.createLongValue(timestamp, severity, "", null, Quality.Original, lvalues);
			break;
		case TypeString:
			String strvalue = ((TypedValueString) typedValue).value;
			value = ValueFactory.createStringValue(timestamp, severity, null, Quality.Original, 
					new String[]{strvalue});
			break;
		case TypeStringSeq:
			String strvalues[] = ((TypedValueStringSeq) typedValue).value;
			value = ValueFactory.createStringValue(timestamp, severity, null, Quality.Original, strvalues);
			break;
		case TypeBool:
			boolean bvalue = ((TypedValueBool) typedValue).value;
			String bstr = (bvalue ? "true":"false");
			value = ValueFactory.createStringValue(timestamp, severity, null, Quality.Original, 
					new String[]{bstr});
			break;
		case TypeBoolSeq:
			boolean bvalues[] = ((TypedValueBoolSeq) typedValue).value;
			String vals[] = new String[bvalues.length];
			for (int i=0; i<bvalues.length; i++) {
				vals[i] = (bvalues[i] ? "true":"false");
			}
			value = ValueFactory.createStringValue(timestamp, severity, null, Quality.Original, vals);
			break;
		default:
			break;
		}
	}

	@Override
	public void disconnected(String pointName) {
		isConnected = false;
		
		// udpate all the listeners
		for (Iterator<PVListener> iter = listeners.iterator(); iter.hasNext();) {
			PVListener listener = iter.next();
			listener.pvDisconnected(this);
		}	
	}
}
