/*
 * 10/16/2004
 *
 * RSyntaxDocument.java - A document capable of syntax highlighting, used by
 * RSyntaxTextArea.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.Action;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.modes.AbstractMarkupTokenMaker;


/**
 * The document used by {@link org.fife.ui.rsyntaxtextarea.RSyntaxTextArea}.
 * This document is like <code>javax.swing.text.PlainDocument</code> except that
 * it also keeps track of syntax highlighting in the document.  It has a "style"
 * attribute associated with it that determines how syntax highlighting is done
 * (i.e., what language is being highlighted).<p>
 *
 * Instances of <code>RSyntaxTextArea</code> will only accept instances of
 * <code>RSyntaxDocument</code>, since it is this document that keeps
 * track of syntax highlighting.  All others will cause an exception to be
 * thrown.<p>
 *
 * To change the language being syntax highlighted at any time, you merely have
 * to call {@link #setSyntaxStyle}.  Other than that, this document can be
 * treated like any other save one caveat:  all <code>DocumentEvent</code>s of
 * type <code>CHANGE</code> use their offset and length values to represent the
 * first and last lines, respectively, that have had their syntax coloring
 * change.  This is really a hack to increase the speed of the painting code
 * and should really be corrected, but oh well.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public class RSyntaxDocument extends PlainDocument implements SyntaxConstants {

	/**
	 * Creates a {@link TokenMaker} appropriate for a given programming
	 * language.
	 */
	private transient TokenMakerFactory tokenMakerFactory;

	/**
	 * Splits text into tokens for the current programming language.
	 */
	private TokenMaker tokenMaker;

	/**
	 * Array of values representing the "last token type" on each line.  This
	 * is used in cases such as multiline comments:  if the previous line
	 * ended with an (unclosed) multiline comment, we can use this knowledge
	 * and start the current line's syntax highlighting in multiline comment
	 * state.
	 */
	protected DynamicIntArray lastTokensOnLines;

	private transient Segment s;


	/**
	 * Constructs a plain text document.  A default root element is created,
	 * and the tab size set to 5.
	 *
	 * @param syntaxStyle The syntax highlighting scheme to use.
	 */
	public RSyntaxDocument(String syntaxStyle) {
		this(null, syntaxStyle);
	}


	/**
	 * Constructs a plain text document.  A default root element is created,
	 * and the tab size set to 5.
	 *
	 * @param tmf The <code>TokenMakerFactory</code> for this document.  If
	 *        this is <code>null</code>, a default factory is used.
	 * @param syntaxStyle The syntax highlighting scheme to use.
	 */
	public RSyntaxDocument(TokenMakerFactory tmf, String syntaxStyle) {
		super(new RGapContent());
		putProperty(tabSizeAttribute, new Integer(5));
		lastTokensOnLines = new DynamicIntArray(400);
		lastTokensOnLines.add(Token.NULL); // Initial (empty) line.
		s = new Segment();
		setTokenMakerFactory(tmf);
		setSyntaxStyle(syntaxStyle);
	}


	/**
	 * Returns the character in the document at the specified offset.
	 *
	 * @param offset The offset of the character.
	 * @return The character.
	 * @throws BadLocationException If the offset is invalid.
	 */
	public char charAt(int offset) throws BadLocationException {
		return ((RGapContent)getContent()).charAt(offset);
	}


	/**
	 * Alerts all listeners to this document of an insertion.  This is
	 * overridden so we can update our syntax highlighting stuff.<p>
	 * The syntax highlighting stuff has to be here instead of in
	 * <code>insertUpdate</code> because <code>insertUpdate</code> is not
	 * called by the undo/redo actions, but this method is.
	 *
	 * @param e The change.
	 */
	protected void fireInsertUpdate(DocumentEvent e) {

		/*
		 * Now that the text is actually inserted into the content and
		 * element structure, we can update our token elements and "last
		 * tokens on lines" structure.
		 */

		Element lineMap = getDefaultRootElement();
		DocumentEvent.ElementChange change = e.getChange(lineMap);
		Element[] added = change==null ? null : change.getChildrenAdded();

		int numLines = lineMap.getElementCount();
		int line = lineMap.getElementIndex(e.getOffset());
		int previousLine = line - 1;
		int previousTokenType = (previousLine>-1 ?
					lastTokensOnLines.get(previousLine) : Token.NULL);

		// If entire lines were added...
		if (added!=null && added.length>0) {

			Element[] removed = change.getChildrenRemoved();
			int numRemoved = removed!=null ? removed.length : 0;

			int endBefore = line + added.length - numRemoved;
			//System.err.println("... adding lines: " + line + " - " + (endBefore-1));
			//System.err.println("... ... added: " + added.length + ", removed:" + numRemoved);
			for (int i=line; i<endBefore; i++) {

				setSharedSegment(i); // Loads line i's text into s.

				int tokenType = tokenMaker.getLastTokenTypeOnLine(s, previousTokenType);
				lastTokensOnLines.add(i, tokenType);
				//System.err.println("--------- lastTokensOnLines.size() == " + lastTokensOnLines.getSize());

				previousTokenType = tokenType;

			} // End of for (int i=line; i<endBefore; i++).

			// Update last tokens for lines below until they stop changing.
			updateLastTokensBelow(endBefore, numLines, previousTokenType);

		} // End of if (added!=null && added.length>0).

		// Otherwise, text was inserted on a single line...
		else {

			// Update last tokens for lines below until they stop changing.
			updateLastTokensBelow(line, numLines, previousTokenType);

		} // End of else.

		// Let all listeners know about the insertion.
		super.fireInsertUpdate(e);

	}


	/**
	 * This method is called AFTER the content has been inserted into the
	 * document and the element structure has been updated.<p>
	 * The syntax-highlighting updates need to be done here (as opposed to
	 * an override of <code>postRemoveUpdate</code>) as this method is called
	 * in response to undo/redo events, whereas <code>postRemoveUpdate</code>
	 * is not.<p>
	 * Now that the text is actually inserted into the content and element
	 * structure, we can update our token elements and "last tokens on
	 * lines" structure.
	 *
	 * @param chng The change that occurred.
	 * @see #removeUpdate
	 */
	protected void fireRemoveUpdate(DocumentEvent chng) {

		Element lineMap = getDefaultRootElement();
		int numLines = lineMap.getElementCount();

		DocumentEvent.ElementChange change = chng.getChange(lineMap);
		Element[] removed = change==null ? null : change.getChildrenRemoved();

		// If entire lines were removed...
		if (removed!=null && removed.length>0) {

			int line = change.getIndex();	// First line entirely removed.
			int previousLine = line - 1;	// Line before that.
			int previousTokenType = (previousLine>-1 ?
					lastTokensOnLines.get(previousLine) : Token.NULL);

			Element[] added = change.getChildrenAdded();
			int numAdded = added==null ? 0 : added.length;

			// Remove the cached last-token values for the removed lines.
			int endBefore = line + removed.length - numAdded;
			//System.err.println("... removing lines: " + line + " - " + (endBefore-1));
			//System.err.println("... added: " + numAdded + ", removed: " + removed.length);

			lastTokensOnLines.removeRange(line, endBefore); // Removing values for lines [line-(endBefore-1)].
			//System.err.println("--------- lastTokensOnLines.size() == " + lastTokensOnLines.getSize());

			// Update last tokens for lines below until they've stopped changing.
			updateLastTokensBelow(line, numLines, previousTokenType);

		} // End of if (removed!=null && removed.size()>0).

		// Otherwise, text was removed from just one line...
		else {

			int line = lineMap.getElementIndex(chng.getOffset());
			if (line>=lastTokensOnLines.getSize())
				return;	// If we're editing the last line in a document...

			int previousLine = line - 1;
			int previousTokenType = (previousLine>-1 ?
					lastTokensOnLines.get(previousLine) : Token.NULL);
			//System.err.println("previousTokenType for line : " + previousLine + " is " + previousTokenType);
			// Update last tokens for lines below until they've stopped changing.
			updateLastTokensBelow(line, numLines, previousTokenType);

		}

		// Let all of our listeners know about the removal.
		super.fireRemoveUpdate(chng);

	}


	/**
	 * Returns whether closing markup tags should be automatically completed.
	 * This method only returns <code>true</code> if
	 * {@link #getLanguageIsMarkup()} also returns <code>true</code>.
	 *
	 * @return Whether markup closing tags should be automatically completed.
	 * @see #getLanguageIsMarkup()
	 */
	public boolean getCompleteMarkupCloseTags() {
		// TODO: Remove terrible dependency on AbstractMarkupTokenMaker
		return getLanguageIsMarkup() &&
				((AbstractMarkupTokenMaker)tokenMaker).getCompleteCloseTags();
	}


	/**
	 * Returns whether the current programming language uses curly braces
	 * ('<tt>{</tt>' and '<tt>}</tt>') to denote code blocks.
	 *
	 * @return Whether curly braces denote code blocks.
	 */
	public boolean getCurlyBracesDenoteCodeBlocks() {
		return tokenMaker.getCurlyBracesDenoteCodeBlocks();
	}


	/**
	 * Returns whether the current language is a markup language, such as
	 * HTML, XML or PHP.
	 *
	 * @return Whether the current language is a markup language.
	 */
	public boolean getLanguageIsMarkup() {
		return tokenMaker.isMarkupLanguage();
	}


	/**
	 * Returns the token type of the last token on the given line.
	 *
	 * @param line The line to inspect.
	 * @return The token type of the last token on the specified line.  If
	 *         the line is invalid, an exception is thrown.
	 */
	public int getLastTokenTypeOnLine(int line) {
		return lastTokensOnLines.get(line);
	}


	/**
	 * Returns the text to place at the beginning and end of a
	 * line to "comment" it in the current programming language.
	 *
	 * @return The start and end strings to add to a line to "comment"
	 *         it out.  A <code>null</code> value for either means there
	 *         is no string to add for that part.  A value of
	 *         <code>null</code> for the array means this language
	 *         does not support commenting/uncommenting lines.
	 */
	public String[] getLineCommentStartAndEnd() {
		return tokenMaker.getLineCommentStartAndEnd();
	}


	/**
	 * Returns whether tokens of the specified type should have "mark
	 * occurrences" enabled for the current programming language.
	 *
	 * @param type The token type.
	 * @return Whether tokens of this type should have "mark occurrences"
	 *         enabled.
	 */
	boolean getMarkOccurrencesOfTokenType(int type) {
		return tokenMaker.getMarkOccurrencesOfTokenType(type);
	}


	/**
	 * This method returns whether auto indentation should be done if Enter
	 * is pressed at the end of the specified line.
	 *
	 * @param line The line to check.
	 * @return Whether an extra indentation should be done.
	 */
	public boolean getShouldIndentNextLine(int line) {
		Token t = getTokenListForLine(line);
		t = t.getLastNonCommentNonWhitespaceToken();
		return tokenMaker.getShouldIndentNextLineAfter(t);
	}


	/**
	 * Returns a token list for the specified segment of text representing
	 * the specified line number.  This method is basically a wrapper for
	 * <code>tokenMaker.getTokenList</code> that takes into account the last
	 * token on the previous line to assure token accuracy.
	 *
	 * @param line The line number of <code>text</code> in the document, >= 0.
	 * @return A token list representing the specified line.
	 */
	public final Token getTokenListForLine(int line) {
		Element map = getDefaultRootElement();
		Element elem = map.getElement(line);
		int startOffset = elem.getStartOffset();
		//int endOffset = (line==map.getElementCount()-1 ? elem.getEndOffset() - 1:
		//									elem.getEndOffset() - 1);
		int endOffset = elem.getEndOffset() - 1; // Why always "-1"?
		try {
			getText(startOffset,endOffset-startOffset, s);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
			return null;
		}
		int initialTokenType = line==0 ? Token.NULL :
								getLastTokenTypeOnLine(line-1);
		return tokenMaker.getTokenList(s, initialTokenType, startOffset);
	}


	boolean insertBreakSpecialHandling(ActionEvent e) {
		Action a = tokenMaker.getInsertBreakAction();
		if (a!=null) {
			a.actionPerformed(e);
			return true;
		}
		return false;
	}


	/**
	 * Deserializes a document.
	 *
	 * @param s The stream to read from.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream s)
						throws ClassNotFoundException, IOException {
		s.defaultReadObject();
		this.s = new Segment();
	}


	/**
	 * Makes our private <code>Segment s</code> point to the text in our
	 * document referenced by the specified element.  Note that
	 * <code>line</code> MUST be a valid line number in the document.
	 *
	 * @param line The line number you want to get.
	 */
	private final void setSharedSegment(int line) {

		Element map = getDefaultRootElement();
		//int numLines = map.getElementCount();

		Element element = map.getElement(line);
		if (element==null)
			throw new InternalError("Invalid line number: " + line);
		int startOffset = element.getStartOffset();
		//int endOffset = (line==numLines-1 ?
		//			element.getEndOffset()-1 : element.getEndOffset() - 1);
		int endOffset = element.getEndOffset()-1; // Why always "-1"?
		try {
			getText(startOffset, endOffset-startOffset, s);
		} catch (BadLocationException ble) {
			throw new InternalError("Text range not in document: " +
								startOffset + "-" + endOffset);
		}

	}


	/**
	 * Sets the syntax style being used for syntax highlighting in this
	 * document.  What styles are supported by a document is determined by its
	 * {@link TokenMakerFactory}.  By default, all <code>RSyntaxDocument</code>s
	 * support all languages built into <code>RSyntaxTextArea</code>. 
	 *
	 * @param styleKey The new style to use, such as
	 *        {@link SyntaxConstants#SYNTAX_STYLE_JAVA}.  If this style is not
	 *        known or supported by this document, then
	 *        {@link SyntaxConstants#SYNTAX_STYLE_NONE} is used.
	 */
	public void setSyntaxStyle(String styleKey) {
		tokenMaker = tokenMakerFactory.getTokenMaker(styleKey);
		updateSyntaxHighlightingInformation();
	}


	/**
	 * Sets the syntax style being used for syntax highlighting in this
	 * document.  You should call this method if you've created a custom token
	 * maker for a language not normally supported by
	 * <code>RSyntaxTextArea</code>.
	 *
	 * @param tokenMaker The new token maker to use.
	 */
	public void setSyntaxStyle(TokenMaker tokenMaker) {
		this.tokenMaker = tokenMaker;
		updateSyntaxHighlightingInformation();
	}


	/**
	 * Sets the token maker factory used by this document.
	 *
	 * @param tmf The <code>TokenMakerFactory</code> for this document.  If
	 *        this is <code>null</code>, a default factory is used.
	 */
	public void setTokenMakerFactory(TokenMakerFactory tmf) {
		tokenMakerFactory = tmf!=null ? tmf :
			TokenMakerFactory.getDefaultInstance();
	}


	/**
	 * Loops through the last-tokens-on-lines array from a specified point
	 * onward, updating last-token values until they stop changing.  This
	 * should be called when lines are updated/inserted/removed, as doing
	 * so may cause lines below to change color.
	 *
	 * @param line The first line to check for a change in last-token value.
	 * @param numLines The number of lines in the document.
	 * @param previousTokenType The last-token value of the line just before
	 *        <code>line</code>.
	 * @return The last line that needs repainting.
	 */
	private int updateLastTokensBelow(int line, int numLines, int previousTokenType) {

		int firstLine = line;

		// Loop through all lines past our starting point.  Update even the last
		// line's info, even though there aren't any lines after it that depend
		// on it changing for them to be changed, as its state may be used
		// elsewhere in the library.
		int end = numLines;
		//System.err.println("--- end==" + end + " (numLines==" + numLines + ")");
		while (line<end) {

			setSharedSegment(line); // Sets s's text to that of line 'line' in the document.

			int oldTokenType = lastTokensOnLines.get(line);
			int newTokenType = tokenMaker.getLastTokenTypeOnLine(s, previousTokenType);
			//System.err.println("---------------- line " + line + "; oldTokenType==" + oldTokenType + ", newTokenType==" + newTokenType + ", s=='" + s + "'");

			// If this line's end-token value didn't change, stop here.  Note
			// that we're saying this line needs repainting; this is because
			// the beginning of this line did indeed change color, but the
			// end didn't.
			if (oldTokenType==newTokenType) {
				//System.err.println("... ... ... repainting lines " + firstLine + "-" + line);
				fireChangedUpdate(new DefaultDocumentEvent(firstLine, line, DocumentEvent.EventType.CHANGE));
				return line;
			}

			// If the line's end-token value did change, update it and
			// keep going.
			// NOTE: "setUnsafe" is okay here as the bounds checking was
			// already done in lastTokensOnLines.get(line) above.
			lastTokensOnLines.setUnsafe(line, newTokenType);
			previousTokenType = newTokenType;
			line++;

		} // End of while (line<numLines).

		// If any lines had their token types changed, fire a changed update
		// for them.  The view will repaint the area covered by the lines.
		// FIXME:  We currently cheat and send the line range that needs to be
		// repainted as the "offset and length" of the change, since this is
		// what the view needs.  We really should send the actual offset and
		// length.
		if (line>firstLine) {
			//System.err.println("... ... ... repainting lines " + firstLine + "-" + line);
			fireChangedUpdate(new DefaultDocumentEvent(firstLine, line,
								DocumentEvent.EventType.CHANGE));
		}

		return line;

	}


	/**
	 * Updates internal state information; e.g. the "last tokens on lines"
	 * data.  After this, a changed update is fired to let listeners know that
	 * the document's structure has changed.<p>
	 *
	 * This is called internally whenever the syntax style changes.
	 */
	protected void updateSyntaxHighlightingInformation() {

		// Reinitialize the "last token on each line" array.  Note that since
		// the actual text in the document isn't changing, the number of lines
		// is the same.
		Element map = getDefaultRootElement();
		int numLines = map.getElementCount();
		int lastTokenType = Token.NULL;
		for (int i=0; i<numLines; i++) {
			setSharedSegment(i);
			lastTokenType = tokenMaker.getLastTokenTypeOnLine(s, lastTokenType);
			lastTokensOnLines.set(i, lastTokenType);
		}

		// Let everybody know that syntax styles have (probably) changed.
		fireChangedUpdate(new DefaultDocumentEvent(
						0, numLines-1, DocumentEvent.EventType.CHANGE));

	}


	/**
	 * Document content that provides access to individual characters.
	 *
	 * @author Robert Futrell
	 * @version 1.0
	 */
	private static class RGapContent extends GapContent {

		public RGapContent() {
		}

		public char charAt(int offset) throws BadLocationException {
			if (offset<0 || offset>=length()) {
				throw new BadLocationException("Invalid offset", offset);
			}
			int g0 = getGapStart();
			char[] array = (char[]) getArray();
			if (offset<g0) { // below gap
				return array[offset];
			}
			return array[getGapEnd() + offset - g0]; // above gap
		}

	}


