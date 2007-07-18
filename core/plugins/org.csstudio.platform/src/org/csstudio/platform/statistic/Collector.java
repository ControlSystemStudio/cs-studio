package org.csstudio.platform.statistic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class Collector {
	private static 	Collector 	thisCollector = null;
	
	private AlarmHandler alarmHandler = null;
	private BackgroundCollector	dummyBackgroundCollector = null;
	
	private Double 		count 			= 0.0;
	private StoredData	actualValue		= null;
	private StoredData	lowestValue		= null;
	private StoredData	highestValue	= null;
	private Double		totalSum		= 0.0;
	private Double		meanValueAbsolute = null;
	private	Double		meanValuerelative = null;
	private Double		meanValueRelativeFactor = 20.0;
	private String		descriptor		= "desc. not set";
	private String		application		= "applic. not set";
	private boolean		continuousPrint	= true;
	private Double		continuousPrintCount = 100.0;
	private String		info = "info not defined";
	private Double		hardLimit = -1.0;


	public Double getHardLimit() {
		return hardLimit;
	}
	
	public String getHardLimitAsString() {
		if ( hardLimit == -1.0) {
			return "not set";
		} else {
			return hardLimit.toString();
		}
	}
	
	public void setHardLimit(int hardLimit) {
		this.hardLimit = new Double(hardLimit);
	}

	public void setHardLimit(Double hardLimit) {
		this.hardLimit = hardLimit;
	}

	public Collector () {
		/*
		 * initialize
		 */
		actualValue = new StoredData( 0.0);
		lowestValue = new StoredData( 1.0E99);
		highestValue = new StoredData( 1.0E-99);
		/*
		 * add entry to CollectorSupervisor
		 */
		CollectorSupervisor.getInstance().addCollector( this);
		/*
		 * add alarm handler
		 */
		alarmHandler = new AlarmHandler();
		
		/*
		 * if background collector has not been started yet - do so
		 */
		if ( dummyBackgroundCollector == null) {
			//dummyBackgroundCollector = BackgroundCollector.getInstance();
		}
	}
	
	/*
	 * TODO - delete !?
	 * getInstance not necessary!
	 * every collected entry needs it's own collector
	 */
	public static Collector getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisCollector == null) {
			synchronized (Collector.class) {
				if (thisCollector == null) {
					thisCollector = new Collector();
				}
			}
		}
		/*
		 * add entry to CollectorSupervisor
		 */
		CollectorSupervisor.getInstance().addCollector( thisCollector);
		return thisCollector;
	}
	
	public void setValue ( Double value) {
		/*
		 * set value
		 */
		incrementCount();
		sumUpTotalSum ( value);
		actualValue.setValue(value);
		actualValue.setTime( new GregorianCalendar());
		actualValue.setCount(getCount());
		actualValue.setInfo(info);
		if ( lowestValue.getValue() > value) {
			lowestValue.setValue(value);
			lowestValue.setCount(getCount());
			lowestValue.setActualTime();
			lowestValue.setInfo(info);
		}
		if ( highestValue.getValue() < value) {
			highestValue.setValue(value);
			highestValue.setCount(getCount());
			highestValue.setActualTime();
			highestValue.setInfo(info);
		}
		setMeanValueAbsolute(getTotalSum()/getCount());
		if ( getCount() > 1) {
			/*
			 * avoid devide by zero
			 */
			if ( getCount() < getMeanValueRelativeFactor()) {
				setMeanValuerelative( getMeanValuerelative() * (getCount() - 1.0)/ getCount() + value/getCount());
			} else {
				setMeanValuerelative( getMeanValuerelative() * (getMeanValueRelativeFactor() - 1.0)/ getMeanValueRelativeFactor() + value/getMeanValueRelativeFactor());
			}
		} else {
			setMeanValuerelative( value);
		}
		/*
		 * performa alarm checking
		 */
		alarmHandler.process(value, this);
		/*
		 * continuous printing
		 */
		if ( isContinuousPrint() && ((int)(getCount()%getContinuousPrintCount())==0)) {
			continuousPrinter();
		}
	}
	
	public void setValue ( int value) {
		Double newValue = new Double(value);
		setValue ( newValue);
	}
	
	public void incrementValue () {
		/*
		 * use synchronized method
		 */
		incDecValue ( true);
	}
	
	public void decrementValue () {
		/*
		 * use synchronized method
		 */
		incDecValue ( false);
	}
	
	private synchronized void incDecValue ( boolean increment) {
		if ( increment) {
			Double newValue = actualValue.getValue();
			newValue++;
			setValue ( newValue);
		} else {
			Double newValue = actualValue.getValue();
			newValue--;
			setValue ( newValue);
		}
	}
	
	public void continuousPrinter () {
		
		System.out.println ("\nApplication: " + getApplication());
		System.out.println ("Descriptor: " + getDescriptor());
		System.out.println ("Counter: " + getCount());
		System.out.println ("Actual value: " + getActualValue().getValue() + " \tDate: " + dateToString(getActualValue().getTime()) + " \tCount: " + getActualValue().getCount() + "Info: " + getActualValue().getInfo());
		System.out.println ("Highest Value: " + getHighestValue().getValue() + " \tDate: " + dateToString(getHighestValue().getTime()) + " \tCount: " + getHighestValue().getCount()  + "Info: " + getHighestValue().getInfo());
		System.out.println ("Lowest Value: " + getLowestValue().getValue() + " \tDate: " + dateToString(getLowestValue().getTime()) + " \tCount: " + getLowestValue().getCount()  + "Info: " + getLowestValue().getInfo());
		System.out.println ("Mean Value abs: " + getMeanValueAbsolute());
		System.out.println ("Mean Value rel.: " + getMeanValuerelative());
		System.out.println ("Alarm Limit (abs) : " + getAlarmHandler().getHighAbsoluteLimit());
		System.out.println ("Alarm Limit (rel) : " + getAlarmHandler().getHighRelativeLimit());
		System.out.println ("Hard Limit : " + getHardLimitAsString());
	}
	
	public String getCollectorStatus () {
		
		String result = "";
		
		result += ("\n\nApplication: " + getApplication());
		result += ("\nDescriptor: " + getDescriptor());
		result += ("\nCounter: " + getCount());
		result += ("\nActual value: " + getActualValue().getValue() + " \tDate: " + dateToString(getActualValue().getTime()) + " \tCount: " + getActualValue().getCount() + "Info: " + getActualValue().getInfo());
		result += ("\nHighest Value: " + getHighestValue().getValue() + " \tDate: " + dateToString(getHighestValue().getTime()) + " \tCount: " + getHighestValue().getCount() + "Info: " + getHighestValue().getInfo());
		result += ("\nLowest Value: " + getLowestValue().getValue() + " \tDate: " + dateToString(getLowestValue().getTime()) + " \tCount: " + getLowestValue().getCount() + "Info: " + getLowestValue().getInfo());
		result += ("\nMean Value abs: " + getMeanValueAbsolute());
		result += ("\nMean Value rel.: " + getMeanValuerelative());
		result += ("Alarm Limit (abs) : " + getAlarmHandler().getHighAbsoluteLimit());
		result += ("Alarm Limit (rel) : " + getAlarmHandler().getHighRelativeLimit());
		result += ("Hard Limit : " + getHardLimitAsString());
		return result;
	}
	
