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
 package org.csstudio.domain.common.statistic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class Collector {

    private AlarmHandler alarmHandler = null;
    private BackgroundCollector    dummyBackgroundCollector = null;

    private Double         count             = 0.0;
    private StoredData    actualValue        = null;
    private StoredData    lowestValue        = null;
    private StoredData    highestValue    = null;
    private Double        totalSum        = 0.0;
    private Double        meanValueAbsolute = null;
    //FIXME: Es muss sicher gestellt werde das meanValuerelative
    //     initalisiert wird. Entweder durch einen Default wert
    //     oder der Construtor muss angepasst werden.
    private    Double        meanValuerelative = null;
    private Double        meanValueRelativeFactor = 20.0;
    private String        descriptor        = "desc. not set";
    private String        application        = "applic. not set";
    private boolean        continuousPrint    = false;
    private Double        continuousPrintCount = 100.0;
    private String        info = "info not defined";
    private Double        hardLimit = -1.0;


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

    public void setHardLimit(final int hardLimit) {
        this.hardLimit = new Double(hardLimit);
    }

    public void setHardLimit(final Double hardLimit) {
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
        CollectorSupervisor.getInstance().addCollector( this); // FIXME (mclausen) : antipattern
        /*
         * add alarm handler
         */
        alarmHandler = new AlarmHandler();

        /*
         * if background collector has not been started yet - do so
         */
        if ( dummyBackgroundCollector == null) {
            dummyBackgroundCollector = BackgroundCollector.getInstance();
        }
    }

    public void setValue ( final Double value) {
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
        if ( (getMeanValuerelative() != null) && (getCount() > 1)) {
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

    public void setValue ( final int value) {
        final Double newValue = new Double(value);
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

    private synchronized void incDecValue ( final boolean increment) {
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
        System.out.println ("Actual value: " + getActualValue().getValue() + " \tDate Actual: " + dateToString(getActualValue().getTime()) + " \tCount Actual: " + getActualValue().getCount() + "Info Actual: " + getActualValue().getInfo());
        System.out.println ("Highest Value: " + getHighestValue().getValue() + " \tDate Highest: " + dateToString(getHighestValue().getTime()) + " \tCount Highest: " + getHighestValue().getCount()  + "Info Highest: " + getHighestValue().getInfo());
        System.out.println ("Lowest Value: " + getLowestValue().getValue() + " \tDate Lowest: " + dateToString(getLowestValue().getTime()) + " \tCount Lowest: " + getLowestValue().getCount()  + "Info Lowest: " + getLowestValue().getInfo());
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
        result += ("\nActual value: " + getActualValue().getValue() + " \tDate Actual: " + dateToString(getActualValue().getTime()) + " \tCount Actual: " + getActualValue().getCount() + "Info Actual: " + getActualValue().getInfo());
        result += ("\nHighest Value: " + getHighestValue().getValue() + " \tDate Highest: " + dateToString(getHighestValue().getTime()) + " \tCount Highest: " + getHighestValue().getCount() + "Info Highest: " + getHighestValue().getInfo());
        result += ("\nLowest Value: " + getLowestValue().getValue() + " \tDate Lowest: " + dateToString(getLowestValue().getTime()) + " \tCount Lowest: " + getLowestValue().getCount() + "Info Lowest: " + getLowestValue().getInfo());
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
        result += ("\n<Actual value>" + getActualValue().getValue() + " \tDate Actual: " + dateToString(getActualValue().getTime()) + " \tCount Actual: " + getActualValue().getCount() + "Info Actual: " + getActualValue().getInfo());
        result += ("\n<Highest Value>" + getHighestValue().getValue() + " \tDate Highest: " + dateToString(getHighestValue().getTime()) + " \tCount Highest: " + getHighestValue().getCount() + "Info Highest: " + getHighestValue().getInfo());
        result += ("\n<Lowest Value>" + getLowestValue().getValue() + " \tDate Lowest: " + dateToString(getLowestValue().getTime()) + " \tCount Lowest: " + getLowestValue().getCount() + "Info Lowest: " + getLowestValue().getInfo());
        result += ("\n<Mean Value abs>" + getMeanValueAbsolute());
        result += ("\n<Mean Value rel>" + getMeanValuerelative());
        // more ..
        result += ("\n<Hard Limit>" + getHardLimitAsString());
        return result;
    }

    public StoredData getActualValue() {
        return actualValue;
    }

    public void setActualValue(final StoredData actualValue) {
        this.actualValue = actualValue;
    }

    public void incrementCount() {
        count ++;
    }

    public Double getCount() {
        return count;
    }

    synchronized public void setCount(final Double count) {
        this.count = count;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(final String descriptor) {
        this.descriptor = descriptor;
        alarmHandler.setDescriptor(descriptor);
    }

    public StoredData getHighestValue() {
        return highestValue;
    }

    public void setHighestValue(final StoredData highestValue) {
        this.highestValue = highestValue;
    }

    public StoredData getLowestValue() {
        return lowestValue;
    }

    public void setLowestValue(final StoredData lowestValue) {
        this.lowestValue = lowestValue;
    }

    public Double getMeanValueAbsolute() {
        return meanValueAbsolute;
    }

    public void setMeanValueAbsolute(final Double meanValueAbsolute) {
        this.meanValueAbsolute = meanValueAbsolute;
    }

    public Double getMeanValuerelative() {
        return meanValuerelative;
    }

    public void setMeanValuerelative(final Double meanValuerelative) {
        this.meanValuerelative = meanValuerelative;
    }

    public Double getMeanValueRelativeFactor() {
        return meanValueRelativeFactor;
    }

    public void setMeanValueRelativeFactor(final Double meanValueRelativeFactor) {
        this.meanValueRelativeFactor = meanValueRelativeFactor;
    }

    public Double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(final Double totalSum) {
        this.totalSum = totalSum;
    }

    public void sumUpTotalSum ( final Double value) {
        this.totalSum = totalSum + value;
    }

    public boolean isContinuousPrint() {
        return continuousPrint;
    }

    public void setContinuousPrint(final boolean continuousPrint) {
        this.continuousPrint = continuousPrint;
    }

    public Double getContinuousPrintCount() {
        return continuousPrintCount;
    }

    public void setContinuousPrintCount(final Double continuousPrintCount) {
        this.continuousPrintCount = continuousPrintCount;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(final String application) {
        this.application = application;
        alarmHandler.setApplication(application);
    }

    public AlarmHandler getAlarmHandler() {
        return alarmHandler;
    }

    public void setAlarmHandler(final AlarmHandler alarmHandler) {
        this.alarmHandler = alarmHandler;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(final String info) {
        this.info = info;
    }

    /**
     * Convert Gregorian date into string.
     * actually format is yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param gregorsDate The Date to convert do default String format
     * @return The Date as String
     */
    public static String dateToString ( final GregorianCalendar gregorsDate) {
        final Date d = gregorsDate.getTime();
        final SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
        return df.format(d);
    }

}
