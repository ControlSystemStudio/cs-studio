package org.csstudio.opibuilder.widgets.figures;

import java.util.Map;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**Figure for a Combo.
 * @author Xihui Chen
 *
 */
public class ComboFigure extends Figure {

	private Combo combo;	
	
	private boolean runmode;
	private Triangle selector;
	private boolean updateFlag;
	private final static Color GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_GRAY);
	private final static Color DARK_GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_DARK_GRAY);

	private static final int SELECTOR_WIDTH = 12;

	private UpdateListener updateManagerListener;
	
	private AbstractContainerModel parentModel;
	
	public ComboFigure(Composite composite) {
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setVisible(false);
		selector = new Triangle();	
		selector.setDirection(PositionConstants.SOUTH);
		selector.setFill(true);

		add(selector);
		
	}
	
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		combo.setEnabled(runmode && value);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		combo.setVisible(isVisible() && isShowing());		
	}
	
	@Override
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		combo.setForeground(fg);
	}
	
	@Override
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		combo.setBackground(bg);
	}
	
	public void setText(String text){
		combo.setText(text);
	}
	
	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea().getCopy().shrink(2, 2);
		selector.setBounds(new Rectangle(clientArea.x + clientArea.width - SELECTOR_WIDTH -2,
				clientArea.y, SELECTOR_WIDTH, clientArea.height));
		relocateCombo();
	}

	@Override
	protected void paintClientArea(Graphics graphics) {		
		repaintCombo();
		//draw this so that it can be seen in the outline view
		if(!runmode){
			Rectangle clientArea = getClientArea().getCopy().shrink(2, 2);
			graphics.setBackgroundColor(GRAY_COLOR);
			graphics.fillRectangle(clientArea);
			graphics.setForegroundColor(DARK_GRAY_COLOR);
			graphics.drawRectangle(
					new Rectangle(clientArea.getLocation(), clientArea.getSize().shrink(1, 1)));
		}
		super.paintClientArea(graphics);	
	}

	/**
	 * 
	 */
	private void repaintCombo() {
		combo.setVisible(isVisible() && isShowing());
		
		//the combo should has the same relative position and visibility as its parent container.
		if(!updateFlag && !(parentModel instanceof DisplayModel)){
			updateManagerListener = new UpdateListener(){
				
				@SuppressWarnings("unchecked")
				public void notifyPainting(Rectangle damage, Map dirtyRegions) {
					combo.setVisible(isVisible() && isShowing());				
				}
	
				public void notifyValidating() {		
					relocateCombo();
				}
				
			};
			getUpdateManager().addUpdateListener(updateManagerListener);
			updateFlag = true;
		}
	}
	
	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		relocateCombo();
	}

	/**
	 * 
	 */
	private void relocateCombo() {
		Rectangle rect = getClientArea().getCopy();		
		translateToAbsolute(rect);
		org.eclipse.swt.graphics.Rectangle trim = combo.computeTrim(0, 0, 0, 0);
		rect.translate(trim.x, trim.y);
		rect.width += trim.width;
		rect.height += trim.height;
		combo.setBounds(rect.x, rect.y, rect.width, rect.height);		
		
	}

	
	/**
	 * @return the SWT combo in the combo figure.
	 */
	public Combo getCombo() {
		return combo;
	}
	
	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		this.runmode = runMode;
		combo.setEnabled(runMode);
		selector.setVisible(!runMode);
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		combo.setFont(f);
	}

	

	public void dispose(){
		if(updateFlag && updateManagerListener != null)
			getUpdateManager().removeUpdateListener(updateManagerListener);
		combo.setMenu(null);
		combo.dispose();		
	}
	
	public Dimension getAutoSizeDimension(){
		return new Dimension(getBounds().width, 
				combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + getInsets().getHeight());
	}

	/**
	 * @param parentModel the parentModel to set
	 */
	public void setParentModel(AbstractContainerModel parentModel) {
		this.parentModel = parentModel;
	}

		
	
}
