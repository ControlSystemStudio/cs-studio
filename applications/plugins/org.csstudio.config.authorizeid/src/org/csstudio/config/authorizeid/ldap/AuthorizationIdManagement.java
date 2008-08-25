package org.csstudio.config.authorizeid.ldap;

import javax.naming.NameAlreadyBoundException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;

/**
 * {@code AuthorizationIdManagement} manages AuthorizationId groups (eain). It can create and 
 * delete them.
 * @author Rok Povsic
 */
public class AuthorizationIdManagement {

	private static DirContext _directory;
	
	/**
	 * The logger that is used by this class.
	 */
	private static final CentralLogger LOG = CentralLogger.getInstance();
	
	static{
		_directory = Engine.getInstance().getLdapDirContext();
	}
	
	/**
	 * Inserts new directory to LDAP.
	 * @param _name name of the directory
	 * @param objectClass the object class
	 */
	public void insertNewData(String _name, String _group, ObjectClass1 objectClass) {
		String _fullname = fullName(_name, _group);
		Attributes attrs = attributesForEntry(objectClass, _name);
		try{
			_directory.bind(_fullname, null, attrs);
		} catch(NameAlreadyBoundException f) {
			LOG.error(AuthorizationIdManagement.class, "An eain with name \""+ _name +"\" already exists" +
					" in group \""+ _group +"\".");
		} catch(Exception f){
			f.printStackTrace();
		}
	}
	
	/**
	 * Gets attributes that are committed when creating new directory.
	 * @param objectClass the object class
	 * @param name name of the directory
	 * @return the result (attributes)
	 */
	private static Attributes attributesForEntry(final ObjectClass1 objectClass, final String name) {
		BasicAttributes result = new BasicAttributes();
		result.put("objectClass", objectClass.getObjectClassName());
		result.put(objectClass.getRdnAttribute(), name);
		return result;
	}
	
	/**
	 * Deletes AuthorizeId from LDAP.
	 * @param _name the name of AuthorizeId
	 * @param _group the group AuthorizeId is in
	 */
	public void deleteData(String _name, String _group) {
		String _fullname = fullName(_name, _group);
		try{
			_directory.unbind(_fullname);
		} catch(Exception f) {
			f.printStackTrace();
		}
	}
	
	/**
	 * Returns full name of given name and group.
	 * @param _name the name of AuthorizeId
	 * @param _group the group AuthorizeId is in
	 * @return full name
	 */
	private String fullName(String _name, String _group) {
		String _fullname = "eain="+ _name +",ou="+ _group +",ou=EpicsAuthorizeID";
		return _fullname;
	}
}
