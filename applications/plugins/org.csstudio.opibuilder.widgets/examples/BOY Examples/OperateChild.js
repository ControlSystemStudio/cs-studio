importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.org.csstudio.swt.xygraph.dataprovider);
importPackage(Packages.org.csstudio.swt.xygraph.figures);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.eclipse.jface.dialogs);

execute();

function execute(){
		var value = ValueUtil.getDouble(pvArray[0].getValue());
		if(value ==0)
			return;
		var xyGraph = widgetController.getWidget("XY Graph").getFigure().getXYGraph();
		xyGraph.setTitle("Simple Example");
		//create a trace data provider, which will provide the data to the trace.
		
		traceDataProvider = xyGraph.getPlotArea().getTraceList().get(0).getDataProvider();
		if(value ==2){
			traceDataProvider.clearTrace();		
			//alert("You clicked the left mouse button!");
		}
		else{
			var xArray = new Array();
			var yArray = new Array();
			for(var i=0; i<200; i++){
				yArray[i] = Math.random()*100;
				xArray[i] = i;
			}
			traceDataProvider.setCurrentXDataArray(xArray);
			traceDataProvider.setCurrentYDataArray(yArray);	
		}
		//create the trace
		//var trace = new Trace("Trace1-XY Plot", xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);			
		
		//set trace property
		//trace.setPointStyle(Trace.PointStyle.XCROSS);
		
		//add the trace to xyGraph
		//xyGraph.addTrace(trace);		

}
