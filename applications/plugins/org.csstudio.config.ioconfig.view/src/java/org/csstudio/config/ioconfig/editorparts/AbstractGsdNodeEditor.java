/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.AbstractGsdPropertyModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.KeyValuePair;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmText;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmTextItem;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 20.04.2011
 */
public abstract class AbstractGsdNodeEditor extends AbstractNodeEditor {
    
    /**
     * TODO (hrickens) :
     * 
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class ExtUserPrmDataContentProvider implements IStructuredContentProvider {
        /**
         * Constructor.
         */
        public ExtUserPrmDataContentProvider() {
            // Constructor.
        }
        
        @Override
        public void dispose() {
            // nothing to dispose
        }
        
        @Override
        @CheckForNull
        public Object[] getElements(@Nullable final Object inputElement) {
            if (inputElement instanceof ExtUserPrmData) {
                ExtUserPrmData eUPD = (ExtUserPrmData) inputElement;
                PrmText prmText = eUPD.getPrmText();
                if (prmText == null) {
                    PrmTextItem[] prmTextArray = new PrmTextItem[eUPD.getMaxValue()
                            - eUPD.getMinValue() + 1];
                    for (int i = eUPD.getMinValue(); i <= eUPD.getMaxValue(); i++) {
                        prmTextArray[i] = new PrmTextItem(Integer.toString(i), i);
                    }
                    return prmTextArray;
                }
                return prmText.getPrmTextItems().toArray();
            }
            return null;
        }
        
