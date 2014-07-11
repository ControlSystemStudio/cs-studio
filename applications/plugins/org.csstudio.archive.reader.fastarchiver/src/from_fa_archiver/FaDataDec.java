package from_fa_archiver;

import java.util.Arrays;
import java.util.Calendar;


public class FaDataDec extends FaData {
	// dataUsed: specifies if mean(0), min(1), max(2) or std(3) data is currently used.
	
	/**
	 * @param source 
	 * @param dataX_Y: int[][]
	 * index 0 -> x mean
	 * index 1 -> y mean
	 * index 2 -> x min
	 * index 3 -> y min
	 * index 4 -> x max
	 * index 5 -> y max
	 * index 6 -> x std
	 * index 7 -> y std
	 */
	public FaDataDec(Calendar start, Calendar end, int[][] dataX_Y, String source) {
		super(dataX_Y, start, end, 0, source); //defaults to mean values	
	}
	
	public FaDataDec(Calendar start, Calendar end, int[][] dataX, int[][] dataY, String source) {
		super(new int[8][dataX.length], start, end, 0, source); //defaults to mean values	
		setData(dataX, dataY);	
	}

	public void setData(int[][] dataX, int[][] dataY){
		this.data = new int[8][dataX.length];
		data[0] = dataX[0];
		data[1] = dataX[1];
		data[2] = dataX[2];
		data[3] = dataX[4];
		data[4] = dataY[1];
		data[5] = dataY[2];
		data[6] = dataY[3];
		data[7] = dataY[4];
	}
	
	public void setDataSetUsed(int dataUsed){
		this.dataUsed = dataUsed;
	}
	
	@Override
	public int[] getDataX() {
		return data[dataUsed*2];
	}
	
	@Override
	public int[] getDataY() {
		return data[dataUsed*2+1];
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("X-mean: " + Arrays.toString(data[0]));
		sb.append("\nY-mean: " + Arrays.toString(data[1]));
		sb.append("\nX-min: " + Arrays.toString(data[2]));
		sb.append("\nY-min: " + Arrays.toString(data[3]));
		sb.append("\nX-max: " + Arrays.toString(data[4]));
		sb.append("\nY-max: " + Arrays.toString(data[5]));
		sb.append("\nX-std: " + Arrays.toString(data[6]));
		sb.append("\nY-std: " + Arrays.toString(data[7]));
		sb.append("\ntime-coordinates: " + Arrays.toString(time));
		return sb.toString();
	}
	
}
