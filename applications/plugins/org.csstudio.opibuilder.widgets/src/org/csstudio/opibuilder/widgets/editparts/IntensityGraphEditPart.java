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

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.IntensityGraphModel;
import org.csstudio.opibuilder.widgets.model.IntensityGraphModel.AxisProperty;
import org.csstudio.opibuilder.widgets.model.IntensityGraphModel.ROIProperty;
import org.csstudio.opibuilder.widgets.util.ListNumberWrapper;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure.IProfileDataChangeLisenter;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VType;

/**The widget editpart of intensity graph widget.
 * @author Xihui Chen
 *
 */
public class IntensityGraphEditPart extends AbstractPVWidgetEditPart {


	private boolean innerTrig;
	
	private IntensityGraphFigure graph;
	
	@Override
	protected IFigure doCreateFigure() {
		IntensityGraphModel model = getWidgetModel();
		graph = new IntensityGraphFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
		graph.setMin(model.getMinimum());
		graph.setMax(model.getMaximum());
		graph.setDataWidth(model.getDataWidth());
		graph.setDataHeight(model.getDataHeight());
		graph.setColorMap(model.getColorMap());
		graph.setShowRamp(model.isShowRamp());
		graph.setCropLeft(model.getCropLeft());
		graph.setCropRight(model.getCropRight());
		graph.setCropTop(model.getCropTOP());
		graph.setCropBottom(model.getCropBottom());
		graph.setInRGBMode(model.isRGBMode());
		graph.setColorDepth(model.getColorDepth());
		graph.setSingleLineProfiling(model.isSingleLineProfiling());
		graph.setROIColor(model.getROIColor().getSWTColor());
		//init X-Axis
		for(AxisProperty axisProperty : AxisProperty.values()){
			String propID = IntensityGraphModel.makeAxisPropID(
					IntensityGraphModel.X_AXIS_ID, axisProperty.propIDPre);
			setAxisProperty(graph.getXAxis(), axisProperty, model.getPropertyValue(propID));
		}
		//init Y-Axis
		for(AxisProperty axisProperty : AxisProperty.values()){
			String propID = IntensityGraphModel.makeAxisPropID(
					IntensityGraphModel.Y_AXIS_ID, axisProperty.propIDPre);
			setAxisProperty(graph.getYAxis(), axisProperty, model.getPropertyValue(propID));
		}
		//add profile data listener
		if(getExecutionMode() == ExecutionMode.RUN_MODE &&
				(model.getHorizonProfileYPV().trim().length() >0 ||
						model.getVerticalProfileYPV().trim().length() > 0)){
			graph.addProfileDataListener(new IProfileDataChangeLisenter(){

				public void profileDataChanged(double[] xProfileData,
						double[] yProfileData, Range xAxisRange, Range yAxisRange) {
					//horizontal
					setPVValue(IntensityGraphModel.PROP_HORIZON_PROFILE_Y_PV_NAME, xProfileData);
					double[] horizonXData = new double[xProfileData.length];
					double d = (xAxisRange.getUpper() - xAxisRange.getLower())/(xProfileData.length-1);
					for(int i=0; i<xProfileData.length; i++){
						horizonXData[i] = xAxisRange.getLower() + d *i;
					}
					setPVValue(IntensityGraphModel.PROP_HORIZON_PROFILE_X_PV_NAME, horizonXData);
					//vertical
					setPVValue(IntensityGraphModel.PROP_VERTICAL_PROFILE_Y_PV_NAME, yProfileData);
					double[] verticalXData = new double[yProfileData.length];
					d = (yAxisRange.getUpper() - yAxisRange.getLower())/(yProfileData.length-1);
					for(int i=0; i<yProfileData.length; i++){
						verticalXData[i] = yAxisRange.getUpper() - d*i;
					}
					setPVValue(IntensityGraphModel.PROP_VERTICAL_PROFILE_X_PV_NAME, verticalXData);
				}

			});
		}
		
		updatePropSheet();

		return graph;
	}

	@Override
	public IntensityGraphModel getWidgetModel() {
		return (IntensityGraphModel)getModel();
	}

