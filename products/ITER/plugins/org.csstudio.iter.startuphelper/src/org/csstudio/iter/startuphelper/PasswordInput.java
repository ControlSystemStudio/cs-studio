package org.csstudio.iter.startuphelper;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/** Read password from command line with masked input
 *
 *  <p>Uses Java 6 console, if available.
 *  Falls back to 'EraserThread' idea from
 *  http://java.sun.com/developer/technicalArticles/Security/pwordmask/
 *
 *  @author Xihui Chen
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PasswordInput
{
    /** Thread to display '*', overwriting any entered characters.
     *  Does not work well within Eclipse IDE's Console view,
     *  but is fine for shell window
     */
    static class EraserThread implements Runnable
    {
        private boolean stop;

        /** @param Prompt displayed to the user
         */
        public EraserThread(final String prompt)
        {
            System.out.print(prompt);
        }

        /**
         * Begin masking...display asterisks (*)
         */
        @Override
        public void run()
        {
            stop = true;
            while (stop)
            {
                System.out.print("\010*");
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException ie)
                {
                    ie.printStackTrace();
                }
            }
        }

        /**
         * Instruct the thread to stop masking
         */
        public void stopMasking()
        {
            this.stop = false;
        }
    }

    /**
     * @param prompt
     *            The prompt to display to the user
     * @return The password as entered by the user
     */
    public static String readPassword(final String prompt)
    {
        final Console console = System.console();
        if (console != null)
            return new String(console.readPassword(prompt));

        // Fall back to EraserThread
        final EraserThread et = new EraserThread(prompt);
        final Thread mask = new Thread(et);
        mask.start();

        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String password = "";

        try
        {
            password = in.readLine();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        // stop masking
        et.stopMasking();
        System.out.print("\010");

        // return the password entered by the user
        return password;
    }
}