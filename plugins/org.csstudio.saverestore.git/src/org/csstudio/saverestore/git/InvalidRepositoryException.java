package org.csstudio.saverestore.git;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * <code>InvalidRepositoryException</code> is thrown when local git repository could not be instantiated.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class InvalidRepositoryException extends GitAPIException {

    private static final long serialVersionUID = 5234624866393451871L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param message the exception message
     */
    public InvalidRepositoryException(String message) {
        super(message);
    }


}
