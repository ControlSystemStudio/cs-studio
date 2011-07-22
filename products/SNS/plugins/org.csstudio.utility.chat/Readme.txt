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



When used with Pidgin as another test client:
Set account within pidgin to
Protocol: XMPP
Username, password: Something that's been added in openfire,
       or select "Create this new account on server" to create
Domain: name of server, like "localhost"
Domain: smack as default works OK

Under "Advanced":
Connect port: 5222
File Transfer proxies: server:7777, like "localhost:7777"
