/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.dnd.DropPVtoPVWidgetEditPolicy;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.Activator;
import org.csstudio.opibuilder.widgets.model.XYGraphModel;
import org.csstudio.opibuilder.widgets.model.XYGraphModel.AxisProperty;
import org.csstudio.opibuilder.widgets.model.XYGraphModel.TraceProperty;
import org.csstudio.opibuilder.widgets.util.DataSourceUrl;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.PlotMode;
import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider.UpdateMode;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser2.archive.ArchiveFetchJob;
import org.csstudio.trends.databrowser2.archive.ArchiveFetchJobListener;
import org.csstudio.trends.databrowser2.archive.XYArchiveFetchJob;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.PVSamples;
import org.csstudio.trends.databrowser2.model.RequestType;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.draw2d.IFigure;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;


/**The XYGraph editpart
 * @author Xihui Chen
 *
 */
public class XYGraphEditPart extends AbstractPVWidgetEditPart {

    private List<Axis> axisList;
    private List<Trace> traceList;
    private Map<Integer, List<VType>>cacheDuringLoad = new HashMap<>();

    @Override
    public XYGraphModel getWidgetModel() {
        return (XYGraphModel)getModel();
    }

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        removeEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE);
        installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE,
                new DropPVtoXYGraphEditPolicy());
    }
    
    @Override
    protected IFigure doCreateFigure() {
        final XYGraphModel model = getWidgetModel();
        ToolbarArmedXYGraph xyGraphFigure = new ToolbarArmedXYGraph();
        XYGraph xyGraph = xyGraphFigure.getXYGraph();
        xyGraph.setTitle(model.getTitle());
        xyGraph.setTitleFont(CustomMediaFactory.getInstance().getFont(
                model.getTitleFont().getFontData()));
        xyGraph.getPlotArea().setShowBorder(model.isShowPlotAreaBorder());
        xyGraph.getPlotArea().setBackgroundColor(
                CustomMediaFactory.getInstance().getColor(model.getPlotAreaBackColor()));
        xyGraph.setShowLegend(model.isShowLegend());
        xyGraphFigure.setShowToolbar(model.isShowToolbar());
        xyGraphFigure.setTransparent(model.isTransprent());
        axisList = new ArrayList<Axis>();
        axisList.add(xyGraph.primaryXAxis);
        axisList.add(xyGraph.primaryYAxis);
        traceList = new ArrayList<Trace>();
        //init all axes
        for(int i=0; i<XYGraphModel.MAX_AXES_AMOUNT; i++){
            if(i>=2){
                axisList.add(new Axis("", true));
                if(i<model.getAxesAmount())
                    xyGraphFigure.getXYGraph().addAxis(axisList.get(i));
            }
            for(AxisProperty axisProperty : AxisProperty.values()){
                //there is no primary and y-axis property for primary axes.
                if(i<2 && (axisProperty == AxisProperty.PRIMARY
                        || axisProperty == AxisProperty.Y_AXIS)){
                    continue;
                }
                String propID = XYGraphModel.makeAxisPropID(
                    axisProperty.propIDPre, i);
                setAxisProperty(axisList.get(i), axisProperty,
                        model.getProperty(propID).getPropertyValue());
            }
        }

        //init all traces
        for(int i=0; i<XYGraphModel.MAX_TRACES_AMOUNT; i++) {
        	cacheDuringLoad.put(new Integer(i), new ArrayList<VType>());

            traceList.add(new Trace("", xyGraph.primaryXAxis, xyGraph.primaryYAxis,
                    new  CircularBufferDataProvider(false)));
            if(i<model.getTracesAmount())
                    xyGraph.addTrace(traceList.get(i));
            String xPVPropID = XYGraphModel.makeTracePropID(
                    TraceProperty.XPV.propIDPre, i);
            String yPVPropID = XYGraphModel.makeTracePropID(
                    TraceProperty.YPV.propIDPre, i);
            for(TraceProperty traceProperty : TraceProperty.values()){
                String propID = XYGraphModel.makeTracePropID(
                    traceProperty.propIDPre, i);
                setTraceProperty(traceList.get(i), traceProperty,
                        model.getProperty(propID).getPropertyValue(), xPVPropID, yPVPropID);
            }
        }
        //all values should be buffered
        getPVWidgetEditpartDelegate().setAllValuesBuffered(true);

        //add values from datasource if the execution is in run mode
    	if (getExecutionMode() == ExecutionMode.RUN_MODE) {
        	addValuesFromDatasource();
        }
        	
        return xyGraphFigure;
    }

    /**
     * Method will add to each trace param with plot datasource the plot point from db.
     */
    private void addValuesFromDatasource() {
    	for (int i = 0; i < getWidgetModel().getTracesAmount(); i++) {
    		String pv = "";
			try {
				Boolean pltDataSource = (Boolean) getWidgetModel().getProperty(XYGraphModel.PROP_PLOT_DATA_SOURCE).getPropertyValue();

				List < String > archiveDataSource = null;
				if (pltDataSource) {
					archiveDataSource = DataSourceUrl.getURLs();
				} else {
	                archiveDataSource = (List<String>) getWidgetModel().getProperty(XYGraphModel.PROP_ARCHIVE_DATA_SOURCE).getPropertyValue();
				}
                String propID = XYGraphModel.makeTracePropID(TraceProperty.YPV.propIDPre, i);
                pv = (String) getWidgetModel().getProperty(propID).getPropertyValue();
                
                Integer timeSpan = (Integer) getWidgetModel().getProperty(XYGraphModel.PROP_TIME_SPAN).getPropertyValue();

                //if one required data source is missing  
                if (archiveDataSource == null || archiveDataSource.size() <= 0
                		|| pv == null || pv.length() <= 0 || timeSpan <= 0) {
                	Activator.getLogger().log(Level.INFO, "data source is missing");
                	continue;
                }
                
                //get back the pv on x 
                boolean pvOnX = false;
                if (pv == null || pv.length() <= 0) {
                	propID = XYGraphModel.makeTracePropID(TraceProperty.XPV.propIDPre, i);
                	pv = (String) getWidgetModel().getProperty(propID).getPropertyValue();
                	
                	pvOnX = (pv != null && pv.length() > 0);
                }

                //prepare the pv item for the job  
				Instant end = Instant.now();
				Instant start = end.minusSeconds(timeSpan);
				PVItem pvItem = new PVItem(pv, 0);
				pvItem.setRequestType(RequestType.RAW);

				//add datasource
				int j = 0;
				for (String urlTmp : archiveDataSource) {
					pvItem.addArchiveDataSource(new ArchiveDataSource(urlTmp, j, ""));
				}

				final Trace trace = traceList.get(i);
				final CircularBufferDataProvider dataProvider = (CircularBufferDataProvider) trace.getDataProvider();
				final Boolean pvOnXBln = pvOnX;
				final Integer traceIndex = i;
				
				//launch the job
				XYArchiveFetchJob job = new XYArchiveFetchJob(pvItem, start, end, new ArchiveFetchJobListener() {
					@Override
					public void archiveFetchFailed(ArchiveFetchJob job,
							ArchiveDataSource archive, Exception error) {
						Activator.getLogger().log(Level.WARNING, "Archive fetch failed for pv '" + pvItem.getName() + "' and url '" + archive.getUrl() + "'", error);
					}
					@Override
					public void fetchCompleted(ArchiveFetchJob job) {
						PVSamples pvSamples = job.getPVItem().getSamples();
						if (pvSamples.size() <= 0) {
							return;
						}
						//use UI thread to display (avoid to use a synchronize)
						UIBundlingThread.getInstance().addRunnable(
                                getViewer().getControl().getDisplay(), new Runnable() {
	                                public void run() {
	                                    if(isActive()) {
	                                    	//clear the data
	                                    	dataProvider.clearTrace();
	                                    	//get from cache data load withou db
	                                    	List <VType> listFinal = new ArrayList<VType>();

	                                    	//add data from db
	                                    	int sampleCount = pvSamples.size();
	                                    	for (int cpt = 0; cpt < sampleCount; cpt++) {
	                                    		listFinal.add(pvSamples.get(cpt).getVType());
	                                    	}
	                                    	
	                                    	//add data from cache
	                                    	List <VType> cacheTypeLoadTrace = cacheDuringLoad.get(traceIndex);
	                                    	if (cacheTypeLoadTrace != null) {
	                                    		listFinal.addAll(cacheTypeLoadTrace);
	                                    	}
	                                    	
	                                    	for (VType vtype : listFinal) {
	                                    		if (pvOnXBln) {
	                                    			setXValue(dataProvider, vtype);
	                                    		} else {
	                                    			setYValue(trace, dataProvider, vtype);
	                                    		}
											}
	                                    	
	                                    	cacheDuringLoad.clear();
	                                    }
	                                }
                                });
						Activator.getLogger().log(Level.INFO, "Completed for " + job.getPVItem().getName() + " - size : " + pvSamples.size());
					}
				});
				job.schedule();
			} catch (Exception e) {
				Activator.getLogger().log(Level.INFO, "Error while getting data from datasource for pv " + pv);
			}
		}
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        registerAxisPropertyChangeHandlers();
        registerTracePropertyChangeHandlers();

        //Title
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
                graph.getXYGraph().setTitle((String) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(XYGraphModel.PROP_TITLE, handler);

        //Title Font
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
                graph.getXYGraph().setTitleFont(
                        CustomMediaFactory.getInstance().getFont(((OPIFont)newValue).getFontData()));
                return true;
            }
        };
        setPropertyChangeHandler(XYGraphModel.PROP_TITLE_FONT, handler);

        //Show plot area border
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
                graph.getXYGraph().getPlotArea().setShowBorder((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(XYGraphModel.PROP_SHOW_PLOTAREA_BORDER, handler);

        //Plot area background color
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
                graph.getXYGraph().getPlotArea().setBackgroundColor(
                        CustomMediaFactory.getInstance().getColor(((OPIColor) newValue).getRGBValue()));
                return true;
            }
        };
        setPropertyChangeHandler(XYGraphModel.PROP_PLOTAREA_BACKCOLOR, handler);

        //Transparent
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
                graph.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(XYGraphModel.PROP_TRANSPARENT, handler);


        //Show legend
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
                graph.getXYGraph().setShowLegend((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(XYGraphModel.PROP_SHOW_LEGEND, handler);

        //Show Toolbar
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ToolbarArmedXYGraph graph = (ToolbarArmedXYGraph) refreshableFigure;
                graph.setShowToolbar((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(XYGraphModel.PROP_SHOW_TOOLBAR, handler);

        //trigger pv value
        handler = new IWidgetPropertyChangeHandler() {

            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                for(int i=0; i<getWidgetModel().getTracesAmount(); i++){
                    CircularBufferDataProvider dataProvider =
                        (CircularBufferDataProvider)traceList.get(i).getDataProvider();
                  if( dataProvider.getUpdateMode() == UpdateMode.TRIGGER){
                      dataProvider.triggerUpdate();
                  }
                }
                return false;
            }
        };

        setPropertyChangeHandler(XYGraphModel.PROP_TRIGGER_PV_VALUE, handler);

        registerAxesAmountChangeHandler();
        registerTraceAmountChangeHandler();

    }

    private void registerAxesAmountChangeHandler(){
        final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){

            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                XYGraphModel model = (XYGraphModel)getModel();
                XYGraph xyGraph = ((ToolbarArmedXYGraph)refreshableFigure).getXYGraph();
                int currentAxisAmount = xyGraph.getAxisList().size();
                //add axis
                if((Integer)newValue > currentAxisAmount){
                    for(int i=0; i<(Integer)newValue - currentAxisAmount; i++){
                        for(AxisProperty axisProperty : AxisProperty.values()){
                            String propID = XYGraphModel.makeAxisPropID(
                                axisProperty.propIDPre, i + currentAxisAmount);
                            model.setPropertyVisible(propID, true);
                        }
                        xyGraph.addAxis(axisList.get(i+currentAxisAmount));
                    }
                }else if((Integer)newValue < currentAxisAmount){ //remove axis
                    for(int i=0; i<currentAxisAmount - (Integer)newValue; i++){
                        for(AxisProperty axisProperty : AxisProperty.values()){
                            String propID = XYGraphModel.makeAxisPropID(
                                axisProperty.propIDPre, i+(Integer)newValue);
                            model.setPropertyVisible(propID, false);
                        }
                        xyGraph.removeAxis(axisList.get(i+(Integer)newValue));
                    }
                }
                return true;
            }
        };
        getWidgetModel().getProperty(XYGraphModel.PROP_AXIS_COUNT).
        addPropertyChangeListener(new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent evt) {
            handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
        }
    });
        //setPropertyChangeHandler(XYGraphModel.PROP_AXES_AMOUNT, handler);
    }


    private void registerAxisPropertyChangeHandlers(){
        XYGraphModel model = (XYGraphModel)getModel();
        //set prop handlers and init all the potential axes
        for(int i=0; i<XYGraphModel.MAX_AXES_AMOUNT; i++){

            for(AxisProperty axisProperty : AxisProperty.values()){
                //there is no primary and y-axis property for primary axes.
                if(i<2 && (axisProperty == AxisProperty.PRIMARY
                        || axisProperty == AxisProperty.Y_AXIS)){
                    continue;
                }
                String propID = XYGraphModel.makeAxisPropID(
                    axisProperty.propIDPre, i);
                IWidgetPropertyChangeHandler handler = new AxisPropertyChangeHandler(i, axisProperty);
                setPropertyChangeHandler(propID, handler);
            }
        }

        for(int i=XYGraphModel.MAX_AXES_AMOUNT -1; i>= model.getAxesAmount(); i--){
            for(AxisProperty axisProperty : AxisProperty.values()){
                String propID = XYGraphModel.makeAxisPropID(
                    axisProperty.propIDPre, i);
                model.setPropertyVisible(propID, false);
            }
        }
    }

    private void setAxisProperty(Axis axis, AxisProperty axisProperty, Object newValue){
            switch (axisProperty) {
            case AUTO_SCALE:
                axis.setAutoScale((Boolean)newValue);
                break;
            case VISIBLE:
                axis.setVisible((Boolean)newValue);
                break;
            case TITLE:
                axis.setTitle((String)newValue);
                break;
            case AUTO_SCALE_THRESHOLD:
                axis.setAutoScaleThreshold((Double)newValue);
                break;
            case AXIS_COLOR:
                axis.setForegroundColor(CustomMediaFactory.getInstance().getColor(((OPIColor)newValue).getRGBValue()));
                break;
            case DASH_GRID:
                axis.setDashGridLine((Boolean)newValue);
                break;
            case GRID_COLOR:
                axis.setMajorGridColor(CustomMediaFactory.getInstance().getColor(((OPIColor)newValue).getRGBValue()));
                break;
            case LOG:
                axis.setLogScale((Boolean)newValue);
                break;
            case MAX:
                double lower = (Double) getPropertyValue(
                        XYGraphModel.makeAxisPropID(AxisProperty.MIN.propIDPre, axisList.indexOf(axis)));
                axis.setRange(lower, (Double)newValue);
                break;
            case MIN:
                double upper = (Double) getPropertyValue(
                        XYGraphModel.makeAxisPropID(AxisProperty.MAX.propIDPre, axisList.indexOf(axis)));
                axis.setRange((Double)newValue, upper);
                break;
            case PRIMARY:
                axis.setPrimarySide((Boolean)newValue);
                break;
            case SHOW_GRID:
                axis.setShowMajorGrid((Boolean)newValue);
                break;
            case TIME_FORMAT:
                if((Integer)newValue == 0){
                    axis.setDateEnabled(false);
                    axis.setAutoFormat(true);
                    break;
                }else if((Integer)newValue == 8){
                    axis.setDateEnabled(true);
                    axis.setAutoFormat(true);
                }else {
                    String format = XYGraphModel.TIME_FORMAT_ARRAY[(Integer)newValue];
                    axis.setDateEnabled(true);
                    axis.setFormatPattern(format);
                }
                break;
            case SCALE_FONT:
                axis.setFont(((OPIFont)newValue).getSWTFont());
                break;
            case TITLE_FONT:
                axis.setTitleFont(((OPIFont)newValue).getSWTFont());
                break;
            case Y_AXIS:
                axis.setYAxis((Boolean)newValue);
                break;
            case SCALE_FORMAT:
                if(((String)newValue).trim().equals("")){ //$NON-NLS-1$
                    if(!axis.isDateEnabled())
                        axis.setAutoFormat(true);
                }else{
                    axis.setAutoFormat(false);
                    try {
                        axis.setFormatPattern((String)newValue);
                    } catch (Exception e) {
                        ConsoleService.getInstance().writeError((String)newValue +
                                " is illegal Numeric Format." +
                                " The axis will be auto formatted.");
                        axis.setAutoFormat(true);
                    }
                }
                break;
            default:
                break;
            }
    }

    private void registerTraceAmountChangeHandler(){
        final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){

            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                XYGraphModel model = (XYGraphModel)getModel();
                XYGraph xyGraph = ((ToolbarArmedXYGraph)refreshableFigure).getXYGraph();
                int currentTracesAmount = xyGraph.getPlotArea().getTraceList().size();
                //add trace
                if((Integer)newValue > currentTracesAmount){
                    for(int i=0; i<(Integer)newValue - currentTracesAmount; i++){
                        for(TraceProperty traceProperty : TraceProperty.values()){
                            if(traceProperty == TraceProperty.XPV_VALUE ||
                                    traceProperty == TraceProperty.YPV_VALUE)
                                continue;
                            String propID = XYGraphModel.makeTracePropID(
                                traceProperty.propIDPre, i + currentTracesAmount);
                            model.setPropertyVisible(propID, true);
                        }
                        xyGraph.addTrace(traceList.get(i+currentTracesAmount));
                    }
                }else if((Integer)newValue < currentTracesAmount){ //remove trace
                    for(int i=0; i<currentTracesAmount - (Integer)newValue; i++){
                        for(TraceProperty traceProperty : TraceProperty.values()){
                            String propID = XYGraphModel.makeTracePropID(
                                traceProperty.propIDPre, i+(Integer)newValue);
                            model.setPropertyVisible(propID, false);
                        }
                        xyGraph.removeTrace(traceList.get(i+(Integer)newValue));
                    }
                }
                return true;
            }
        };
        getWidgetModel().getProperty(XYGraphModel.PROP_TRACE_COUNT).
            addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
            }
        });

        //setPropertyChangeHandler(XYGraphModel.PROP_TRACES_AMOUNT, handler);
    }


    private void registerTracePropertyChangeHandlers(){
        XYGraphModel model = (XYGraphModel)getModel();
        //set prop handlers and init all the potential axes
        for(int i=0; i<XYGraphModel.MAX_TRACES_AMOUNT; i++){
            boolean concatenate = (Boolean) getWidgetModel().getProperty(
                    XYGraphModel.makeTracePropID(TraceProperty.CONCATENATE_DATA.propIDPre, i)).getPropertyValue();
            String xPVPropID = XYGraphModel.makeTracePropID(
                    TraceProperty.XPV.propIDPre, i);
            String yPVPropID = XYGraphModel.makeTracePropID(
                    TraceProperty.YPV.propIDPre, i);
            for(TraceProperty traceProperty : TraceProperty.values()){
                final String propID = XYGraphModel.makeTracePropID(
                    traceProperty.propIDPre, i);
                final IWidgetPropertyChangeHandler handler = new TracePropertyChangeHandler(i, traceProperty, xPVPropID, yPVPropID);

                if(concatenate){
                    //cannot use setPropertyChangeHandler because the PV value has to be buffered
                    //which means that it cannot be ignored.
                    getWidgetModel().getProperty(propID).addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(final PropertyChangeEvent evt) {
                            UIBundlingThread.getInstance().addRunnable(
                                getViewer().getControl().getDisplay(), new Runnable() {
	                                public void run() {
	                                    if(isActive()) {
	                                        handler.handleChange(
	                                            evt.getOldValue(), evt.getNewValue(), getFigure());
	                                    }
                                    }
                                });
                        }
                    });
                }else
                    setPropertyChangeHandler(propID, handler);
            }
        }
        for(int i=XYGraphModel.MAX_TRACES_AMOUNT -1; i>= model.getTracesAmount(); i--){
            for(TraceProperty traceProperty : TraceProperty.values()){
                String propID = XYGraphModel.makeTracePropID(
                    traceProperty.propIDPre, i);
                model.setPropertyVisible(propID, false);
            }
        }
    }

    private void setTraceProperty(Trace trace, TraceProperty traceProperty, Object newValue, String xPVPropID, String yPVPropID){
        CircularBufferDataProvider dataProvider = (CircularBufferDataProvider)trace.getDataProvider();
        switch (traceProperty) {
        case ANTI_ALIAS:
            trace.setAntiAliasing((Boolean)newValue);
            break;
        case BUFFER_SIZE:
            dataProvider.setBufferSize((Integer)newValue);
            break;
//        case CHRONOLOGICAL:
            //dataProvider.setChronological((Boolean)newValue);
//            break;
        //case CLEAR_TRACE:
        //    if((Boolean)newValue)
        //        dataProvider.clearTrace();
        //    break;
        case LINE_WIDTH:
            trace.setLineWidth((Integer)newValue);
            break;
        case NAME:
            trace.setName((String)newValue);
            break;
        case PLOTMODE:
            dataProvider.setPlotMode(PlotMode.values()[(Integer)newValue]);
            break;
        case POINT_SIZE:
            trace.setPointSize((Integer)newValue);
            break;
        case POINT_STYLE:
            trace.setPointStyle(PointStyle.values()[(Integer)newValue]);
            break;
        case TRACE_COLOR:
            trace.setTraceColor(CustomMediaFactory.getInstance().getColor(((OPIColor)newValue).getRGBValue()));
            break;
        case TRACE_TYPE:
            trace.setTraceType(TraceType.values()[(Integer)newValue]);
            break;
        case CONCATENATE_DATA:
            dataProvider.setConcatenate_data((Boolean)newValue);
            break;
    //    case TRIGGER_VALUE:
            //dataProvider.triggerUpdate();
    //        break;
        case UPDATE_DELAY:
            dataProvider.setUpdateDelay((Integer)newValue);
            break;
        case UPDATE_MODE:
            dataProvider.setUpdateMode(UpdateMode.values()[(Integer)newValue]);
            break;
        case XAXIS_INDEX:
            if(!axisList.get((Integer)newValue).isYAxis())
                trace.setXAxis(axisList.get((Integer)newValue));
            break;
        case YAXIS_INDEX:
            if(axisList.get((Integer)newValue).isYAxis())
                trace.setYAxis(axisList.get((Integer)newValue));
            break;
        case XPV:
            if(newValue.toString()!= null && newValue.toString().trim().length() > 0)
                dataProvider.setChronological(false);
            else
                dataProvider.setChronological(true);
            break;
        case XPV_VALUE:
            if(newValue == null || !(newValue instanceof VType))
                break;
            if(dataProvider.isConcatenate_data()){
                IPV pv = getPV(xPVPropID);
                if (pv != null) {
                    for (VType o : pv.getAllBufferedValues()) {
                        setXValue(dataProvider, o);
                    }
                }
            }else
                setXValue(dataProvider, (VType) newValue);
            break;
        case YPV_VALUE:
            if(newValue == null || !(newValue instanceof VType))
                break;
            if(dataProvider.isConcatenate_data()){
                IPV pv = getPV(yPVPropID);
                if (pv != null) {
                    //values are set during figure construction time, when the pv might not even exist yet
                    for(VType o:pv.getAllBufferedValues()){
                        setYValue(trace, dataProvider, o);
                    }
                }
            } else {
                setYValue(trace, dataProvider, (VType) newValue);
            }
            break;
        case VISIBLE:
            trace.setVisible((Boolean)newValue);
            break;
        default:
            break;
        }
    }

    private void setXValue(CircularBufferDataProvider dataProvider, VType value) {
        if(VTypeHelper.getSize(value) > 1){
            dataProvider.setCurrentXDataArray(VTypeHelper.getDoubleArray(value));
        }else
            dataProvider.setCurrentXData(VTypeHelper.getDouble(value));
    }

    private void setYValue(Trace trace,
            CircularBufferDataProvider dataProvider, VType y_value) {
        if(VTypeHelper.getSize(y_value) == 1 && trace.getXAxis().isDateEnabled() && dataProvider.isChronological()) {
        	long time = yValueTimeStampToLong(y_value);
        	//verification that the last add is before the next that will be added
        	int size = dataProvider.getSize() - 1;
        	if (size > 0) {
	        	ISample sample = dataProvider.getSample(size);
	            if (sample.getXValue() > time) {
	            	return;
	            }
            }
            dataProvider.setCurrentYData(VTypeHelper.getDouble(y_value), time);
        }else{
            if(VTypeHelper.getSize(y_value) > 1){
                dataProvider.setCurrentYDataArray(VTypeHelper.getDoubleArray(y_value));
            }else {
                dataProvider.setCurrentYData(VTypeHelper.getDouble(y_value));
            }
        }
    }
    
    
    private long yValueTimeStampToLong(VType y_value) {
    	if (y_value == null) {
    		return Long.MAX_VALUE;
    	}
    	Timestamp timestamp = VTypeHelper.getTimestamp(y_value);
        return timestamp.getSec() * 1000 + timestamp.getNanoSec()/1000000;
    }

    class AxisPropertyChangeHandler implements IWidgetPropertyChangeHandler {
        private int axisIndex;
        private AxisProperty axisProperty;
        public AxisPropertyChangeHandler(int axisIndex, AxisProperty axisProperty) {
            this.axisIndex = axisIndex;
            this.axisProperty = axisProperty;
        }
        public boolean handleChange(Object oldValue, Object newValue,
                IFigure refreshableFigure) {
            Axis axis = axisList.get(axisIndex);
            setAxisProperty(axis, axisProperty, newValue);
            return true;
        }
    }

    class TracePropertyChangeHandler implements IWidgetPropertyChangeHandler {
        private int traceIndex;
        private TraceProperty traceProperty;
        private String xPVPropID;
        private String yPVPropID;
        public TracePropertyChangeHandler(int traceIndex, TraceProperty traceProperty, String xPVPropID, String yPVPropID) {
            this.traceIndex = traceIndex;
            this.traceProperty = traceProperty;
            this.xPVPropID = xPVPropID;
            this.yPVPropID = yPVPropID;
        }
        public boolean handleChange(Object oldValue, Object newValue,
                IFigure refreshableFigure) {
            Trace trace = traceList.get(traceIndex);

            List <VType> samples = cacheDuringLoad.get(new Integer(traceIndex));
            if (samples != null) {
            	samples.add((VType) newValue);
            }

            setTraceProperty(trace, traceProperty, newValue, xPVPropID, yPVPropID);
            return false;
        }
    }

    @Override
    public void setValue(Object value) {
        throw new RuntimeException("XY Graph does not accept value");
    }

    @Override
    public Object getValue() {
        throw new RuntimeException("XY Graph does not have value");
    }

    /**
     * Clear the graph by deleting data in buffer.
     */
    public void clearGraph(){
        for(int i=0; i<getWidgetModel().getTracesAmount(); i++){
            ((CircularBufferDataProvider)traceList.get(i).getDataProvider()).clearTrace();
        }
    }

    public double[] getXBuffer(int i){
        CircularBufferDataProvider dataProvider = (CircularBufferDataProvider)traceList.get(i).getDataProvider();
        double[] XBuffer = new double[dataProvider.getSize()];
        for (int j = 0; j < dataProvider.getSize(); j++) {
            XBuffer[j] = dataProvider.getSample(j).getXValue();
        }
        return XBuffer;
    }

    public double[] getYBuffer(int i){
        CircularBufferDataProvider dataProvider = (CircularBufferDataProvider)traceList.get(i).getDataProvider();
        double[] YBuffer = new double[dataProvider.getSize()];
        for (int j = 0; j < dataProvider.getSize(); j++) {
            YBuffer[j] = dataProvider.getSample(j).getYValue();
        }
        return YBuffer;
    }
}
