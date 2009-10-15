package org.csstudio.opibuilder.widgets.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.ui.util.CustomMediaFactory;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**The tab figure.
 * @author Xihui Chen
 *
 */
public class TabFigure extends Figure {
	
	private static final int MARGIN = 10;
	private List<Label> tabLabelList;
	private List<Color> tabColorList;
	private int activeTabIndex;
	private final static Color BORDER_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_DARK_GRAY); 
	private final static Color DEFAULT_TABCOLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE); 
	
	private final static Font DEFAULT_TITLE_FONT = CustomMediaFactory.getInstance().getFont(
			new FontData("Arial", 12, SWT.BOLD));
	
	private final static int MINIMUM_TAB_HEIGHT = 10;
	private final static int MINIMUM_TAB_WIDTH = 20;
	
	private IFigure pane;
	private ScrollPane tabArea;

	/**
	 * Listeners that react on tab index events.
	 */
	private List<ITabListener> tabListeners = 
		new ArrayList<ITabListener>();
	
	public TabFigure() {
		tabLabelList = new ArrayList<Label>();
		tabColorList = new ArrayList<Color>();
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
	
	public void addTabListener(ITabListener listener){
		tabListeners.add(listener);
	}
	
	private void fireActiveTabIndexChanged(int oldIndex, int newIndex){
		for(ITabListener listener : tabListeners)
			listener.activeTabIndexChanged(oldIndex, newIndex);
	}
	
	public IFigure getContentPane(){
		return pane;
	}
	
	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea();
		int left = clientArea.x;
		int top = clientArea.y;
		int height = getTabLabelHeight();
		for(Label label : tabLabelList){
			Dimension labelSize = label.getPreferredSize();
			label.setBounds(new Rectangle(left, top, labelSize.width + MARGIN, height));
			left += (labelSize.width + MARGIN -1);
		}	
		tabArea.setBounds(new Rectangle(clientArea.x, clientArea.y + height-1, 
				clientArea.width-1, clientArea.height - height));		
	}
	
	public int getTabLabelHeight(){
		int h = MINIMUM_TAB_HEIGHT;
		for(Label label : tabLabelList){
			if(label.getPreferredSize().height > h){
				h = label.getPreferredSize().height;
			}
		}
		return h + MARGIN;
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
			graphics.drawLine(tabLabelBounds.x +1, tabLabelBounds.y + tabLabelBounds.height-1,
					tabLabelBounds.x + tabLabelBounds.width -2, tabLabelBounds.y + tabLabelBounds.height-1);
		}
	}
	
	
	public int getActiveTabIndex() {
		return activeTabIndex;
	}
	
	public void setActiveTabIndex(int activeTabIndex) {
		//if(this.activeTabIndex == activeTabIndex)
		//	return;
		int i=0;
		for(Label l : tabLabelList){
			l.setBackgroundColor(
				getDarkColor(tabColorList.get(i++)));
		}
		
		getTabLabel(activeTabIndex).setBackgroundColor(tabColorList.get(activeTabIndex));
		tabArea.setBackgroundColor(tabColorList.get(activeTabIndex));
		fireActiveTabIndexChanged(this.activeTabIndex, activeTabIndex);
		this.activeTabIndex = activeTabIndex;
		repaint();
		
	}
	
	public void setTabColor(int index, Color color){
		tabColorList.set(index, color);
		getTabLabel(index).setBackgroundColor(
			index == activeTabIndex ? color : getDarkColor(color));
		if(index == activeTabIndex)
			tabArea.setBackgroundColor(color);
		repaint();
	}
	
	public Label getTabLabel(int index){
		return tabLabelList.get(index);
	}
	
	public void addTab(String title){
		Label tabLabel = createTabLabel(title, tabLabelList.size());
		tabLabelList.add(tabLabel);
		tabColorList.add(DEFAULT_TABCOLOR);
		add(tabLabel);
		if(activeTabIndex <0)
			setActiveTabIndex(0);
		revalidate();
	}


	private Label createTabLabel(String title, final int index) {
		final Label tabLabel = new Label(title);
		tabLabel.setLabelAlignment(PositionConstants.CENTER);
		tabLabel.setOpaque(true);
		tabLabel.setBorder(new LineBorder(BORDER_COLOR));		
		tabLabel.setBackgroundColor(getDarkColor(DEFAULT_TABCOLOR));
	//	tabLabel.setCursor(Cursors.HAND);
		tabLabel.addMouseListener(new MouseListener.Stub(){
			@Override
			public void mousePressed(MouseEvent me) {
				setActiveTabIndex(tabLabelList.indexOf(tabLabel));
			}
		});
		return tabLabel;
	}
	
	public void addTab(String title, int index){
		Label tabLabel = createTabLabel(title, index);
		tabLabelList.add(index, tabLabel);
		tabColorList.add(index, DEFAULT_TABCOLOR);
		add(tabLabel);
		if(activeTabIndex <0)
			setActiveTabIndex(0);
		revalidate();
	}
	
	public void removeTab(int index){
		remove(tabLabelList.get(index));
		tabLabelList.remove(index);
		tabColorList.remove(index);
		revalidate();
	}
	
	public void removeTab(){
		remove(tabLabelList.get(tabLabelList.size()-1));
		tabLabelList.remove(tabLabelList.size()-1);
		tabColorList.remove(tabColorList.size()-1);
		revalidate();
	}
	

	
	
	public int getTabAmount(){
		return tabLabelList.size();
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
	
	/**
	 * Definition of listeners that react on active tab index changed.
	 * 
	 * @author Xihui Chen
	 * 
	 */
	public interface ITabListener {
		
		void activeTabIndexChanged(int oldIndex, int newIndex);
	}
	
}
