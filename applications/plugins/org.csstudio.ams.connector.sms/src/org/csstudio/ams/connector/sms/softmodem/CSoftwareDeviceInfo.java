
// SMSLib for Java
// An open-source API Library for sending and receiving SMS via a GSM modem.
// Copyright (C) 2002-2007, Thanasis Delenikas, Athens/GREECE
// Web Site: http://www.smslib.org
//
// SMSLib is distributed under the LGPL license.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

package org.csstudio.ams.connector.sms.softmodem;

/**
 * This class contains some information about the GSM modem/phone
 * as well as some simple statistics (total inbound/outbound messages).
 */
public class CSoftwareDeviceInfo
{
    protected String manufacturer;

    protected String model;

    protected String serialNo;

    protected String imsi;

    protected String swVersion;

    protected int batteryLevel;

    protected int signalLevel;

    protected boolean gprsStatus;

    protected CSoftwareStatistics statistics;

    public CSoftwareDeviceInfo()
    {
        manufacturer = "";
        model = "";
        serialNo = "";
        imsi = "";
        swVersion = "";
        batteryLevel = 0;
        signalLevel = 0;
        gprsStatus = false;
        statistics = new CSoftwareStatistics();
    }

    /**
     * Returns the manufacturer.
     * 
     * @return The manufacturer string.
     */
    public String getManufacturer()
    {
        return manufacturer;
    }

    /**
     * Returns the model.
     * 
     * @return The model string.
     */
    public String getModel()
    {
        return model;
    }

    /**
     * Returns the serial number (IMEI).
     * 
     * @return The serial number (IMEI).
     */
    public String getSerialNo()
    {
        return serialNo;
    }

    /**
     * Returns the subscriber number (IMSI).
     * 
     * @return The subscriber number (IMSI).
     */
    public String getImsi()
    {
        return imsi;
    }

    /**
     * Returns the software (firmware) version string.
     * 
     * @return The software (firmware) version string.
     */
    public String getSwVersion()
    {
        return swVersion;
    }

    /**
     * Returns the battery level (range 1-100).
     * 
     * @return The battery level.
     */
    public int getBatteryLevel()
    {
        return batteryLevel;
    }

    /**
     * Returns the signal level.
     * 
     * @return The signal level.
     */
    public int getSignalLevel()
    {
        return signalLevel;
    }

    /**
     * Returns the GPRS status.
     * 
     * @return True if GPRS connection is enabled.
     */
    public boolean getGprsStatus()
    {
        return gprsStatus;
    }

    /**
     * Returns the Statistics object, which holds the total number of inbound & outbound messages since restart.
     * 
     * @return The Statistics object.
     * @see CSoftwareStatistics#getTotalIn()
     * @see CSoftwareStatistics#getTotalOut()
     */
    public CSoftwareStatistics getStatistics()
    {
        return statistics;
    }

    /**
     * Statistics' class (total inbound/outbound messages).
     */
    public static class CSoftwareStatistics
    {
        private int totalIn;

        private int totalOut;

        public CSoftwareStatistics()
        {
            totalIn = 0;
            totalOut = 0;
        }

        /**
         * Returns the total number of inbound messages since restart
         * 
         * @return The number of inbound messages.
         * @see #getTotalOut()
         */
        public int getTotalIn()
        {
            return totalIn;
        }

        /**
         * Returns the total number of outbound messages since restart
         * 
         * @return The number of outbound messages.
         * @see #getTotalIn()
         */
        public int getTotalOut()
        {
            return totalOut;
        }

        protected void incTotalIn()
        {
            totalIn++;
        }

        protected void incTotalOut()
        {
            totalOut++;
        }

        protected void decTotalIn()
        {
            totalIn--;
        }
    }
}
