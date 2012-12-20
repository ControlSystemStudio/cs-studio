package org.csstudio.graphene;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.csstudio.ui.util.AbstractConfigurationPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.epics.graphene.InterpolationScheme;

public class LineGraphConfigurationPanel extends
AbstractConfigurationPanel {
	private Text initialOffsetField;
	private Combo interpolationSchemeCombo;
	private Text incrementSizeField;
	private Text xData;

	public LineGraphConfigurationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblInterpolationScheme = new Label(this, SWT.NONE);
		FormData fd_lblInterpolationScheme = new FormData();
		fd_lblInterpolationScheme.top = new FormAttachment(0, 13);
		fd_lblInterpolationScheme.left = new FormAttachment(0, 10);
		lblInterpolationScheme.setLayoutData(fd_lblInterpolationScheme);
		lblInterpolationScheme.setText("Interpolation scheme:");
		
		interpolationSchemeCombo = new Combo(this, SWT.NONE);
		FormData fd_interpolationSchemeCombo = new FormData();
		fd_interpolationSchemeCombo.left = new FormAttachment(lblInterpolationScheme, 6);
		fd_interpolationSchemeCombo.right = new FormAttachment(100, -10);
		fd_interpolationSchemeCombo.top = new FormAttachment(0, 10);
		interpolationSchemeCombo.setLayoutData(fd_interpolationSchemeCombo);
		String[] labels = new String[InterpolationScheme.values().length];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = InterpolationScheme.values()[i].name();
		}
		interpolationSchemeCombo.setItems(labels);
		
		Group grpXData = new Group(this, SWT.NONE);
		grpXData.setText("X data");
		grpXData.setLayout(new FormLayout());
		FormData fd_grpXData = new FormData();
		fd_grpXData.bottom = new FormAttachment(interpolationSchemeCombo, 218, SWT.BOTTOM);
		fd_grpXData.right = new FormAttachment(100, -10);
		fd_grpXData.top = new FormAttachment(interpolationSchemeCombo, 1);
		fd_grpXData.left = new FormAttachment(0, 10);
		grpXData.setLayoutData(fd_grpXData);
		
		Button btnArrayIndex = new Button(grpXData, SWT.RADIO);
		FormData fd_btnArrayIndex = new FormData();
		fd_btnArrayIndex.top = new FormAttachment(0, 10);
		fd_btnArrayIndex.left = new FormAttachment(0, 10);
		btnArrayIndex.setLayoutData(fd_btnArrayIndex);
		btnArrayIndex.setText("Array index");
		
		final Button btnArrayIndexScaled = new Button(grpXData, SWT.RADIO);
		FormData fd_btnArrayIndexScaled = new FormData();
		fd_btnArrayIndexScaled.top = new FormAttachment(btnArrayIndex, 10);
		fd_btnArrayIndexScaled.left = new FormAttachment(btnArrayIndex, 0, SWT.LEFT);
		btnArrayIndexScaled.setLayoutData(fd_btnArrayIndexScaled);
		btnArrayIndexScaled.setText("Array index scaled");
		
		final Label lblInitialOffsetPv = new Label(grpXData, SWT.NONE);
		lblInitialOffsetPv.setEnabled(false);
		FormData fd_lblInitialOffsetPv = new FormData();
		fd_lblInitialOffsetPv.top = new FormAttachment(btnArrayIndexScaled, 10);
		fd_lblInitialOffsetPv.left = new FormAttachment(btnArrayIndexScaled, 20, SWT.LEFT);
		lblInitialOffsetPv.setLayoutData(fd_lblInitialOffsetPv);
		lblInitialOffsetPv.setText("Initial offset PV:");
		
		initialOffsetField = new Text(grpXData, SWT.BORDER);
		initialOffsetField.setEnabled(false);
		FormData fd_initialOffsetField = new FormData();
		fd_initialOffsetField.top = new FormAttachment(lblInitialOffsetPv, -3, SWT.TOP);
		fd_initialOffsetField.left = new FormAttachment(lblInitialOffsetPv, 6);
		fd_initialOffsetField.right = new FormAttachment(100, -10);
		initialOffsetField.setLayoutData(fd_initialOffsetField);
		
		final Label lblIncrementSizePv = new Label(grpXData, SWT.NONE);
		lblIncrementSizePv.setEnabled(false);
		FormData fd_lblIncrementSizePv = new FormData();
		fd_lblIncrementSizePv.top = new FormAttachment(initialOffsetField, 10);
		fd_lblIncrementSizePv.left = new FormAttachment(lblInitialOffsetPv, 0, SWT.LEFT);
		lblIncrementSizePv.setLayoutData(fd_lblIncrementSizePv);
		lblIncrementSizePv.setText("Increment size PV:");
		
		btnArrayIndexScaled.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				initialOffsetField.setEnabled(btnArrayIndexScaled.getSelection());
				lblInitialOffsetPv.setEnabled(btnArrayIndexScaled.getSelection());
				lblIncrementSizePv.setEnabled(btnArrayIndexScaled.getSelection());
				incrementSizeField.setEnabled(btnArrayIndexScaled.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		incrementSizeField = new Text(grpXData, SWT.BORDER);
		incrementSizeField.setEnabled(false);
		FormData fd_incrementSizeField = new FormData();
		fd_incrementSizeField.right = new FormAttachment(100, -10);
		fd_incrementSizeField.top = new FormAttachment(lblIncrementSizePv, -3, SWT.TOP);
		fd_incrementSizeField.left = new FormAttachment(lblIncrementSizePv, 6);
		incrementSizeField.setLayoutData(fd_incrementSizeField);
		
		final Button btnOtherArray = new Button(grpXData, SWT.RADIO);
		FormData fd_btnOtherArray = new FormData();
		fd_btnOtherArray.top = new FormAttachment(lblIncrementSizePv, 10);
		fd_btnOtherArray.left = new FormAttachment(btnArrayIndex, 0, SWT.LEFT);
		btnOtherArray.setLayoutData(fd_btnOtherArray);
		btnOtherArray.setText("Other array");
		btnOtherArray.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				xData.setEnabled(btnOtherArray.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		Label lblXPv = new Label(grpXData, SWT.NONE);
		lblXPv.setEnabled(false);
		FormData fd_lblXPv = new FormData();
		fd_lblXPv.top = new FormAttachment(btnOtherArray, 10);
		fd_lblXPv.left = new FormAttachment(lblInitialOffsetPv, 0, SWT.LEFT);
		lblXPv.setLayoutData(fd_lblXPv);
		lblXPv.setText("X PV:");
		
		xData = new Text(grpXData, SWT.BORDER);
		xData.setEnabled(false);
		FormData fd_xData = new FormData();
		fd_xData.right = new FormAttachment(100, -10);
		fd_xData.top = new FormAttachment(lblXPv, -3, SWT.TOP);
		fd_xData.left = new FormAttachment(lblXPv, 6);
		xData.setLayoutData(fd_xData);
		
	}
}
