/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.common.trendplotter.propsheet;

import org.csstudio.common.trendplotter.model.AxisConfig;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.model.RequestType;
import org.csstudio.common.trendplotter.model.TraceType;
import org.eclipse.swt.graphics.RGB;

/**
 * TODO (jhatje) : 
 * 
 * @author jhatje
 * @since 10.05.2012
 */
public class BulkEditValueDataModel {
    
    private String _axisName;
    private Boolean _visible;
    private String _min;
    private String _max;
    private Boolean _autoScale;
    private Boolean _logScale;

    private boolean _axisNameChanged = false;
    private boolean _visibleChanged = false;
    private boolean _minChanged = false;
    private boolean _maxChanged = false;
    private boolean _autoScaleChanged = false;
    private boolean _logScaleChanged = false;

    /**
     * @param pvs
     */
    public void createDataFromSelection(AxisConfig[] conf) {
        //init with first item
        if(conf[0]!=null) {
            _visible = conf[0].isVisible();
            _axisName = conf[0].getName();
            _min = String.valueOf(conf[0].getMin());
            _max = String.valueOf(conf[0].getMax());
            _autoScale = conf[0].isAutoScale();
            _logScale = conf[0].isLogScale();
        }
        
        //Check if all selected items have the same value
        for (AxisConfig axisConfig : conf) {
            if (_visible == null || !_visible == axisConfig.isVisible()) {
                _visible = null;
            }
            if (!_axisName.equals(axisConfig.getName()) || _axisName.equals("")) {
                _axisName = "";
            }
            if (!_min.equals(String.valueOf(axisConfig.getMin())) || _min.equals("")) {
                _min = "";
            }
            if (!_max.equals(String.valueOf(axisConfig.getMax())) || _max.equals("")) {
                _max = "";
            }
            if (_autoScale == null || !_autoScale == axisConfig.isAutoScale()) {
                _autoScale = null;
            }
            if (_logScale == null || !_logScale == axisConfig.isLogScale()) {
                _logScale = null;
            }
        }
    }

    public void setVisible(Boolean visible) {
        _visible = visible;
    }

    public Boolean getVisible() {
        return _visible;
    }
    
    public boolean isVisibleChanged() {
        return _visibleChanged;
    }

    public void setVisibleChanged(boolean visibleChanged) {
        _visibleChanged = visibleChanged;
    }

    public void setAxisName(String name) {
        _axisName = name;
    }

    public void setAxisNameChanged(boolean nameChanged) {
        _axisNameChanged = nameChanged;
    }

    public String getAxisName() {
        return _axisName;
    }

    public boolean isAxisNameChanged() {
        return _axisNameChanged;
    }

    public String getMin() {
        return _min;
    }
    
    public void setMin(String min) {
        _min = min;
    }
    
    public String getMax() {
        return _max;
    }
    
    public void setMax(String max) {
        _max = max;
    }
    
    public boolean isMinChanged() {
        return _minChanged;
    }
    
    public void setMinChanged(boolean minChanged) {
        _minChanged = minChanged;
    }
    
    public boolean isMaxChanged() {
        return _maxChanged;
    }
    
    public void setMaxChanged(boolean maxChanged) {
        _maxChanged = maxChanged;
    }

    public void setAutoScale(Boolean autoScale) {
        _autoScale = autoScale;
    }

    public Boolean getAutoScale() {
        return _autoScale;
    }

    public void setAutoScaleChanged(boolean autoScaleChanged) {
        _autoScaleChanged = autoScaleChanged;
    }

    public boolean isAutoScaleChanged() {
        return _autoScaleChanged;
    }

    public void setLogScale(Boolean logScale) {
        _logScale = logScale;
    }

    public Boolean getLogScale() {
        return _logScale;
    }

    public void setLogScaleChanged(boolean logScaleChanged) {
        _logScaleChanged = logScaleChanged;
    }

    public boolean isLogScaleChanged() {
        return _logScaleChanged;
    }
}
