package org.csstudio.utility.pv.simu;

/** Dynamic value that produces noise waveform
 *  @author Xihui Chen
 */
public class NoiseWaveformValue extends DynamicValue
{
    /** Initialize
     *  @param name
     */
    public NoiseWaveformValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {	int arraySize = (int) step;
    	double[] doubleArray = new double[arraySize];
    	for(int i=0; i<arraySize; i++){
    		doubleArray[i] = min + (Math.random() * (max - min));
    	}
        setValue(doubleArray);
    }
}
