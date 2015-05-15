/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.datadefinition;

/**A wrapper for double[].
 * @author Xihui Chen
 *
 */
public class DoubleArrayWrapper implements IPrimaryArrayWrapper {

    public double[] data;



    public DoubleArrayWrapper(double[] data) {
        this.data = data;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public double get(int i) {
        return data[i];
    }

    public int getSize() {
        return data.length;
    }

}
