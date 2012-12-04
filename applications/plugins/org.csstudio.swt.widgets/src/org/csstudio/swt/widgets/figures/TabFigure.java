/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**The tab figure.
 * @author Xihui Chen
 *
 */
public class TabFigure extends Figure implements Introspectable{
	
	private static final int GAP = 2;

	/**
	 * Definition of listeners that react on active tab index changed.
	 * 
	 * @author Xihui Chen
	 * 
	 */
	public interface ITabListener extends EventListener{
		
		void activeTabIndexChanged(int oldIndex, int newIndex);
	}
	private static final int MARGIN = 10;
	private List<Label> tabLabelList;
	private List<Color> tabColorList;
	private List<Boolean> tabEnabledList;
	private int activeTabIndex; 
	private final static Color BORDER_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_DARK_GRAY); 
	private final static Color TAB_3D_COLOR = CustomMediaFactory.getInstance().getColor(
			255,255,255); 
	
//	private final static Font DEFAULT_TITLE_FONT = CustomMediaFactory.getInstance().getFont(
//			new FontData("Arial", 12, SWT.BOLD));
	
	private final static Color DEFAULT_TABCOLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	
	private int minimumTabHeight = 10;
	private final static int MINIMUM_TAB_WIDTH = 20;
	private IFigure pane;

	private ScrollPane tabArea;
	private boolean horizontal = true;
	
	/**
	 * Listeners that react on tab index events.
	 */
	private List<ITabListener> tabListeners = 
		new ArrayList<ITabListener>();
	
	public TabFigure() {
		tabLabelList = new ArrayList<Label>();
		tabColorList = new ArrayList<Color>();
		tabEnabledList = new ArrayList<Boolean>();
		activeTabIndex = -1;
		tabArea = new ScrollPane();		
		tabArea.setScrollBarVisibility(ScrollPane.NEVER);
		pane = new FreeformLayer();
		tabArea.setForegroundColor(BORDER_COLOR);
		pane.setLayoutManager(new FreeformLayout());	
		add(tabArea);
		tabArea.setViewport(new FreeformViewport());
		tabArea.setContents(pane);	
		
		
	}
	
	public synchronized void addTab(String title){
		Label tabLabel = createTabLabel(title, tabLabelList.size());
		tabLabelList.add(tabLabel);
		tabColorList.add(DEFAULT_TABCOLOR);
		tabEnabledList.add(true);
		add(tabLabel);
//		if(activeTabIndex <0)
//			setActiveTabIndex(0);
		revalidate();
	}
	
	public synchronized void addTab(String title, int index){
		Label tabLabel = createTabLabel(title, index);
		tabLabelList.add(index, tabLabel);
		tabColorList.add(index, DEFAULT_TABCOLOR);
		tabEnabledList.add(index, true);
		add(tabLabel);
//		if(activeTabIndex <0)
//			setActiveTabIndex(0);
		revalidate();
	}
	
	public void addTabListener(ITabListener listener){
		tabListeners.add(listener);
	}
	
	
	private Label createTabLabel(String title, final int index) {
		final Label tabLabel = new Label(title){
			@Override
			protected void paintFigure(Graphics graphics) {
				graphics.pushState();
				graphics.setForegroundColor(TAB_3D_COLOR);				
				graphics.fillGradient(getClientArea(), horizontal);
				graphics.popState();
				super.paintFigure(graphics);
			}
		};
		tabLabel.setLabelAlignment(PositionConstants.CENTER);
		tabLabel.setOpaque(false);
		tabLabel.setBorder(new LineBorder(BORDER_COLOR));		
		tabLabel.setBackgroundColor(getDarkColor(DEFAULT_TABCOLOR));
	//	tabLabel.setCursor(Cursors.HAND);
		tabLabel.addMouseListener(new MouseListener.Stub(){
			@Override
			public void mousePressed(MouseEvent me) {
				if(me.button != 1)
					return;
				setActiveTabIndex(tabLabelList.indexOf(tabLabel));
			}
		});
		return tabLabel;
	}
	
	
	private void fireActiveTabIndexChanged(int oldIndex, int newIndex){
		for(ITabListener listener : tabListeners)
			listener.activeTabIndexChanged(oldIndex, newIndex);
	}
	
	
	public int getActiveTabIndex() {
		return activeTabIndex;
	}
	
	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}
	
	public IFigure getContentPane(){
		return pane;
	}
	
	private Color getDarkColor(Color color){
		int d = 30;
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		r = Math.max(0, r-d);
		g = Math.max(0, g-d);
		b = Math.max(0, b-d);		
		return CustomMediaFactory.getInstance().getColor(r,g,b);
	}
	
	public int getTabAmount(){
		return tabLabelList.size();
	}


	public Label getTabLabel(int index){
		if(index >= getTabAmount())
			throw new IndexOutOfBoundsException();		
		return tabLabelList.get(index);
	}
	
	public int getTabLabelHeight(){
		int h = minimumTabHeight;
		for(Label label : tabLabelList){
			if(label.getPreferredSize().height > h){
				h = label.getPreferredSize().height;
			}
		}
		return h + MARGIN;
	}

	public int getTabLabelWidth() {
		int h = MINIMUM_TAB_WIDTH;
		for (Label label : tabLabelList) {
			if (label.getPreferredSize().width > h) {
				h = label.getPreferredSize().width;
			}
		}
		return h + MARGIN;
	}
	
	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea();
		int left = clientArea.x;
		int top = clientArea.y;
		int width = getTabLabelWidth();
		int height = getTabLabelHeight();
		int i = 0;
		for(Label label : tabLabelList){
			Dimension labelSize = label.getPreferredSize();
			if (horizontal) {
				if (getActiveTabIndex() == i) label.setBounds(new Rectangle(left, top, labelSize.width + MARGIN + GAP, height));
				else label.setBounds(new Rectangle(left + GAP, top + 2, labelSize.width + MARGIN - GAP, height - 2));
				left += (labelSize.width + MARGIN - 1);
			} else {
				int labelH = Math.max(labelSize.height, minimumTabHeight);
				if (getActiveTabIndex() == i) label.setBounds(new Rectangle(left, top, width, labelH + MARGIN + GAP));
				else label.setBounds(new Rectangle(left + 2, top + GAP, width - 2, labelH + MARGIN - GAP));
				top += (labelH + MARGIN - 1);
			}
			i++;
		}	
		if (horizontal) {
			tabArea.setBounds(new Rectangle(clientArea.x, clientArea.y + height - 1, 
					clientArea.width - 1, clientArea.height - height));
		} else {
			tabArea.setBounds(new Rectangle(clientArea.x + width - 1, clientArea.y, 
					clientArea.width - width, clientArea.height - 1));
		}
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		//paint tabArea bounds
		graphics.setForegroundColor(BORDER_COLOR);
		graphics.drawRectangle(tabArea.getBounds());
		//paint hidding rect
		if(activeTabIndex >= 0 && activeTabIndex < tabLabelList.size()){
			graphics.setBackgroundColor(tabLabelList.get(activeTabIndex).getBackgroundColor());
			Rectangle tabLabelBounds = tabLabelList.get(activeTabIndex).getBounds();
		//	graphics.fillRectangle(tabLabelBounds.x+1, tabLabelBounds.y +tabLabelBounds.height-2,
		//		tabLabelBounds.width -2, 4);
			graphics.setForegroundColor(tabLabelList.get(activeTabIndex).getBackgroundColor());
			if (horizontal) {
				graphics.drawLine(tabLabelBounds.x + 1, 
						tabLabelBounds.y + tabLabelBounds.height - 1, 
						tabLabelBounds.x + tabLabelBounds.width - 2, 
						tabLabelBounds.y + tabLabelBounds.height - 1);
			} else {
				graphics.drawLine(tabLabelBounds.x + tabLabelBounds.width - 1, 
						tabLabelBounds.y + 1, 
						tabLabelBounds.x + tabLabelBounds.width - 1, 
						tabLabelBounds.y + tabLabelBounds.height - 2);
			}
		}
	}
	
	
	
	public void removeTab(){
		removeTab(tabLabelList.size()-1);		
	}
	
	public synchronized void removeTab(int index){
		if(index <0 || index >=getTabAmount())
			throw new IndexOutOfBoundsException();
		remove(tabLabelList.get(index));
		dispose(index);
		tabLabelList.remove(index);
		tabColorList.remove(index);
		tabEnabledList.remove(index);

		revalidate();
		repaint();
	}
	
	public void setActiveTabIndex(int activeTabIndex) {
		if(activeTabIndex >= getTabAmount())
			throw new IndexOutOfBoundsException();

		// If disabled we do not show
		if (!tabEnabledList.get(activeTabIndex))
			return;

		int i=0;
		for(Label l : tabLabelList){
			l.setBackgroundColor(
				getDarkColor(tabColorList.get(i++)));
		}
		
		getTabLabel(activeTabIndex).setBackgroundColor(tabColorList.get(activeTabIndex));
		tabArea.setBackgroundColor(tabColorList.get(activeTabIndex));
		fireActiveTabIndexChanged(this.activeTabIndex, activeTabIndex);
		this.activeTabIndex = activeTabIndex;
		revalidate();
		repaint();
		
	}
	
	public void setIconPath(final int index, final IPath path, final IJobErrorHandler errorHandler){		
		dispose(index);	
		
		if(path != null && !path.isEmpty()){			
				
				AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {
					
					@Override
					public void runWithInputStream(InputStream stream) {
						Image image = new Image(null, stream);
						try {
							stream.close();
						} catch (IOException e) {							
						}
						getTabLabel(index).setIcon(image);
					}
				};				
				ResourceUtil.pathToInputStreamInJob(path, uiTask, "Loading Tab Icon...", errorHandler);
				
		}	
	}
	

	/**
	 * Dispose image resources.
	 */
	public void dispose() {
		for(int i=0; i<tabLabelList.size(); i++)
			dispose(i);
	}
	
	private void dispose(int index){
		Label label = tabLabelList.get(index);
		
		Image image = label.getIcon();
		if(image != null){
			image.dispose();
			image = null;
			label.setIcon(null);
		}
	}

	public void setTabColor(int index, Color color){
		if(index >= getTabAmount())
			throw new IndexOutOfBoundsException();
		tabColorList.set(index, color);
		getTabLabel(index).setBackgroundColor(
			index == activeTabIndex ? color : getDarkColor(color));
		if(index == activeTabIndex)
			tabArea.setBackgroundColor(color);
		repaint();
	}
	
	public void setTabEnabled(int index, boolean enabled) {
		if (index >= getTabAmount())
			throw new IndexOutOfBoundsException();
		tabEnabledList.set(index, enabled);
		getTabLabel(index).setEnabled(enabled);
		repaint();
	}
	
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		revalidate();
	}
	
	public void setMinimumTabHeight(int minimumTabHeight) {
		this.minimumTabHeight = minimumTabHeight;
		revalidate();
	}
	
}