/**
 * Similar to a <code>java.util.ArrayList</code>, but specifically for
 * <code>int</code>s.  This is basically an array of integers that resizes
 * itself (if necessary) when adding new elements.
 *
 * @author Robert Futrell
 * @version 0.8
 */
	private class DynamicIntArray implements Serializable {

	/**
	 * The actual data.
	 */
	private int[] data;

	/**
	 * The number of values in the array.  Note that this is NOT the
	 * capacity of the array; rather, <code>size &lt;= capacity</code>.
	 */
	private int size;


	/**
	 * Constructs a new array object with an initial capacity of 10.
	 */
	public DynamicIntArray() {
		this(10);
	}


	/**
	 * Constructs a new array object with a given initial capacity.
	 *
	 * @param initialCapacity The initial capacity.
	 * @throws IllegalArgumentException If <code>initialCapacity</code> is
	 *         negative.
	 */
	public DynamicIntArray(int initialCapacity) {
		if (initialCapacity<0) {
			throw new IllegalArgumentException("Illegal initialCapacity: "
												+ initialCapacity);
		}
		data = new int[initialCapacity];
		size = 0;
	}


	/**
	 * Constructs a new array object from the given int array.  The resulting
	 * <code>DynamicIntArray</code> will have an initial capacity of 110%
	 * the size of the array.
	 *
	 * @param intArray Initial data for the array object.
	 * @throws NullPointerException If <code>intArray</code> is
	 *         <code>null</code>.
	 */
	public DynamicIntArray(int[] intArray) {
		size = intArray.length;
		int capacity = (int)Math.min(size*110L/100, Integer.MAX_VALUE);
		data = new int[capacity];
		System.arraycopy(intArray,0, data,0, size); // source, dest, length.
	}


	/**
	 * Appends the specified <code>int</code> to the end of this array.
	 *
	 * @param value The <code>int</code> to be appended to this array.
	 */
	public void add(int value) {
		ensureCapacity(size + 1);
		data[size++] = value;
	}


	/**
	 * Inserts all <code>int</code>s in the specified array into this array
	 * object at the specified location.  Shifts the <code>int</code>
	 * currently at that position (if any) and any subsequent
	 * <code>int</code>s to the right (adds one to their indices).
	 *
	 * @param index The index at which the specified integer is to be
	 *        inserted.
	 * @param intArray The array of <code>int</code>s to insert.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than <code>getSize()</code>.
	 * @throws NullPointerException If <code>intArray<code> is
	 *         <code>null<code>.
	 */
	public void add(int index, int[] intArray) {
		if (index>size) {
			throwException2(index);
		}
		int addCount = intArray.length;
		ensureCapacity(size+addCount);
		int moveCount = size - index;
		if (moveCount>0)
			System.arraycopy(data,index, data,index+addCount, moveCount);
		System.arraycopy(data,index, intArray,0, moveCount);
		size += addCount;
	}


	/**
	 * Inserts the specified <code>int</code> at the specified position in
	 * this array. Shifts the <code>int</code> currently at that position (if
	 * any) and any subsequent <code>int</code>s to the right (adds one to
	 * their indices).
	 *
	 * @param index The index at which the specified integer is to be
	 *        inserted.
	 * @param value The <code>int</code> to be inserted.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than <code>getSize()</code>.
	 */
	public void add(int index, int value) {
		if (index>size) {
			throwException2(index);
		}
		ensureCapacity(size+1);
		System.arraycopy(data,index, data,index+1, size-index);
		data[index] = value;
		size++;
	}


	/**
	 * Removes all values from this array object.  Capacity will remain the
	 * same.
	 */
	public void clear() {
		size = 0;
	}


	/**
	 * Returns whether this array contains a given integer.  This method
	 * performs a linear search, so it is not optimized for performance.
	 *
	 * @param integer The <code>int</code> for which to search.
	 * @return Whether the given integer is contained in this array.
	 */
	public boolean contains(int integer) {
		for (int i=0; i<size; i++) {
			if (data[i]==integer)
				return true;
		}
		return false;
	}


	/**
	 * Makes sure that this <code>DynamicIntArray</code> instance can hold
	 * at least the number of elements specified.  If it can't, then the
	 * capacity is increased.
	 *
	 * @param minCapacity The desired minimum capacity.
	 */
	private final void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		if (minCapacity > oldCapacity) {
			int[] oldData = data;
			// Ensures we don't just keep increasing capacity by some small
			// number like 1...
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			data = new int[newCapacity];
			System.arraycopy(oldData,0, data,0, size);
		}
	}


	/**
	 * Returns the <code>int</code> at the specified position in this array
	 * object.
	 *
	 * @param index The index of the <code>int</code> to return.
	 * @return The <code>int</code> at the specified position in this array.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than or equal to <code>getSize()</code>.
	 */
	public int get(int index) {
		// Small enough to be inlined, and throwException() is rarely called.
		if (index>=size) {
			throwException(index);
		}
		return data[index];
	}


	/**
	 * Returns the <code>int</code> at the specified position in this array
	 * object, without doing any bounds checking.  You really should use
	 * {@link #get(int)} instead of this method.
	 *
	 * @param index The index of the <code>int</code> to return.
	 * @return The <code>int</code> at the specified position in this array.
	 */
	public int getUnsafe(int index) {
		// Small enough to be inlined.
		return data[index];
	}


	/**
	 * Returns the number of <code>int</code>s in this array object.
	 *
	 * @return The number of <code>int</code>s in this array object.
	 */
	public int getSize() {
		return size;
	}


	/**
	 * Returns whether or not this array object is empty.
	 *
	 * @return Whether or not this array object contains no elements.
	 */
	public boolean isEmpty() {
		return size==0;
	}


	/**
	 * Removes the <code>int</code> at the specified location from this array
	 * object.
	 *
	 * @param index The index of the <code>int</code> to remove.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than or equal to <code>getSize()</code>.
	 */
	public void remove(int index) {
		if (index>=size) {
			throwException(index);
		}
		int toMove = size - index - 1;
		if (toMove>0) {
			System.arraycopy(data,index+1, data,index, toMove);
		}
		--size;
	}


	/**
	 * Removes the <code>int</code>s in the specified range from this array
	 * object.
	 *
	 * @param fromIndex The index of the first <code>int</code> to remove.
	 * @param toIndex The index AFTER the last <code>int</code> to remove.
	 * @throws IndexOutOfBoundsException If either of <code>fromIndex</code>
	 *         or <code>toIndex</code> is less than zero or greater than or
	 *         equal to <code>getSize()</code>.
	 */
	public void removeRange(int fromIndex, int toIndex) {
		if (fromIndex>=size || toIndex>size) {
			throwException3(fromIndex, toIndex);
		}
		int moveCount = size - toIndex;
		System.arraycopy(data,toIndex, data,fromIndex, moveCount);
		size -= (toIndex - fromIndex);
	}

		
	/**
	 * Sets the <code>int</code> value at the specified position in this
	 * array object.
	 *
	 * @param index The index of the <code>int</code> to set
	 * @param value The value to set it to.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than or equal to <code>getSize()</code>.
	 */
	public void set(int index, int value) {
		// Small enough to be inlined, and throwException() is rarely called.
		if (index>=size) {
			throwException(index);
		}
		data[index] = value;
	}


	/**
	 * Sets the <code>int</code> value at the specified position in this
	 * array object, without doing any bounds checking.  You should use
	 * {@link #set(int, int)} instead of this method.
	 *
	 * @param index The index of the <code>int</code> to set
	 * @param value The value to set it to.
	 */
	public void setUnsafe(int index, int value) {
		// Small enough to be inlined.
		data[index] = value;
	}


	/**
	 * Throws an exception.  This method isolates error-handling code from
	 * the error-checking code, so that callers (e.g. {@link #get} and
	 * {@link #set}) can be both small enough to be inlined, as well as
	 * not usually make any expensive method calls (since their callers will
	 * usually not pass illegal arguments to them).
	 *
	 * See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103956">
	 * this Sun bug report</a> for more information.
	 *
	 * @param index The invalid index.
	 * @throws IndexOutOfBoundsException Always.
	 */
	private final void throwException(int index)
								throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Index " + index +
						" not in valid range [0-" + (size-1) + "]");
	}


	/**
	 * Throws an exception.  This method isolates error-handling code from
	 * the error-checking code, so that callers can be both small enough to be
	 * inlined, as well as not usually make any expensive method calls (since
	 * their callers will usually not pass illegal arguments to them).
	 *
	 * See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103956">
	 * this Sun bug report</a> for more information.
	 *
	 * @param index The invalid index.
	 * @throws IndexOutOfBoundsException Always.
	 */
	private final void throwException2(int index)
								throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Index " + index +
								", not in range [0-" + size + "]");
	}


	/**
	 * Throws an exception.  This method isolates error-handling code from
	 * the error-checking code, so that callers can be both small enough to be
	 * inlined, as well as not usually make any expensive method calls (since
	 * their callers will usually not pass illegal arguments to them).
	 *
	 * See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103956">
	 * this Sun bug report</a> for more information.
	 *
	 * @param index The invalid index.
	 * @throws IndexOutOfBoundsException Always.
	 */
	private final void throwException3(int fromIndex, int toIndex)
								throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Index range [" +
						fromIndex + ", " + toIndex +
						"] not in valid range [0-" + (size-1) + "]");
	}


}

}