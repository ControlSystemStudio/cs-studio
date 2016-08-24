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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figureparts.RapArrowButton;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.ui.util.ColorConstants;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.ButtonBorder;
import org.eclipse.draw2d.ButtonBorder.ButtonScheme;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Orientable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**The figure for a spinner widget.
 * @author Xihui Chen
 *
 */
public class SpinnerFigure extends Figure implements Introspectable {
    public static enum NumericFormatType {
        DECIMAL("Decimal"),
        EXP("Exponential"),
        HEX("Hex");

        public static String[] stringValues(){
            String[] result = new String[values().length];
            int i =0 ;
            for(NumericFormatType f : values()){
                result[i++] = f.toString();
            }
            return result;
        }
        private String description;

        private NumericFormatType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
    private static final String HEX_PREFIX = "0x"; //$NON-NLS-1$

    private double min = -100;
    private double max = 100;
    private double stepIncrement = 1;
    private double pageIncrement = 10;
    private double value = 0;

    private ArrowButton buttonUp, buttonDown;
    private TextFigure labelFigure;
    private List<IManualValueChangeListener> spinnerListeners;

    private boolean arrowButtonsOnLeft = true;

    private boolean arrowButtonsHorizontal = false;

    private int buttonWidth = 20;

    private NumericFormatType formatType;

    private int precision = 3;

    public SpinnerFigure() {
        formatType = NumericFormatType.DECIMAL;
        spinnerListeners = new ArrayList<IManualValueChangeListener>();
        setRequestFocusEnabled(true);
        setFocusTraversable(true);
            addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent ke) {
                    if(ke.keycode == SWT.ARROW_DOWN)
                        stepDown();
                    else if(ke.keycode == SWT.ARROW_UP)
                        stepUp();
                    else if(ke.keycode == SWT.PAGE_UP)
                        pageUp();
                    else if(ke.keycode == SWT.PAGE_DOWN)
                        pageDown();
                }

