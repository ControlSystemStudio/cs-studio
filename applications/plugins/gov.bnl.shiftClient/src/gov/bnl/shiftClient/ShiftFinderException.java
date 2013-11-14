package gov.bnl.shiftClient;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 *@author: eschuhmacher
 */
public class ShiftFinderException extends RuntimeException {

	private static final long serialVersionUID = 6279865221993808192L;

    private Status status;

    public ShiftFinderException() {
        super();
    }

    public ShiftFinderException(UniformInterfaceException cause) {
        super(parseErrorMsg(cause), cause);
        this.setStatus(Status.fromStatusCode(cause.getResponse().getStatus()));
    }

    private static String parseErrorMsg(UniformInterfaceException ex) {
        String entity = ex.getResponse().getEntity(String.class);
        try {
            ClientResponseParser callback = new ClientResponseParser();
            Reader reader = new StringReader(entity);
            new ParserDelegator().parse(reader, callback, false);
            return callback.getMessage();
        } catch (IOException e) {
            //e.printStackTrace();
            return "Could not retrieve message from server";
        }
    }

    public ShiftFinderException(Status status, String message) {
        super(message);
        this.setStatus(status);
    }

    /**
     *
     * @param status - the http error status code
     * @param cause - the original UniformInterfaceException 
     * @param message - additional error information
     */
    public ShiftFinderException(Status status, Throwable cause ,String message) {
        super(message, cause);
        this.setStatus(status);
    }

    /**
     *
     */
    public ShiftFinderException(FileNotFoundException ex){
        super(ex.getMessage(), ex.getCause());
        this.setStatus(null);
    }

    /**
     *
     */
    public ShiftFinderException(IOException ex){
        super(ex.getMessage(), ex.getCause());
        this.setStatus(null);
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }


}
