/*
 * Copyright © 2008, Brian Joyce
 * By Brian Joyce, Duolog Technologies Ltd., Galway, Ireland
 * June 13, 2008
 *
 * http://www.eclipse.org/articles/Article-PDEJUnitAntAutomation/index.html#PDETestListener
 */
package org.csstudio.testsuite.pde;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

/**
 * @author Brian Joyce
 * @author bknerr
 * @since 16.06.2011
 */
public class PDETestPortLocator {
  //CHECKSTYLE OFF: |
    @SuppressWarnings("all")
    public static void main(final String[] args) {
        new PDETestPortLocator().savePortToFile();
    }

    public void savePortToFile() {
        final int port = locatePDETestPortNumber();
        final File propsFile = new File("pde_test_port.properties");
        System.out.println("PDE Test port: " + port);
        OutputStream os = null;
        try {
            os = new FileOutputStream(propsFile);
            os.write(("pde.test.port=" + String.valueOf(port)).getBytes());
            os.flush();
            System.out.println("PDE Test port saved to file " + propsFile.getAbsolutePath());
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (final IOException ioe) {
                    // ignore
                }
            }
            os = null;
        }
    }

    private int locatePDETestPortNumber() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (final IOException e) {
            // ignore
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (final IOException e) {
                    // ignore
                }
            }
        }
        return -1;
    }
}
//CHECKSTYLE ON: |
