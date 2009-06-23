package org.csstudio.swt.xygraph.toolbar;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.Annotation.CursorLineStyle;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**This will help to create the necessary widgets 
 * to configure an annotation's properties.
 * @author Xihui Chen
 *
 */
public class AnnotationConfigPage {
	private XYGraph xyGraph;
	private Annotation annotation;
	private Text nameText;
	private Button snapToTrace;
	private Button useDefaultColorButton;
	private Combo xAxisOrTraceCombo;
	private Combo yAxisCombo;
	private ColorSelector colorSelector;
	private Font font;
	private Combo cursorLineCombo;
	private Button showNameButton;
	private Button showSampleInfoButton;
	private Button showPositionButton;
	private Label fontLabel;
	private Composite composite;
	private Label xAxisLabel;
	private Label yAxisLabel;
	private Label colorLabel;
	
	public AnnotationConfigPage(XYGraph xyGraph, Annotation annotation) {
		this.xyGraph = xyGraph;
		this.annotation = annotation;
		font = annotation.getFont();
	}
	
	public void createPage(final Composite composite){
		this.composite = composite;
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(3, false));
		GridData gd;
		GridData labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);		
		
		final Label nameLabel = new Label(composite, 0);
		nameLabel.setText("Name: ");
		nameLabel.setLayoutData(labelGd);
		
		nameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		nameText.setLayoutData(gd);		
		
		snapToTrace = new Button(composite, SWT.CHECK);
		snapToTrace.setText("Snap to Trace");		
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 3, 2);
		snapToTrace.setLayoutData(gd);
		
		xAxisLabel = new Label(composite, 0);
		labelGd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);	
		xAxisLabel.setLayoutData(labelGd);		
		
		xAxisOrTraceCombo = new Combo(composite, SWT.DROP_DOWN);		
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1);
		xAxisOrTraceCombo.setLayoutData(gd);		
		
		yAxisLabel = new Label(composite, 0);
		yAxisLabel.setText("Y-Axis: ");	
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);	
		yAxisLabel.setLayoutData(labelGd);
		
		yAxisCombo = new Combo(composite, SWT.DROP_DOWN);		
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1);
		yAxisCombo.setLayoutData(gd);
		
		//snapToTrace listener
		snapToTrace.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {				
					xAxisLabel.setText(snapToTrace.getSelection()?
							"Trace: " : "X-Axis: ");
					xAxisOrTraceCombo.removeAll();
					if(snapToTrace.getSelection()){
						for(Trace trace : xyGraph.getPlotArea().getTraceList())
							xAxisOrTraceCombo.add(trace.getName());
					}else{
						for(Axis axis : xyGraph.getXAxisList())
							xAxisOrTraceCombo.add(axis.getTitle());
					}
					xAxisOrTraceCombo.select(0);
					if(annotation.isFree() && !snapToTrace.getSelection())
						xAxisOrTraceCombo.select(
								xyGraph.getXAxisList().indexOf(annotation.getXAxis()));
					else if(!annotation.isFree() && snapToTrace.getSelection())
						xAxisOrTraceCombo.select(xyGraph.getPlotArea().
								getTraceList().indexOf(annotation.getTrace()));
					
					yAxisLabel.setVisible(!snapToTrace.getSelection());
					yAxisCombo.setVisible(!snapToTrace.getSelection());	
					composite.layout(true, true);
			}
		});
		//annotation color
		useDefaultColorButton = new Button(composite, SWT.CHECK);
		useDefaultColorButton.setText("Use Y-Axis color as annotation color");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false, 3, 1);
		useDefaultColorButton.setLayoutData(gd);		
		
		colorLabel = new Label(composite, 0);
		colorLabel.setText("Color:");		
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);	
		colorLabel.setLayoutData(labelGd);
		
		colorSelector = new ColorSelector(composite);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
		colorSelector.getButton().setLayoutData(gd);		
		useDefaultColorButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorSelector.getButton().setVisible(!useDefaultColorButton.getSelection());
				colorLabel.setVisible(!useDefaultColorButton.getSelection());
			}
		});
		
		fontLabel = new Label(composite, 0);		
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);	
		fontLabel.setLayoutData(labelGd);
		
		final Button fontButton = new Button(composite, SWT.PUSH);
		fontButton.setText("Change...");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
		fontButton.setLayoutData(gd);
		fontButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FontDialog fontDialog = new FontDialog(composite.getShell());
				if(font != null)
					fontDialog.setFontList(font.getFontData());
				FontData fontData = fontDialog.open();
				if(fontData != null){
					font = XYGraphMediaFactory.getInstance().getFont(fontData);
					fontLabel.setFont(font);
					fontLabel.setText("Font: " + fontData.getName());
					composite.getShell().layout(true, true);
				}
			}
		});
		
		
		final Label cursorLineLabel = new Label(composite, 0);
		cursorLineLabel.setText("Cursor Line Style: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);	
		cursorLineLabel.setLayoutData(labelGd);
		
		cursorLineCombo = new Combo(composite, SWT.DROP_DOWN);
		cursorLineCombo.setItems(CursorLineStyle.stringValues());		
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
		cursorLineCombo.setLayoutData(gd);
		
		showNameButton = new Button(composite, SWT.CHECK);		
		showNameButton.setText("Show Name");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 1);
		showNameButton.setLayoutData(gd);
		
		showSampleInfoButton = new Button(composite, SWT.CHECK);		
		showSampleInfoButton.setText("Show Sample Infomation");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 1);
		showSampleInfoButton.setLayoutData(gd);
		
		showPositionButton = new Button(composite, SWT.CHECK);
		showPositionButton.setText("Show Position");
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 1);
		showPositionButton.setLayoutData(gd);
		initialize();
	}
	
	/**
	 * @return the composite
	 */
	public Composite getComposite() {
		return composite;
	}

	public void applyChanges(){
		annotation.setName(nameText.getText());		
		if(snapToTrace.getSelection())			
			annotation.setTrace(xyGraph.getPlotArea().getTraceList().get(
					xAxisOrTraceCombo.getSelectionIndex()));			
		else
			annotation.setFree(xyGraph.getXAxisList().get(
					xAxisOrTraceCombo.getSelectionIndex()), 
					xyGraph.getYAxisList().get(yAxisCombo.getSelectionIndex()));
		
		if(!useDefaultColorButton.getSelection())
			annotation.setAnnotationColor(XYGraphMediaFactory.getInstance().getColor(
					colorSelector.getColorValue()));
		else
			annotation.setAnnotationColor(null);
		annotation.setFont(font);
		annotation.setCursorLineStyle(CursorLineStyle.values()[
				cursorLineCombo.getSelectionIndex()]);
		annotation.setShowName(showNameButton.getSelection());
		annotation.setShowSampleInfo(showSampleInfoButton.getSelection());
		annotation.setShowPosition(showPositionButton.getSelection());	
	}

	/**
	 * @return the annotation
	 */
	public Annotation getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;		
	}
	
	private void initialize(){
		nameText.setText(annotation.getName());
		nameText.setSelection(0, nameText.getText().length());
		snapToTrace.setSelection(!annotation.isFree());
		xAxisLabel.setText(snapToTrace.getSelection()?
				"Trace: " : "X-Axis: ");
		xAxisOrTraceCombo.removeAll();
		if(!annotation.isFree()){
			for(Trace trace : xyGraph.getPlotArea().getTraceList())
				xAxisOrTraceCombo.add(trace.getName());
			xAxisOrTraceCombo.select(xyGraph.getPlotArea().getTraceList().indexOf(
					annotation.getTrace()));			
		}else{
			for(Axis axis : xyGraph.getXAxisList())
				xAxisOrTraceCombo.add(axis.getTitle());
			xAxisOrTraceCombo.select(xyGraph.getXAxisList().indexOf(annotation.getXAxis()));
		}
		for(Axis axis : xyGraph.getYAxisList())
			yAxisCombo.add(axis.getTitle());	
		yAxisCombo.select(xyGraph.getYAxisList().indexOf(annotation.getYAxis()));
		yAxisLabel.setVisible(!snapToTrace.getSelection());
		yAxisCombo.setVisible(!snapToTrace.getSelection());	
		useDefaultColorButton.setSelection(annotation.getAnnotationColor() == null);
		colorLabel.setVisible(!useDefaultColorButton.getSelection());
		colorSelector.getButton().setVisible(annotation.getAnnotationColor() != null);
		colorSelector.setColorValue(
				annotation.getAnnotationColor() == null ? 
				annotation.getYAxis().getForegroundColor().getRGB() :
				annotation.getAnnotationColor().getRGB());
		
		fontLabel.setText("Font: " + (font==null? "System Default" : font.getFontData()[0].getName()));
		fontLabel.setFont(font);
		cursorLineCombo.select(annotation.getCursorLineStyle().getIndex());
		showNameButton.setSelection(annotation.isShowName());
		showSampleInfoButton.setSelection(annotation.isShowSampleInfo());
		showPositionButton.setSelection(annotation.isShowPosition());

	}
	
	
}
