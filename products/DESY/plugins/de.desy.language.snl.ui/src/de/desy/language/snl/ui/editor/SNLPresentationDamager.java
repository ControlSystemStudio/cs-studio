package de.desy.language.snl.ui.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.presentation.IPresentationDamager;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * This class determines the region of the document to refresh after a character
 * was typed or removed. For SNL this is the part of the text starting at the
 * last ';' or '{' before the current position and ending with ';' or '}'
 * ignoring all declared (single line) comments, (single line) embedded C
 * statements, strings or chars. If the position of the cursor is inside of one
 * of them only this region is refreshed.
 * 
 * @author C1 WPS / KM
 * 
 */
public class SNLPresentationDamager implements IPresentationDamager {

	/**
	 * The {@link IDocument} associated with this {@link IPresentationDamager}.
	 */
	private IDocument _document;
	/**
	 * The chars that starts a single line of comment.
	 */
	private final char[] _singleLineCommentPrefix = new char[] { '/', '/' };
	/**
	 * The chars that starts a single line of embedded c.
	 */
	private final char[] _singleLineEmbeddedCPrefix = new char[] { '%', '%' };
	/**
	 * The chars that starts a multi line comment.
	 */
	private final char[] _commentPrefix = new char[] { '/', '*' };
	/**
	 * The chars that ends a multi line comment.
	 */
	private final char[] _commentPostfix = new char[] { '*', '/' };
	/**
	 * The chars that starts a multi line embedded c statement.
	 */
	private final char[] _embeddedCPrefix = new char[] { '%', '{' };
	/**
	 * The chars that ends a multi line embedded c statement.
	 */
	private final char[] _embeddedCPostfix = new char[] { '}', '%' };

	/**
	 * Checks if the char of the {@link IDocument} at the given offset is the
	 * given compare char <code>c</code> ignoring all occurrences after the
	 * given escape char, when it is not null. If the given offset is not inside
	 * the range of the {@link IDocument} the match fails.
	 * 
	 * @param offset
	 *            The position within the {@link IDocument}
	 * @param c
	 *            The compare char
	 * @param escapeChar
	 *            The escape char (may be null)
	 * @return <code>true</code> if the char at the offset is equal the
	 *         compare char, otherwise <code>false</code>
	 */
	private boolean checkChar(final int offset, final char c,
			final Character escapeChar) {
		try {
			if (_document.getChar(offset) == c) {
				if (escapeChar != null
						&& (offset - 1 < 0 || _document.getChar(offset - 1) == escapeChar)) {
					return false;
				}
				return true;
			}
		} catch (final BadLocationException e) {
			// do nothing
		}
		return false;
	}

