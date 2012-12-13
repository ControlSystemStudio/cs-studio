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

//	private final HighlightingRegionMarker[] highlightingMarkers = new HighlightingRegionMarker[] {
//			new HighlightingRegionMarker("/*", "*/"),
//			new HighlightingRegionMarker("//"),
//			new HighlightingRegionMarker("%{", "}%"),
//			new HighlightingRegionMarker("%%") };

	private IDocument _document;
	private IRegion _rememberedRegion = null;

	private LinkedList<Interval> _comments;

	public void setDocument(IDocument document) {
		_document = document;
		_comments = new LinkedList<Interval>();
		try {
			determineComments();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
			boolean documentPartitioningChanged) {
		try {
			if (event.getLength() > 0 && _rememberedRegion != null) {
				IRegion result = _rememberedRegion;
				_rememberedRegion = null;
				return result;
			}
			final int lineIndex = _document.getLineOfOffset(event.getOffset());
			int lineOffset = _document.getLineOffset(lineIndex);
			int lineLength = _document.getLineLength(lineIndex);
			
			Interval oldInterval = getSurroundingComment(event.getOffset());
			
			determineComments();
			Interval interval = getSurroundingComment(event.getOffset());
			if (interval == null) {
				if (oldInterval != null) {
					lineOffset = oldInterval.getStart();
					lineLength = oldInterval.getEnd() - oldInterval.getStart()+2;
				}
			} else {
				if (oldInterval != null) {
					lineOffset = Math.min(oldInterval.getStart(), interval.getStart());
					lineLength = Math.max(oldInterval.getEnd(), interval.getEnd()) - lineOffset + 2;
				} else {
					lineOffset = interval.getStart();
					lineLength = interval.getEnd() - interval.getStart() + 2;
				}
			}
			if (event.getText().length() > lineLength) {
				lineLength = (event.getOffset() - lineOffset) + event.getText().length();
			}
			IRegion result = new Region(lineOffset, lineLength);
			if (event.getLength() > 0 ) {
				_rememberedRegion = result; 
			}
			return result;
		} catch (BadLocationException e) {
			e.printStackTrace();
			return new Region(event.getOffset(), 1);
		}
	}
	
	private void determineComments() throws BadLocationException {
		_comments.clear();
		int beginIndex = 0;
		int endIndex = 0;
		while (beginIndex >= 0 && endIndex >= 0) {
			beginIndex = _document.get().indexOf("/*", endIndex);
			endIndex = _document.get().indexOf("*/", beginIndex);
			if (beginIndex >= 0 && !shouldMarkerBeIgnored(beginIndex) && 
					endIndex >= 0 && !shouldMarkerBeIgnored(endIndex)) {
				int start = _document.getLineOffset(_document.getLineOfOffset(beginIndex));
				_comments.add(new Interval(start, endIndex));			
			}
		}
		
		beginIndex = 0;
		endIndex = 0;
		while (beginIndex >= 0 && endIndex >= 0) {
			beginIndex = _document.get().indexOf("%{", endIndex);
			endIndex = _document.get().indexOf("}%", beginIndex);
			if (beginIndex >= 0 && !shouldMarkerBeIgnored(beginIndex) && 
					endIndex >= 0 && !shouldMarkerBeIgnored(endIndex)) {
				int start = _document.getLineOffset(_document.getLineOfOffset(beginIndex));
				_comments.add(new Interval(start, endIndex));			
			}
		}
	}

	private boolean shouldMarkerBeIgnored(int offset) throws BadLocationException {
		int lineIndex = _document.getLineOfOffset(offset);
		int lineOffset = _document.getLineOffset(lineIndex);
		int lineLength = _document.getLineLength(lineIndex);
		String line = _document.get().substring(lineOffset,
				lineOffset + lineLength);

		int singleLineCommentIndex = line.indexOf("//");
		if (singleLineCommentIndex >= 0 && singleLineCommentIndex < offset) {
			return true;
		}

		int index = line.indexOf('"');
		int count = 0;
		while (index >= 0) {
			if (index > 0 && line.charAt(index - 1) != '\\') {
				count++;
			}
			index = line.indexOf('"', index+1);
		}
		if (count % 2 != 0) {
			return true;
		}
		return false;
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
