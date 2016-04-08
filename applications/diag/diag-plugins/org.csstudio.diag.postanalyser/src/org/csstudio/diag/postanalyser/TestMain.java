package org.csstudio.diag.postanalyser;

import java.time.Instant;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.diag.postanalyser.model.Channel;
import org.csstudio.diag.postanalyser.model.Model;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Standalone "main" test of the GUI with demo data */
public class TestMain
{
    /** Data length */
    final private static int N = 100;

    @SuppressWarnings("nls")
    public static void main(String[] args) throws Exception
    {
        // Initialize SWT
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("A RDB Shell Example");

        final Model model = new Model();

        final double x[] = createX();
        model.addChannel(new Channel("Fred", x, createLine(x, 2.5, 3.0)));
        model.addChannel(new Channel("Jane", x, createLine(x, -3.0, 5.0)));
        // TODO Line fit to flat line looks funny:
        // Result is a line 0*x + 4.99999, and that gets displayed
        // zoomed waaay in.
        model.addChannel(new Channel("Flat", x, createLine(x, 0.0, 5.0, 0.0)));
        model.addChannel(new Channel("Narrow", x, createGauss(x, 0.3)));
        model.addChannel(new Channel("Wide", x, createGauss(x, 3.0)));

        final double samples[] = new double[]
        { 7.0228, 7.0228, 7.0228, 7.0232, 7.0235, 7.0238, 7.0242, 7.0247,
                7.0254, 7.0258, 7.0262, 7.0265, 7.0268, 7.0272, 7.0275, 7.0278,
                7.0282, 7.0285, 7.0292, 7.0295, 7.0299, 7.0302, 7.0307, 7.0312,
                7.0315, 7.0319, 7.0322, 7.0325, 7.0329, 7.0332, 7.0335, 7.0339,
                7.0342, 7.0345, 7.0349, 7.0352, 7.0355, 7.0359, 7.0364, 7.0364,
                7.0369, 7.0372, 7.0377, 7.0382, 7.0385, 7.0389, 7.0385, 7.0389,
                7.0392, 7.0395, 7.0392, 7.0395, 7.0399, 7.0397, 7.0402, 7.0405,
                7.0402, 7.0405, 7.0402, 7.0405, 7.0409, 7.0405, 7.0409, 7.0410,
                7.0412, 7.0409, 7.0412, 7.0409, 7.0407, 7.0405, 7.0402, 7.0399,
                7.0395, 7.0392, 7.0389, 7.0385, 7.0382, 7.0379, 7.0375, 7.0372,
                7.0369, 7.0364, 7.0359, 7.0355, 7.0352, 7.0349, 7.0349, 7.0345,
                7.0342, 7.0339, 7.0335, 7.0339, 7.0334, 7.0329, 7.0324, 7.0325,
                7.0322, 7.0319, 7.0315, 7.0312, 7.0309, 7.0304, 7.0305, 7.0302,
                7.0299, 7.0295, 7.0292, 7.0288, 7.0284, 7.0278, 7.0275, 7.0272,
                7.0268, 7.0265, 7.0262, 7.0258, 7.0255, 7.0250, 7.0245, 7.0242,
                7.0237, 7.0232, 7.0228 };
        final double samples_x[] = new double[samples.length];
        for (int i=0; i<samples_x.length; ++i)
            samples_x[i] = 70000*i;

        model.addChannel(new Channel("Samples", samples_x, samples));

        model.addChannel(new Channel("Slow", x, createExp(x, 0.0, 2000.0, 5.0)));
        model.addChannel(new Channel("Fast", x, createExp(x, 0.0, 2000.0, 0.5)));
        model.addChannel(new Channel("Slow Offset", x, createExp(x, 500.0, 2000.0, 5.0)));
        model.addChannel(new Channel("Fast Offset", x, createExp(x, 500.0, 2000.0, 0.5)));

        model.addChannel(new Channel("0.1 Hz", x, createPeriod(x, 1.0)));
        model.addChannel(new Channel("1.0 Hz", x, createPeriod(x, 10.0)));
        model.addChannel(new Channel("2.0 Hz", x, createPeriod(x, 20.0)));
        model.addChannel(new Channel("4.5 Hz", x, createPeriod(x, 45.0)));
        model.addChannel(new Channel("6.0 Hz", x, createPeriod(x, 60.0)));

        new GUI(model, shell);

        shell.pack();
        shell.open();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    /** @return Linear 'x' vector 0.0 ... 10.0 */
    private static double[] createX()
    {
        final double start = TimestampHelper.toMillisecs(Instant.now());
        // final double start = 0.0;
        final double x[] = new double[N];
        for (int i = 0; i < N; ++i)
            x[i] = start + 10.0 * i / (N-1);
        return x;
    }

    /** Create line
     *  @param x X axis locations
     *  @param slope Line slope
     *  @param intersect Intersection with Y axis
     *  @return Line values for x
     */
    private static double[] createLine(final double[] x, final double slope,
            final double intersect)
    {
        return createLine(x, slope, intersect, 1.0);
    }

    /** Create line
     *  @param x X axis locations
     *  @param slope Line slope
     *  @param intersect Intersection with Y axis
     *  @param noise Amplitude of added noise
     *  @return Line values for x
     */
    private static double[] createLine(final double[] x, final double slope,
            final double intersect, final double noise)
    {
        final double y[] = new double[N];
        for (int i = 0; i < N; ++i)
            y[i] = slope * x[i] + intersect + noise * (Math.random() - 0.5);
        return y;
    }

    /** @return Gaussian curve for x */
    private static double[] createGauss(final double[] x, final double sigma)
    {
        final double base = 1.0;
        final double amp = 2.0;
        final double center = 4.0;

        final double data[] = new double[x.length];
        final double x0 = x[0];
        for (int i = 0; i < x.length; ++i)
        {
            final double d_x = (x[i]-x0) - center;
            data[i] = base + amp * Math.exp(-(d_x * d_x) / (2 * sigma * sigma));
            data[i] += 0.2 * (Math.random() - 0.5);
        }
        return data;
    }

    /** @return Exponential curve for x */
    private static double[] createExp(final double[] x,
            final double base, final double amp, final double decay)
    {
        final double data[] = new double[x.length];
        final double x0 = x[0];
        for (int i = 0; i < x.length; ++i)
        {
            data[i] = base + amp * Math.exp(-(x[i]-x0) / decay);
            data[i] += 0.2 * (Math.random() - 0.5);
        }
        return data;
    }

    /** @return periodic signal over x */
    private static double[] createPeriod(double[] x, double periods)
    {
        final double dc = 1.0;

        final double data[] = new double[N];
        for (int i = 0; i < N; ++i)
        {
            data[i] = dc + Math.cos(2.0*Math.PI*periods*i/N);
            data[i] += 0.2 * (Math.random() - 0.5);
        }
        return data;
    }
}
