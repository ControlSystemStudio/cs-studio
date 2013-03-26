/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author nitishp
 */
public class IntensityGraph2DRendererExample {
    public static void main(String[] args) throws IOException
        {
            Cell2DDataset data = Cell2DDatasets.linearRange(new Cell2DDatasets.Function2D() {
                
                @Override
                public double getValue(double x, double y) {
                    return x+y;
                }
            }, RangeUtil.range(0, 10), 10, RangeUtil.range(0, 10), 10);
            BufferedImage image = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = (Graphics2D) image.getGraphics();
            IntensityGraph2DRenderer renderer = new IntensityGraph2DRenderer(640,480);
            renderer.draw(g, data);
            ImageIO.write(image, "png", new File("IntensityGraph.png"));
        }
    }
