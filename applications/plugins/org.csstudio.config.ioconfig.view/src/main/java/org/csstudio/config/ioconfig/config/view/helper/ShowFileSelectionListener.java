/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.config.ioconfig.config.view.helper;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.tools.Helper;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 19.05.2010
 */
public class ShowFileSelectionListener implements SelectionListener {
    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 20.05.2010
     */
    private final class GSDFileEditDialog extends Dialog {
        private final Color GREEN = new Color(null, 0, 100, 0);
        private final Color WHITE = new Color(null, 255, 255, 255);
        private final GSDFile _gsdFile;
        private StyledText _text;

        /**
         * Constructor.
         * @param parentShell
         */
        private GSDFileEditDialog(final Shell parentShell, @Nonnull final GSDFile gsdFile) {
            super(parentShell);
            _gsdFile = gsdFile;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell newShell) {
            super.configureShell(newShell);
            newShell.setText(_gsdFile.getName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control createDialogArea(final Composite parent) {
            Control createDialogArea = super.createDialogArea(parent);
            _text = new StyledText(parent, SWT.MULTI | SWT.LEAD | SWT.BORDER | SWT.H_SCROLL
                    | SWT.V_SCROLL);
            _text.setEditable(false);
            GridData create = GridDataFactory.fillDefaults().hint(800, 600).create();
            _text.setLayoutData(create);
            String gsdFile = _gsdFile.getGSDFile();
            _text.setText(gsdFile);
            StyleRange[] ranges = makeStyleRanges(gsdFile);
            _text.setStyleRanges(ranges);
            _text.pack();
            createDialogArea.pack();
            return createDialogArea;
        }

        /**
         * @param gsdFile
         * @return
         */
        private StyleRange[] makeStyleRanges(final String gsdFile) {
            List<StyleRange> styleRangeList = new ArrayList<StyleRange>();
            int start = 0;
            do {
                int indexOf = gsdFile.indexOf(';', start);
                start = indexOf;
                if (indexOf >= 0) {
                    int end = gsdFile.indexOf('\n', indexOf);
                    styleRangeList.add(new StyleRange(start, end - start, GREEN, WHITE));
                    start = end;
                }
            } while (start >= 0);
            return styleRangeList.toArray(new StyleRange[0]);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void createButtonsForButtonBar(final Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        }

    }

    private final TableViewer _parentViewer;

    public ShowFileSelectionListener(final TableViewer parentViewer) {
        _parentViewer = parentViewer;
    }

    public void widgetSelected(final SelectionEvent e) {
        openFileInBrowser();
    }

    public void widgetDefaultSelected(final SelectionEvent e) {
        openFileInBrowser();
    }

    private void openFileInBrowser() {
        final StructuredSelection selection = (StructuredSelection) _parentViewer.getSelection();
        Object object = selection.getFirstElement();
        File createTempFile = null;
        if (object instanceof GSDFile) {

            final GSDFile firstElement = (GSDFile) object;
            GSDFileEditDialog gsdEditDialog = new GSDFileEditDialog(Display.getDefault()
                    .getActiveShell(), firstElement);
            gsdEditDialog.open();
        } else if (object instanceof Document) {
            final Document firstElement = (Document) object;
            try {
                String filename = firstElement.getSubject();
                if ( (filename == null) || (filename.length() < 1)) {
                    filename = "showTmp";
                }
                createTempFile = File.createTempFile(filename, "." + firstElement.getMimeType());
                Helper.writeDocumentFile(createTempFile, firstElement);
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        if ((createTempFile != null) && createTempFile.isFile()) {
            if (Desktop.isDesktopSupported()) {
                if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    try {
                        Desktop.getDesktop().open(createTempFile);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
