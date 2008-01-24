/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.ldap.namespacebrowser.utility;

import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.csstudio.utility.nameSpaceBrowser.utility.CSSViewParameter;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 08.05.2007
 */
public class LDAP2Automat extends Automat {

	// State machines parameter
	private Zustand aktuell = Zustand.START;
	// LDAP parameter
	private String storeName=""; 
    private String root="";

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.Automat#event(org.csstudio.utility.nameSpaceBrowser.utility.Automat.Ereignis, java.lang.String)
	 */
	@Override
	public CSSViewParameter goDown(Ereignis ereignis, String select) {
	    System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		CSSViewParameter parameter = new CSSViewParameter();
		String aktuelleEbene = select.split("=")[0]+"=";
		System.out.println("Selection: "+select);
		int index = storeName.indexOf(aktuelleEbene);
		if(aktuelleEbene.compareTo("ou=")==0){
            if(select.indexOf("*")<0){
                parameter.name=select;
                root=select;
            }
			parameter.filter = "efan=*";
			parameter.newCSSView=true;
			aktuell=Zustand.CONTROLLER;
		}else if(aktuelleEbene.compareTo("efan=")==0){
		    // All selected?
            if(select.indexOf("*")<0){
                // new sub
    			if(index<0){
//    				parameter.name = "ecom=EPICS-IOC,"+select+storeName;
    			    if(select.endsWith(",")){
    			        select = select.substring(0,select.length()-1);
    			    }
    				parameter.name = "ecom=EPICS-IOC,"+select;
                // change sub
    			}else{
    				// Replace the start of the String to the first ',' after aktuelleEbene
//    				parameter.name = "ecom=EPICS-IOC,"+select+storeName.substring(storeName.indexOf(',',index)+1,storeName.length());
    			    parameter.name = "ecom=EPICS-IOC,"+select;
    			}
            }else parameter.name = root;

			parameter.filter = "econ=*";
			parameter.newCSSView=true;
			aktuell=Zustand.CONTROLLER;
		}else if(aktuelleEbene.compareTo("econ=")==0){
            // All selected?
            if(select.indexOf("*")<0){
                // new sub
    			if(index<0){
    				System.out.println("1");
//    				parameter.name = select+storeName;
    				parameter.name = select;
                // change sub                    
    			}else{
    				System.out.println("2");
    				// Replace the start of the String to the first ',' after aktuelleEbene
    				parameter.name = select;
//    				parameter.name = select+storeName.substring(storeName.indexOf(',',index)+1,storeName.length());
    			}
            }else{
                if(storeName.startsWith("econ=")){
                    parameter.name = storeName.substring(storeName.indexOf(',')+1);
                }else{
                    parameter.name = storeName;
                }
            }
			parameter.filter = "eren=*";
			parameter.newCSSView=false;
			aktuell=Zustand.RECORD;
		}
		storeName = parameter.name; 		
		System.out.println("Name: "+parameter.name+"\r\nfilter: "+parameter.filter);
		System.out.println("------------------------------------------");
		return parameter;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.Automat#getZustand()
	 */
	@Override
	public Zustand getZustand() {
		return aktuell;
	}

}
