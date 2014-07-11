package from_fa_archiver;


//import java.util.ArrayList;
import java.util.Calendar;
//import java.util.List;

//import org.csstudio.trends.databrowser2.model.PlotSampleArray;
//import org.csstudio.trends.databrowser2.model.PlotSample;
//import org.epics.vtype.VType;

/* Class to store data requested from the Fa-Archiver
 * Can be used to plot data later on
 * Two subclasses for decimated data and undecimated data 
 * */
public abstract class FaData {
	long[] time; // timestamp in milliseconds from epoch
	int[][] data; // dependent on subclass
	int dataUsed; // needed for the Decimated data. Specifies if min, max, mean or std data is currently used. Constant for undecimated data
	String source = "";
	
	
	//constructors !! needed?
	public FaData(int[][] data, Calendar start, Calendar end){
		this.data=data;
		this.time=createTimestamps(start, end, data[1].length);
	}
	
	public FaData(Calendar start, Calendar end, int dataUsed){
		this.dataUsed = dataUsed;
		this.time=createTimestamps(start, end, data[1].length);
		}
	
	public FaData(int[][] data, Calendar start, Calendar end, int dataUsed, String source){
		this.data=data;
		this.time=createTimestamps(start, end, data[1].length);
		this.dataUsed = dataUsed;
		this.source = source;
	}
	
	//methods
	public int getSampleNumber(){
		return data.length;
	}
	
	public abstract int[] getDataX();
	
	public abstract int[] getDataY();
	
	//Gets datasets for x-coordinates, the wanted datasets are specified in an array with values as for dataUsed
	public int[][] getMultiDataX(int[] dataSetsWanted) {
		int[][] dataSets = new int[dataSetsWanted.length][];
		for (int i = 0; i<dataSetsWanted.length; i++){
			dataSets[i]=data[dataSetsWanted[i]];
		}
		return dataSets;
	}
	
	public int[][] getMultiDataY(int[] dataSetsWanted) {
		int[][] dataSets = new int[dataSetsWanted.length][];
		for (int i = 0; i<dataSetsWanted.length; i++){
			dataSets[i]=data[dataSetsWanted[i+4]];
		}
		return dataSets;
	}
	
	// creates an array with time coordinates (milliseconds from epoch time) given a sample number a start time and an end time
	public static long[] createTimestamps(Calendar start, Calendar end, int sampleNumber){
		long startTime = start.getTimeInMillis();
		long endTime = end.getTimeInMillis();
		long timeInterval = endTime-startTime;
		long[] time = new long[sampleNumber];
		for (int i = 0; i<sampleNumber; i++){
			time[i]=startTime+(timeInterval/sampleNumber)*i; //No integer division problems? 
		}
		return time;
	}

	/*public PlotSampleArray getDataSetAsPlotSampleArray(char coordinate) throws Exception{
		int[] dataArray;
		if (coordinate == 'x'){
			dataArray = getDataX();
		} else if (coordinate == 'y'){
			dataArray = getDataY();
		} else {
			// Throw some exception to show invalid char
			throw new Exception("char must be x or y");
		}
		int size = getSampleNumber();
		List<PlotSample> plotSamplesList = new ArrayList<PlotSample>();
		PlotSample ps;
		for (int i = 0; i<size; i++){
			VType value = new VInt(dataArray[i]);
			ps = new PlotSample(source, value);
			plotSamplesList.add(ps);
		}
		PlotSampleArray psa = new PlotSampleArray();
		psa.set(plotSamplesList);
		return  psa;
	}
	
	// to create a PlotSample.
	class VInt implements VType{
		int i;
		VInt(int i){
			this.i = i;
		}
	} */
}
