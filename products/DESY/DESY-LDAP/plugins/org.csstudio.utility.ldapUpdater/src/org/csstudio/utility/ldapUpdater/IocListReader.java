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

// import java.io.BufferedReader;
// import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.GregorianCalendar;
// import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

import java.util.regex.*;
import java.io.*;
import java.util.*;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldapUpdater.model.DataModel;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Reads the file with the list of the IOCs.
 * 
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 30.04.2008
 */
public class IocListReader {

	private DataModel _model;

	private IPreferencesService _prefs;

	public IocListReader(DataModel model) {
		_prefs = Platform.getPreferencesService();
		this._model = model;
	}

	public void dirlist() {
		File path = new File("U:\\cfg");
		String[] list;
			list = path.list();
		Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
		for (String dirItem : list) {
			System.out.println(dirItem);
		}
	}

	/**
	 * Reads the list of all IOCs from the IOC list file.
	 * 
	 * @return the list of all IOCs.
	 */
	public final void readIocList() {
//		dirlist();
		List<IOC> result = new ArrayList<IOC>();
		BufferedReader fr = null;
		try {
			fr = new BufferedReader(new FileReader(_prefs.getString(Activator
					.getDefault().getPluginId(),
					LdapUpdaterPreferenceConstants.IOC_LIST_FILE, "", null)));
			String line;
			while ((line = fr.readLine()) != null) {
				if (line.length() > 0) {
					IOC ioc = parseLine(line);
					result.add(ioc);
				}
			}
		} catch (FileNotFoundException e) {
//			System.err.println("Error: " + e.getMessage());
//			System.out.println("Error: " + e.getMessage());
			_model.setSerror(_model.getSerror()+1);			
			CentralLogger.getInstance().error(this, "File not Found : " + e.getMessage() ); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.err.println("Error: " + e.getMessage());
//			System.out.println("Error: " + e.getMessage());
			_model.setSerror(_model.getSerror()+2);			
			CentralLogger.getInstance().error(this, "IOExeption : " + e.getMessage() ); 
				} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					System.err.println("Error: " + e.getMessage());
//					System.out.println("Error: " + e.getMessage());
//					System.err.println("Error: " + e.toString());
//					System.out.println("Error: " + e.toString());
					CentralLogger.getInstance().error(this, "Error: " + e.getMessage() + e.toString()); 
				}
			}
		}
		_model.setIocList(result);
		return;
	}

	/**
	 * Parses a single line from the IOC list file.
	 * 
	 * @param line
	 *            the line to parse.
	 * @return the parsed IOC information.
	 */
	private IOC parseLine(final String line) {
		Pattern p = Pattern.compile("(\\S*)(\\s*)(\\S*)(\\s*)(\\S*)");
		Matcher m = p.matcher(line);
		if (!m.matches()) {
			throw new RuntimeException("Fehler in Datei, Zeile ist: " + line);
		}
		String iocName = m.group(1);
		String iocPath = m.group(3);
		String physicalName = m.group(5);

		GregorianCalendar dateTime = new GregorianCalendar();
		dateTime.setTimeInMillis(new File(new File(_prefs.getString(Activator
				.getDefault().getPluginId(),
				LdapUpdaterPreferenceConstants.IOC_DBL_DUMP_PATH, "", null)),
				iocName).lastModified());

		Pattern p2 = Pattern.compile("/[^/]+/[^/]+/([^/]+)/.+");
		Matcher m2 = p2.matcher(iocPath);
		if (!m2.matches()) {
			throw new RuntimeException("Fehler in Datei, Zeile ist: " + line);
		}
		String iocGroup = m2.group(1);

		IOC result = new IOC(iocName, iocGroup, physicalName, dateTime);

		return result;
	}

	public void readIocRecordNames() {
		for (IOC ioc : _model.getIocList()) {
			CentralLogger.getInstance().debug(this, "from ioclist: " + ioc); 	// das
																				// tut
																				// NIX.
																				// warum
																				// nicht?
//			System.out.println("from ioclist: " + ioc); 						// -------------> das tut, was es soll.
			CentralLogger.getInstance().info(this, "from ioclist: " + ioc);		// -------------> das tut auch, was es soll. 						
			String pathFile = _prefs.getString(Activator.getDefault()
					.getPluginId(),
					LdapUpdaterPreferenceConstants.IOC_DBL_DUMP_PATH, "", null)
					+ ioc.getName();
			BufferedReader fr = null;
			try {
				fr = new BufferedReader(new FileReader(pathFile));
				String line;
				List<String> temp = ioc.getIocRecordNames();
				while ((line = fr.readLine()) != null) {
					if (line.length() > 0) {
						temp.add(line);
					}
				}
				ioc.setIocRecordNames(temp);
			} catch (FileNotFoundException e) {
				_model.setSerror(_model.getSerror()+1);			
				CentralLogger.getInstance().error(this, "File not Found : " + pathFile );
//				System.err.println(e.getMessage());
//				System.out.println(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_model.setSerror(_model.getSerror()+2);			
				CentralLogger.getInstance().error (this, "I/O-Execption while trying to read " + pathFile );
//				e.printStackTrace();
//				System.out.println(e.getMessage());
//				System.out.println(e.getMessage());
			} finally {
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
//						System.err.println("error: " + e.getMessage());
//						System.out.println("error: " + e.getMessage());
						_model.setSerror(_model.getSerror()+2);			
						CentralLogger.getInstance().error (this, "I/O-Execption while trying to close " + pathFile );
					}
				}
			}
		}
		CentralLogger.getInstance().info(this, "all record names read.");
	}

}
