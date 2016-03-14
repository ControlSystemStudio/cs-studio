/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.draw2d.Figure;
import org.eclipse.swt.widgets.Display;


public class IntensityGraphTest extends AbstractWidgetTest{

    public static int count=0;
    @Override
    public Figure createTestWidget() {
        final IntensityGraphFigure figure = new IntensityGraphFigure();

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    final double[] simuData = new double[1024*768];
                    int seed = count++;
                    for(int i=0; i<768; i++){
                        for(int j=0; j<1024; j++){
                            int x = j-128;
                            int y = i-128;
                            int p = (int) Math.sqrt(x*x + y*y);
                            simuData[i*1024 + j] = Math.sin(p*2*Math.PI/256 + seed*10);
                        }
                    }Arrays.fill(simuData, Math.random());
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            System.out.println(count);

                            figure.setDataArray(simuData);

                        }
                    });
                }
            }
        });
        t.start();

        figure.setMax(1);
        figure.setMin(-1);
        figure.setDataHeight(768);
        figure.setDataWidth(1024);
        figure.setColorMap(new ColorMap(PredefinedColorMap.JET, true, true));
        return figure;
    }


    @Override
    public String[] getPropertyNames() {
        String[] superProps =  super.getPropertyNames();
        String[] myProps = new String[]{
                "colorMap",
                "cropBottom",
                "cropLeft",
                "cropRight",
                "cropTop",
                "dataArray",
                "dataHeight",
                "dataWidth",
                "max",
                "min",
                "runMode",
                "showRamp"
        };

        return concatenateStringArrays(superProps, myProps);
    }


    @Override
    public Object generateTestData(PropertyDescriptor pd, Object seed) {
        if(pd.getName().equals("dataArray") && seed != null && seed instanceof Integer){
            double[] simuData = new double[65536];
            for(int i=0; i<256; i++){
                for(int j=0; j<256; j++){
                    int x = j-128;
                    int y = i-128;
                    int p = (int) Math.sqrt(x*x + y*y);
                    simuData[i*256 + j] = Math.sin(p*2*Math.PI/256 + (Integer)seed);
                }
            }
            return simuData;
        }else if(pd.getName().equals("dataWidth") || pd.getName().equals("dataHeight"))
            return 256;
        else if(pd.getName().equals("max"))
            return 1;
        else if(pd.getName().equals("min"))
            return -1;
        else if(pd.getName().equals("colorMap") && seed != null && seed instanceof Integer)
            return new ColorMap(PredefinedColorMap.values()[(Integer)seed % 6 + 1], true, true);
        return super.generateTestData(pd, seed);
    }

    @Override
    public boolean isAutoTest() {
        // TODO Auto-generated method stub
        return false;
    }

}
