/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;

import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.draw2d.Figure;
import org.eclipse.swt.widgets.Display;


public class IntensityGraphManualTest extends AbstractWidgetTest{

    private static final int DataHeight = 1024;
    private static final int DataWidth = 1280;
    public static int count=0;
    public static int count2=0;
    long start = System.nanoTime();
    @Override
    public Figure createTestWidget() {
        final IntensityGraphFigure figure = new IntensityGraphFigure();
        final short[] simuData = new short[DataWidth*DataHeight*2];
        final short[] data = new short[DataWidth*DataHeight];
    int seed = count++;
                    for(int i=0; i<DataHeight; i++){
                        for(int j=0; j<DataWidth; j++){
                            int x = j-DataWidth;
                            int y = i-DataHeight;
                            int p = (int) Math.sqrt(x*x + y*y);
                            simuData[i*DataWidth + j] = (short) ( Math.sin(p*2*Math.PI/DataWidth + seed*Math.PI/100)*100);
                        }
                    }
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    count++;
                    try {

                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    long nanoTime = System.nanoTime();

                    System.arraycopy(simuData, count%DataWidth, data, 0, DataWidth*DataHeight);
//                    System.out.println((System.nanoTime()-nanoTime)/1000000 + " generating "+ data[0] + "Count: " +count);
//                    start=System.nanoTime();
                    Display.getDefault().asyncExec(new Runnable() {

                        @Override
                        public void run() {
//                            long nanoTime = 0;//System.nanoTime();

                            count2++;
                            figure.setDataArray(data);
//                            System.out.println(System.nanoTime()/1000 + " setting " + data[0] + "Count: " +count2);
                        }
                    });






                }
            }
        });
        t.start();

        figure.setMax(100);
        figure.setMin(-100);
        figure.setDataHeight(DataHeight);
        figure.setDataWidth(DataWidth);
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
        return false;
    }

}
