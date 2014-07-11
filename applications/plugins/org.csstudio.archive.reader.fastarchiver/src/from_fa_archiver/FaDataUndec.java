package from_fa_archiver;

import java.util.Arrays;
import java.util.Calendar;



public class FaDataUndec extends FaData {
	static final int DATA_USED = 0;

	public FaDataUndec(Calendar start, Calendar end, int[][] dataX_Y, String source) {
		super(dataX_Y, start, end, 0, source); //Only one data set	
	}
	
	public FaDataUndec(Calendar start, Calendar end, int[] dataX, int[] dataY, String source) {
		// get time coordinates: milliseconds from epoch?
		// only one set of datapoints
		super(new int[2][dataX.length], start, end, DATA_USED, source); 
		setData(dataX, dataY);
	}

	public void setData(int[] dataX, int[] dataY){
		data[0] = dataX;
		data[1] = dataY;
	}
	
	@Override
	public int[] getDataX() {
		return data[dataUsed];
	}
	
	@Override
	public int[] getDataY() {
		return data[dataUsed+1];
	}
	
	@Override
	public String toString(){
		return "X-coordinates: " + Arrays.toString(getDataX()) + "\nY-coordinates: " + Arrays.toString(getDataY()) + "\ntime-coordinates: " + Arrays.toString(time);
	}
}
