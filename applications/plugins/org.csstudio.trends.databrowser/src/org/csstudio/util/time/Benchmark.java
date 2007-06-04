package org.csstudio.util.time;

/** Naive banechmark timer.
 *  @author Kay Kasemir
 */ 
@SuppressWarnings("nls")
public class Benchmark
{
    final private int runs;
    private long t0, t1;
    
    /** Create benchmark timer.
     *  @param runs Number of runs that will be performed between start/end.
     */
    public Benchmark(int runs)
    {
        this.runs = runs;
    }
    
    /** Start the timer */
    public void start()
    {
        t0 = System.nanoTime();
    }

    /** End the timer.
     *  @return Runtime in seconds per individual run
     */
    public double end()
    {
        t1 = System.nanoTime();
        return seconds();
    }
    
    /** @return Runtime in seconds per individual run */
    public double seconds()
    {
        return (t1 - t0)*1e-9/runs;
    }
    
    @Override
    public String toString()
    {
        return String.format("%f seconds per run", seconds());
    }
}
