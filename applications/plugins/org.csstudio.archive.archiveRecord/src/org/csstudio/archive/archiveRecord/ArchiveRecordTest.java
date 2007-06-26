package org.csstudio.archive.archiveRecord;
//
//  main (example) method for when run as an application 
//
public class ArchiveRecordTest {
	public static void main(String args[]) throws Exception { 
	    System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "131.169.115.236");
	    System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list","YES");
		final String PV = "RZ:K:KL:P1:B50_T_Raum_ai";  
		ArchiveRecord ar= new ArchiveRecord(PV);
		int dim = ar.getDimension();
		if (dim<=0) {
			System.out.println("bad Dim");
			return;
		}
		if (ar.getAllFromCA()<=0) {
			System.out.println("bad getAllFromCA");
			return;
		}
		System.out.println("Val:");
		for (int i=0;i<dim;i++) System.out.print(+ ar.getVal()[i]+" ");
		System.out.println("TIM:");
		for (int i=0;i<dim;i++) System.out.print(+ ar.getTime()[i]+" ");
		System.out.println("Nsec:");
		for (int i=0;i<dim;i++) System.out.print(+ ar.getNsec()[i]+" ");
		System.out.println("Stat:");
		for (int i=0;i<dim;i++) System.out.print(+ ar.getSevr()[i]+" ");
		
    	} // eof main()
 }  // eof class