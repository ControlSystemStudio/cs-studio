package org.csstudio.logbook.midas;

import org.csstudio.logbook.ILogbook;

@SuppressWarnings("nls")
public class MidasLogbook implements ILogbook {

    final private String logbook;
    final private String user;
    final private String password;

    public MidasLogbook(final String logbook, final String user, final String password)
    {
        this.logbook = logbook;
        this.user = user;
        this.password = password;
    }

    @Override
    public void createEntry(final String title, final String text, final String... file_names)
            throws Exception
    {
        try
        {
            String cmd = "elog -h " +
                    LogbookMidasPreferences.getHOST() + " -p " +
                    LogbookMidasPreferences.getPORT() + " -l " + logbook + " -s" +
                    " -u " + user + " " + password +
                    " -a Author=" + user + " -a Type=Diary -a Category=SlowControl" +
                    " -a Subject=\"" + title + "\" -n 1";

            for (String file : file_names)
                cmd += " -f " + file;

            cmd += " \"" + text + "\"";
            //System.out.println( cmd );
            Runtime.getRuntime().exec( cmd );
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void createEntry(final String title, final String text, final String[] filenames,
            final String[] captions) throws Exception
    {
        createEntry(title, text, filenames);
    }

    @Override
    public void close()
    {
        // NOP
    }
}
