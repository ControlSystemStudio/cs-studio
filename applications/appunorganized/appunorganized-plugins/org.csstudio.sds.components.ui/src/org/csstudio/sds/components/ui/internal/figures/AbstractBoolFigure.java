package org.csstudio.sds.components.ui.internal.figures;

import java.util.Arrays;

import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base figure for a widget based on {@link AbstractBoolWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public class AbstractBoolFigure extends Figure implements IAdaptable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBoolFigure.class);

    protected Label boolLabel;

    protected long value = 0;

    protected int bit = -1;

    protected boolean showBooleanLabel = false;

    protected boolean boolValue = false;

    protected String onLabel = "ON";

    protected String offLabel = "OFF";

    protected Color onColor = CustomMediaFactory.getInstance()
            .getColor(CustomMediaFactory.COLOR_GREEN);

    protected Color offColor = CustomMediaFactory.getInstance().getColor(new RGB(0, 128, 0));

    private CrossedOutAdapter _crossedOutAdapter;

    private RhombusAdapter _rhombusAdapter;


    protected AbstractBoolFigure() {
        boolLabel = new Label(offLabel);
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    /**
     * @return the boolValue
     */
    public boolean getBoolValue() {
        return boolValue;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final double value) {
        this.value = (long) value;
        updateBoolValue();
        revalidate();
        repaint();
    }

    public void setBooleanValue(final Boolean value) {
        this.boolValue = value;
        updateValue();
    }

    /**
     * @param bit
     *            the bit to set
     */
    public void setBit(final int bit) {
        this.bit = bit;
        updateBoolValue();
    }

    /**
     * update the boolValue from value and bit. All the boolValue based behavior changes should be
     * implemented here by inheritance.
     */
    protected void updateBoolValue() {
        // get boolValue
        if (bit == -1) {
            boolValue = (this.value != 0);
        } else if (bit >= 0) {
            char[] binArray = Long.toBinaryString(this.value).toCharArray();
            if (bit >= binArray.length) {
                boolValue = false;
            } else {
                boolValue = (binArray[binArray.length - 1 - bit] == '1');
            }
        }
        // change boolLabel text
        if (boolValue) {
            boolLabel.setText(onLabel);
        } else {
            boolLabel.setText(offLabel);
        }
    }

    /**
     * update the value from boolValue
     */
    protected void updateValue() {
        // get boolValue
        if (bit == -1) {
            setValue(boolValue ? 1 : 0);
        } else if (bit >= 0) {
            char[] binArray = Long.toBinaryString(value).toCharArray();
            if ((bit >= 64) || (bit < -1)) {
                try {
                    throw new Exception("bit is out of range: [-1,63]");
                } catch (Exception e) {
                    LOG.error(e.toString());
                }
            } else {
                char[] bin64Array = new char[64];
                Arrays.fill(bin64Array, '0');
                for (int i = 0; i < binArray.length; i++) {
                    bin64Array[64 - binArray.length + i] = binArray[i];
                }
                bin64Array[63 - bit] = boolValue ? '1' : '0';
                String binString = new String(bin64Array);

                if (binString.indexOf('1') <= -1) {
                    binArray = new char[] { '0' };
                } else {
                    binArray = new char[64 - binString.indexOf('1')];
                    for (int i = 0; i < binArray.length; i++) {
                        binArray[i] = bin64Array[i + 64 - binArray.length];
                    }
                }

                binString = new String(binArray);
                setValue(Long.parseLong(binString, 2));
            }
        }
    }

    /**
     * @param showBooleanLabel
     *            the showBooleanLabel to set
     */
    public void setShowBooleanLabel(final boolean showBooleanLabel) {
        this.showBooleanLabel = showBooleanLabel;
        boolLabel.setVisible(showBooleanLabel);
    }

    /**
     * @param onLabel
     *            the onLabel to set
     */
    public void setOnLabel(final String onLabel) {
        this.onLabel = onLabel;
        if (boolValue) {
            boolLabel.setText(onLabel);
        }
    }

    /**
     * @param offLabel
     *            the offLabel to set
     */
    public void setOffLabel(final String offLabel) {
        this.offLabel = offLabel;
        if (!boolValue) {
            boolLabel.setText(offLabel);
        }
    }

    public void setOnColor(final Color color) {
        this.onColor = color;
    }

    public void setOffColor(final Color color) {
        this.offColor = color;
    }

    @Override
    public void setFont(final Font f) {
        super.setFont(f);
        boolLabel.setFont(f);
    }

    public Object getAdapter(final Class adapter) {
        if (adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if (adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }
        return null;
    }

}
