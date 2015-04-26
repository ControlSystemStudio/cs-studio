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
 package org.csstudio.archive.reader.archiverecord;
//
//  main (example) method for when run as an application
//
public class ArchiveRecordTest {
	public static void main(final String args[]) throws Exception {
	    System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "131.169.115.236");
	    System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list","YES");
		final String PV = "RZ:K:KL:P1:B50_T_Raum_ai_h";
		final ArchiveRecord ar= new ArchiveRecord(PV);
		final int dim = ar.getDimension();
		if (dim<=0) {
			System.out.println("bad Dim");
			return;
		}
		if (ar.getAllFromCA()<=0) {
			System.out.println("bad getAllFromCA");
			return;
		}
		System.out.println("Val:");
		for (int i=0;i<dim;i++) {
            System.out.print(+ ar.getVal()[i]+" ");
        }
		System.out.println("TIM:");
		for (int i=0;i<dim;i++) {
            System.out.print(+ ar.getTime()[i]+" ");
        }
		System.out.println("Nsec:");
		for (int i=0;i<dim;i++) {
            System.out.print(+ ar.getNsec()[i]+" ");
        }
		System.out.println("Stat:");
		for (int i=0;i<dim;i++) {
            System.out.print(+ ar.getSevr()[i]+" ");
        }

    	} // eof main()
 }  // eof class