public String getCollectorStatusAsXml () {
		/*
		 * the big TODO!
		 * create XML output in order to pass it over XML to another CSS instance
		 */
		String result = "<TODO XML start string - nothing defined so far!/>";
		
		result += ("\n<Application>" + getApplication());
		result += ("\n<Descriptor>" + getDescriptor());
		result += ("\n<Counter>" + getCount());
		result += ("\n<Actual value>" + getActualValue().getValue() + " \tDate: " + dateToString(getActualValue().getTime()) + " \tCount: " + getActualValue().getCount() + "Info: " + getActualValue().getInfo());
		result += ("\n<Highest Value>" + getHighestValue().getValue() + " \tDate: " + dateToString(getHighestValue().getTime()) + " \tCount: " + getHighestValue().getCount() + "Info: " + getHighestValue().getInfo());
		result += ("\n<Lowest Value>" + getLowestValue().getValue() + " \tDate: " + dateToString(getLowestValue().getTime()) + " \tCount: " + getLowestValue().getCount() + "Info: " + getLowestValue().getInfo());
		result += ("\n<Mean Value abs>" + getMeanValueAbsolute());
		result += ("\n<Mean Value rel>" + getMeanValuerelative());
		// more ..
		result += ("\n<Hard Limit>" + getHardLimitAsString());
		return result;
	}

	public StoredData getActualValue() {
		return actualValue;
	}

	public void setActualValue(StoredData actualValue) {
		this.actualValue = actualValue;
	}

	public void incrementCount() {
		count ++;
	}
	
	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
		alarmHandler.setDescriptor(descriptor);
	}

	public StoredData getHighestValue() {
		return highestValue;
	}

	public void setHighestValue(StoredData highestValue) {
		this.highestValue = highestValue;
	}

	public StoredData getLowestValue() {
		return lowestValue;
	}

	public void setLowestValue(StoredData lowestValue) {
		this.lowestValue = lowestValue;
	}

	public Double getMeanValueAbsolute() {
		return meanValueAbsolute;
	}

	public void setMeanValueAbsolute(Double meanValueAbsolute) {
		this.meanValueAbsolute = meanValueAbsolute;
	}

	public Double getMeanValuerelative() {
		return meanValuerelative;
	}

	public void setMeanValuerelative(Double meanValuerelative) {
		this.meanValuerelative = meanValuerelative;
	}

	public Double getMeanValueRelativeFactor() {
		return meanValueRelativeFactor;
	}

	public void setMeanValueRelativeFactor(Double meanValueRelativeFactor) {
		this.meanValueRelativeFactor = meanValueRelativeFactor;
	}

	public Double getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(Double totalSum) {
		this.totalSum = totalSum;
	}
	
	public void sumUpTotalSum ( Double value) {
		this.totalSum = totalSum + value;
	}

	public boolean isContinuousPrint() {
		return continuousPrint;
	}

	public void setContinuousPrint(boolean continuousPrint) {
		this.continuousPrint = continuousPrint;
	}

	public Double getContinuousPrintCount() {
		return continuousPrintCount;
	}

	public void setContinuousPrintCount(Double continuousPrintCount) {
		this.continuousPrintCount = continuousPrintCount;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
		alarmHandler.setApplication(application);
	}

	public AlarmHandler getAlarmHandler() {
		return alarmHandler;
	}

	public void setAlarmHandler(AlarmHandler alarmHandler) {
		this.alarmHandler = alarmHandler;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public static String dateToString ( GregorianCalendar gregorsDate) {
		
		//
		// convert Gregorian date into string
		//
		//TODO: use other time format - actually : DD-MM-YYYY
		Date d = gregorsDate.getTime();
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
	    //DateFormat df = DateFormat.getDateInstance();
	    return df.format(d);
	}

}
