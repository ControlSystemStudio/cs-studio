package org.csstudio.dal;

/**
 *
 * <code>AccessType</code> defines the type of access for a particular property.
 * The property can be read only, read write or there is no defined access.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public enum AccessType {
    READ,READ_WRITE,WRITE,NONE;

    /**
     * Returns the access type for the given read and write access.
     *
     * @param read true if read access is allowed
     * @param write true if write access is allowed
     * @return one of the four access type depending on the conditions met
     */
    public static AccessType getAccess(boolean read, boolean write) {
        if (write && read) return READ_WRITE;
        else if (write) return WRITE;
        else if (read) return READ;
        else return NONE;
    }
}
