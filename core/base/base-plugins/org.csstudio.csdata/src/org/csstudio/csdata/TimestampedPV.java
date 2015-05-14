package org.csstudio.csdata;

/**
 * A Control system process variable with a timestamp.
 *
 * @author Kunal Shroff
 *
 */
public class TimestampedPV extends ProcessVariable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //TODO java8 replace this with an Instant
    private final long time;

    public TimestampedPV(String name, long time) {
        super(name);
        this.time = time;
    }

    public long getTime() {
        return time;
    }

}
