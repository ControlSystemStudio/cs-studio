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
package org.csstudio.config.ioconfig.editorparts;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

/**
 * Editor for {@link IocDBO} node's
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class IocEditor extends AbstractNodeEditor {

    public static final String ID = "org.csstudio.config.ioconfig.view.editor.ioc";

    /**
     * The IOC Object.
     */
    private IocDBO _ioc;

    /**
     * Constructor.
     */
    public IocEditor() {
        // nothing to do.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        super.createPartControl(parent);
        _ioc = (IocDBO) getNode();
        if (_ioc == null) {
            getNode();
            newNode();
        }
        main("IOC");
        selecttTabFolder(0);    
    }
    

    /**
     * Generate the Main IOC configuration Tab.
     *
     * @param head
     *            The headline of the tab.
     */
    private void main(@Nonnull final String head) {
		TabFolder tabFolder = getTabFolder();
		if (tabFolder != null) {
			Composite comp = ConfigHelper.getNewTabItem(head, tabFolder,
					5, 300, 260);
			comp.setLayout(new GridLayout(4, false));

			Group gName = new Group(comp, SWT.NONE);
			gName.setText("Name");
			gName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
					5, 1));
			gName.setLayout(new GridLayout(3, false));

			Text nameText = new Text(gName, SWT.BORDER | SWT.SINGLE);
			setText(nameText, _ioc.getName(), 255);
			nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					false, 1, 1));
			setNameWidget(nameText);

			Spinner indexSpinner = ConfigHelper.getIndexSpinner(gName, _ioc,
					getMLSB(), "Index", getProfiBusTreeView());
			setIndexSpinner(indexSpinner);
			indexSpinner.setMaximum(
					_ioc.getParent().getChildren().size() - 1);

			makeDescGroup(comp, 3);
		}
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.csstudio.config.ioconfig.config.view.NodeConfig#fill(org.csstudio
     * .config.ioconfig.model .pbmodel.GSDFile)
     */
    @Override
    public boolean fill(@Nullable final GSDFileDBO gsdFile) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.config.ioconfig.config.view.NodeConfig#getGSDFile()
     */
    @Override
    public GSDFileDBO getGsdFile() {
        return null;
    }

    @Override
    public void cancel() {
        super.cancel();
        if (_ioc != null) {
            Text text = getHeaderField(HeaderFields.VERSION);
            if (text != null) {
                    text.setText("");
            }
            Spinner indexSpinner = getIndexSpinner();
            if(indexSpinner!=null) {
            	indexSpinner.setSelection((Short) indexSpinner.getData());
            }
            Text nameWidget = getNameWidget();
			if(nameWidget!=null) {
            	nameWidget.setText((String) nameWidget.getData());
            }
        }
        DocumentationManageView dMV = getDocumentationManageView();
        if (dMV != null) {
            dMV.cancel();
        }
        setSaveButtonSaved();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        // Main
        Text nameWidget = getNameWidget();
		if (nameWidget != null) {
			_ioc.setName(nameWidget.getText());
			nameWidget.setData(nameWidget.getText());
		}

        Spinner indexSpinner = getIndexSpinner();
        if(indexSpinner!=null) {
        	indexSpinner.setData(_ioc.getSortIndex());
        }

        // Document
        DocumentationManageView documentationManageView = getDocumentationManageView();
		if(documentationManageView!=null) {
        	Set<DocumentDBO> docs = documentationManageView.getDocuments();
        	_ioc.setDocuments(docs);
        }

        save();
    }
}
