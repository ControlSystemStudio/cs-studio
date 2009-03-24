package org.csstudio.sds.components.ui.internal.figureparts;

/**
 * A range.
 */
public class Range {

    /** the lower value of range */
    public double lower;

    /** the upper value of range */
    public double upper;

    /**
     * Constructor.
     * 
     * @param start
     *            the start value of range
     * @param end
     *            the end value of range
     */
    public Range(double start, double end) {
    	if(end == start)
    		end = start + 1;
        this.lower = (end > start) ? start : end;
        this.upper = (end > start) ? end : start;
    }

    /**
     * Constructor.
     * 
     * @param range
     *            the range
     */
    public Range(Range range) {
        lower = (range.upper > range.lower) ? lower : upper;
        upper = (range.upper > range.lower) ? upper : lower;
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "lower=" + lower + ", upper=" + upper;
    }
}
