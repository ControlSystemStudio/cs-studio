package org.csstudio.diag.postanalyser.math;

/** Perform FFT
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public class Fourier
{
    /** Number of points in original signal */
    final private int N;
    
    /** Sample period, distance between signal points, in seconds */
    final double period;
    
    /** Index of Nyquist frequency */
    final private int nyquist_index;
    
    /** FFT amplitudes, DC to nyquist_index */
    final private double amplitude[];
    
    /** Compute discrete Fourier transform
     *  <p>
     *  <code>FFT(n) = 1/N sum{k} [ signal(k) exp(-2 pi j k n/N) ]</code>
     *  <br>
     *  <code>amplitude = abs(FFT(n))</code>
     *  <p>
     *  For real-valued input signals, N points taken at a sample period
     *  of T seconds between samples, the amplitudes reflect:
     *  <ul>
     *  <li>Amplitude[0] for the DC component,
     *  <li>Amplitude[1] for the fundamental frequency, where the input signal
     *      contains one exact full period
     *  <li>Amplitude[2] for the harmonic, where the input signal
     *      contains two exact full periods
     *  <li>....
     *  <li>Amplitude[N/2] for the Nyquist frequency equal to (1/T) Hz,
     *  <li>Amplitude[N/2 + i] is the same as Amplitude[i], meaning the second
     *      half is a mirror image of the first half.
     *  </ul>
     *  
     *  @param signal Real-valued input signal, N points
     *  @see #getAmplitudes()
     *  @see #getFrequencies()
     */
    public Fourier(final double period, double[] signal)
    {
        this.period = period;
        N = signal.length;
        final double exp_coeff = 2.0 * Math.PI / N;
        
        // FFT(n) = 1/N sum{k} [ signal(k) exp(-2 pi j k n/N) ]
        nyquist_index = (int) Math.ceil(N/2);
        amplitude = new double[nyquist_index];
        for (int n = 0; n < nyquist_index; n++)
        {
            // Compute sum{k} [ signal(k) exp(-2 pi j k n/N) ]
            double real = 0.0;
            double imag = 0.0;
            for (int k = 0; k < N; k++)
            {
                real += signal[k] * Math.cos(exp_coeff * k * n);
                imag += signal[k] * Math.sin(exp_coeff * k * n);
            }
            // 1/N
            real /= N;
            imag /= N;
            amplitude[n] = Math.sqrt(real*real + imag*imag);
        }
    }
    
    /** @return Amplitude of FFT(signal) for 0 to Nyquist (N/2) */
    public double [] getAmplitudes()
    {
        return amplitude;
    }
    
    /** @return Frequency points in Hz; 0 to Nyquist */
    public double [] getFrequencies()
    {
        final double freq[] = new double[nyquist_index];
        // Data from nyquist_index to N is mirror image of 0..nyquist_index
        for (int i=0; i<nyquist_index; ++i)
            freq[i] = i / (2.0*period*nyquist_index);
        return freq;
    }
}
