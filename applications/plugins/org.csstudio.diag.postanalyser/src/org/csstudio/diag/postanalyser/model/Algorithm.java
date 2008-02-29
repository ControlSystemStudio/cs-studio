package org.csstudio.diag.postanalyser.model;

import org.csstudio.diag.postanalyser.Messages;

/** Base class for all Algorithms.
 *  <p>
 *  An Algorithm has a named,
 *  it take input which might vary for derived types,
 *  and produces outputs which should get displayed.
 *  @author Kay Kasemir
 */ 
abstract public class Algorithm
{
    /** Name of the Algorithm */
    final private String name;
    
    /** Channel with input data */
    protected Channel input;

    /** Message that describes the result of the last <code>process()</code> run. */
    protected String message;

    /** X-Axis label from the last <code>process()</code> run. */
    protected String x_axis_label;

    /** Y-Axis label from the last <code>process()</code> run. */
    protected String y_axis_label;
    
    /** One or more outputs from the last <code>process()</code> run. */
    protected AlgorithmOutput[] outputs;
    
    /** Construct new Algorithm */
    public Algorithm(final String name)
    {
        this.name = name;
        x_axis_label = Messages.Algorithm_TimeAxisLabel;
        reset();
    }

    /** @return Algorithm's name */
    public String getName()
    {
        return name;
    }

    /** Reset whatever needs resetting.
     *  <p>
     *  Default clears the input.
     */
    public void reset()
    {
        input = null;
        message = "<uninitialized>"; //$NON-NLS-1$
        outputs = null;
    }

    /** Set the channel that provides input data.
     *  <p>
     *  Derived  Algorithms might require more than one input...
     */
    public void setInput(final Channel input)
    {
        this.input = input;
        this.y_axis_label = input.getName();
    }

    /** Process the input data, producing output.
     *  @throws Exception on error, for example missing input data.
     */
    abstract public void process() throws Exception;

    /** @return Message that describes the result of the last <code>process()</code> run.
     */
    public String getMessage()
    {
        return message;
    }

    /** @return <code>true</code> if this algorithm uses a "time" axis instead
     *          of a generic "x" axis
     */
    public boolean needTimeAxis()
    {
        return true;
    }

    /** @return X-Axis label from the last <code>process()</code> run. */
    public String getXAxisLabel()
    {
        return x_axis_label;
    }

    /** @return Y-Axis label from the last <code>process()</code> run. */
    public String getYAxisLabel()
    {
        return y_axis_label;
    }

    /** @return One or more outputs from the last <code>process()</code> run. */
    public AlgorithmOutput[] getOutputs() throws Exception
    {
        return outputs;
    }
}
