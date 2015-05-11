package org.csstudio.utility.speech;

public class NoSoundCardAvailableException extends Exception {

    private static final long serialVersionUID = 6255602883792510511L;

    public NoSoundCardAvailableException() {
    }

    public NoSoundCardAvailableException(String arg0) {
        super(arg0);
    }

    public NoSoundCardAvailableException(Throwable arg0) {
        super(arg0);
    }

    public NoSoundCardAvailableException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
