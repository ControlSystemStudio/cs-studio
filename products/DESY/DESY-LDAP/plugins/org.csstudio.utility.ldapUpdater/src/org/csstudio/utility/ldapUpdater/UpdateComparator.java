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

package org.csstudio.utility.ldapUpdater;

import java.io.FileWriter;
import java.io.IOException;
// import java.text.SimpleDateFormat;
// import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldapUpdater.model.DataModel;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class UpdateComparator {

	private static final CentralLogger LOGGER = CentralLogger.getInstance();

	private DataModel _model;

	private IPreferencesService _prefs;
	
	public UpdateComparator(DataModel model) {
		this._model = model;
	    _prefs = Platform.getPreferencesService();
	}

	
	
	public void compareLDAPWithIOC() {
		Boolean warning_found = false;
		
		for (IOC ioc : _model.getIocList()) {
			
			String iocName = ioc.getName();
			HashMap<String, Long> historyMap = _model.getHistoryMap();
			if (historyMap.containsKey(iocName)) {

				if (isIOCFileOlderThanHistoryEntry(ioc, historyMap.get(iocName))) {
					continue;
				}
				
				List<String> recordNames = ioc.getIocRecordNames();
				for (String recordName : recordNames) {
					int ind = (recordName.indexOf("+")) + (recordName.indexOf("/"));
					if (ind <= 0) {
						InLdap inLdap = new InLdap();
						if (!inLdap.existRecord(ioc, recordName)) {
							DirContext directory = Engine.getInstance().getLdapDirContext();
							Formatter f = new Formatter();
 							f.format("eren=%s, econ=%s, ecom=EPICS-IOC, efan=%s, ou=EpicsControls",
										recordName, iocName, ioc.getGroup());
							Attributes afe = attributesForEntry("epicsRecord", "eren", recordName);
							try {
								directory.bind(f.toString(), null, afe); // = Record schreiben
								LOGGER.info( this," Record written: \"" + iocName+ " - " + recordName + "\"");
								warning_found = false;
								
							} catch (NamingException e) {
								LOGGER.warn(this, "Naming Exception while try to write " + iocName + " " + recordName);
								warning_found = true;
							}
						}
					}
				}
				LOGGER.info(this, "IOC " + iocName + "\t\t records " +  recordNames.size());
				if (!warning_found) {
					AppendLineToHistfile(iocName); 
				}
				
			} else {
				_model.getNewIocNames().add(iocName);
//		        CentralLogger.getInstance().info(this, ioc + " added to NewIocNames, try to write this IOC data to LDAP completely");
		        LOGGER.info(this, iocName + " added to NewIocNames, cannot write to LDAP : unknown efan ");
/*
		        // ^ hier kommt das programm vorbei,
				// wenn der IOC in iocListFile (=IOCpathes) steht und
				// wenn der IOC NICHT in der history-liste steht.
				// -------------------------------------------------------------------------
				// erst neuen IOC in LDAP anlegen
		        // Folgendes kann jetzt nicht mehr ausgeführt werden, da der group name = facility name jetzt 
		        // in dieser neuen Programmversion nicht mehr bekannt ist.
//				System.out.println(ioc.getName());
				CentralLogger.getInstance().info( this, "New ioc : " + ioc.getName());
				DirContext directory = Engine.getInstance().getLdapDirContext();
				Formatter f = new Formatter();
				f.format("econ=%s, ecom=EPICS-IOC, efan=%s, ou=EpicsControls", ioc.getName(), ioc.getGroup());
				Attributes afe = attributesForEntry("epicsController", "econ", ioc.getName());
				try {
					directory.bind(f.toString(), null, afe); // = iocNamen schreiben
					CentralLogger.getInstance().info(this, "iocName written to LDAP : " + ioc);
					error_found=false;
				} catch (NamingException e) {
					CentralLogger.getInstance().error (this, "Naming Exception while try to write " + ioc);
					error_found=true;
				}

				// hier müssen die header-parameter geschrieben werden, zB epicsIPAddress, ...
				//...
				//...
				//...
				
				// und wenn das fertig ist, müssen sie in dem exist-zweig auch aktualisiert werden, wenn
				// sie noch nicht vorhanden sind.
				// das ist dann fast der gleiche mechanismus wie bei den record-namen, nur der LDAP-level ist ein anderer.
				
				// -------------------------------------------------------------------------
				// dann alle recordNames dieses IOC in LDAP anlegen:
				if  (!error_found) {
					List<String> recordNames2 = ioc.getIocRecordNames();
					for (String recordName2 : recordNames2) {
						ind = (recordName2.indexOf("+")) + (recordName2.indexOf("/"));
						if (ind <= 0) {
							InLdap inLdap = new InLdap();
							if (!inLdap.existRecord(ioc, recordName2)) {
								DirContext directory2 = Engine.getInstance().getLdapDirContext();
								Formatter f2 = new Formatter();
								f2.format("eren=%s, econ=%s, ecom=EPICS-IOC, efan=%s, ou=EpicsControls",
												recordName2, ioc.getName(), ioc.getGroup());
								Attributes afe2 = attributesForEntry("epicsRecord","eren", recordName2);
								try {
									directory2.bind(f2.toString(), null, afe2); // = ioc records schreiben
									CentralLogger.getInstance().info(this,
										"Record written!" + ioc.getName() + " - " + recordName2);
										error_found=false;
								} catch (NamingException e) {
									CentralLogger.getInstance().error (this, "Naming Exception while try to write " + ioc.getName() + " " + recordName2);
									error_found=true;
								}
							}
							ioc.set_mustWriteIOCToHistory(true);
						}
					}
				}
*/			
			}
		}
		//}
	}

	private boolean isIOCFileOlderThanHistoryEntry(IOC ioc, Long historyMapTimeInMillis) {
		Long iocTime = ioc.getDateTime().getTimeInMillis();
		return iocTime < historyMapTimeInMillis;
	}



	/**
	 * Returns the attributes for a new entry with the given object class and
	 * name.
	 * 
	 * @param objectClass
	 *            the object class of the new entry.
	 * @param rdnAttr 
	 * 			  ? // TODO (someone) : 
	 * @param name
	 *            the name of the new entry.
	 * @return the attributes for the new entry.
	 */
	private Attributes attributesForEntry(final String objectClass,
			String rdnAttr, final String name) {
		BasicAttributes result = new BasicAttributes();
		result.put("objectClass", objectClass);
		result.put(rdnAttr, name);
		// result.put("epicsCssType", objectClass.getCssType());
		return result;
	}

	
	/**
	 * append a line to the history file.
	 * 
	 * @param iocname
	 */
	private void AppendLineToHistfile(final String iocname) {
		try {
			FileWriter fw = new FileWriter(
					_prefs.getString(Activator.getDefault().getPluginId(),
		    	    		LdapUpdaterPreferenceConstants.LDAP_HIST_PATH, "", null) + "history.dat", true);	
			long now = System.currentTimeMillis();			
			myDateTimeString dateTimeString = new myDateTimeString();
			String ymd_hms = dateTimeString.getDateTimeString( "yyyy-MM-dd", "HH:mm:ss", now);
			now = now / 1000; // now is now in seconds
			
			String _iocname = iocname;
			do  { _iocname = _iocname.concat ( " " ); } while (_iocname.length() < 20);
            
			fw.append ( _iocname + "xxx     " + now + "   " + ymd_hms + System.getProperty("line.separator" ) ); 
			fw.flush();
			fw.close();
		} catch (IOException e) {
				LOGGER.error (this, "I/O-Exception while try to append a line to " + LdapUpdaterPreferenceConstants.LDAP_HIST_PATH + "" + null + "history.dat");
		}
	}
}