	/**
	 * Checks if the chars of the {@link IDocument} starting at the given offset
	 * are equal to the given compare char array <code>chars</code>. If the
	 * given offset is not inside the range of the {@link IDocument} the match
	 * fails.
	 * 
	 * @param offset
	 *            The position within the {@link IDocument}
	 * @param chars
	 *            The compare char array (not null)
	 * @return <code>true</code> if the char at the offset is equal the
	 *         compare char, otherwise <code>false</code>
	 */
	private boolean checkChars(final int offset, final char[] chars) {
		Contract.requireNotNull("chars", chars);
		try {
			for (int i = 0; i < chars.length; i++) {
				int j = offset + i;
				char char1 = _document.getChar(j);
				if (char1 != chars[i]) {
					return false;
				}
			}
			return true;
		} catch (final BadLocationException e) {
			// do nothing
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRegion getDamageRegion(final ITypedRegion partition,
			final DocumentEvent event, final boolean documentPartitioningChanged) {
		IRegion region = this.getSurroundingComment(event.getOffset());
		if (region == null) {
			region = this.getSurroundingEmbeddedC(event.getOffset());
		}
		if (region == null) {
			region = this.getSurroundingSingleLineComment(event.getOffset());
		}
		if (region == null) {
			region = this.getSurroundingSingleLineEmbeddedC(event.getOffset());
		}
		if (region == null) {
			region = this.getSurroundingString(event.getOffset());
		}
		if (region == null) {
			region = this.getSurroundingChar(event.getOffset());
		}
		if (region == null) {
			int start = this.findPositionOfOpeningMarker(event
					.getOffset());
			int end = this.findPositionOfClosingMarker(event.getOffset());

			start = Math.min(start, event.getOffset());
			end = Math.max(end, event.getText().length()+event.getOffset());
			
			region = new Region(start, end - start);
		}
		return region;
	}

	/**
	 * Checks if the given offset is surrounded by <code>/*</code> and
	 * <code>*\/</code> . If a match was found the corresponding region is
	 * returned else <code>null</code> is returned
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The region of the comment or null if no surrounding comment
	 *         exists
	 */
	private Region getSurroundingComment(final int offset) {
		int start = offset;
		int end = offset;
		for (int i = offset - 1; i >= 0; i--) {
			if (this.checkChars(i, _commentPostfix)) {
				break;
			}
			if (this.checkChars(i, _commentPrefix)) {
				start = i - 1;
				break;
			}
		}
		if (start != offset) {
			for (int i = offset; i < this._document.getLength(); i++) {
				if (this.checkChars(i, _commentPostfix)) {
					end = i + 1;
					break;
				}
			}
			if (start != end) {
				return new Region(start, end - start);
			}
		}
		return null;
	}

	/**
	 * Checks if the given offset is surrounded by <code>%{</code> and
	 * <code>}%</code> . If a match was found the corresponding region is
	 * returned else <code>null</code> is returned
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The region of the comment or null if no surrounding comment
	 *         exists
	 */
	private Region getSurroundingEmbeddedC(final int offset) {
		int start = offset;
		int end = offset;
		for (int i = offset - 1; i >= 0; i--) {
			if (this.checkChars(i, _embeddedCPostfix)) {
				break;
			}
			if (this.checkChars(i, _embeddedCPrefix)) {
				start = i - 1;
				break;
			}
		}
		if (start != offset) {
			for (int i = offset; i < this._document.getLength(); i++) {
				if (this.checkChars(i, _embeddedCPostfix)) {
					end = i + 2;
					break;
				}
			}
			if (start != end) {
				return new Region(start, end - start);
			}
		}
		return null;
	}

	/**
	 * Checks if the given offset is surrounded by <code>//</code> and a line
	 * break. If a match was found the corresponding region is returned else
	 * <code>null</code> is returned
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The region of the comment or null if no surrounding comment
	 *         exists
	 */
	private IRegion getSurroundingSingleLineComment(int offset) {
		try {
			final int lineIndex = this._document.getLineOfOffset(offset);
			final int lineOffset = this._document.getLineOffset(lineIndex);
			final int lineLength = this._document.getLineLength(lineIndex);

			for (int i = lineOffset; i < lineOffset + lineLength; i++) {
				if (this.checkChars(i, _singleLineCommentPrefix)) {
					return new Region(lineOffset, lineLength);
				}
			}
		} catch (final BadLocationException e) {
			// do nothing
		}
		return null;
	}

	/**
	 * Checks if the given offset is surrounded by <code>%%</code> and a line
	 * break. If a match was found the corresponding region is returned else
	 * <code>null</code> is returned
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The region of the comment or null if no surrounding comment
	 *         exists
	 */
	private IRegion getSurroundingSingleLineEmbeddedC(int offset) {
		try {
			final int lineIndex = this._document.getLineOfOffset(offset);
			final int lineOffset = this._document.getLineOffset(lineIndex);
			final int lineLength = this._document.getLineLength(lineIndex);

			for (int i = lineOffset; i < lineOffset + lineLength; i++) {
				if (this.checkChars(i, _singleLineEmbeddedCPrefix)) {
					return new Region(lineOffset, lineLength);
				}
			}
		} catch (final BadLocationException e) {
			// do nothing
		}
		return null;
	}

	/**
	 * Checks if the given offset is surrounded by <code>"</code>. If a match
	 * was found the corresponding region is returned else <code>null</code>
	 * is returned
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The region of the comment or null if no surrounding comment
	 *         exists
	 */
	private Region getSurroundingString(final int offset) {
		try {
			int start = -1;
			int end = -1;

			final int lineIndex = this._document.getLineOfOffset(offset);
			final int lineOffset = this._document.getLineOffset(lineIndex);
			int quoteCount = 0;
			for (int i = lineOffset; i < offset; i++) {
				if (this.checkChar(i, '"', '\\')) {
					start = i;
					quoteCount++;
				}
			}
			if (quoteCount % 2 == 1) {
				for (int i = offset; i < this._document.getLength(); i++) {
					if (this.checkChar(i, '\n', null)) {
						end = -1;
						break;
					}
					if (this.checkChar(i, '"', '\\')) {
						end = i + 1;
						break;
					}
				}
				if ((start != end) && (start > 0) && (end > 0)) {
					return new Region(start, end - start);
				}
			}
		} catch (final BadLocationException e) {
			// do nothing
		}
		return null;
	}

	/**
	 * Checks if the given offset is surrounded by <code>'</code>. If a match
	 * was found the corresponding region is returned else <code>null</code>
	 * is returned
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The region of the comment or null if no surrounding comment
	 *         exists
	 */
	private Region getSurroundingChar(final int offset) {
		try {
			int start = -1;
			int end = -1;

			final int lineIndex = this._document.getLineOfOffset(offset);
			final int lineOffset = this._document.getLineOffset(lineIndex);
			int quoteCount = 0;
			for (int i = lineOffset; i < offset; i++) {
				if (this.checkChar(i, '\'', '\\')) {
					start = i;
					quoteCount++;
				}
			}
			if (quoteCount % 2 == 1) {
				for (int i = offset; i < this._document.getLength(); i++) {
					// final char currentChar = this._document.getChar(i);
					// if (currentChar == '\n') {
					if (this.checkChar(i, '\n', null)) {
						end = -1;
						break;
					}
					// if (currentChar == '"') {
					if (this.checkChar(i, '\'', '\\')) {
						end = i + 1;
						break;
					}
				}
				if ((start != end) && (start > 0) && (end > 0)) {
					return new Region(start, end - start);
				}
			}
		} catch (final BadLocationException e) {
			// do nothing
		}
		return null;
	}

	/**
	 * Returns the offset first <code>;</code> or <code>{</code> outside
	 * other SNL-statements
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The offset first <code>;</code> or <code>{</code>
	 */
	private int findPositionOfClosingMarker(final int offset) {
		try {
			final int end = this._document.getLength() - offset;
			if (this._document.get(offset, end).contains("}")
					|| this._document.get(offset, end).contains(";")) {
				boolean inComment = false;
				for (int i = offset + 1; i < this._document.getLength(); i++) {
					final char currentChar = this._document.getChar(i);
					final char nextChar = this._document.getChar(i + 1);
					if ((currentChar == '/') && (nextChar == '*')
							|| (currentChar == '\\' && nextChar != '"')
							|| (currentChar == '\\' && nextChar != '\'')) {
						inComment = true;
					} else if ((currentChar == '*') && (nextChar == '/')
							|| (currentChar == '\\' && nextChar != '"')
							|| (currentChar == '\\' && nextChar != '\'')) {
						inComment = false;
					} else if (!inComment
							&& (((currentChar == '}') && (nextChar != '%')) || (currentChar == ';'))) {
						return i;
					}
				}
			}
		} catch (final BadLocationException e) {
			// do nothing
		}
		return this._document.getLength();
	}

	/**
	 * Returns the offset first <code>;</code> or <code>}</code> outside
	 * other SNL-statements
	 * 
	 * @param offset
	 *            The current position within the {@link IDocument}
	 * @return The offset first <code>;</code> or <code>}</code>
	 */
	private int findPositionOfOpeningMarker(final int offset) {
		try {
			if (this._document.get(0, offset).contains("{")
					|| this._document.get(0, offset).contains(";")) {
				boolean inComment = false;
				for (int i = offset - 1; i >= 0; i--) {
					final char currentChar = this._document.getChar(i);
					final char previousChar = this._document.getChar(i - 1);
					if ((currentChar == '/') && (previousChar == '*')
							|| (currentChar == '"' && previousChar != '\\')
							|| (currentChar == '\'' && previousChar != '\\')) {
						inComment = true;
					} else if ((currentChar == '*') && (previousChar == '/')
							|| (currentChar == '"' && previousChar != '\\')
							|| (currentChar == '\'' && previousChar != '\\')) {
						inComment = false;
					} else if (!inComment
							&& (((currentChar == '{') && (previousChar != '%')) || (currentChar == ';'))) {
						return i;
					}
				}
			}
		} catch (final BadLocationException e) {
			// do nothing
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDocument(final IDocument document) {
		this._document = document;
	}

}
