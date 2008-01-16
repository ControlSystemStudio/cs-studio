package org.csstudio.diag.interconnectionServer.server;

import java.text.SimpleDateFormat;

import org.csstudio.platform.logging.CentralLogger;

public class LdapSupport {
	
	private static LdapSupport thisLdapSupportInstance = null;
	
	public LdapSupport() {
		/*
		 * nothing to do
		 */
	}
	public static LdapSupport getInstance() {
		//
		// get an instance of our singleton
		//
		if ( thisLdapSupportInstance == null) {
			synchronized (LdapSupport.class) {
				if (thisLdapSupportInstance == null) {
					thisLdapSupportInstance = new LdapSupport();
				}
			}
		}
		return thisLdapSupportInstance;
	}
	
	
	public String getLogicalIocName ( String ipAddress) {
		
		/*
		 * error handling
		 */
		if ( ipAddress.length() < 8) {
			/*
			 * can't be a valid IP address
			 */
			return "invalid IP address";
		}
		
		/*TODO
		 * connect to LDAP
		 * search in EpicsControls namespace
		 * find logical IOC name  stored in econ by matching epicsIocIpAddress with the IP address here
		 */
		
		/*
		 * in the meantime ...
		 */

		if ( ipAddress.equals("131.169.112.56")) {
			return "mkk10KVB1";
		} else if ( ipAddress.equals("131.169.112.146")) {
			return "mthKryoStand";
		} else if ( ipAddress.equals("131.169.112.155")) {
			return "ttfKryoCMTB";
		} else if ( ipAddress.equals("131.169.112.68")) {
			return "utilityIOC";
		} else if ( ipAddress.equals("131.169.112.80")) {
			return "ttfKryoLinac";
		} else if ( ipAddress.equals("131.169.112.52")) {
			return "krykWetter";
		} else if ( ipAddress.equals("131.169.112.141")) {
			return "Bernds_Test_IOC";
		} else return "no logical IOC Name found for: " + ipAddress;
		
		/*
		 *epicsGPFC01       mkk10KVA1       : Keine Datei Y:\directoryServer\mkk10KVA1.BootLine.dat gefunden
epicsGPFC02       mkk10KVB1       131.169.112.56
epicsGPFC03       mkk10KVC1       131.169.112.69
epicsGPFC04       mkk10KVC2       131.169.112.87
epicsGPFC05       mkk10KV6A       131.169.112.153
epicsGPFC06       mkk10KV2B       131.169.112.154
epicsGPFC07       mkk10KV3B       131.169.112.157
epicsPC21         mthKryoStand    131.169.112.146
epicsPC24         wienerVME       131.169.112.150
epicsPC25         ttfKryoCMTB     131.169.112.155
epicsPC26         ttfKryoXCB      131.169.112.170
epicsPPC02        mkkPPC02        131.169.112.224
epicsPPC11        mkkSender       : Keine Datei Y:\directoryServer\mkkSender.BootLine.dat gefunden
epicsPPC12        ttfKryoSK47a    131.169.112.104
epicsPPC13        mkkPPC03        : Keine Datei Y:\directoryServer\mkkPPC03.BootLine.dat gefunden
epicsPPC14        ttfKryoVC2      131.169.112.109
epicsPPC18        mkkModbus       131.169.113.52
epicsPPC19        ttfKryoVC1      131.169.113.53
epicsVME00        utilityIOC      131.169.112.68
epicsVME01        mkkTempPuls     : Keine Datei Y:\directoryServer\mkkTempPuls.BootLine.dat gefunden
epicsVME02        kryoCta         131.169.112.94
epicsVME04        ttfKryoLinac    131.169.112.80
epicsVME08        analyze         131.169.112.228
epicsVME11        heraKryoKoMag   131.169.112.92
epicsVME12        modulator       : Keine Datei Y:\directoryServer\modulator.BootLine.dat gefunden
epicsVME14        ttfDiagLinac    131.169.112.225
epicsVME15        mhf-irm-a       : Keine Datei Y:\directoryServer\mhf-irm-a.BootLine.dat gefunden
epicsVME16        mkkKlima3       131.169.112.227
epicsVME17        mkkPowStatC_B   131.169.112.176
epicsVME18        mkk-irm-b       131.169.112.177
epicsVME20        krykWetter      131.169.112.52
epicsVME22        ttfKryoCB       131.169.112.54
epicsVME27        heraKryoRefmag  : Keine Datei Y:\directoryServer\heraKryoRefmag.BootLine.dat gefunden
epicsVME28        heraKryoCavity  : Keine Datei Y:\directoryServer\heraKryoCavity.BootLine.dat gefunden
epicsVME29        tineDataSrv     131.169.112.229
epicsVME34        mkkKlima2       131.169.112.138
epicsVME35        heraKryoFel     131.169.112.144
epicsVME36        mkkKlima1       131.169.112.145
epicsVME37        mkk-irm-a       131.169.112.114
epicsVME40        ttfKryoFV       131.169.112.101
epicsVME62        mkkPowStatC_A   131.169.112.142
epicsVME62.irm-c  mkk-irm-c       : Keine Datei Y:\directoryServer\mkk-irm-c.BootLine.dat gefunden
		 */
	}
	
