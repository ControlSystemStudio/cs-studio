
/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.utility.screenshot.dialog;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import javax.swing.JButton;
//import javax.swing.JDialog;
//import javax.swing.JLabel;
//import javax.swing.JTextField;
//import javax.swing.plaf.basic.BasicBorders;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Text;

public class AboutDialog extends Dialog implements ActionListener
{
    private ApplicationWindow   _window     = null;
    private CLabel              textName    = null;
    //private Button              buttonDone  = null;
    //private Label               labelInfo1  = null;
    //private Label               labelInfo2  = null;
    //private Label               labelInfo3  = null;
    //private Label               labelInfo4  = null;

    private final           int     INIT_WIDTH          = DialogUnit.mapUnitX(215);
    private final           int     INIT_HEIGHT         = DialogUnit.mapUnitY(149);

    public AboutDialog(ApplicationWindow c)
    {
        this(c.getShell());

        _window = c;
    }

    public AboutDialog(Shell s)
    {
        super(s);
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Rectangle   rect    = null;

        this.
        /*this.setResizable(false);
        this.setModal(true);
        this.setLayout(null);
        */

        textName = new CLabel(this.getShell(), SWT.SHADOW_IN | SWT.CENTER);
        textName.setText(_window.getShell().getText().toUpperCase());

        rect = new Rectangle(45, 24, 115, 16);
        textName.setBounds(DialogUnit.mapUnits(rect));
        rect = null;

        /*
        buttonDone = new JButton("OK");
        rect.setBounds(88, 128, 50, 14);
        buttonDone.setBounds(DialogUnit.mapUnits(rect));
        buttonDone.setActionCommand("BUTTON_OK");
        buttonDone.addActionListener(this);

        labelInfo1 = new JLabel("Designed and Written by:");
        labelInfo1.setHorizontalAlignment(JLabel.CENTER);
        rect.setBounds(7, 50, 201, 12);
        labelInfo1.setBounds(DialogUnit.mapUnits(rect));

        labelInfo2 = new JLabel("Markus M�ller, MKS-2");
        labelInfo2.setHorizontalAlignment(JLabel.CENTER);
        rect.setBounds(7, 62, 201, 12);
        labelInfo2.setBounds(DialogUnit.mapUnits(rect));

        labelInfo3 = new JLabel("Send questions and bug reports to:");
        labelInfo3.setHorizontalAlignment(JLabel.CENTER);
        rect.setBounds(7, 83, 201, 12);
        labelInfo3.setBounds(DialogUnit.mapUnits(rect));

        labelInfo4 = new JLabel("Markus.Moeller@desy.de");
        labelInfo4.setHorizontalAlignment(JLabel.CENTER);
        rect.setBounds(7, 96, 201, 12);
        labelInfo4.setBounds(DialogUnit.mapUnits(rect));

        this.add(textName);
        this.add(labelInfo1);
        this.add(labelInfo2);
        this.add(labelInfo3);
        this.add(labelInfo4);
        this.add(buttonDone);

        this.setVisible(true);
        */
        return parent;
    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);

        shell.setLayout(null);
        shell.setText(_window.getShell().getText() + " - About");
    }

    @Override
    protected void initializeBounds()
    {
        Point       point   = null;

        point = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        point.setLocation(point.x - (INIT_WIDTH / 2), point.y - (INIT_HEIGHT / 2));

        this.getShell().setBounds(point.x, point.y, INIT_WIDTH, INIT_HEIGHT);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if(actionEvent.getActionCommand().compareTo("BUTTON_OK") == 0)
        {
            //this.setVisible(false);
            //this.removeAll();
            //this.dispose();
            try
            {
                this.finalize();
            }
            catch(Throwable te)
            {
                te.printStackTrace();
            }
        }
    }

/*
    ABOUTDLG DIALOG  0, 0, 215, 149
    STYLE DS_SETFONT | DS_MODALFRAME | DS_CENTER | WS_POPUP | WS_CAPTION |
        WS_SYSMENU
    CAPTION "About me"
    FONT 8, "MS Sans Serif"
    BEGIN
        DEFPUSHBUTTON   "OK",IDC_ABOUTDLG_OK,88,128,50,14
        CTEXT           "Designed and Written by: ",IDC_STATIC,7,50,201,12,
                        SS_CENTERIMAGE
        CTEXT           "Markus M�ller, MKS-2",IDC_STATIC,7,62,201,12,
                        SS_CENTERIMAGE
        CTEXT           "Send questions and bug reports to:",IDC_STATIC,7,83,201,12,
                        SS_CENTERIMAGE
        CTEXT           "Markus.Moeller@desy.de",IDC_STATIC,7,96,201,12,
                        SS_CENTERIMAGE
        CONTROL         124,IDC_STATIC,"Static",SS_BITMAP,7,7,43,39
        CTEXT           "CAPONE",IDC_ABOUTDLG_TITLE,65,24,85,16,SS_CENTERIMAGE |
                        SS_SUNKEN | WS_BORDER
    END
*/
}
