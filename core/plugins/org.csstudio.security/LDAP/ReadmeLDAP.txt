LDAP Setup for Testing
======================

On Linux server, install at least openldap-servers and openldap-clients (example package names on RHEL).

By default, there may not be a /etc/openldap/slapd.conf and instead it's using /etc/openldap/slap.d, maybe leaving an example old-style config in /usr/share/openldap-servers/slapd.conf.obsolete.
The new slap.d mechanism is prefered for operational setups, but for tests the older setup with an slapd.conf as included in here, deleting the slap.d directory, may be easier.


Start Server:
/etc/rc.d/init.d/slapd start


Check if server is running:
ldapsearch -x -b '' -s base '(objectclass=*)' namingContexts


Add entries:
# Only needed once
ldapadd -x -D "cn=Manager,dc=css-demo,dc=org" -W -f base.ldif

# Add example users
ldapadd -x -D "cn=Manager,dc=css-demo,dc=org" -W -f users.ldif


Display entries:
# For remote access add -h hostname
ldapsearch -x -b 'dc=css-demo,dc=org'  '(objectclass=*)'
ldapsearch -x -b 'ou=Users,dc=css-demo,dc=org'  '(objectclass=account)'
ldapsearch -x -b 'ou=Groups,dc=css-demo,dc=org'  '(memberUid=fred)'


Check password of user 'fred':
ldapsearch -h ky9linux.ornl.gov -x -b 'ou=Users,dc=css-demo,dc=org' -D 'uid=fred,ou=Users,dc=css-demo,dc=org' -W '(uid=fred)'
# At prompt, enter password. On success, will see full info for user.


Delete entries:
ldapdelete -x -D "cn=Manager,dc=css-demo,dc=org" -W 'uid=fred,ou=Users,dc=css-demo,dc=org'