                public void keyReleased(KeyEvent ke) {
                }
            });

            addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent fe) {
                    repaint();
                }

                public void focusLost(FocusEvent fe) {
                    repaint();
                }
            });


        labelFigure = new TextFigure(){
            /**
             * If this button has focus, this method paints a focus rectangle.
             *
             * @param graphics Graphics handle for painting
             */
            protected void paintBorder(Graphics graphics) {
                super.paintBorder(graphics);
                if (SpinnerFigure.this.hasFocus()) {
                    graphics.setForegroundColor(ColorConstants.black);
                    graphics.setBackgroundColor(ColorConstants.white);

                    Rectangle area = getClientArea();
                    graphics.drawFocus(area.x, area.y, area.width-1, area.height-1);

                }
            }
        };
        labelFigure.setText(format(value));
        labelFigure.addMouseListener(new MouseListener.Stub(){
            @Override
            public void mousePressed(MouseEvent me) {
                if(!hasFocus())
                    requestFocus();
            }
        });
        add(labelFigure);

        ButtonBorder buttonBorder = new ButtonBorder(new ButtonScheme(new Color[]{ColorConstants.buttonLightest},
                new Color[]{ColorConstants.buttonDarkest}));

        buttonUp = new RapArrowButton();
        buttonUp.setBorder(buttonBorder);
        buttonUp.setDirection(Orientable.NORTH);
        buttonUp.setFiringMethod(Clickable.REPEAT_FIRING);
        buttonUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                stepUp();
                if(!hasFocus())
                    requestFocus();
            }
        });
        add(buttonUp);

        buttonDown = new RapArrowButton();
        buttonDown.setBorder(buttonBorder);
        buttonDown.setDirection(Orientable.SOUTH);
        buttonDown.setFiringMethod(Clickable.REPEAT_FIRING);
        buttonDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                stepDown();
                if(!hasFocus())
                    requestFocus();
            }
        });
        add(buttonDown);



    }

    public void addManualValueChangeListener(IManualValueChangeListener listener){
        if(listener != null)
            spinnerListeners.add(listener);
    }

    /**
     * Inform all slider listeners, that the manual value has changed.
     *
     * @param newManualValue
     *            the new manual value
     */
    private void fireManualValueChange(final double newManualValue) {

            for (IManualValueChangeListener l : spinnerListeners) {
                l.manualValueChanged(newManualValue);
            }
    }


    private String format(double value){
        DecimalFormat format;
        switch (formatType) {

        case EXP:
             StringBuffer pattern = new StringBuffer(10);
                pattern.append("0."); //$NON-NLS-1$
                for (int i=0; i<precision; ++i)
                    pattern.append('#'); //$NON-NLS-1$
                pattern.append("E0"); //$NON-NLS-1$
            format = new DecimalFormat(pattern.toString()); //$NON-NLS-1$
            return format.format(value);
        case HEX:
            return HEX_PREFIX + Long.toHexString((long)value);
        case DECIMAL:
        default:
            format = new DecimalFormat("0"); //$NON-NLS-1$
            format.setMaximumFractionDigits(precision);
            format.setMinimumFractionDigits(0);
            return format.format(value);
        }
    }

    /**
     * @return the buttonWidth
     */
    public int getButtonWidth() {
        return buttonWidth;
    }

    /**
     * @param buttonWidth the buttonWidth to set
     */
    public void setButtonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
    }

    /**
     * @return the formatType
     */
    public NumericFormatType getFormatType() {
        return formatType;
    }

    public TextFigure getLabelFigure() {
        return labelFigure;
    }

    /**
     * @return the max
     */
    public final double getMax() {
        return max;
    }

    /**
     * @return the min
     */
    public final double getMin() {
        return min;
    }


    public double getPageIncrement() {
        return pageIncrement;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }


    /**
     * @return the stepIncrement
     */
    public final double getStepIncrement() {
        return stepIncrement;
    }

    /**
     * @return the value
     */
    public final double getValue() {
        return value;
    }

    /**
     * @return true if arrow buttons on left side of the figure.
     */
    public boolean isArrowButtonsOnLeft() {
        return arrowButtonsOnLeft;
    }

    /**
     * @return true if arrow buttons layout is horizontal.
     */
    public boolean isArrowButtonsHorizontal() {
        return arrowButtonsHorizontal;
    }

    @Override
    protected void layout() {
        Rectangle clientArea = getClientArea();
        if(arrowButtonsOnLeft){
            if(arrowButtonsHorizontal){
                labelFigure.setBounds(new Rectangle(clientArea.x+1 + 2 * buttonWidth, clientArea.y,
                        clientArea.width - 2 * buttonWidth-1, clientArea.height));
                buttonUp.setBounds(new Rectangle(clientArea.x,
                        clientArea.y, buttonWidth, clientArea.height));
                buttonDown.setBounds(new Rectangle(clientArea.x + buttonWidth,
                        clientArea.y, buttonWidth, clientArea.height));
            }
            else{
                labelFigure.setBounds(new Rectangle(clientArea.x+1 + buttonWidth, clientArea.y,
                        clientArea.width - buttonWidth-1, clientArea.height));
                buttonUp.setBounds(new Rectangle(clientArea.x,
                        clientArea.y, buttonWidth, clientArea.height/2));
                buttonDown.setBounds(new Rectangle(clientArea.x,
                        clientArea.y + clientArea.height/2, buttonWidth, clientArea.height/2));
            }
        }else{
            if(arrowButtonsHorizontal){
                labelFigure.setBounds(new Rectangle(clientArea.x, clientArea.y,
                        clientArea.width - 2 * buttonWidth, clientArea.height));
                buttonUp.setBounds(new Rectangle(clientArea.x + clientArea.width - 2 * buttonWidth,
                        clientArea.y, buttonWidth, clientArea.height));
                buttonDown.setBounds(new Rectangle(clientArea.x + clientArea.width - buttonWidth,
                        clientArea.y, buttonWidth, clientArea.height));
            }
            else{
                labelFigure.setBounds(new Rectangle(clientArea.x, clientArea.y,
                        clientArea.width - buttonWidth, clientArea.height));
                buttonUp.setBounds(new Rectangle(clientArea.x + clientArea.width - buttonWidth,
                        clientArea.y, buttonWidth, clientArea.height/2));
                buttonDown.setBounds(new Rectangle(clientArea.x + clientArea.width - buttonWidth,
                        clientArea.y + clientArea.height/2, buttonWidth, clientArea.height/2));
            }
        }
        super.layout();
    }

    /**Set Value from manual control of the widget. Value will be coerced in range.
     * @param value
     */
    public boolean manualSetValue(double value){
        double oldValue = getValue();
        setValue(
                value < min ? min : (value > max ? max : value));
        return oldValue != getValue();
    }

    /**
     * Cause the spinner to decrease its value by its step increment;
     */
    protected void pageDown() {
        if(manualSetValue(getValue() - getPageIncrement()))
            fireManualValueChange(getValue());
    }

    /**
     * Cause the spinner to increase its value by its step increment;
     */
    protected void pageUp() {
        if(manualSetValue(getValue() + getPageIncrement()))
            fireManualValueChange(getValue());
    }

    public void removeManualValueChangeListener(IManualValueChangeListener listener){
        if(listener != null && spinnerListeners.contains(listener))
            spinnerListeners.remove(listener);
    }

    /**Set the position of arrow buttons
     * @param arrowButtonsOnLeft true if on left.
     */
    public void setArrowButtonsOnLeft(boolean arrowButtonsOnLeft) {
        this.arrowButtonsOnLeft = arrowButtonsOnLeft;
        revalidate();
    }

    /**Set the layout of the arrow buttons
     * @param arrowButtonsHorizontal true if horizontal.
     */
    public void setArrowButtonsHorizontal(boolean arrowButtonsHorizontal) {
        this.arrowButtonsHorizontal = arrowButtonsHorizontal;
        revalidate();
    }

    /**Set the displayed value in the spinner. It may out of the range.
     * @param value the value to be displayed
     */
    public final void setDisplayValue(double value){
        if(this.value == value)
            return;
        this.value = value;
        labelFigure.setText(format(value));
    }

    @Override
    public void setEnabled(boolean value) {
        buttonUp.setEnabled(value);
        buttonDown.setEnabled(value);
        labelFigure.setEnabled(value);
        super.setEnabled(value);
        repaint();
    }

    public void setFormatType(NumericFormatType formatType) {
        this.formatType = formatType;
        labelFigure.setText(format(value));
    }

    /**
     * @param max the max to set
     */
    public final void setMax(double max) {
        if(this.max == max || Double.isNaN(max))
            return;
        this.max = max;
        repaint();
    }

    /**
     * @param min the min to set
     */
    public final void setMin(double min) {
        if(this.min == min || Double.isNaN(min))
            return;
        this.min = min;
        repaint();
    }

    public void setPageIncrement(double pageIncrement) {
        this.pageIncrement = pageIncrement;
    }


    /**
     * @param precision the precision to set
     */
    public void setPrecision(int precision) {
        if(this.precision == precision)
            return;
        this.precision = precision;
        labelFigure.setText(format(value));
    }

    /**
     * @param stepIncrement the stepIncrement to set
     */
    public final void setStepIncrement(double stepIncrement) {
        this.stepIncrement = stepIncrement;
    }

    /**Set the value of the spinner. It will be coerced in the range.
     * This only update the text.
     * It will not notify listeners about the value change.
     * @param value the value to set
     * @return true if value changed. false otherwise.
     */
    public void setValue(double value) {
        if (this.value == value)
            return;
        this.value = value;
        labelFigure.setText(format(value));
    }


    /**
     * Cause the spinner to decrease its value by its step increment;
     */
    protected void stepDown() {
        if(manualSetValue(getValue() - getStepIncrement()))
            fireManualValueChange(getValue());
    }

    /**
     * Cause the spinner to increase its value by its step increment;
     */
    protected void stepUp() {
        if(manualSetValue(getValue() + getStepIncrement()))
            fireManualValueChange(getValue());
    }

    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
    }

    public void showText(boolean isVisible) {
        labelFigure.setVisible(isVisible);
        labelFigure.setEnabled(isVisible);
        repaint();
    }



}
