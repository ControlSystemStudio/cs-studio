package org.csstudio.trends.databrowser.exportview;

// Just a placeholder for ideas
public interface TODO
{
    /** TODO Matlab export
     *  
     *  Simple way: Create a text file similar to the spreadsheet
     *  but easily readable my Matlab. Maybe a Matlab command file:
     *  
     *  data.name = 'SomePVName';
     *  data.values = [ 1 2 3 4 ];
     *  data.time = [ datenum('2008....') datenum(...) ... ];
     *  
     *  Better: Start a Matlab instance and send data there.
     *  How?
     *  Looks like Matlab can call Java code in pretty much any jar file.
     *  But is it possible to _start_ Matlab from within Java code (CSS)?
     *  
     *  Idea seen on the Web:
     *  Start Matlab as external process with some Matlab script that
     *  runs "CSSClient" Java code which does this:
     *  - Open socket, read & execute commands
     *  
     *  Then CSS can send data to that socket, tell CSSClient to quit,
     *  and data is in Matlab.
     *  To receive more data, user would have to start CSSClient again?
     *  Or is there a way to keep the CSSClient running in Matlab,
     *  while the user can still access the Matlab shell?
     *  Or would Matlab remain in "client" mode, and commands are send
     *  from a Matlab terminal inside CSS?
     */
}
