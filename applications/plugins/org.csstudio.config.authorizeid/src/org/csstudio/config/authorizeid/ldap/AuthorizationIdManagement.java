package org.csstudio.config.authorizeid.ldap;

import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;

import org.csstudio.config.authorizeid.AuthorizeIdActivator;
import org.csstudio.utility.ldap.service.ILdapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code AuthorizationIdManagement} manages AuthorizationId groups (eain). It can create and
 * delete them.
 * @author Rok Povsic
 */
public class AuthorizationIdManagement {

    
	/**
	 * The logger that is used by this class.
	 */
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationIdManagement.class);

	/**
	 * Inserts new directory to LDAP.
	 * @param _name name of the directory
	 * @param objectClass the object class
	 *
	 * @throws ServiceUnavailableException
	 * @throws InvalidNameException
	 */
	public void insertNewData(final String _name, final String _group, final ObjectClass1 objectClass) throws ServiceUnavailableException, InvalidNameException {
		final String _fullname = fullName(_name, _group);
		final Attributes attrs = attributesForEntry(objectClass, _name);
		final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
		if (service == null) {
		    throw new ServiceUnavailableException("LDAP service not available. Insertion of new data failed.");
		}
		if (!service.createComponent(new LdapName(_fullname), attrs)) {
		    LOG.error("An eain with name \""+ _name +"\" already exists" +
		              " in group \""+ _group +"\".");
		}
	}

	/**
	 * Gets attributes that are committed when creating new directory.
	 * @param objectClass the object class
	 * @param name name of the directory
	 * @return the result (attributes)
	 */
	private static Attributes attributesForEntry(final ObjectClass1 objectClass, final String name) {
		final BasicAttributes result = new BasicAttributes();
		result.put("objectClass", objectClass.getObjectClassName());
		result.put(objectClass.getRdnAttribute(), name);
		return result;
	}

	/**
	 * Deletes AuthorizeId from LDAP.
	 * @param _name the name of AuthorizeId
	 * @param _group the group AuthorizeId is in
	 * @throws InvalidNameException
	 * @throws ServiceUnavailableException
	 */
	public void deleteData(final String _name, final String _group) throws InvalidNameException, ServiceUnavailableException {
		final String _fullname = fullName(_name, _group);
		    final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
		    if (service == null) {
		        throw new ServiceUnavailableException("LDAP service not available. Deletion of data failed.");
		    }
		    service.removeLeafComponent(new LdapName(_fullname));
	}

	/**
	 * Returns full name of given name and group.
	 * @param _name the name of AuthorizeId
	 * @param _group the group AuthorizeId is in
	 * @return full name
	 */
	private String fullName(final String _name, final String _group) {
		final String _fullname = "eain="+ _name +",ou="+ _group +",ou=EpicsAuthorizeID";
		return _fullname;
	}
}
