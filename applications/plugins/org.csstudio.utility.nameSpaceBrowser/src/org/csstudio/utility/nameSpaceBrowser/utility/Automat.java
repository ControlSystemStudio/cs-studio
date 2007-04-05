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
package org.csstudio.utility.nameSpaceBrowser.utility;

import java.util.ArrayList;
import java.util.ListIterator;

//import org.csstudio.utility.nameSpaceBrowser.Messages;

public class Automat {
	public enum Zustand {FACILITY,CONTROLLER,RECORD};
	public enum Ereignis {ou,efan,econ,UNKNOWN};
	private Zustand zustand = Zustand.FACILITY;
//	private static String[] nuf ={"",""};
	private ArrayList<String> name = new ArrayList<String>();
//	private String select=""; //$NON-NLS-1$

	private Ereignis ereignis;
	private String select;
	private CSSViewParameter para;

	public Automat(){
		System.out.println("new Automat");
	}

	public CSSViewParameter event(Ereignis ereignis, String select){
		this.ereignis=ereignis;
		this.select=select;
		para = new CSSViewParameter();
//		System.out.print("Zustand alter: "+zustand+"\tEreignis: "+ereignis);
		switch(zustand){
			case FACILITY:
				facility();
				break;
			case CONTROLLER:
				controller();
				break;
			case RECORD:
				record();
				break;
		}
		ListIterator<String> it = name.listIterator();
		while (it.hasNext()) {
			para.name = (String) it.next()+" "+para.name; //$NON-NLS-1$
		}
//		String tab=""; //$NON-NLS-1$
		para.name =para.name.substring(0, para.name.length()-1);
//		for(int i=0;i<zustand.ordinal();i++){
//			tab+="\t"; //$NON-NLS-1$
//		}
//		System.out.println("\t neuer Zustand: "+zustand);
		return para;
	}

	private void record() {
//		System.out.println("\r\n\tselect:"+select);
		switch(ereignis){
			case efan:
				if(name.size()>2){
//					System.out.print("\t Bedingung 1-");
					if(select.indexOf("*")<0){ //$NON-NLS-1$
//						System.out.print("1");
						if(name.get(name.size()-1).startsWith("econ")){ //$NON-NLS-1$
//							System.out.print("a");
							name.remove(name.size()-1);
						}
						name.set(name.size()-2,select);
					}else if(name.get(name.size()-1).startsWith("ecom")){ //$NON-NLS-1$
//						System.out.println("2");
						name.remove(name.size()-1);
						name.remove(name.size()-1);
					}
					else if(name.get(name.size()-1).startsWith("econ")){ //$NON-NLS-1${
						name.remove(name.size()-1);
						name.remove(name.size()-1);
						name.remove(name.size()-1);
					}
					else
						System.out.println(Messages.getString("Automat.Error2")); //$NON-NLS-1$
				}else{
//					System.out.print("\t Bedingung 2-");
					if(name.get(name.size()-1).startsWith("ou")){ //$NON-NLS-1$
//						System.out.print("1");
						if(select.indexOf("*")<0){ //$NON-NLS-1$
//							System.out.print("a");
							name.add(select);
							name.add("ecom=EPICS-IOC,"); //$NON-NLS-1$
						}
					}else {
//						System.out.print("2");
						name.remove(name.size()-1);
						if(select.indexOf("*")<0){ //$NON-NLS-1$
//							System.out.print("a");
							name.add(select);
							name.add("ecom=EPICS-IOC,"); //$NON-NLS-1$
						}
					}
				}

				para.filter= "econ=*"; //$NON-NLS-1$
				para.newCSSView= true;
				zustand = Zustand.CONTROLLER;
				break;
			case econ:
				if(name.get(name.size()-1).startsWith("econ")&&select.indexOf("*")<0){ //$NON-NLS-1$ //$NON-NLS-2$
//					System.out.println("\t Bedingung 1");
					name.set(name.size()-1, select);
				}
				else if(name.get(name.size()-1).startsWith("econ")&&select.indexOf("*")>=0){ //$NON-NLS-1$ //$NON-NLS-2$
//					System.out.println("\t Bedingung 2");
					name.remove(name.size()-1);
				}
				else if(!name.get(name.size()-1).startsWith("econ")&&select.indexOf("*")<0){//$NON-NLS-1$ //$NON-NLS-2$
//					System.out.println("\t Bedingung 3");
					name.add(select);
				}
//				else System.out.println("\t Bedingung 4");
				para.filter= "eren=*"; //$NON-NLS-1$
				para.newCSSView= false;
				zustand = Zustand.RECORD;
				break;
		}
	}

	private void controller() {
		switch(ereignis){
		case econ:
			if(select.indexOf("*")<0){ //$NON-NLS-1$
//				System.out.print("\t Bedingung 1");
				name.add(select);
			}
//			else System.out.print("\t Bedingung 2");
			para.filter= "eren=*"; //$NON-NLS-1$
			zustand = Zustand.RECORD;
			break;
		case efan:
			// Facility hat sich geändert
			if(name.size()>1&&name.get(name.size()-2).startsWith("efan")&&select.indexOf("*")<0){ //$NON-NLS-1$ //$NON-NLS-2$
//				System.out.print("\t Bedingung 1");
				name.set(name.size()-2,select);
			// Facility hat sich geändert zu All
			}else if(name.size()>1&&name.get(name.size()-2).startsWith("efan")&&select.indexOf("*")>=0){ //$NON-NLS-1$ //$NON-NLS-2$
//				System.out.print("\t Bedingung 2");
				name.remove(name.size()-1);
				name.remove(name.size()-1);
//			// Facility hat sich von ALL geändert zu Speziell
			}else if(name.size()<2&&select.indexOf("*")<0){//$NON-NLS-1$
//				System.out.print("\t Bedingung 3");
				name.add(select);
				name.add("ecom=EPICS-IOC,"); //$NON-NLS-1$
			}
//			else System.out.print("\t Bedingung 4");
			para.filter= "econ=*"; //$NON-NLS-1$
			para.newCSSView= true;
			zustand = Zustand.CONTROLLER;
			break;
		}
	}
	private void facility() {
		switch(ereignis){
			case efan:
				if(select.indexOf("*")<0){ //$NON-NLS-1$
//					System.out.print("\t Bedingung 1");
					name.clear();
					name.add("ou=EpicsControls"); //$NON-NLS-1$
					name.add(select);
					name.add("ecom=EPICS-IOC,"); //$NON-NLS-1$
				}
//				else System.out.print("\t Bedingung 2");
				para.filter= "econ=*"; //$NON-NLS-1$
				para.newCSSView= true;
				zustand = Zustand.CONTROLLER;
				break;
			case ou:
//				System.out.print("\t Bedingung 1");
				name.clear();
				name.add(select); //$NON-NLS-1$
				para.filter= "efan=*"; //$NON-NLS-1$
				para.newCSSView= true;
				zustand = Zustand.FACILITY;
				break;
			case UNKNOWN:
				break;
		}
	}

	public Zustand getZustand() {
		return zustand;
	}
	public Ereignis getEreignis() {
		Ereignis e;
		e= Ereignis.valueOf(zustand.name());
		return e;
	}

	public Zustand getName() {
		return zustand;

	}
}