	public void setAllRecordsToConnected ( String logicalIocName) {
		/*
		 * just a convenience method
		 */
		CentralLogger.getInstance().debug(this,"IocChangeState: setAllRecordsToConnected");
		setAllRecordsInLdapServer ( logicalIocName, true);
		
	}
	
	public void setAllRecordsToDisconnected ( String logicalIocName) {
		/*
		 * just a convenience method
		 */
		CentralLogger.getInstance().debug(this,"IocChangeState: setAllRecordsToDisconnected");
		setAllRecordsInLdapServer ( logicalIocName, false);
		
	}
	
	/*
	 * method is synchronized: In case several IOCs disconnect the threads to enter the changes in the LDAP server will be
	 * started. But they will write in parallel to the LDAP server - but in sequence!
	 * This will (partly) avoid congestion on the send queue in addLdapWriteRequest()
	 */
	synchronized private void setAllRecordsInLdapServer ( String logicalIocName, boolean connected) {
		String status = null;
		/*
		 * TODO
		 */
		
		/*
		 * find all records belonging to the IOC: logicalIocName
		 * -> search for econ == logicalIocName
		 * -> create list for all eren (record) entries
		 * 
		 * for each eren entry set the epicsAlarmStatus to 'OFFLINE' and the epicsAlarmTimeStamp to the actual time
		 */
		if ( connected) {
			status = "ONLINE";
		} else {
			status = "OFFLINE";
		}
		
		
		//
		// create time stamp written to epicsAlarmTimeStamp
		// this is a copy from the class ClientRequest
		//
		SimpleDateFormat sdf = new SimpleDateFormat( PreferenceProperties.JMS_DATE_FORMAT);
        java.util.Date currentDate = new java.util.Date();
        String eventTime = sdf.format(currentDate);
        
        /*
         * TODO
         * Changes in LDAP support: We must make sure that we can make changes in really BIG databases.
         * The pool size in the LDAP addLdapWriteRequest class must be adjusted accordingly!
         * For now I would expect ~ 10.000 (ten thousand) records time two changes which makes 20.000 entries in the addLdapWriteRequest queue!
         */
        
        /*
         * for ( all the records we found) {
         * 		Engine.getInstance().addLdapWriteRequest( "epicsAlarmTimeStamp", channel, eventTime);
         * 		Engine.getInstance().addLdapWriteRequest( "epicsAlarmStatus", channel, status);
         * }
         */
        
        CentralLogger.getInstance().debug(this,"IocChangeState: setAllRecordsInLdapServer - DONE");
	}

}
