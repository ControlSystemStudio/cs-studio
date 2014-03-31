/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VByte;
import org.epics.vtype.VTypeToString;

/**
 * @author dkumar
 */
public class PVFieldToVByte extends AlarmTimeDisplayExtractor implements VByte {
 
  protected final Byte value;


  /**
   * @param pvField
   * @param disconnected
   */
  public PVFieldToVByte(PVStructure pvField, boolean disconnected) {
    super(pvField, disconnected);

	PVField field = pvField.getSubField("value");
	if (field instanceof PVScalar)
		value = convert.toByte((PVScalar)field);
    else
    	value = null;
  }


  @Override
  public Byte getValue() {
    return value;
  }


  @Override
  public String toString() {
    return VTypeToString.toString(this);
  }
}
