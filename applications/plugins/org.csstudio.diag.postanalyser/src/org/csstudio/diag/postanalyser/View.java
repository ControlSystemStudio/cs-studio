package org.csstudio.diag.postanalyser;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithSample;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableWithSampleDropTarget;
import org.eclipse.swt.widgets.Label;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequenceContainer;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.Trace;
import org.csstudio.swt.chart.axes.YAxis;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.graphics.Color;
import java.util.Vector;
import java.util.Date;
import java.text.DateFormat;


public final class View extends ViewPart {	
	final static String ID = "org.csstudio.diag.postanalyser.view"; 
	final static int samplesArrayMax=10;
	final static String linearScale = "Linear"; 
	static Chart chart;		
	enum Algo {FFT,CorrelationPlot,GaussianFit,FFT_Hamming,FFT_Hanning,FFT_Blackman,FFT_Bartlett,FFT_Blackman_Harris};
	Vector samplesVector;
	Combo algoSelector;
	Combo xSamplesSelector;
	Combo ySamplesSelector;
	boolean showDC=true;
	
	final static boolean debug =false;
	
	public void createPartControl(final Composite parent) {
		samplesVector = new Vector(0);
	    createGUI(parent);
        // Dummy listeners should be here
    } 

    private void createGUI(Composite parent) {
	    InteractiveChart ichart = new InteractiveChart(parent, 0);
	    chart = ichart.getChart();
	    
	 // 
	 // Label "ActionsList:" and Combo of actions as FFT, FFT+Window, 
	 //    ... Correlations, Gaussians, etc
	 //  
	    Label actionLabel = new Label(ichart.getButtonBar(), SWT.LEFT | SWT.CENTER);
	    actionLabel.setText("ActionsList:"); //$NON-NLS-1$
	    algoSelector = new Combo(ichart.getButtonBar(), SWT.READ_ONLY);
	    // Let's fill the combo with date range options.
	    for (Algo p : Algo.values()) {
	    	if (p== Algo.CorrelationPlot) continue;
	    		String item = String.valueOf(p);
	    		algoSelector.add(item);
	    }
	    algoSelector.select(0);
	    		
	    algoSelector.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {	    			
	    			RecalculateAction(); 
	    	}
	    }); 
	    
		 // 
		 // Label "Y-Sample" and Combo of last max. 10=samplesArrayMax
		 //      samples (name From To)
		 //  
	    
	    Label ySampleLabel = new Label(ichart.getButtonBar(), SWT.LEFT | SWT.CENTER);
	    ySampleLabel.setText("Y-Sample:"); //$NON-NLS-1$
	    ySamplesSelector = new Combo(ichart.getButtonBar(), SWT.READ_ONLY);
	    ySamplesSelector.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    			RecalculateAction(); 
	    	}
	    }); 
        
		 // 
		 // Label "X-Sample:" and Combo of last max. 11=samplesArrayMax+
		 //      samples(name From To) or LinearScale for FFT
		 //      linearScale is default scale for all except CorrelationPlot
	    
	    Label xSampleLabel = new Label(ichart.getButtonBar(), SWT.LEFT | SWT.CENTER);
	    xSampleLabel.setText("X-Sample:"); //$NON-NLS-1$
	    xSamplesSelector = new Combo(ichart.getButtonBar(), SWT.READ_ONLY);
	    xSamplesSelector.add(linearScale);
	    xSamplesSelector.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    			RecalculateAction(); 
	    	}
	    });
	    Button filterDC = new Button(ichart.getButtonBar(), SWT.CHECK);
	    filterDC.setText("&Without DC");
	    filterDC.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(SelectionEvent event) {
	          if (((Button) event.widget).getSelection()) {
	            showDC=false;
	          RecalculateAction(); 
	          }else{
	        	  showDC=true;
	          RecalculateAction(); 
	          }
	        }
	      });
	    
        new ProcessVariableWithSampleDropTarget(ichart){
        	@Override
        	public void handleDrop(IProcessVariable name, DropTargetEvent event){
        		// Add a new PV, no archive info
        		System.out.println("received only pv: name: " + name.getName());
        	}
        	@Override
        	public void handleDrop(IProcessVariableWithSample pv_name, DropTargetEvent event) {
        		System.out.println("Drop a pvws!");

    			try {
					String name=pv_name.getName();
					if(debug) System.out.println("handleDrop PV_name="+name);
					int len=pv_name.getSampleValue().length;
					if (len <= 4) {
						System.out.println(" Error: FFT ->  View -> activateWithPV array with unpredictable len="+len);
						return ;
					}
					double timeRange = pv_name.getTimeStamp()[len-1] - pv_name.getTimeStamp()[0];
					if(timeRange <= 0.0)  {
						System.out.println(" Error: FFT ->  View -> activateWithPV non-positive timeRange="+timeRange);
						return ;
					}
					samplesVector.add(0,pv_name);
					if (samplesVector.size() >= samplesArrayMax) samplesVector.setSize(samplesArrayMax);
					
					//if only 1 element in stack we have to disable CorelationPlot, otherwise enable it
					if(debug) System.out.println("samplesVector.size()="+samplesVector.size());
					if (samplesVector.size() > 1 )  {
						algoSelector.removeAll();
					    for (Algo p : Algo.values()) {
					    		String item = String.valueOf(p);
					    		algoSelector.add(item);
					    }
					}
						
					ySamplesSelector.add(getSampleLabel(pv_name),0);
					xSamplesSelector.add(getSampleLabel(pv_name),1);
					ySamplesSelector.select(0);
					xSamplesSelector.select(0);	
					calc(name,pv_name.getSampleValue(),pv_name.getTimeStamp());
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					System.out.println(" Error: FFT ->  View -> DragAndDrop exception");
					e.printStackTrace();
				}
    		    
        	}
        };

	 }

	private YAxis addFFTTrace(String name, YAxis yaxis,double[] data,double[] time)	  {
	         ChartSampleSequenceContainer seq = new ChartSampleSequenceContainer();
	         int i;
	         double twopi = 2.*Math.PI;
	         double x=0,y=0;
	         String info = null;
	         if((data==null)||(data.length <2)) {
	        	 System.out.println("bad data array");
	        	 return null;
	         }
	         double maxFFT=data[0];
	         int currentLen=data.length;
	         double timeRange = ((time[currentLen-1] - time[0])); //time in sec
	         
	         if(timeRange <= 0.0)  {
				System.out.println(" Error: FFT ->  View -> activateWithPV non-positive timeRange="+timeRange);
				return null;
	         }
	         if (debug) System.out.println("timeRange= "+ timeRange);
	         
	         for (i=0; i<currentLen/2; i++) {
	             ChartSample.Type type;
	             x= (i*twopi)/(timeRange);
	             y = data[i]; 
	             type = ChartSample.Type.Normal;
	             if (debug) System.out.println("x= "+x+" y= "+ y );
	             if((i !=0)||((i==0)&&showDC)) {
	            	 seq.add(type, x, y, info);
	            	 	if(data[i]> maxFFT) maxFFT=data[i];
	             }	
	             
	         }
	         chart.getXAxis().setValueRange (0.0, (double) (twopi*currentLen)/(timeRange*2) );
	         
	         String AlgoName = new String(String.valueOf(algoSelector.getText()));
	         if( AlgoName.indexOf(Algo.CorrelationPlot.toString()) >= 0 ) {
	        	 AlgoName = new String("fft");
	         }
	         //
	         // Memo: time in time[] is seconds from EPOCH, but
	         // Date(long) used milisecond, so we have to  * 1000 
	         //
		 	 long startLong= (long) (time[0])*1000;
		     Date startD = new Date(startLong);
	 		 long endLong= (long) (time[currentLen-1])*1000;
			 Date endD = new Date(endLong);
     	     chart.getXAxis().setLabel(new String(AlgoName+" Y="+ name+ " numPoints="+currentLen/2+" "+ startD+" "+ endD + " (in Hz)"));
	         // Create new trace, using new or given Y axis
	         if (yaxis == null) yaxis = chart.addYAxis(name);
	         else yaxis.setLabel(name);
	         Color color = chart.getDisplay().getSystemColor(SWT.COLOR_BLUE); 
	         // Add to chart
	         try {
	             while (chart.getNumTraces() > 0)
	                 chart.removeTrace(0);
	             // .. and all but the first Y Axis.
	             while (chart.getNumYAxes() > 1)
	                 chart.removeYAxis(chart.getNumYAxes()-1); // del. last axis
	         	} catch (Exception e) {
	        	 System.out.println("remove Trace error");
	             e.printStackTrace();
	         }
	        	 
	         
	         chart.addTrace(name,seq,color,1,chart.getYAxisIndex(yaxis),
	        		 0,maxFFT,true,Trace.Type.Bars);
	         return yaxis;
	     }
	private YAxis addGaussTrace(String name, YAxis yaxis,double[] data,double[] time,double Gauss[],double B,double A,double m,double d )	  {
        ChartSampleSequenceContainer seq = new ChartSampleSequenceContainer();
        ChartSampleSequenceContainer seqG = new ChartSampleSequenceContainer();
        int i;
        double x=0,y=0;
        
        String info = null;
        if((data==null)||(data.length <2)) {
       	 System.out.println("bad data array");
       	 return null;
        }
        int len=data.length;
        
        double maxCurve =data[0]; 
        double minCurve =data[0]; 
        if (debug) System.out.println("Gauss");
        
        for (i=0; i<len; i++) {
            ChartSample.Type type;
            x= time[i];
            y = data[i]; 
            type = ChartSample.Type.Point;
            if (debug) System.out.println("x= "+x+" y= "+ y );
           	 seq.add(type, x, y, info);
           	 	if(data[i]> maxCurve) maxCurve=data[i];
           	 	if(data[i]< minCurve) minCurve=data[i];
            }	     
        
        
        double maxCurve1 =Gauss[0]; 
        double minCurve1 =Gauss[0];
        for (i=0; i<len; i++) {
            ChartSample.Type type;
            x= time[i];
            y = Gauss[i]; 
            type = ChartSample.Type.Normal;
            if (debug) System.out.println("x= "+x+" y= "+ y );
           	 seqG.add(type, x, y, info);
           	 	if(Gauss[i]> maxCurve1) maxCurve1=Gauss[i];
           	 	if(Gauss[i]< minCurve1) minCurve1=Gauss[i];
            }	  
	
        chart.getXAxis().setValueRange (time[0],time[len-1]);
        
        String AlgoName = new String(String.valueOf(algoSelector.getText()));

        //
        // Memo: time in time[] is seconds from EPOCH, but
        // Date(long) used milisecond, so we have to  * 1000 
        //
	 	 long startLong= (long) (time[0])*1000;
	     Date startD = new Date(startLong);
		 long endLong= (long) (time[len-1])*1000;
		 Date endD = new Date(endLong);
	     chart.getXAxis().setLabel(new String(AlgoName+" Y="+ name+ " numPoints="+len+" "+ startD+" "+ endD+ " delta="+d+ " mean="+m+ " Background="+B+ " Amplit="+A ));
	     String nameFit = new String("Fit for "+ name);
        // Create new trace, using new or given Y axis
        if (yaxis == null) yaxis = chart.addYAxis(name);
        else yaxis.setLabel(name);
        Color colorG = chart.getDisplay().getSystemColor(SWT.COLOR_BLUE); 
        Color color = chart.getDisplay().getSystemColor(SWT.COLOR_BLACK); 
        // Add to chart
        try {
            while (chart.getNumTraces() > 0)
                chart.removeTrace(0);
            // .. and all but the first Y Axis.
            while (chart.getNumYAxes() > 1)
                chart.removeYAxis(chart.getNumYAxes()-1); // del. last axis
        	} catch (Exception e) {
       	 System.out.println("remove Trace error");
            e.printStackTrace();
        }
       	 
        chart.addTrace(name   ,seq, color, 1,chart.getYAxisIndex(yaxis),minCurve,maxCurve,true,Trace.Type.Markers);
        //YAxis  yaxisFit = chart.addYAxis(nameFit);
        chart.addTrace(nameFit,seqG,colorG,1,chart.getYAxisIndex(yaxis),minCurve1,maxCurve1,true,Trace.Type.Lines);
        return yaxis;
    }
	
	private YAxis addXYtrace(String name,String nameX,double[] raw, double[] rawX,double[] time,double[] timeX,  YAxis yaxis)	  {
        ChartSampleSequenceContainer seq = new ChartSampleSequenceContainer();
        int i;
        double x=0,y=0;
        String info = null;
        int len= Math.min(raw.length,rawX.length);
        double maxY=raw[0];
        double maxX=rawX[0];
        double minY=raw[0];
        double minX=rawX[0];

        try {
	        for (i=0; i<len; i++) {
	            ChartSample.Type type;
	            x= rawX[i];
	            y= raw[i]; 
	            type = ChartSample.Type.Normal;
	            if(debug) System.out.println("x= "+x+" y= "+ y );
	            seq.add(type, x, y, info);
	            if(raw[i]> maxY)  maxY=raw[i];
	            if(rawX[i]> maxX) maxX=rawX[i];
	            if(raw[i]< minY)  minY=raw[i];
	            if(rawX[i]< minX) minX=rawX[i];
	        }
	        chart.getXAxis().setValueRange(minX,maxX);
	        String AlgoName = new String(String.valueOf(algoSelector.getText())); 
			long startLong= (long) (time[0])*1000;
			Date startD = new Date(startLong);
			long endLong= (long) (time[len-1])*1000;
			Date endD = new Date(endLong);
	        chart.getXAxis().setLabel(new String(AlgoName+" X="+nameX+ " Y="+ name+ " numPoints="+len +" "+ startD+" "+ endD));
        } catch (Exception e) {
        	System.out.println("addXYtrace error");
        	e.printStackTrace();
        	return null;
        }
        // Create new trace, using new or given Y axis
        if (yaxis == null) yaxis = chart.addYAxis(name);
        else yaxis.setLabel(name);
        Color color = chart.getDisplay().getSystemColor(SWT.COLOR_BLUE); 
        // Add to chart
        try {
            while (chart.getNumTraces() > 0)
                chart.removeTrace(0);
            // .. and all but the first Y Axis.
            while (chart.getNumYAxes() > 1)
                chart.removeYAxis(chart.getNumYAxes()-1); // del. last axis
        } catch (Exception e) {
        	System.out.println("remove Trace error");
        	e.printStackTrace();
        	return null;
        }
       	 
        
        chart.addTrace(name,seq,color,2,chart.getYAxisIndex(yaxis),
       		 minY,maxY,true,Trace.Type.Markers);
        return yaxis;
    }
	
	
	private boolean newData(IProcessVariableWithSample[] pv_name) {
		try {
			int size=pv_name.length;
			if (size < 1) {
				System.out.println(" Error: FFT ->  View -> activateWithPV array with  unpredictable size="+size);
				return false;
			}
			String name=pv_name[0].getName();
			int len=pv_name[0].getSampleValue().length;
			if (len <= 4) {
				System.out.println(" Error: FFT ->  View -> activateWithPV array with unpredictable len="+len);
				return false;
			}
			double timeRange = pv_name[0].getTimeStamp()[len-1] - pv_name[0].getTimeStamp()[0];
			if(timeRange <= 0.0)  {
				System.out.println(" Error: FFT ->  View -> activateWithPV non-positive timeRange="+timeRange);
				return false;
			}
			
			//samplesVector.setSize(samplesArrayMax);	
			samplesVector.add(0,pv_name[0]);
			if (samplesVector.size() >= samplesArrayMax) samplesVector.setSize(samplesArrayMax);
			
			//if only 1 element in stack we have to disable CorelationPlot, otherwise enable it
			if(debug) System.out.println("samplesVector.size()="+samplesVector.size());
			if (samplesVector.size() > 1 )  {
				algoSelector.removeAll();
			    for (Algo p : Algo.values()) {
			    		String item = String.valueOf(p);
			    		algoSelector.add(item);
			    }
			}
				
			ySamplesSelector.add(getSampleLabel(pv_name[0]),0);
			xSamplesSelector.add(getSampleLabel(pv_name[0]),1);
		    ySamplesSelector.select(0);
		    xSamplesSelector.select(0);		    
			if(debug) {
				for (int i=0;i<len;i++)  {
					long timeL= (long) (pv_name[0].getTimeStamp()[i])*1000;
					Date a = new Date(timeL);
					System.out.println("time="+a);	      
				}
			}
			
			return calc(name,pv_name[0].getSampleValue(),pv_name[0].getTimeStamp());
		}       catch (Exception e) {
	    	System.out.println(" Error: FFT ->  View -> newData array exception");
	        //Plugin.logException("activateWithPV", e); //$NON-NLS-1$
	        e.printStackTrace();
	    }
	    return false;
	}
	
	private String getSampleLabel(IProcessVariableWithSample pvAndSample) {
		long timeL= (long) (pvAndSample.getTimeStamp()[0])*1000;
		String name=pvAndSample.getName();
		Date dateFrom = new Date(timeL);
		DateFormat formatter=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
		String shortTime=formatter.format(dateFrom);
		String labelT = new String (name+" " +shortTime);
		return labelT;
	}
	
    //
    // FFT calculation here:
    //
	private boolean calc(String name, double[] raw, double[] time ) {
		int index=getAlgoFFT();
		int len=raw.length;
		if (index != 0) index -= 2;
        double[] fft=   FFTcalculations.dftCalc(raw,len,index);
        
        try {
        	if(chart.getNumYAxes() <1)  addFFTTrace(name, null,fft,time);
        	else addFFTTrace(name, chart.getYAxis(0),fft,time);
        } catch (Exception e) {
        	System.out.println("calcFFT(): Select Algorithm first and try again!");
        	e.printStackTrace();
        	return false;
        }
        chart.redrawTraces();
        return true;
    
}   
	//
    // GaussianFit helper  here:
    //
	
	private boolean calcGauss(String name,double[] raw, double[] time) {
        try {
        	
        	
        	GaussCalculation gC=new GaussCalculation();
        	if (!( gC.GaussCalculationProc(raw, time) )) {
        		System.out.println("calcGauss error");
        		return false;
        	}	
        	double B=gC.getBackground();
        	double A=gC.getAmpl();
        	double	m=gC.getMean();
        	double d=gC.getDelta();
        	double[] gaussArray= gC.getGauss();
        	if(debug)System.out.println("\t\t!!!calcGauss OUT" + "B="+B+" A="+A+" m="+m+" d="+d);
        	if (gaussArray==null) {
        		System.out.println("calcGauss return null arr");
        		return false;
        	}
        	
        	int len=gaussArray.length;
        	if(debug) System.out.println("\t\t!!!calcGaussLEN=" + len);
        	for(int i=0;i<len;i++) {
        		if(debug) System.out.print(" "+gaussArray[i]);
        	}
        		
        	if(chart.getNumYAxes() <1) addGaussTrace(name,null,raw, time,gaussArray,B,A,m,d);
        	else                       addGaussTrace(name,chart.getYAxis(0),raw, time,gaussArray,B,A,m,d);
        } catch (Exception e) {
        	System.out.println("calcCorrelation(): Select Algorithm first and try again!");
        	e.printStackTrace();
        	return false;
        }
        chart.redrawTraces();
        return true;
	}
	//
    // CorrelationPlot helper  here:
    //
	
	private boolean calc(String name, String nameX, double[] raw, double[] rawX,double[] time,double[] timeX ) {
        try {
        	if(chart.getNumYAxes() <1) addXYtrace(name,nameX, raw, rawX,time,timeX,null);
        	else                       addXYtrace(name,nameX, raw, rawX,time,timeX,chart.getYAxis(0));
        } catch (Exception e) {
        	System.out.println("calcCorrelation(): Select Algorithm first and try again!");
        	e.printStackTrace();
        	return false;
        }
        chart.redrawTraces();
        return true;
    
}
	private boolean RecalculateAction() {
		IProcessVariableWithSample pvAndSample=(IProcessVariableWithSample) samplesVector.elementAt(ySamplesSelector.getSelectionIndex());
		//int len = lenCalculator(pvAndSample);
		int len = pvAndSample.getSampleValue().length;
		if(len<=4) {
			System.out.println("Error: FFT ->  View -> RecalculateAction arraySize is very small="+len );
			return false;
		}
		double timeRange = pvAndSample.getTimeStamp()[len-1] - pvAndSample.getTimeStamp()[0];
		if(timeRange <= 0.0)  {
			System.out.println(" Error: FFT ->  View -> RecalculateActionnon-positive timeRange="+timeRange);
			return false;
		}
		
		String name=pvAndSample.getName();
		String CorrelationPlotStr = Algo.CorrelationPlot.toString();
		String GaussStr = Algo.GaussianFit.toString();
		String CurrentLabel = algoSelector.getText();
		if (CurrentLabel != null) {
			
			if(debug) System.out.println("CurrentLabel="+CurrentLabel +" CP="+CorrelationPlotStr+" G="+GaussStr);
		} else  {
			System.out.println("BAD CurrentLabel CP="+CorrelationPlotStr+" G="+GaussStr);	
			return false;
		}
			

		if( CurrentLabel.compareTo(GaussStr) == 0 ) {
			if(debug) System.out.println("GAUSS!!!");
			calcGauss(name, pvAndSample.getSampleValue(), pvAndSample.getTimeStamp());
			return true;
		} 
		if( CurrentLabel.compareTo(CorrelationPlotStr) == 0 ) {
			if(debug) System.out.println("CORR!!!");
			int indX=xSamplesSelector.getSelectionIndex();
			if(indX ==1) {
				System.out.println("Correlation plot need 2 arguments! ");
				return false;
			}
			IProcessVariableWithSample pvAndSampleX=(IProcessVariableWithSample) samplesVector.elementAt(indX-1);
			String nameX=pvAndSampleX.getName();
			if(debug) System.out.println("nameX= "+ nameX);
			calc(name,nameX,pvAndSample.getSampleValue(),pvAndSampleX.getSampleValue(),pvAndSample.getTimeStamp(),pvAndSampleX.getTimeStamp());	
			return true;
		}

			calc(name, pvAndSample.getSampleValue(), pvAndSample.getTimeStamp());

		return true;
	}
	private int getAlgoFFT() {
		int index =  Integer.valueOf(algoSelector.getSelectionIndex()) ;
		return index;
	}
	
	public  static boolean activateWithPV(IProcessVariableWithSample[] pv_name)
    {
        try
        {	
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            //View FFTView = (View) page.showView(View.ID);
            View FFTView = (View) page.showView(View.ID);
            if(FFTView==null) {
            	System.out.println(" Error: activateWithPV NULL-class exception");
            	return false;
            }
            return FFTView.newData(pv_name);
            
        }catch (Exception e)
        {
        	System.out.println(" Error: FFT ->  View -> activateWithPV common exception");
            //Plugin.logException("activateWithPV", e); //$NON-NLS-1$
            e.printStackTrace();
        }
        return false;
    }
	public void setFocus() {}
}

