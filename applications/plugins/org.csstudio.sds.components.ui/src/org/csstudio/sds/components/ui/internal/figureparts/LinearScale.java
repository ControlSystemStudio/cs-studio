package org.csstudio.sds.components.ui.internal.figureparts;


import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Linear(straight) scale has the tick labels and tick marks on a straight line. 
 * It can be used for any scale based widget, such as 2D plot, chart, graph, 
 * thermometer or tank etc. <br>
 * A scale is comprised of Margins, Scale line, tick labels and tick marks which include
 * minor ticks and major ticks. <br>
 * 
 * Margin is half of the label's length(Horizontal Scale) or 
 * height(Vertical scale), so that the label can be displayed correctly. 
 * So the range must be set before you can get the correct margin.<br><br>
 * 
 * |Margin|______|______|______|______|______|______|Margin| <br>
 *
 * @author Xihui Chen
 *  
 */
public class LinearScale extends AbstractScale {

	/** scale direction */
    public enum Orientation {

        /** the constant to represent horizontal scales */
        HORIZONTAL,

        /** the constant to represent vertical scales */
        VERTICAL
    }

	private static final int SPACE_BTW_MARK_LABEL = 3;
    
    /** scale direction, no meaning for round scale */
    private Orientation orientation = Orientation.HORIZONTAL;
    
    /** the scale tick labels */
    private LinearScaleTickLabels tickLabels;

    /** the scale tick marks */
    private LinearScaleTickMarks tickMarks;

    /** the length of the whole scale */
    private int length;
    
    private int margin;
    
    private final static Font DEFAULT_FONT = CustomMediaFactory.getInstance().getFont(
    		CustomMediaFactory.FONT_ARIAL);
    
    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     * @param scale
     *            the scale
     */
    public LinearScale() {      
    	
        tickLabels = new LinearScaleTickLabels(this);        
        tickMarks = new LinearScaleTickMarks(this);                  
        add(tickMarks);        
        add(tickLabels);    
        setFont(DEFAULT_FONT);
 
    }
	
	private void calcMargin() {
		if(isHorizontal()) {			
			margin = (int) Math.ceil(Math.max(FigureUtilities.getTextExtents(
					format(getRange().lower),getFont()).width, 
					FigureUtilities.getTextExtents(format(getRange().upper), getFont()).width)/2.0);
		}else
			margin = (int) Math.ceil(Math.max(FigureUtilities.getTextExtents(
					format(getRange().lower), getFont()).height, 
					FigureUtilities.getTextExtents(format(getRange().upper), getFont()).height)/2.0);;
	}
	
	/**
	 * @return the length of the whole scale (include margin)
	 */
	public int getLength() {
		return length;
	}

    /**Margin is half of the label's length(Horizontal Scale) or 
     * height(Vertical scale), so that the label can be displayed correctly. 
     * So the range and format pattern must be set correctly
     * before you can get the correct margin.
     * @return the margin
     */
    public int getMargin() {
		if(isDirty())
			calcMargin();
		return margin;
	}
	/**
	 * @return the orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		
		Dimension size = new Dimension(wHint, hHint);
		
		if(isHorizontal()) {
			length = wHint;
			tickLabels.update(length-2*getMargin());
			size.height = (int)getScaleTickLabels().getTickLabelMaxHeight() 
							+ SPACE_BTW_MARK_LABEL + LinearScaleTickMarks.MAJOR_TICK_LENGTH;
		} else {
			length = hHint;
			tickLabels.update(length-2*getMargin());
			size.width = (int)getScaleTickLabels().getTickLabelMaxLength() 
							+ SPACE_BTW_MARK_LABEL + LinearScaleTickMarks.MAJOR_TICK_LENGTH;
		
		}
			
		return size;
		
	}

    /**
     * Gets the scale tick labels.
     * 
     * @return the scale tick labels
     */
    public LinearScaleTickLabels getScaleTickLabels() {
        return tickLabels;
    }
    /**
     * Gets the scale tick marks.
     * 
     * @return the scale tick marks
     */
    public LinearScaleTickMarks getScaleTickMarks() {
        return tickMarks;
    }

    /**
	 * @return the length of the tick part (without margin)
	 */
	public int getTickLength() {
		return length - 2*getMargin();
	}

