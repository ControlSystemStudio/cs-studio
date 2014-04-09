/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVFloat;
import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VFloat;
import org.epics.vtype.VTypeToString;

/**
 * @author dkumar
 */
public class PVFieldToVFloat extends AlarmTimeDisplayExtractor implements VFloat {
 
  protected final Float value;


  /**
   * @param pvField
   * @param disconnected
   */
  public PVFieldToVFloat(PVStructure pvField, boolean disconnected) {
    super(pvField, disconnected);

    PVFloat floatField = pvField.getFloatField("value");
    if (floatField != null) {
      value = floatField.get();
    } else {
      value = null;
    }
  }


  @Override
  public Float getValue() {
    return value;
  }


  @Override
  public String toString() {
    return VTypeToString.toString(this);
  }
}
