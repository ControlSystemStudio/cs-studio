/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.EventListener;

import org.csstudio.swt.widgets.figureparts.RapArrowButton;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * The view for ThumbWheel.
 *
 * @author Alen Vrecko, Jose Ortega, Xihui Chen, Takashi Nakamoto
 *
 */
public class ThumbWheelFigure extends Figure implements Introspectable{

    /**
     * Represents a box with a single char in it.
     *
     */
    private static class CharBox extends Figure {

        private Label label;
        private Color color;
        private int thickness;

        public CharBox(char ch) {

            BorderLayout layout = new BorderLayout();
            layout.setVerticalSpacing(0);

            setLayoutManager(layout);

            label = new Label("" + ch);
            add(label);
            setConstraint(label, BorderLayout.CENTER);

        }

        public void setBorderColor(Color color) {
            this.color = color;
            if (color == null) {
                setBorder(null);
            } else {
//                if (color != null) {
                    setBorder(new LineBorder(color, thickness));
//                } else {
//                    setBorder(new LineBorder(thickness));
//                }
            }
        }

        public void setBorderThickness(int thickness) {
            this.thickness = thickness;
            if (thickness == 0) {
                setBorder(null);
            } else {
                if (color != null) {
                    setBorder(new LineBorder(color, thickness));
                } else {
                    setBorder(new LineBorder(thickness));
                }
            }
        }

        public void setChar(char c) {
            label.setText("" + c);
        }

        @Override
        public void setEnabled(boolean value) {
            super.setEnabled(value);
            setChildrenEnabled(value);
        }


    }
    /**
     * Represents a box with a digit and up and down button. Calls
     * increment/decrement listeners on button clicks.
     *
     */
    private class DigitBox extends Figure {

        private Label label;
        private int thickness;
        private Color color;
        private Color colorFocused;
        private ArrowButton up;
        private ArrowButton down;

        private DigitBox right = null;
        private DigitBox left = null;

