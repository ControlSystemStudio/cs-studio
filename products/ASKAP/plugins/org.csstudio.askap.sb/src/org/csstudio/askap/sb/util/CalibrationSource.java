package org.csstudio.askap.sb.util;

public class CalibrationSource {
	public String catalogue;
	
	public String name;
	public String frame;
	public String C1;
	public String C2;
	
	public String magnitude;
	
	public String getC1() {
		return C1;
	}
	
	public String getC2() {
		return C2;
	}
	
	/**
	 *  sets the value of C1 in radians
	 * @param radians
	 */
	public void setC1(double radians) {
		C1 = radianToHour(radians);
	}
	
	/**
	 *  sets the value of C1 in radians
	 * @param radians
	 */
	public void setC2(double radians) {
		C2 = radianToDeg(radians);
		
	}
	
	
	/**
	 * Converts radians to hours in time format, hours minutes seconds and milliseconds (HH:MM:SS.X)
	 * 
	 * @param radians
	 * @return
	 */
	private String radianToHour(double radians) {
		double time = radians * 24 * 60 * 60 / (2*Math.PI);
		
		int timeInSec = (int) time;
		
		String format = "%02d";
		String seconds = String.format(format, timeInSec % 60);
		String minutes = String.format(format, (timeInSec % 3600) / 60);
		String hours = String.format(format, timeInSec / 3600);
					
		String milliSec = String.format("%03d", (int) ((time - timeInSec) * 1000));
		
		String timeStr = hours + ":" + minutes + ":" + seconds + "." + milliSec;
			
		return timeStr;

	}
	
	/**
	 * Converts radians to degrees in degrees, minutes, seconds and milliseconds (-DD^MM'SS".X) 
	 * 
	 * @param radians
	 * @return
	 */
	private String radianToDeg(double radians) {
		double deg = Math.toDegrees(radians);
		String degStr = "";
		
		if (deg<0)
			degStr = "-";
		else
			degStr = "+";
		deg = Math.abs(deg);
		
		String format = "%02d";			
		String arcDeg = String.format(format, (int)deg);
		
		double minute = (deg - (int)deg) * 60;
		String arcMin = String.format(format, (int) minute);
		
		double second = (minute - (int)minute) * 60;
		String arcSec = String.format(format, (int)second);
					
		String milliSec = String.format("%03d", (int) ((second - (int)second) * 1000));
		
		degStr = degStr + arcDeg + "^" + arcMin + "'" + arcSec + "\"." + milliSec;
			
		return degStr;
		
	}
	
	public void setC1(String c1) {
		C1 = c1;
	}
	
	public void setC2(String c2) {
		C2 = c2;
	}

}