        @Override
        public void inputChanged(@Nullable final Viewer viewer,
                                 @Nullable final Object oldInput,
                                 @Nullable final Object newInput) {
            // nothing to do.
        }
    }
    
    /**
     * {@link PrmTextItem} Label provider that mark the default selection with a
     * '*'. The {@link ExtUserPrmData} give the default.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 18.10.2010
     */
    private final class PrmTextComboLabelProvider extends LabelProvider {
        
        private final ExtUserPrmData _extUserPrmData;
        
        /**
         * Constructor.
         * 
         * @param extUserPrmData
         */
        public PrmTextComboLabelProvider(@Nonnull ExtUserPrmData extUserPrmData) {
            _extUserPrmData = extUserPrmData;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getText(@Nullable Object element) {
            if (element instanceof PrmTextItem) {
                PrmTextItem prmText = (PrmTextItem) element;
                if (prmText.getIndex() == _extUserPrmData.getDefault()) {
                    return "*" + element.toString();
                }
            }
            return super.getText(element);
        }
    }
    
    /**
     * {@link PrmTextItem} Sorter
     * 
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class PrmTextViewerSorter extends ViewerSorter {
        /**
         * Constructor.
         */
        public PrmTextViewerSorter() {
            // Constructor.
        }
        
        @Override
        public int compare(@Nullable final Viewer viewer,
                           @Nullable final Object e1,
                           @Nullable final Object e2) {
            if ((e1 instanceof PrmTextItem) && (e2 instanceof PrmTextItem)) {
                PrmTextItem eUPD1 = (PrmTextItem) e1;
                PrmTextItem eUPD2 = (PrmTextItem) e2;
                return eUPD1.getIndex() - eUPD2.getIndex();
            }
            return super.compare(viewer, e1, e2);
        }
    }
    
    private final ArrayList<Object> _prmTextCV = new ArrayList<Object>();
    
    /**
     * @param currentUserParamDataComposite
     * @throws IOException
     */
    protected void buildCurrentUserPrmData(@Nonnull final Composite currentUserParamDataComposite) throws IOException {
        AbstractGsdPropertyModel parsedGsdFileModel = getGsdPropertyModel();
        if (parsedGsdFileModel != null) {
            Collection<KeyValuePair> extUserPrmDataRefMap = parsedGsdFileModel
                    .getExtUserPrmDataRefMap().values();
            for (KeyValuePair extUserPrmDataRef : extUserPrmDataRefMap) {
                ExtUserPrmData extUserPrmData = parsedGsdFileModel
                        .getExtUserPrmData(extUserPrmDataRef.getIntValue());
                Integer value = getUserPrmDataValue(extUserPrmDataRef, extUserPrmData);
                makeCurrentUserParamDataItem(currentUserParamDataComposite, extUserPrmData, value);
            }
        }
    }
    
    @CheckForNull
    abstract AbstractGsdPropertyModel getGsdPropertyModel() throws IOException;
    
    @Nonnull
    abstract List<Integer> getPrmUserDataList();
    
    abstract void setPrmUserData(@Nonnull Integer index, @Nonnull Integer value);
    
    @Nonnull
    abstract Integer getPrmUserData(@Nonnull Integer index);
    
    int getUserPrmDataValue(@Nonnull KeyValuePair extUserPrmDataRef,
                            @Nonnull ExtUserPrmData extUserPrmData) {
        List<Integer> prmUserDataList = getPrmUserDataList();
        List<Integer> values = new ArrayList<Integer>();
        values.add(prmUserDataList.get(extUserPrmDataRef.getIndex()));
        int maxBit = extUserPrmData.getMaxBit();
        if ((maxBit > 7) && (maxBit < 16)) {
            values.add(prmUserDataList.get(extUserPrmDataRef.getIndex() + 1));
        }
        int val = getValueFromBitMask(extUserPrmData, values);
        return val;
    }
    
    private int getValueFromBitMask(@Nonnull final ExtUserPrmData ranges,
                                    @Nonnull final List<Integer> values) {
        // TODO (hrickens) [21.04.2011]: Muss refactort werde da der gleiche code auch in setValue2BitMask() verwendent wird.      
        
        int lowByte = 0;
        int highByte = 0;
        if (values.size() > 0) {
            lowByte = values.get(0);
        }
        if (values.size() > 1) {
            highByte = values.get(1);
        }
        int val = highByte * 256 + lowByte;
        int minBit = ranges.getMinBit();
        int maxBit = ranges.getMaxBit();
        if (maxBit < minBit) {
            minBit = ranges.getMaxBit();
            maxBit = ranges.getMinBit();
        }
        int mask = ((int) (Math.pow(2, maxBit + 1) - Math.pow(2, minBit)));
        val = (val & mask) >> ranges.getMinBit();
        return val;
    }
    
    /**
     * 
     * @param parent
     *            the Parent Composite.
     * @param value
     *            the Selected currentUserParamData Value.
     * @param extUserPrmData
     * @param prmText
     * @return a ComboView for are currentUserParamData Property
     */
    @Nonnull
    ComboViewer makeComboViewer(@Nonnull final Composite parent,
                                @Nullable final Integer value,
                                @Nonnull final ExtUserPrmData extUserPrmData,
                                @CheckForNull final PrmText prmText) {
        Integer localValue = value;
        
        ComboViewer prmTextCV = new ComboViewer(parent);
        RowData data = new RowData();
        data.exclude = false;
        prmTextCV.getCombo().setLayoutData(data);
        prmTextCV.setLabelProvider(new PrmTextComboLabelProvider(extUserPrmData));
        prmTextCV.setContentProvider(new ExtUserPrmDataContentProvider());
        prmTextCV.getCombo().addModifyListener(getMLSB());
        prmTextCV.setSorter(new PrmTextViewerSorter());
        
        if (localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmTextCV.setInput(extUserPrmData);
        
        if (prmText != null) {
            PrmTextItem prmTextItem = prmText.getPrmTextItem(localValue);
            if (prmTextItem != null) {
                prmTextCV.setSelection(new StructuredSelection(prmTextItem));
            } else {
                prmTextCV.getCombo().select(0);
            }
        } else {
            prmTextCV.getCombo().select(localValue);
        }
        prmTextCV.getCombo().setData(prmTextCV.getCombo().getSelectionIndex());
        return prmTextCV;
    }
    
    @SuppressWarnings("unused")
    private void makeCurrentUserParamDataItem(@Nonnull final Composite currentUserParamDataGroup,
                                              @Nullable final ExtUserPrmData extUserPrmData,
                                              @Nullable final Integer value) {
        PrmText prmText = null;
        
        Text text = new Text(currentUserParamDataGroup, SWT.SINGLE | SWT.READ_ONLY);
        
        if (extUserPrmData != null) {
            text.setText(extUserPrmData.getText() + ":");
            prmText = extUserPrmData.getPrmText();
            if ((prmText == null || prmText.isEmpty())
                    && (extUserPrmData.getMaxValue() - extUserPrmData.getMinValue() > 10)) {
                _prmTextCV.add(makeTextField(currentUserParamDataGroup, value, extUserPrmData));
            } else {
                _prmTextCV.add(makeComboViewer(currentUserParamDataGroup,
                                               value,
                                               extUserPrmData,
                                               prmText));
            }
        }
        new Label(currentUserParamDataGroup, SWT.SEPARATOR | SWT.HORIZONTAL);// .setLayoutData(new
    }
    
    /**
     * 
     * @param currentUserParamDataGroup
     * @param value
     * @param extUserPrmData
     * @return
     */
    @Nonnull
    Text makeTextField(@Nonnull final Composite currentUserParamDataGroup,
                       @CheckForNull final Integer value,
                       @Nonnull final ExtUserPrmData extUserPrmData) {
        Integer localValue = value;
        Text prmText = new Text(currentUserParamDataGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
        Formatter f = new Formatter();
        f.format("Min: %d, Min: %d Default: %d",
                 extUserPrmData.getMinValue(),
                 extUserPrmData.getMaxValue(),
                 extUserPrmData.getDefault());
        prmText.setToolTipText(f.toString());
        prmText.setTextLimit(Integer.toString(extUserPrmData.getMaxValue()).length());
        
        if (localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmText.setText(localValue.toString());
        prmText.setData(localValue.toString());
        prmText.setData("ExtUserPrmData", extUserPrmData);
        
        prmText.addModifyListener(getMLSB());
        prmText.addVerifyListener(new VerifyListener() {
            
            @Override
            public void verifyText(@Nonnull final VerifyEvent e) {
                if (e.text.matches("^\\D+$")) {
                    e.doit = false;
                }
            }
            
        });
        return prmText;
    }
    
    /**
     * @throws IOException 
    *
    */
    protected void saveUserPrmData() throws IOException {
        Collection<KeyValuePair> extUserPrmDataRefMap = getGsdPropertyModel()
                .getExtUserPrmDataRefMap().values();
        
        if (extUserPrmDataRefMap.size() == _prmTextCV.size()) {
            int i = 0;
            for (KeyValuePair ref : extUserPrmDataRefMap) {
                Object prmTextObject = _prmTextCV.get(i);
                if (prmTextObject instanceof ComboViewer) {
                    ComboViewer prmTextCV = (ComboViewer) prmTextObject;
                    handleComboViewer(prmTextCV, ref.getIndex());
                } else if (prmTextObject instanceof Text) {
                    Text prmText = (Text) prmTextObject;
                    handleText(prmText, ref.getIndex());
                }
                i++;
            }
            
        }
    }
    
    private void handleComboViewer(@Nonnull final ComboViewer prmTextCV,
                                   @Nonnull final Integer byteIndex) throws IOException {
        if (!prmTextCV.getCombo().isDisposed()) {
            ExtUserPrmData extUserPrmData = (ExtUserPrmData) prmTextCV.getInput();
            StructuredSelection selection = (StructuredSelection) prmTextCV.getSelection();
            Integer bitValue = ((PrmTextItem) selection.getFirstElement()).getIndex();
            setValue2BitMask(extUserPrmData, byteIndex, bitValue);
            Integer indexOf = prmTextCV.getCombo().indexOf(selection.getFirstElement().toString());
            prmTextCV.getCombo().setData(indexOf);
        }
    }
    
    @Nonnull
    private void handleText(@Nonnull final Text prmText, @Nonnull Integer byteIndex) {
        if (!prmText.isDisposed()) {
            Object data = prmText.getData("ExtUserPrmData");
            if (data instanceof ExtUserPrmData) {
                ExtUserPrmData extUserPrmData = (ExtUserPrmData) data;
                
                String value = prmText.getText();
                Integer bitValue;
                if (value == null || value.isEmpty()) {
                    bitValue = extUserPrmData.getDefault();
                } else {
                    bitValue = Integer.parseInt(value);
                }
                setValue2BitMask(extUserPrmData, byteIndex, bitValue);
                prmText.setData(bitValue);
            }
        }
    }
    
    /**
     * Change the a value on the Bit places, that is given from the input, to
     * the bitValue.
     * 
     * @param extUserPrmData
     *            give the start and end Bit position.
     * @param bitValue
     *            the new Value for the given Bit position.
     * @param value
     *            the value was changed.
     * @return the changed value.
     */
    @Nonnull
    private void setValue2BitMask(@Nonnull final ExtUserPrmData extUserPrmData,
                                  @Nonnull final Integer byteIndex,
                                  @Nonnull final Integer bitValue) {
        // TODO (hrickens) [21.04.2011]: Muss refactort werde da der gleiche code auch in AbstractGsdPropertyModel#setExtUserPrmDataValue verwendent wird.
        int val = bitValue;
        int minBit = extUserPrmData.getMinBit();
        int maxBit = extUserPrmData.getMaxBit();
        if (maxBit < minBit) {
            minBit = extUserPrmData.getMaxBit();
            maxBit = extUserPrmData.getMinBit();
        }
        int mask = ~((int) (Math.pow(2, maxBit + 1) - Math.pow(2, minBit)));
        if ((maxBit > 7) && (maxBit < 16)) {
            int modifyByteHigh = 0;
            int modifyByteLow = 0;
            modifyByteHigh = getPrmUserData(byteIndex);
            modifyByteLow = getPrmUserData(byteIndex + 1);
            
            int parseInt = modifyByteHigh * 256 + modifyByteLow;
            val = val << (minBit);
            int result = (parseInt & mask) | (val);
            modifyByteLow = result % 256;
            modifyByteHigh = (result - modifyByteLow) / 256;
            setPrmUserData(byteIndex + 1, modifyByteHigh);
            setPrmUserData(byteIndex, modifyByteLow);
        } else {
            int modifyByte = getPrmUserData(byteIndex);
            val = val << (minBit);
            int result = (modifyByte & mask) | (val);
            setPrmUserData(byteIndex, result);
        }
    }
    
}
