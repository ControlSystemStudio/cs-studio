XMPP Server Setup

Unpack openfire, start it.
"Launch Admin" to get web interface.

Configure this:

Domain - Set to either "localhost" for local tests,
or set to complete host name.

Database - Embedded Database works well for a while.

Administrator account -
Use for examlpe "admin@localhost",
and a password.

The last 'continue' takes a little longer.

To then log in, use just "admin" and the password,
NOT "admin@localhost".


Create a "css" group chat, in the end called something
like css@conference.localhost


To re-set and get back to the original admin setup,
for example after forgetting the admin password:

Stop openfire,
in conf/openfile.xml, set <setup>true</setup> to false,
start openfile,
open the admin
