package org.csstudio.diag.postanalyser.math;

/** Discrete filters.
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public class Filter
{
    /**  Filter (Window) types */
    public enum Type
    {
        None,
        Hamming,
        /** von Hann, sometimes improperly called Hanning */
        Hanning,
        Blackman,
        /** Bartlett (triangle) */
        Bartlett,
        Blackman_Harris
    }

    /** Apply a window filter to the data
     *  @param signal Input signal, N points
     *  @param type Filter
     *  @return Filtered (windowed) signal, also N points.
     */
    public static double[] window(final double[] signal, final Type type)
    {
        if (type == Type.None)
            return signal;
        final int N = signal.length;
        final double output[] = new double[N];
        for (int i = 0; i < N; ++i)
            output[i] = coeff(type, i, N) * signal[i];
        return output;
    }

    /** Obtain filter coefficient.
     *  @param type Window type
     *  @param i Index of filter coefficient, 0...N-1
     *  @param N Length of data
     *  @return
     */
    private static double coeff(final Type type, final int i, final int N)
    {
        switch (type)
        {
        case Hamming:
            return  (0.54 - 0.46 * Math.cos(2.0*Math.PI * i / (N - 1)))
                  * 0.5 * (1. - Math.cos(2.0*Math.PI * i / (N - 1)));
        case Hanning:
            return 0.5 * (1.0 - Math.cos(2.0*Math.PI * i / (N - 1)));
        case Blackman:
            return 0.42 - 0.5 * Math.cos(2.0*Math.PI * i / (N - 1)) + 0.08
                        * Math.cos(2. * 2.0*Math.PI * i / (N - 1));
        case Bartlett:
            if (i <= (N - 1) / 2)
                return 2.0 * i / (N - 1);
            else
                return 2.0 - 2.0 * i / (N - 1);
        case Blackman_Harris:
            final double a0 = 0.35875;
            final double a1 = 0.48829;
            final double a2 = 0.14128;
            final double a3 = 0.01168;
            return a0
                - a1 * Math.cos(2.0*Math.PI *      (i + 0.5) / N)
                + a2 * Math.cos(2.0*Math.PI * 2. * (i + 0.5) / N)
                - a3 * Math.cos(2.0*Math.PI * 3. * (i + 0.5) / N);
        default:
            return 1.0;
        }
    }
}
