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

import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.Date;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;


public class myDateTimeString {
			  
	/**
	 * Konversion von Millisekunden-Wert nach String
	 * 		params in : Date_Format, Time_Format, Millisekunden
	 * 		params out: String, der Datum und / oder Uhrzeit enthält
	 * (dateform und / oder timeform dürfen leere Strings sein, aber beide Strings leer zu übergeben macht 
	 * nicht viel Sinn, denn wer will schon einen leeren String zurückgegeben haben !)

	 * sample call :
	 * String ymd_hms = getDateTimeString( "yyyy-MM-dd", "HH:mm:ss", now);	
	 * 
	 * @author valett
	 *
	 */

	public String getDateTimeString(final String dateform, final String timeform, final long millis) {
		  String result = "";

		  if ( dateform.length() != 0 ) {
			  SimpleDateFormat sdfDate = new SimpleDateFormat(dateform);
			  String strDate = sdfDate.format(millis);
			  result = strDate;
		  } 
		    
		  if ( timeform.length() != 0 ) {
			  SimpleDateFormat sdfTime = new SimpleDateFormat(timeform);
			  String strTime = sdfTime.format(millis);
			  if ( result.length() != 0 ){
				  result = result + " " + strTime ;
			  } else {
				  result = strTime;
			  }
		  }

		  return ( result );
	}
}
