
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.application.weightrequest.data;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 01.12.2011
 */
public class MetaData {
    
    private String _pvName;
    private String _pvEgu;
    private short _precision;
    private Double _lowerWarningValue = Double.valueOf(-10000000000.0D);
    private Double _upperWarningValue = Double.valueOf(10000000000.0D);

    private Double _lowerAlarmValue = Double.valueOf(-100000000000.0D);
    private Double _upperAlarmValue = Double.valueOf(100000000000.0D);

    private Double _lowerControlValue = Double.valueOf(0.0D);
    private Double _upperControlValue = Double.valueOf(100.0D);

    private Double _lowerDisplayValue = Double.valueOf(0.0D);
    private Double _upperDisplayValue = Double.valueOf(100.0D);

    public MetaData() {
      this._pvName = null;
      this._pvEgu = null;
      this._precision = 0;
      this._lowerWarningValue = Double.valueOf(-10000000000.0D);
      this._upperWarningValue = Double.valueOf(10000000000.0D);
      this._lowerAlarmValue = Double.valueOf(-100000000000.0D);
      this._upperAlarmValue = Double.valueOf(100000000000.0D);
      this._lowerControlValue = Double.valueOf(0.0D);
      this._upperControlValue = Double.valueOf(100.0D);
      this._lowerDisplayValue = Double.valueOf(0.0D);
      this._upperDisplayValue = Double.valueOf(100.0D);
    }

    public void setAlarmLimits(Double lowerWarningValue,
                               Double upperWarningValue,
                               Double lowerAlarmValue,
                               Double upperAlarmValue) {
      
        this._lowerWarningValue = lowerWarningValue;
      this._upperWarningValue = upperWarningValue;
      this._lowerAlarmValue = lowerAlarmValue;
      this._upperAlarmValue = upperAlarmValue;
    }

    public void setControlValues(Double lowerControlValue, Double upperControlValue)
    {
      this._lowerControlValue = lowerControlValue;
      this._upperControlValue = upperControlValue;
    }

    public void setDisplayValues(Double lowerDisplayValue, Double upperDisplayValue)
    {
      this._lowerDisplayValue = lowerDisplayValue;
      this._upperDisplayValue = upperDisplayValue;
    }

    public String getPvName() {
      return this._pvName;
    }

    public void setPvName(String pvName) {
      this._pvName = pvName;
    }

    public String getEgu() {
      return this._pvEgu;
    }

    public void setEgu(String egu) {
      this._pvEgu = egu;
    }

    public short getPrecision() {
      return this._precision;
    }

    public void setPrecision(short precision) {
      this._precision = precision;
    }

    public Double getLowerWarningValue() {
      return this._lowerWarningValue;
    }

    public void setLowerWarningValue(Double lowerWarningValue) {
      this._lowerWarningValue = lowerWarningValue;
    }

    public Double getUpperWarningValue() {
      return this._upperWarningValue;
    }

    public void setUpperWarningValue(Double upperWarningValue) {
      this._upperWarningValue = upperWarningValue;
    }

    public Double getLowerAlarmValue() {
      return this._lowerAlarmValue;
    }

    public void setLowerAlarmValue(Double lowerAlarmValue) {
      this._lowerAlarmValue = lowerAlarmValue;
    }

    public Double getUpperAlarmValue() {
      return this._upperAlarmValue;
    }

    public void setUpperAlarmValue(Double upperAlarmValue) {
      this._upperAlarmValue = upperAlarmValue;
    }

    public Double getLowerControlValue() {
      return this._lowerControlValue;
    }

    public void setLowerControlValue(Double lowerControlValue) {
      this._lowerControlValue = lowerControlValue;
    }

    public Double getUpperControlValue() {
      return this._upperControlValue;
    }

    public void setUpperControlValue(Double upperControlValue) {
      this._upperControlValue = upperControlValue;
    }

    public Double getLowerDisplayValue() {
      return this._lowerDisplayValue;
    }

    public void setLowerDisplayValue(Double lowerDisplayValue) {
      this._lowerDisplayValue = lowerDisplayValue;
    }

    public Double getUpperDisplayValue() {
      return this._upperDisplayValue;
    }

    public void setUpperDisplayValue(Double upperDisplayValue) {
      this._upperDisplayValue = upperDisplayValue;
    }
}
