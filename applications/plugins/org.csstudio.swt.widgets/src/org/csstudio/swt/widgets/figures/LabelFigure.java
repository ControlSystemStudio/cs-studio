package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Font;

/**
 * A Figure with an embedded TextFlow within a FlowPage that contains
 * text.
 *
 * @author Xihui Chen
 *
 */
public class LabelFigure extends Figure implements Introspectable{
	
	public enum H_ALIGN{
		LEFT("Left"),
		CENTER("Center"),
		RIGHT("Right");
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i=0;
			for(H_ALIGN h : values()){
				result[i++] = h.toString();
			}
			return result;
		}
		String descripion;
		H_ALIGN(String description){
			this.descripion = description;
		}
		
		@Override
		public String toString() {
			return descripion;
		}
	}
	
	public enum V_ALIGN{
		TOP("Top"),
		MIDDLE("Middle"),
		BOTTOM("Bottom");
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i=0;
			for(V_ALIGN h : values()){
				result[i++] = h.toString();
			}
			return result;
		}
		String descripion;
		V_ALIGN(String description){
			this.descripion = description;
		}
		
		@Override
		public String toString() {
			return descripion;
		}
	}

	
	/** The inner TextFlow **/
	private TextFlow textFlow;
	private FlowPage flowPage;
	
	private V_ALIGN verticalAlignment = V_ALIGN.TOP;
	private H_ALIGN horizontalAlignment = H_ALIGN.LEFT;
	
	private boolean runMode;
	
	private boolean selectable = true;

	public LabelFigure() {
		this(false);
	}
	
	/** 
	 * Creates a new StickyNoteFigure with a MarginBorder that is the given size and a
	 * FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 * 
	 * @param borderSize the size of the MarginBorder
	 */
	public LabelFigure(boolean runMode) {
		this.runMode = runMode;
		//setLayoutManager(new StackLayout());
		//add(scrollPane);
		flowPage = new FlowPage();
		textFlow = new TextFlow(""){
			@Override
			public void setFont(Font f) {
				super.setFont(f);
				revalidateBidi(this);
				repaint();
			}
		};
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
				ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.add(textFlow);
		add(flowPage);
	}

	@Override
	public boolean containsPoint(int x, int y) {
		if(runMode && !selectable)
			return false;
		else
			return super.containsPoint(x, y);
	}

	public Dimension getAutoSizeDimension(){
		return flowPage.getPreferredSize().getCopy().expand(
				getInsets().getWidth(), getInsets().getHeight());
	}

	/**
	 * @return the h_alignment
	 */
	public H_ALIGN getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return textFlow.getText();
	}

	/**
	 * @return the v_alignment
	 */
	public V_ALIGN getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * @return the runMode
	 */
	public boolean isRunMode() {
		return runMode;
	}

	/**
	 * @return the selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}

	

	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea();
		Dimension preferedSize = flowPage.getPreferredSize();
			int x=clientArea.x;
			if(clientArea.width > preferedSize.width){
				
				switch (horizontalAlignment) {
				case CENTER:
					x = clientArea.x + (clientArea.width - preferedSize.width)/2;
					break;
				case RIGHT:
					x = clientArea.x + clientArea.width - preferedSize.width;
					break;
				case LEFT:
				default:
					x=clientArea.x;
					break;
				}
			}else{
				preferedSize = flowPage.getPreferredSize(clientArea.width, -1);
			}
			int y=clientArea.y;
			if(clientArea.height > preferedSize.height){
				switch (verticalAlignment) {
				case MIDDLE:
					y = clientArea.y + (clientArea.height - preferedSize.height)/2;
					break;
				case BOTTOM:
					y = clientArea.y + clientArea.height - preferedSize.height;
					break;
				case TOP:
				default:
					y=clientArea.y;
					break;
				}
			}
			
			flowPage.setBounds(new Rectangle(x, y, 
					clientArea.width - (x - clientArea.x), clientArea.height - (y - clientArea.y)));
	}
	
	
	@Override
	public void setFont(Font f) {
		flowPage.setFont(f);
		textFlow.setFont(f);
		super.setFont(f);
		revalidate();			
	}
	

	

	
	public void setHorizontalAlignment(H_ALIGN hAlignment) {
		if(this.horizontalAlignment == hAlignment)
			return;
		horizontalAlignment = hAlignment;
		revalidate();
	}
	
	@Override
	public void setOpaque(boolean opaque) {		
		textFlow.setOpaque(opaque);
		super.setOpaque(opaque);
	}
	
	
	
	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}
	
	
	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	/**
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText the new text value.
	 */
	public void setText(String newText) {		
		textFlow.setText(newText);
		revalidate();

	}

	public void setVerticalAlignment(V_ALIGN vAlignment) {
		if(this.verticalAlignment == vAlignment)
			return;
		verticalAlignment = vAlignment;
		revalidate();
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}

	

}