    /**
	 * Get the relative position of the value. 
	 * @param value the value to find its position. It would be coerced to the range of scale.
	 * @param relative return the position relative to the left/bottom bound of the scale if true. 
	 * If false, return the absolute position which has the scale bounds counted.
	 * @return position in pixels
	 */
	public int getValuePosition(double value, boolean relative) {
		//coerce to range
		double min = getRange().lower;
        double max = getRange().upper;
		value = value < min ? min : (value > max ? max : value);
		int pixelsToStart =0;
		if(isLogScaleEnabled()){
			if(value <=0)
				throw new IllegalArgumentException(
						"Invalid value: value must be greater than 0");
			pixelsToStart = (int) ((Math.log10(value) - Math.log10(min))/
							(Math.log10(max) - Math.log10(min)) * (length - 2*margin)) + margin;
		}else			
			pixelsToStart = (int) ((value - min)/(max-min)*(length-2*margin)) + margin;
		
		if(relative) {
			if(isHorizontal())
				return pixelsToStart;
			else
				return length - pixelsToStart;
		} else {
			if(isHorizontal())
				return pixelsToStart + bounds.x;
			else
				return length - pixelsToStart + bounds.y;
		}		
	}


    public boolean isHorizontal() {
		return getOrientation() == Orientation.HORIZONTAL;
	}


    @Override
    protected void layout() {
    	super.layout();
    	updateTick();
      	Rectangle area = getClientArea();
      	if(isHorizontal() && getTickLablesSide() == LabelSide.Primary) {
      		tickLabels.setBounds(new Rectangle(area.x, 
      				area.y + LinearScaleTickMarks.MAJOR_TICK_LENGTH + SPACE_BTW_MARK_LABEL,
      				area.width, area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH
      				));
      		tickMarks.setBounds(area);      		
      	}else if(isHorizontal() && getTickLablesSide() == LabelSide.Secondary) {
      		tickLabels.setBounds(new Rectangle(area.x, 
      				area.y + area.height -LinearScaleTickMarks.MAJOR_TICK_LENGTH - 
      				tickLabels.getTickLabelMaxHeight() - SPACE_BTW_MARK_LABEL,
      				area.width,
      				tickLabels.getTickLabelMaxHeight()
      				));
      		tickMarks.setBounds(new Rectangle(area.x, 
      				area.y + area.height - LinearScaleTickMarks.MAJOR_TICK_LENGTH,
      				area.width,
      				LinearScaleTickMarks.MAJOR_TICK_LENGTH
      				));  
      	}else if(getTickLablesSide() == LabelSide.Primary) {
      		tickLabels.setBounds(new Rectangle(area.x + area.width 
      				- LinearScaleTickMarks.MAJOR_TICK_LENGTH - tickLabels.getTickLabelMaxLength()
      				-SPACE_BTW_MARK_LABEL, 
      				area.y, 
      				tickLabels.getTickLabelMaxLength(),
      				area.height));
      		tickMarks.setBounds(new Rectangle(area.x + area.width 
      				- LinearScaleTickMarks.MAJOR_TICK_LENGTH, 
      				area.y,
      				LinearScaleTickMarks.MAJOR_TICK_LENGTH,
      				area.height));  
      	}else {
      		tickLabels.setBounds(new Rectangle(area.x+ LinearScaleTickMarks.MAJOR_TICK_LENGTH 
      				+SPACE_BTW_MARK_LABEL, 
      				area.y, 
      				tickLabels.getTickLabelMaxLength(),
      				area.height));
      		tickMarks.setBounds(new Rectangle(area.x, 
      				area.y,
      				LinearScaleTickMarks.MAJOR_TICK_LENGTH,
      				area.height));  
      	}    	
    } 

    @Override
    public void setBounds(Rectangle rect) {
    	if(!bounds.equals(rect))
    		setDirty(true);
    	super.setBounds(rect);   
    	
    }
    /*
     * @see IAxisTick#setFont(Font)
     */
    public void setFont(Font font) {
        if (font != null && font.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        tickLabels.setFont(font);
        super.setFont(font);
        
    }

	/*
     * @see IAxisTick#setForeground(Color)
     */
    public void setForegroundColor(Color color) {
        tickMarks.setForegroundColor(color);
        tickLabels.setForegroundColor(color);
    }
	
	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
        setDirty(true);

	}
	

	/**
     * Updates the tick, recalculate all parameters, such as margin, length...
     */
    public void updateTick() {
    	if(isDirty()){
	    	length = isHorizontal() ? 
	    			getClientArea().width: getClientArea().height;    		
	    	if(length > 2*getMargin())
	    		tickLabels.update(length-2*getMargin());    	
    	}
    	setDirty(false);
    }
	
	/**
     * Updates the tick layout.
     
    protected void updateLayoutData() {
        axisTickLabels.updateLayoutData();
        axisTickMarks.updateLayoutData();
    }
     */
	
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}
}
