package org.csstudio.opibuilder.widgets.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;

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
	
	private RectangleFigure tabArea;
	
	
	public TabFigure() {
		tabLabelList = new ArrayList<Label>();
		tabColorList = new ArrayList<Color>();
		activeTabIndex = -1;
		tabArea = new RectangleFigure();
		tabArea.setForegroundColor(BORDER_COLOR);
		tabArea.setOutline(true);
		add(tabArea);
	}
	
	
	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea();
		int left = clientArea.x;
		int top = clientArea.y;
		int height = getMaxLabelHeight()+MARGIN;
		for(Label label : tabLabelList){
			Dimension labelSize = label.getPreferredSize();
			label.setBounds(new Rectangle(left, top, labelSize.width + MARGIN, height));
			left += (labelSize.width + MARGIN -1);
		}	
		tabArea.setBounds(new Rectangle(clientArea.x, clientArea.y + height -1, 
				clientArea.width, clientArea.height - height));
	}
	
	private int getMaxLabelHeight(){
		int h = MINIMUM_TAB_HEIGHT;
		for(Label label : tabLabelList){
			if(label.getPreferredSize().height > h){
				h = label.getPreferredSize().height;
			}
		}
		return h;
	}
	
	
	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		//paint hidding rect
		if(activeTabIndex >= 0 && activeTabIndex < tabLabelList.size()){
			graphics.setBackgroundColor(tabLabelList.get(activeTabIndex).getBackgroundColor());
			Rectangle tabLabelBounds = tabLabelList.get(activeTabIndex).getBounds();
			graphics.fillRectangle(tabLabelBounds.x+1, tabLabelBounds.y +tabLabelBounds.height-2,
				tabLabelBounds.width -2, 4);
		}
	}
	
	
	public int getActiveTabIndex() {
		return activeTabIndex;
	}
	
	public void setActiveTabIndex(int activeTabIndex) {
		if(this.activeTabIndex == activeTabIndex)
			return;
		
		if(this.activeTabIndex >=0){
			getTabLabel(this.activeTabIndex).setBackgroundColor(
				getDarkColor(tabColorList.get(this.activeTabIndex)));
		}
		
		this.activeTabIndex = activeTabIndex;
		getTabLabel(activeTabIndex).setBackgroundColor(tabColorList.get(activeTabIndex));
		tabArea.setBackgroundColor(tabColorList.get(activeTabIndex));
		repaint();
		
	}
	
	public void setTabColor(int index, Color color){
		tabColorList.set(index, color);
		getTabLabel(index).setBackgroundColor(
			index == activeTabIndex ? color : getDarkColor(color));
	}
	
	public Label getTabLabel(int index){
		return tabLabelList.get(index);
	}
	
	public void addTab(String title){
		Label tabLabel = createTabLabel(title, tabLabelList.size());
		tabLabelList.add(tabLabel);
		tabColorList.add(DEFAULT_TABCOLOR);
		add(tabLabel);
		revalidate();
	}


	private Label createTabLabel(String title, final int index) {
		Label tabLabel = new Label(title);
		tabLabel.setLabelAlignment(PositionConstants.CENTER);
		tabLabel.setOpaque(true);
		tabLabel.setBorder(new LineBorder(BORDER_COLOR));		
		tabLabel.setBackgroundColor(getDarkColor(DEFAULT_TABCOLOR));
		tabLabel.addMouseListener(new MouseListener.Stub(){
			@Override
			public void mouseReleased(MouseEvent me) {
				setActiveTabIndex(index);
			}
		});
		return tabLabel;
	}
	
	public void addTab(String title, int index){
		Label tabLabel = createTabLabel(title, index);
		tabLabelList.add(index, tabLabel);
		tabColorList.add(index, DEFAULT_TABCOLOR);
		add(tabLabel, index);
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
	
	
}
