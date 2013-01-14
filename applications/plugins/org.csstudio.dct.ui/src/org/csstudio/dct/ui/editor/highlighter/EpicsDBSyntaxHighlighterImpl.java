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
package org.csstudio.dct.ui.editor.highlighter;

import java.util.SortedMap;
import java.util.TreeMap;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 01.08.2011
 */
public class EpicsDBSyntaxHighlighterImpl implements IEpicsDBSyntaxHighlighter {
    
    private final StringBuilder _epicsDB;
    
    /**
     * Constructor.
     */
    public EpicsDBSyntaxHighlighterImpl() {
        _epicsDB = new StringBuilder();
    }
    
    /**
     * {@inheritDoc}
     */
    public IEpicsDBSyntaxHighlighter append(String epicsDB) {
        _epicsDB.append(epicsDB);
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public StyleRange[] getStyleRange() {
        SortedMap<Integer, StyleRange> styleRanges = new TreeMap<Integer, StyleRange>();
        Color comment = CustomMediaFactory.getInstance().getColor(63, 127, 95);
        Color string = CustomMediaFactory.getInstance().getColor(42, 0, 255);
        Color field = CustomMediaFactory.getInstance().getColor(0, 0, 0);
        Color record = CustomMediaFactory.getInstance().getColor(127, 0, 85);
        Color background = CustomMediaFactory.getInstance().getColor(255, 255, 255);
        Color error = CustomMediaFactory.getInstance().getColor(255, 0, 0);
        String export = _epicsDB.toString();
        styleRanges = buildStyleRangeComment(export, styleRanges, comment, background);
        styleRanges = buildStyleRangeStringWithErrorDetection(export, styleRanges, string, background, error);
        styleRanges = buildStyleRangeField(export, styleRanges, field, background);
        styleRanges = buildStyleRangeRecord(export, styleRanges, record, background);
        return styleRanges.values().toArray(new StyleRange[0]);
    }
    
    private SortedMap<Integer, StyleRange> buildStyleRangeRecord(String export,
                                       SortedMap<Integer, StyleRange> styleRanges,
                                       Color record,
                                       Color background) {
        int lastPosision;
        lastPosision = 0;
        while (lastPosision < export.length()) {
            int start = export.indexOf("record(", lastPosision);
            int end = start + 7;
            if (start >= 0) {
                start += 7;
                end = export.indexOf(',', start + 1);
                if (end >= 0) {
                    styleRanges.put(start, new StyleRange(start,
                                                          end - (start),
                                                          record,
                                                          background,
                                                          SWT.NORMAL));
                } else {
                    break;
                }
            } else {
                break;
            }
            lastPosision = end + 1;
        }
        return styleRanges;
    }
    
    private SortedMap<Integer, StyleRange> buildStyleRangeField(String export,
                                      SortedMap<Integer, StyleRange> styleRanges,
                                      Color field,
                                      Color background) {
        int lastPosision;
        lastPosision = 0;
        while (lastPosision < export.length()) {
            int start = export.indexOf("field(", lastPosision);
            int end = start + 6;
            if (start >= 0) {
                start += 6;
                end = export.indexOf(',', start + 1);
                if (end >= 0) {
                    styleRanges.put(start, new StyleRange(start,
                                                          end - (start),
                                                          field,
                                                          background,
                                                          SWT.BOLD));
                } else {
                    break;
                }
            } else {
                break;
            }
            lastPosision = end + 1;
        }
        return styleRanges;
    }
    
    private SortedMap<Integer, StyleRange> buildStyleRangeStringWithErrorDetection(String export,
                                                         SortedMap<Integer, StyleRange> styleRanges,
                                                         Color string,
                                                         Color background,
                                                         Color error) {
        int lastPosision;
        lastPosision = 0;
        while (lastPosision < export.length()) {
            int start = export.indexOf('"', lastPosision);
            int end = start;
            if (start >= 0) {
                end = export.indexOf('"', start + 1);
                if (end >= start
                    && (export.startsWith("%%%", start + 1) || export.startsWith("<Error",
                                                                                 start + 1))) {
                    styleRanges.put(start, new StyleRange(start,
                                                          end - start + 1,
                                                          error,
                                                          background,
                                                          SWT.NORMAL));
                } else if (end >= start) {
                    styleRanges.put(start, new StyleRange(start,
                                                          end - start + 1,
                                                          string,
                                                          background,
                                                          SWT.NORMAL));
                } else {
                    break;
                }
            } else {
                break;
            }
            lastPosision = end + 1;
        }
        return styleRanges;
    }
    
    private SortedMap<Integer, StyleRange> buildStyleRangeComment(final String export,
                                        final SortedMap<Integer, StyleRange> styleRanges,
                                        final Color comment,
                                        final Color background) {
        int lastPosision = 0;
        while (lastPosision < export.length()) {
            int start = export.indexOf('#', lastPosision);
            int end = start;
            if (start >= 0) {
                end = export.indexOf("\n", start + 1);
                styleRanges.put(start, new StyleRange(start,
                                                      end - (start),
                                                      comment,
                                                      background,
                                                      SWT.NORMAL));
            } else {
                break;
            }
            lastPosision = end + 1;
        }
        return styleRanges;
    }
    
}