		/**
		* @param actionsFromPV
		*/
	private void updatePropSheet() {
		boolean rgbMode = getWidgetModel().isRGBMode();
		getWidgetModel().setPropertyVisible(
				IntensityGraphModel.PROP_COLOR_DEPTH, rgbMode);
		getWidgetModel().setPropertyVisible(
				IntensityGraphModel.PROP_COLOR_MAP, !rgbMode);
		getWidgetModel().setPropertyVisible(
				IntensityGraphModel.PROP_SHOW_RAMP, !rgbMode);
		
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		innerUpdateGraphAreaSizeProperty();
		registerAxisPropertyChangeHandler();
		registerROIPropertyChangeHandlers();
		registerROIAmountChangeHandler();
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				if(newValue == null)
					return false;
				VType value = (VType)newValue;
				
					if(value instanceof VNumberArray){
						setValue(((VNumberArray)value).getData());
						return false;
					}
							
				((IntensityGraphFigure)figure).setDataArray(VTypeHelper.getDoubleArray(value));

				return false;
			}
		};

		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);


		getWidgetModel().getProperty(IntensityGraphModel.PROP_MIN).addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						((IntensityGraphFigure)figure).setMin((Double)evt.getNewValue());
						figure.repaint();
						innerUpdateGraphAreaSizeProperty();
					}
				});

		getWidgetModel().getProperty(IntensityGraphModel.PROP_MAX).addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						((IntensityGraphFigure)figure).setMax((Double)evt.getNewValue());
						figure.repaint();
						innerUpdateGraphAreaSizeProperty();
					}
				});

		getWidgetModel().getProperty(IntensityGraphModel.PROP_BORDER_STYLE).removeAllPropertyChangeListeners();
		getWidgetModel().getProperty(IntensityGraphModel.PROP_BORDER_STYLE).addPropertyChangeListener(
				new PropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent evt) {
						figure.setBorder(
								BorderFactory.createBorder(BorderStyle.values()[(Integer)evt.getNewValue()],
								getWidgetModel().getBorderWidth(), getWidgetModel().getBorderColor(),
								getWidgetModel().getName()));
						innerUpdateGraphAreaSizeProperty();
					}
				});

		getWidgetModel().getProperty(IntensityGraphModel.PROP_BORDER_WIDTH).removeAllPropertyChangeListeners();
		getWidgetModel().getProperty(IntensityGraphModel.PROP_BORDER_WIDTH).addPropertyChangeListener(
				new PropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent evt) {
						figure.setBorder(
								BorderFactory.createBorder(getWidgetModel().getBorderStyle(),
								(Integer)evt.getNewValue(), getWidgetModel().getBorderColor(),
								getWidgetModel().getName()));
						innerUpdateGraphAreaSizeProperty();
					}
				});

		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setDataWidth((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_DATA_WIDTH, handler);

		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setDataHeight((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_DATA_HEIGHT, handler);

		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setColorMap((ColorMap)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_COLOR_MAP, handler);

		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setCropLeft((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_CROP_LEFT, handler);

		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setCropRight((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_CROP_RIGHT, handler);

		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setCropTop((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_CROP_TOP, handler);

		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setCropBottom((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_CROP_BOTTOM, handler);



		getWidgetModel().getProperty(IntensityGraphModel.PROP_SHOW_RAMP).addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						((IntensityGraphFigure)getFigure()).setShowRamp((Boolean)evt.getNewValue());
						Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();
						innerTrig = true;
						getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH,
								getWidgetModel().getWidth() - d.width);
						innerTrig = false;
					}
		});

		getWidgetModel().getProperty(IntensityGraphModel.PROP_WIDTH).addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){ // if it is not triggered from inner
							innerTrig = true;
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH,
								((Integer)evt.getNewValue()) - d.width);
							innerTrig = false; // reset innerTrig to false after each inner triggering
						}else //if it is triggered from inner, do nothing
							innerTrig = false;
					}
		});

		getWidgetModel().getProperty(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH).addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){
							innerTrig = true;
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_WIDTH,
									((Integer)evt.getNewValue()) + d.width);
							innerTrig = false; // reset innerTrig to false after each inner triggering
						}else
							innerTrig = false;
					}
		});


		getWidgetModel().getProperty(IntensityGraphModel.PROP_HEIGHT).addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){
							innerTrig = true;
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_HEIGHT,
								((Integer)evt.getNewValue()) - d.height);
							innerTrig = false; // reset innerTrig to false after each inner triggering
						}else
							innerTrig = false;
					}
		});

		getWidgetModel().getProperty(IntensityGraphModel.PROP_GRAPH_AREA_HEIGHT).addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){
							innerTrig = true;
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_HEIGHT,
								((Integer)evt.getNewValue()) + d.height);
							innerTrig = false; // reset innerTrig to false after each inner triggering
						}else
							innerTrig = false;
					}
		});		

	
		getWidgetModel().getProperty(IntensityGraphModel.PROP_RGB_MODE).addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updatePropSheet();
				((IntensityGraphFigure)getFigure()).setInRGBMode((Boolean)(evt.getNewValue()));				
			}
		});

		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((IntensityGraphFigure)getFigure()).setColorDepth(getWidgetModel().getColorDepth());
				return false;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_COLOR_DEPTH, handler);
		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((IntensityGraphFigure)getFigure()).setSingleLineProfiling((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_SINGLE_LINE_PROFILING, handler);
		

		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((IntensityGraphFigure)getFigure()).setROIColor(((OPIColor)newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_ROI_COLOR, handler);
	}

	private synchronized void innerUpdateGraphAreaSizeProperty(){
		Dimension d = ((IntensityGraphFigure)figure).getGraphAreaInsets();
		innerTrig = true;
		getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH,
				getWidgetModel().getSize().width - d.width);
		innerTrig = true; // recover innerTrig
		getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_HEIGHT,
				getWidgetModel().getSize().height - d.height);
		innerTrig = false; // reset innerTrig to false after each inner triggering
	}
	
	
	private void registerROIAmountChangeHandler() {
		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				int currentCount = (Integer) evt.getOldValue();
				int newCount = (Integer) evt.getNewValue();
				if (newCount > currentCount) {
					for (int i = currentCount; i < newCount; i++) {
						for (ROIProperty roiProperty : ROIProperty.values()) {
							if (roiProperty != ROIProperty.XPV_VALUE
									&& roiProperty != ROIProperty.YPV_VALUE
									&& roiProperty != ROIProperty.WPV_VALUE
									&& roiProperty != ROIProperty.HPV_VALUE) {
								String propID = IntensityGraphModel.makeROIPropID(
										roiProperty.propIDPre, i);
								getWidgetModel().setPropertyVisible(propID, true);
							}
						}
						final int roiIndex = i;
						graph.addROI(getROIName(roiIndex), new IntensityGraphFigure.IROIListener() {
							@Override
							public void roiUpdated(int xIndex, int yIndex, int width, int height) {
								String propID = IntensityGraphModel.makeROIPropID(
										ROIProperty.XPV.propIDPre, roiIndex);
								setPVValue(propID, xIndex);
								propID = IntensityGraphModel.makeROIPropID(
										ROIProperty.YPV.propIDPre, roiIndex);
								setPVValue(propID, yIndex);
								propID = IntensityGraphModel.makeROIPropID(
										ROIProperty.WPV.propIDPre, roiIndex);
								setPVValue(propID, width);
								propID = IntensityGraphModel.makeROIPropID(
										ROIProperty.HPV.propIDPre, roiIndex);
								setPVValue(propID, height);
							}
						}, new IntensityGraphFigure.IROIInfoProvider() {

							@Override
							public String getROIInfo(int xIndex, int yIndex, int width, int height) {
								String propID = IntensityGraphModel.makeROIPropID(
										ROIProperty.TITLE.propIDPre, roiIndex);
								return (String) getPropertyValue(propID);
							}
						});
					}
				} else if (newCount < currentCount) {
					for (int i = currentCount - 1; i >= newCount; i--) {
						graph.removeROI(getROIName(i));
						for (ROIProperty roiProperty : ROIProperty.values()) {
							String propID = IntensityGraphModel.makeROIPropID(
									roiProperty.propIDPre, i);
							getWidgetModel().setPropertyVisible(propID, false);
						}
					}
				}
			}
		};
		AbstractWidgetProperty countProperty = getWidgetModel().getProperty(IntensityGraphModel.PROP_ROI_COUNT);
		countProperty.addPropertyChangeListener(listener);
		
		//init
		int currentCount = (Integer) getPropertyValue(IntensityGraphModel.PROP_ROI_COUNT);
		listener.propertyChange(new PropertyChangeEvent(countProperty, IntensityGraphModel.PROP_ROI_COUNT, 0, 
				currentCount));
		for(int i=0; i<currentCount; i++){
			for(final ROIProperty roiProperty: ROIProperty.values()){
				String propID = IntensityGraphModel.makeROIPropID(roiProperty.propIDPre, i);
				Object propertyValue = getPropertyValue(propID);
				if(propertyValue !=null)
					setROIProperty(getROIName(i), roiProperty, propertyValue);
			}
		}
		
		
	}
	
	private static String getROIName(int index){
		return "ROI_" + index; //$NON-NLS-1$
	}

	private void registerROIPropertyChangeHandlers(){
		for(int i=0; i<IntensityGraphModel.MAX_ROIS_AMOUNT; i++){
			final String roiName = getROIName(i);
			for(final ROIProperty roiProperty: ROIProperty.values()){
				String propID = IntensityGraphModel.makeROIPropID(roiProperty.propIDPre, i);
				if(i>=(Integer)getPropertyValue(IntensityGraphModel.PROP_ROI_COUNT)){
					getWidgetModel().setPropertyVisible(propID, false);
				}
				setPropertyChangeHandler(propID, 
						new IWidgetPropertyChangeHandler() {
					
					@Override
					public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
						setROIProperty(roiName, roiProperty, newValue);
						return false;
					}					
				});
			}
		}
	}
	
	private void setROIProperty(final String roiName, final ROIProperty roiProperty, Object newValue) {
		switch (roiProperty) {
		case TITLE:
			break;
		case VISIBLE:
			graph.setROIVisible(roiName, (Boolean) newValue);
			break;
		case XPV_VALUE:
			graph.getROI(roiName).setROIDataBoundsX((int) VTypeHelper.getDouble((VType) newValue));
			break;
		case YPV_VALUE:
			graph.getROI(roiName).setROIDataBoundsY((int) VTypeHelper.getDouble((VType) newValue));
			break;
		case WPV_VALUE:
			graph.getROI(roiName).setROIDataBoundsW((int) VTypeHelper.getDouble((VType) newValue));
			break;
		case HPV_VALUE:
			graph.getROI(roiName).setROIDataBoundsH((int) VTypeHelper.getDouble((VType) newValue));
			break;
		default:
			break;
		}
	}
	private void registerAxisPropertyChangeHandler(){
		for(String axisID : new String[]{IntensityGraphModel.X_AXIS_ID, IntensityGraphModel.Y_AXIS_ID}){
			for(AxisProperty axisProperty : AxisProperty.values()){
				final IWidgetPropertyChangeHandler handler =
					new AxisPropertyChangeHandler(
							axisID.equals(IntensityGraphModel.X_AXIS_ID)?
									((IntensityGraphFigure)getFigure()).getXAxis():
									((IntensityGraphFigure)getFigure()).getYAxis(),
									axisProperty);
				//must use listener instead of handler because the prop sheet need to be
				//refreshed immediately.
				getWidgetModel().getProperty(IntensityGraphModel.makeAxisPropID(
						axisID, axisProperty.propIDPre)).
							addPropertyChangeListener(new PropertyChangeListener() {
								public void propertyChange(PropertyChangeEvent evt) {
									handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
									UIBundlingThread.getInstance().addRunnable(
											getViewer().getControl().getDisplay(), new Runnable(){
										public void run() {
											getFigure().repaint();
										}
									});
							}
						});

			}
		}

	}

	private void setAxisProperty(Axis axis, AxisProperty axisProperty, Object newValue){
		switch (axisProperty) {
		case TITLE:
			axis.setTitle((String)newValue);
			break;
		case AXIS_COLOR:
			axis.setForegroundColor(CustomMediaFactory.getInstance().getColor(((OPIColor)newValue).getRGBValue()));
			break;
		case MAX:
			String axisID = (axis == graph.getXAxis())? 
					IntensityGraphModel.X_AXIS_ID : IntensityGraphModel.Y_AXIS_ID;				
			double lower = (Double) getPropertyValue(
					IntensityGraphModel.makeAxisPropID(axisID, AxisProperty.MIN.propIDPre));
			axis.setRange(lower, (Double)newValue);
			break;
		case MIN:
			String axisID2 = (axis == graph.getXAxis())? 
					IntensityGraphModel.X_AXIS_ID : IntensityGraphModel.Y_AXIS_ID;				
			double upper = (Double) getPropertyValue(
					IntensityGraphModel.makeAxisPropID(axisID2, AxisProperty.MAX.propIDPre));
			axis.setRange((Double)newValue, upper);
			break;
		case SCALE_FONT:
			axis.setFont(((OPIFont)newValue).getSWTFont());
			break;
		case TITLE_FONT:
			axis.setTitleFont(((OPIFont)newValue).getSWTFont());
			break;
		case MAJOR_TICK_STEP_HINT:
			axis.setMajorTickMarkStepHint((Integer)newValue);
			break;
		case SHOW_MINOR_TICKS:
			axis.setMinorTicksVisible((Boolean)newValue);
			break;
		case VISIBLE:
			axis.setVisible((Boolean)newValue);
			break;
		default:
			break;
		}
	}

	public void setColorMap(String mapName){
		for(PredefinedColorMap map : ColorMap.PredefinedColorMap.values()){
			if(map.toString().equals(mapName)){
				setPropertyValue(IntensityGraphModel.PROP_COLOR_MAP, new ColorMap(map, true, true));
			break;
			}
		}

	}

	@Override
	public double[] getValue() {
		return ((IntensityGraphFigure)getFigure()).getDataArray();
	}

	
	@Override
	public void setValue(Object value) {
		if(value instanceof double[]){
			((IntensityGraphFigure)getFigure()).setDataArray((double[]) value);
		}else if(value instanceof ListNumber)
			((IntensityGraphFigure)getFigure()).setDataArray(
					new ListNumberWrapper((ListNumber)value));
		else if(value instanceof short[]){
			((IntensityGraphFigure)getFigure()).setDataArray((short[]) value);
		}else if(value instanceof byte[]){
			((IntensityGraphFigure)getFigure()).setDataArray((byte[]) value);
		}
		else if(value instanceof float[]){
			((IntensityGraphFigure)getFigure()).setDataArray((float[]) value);
		}
		else if(value instanceof long[]){
			((IntensityGraphFigure)getFigure()).setDataArray((long[]) value);
		}
		else if(value instanceof int[]){
			((IntensityGraphFigure)getFigure()).setDataArray((int[]) value);
		}
		
		else
			super.setValue(value);
	}


	
	@Override
	public void deactivate() {
		((IntensityGraphFigure)getFigure()).dispose();
		super.deactivate();
	}

	class AxisPropertyChangeHandler implements IWidgetPropertyChangeHandler {
		private AxisProperty axisProperty;
		private Axis axis;
		public AxisPropertyChangeHandler(Axis axis, AxisProperty axisProperty) {
			this.axis = axis;
			this.axisProperty = axisProperty;
		}
		public boolean handleChange(Object oldValue, Object newValue,
				IFigure refreshableFigure) {
			setAxisProperty(axis, axisProperty, newValue);
			innerTrig = true;
			innerUpdateGraphAreaSizeProperty();
			axis.revalidate();
			return true;
		}
	}

}
