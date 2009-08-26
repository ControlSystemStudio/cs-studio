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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.config.view.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.NodeImage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 17.06.2008
 */
public class IconManageView extends Composite {
//    public class IconManageView extends ScrolledComposite {

    private Composite _mainComposite;
    private List<NodeImage> _nodeImages;
    private NodeImage _selectedImage;

    /**
     * @param parent
     * @param style
     */
    public IconManageView(final Composite parent, int style, Node node) {
        super(parent, style);
        this.setLayoutData(GridDataFactory.fillDefaults().create());
        this.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        _mainComposite = this;
        Composite tableComposite = new Composite(_mainComposite, SWT.BORDER);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        GridDataFactory.fillDefaults().grab(false, true).span(1, 2).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);
        tableComposite.setBackground(new Color(null,125,125,0));

        final TableViewer iconOverview = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        iconOverview.getTable().setHeaderVisible(true);
        iconOverview.setContentProvider(new ArrayContentProvider());
        iconOverview.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(iconOverview.getTable());

        // Column Icon
        TableViewerColumn column = new TableViewerColumn(iconOverview, SWT.NONE);
        column.getColumn().setText("Icon");
        column.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                Image icon = new Image(parent.getDisplay(), new ByteArrayInputStream(((NodeImage)cell.getElement()).getImageBytes()));
                cell.setImage(icon);
            }
        });
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(2, 100, true));

        // Column File Name
        column = new TableViewerColumn(iconOverview, SWT.NONE);
        column.getColumn().setText("File Name");
        column.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                cell.setText(((NodeImage)cell.getElement()).getName());
            }
        });
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(4, 200, true));
//        _nodeImages = Repository.load(NodeImage.class);
        _nodeImages = new ArrayList<NodeImage>();
        iconOverview.setInput(_nodeImages);
        
        final Label iconSelectX = new Label(_mainComposite,SWT.BORDER|SWT.CENTER);
        GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, true, true, 1,1);
        layoutData.minimumHeight=100;
        layoutData.minimumWidth=100;
        iconSelectX.setLayoutData(layoutData);
        iconSelectX.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        iconSelectX.setText("testX");
        
        Button fileChooserButton = new Button(_mainComposite,SWT.PUSH);
        fileChooserButton.setText("Add Icon");

        fileChooserButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                fileChooserAction();
            }

            @Override
            public void widgetSelected(final SelectionEvent e) {
                fileChooserAction();
            }
            
            private void fileChooserAction() {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setFilterExtensions(new String[]{"*.jpg; *.jpeg; *.gif; *.png; *.bmp","*.*"});
                String iconFile = dialog.open();
                if(iconFile!=null){
                    IconManageView.this.layout(true,true);
                    ImageLoader im = new ImageLoader();
                    Image image = new Image(null, im.load(iconFile)[0]);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    im.save(baos, SWT.IMAGE_JPEG);
                    _selectedImage = new NodeImage();
                    _selectedImage.setImageBytes(baos.toByteArray());
                    _selectedImage.setName(iconFile);
                    _selectedImage.setFile(iconFile);
                    iconOverview.add(_selectedImage);
                    iconSelectX.setImage(image);
                    iconSelectX.pack();
                    IconManageView.this.pack(false);
                }
            }
        });

    }
    
    public NodeImage getSelectedImage() {
        return _selectedImage;
    }

}
