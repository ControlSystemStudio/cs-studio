package org.csstudio.diag.postanalyser.math;

import static org.junit.Assert.*;

import org.junit.Test;

/** Test of the Fourier code.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FourierTest
{
    private static final double EPS = 0.0001;

    @Test
    public void testFourier()
    {
        // Number of samples
        final int N = 200;
        // Sample period in seconds
        final double period = 1.0;

        // Signal with one full period in N samples
        final double sine_period = period*N;
        // .. that as a frequency in Hz
        final double signal_freq = 1.0/sine_period;
        // Another signal at 0.25 Hz
        final double signal_freq2 = 0.25;
        // DC amplitude
        final double dc = 1.0;

        // Create the signal
        System.out.println("# Signal");
        final double time[] = new double[N];
        final double signal[] = new double[N];
        for (int i=0; i<N; ++i)
        {
            time[i] = period*i;
            signal[i] = dc + Math.cos(2.0*Math.PI * signal_freq  * time[i])
                           + Math.cos(2.0*Math.PI * signal_freq2 * time[i]);
            System.out.format("%g\t%g\n", time[i], signal[i]);
        }


        final Fourier fft = new Fourier(period, signal);
        final double ampl[] = fft.getAmplitudes();
        final double freq[] = fft.getFrequencies();
        System.out.println("\n\n# Fourier");
        for (int i=0; i<ampl.length; ++i)
            System.out.format("%g\t%g\n", freq[i], ampl[i]);

        System.out.println("# DC component             : " + ampl[0]);
        assertEquals(dc, ampl[0], EPS);
        System.out.println("# Full period amplitude    : " + ampl[1]);
        assertEquals(0.5, ampl[1], EPS);
        System.out.println("# Harmonic period amplitude: " + ampl[2]);
        assertEquals(0, ampl[2], EPS);
        // Locate the second frequency
        for (int i=0; i<freq.length; ++i)
        {
            if (Math.abs(freq[i] - signal_freq2) < EPS)
            {
                System.out.format("# %g Hz: %g\n", freq[i], ampl[i]);
                assertEquals(0.5, ampl[i], EPS);
            }
        }

        System.out.println("\n\n# Gnuplot: Paste output into file 'x', then:");
        System.out.println("# plot 'x' index 0 with lines");
        System.out.println("# plot 'x' index 1 with lines");
    }
}
