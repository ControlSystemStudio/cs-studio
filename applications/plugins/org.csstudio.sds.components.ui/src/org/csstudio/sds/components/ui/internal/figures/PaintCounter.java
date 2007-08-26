package org.csstudio.sds.components.ui.internal.figures;

public class PaintCounter {
	private static int cnt=0;
	
	public synchronized static void increment(){
		cnt+=1;
		System.out.println("PAINT OPS: "+cnt);
	}
}
