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
public class BulkEditTraceDataModel {
    
    private String _traceDisplayName;
    private double _scanPeriod;
    private int _liveCapacity;
    private int _lineWidth;
    private AxisConfig _axis;
    private TraceType _traceType;
    private RequestType _requestType;
    private RGB _rgb;
    private Boolean _visible;


    private boolean _traceDisplayNameChanged = false;
    private boolean _scanPeriodChanged = false;
    private boolean _liveCapacityChanged = false;
    private boolean _lineWidthChanged = false;
    private boolean _axisChanged = false;
    private boolean _traceTypeChanged = false;
    private boolean _requestTypeChanged = false;
    private boolean _rgbChanged = false;
    private boolean _visibleChanged = false;
    
    /**
     * @param pvs
     */
    public void createDataFromSelection(PVItem[] pvs) {
        //init with first item
        if(pvs[0]!=null) {
            _traceDisplayName = pvs[0].getDisplayName();
            _scanPeriod = pvs[0].getScanPeriod();
            _liveCapacity = pvs[0].getLiveCapacity();
            _lineWidth = pvs[0].getLineWidth();
            _axis = pvs[0].getAxis();
            _traceType = pvs[0].getTraceType();
            _requestType = pvs[0].getRequestType();
            _rgb = pvs[0].getColor();
            _visible = pvs[0].isVisible();
        }
        
        //Check if all selected items have the same value
        for (PVItem pvItem : pvs) {
            if (_visible == null || !_visible == pvItem.isVisible()) {
                _visible = null;
            }
            if (!_traceDisplayName.equals(pvItem.getDisplayName()) || _traceDisplayName.equals("")) {
                _traceDisplayName = "";
            }
            if (_scanPeriod != pvItem.getScanPeriod()  || _scanPeriod == -1) {
                _scanPeriod = -1;
            }
            if (_liveCapacity != pvItem.getLiveCapacity()  || _liveCapacity == -1) {
                _liveCapacity = -1;
            }
            if (_lineWidth != pvItem.getLineWidth()  || _lineWidth == -1) {
                _lineWidth = -1;
            }
            if (_axis == null || !_axis.getName().equals(pvItem.getAxis().getName())) {
                _axis = null;
            }
            if (_traceType == null || !_traceType.name().equals(pvItem.getTraceType().name())) {
                _traceType = null;
            }
            if (_requestType == null || !_requestType.name().equals(pvItem.getRequestType().name())) {
                _requestType = null;
            }
            if (_rgb == null || !_rgb.equals(pvItem.getColor())) {
                _rgb = null;
            }
        }
    }
    
    public String getTraceDisplayName() {
        return _traceDisplayName;
    }

    public double getScanPeriod() {
        return _scanPeriod;
    }

    public void setTraceDisplayName(String value) {
        _traceDisplayName = value;
    }

    public void setScanPeriod(double value) {
        _scanPeriod = value;
    }

    public int getLiveCapacity() {
        return _liveCapacity;
    }

    public void setLiveCapacity(Integer value) {
        _liveCapacity = value;
    }

    public void setLineWidth(int lineWidth) {
        _lineWidth = lineWidth;
    }

    public int getLineWidth() {
        return _lineWidth;
    }

    public void setAxis(AxisConfig axis) {
        _axis = axis;
    }

    public AxisConfig getAxis() {
        return _axis;
    }

    public void setTraceType(TraceType traceType) {
        _traceType = traceType;
    }

    public TraceType getTraceType() {
        return _traceType;
    }

    public void setRequestType(RequestType requestType) {
        _requestType = requestType;
    }

    public RequestType getRequestType() {
        return _requestType;
    }

    public void setRgb(RGB rgb) {
        _rgb = rgb;
    }

    public RGB getRgb() {
        return _rgb;
    }

    public void setVisible(Boolean visible) {
        _visible = visible;
    }

    public Boolean getVisible() {
        return _visible;
    }
    
    public boolean isTraceDisplayNameChanged() {
        return _traceDisplayNameChanged;
    }

    public void setTraceDisplayNameChanged(boolean traceDisplayNameChanged) {
        _traceDisplayNameChanged = traceDisplayNameChanged;
    }

    public boolean isScanPeriodChanged() {
        return _scanPeriodChanged;
    }

    public void setScanPeriodChanged(boolean scanPeriodChanged) {
        _scanPeriodChanged = scanPeriodChanged;
    }

    public boolean isLiveCapacityChanged() {
        return _liveCapacityChanged;
    }

    public void setLiveCapacityChanged(boolean liveCapacityChanged) {
        _liveCapacityChanged = liveCapacityChanged;
    }

    public boolean isLineWidthChanged() {
        return _lineWidthChanged;
    }

    public void setLineWidthChanged(boolean lineWidthChanged) {
        _lineWidthChanged = lineWidthChanged;
    }

    public boolean isAxisChanged() {
        return _axisChanged;
    }

    public void setAxisChanged(boolean axisChanged) {
        _axisChanged = axisChanged;
    }

    public boolean isTraceTypeChanged() {
        return _traceTypeChanged;
    }

    public void setTraceTypeChanged(boolean traceTypeChanged) {
        _traceTypeChanged = traceTypeChanged;
    }

    public boolean isRequestTypeChanged() {
        return _requestTypeChanged;
    }

    public void setRequestTypeChanged(boolean requestTypeChanged) {
        _requestTypeChanged = requestTypeChanged;
    }

    public boolean isRgbChanged() {
        return _rgbChanged;
    }

    public void setRgbChanged(boolean rgbChanged) {
        _rgbChanged = rgbChanged;
    }

    public boolean isVisibleChanged() {
        return _visibleChanged;
    }

    public void setVisibleChanged(boolean visibleChanged) {
        _visibleChanged = visibleChanged;
    }

    public void setLiveCapacity(int liveCapacity) {
        _liveCapacity = liveCapacity;
    }

}
