/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.namespacebrowser.ui;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;


import org.csstudio.apputil.ui.dialog.ErrorDetailDialog;
import org.csstudio.utility.ldap.namespacebrowser.Activator;
import org.csstudio.utility.ldap.namespacebrowser.utility.LDAP2Automat;
import org.csstudio.utility.ldap.namespacebrowser.utility.LdapNameSpace;
import org.csstudio.utility.nameSpaceBrowser.ui.CSSView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**********************************************************************************
 *
 * @author Helge Rickens
 *
 * Es wird das ergebnis aus einer LDAP-Anfrage, in einem Listeelemt dargestellt.
 * Das Ergebnis kann durch eine eingabe in das darüberliegende Feld gefiltertert
 * werden. Wird ein Element selektiert wird eine neu LDAP anfrage nach den
 * Kindelmenten gestartet die wiederum in einer Liste (CSSView) dargestellt werden.
 * Die Strucktur die dazu verwendetet wird ist von der Klasse LDAPAutomat abhängig.
 *
 **********************************************************************************/

public class MainView extends ViewPart {
    public static final String ID = MainView.class.getName();
    private static String _DEFAULT_PV_FILTER = ""; //$NON-NLS-1$
    private CSSView cssview;
    private LDAP2Automat _automat;

    @Override
    public void createPartControl(final Composite parent) {
        _automat = new LDAP2Automat();
        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL);
        final Composite c = new Composite(sc, SWT.NONE);
        sc.setContent(c);
        sc.setExpandVertical(true);
        c.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
        c.setLayout(new GridLayout(1, false));
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(parent.getShell(), Activator.PLUGIN_ID + ".nsB");

        c.getShell().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if(e.keyCode == SWT.F1) {
                    PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
                }
            }
        });
        final String[] headlines = {Messages.getString("CSSView_Facility"),
                Messages.getString("CSSView_Controller"), Messages.getString("CSSView_Record")};

        try {
            cssview = new CSSView(c,
                                  _automat,
                                  new LdapNameSpace(),
                                  getSite(),
                                  _DEFAULT_PV_FILTER,
                                  UNIT.getNodeTypeName() + FIELD_ASSIGNMENT
                                          + UNIT.getUnitTypeValue(),
                                  headlines,
                                  0);
        } catch (Exception e) {
            ErrorDetailDialog errorDetailDialog = new ErrorDetailDialog(null,
                                                                        "Titel",
                                                                        e.getLocalizedMessage(),
                                                                        e.toString());
            errorDetailDialog.open();
        }

    }

    @Override
    public void setFocus() {
        // EMPTY
    }

    public void setDefaultPVFilter(final String defaultFilter) {
        _DEFAULT_PV_FILTER = defaultFilter;
        cssview.setDefaultFilter(_DEFAULT_PV_FILTER);
    }

    @Override
    public void dispose() {
        _automat = null;
    }
}
