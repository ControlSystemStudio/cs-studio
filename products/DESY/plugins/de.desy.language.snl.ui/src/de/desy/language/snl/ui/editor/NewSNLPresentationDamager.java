package de.desy.language.snl.ui.editor;

import java.util.LinkedList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.presentation.IPresentationDamager;

import de.desy.language.snl.parser.Interval;

public class NewSNLPresentationDamager implements IPresentationDamager {

	private final HighlightingRegionMarker[] highlightingMarkers = new HighlightingRegionMarker[] {
			new HighlightingRegionMarker("/*", "*/"),
			new HighlightingRegionMarker("//"),
			new HighlightingRegionMarker("%{", "}%"),
			new HighlightingRegionMarker("%%") };

	private IDocument _document;

	private LinkedList<Interval> _comments;

	public void setDocument(IDocument document) {
		_document = document;
		_comments = new LinkedList<Interval>();
	}

	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
			boolean documentPartitioningChanged) {
		try {
			final int lineIndex = _document.getLineOfOffset(event.getOffset());
			int lineOffset = _document.getLineOffset(lineIndex);
			int lineLength = _document.getLineLength(lineIndex);
			
			Interval oldInterval = getSurroundingComment(event.getOffset());
			
			determineComments();
			Interval interval = getSurroundingComment(event.getOffset());
			if (interval == null) {
				if (oldInterval != null) {
					lineOffset = oldInterval.getStart();
					lineLength = oldInterval.getEnd() - oldInterval.getStart()+1;
				}
			} else {
				lineOffset = interval.getStart();
				lineLength = interval.getEnd() - interval.getStart()+1;
			}
			if (event.getText().length() > lineLength) {
				lineLength = (event.getOffset() - lineOffset) + event.getText().length();
			}
			
			return new Region(lineOffset, lineLength);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return new Region(event.getOffset(), 1);
		}
	}
	
	private void determineComments() {
		_comments.clear();
		int beginIndex = 0;
		int endIndex = 0;
		while (beginIndex >= 0 && endIndex >= 0) {
			beginIndex = _document.get().indexOf("/*", endIndex);
			endIndex = _document.get().indexOf("*/", beginIndex);
			if (beginIndex >= 0 && endIndex >= 0) {
				_comments.add(new Interval(beginIndex, endIndex));			
			}
		}
	}
	
	private Interval getSurroundingComment(int offset) {
		for (Interval interval : _comments) {
			if (interval.getStart() <= offset
					&& offset <= interval.getEnd() + 3) {
				return interval;
			}
		}
		return null;
	}

}

//public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
//boolean documentPartitioningChanged) {
//String insertedText = event.fText;
//
//try {
//final int lineIndex = _document.getLineOfOffset(event.getOffset());
//final int lineOffset = _document.getLineOffset(lineIndex);
//final int lineLength = _document.getLineLength(lineIndex);
//int startOffset = lineOffset;
//int length = lineLength;
//
///*		if (insertedText.trim().length() == 1) {
//	int offset = event.getOffset() - 1;
//	String focus = _document.get(offset, 3);
//	for (HighlightingRegionMarker marker : highlightingMarkers) {
//		if (focus.contains(marker.getBeginMarker())) {
//			if (marker.isSingleLine()) {
//				length = lineLength - (offset - lineOffset);
//			} else {
//				int end = _document.get().indexOf(
//						marker.getEndMarker(), offset);
//				if (end > offset) {
//					length = (end + 2) - offset;
//				}
//			}
//			return new Region(offset, length);
//		} else if (!marker.isSingleLine()
//				&& focus.contains(marker.getEndMarker())) {
//			int begin = _document.get().lastIndexOf(
//					marker.getBeginMarker(), offset);
//			if (begin > 0) {
//				return new Region(begin, offset + 2 - begin);
//			}
//		}
//	}
//} else {*/
//	for (HighlightingRegionMarker marker : highlightingMarkers) {
//		if (!marker.isSingleLine()) {
//			int begin = _document.get().lastIndexOf(marker.getBeginMarker(), event.getOffset());
//			int end = _document.get().lastIndexOf(marker.getEndMarker(), event.getOffset()-2);
//			if (begin > end && begin != end) {
//				end = _document.get().indexOf(marker.getEndMarker(), event.getOffset()+event.getLength()-2);
//				if (end > 0) {
//					return new Region(begin, end+2-begin);
//				}
//			}
//		}
//	}
//	if (event.getLength() > 1) {
//		length = Math.max(lineLength, (event.getOffset() - lineOffset) + event.getLength());
//		return new Region(lineOffset, length);
//	}
////}
//return new Region(startOffset, length);
//} catch (BadLocationException e) {
//e.printStackTrace();
//return new Region(event.getOffset(), 0);
//}
//}
