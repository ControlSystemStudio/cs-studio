package org.csstudio.swt.xygraph.linearscale;

/**
 * A range.
 * @author Xihui Chen
 *
 */
public class Range {

    /** the lower value of range */
    private double lower;

    /** the upper value of range */
    private double upper;

    /**
     * Constructor.
     * 
     * @param start
     *            the start value of range
     * @param end
     *            the end value of range
     */
    public Range(double start, double end) {
    	//if(end == start)
    	//	end = start + 1;
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

    
    /**If a value in the range or not.
     * @param value
     * @param includeBoundary true if the boundary should be considered.
     * @return true if the value is in the range. Otherwise false.
     */
    public boolean inRange(double value, boolean includeBoundary){
    	if(includeBoundary)
    		return (value >= lower && value <= upper);
    	else
    		return (value > lower && value < upper);
    }
    
    /**If a value in the range or not. The boundary is included.
     * @param value
     * @return true if the value is in the range. Otherwise false.
     */
    public boolean inRange(double value){
    	return value >= lower && value <= upper;
    }

	/**
	 * @return the lower
	 */
	public double getLower() {
		return lower;
	}

	/**
	 * @return the upper
	 */
	public double getUpper() {
		return upper;
	}

	/** {@inheritDoc} */
	@Override
    public boolean equals(final Object obj)
    {   // See "Effective Java" Item 7
	    if (this == obj)
	        return true;
	    if (! (obj instanceof Range))
	        return false;
	    final Range other = (Range) obj;
	    return other.lower == lower  &&  other.upper == upper;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        // "Effective Java" Item 8: When overriding equals(), also implement hashCode
        int result = (int) Double.doubleToLongBits(lower);
        result = 37*result + (int) Double.doubleToLongBits(upper);
        return result;
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "lower=" + lower + ", upper=" + upper;
    }
}
