/*
 * 09/26/2005
 *
 * ParserManager.java - Manages the parsing of an RSyntaxTextArea's document,
 * if necessary.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.parser.Parser;



/**
 * Manages running a parser object for an <code>RSyntaxTextArea</code>.
 *
 * @author Robert Futrell
 * @version 0.9
 */
class ParserManager implements DocumentListener, ActionListener {

	private RSyntaxTextArea textArea;
	private List parsers;
	private Timer timer;
	private boolean running;
	
	/**
	 * Mapping of notices to their highlights in the editor.  Can't use a Map
	 * since parsers could return two <code>ParserNotice</code>s that compare
	 * equally via <code>equals()</code>.  Real-world example:  The Perl
	 * compiler will return 2+ identical error messages if the same error is
	 * committed in a single line more than once.
	 */
	private List noticeHighlightPairs;

	/**
	 * The default delay between the last key press and when the document
	 * is parsed, in milliseconds.
	 */
	private static final int DEFAULT_DELAY_MS		= 100;


	/**
	 * Constructor.
	 *
	 * @param textArea The text area whose document the parser will be
	 *        parsing.
	 */
	public ParserManager(RSyntaxTextArea textArea) {
		this(DEFAULT_DELAY_MS, textArea);
	}


	/**
	 * Constructor.
	 *
	 * @param delay The delay between the last key press and when the document
	 *        is parsed.
	 * @param textArea The text area whose document the parser will be
	 *        parsing.
	 */
	public ParserManager(int delay, RSyntaxTextArea textArea) {
		this.textArea = textArea;
		textArea.getDocument().addDocumentListener(this);
		parsers = new ArrayList(1); // Usually small
		timer = new Timer(delay, this);
		timer.setRepeats(false);
		running = true;
	}


	/**
	 * Called when the timer fires (e.g. it's time to parse the document).
	 * 
	 * @param e The event.
	 */
	public void actionPerformed(ActionEvent e) {

		// Sanity check - should have >1 parser if event is fired.
		int parserCount = getParserCount();
		if (parserCount==0) {
			return;
		}

		RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();

		String style = textArea.getSyntaxEditingStyle();
		doc.readLock();
		try {
			for (int i=0; i<parserCount; i++) {
				Parser parser = getParser(i);
				if (parser.isEnabled()) {
					parser.parse(doc, style);
				}
			}
		} finally {
			doc.readUnlock();
		}

	}


	/**
	 * Adds a parser for the text area.
	 *
	 * @param parser The new parser.  If this is <code>null</code>, nothing
	 *        happens.
	 * @see #getParser(int)
	 * @see #removeParser(Parser)
	 */
	public void addParser(Parser parser) {
		if (parser!=null && !parsers.contains(parser)) {
			if (running) {
				timer.stop();
			}
			parsers.add(parser);
			if (parsers.size()==1) {
				// Okay to call more than once.
				ToolTipManager.sharedInstance().registerComponent(textArea);
			}
			if (running) {
				timer.restart();
			}
		}
	}

	/**
	 * Called when the document is modified.
	 *
	 * @param e The document event.
	 */
	public void changedUpdate(DocumentEvent e) {
	}


	private void clearParserNoticeHighlights() {
		RSyntaxTextAreaHighlighter h = (RSyntaxTextAreaHighlighter)
											textArea.getHighlighter();
		if (h!=null) {
			h.clearParserHighlights();
		}
		if (noticeHighlightPairs!=null) {
			noticeHighlightPairs.clear();
		}
	}


	/**
	 * Removes all parsers and any highlights they have created.
	 *
	 * @see #addParser(Parser)
	 */
	public void clearParsers() {
		timer.stop();
		clearParserNoticeHighlights();
		parsers.clear();
	}

	/**
	 * Returns the delay between the last "concurrent" edit and when the
	 * document is re-parsed.
	 *
	 * @return The delay, in milliseconds.
	 * @see #setDelay(int)
	 */
	public int getDelay() {
		return timer.getDelay();
	}


	/**
	 * Returns the specified parser.
	 *
	 * @param index The index of the parser.
	 * @return The parser.
	 * @see #getParserCount()
	 * @see #addParser(Parser)
	 * @see #removeParser(Parser)
	 */
	public Parser getParser(int index) {
		return (Parser)parsers.get(index);
	}


	/**
	 * Returns the number of registered parsers.
	 *
	 * @return The number of registered parsers.
	 */
	public int getParserCount() {
		return parsers.size();
	}

	/**
	 * Called when the document is modified.
	 *
	 * @param e The document event.
	 */
	public void handleDocumentEvent(DocumentEvent e) {
		if (running && parsers.size()>0) {
			timer.restart();
		}
	}

	/**
	 * Called when the document is modified.
	 *
	 * @param e The document event.
	 */
	public void insertUpdate(DocumentEvent e) {
		handleDocumentEvent(e);
	}


	/**
	 * Removes a parser.
	 *
	 * @param parser The parser to remove.
	 * @return Whether the parser was found.
	 * @see #addParser(Parser)
	 * @see #getParser(int)
	 */
	public boolean removeParser(Parser parser) {
		boolean removed = parsers.remove(parser);
		return removed;
	}

	/**
	 * Called when the document is modified.
	 *
	 * @param e The document event.
	 */
	public void removeUpdate(DocumentEvent e) {
		handleDocumentEvent(e);
	}


	/**
	 * Restarts parsing the document.
	 *
	 * @see #stopParsing()
	 */
	public void restartParsing() {
		timer.restart();
		running = true;
	}


	/**
	 * Sets the delay between the last "concurrent" edit and when the document
	 * is re-parsed.
	 *
	 * @param millis The new delay, in milliseconds.  This must be greater
	 *        than <code>0</code>.
	 * @see #getDelay()
	 */
	public void setDelay(int millis) {
		if (running) {
			timer.stop();
		}
		timer.setDelay(millis);
		if (running) {
			timer.start();
		}
	}


	/**
	 * Stops parsing the document.
	 *
	 * @see #restartParsing()
	 */
	public void stopParsing() {
		timer.stop();
		running = false;
	}


}