        public DigitBox(final int positionIndex, final boolean isDecimal) {

            BorderLayout layout = new BorderLayout();
            layout.setVerticalSpacing(0);

            setLayoutManager(layout);

            label = new Label("0");
            up = new RapArrowButton(ArrowButton.NORTH);
            up.setFiringMethod(ArrowButton.REPEAT_FIRING);
            up.setPreferredSize(20, 20);
            if (isDecimal) {
                up.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        fireIncrementDecimalListeners(positionIndex);
                    }

                });
            } else {
                up.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        fireIncrementIntegerListeners(integerDigits
                                - positionIndex - 1);
                    }
                });
            }

            add(up);
            setConstraint(up, BorderLayout.TOP);

            label.setPreferredSize(20, 10);
            add(label);
            setConstraint(label, BorderLayout.CENTER);

            down = new RapArrowButton(ArrowButton.SOUTH);
            down.setFiringMethod(ArrowButton.REPEAT_FIRING);
            down.setPreferredSize(20, 20);
            if (isDecimal) {
                down.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        fireDecrementDecimalListeners(positionIndex);
                    }
                });
            } else {
                down.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        fireDecrementIntegerListeners(integerDigits
                                - positionIndex - 1);
                    }
                });
            }

            add(down, BorderLayout.BOTTOM);

            // Enable focusing and key operation only in run mode.
            if (runmode) {
                setRequestFocusEnabled(true);
                setFocusTraversable(true);

                // When the user clicks this figure, the focus is requested so that
                // this widget can get key events.
                addMouseListener(new MouseListener.Stub() {
                    @Override
                    public void mousePressed(MouseEvent me) {
                        if (!hasFocus())
                            requestFocus();
                        me.consume();
                    }
                });

                // Repaint the figure to show or hide focus indicator.
                addFocusListener(new FocusListener() {
                    public void focusGained(FocusEvent fe) {
                        if (colorFocused == null) {
                            setBorder(null);
                        } else {
                            setBorder(new LineBorder(colorFocused, thickness));
                        }
                    }

                    public void focusLost(FocusEvent fe) {
                        if (color == null) {
                            setBorder(null);
                        } else {
                            setBorder(new LineBorder(color, thickness));
                        }
                    }
                });

                // Increment or decrement the focused digit when the user
                // releases up/down arrow key. Focus adjacent digit when
                // the user releases left/right arrow key.
                addKeyListener(new KeyListener.Stub() {
                    @Override
                    public void keyPressed(KeyEvent ke) {
                        if (ke.keycode == SWT.ARROW_UP) {
                            if (isDecimal) {
                                fireIncrementDecimalListeners(positionIndex);
                            } else {
                                fireIncrementIntegerListeners(integerDigits
                                        - positionIndex - 1);
                            }
                        } else if (ke.keycode == SWT.ARROW_DOWN) {
                            if (isDecimal) {
                                fireDecrementDecimalListeners(positionIndex);
                            } else {
                                fireDecrementIntegerListeners(integerDigits
                                        - positionIndex - 1);
                            }
                        } else if (ke.keycode == SWT.ARROW_LEFT && left != null) {
                            left.requestFocus();
                        } else if (ke.keycode == SWT.ARROW_RIGHT && right != null) {
                            right.requestFocus();
                        }
                    }
                });
            }
        }

        public void setBorderColor(Color color) {
            this.color = color;
            if (!hasFocus()) {
                if (color == null) {
                    setBorder(null);
                } else {
                    setBorder(new LineBorder(color, thickness));
                }
            }
        }

        public void setFocusedBorderColor(Color colorFocused) {
            this.colorFocused = colorFocused;
            if (hasFocus()) {
                if (colorFocused == null) {
                    setBorder(null);
                } else {
                    setBorder(new LineBorder(colorFocused, thickness));
                }
            }
        }

        public void setBorderThickness(int thickness) {
            this.thickness = thickness;
            if (thickness == 0) {
                setBorder(null);
            } else {
                if (!hasFocus() && color != null) {
                    setBorder(new LineBorder(color, thickness));
                } else if (hasFocus() && colorFocused != null) {
                    setBorder(new LineBorder(colorFocused, thickness));
                } else {
                    setBorder(new LineBorder(thickness));
                }

            }
        }

        @Override
        public void setEnabled(boolean value) {
            super.setEnabled(value);
            setChildrenEnabled(value);
        }

        public void setValue(String value) {
            label.setText("" + value);
        }

        public void setButtonVisibility(boolean b) {
            up.setVisible(b);
            down.setVisible(b);
        }

        public void setLeftDigitBox(DigitBox box) {
            this.left = box;
        }

        public void setRightDigitBox(DigitBox box) {
            this.right = box;
        }

    }

    public static interface WheelListener extends EventListener{
        /**
         * Signals decrement on a wheel of the integer part.
         *
         * @param index
         */
        void decrementDecimalPart(int index);

        /**
         * Signals decrement on a wheel of the integer part.
         *
         * @param index
         */
        void decrementIntegerPart(int index);

        /**
         * Signals increment on a wheel of the decimal part.
         *
         * @param index
         */
        void incrementDecimalPart(int index);

        /**
         * Signals increment on a wheel of the integer part.
         *
         * @param index
         */
        void incrementIntegerPart(int index);

    }

    /**
     * A border adapter, which covers all border handlings.
     */

    // need reference because of changing font and color
    private CharBox dot;
    private CharBox minus;

    private GridLayout layout = new GridLayout();

    // before the dot. 123,45 - we store 123
    private DigitBox[] wholePart;

    // part after the dot. 123,45 - we store 45
    private DigitBox[] decimalPart;

    private int integerDigits;

    private int decimalDigits;

    private boolean test;

    private Color internalBorderColor;

    private Color internalFocusedBorderColor;

    private int internalBorderThickness;
    private ArrayList<WheelListener> listeners = new ArrayList<WheelListener>();

    private Font wheelFont;

    private boolean showButtons = true;

    private boolean runmode;

    public ThumbWheelFigure(){
        this(3,2,false);
    }

    public ThumbWheelFigure(int integerWheels, int decimalDigits, boolean runmode) {
        integerDigits = integerWheels;
        this.decimalDigits = decimalDigits;

        this.runmode = runmode;

        // we will be displaying the widget anyway so I don't see a point in
        // deferring this till later.
        createWidgets();
    }

    public void addWheelListener(WheelListener listener) {
        listeners.add(listener);
    }

    public GridData createGridData() {
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        return data;
    }

    /**
     * Creates new widgets if needed to satisfy number of wheels specified.
     */
    private void createWidgets() {
        removeAll();
        wholePart = null;
        decimalPart = null;
        layout = new GridLayout(2 + integerDigits + decimalDigits, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayoutManager(layout);

        minus = new CharBox(' ');
        add(minus);
        setConstraint(minus, createGridData());
        wholePart = new DigitBox[integerDigits];
        for (int i = 0; i < integerDigits; i++) {
            DigitBox box = new DigitBox(i, false);
            box.setButtonVisibility(showButtons);
            add(box);
            wholePart[integerDigits - i - 1] = box;
            setConstraint(box, createGridData());

        }

        dot = new CharBox(' ');
        add(dot);
        setConstraint(dot, createGridData());

        decimalPart = new DigitBox[decimalDigits];
        for (int i = 0; i < decimalDigits; i++) {
            DigitBox box = new DigitBox(i, true);
            box.setButtonVisibility(showButtons);
            add(box);
            decimalPart[i] = box;

            setConstraint(box, createGridData());
        }

        setInternalBorderColor(internalBorderColor);
        setInternalFocusedBorderColor(internalFocusedBorderColor);
        setInternalBorderThickness(internalBorderThickness);
        setWheelFont(wheelFont);

        // Set the order to be able to change focus when left/right
        // key is pressed.
        for (int i = 0; i < integerDigits; i++) {
            if (i != integerDigits - 1) {
                wholePart[i].setLeftDigitBox(wholePart[i+1]);
            }
            if (i != 0) {
                wholePart[i].setRightDigitBox(wholePart[i-1]);
            }
        }

        if (integerDigits > 0 && decimalDigits > 0) {
            wholePart[0].setRightDigitBox(decimalPart[0]);
            decimalPart[0].setLeftDigitBox(wholePart[0]);
        }

        for (int i = 0; i < decimalDigits; i++) {
            if (i != 0) {
                decimalPart[i].setLeftDigitBox(decimalPart[i-1]);
            }
            if (i != decimalDigits - 1) {
                decimalPart[i].setRightDigitBox(decimalPart[i+1]);
            }
        }

        revalidate();
    }

    private void fireDecrementDecimalListeners(int index) {
        for (WheelListener listener : listeners) {
            listener.decrementDecimalPart(index);
        }
    }

    private void fireDecrementIntegerListeners(int index) {
        for (WheelListener listener : listeners) {
            listener.decrementIntegerPart(index);
        }
    }

    private void fireIncrementDecimalListeners(int index) {
        for (WheelListener listener : listeners) {
            listener.incrementDecimalPart(index);
        }
    }

    private void fireIncrementIntegerListeners(int index) {
        for (WheelListener listener : listeners) {
            listener.incrementIntegerPart(index);
        }
    }

    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
    }

    /**
     * @return the decimalDigits
     */
    public int getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * @return the integerDigits
     */
    public int getIntegerDigits() {
        return integerDigits;
    }

    /**
     * @return the internalBorderColor
     */
    public Color getInternalBorderColor() {
        return internalBorderColor;
    }

    /**
     * @return the internalFocusedBorderColor
     */
    public Color getInternalFocusedBorderColor() {
        return internalFocusedBorderColor;
    }

    /**
     * @return the internalBorderThickness
     */
    public int getInternalBorderThickness() {
        return internalBorderThickness;
    }

    /**
     * @return the wheelFont
     */
    public Font getWheelFont() {
        return wheelFont;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    // LISTENER PART

    public boolean isTest() {
        return test;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintFigure(final Graphics graphics) {
        graphics.setBackgroundColor(this.getBackgroundColor());
        Rectangle bounds = this.getBounds().getCopy();
        bounds.crop(this.getInsets());
        graphics.fillRectangle(bounds);
        super.paintFigure(graphics);
    }

    public void removeWheelListener(WheelListener listener) {
        listeners.remove(listener);
    }

    public void setDecimalDigits(int decimalDigits) {
        if(decimalDigits <0 || decimalDigits > 64)
            throw new IllegalArgumentException();
        if(this.decimalDigits == decimalDigits)
            return;
        this.decimalDigits = decimalDigits;
        createWidgets();

    }

    public void setDecimalWheel(int index, char value) {
        DigitBox box = decimalPart[index];
        box.setValue("" + value);
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        setChildrenEnabled(value);
        repaint();
    }

    public void setIntegerDigits(int integerDigits) {
        if(integerDigits < 0 || integerDigits > 64)
            throw new IllegalArgumentException();
        if(this.integerDigits == integerDigits)
            return;
        this.integerDigits = integerDigits;
        createWidgets();

    }

    public void setIntegerWheel(int index, char value) {
        DigitBox box = wholePart[index];
        box.setValue("" + value);

    }

    public void setInternalBorderColor(Color color) {
        this.internalBorderColor = color;
        Color col = color;

        for (DigitBox box : wholePart) {
            box.setBorderColor(col);
        }

        for (DigitBox box : decimalPart) {
            box.setBorderColor(col);
        }

        dot.setBorderColor(col);
        minus.setBorderColor(col);
    }

    public void setInternalFocusedBorderColor(Color color) {
        this.internalFocusedBorderColor = color;
        Color col = color;

        for (DigitBox box : wholePart) {
            box.setFocusedBorderColor(col);
        }

        for (DigitBox box : decimalPart) {
            box.setFocusedBorderColor(col);
        }
    }

    public void setInternalBorderThickness(int thickness) {
        if(thickness <0)
            throw new IllegalArgumentException();
        this.internalBorderThickness = thickness;
        for (DigitBox box : wholePart) {
            box.setBorderThickness(thickness);
        }

        for (DigitBox box : decimalPart) {
            box.setBorderThickness(thickness);
        }

        dot.setBorderThickness(thickness);
        minus.setBorderThickness(thickness);
    }



    public void setTest(boolean test) {
        this.test = test;
    }

    public void setWheelFont(Font font) {
        if(this.wheelFont != null && this.wheelFont.equals(font))
            return;
        this.wheelFont = font;
        if(font == null){
            return;
        }

        for (DigitBox box : wholePart) {
            box.setFont(font);
        }

        for (DigitBox box : decimalPart) {
            box.setFont(font);
        }

        dot.setFont(font);
        minus.setFont(font);
    }
    public void showDot(boolean b) {
        if (b) {
            dot.setChar('.');
        } else {
            dot.setChar(' ');
        }
    }
    public void showMinus(boolean b) {
        if (b) {
            minus.setChar('-');
        } else {
            minus.setChar(' ');
        }
    }

    public void setButtonVisibility(boolean b) {
        if (showButtons == b)
            return;

        showButtons = b;

        for (DigitBox box : wholePart) {
            box.setButtonVisibility(b);
        }

        for (DigitBox box : decimalPart) {
            box.setButtonVisibility(b);
        }
    }
}