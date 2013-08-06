/*
 * 08/13/2004
 *
 * RTextAreaEditorKit.java - The editor kit used by RTextArea.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rtextarea;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Reader;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;


/**
 * An extension of <code>DefaultEditorKit</code> that adds functionality found
 * in <code>RTextArea</code>.
 *
 * @author Robert Futrell
 * @version 0.1
 */
// FIXME:  Replace Utilities calls with custom versions (in RSyntaxUtilities) to
// cut down on all of the modelToViews, as each call causes
// a getTokenList => expensive!
public class RTextAreaEditorKit extends DefaultEditorKit {

	/**
	 * The name of the action for "redoing" the last action undone.
	 */
	public static final String rtaRedoAction				= "RTA.RedoAction";

	/**
	 * The name of the action for "undoing" the last action done.
	 */
	public static final String rtaUndoAction				= "RTA.UndoAction";

	/**
	 * The actions that <code>RTextAreaEditorKit</code> adds to those of
	 * the default editor kit.
	 */
	private static final RecordableTextAction[] defaultActions = {
		new BeginAction(beginAction, false), 
		new BeginAction(selectionBeginAction, true), 
		new BeginLineAction(beginLineAction, false),  
		new BeginLineAction(selectionBeginLineAction, true),  
		new BeginWordAction(beginWordAction, false),
		new BeginWordAction(selectionBeginWordAction, true),
		new CopyAction(),
		new CutAction(),
		new DeleteNextCharAction(),
		new DeletePrevCharAction(),
		new EndAction(endAction, false),
		new EndAction(selectionEndAction, true),
		new EndLineAction(endLineAction, false),
		new EndLineAction(selectionEndLineAction, true),
		new EndWordAction(endWordAction, false),
		new EndWordAction(endWordAction, true),
		new NextVisualPositionAction(forwardAction, false, SwingConstants.EAST),
		new NextVisualPositionAction(backwardAction, false, SwingConstants.WEST),
		new NextVisualPositionAction(selectionForwardAction, true, SwingConstants.EAST),
		new NextVisualPositionAction(selectionBackwardAction, true, SwingConstants.WEST),
		new NextVisualPositionAction(upAction, false, SwingConstants.NORTH),
		new NextVisualPositionAction(downAction, false, SwingConstants.SOUTH),
		new NextVisualPositionAction(selectionUpAction, true, SwingConstants.NORTH),
		new NextVisualPositionAction(selectionDownAction, true, SwingConstants.SOUTH),
		new InsertBreakAction(),
		new InsertTabAction(),
		new PasteAction(),
		new RedoAction(),
		new SelectAllAction(),
		new SelectLineAction(),
		new SelectWordAction(),
		new UndoAction()
	};

	/**
	 * The amount of characters read at a time when reading a file.
	 */
	private static final int READBUFFER_SIZE	= 32768;


	/**
	 * Constructor.
	 */
	public RTextAreaEditorKit() {
		super();
	}

	/**
	 * Creates a line number list to use in the gutter for a text area.
	 *
	 * @param textArea The text area.
	 * @return The line number list.
	 */
	public LineNumberList createLineNumberList(RTextArea textArea) {
		return new LineNumberList(textArea);
	}


	/**
	 * Fetches the set of commands that can be used
	 * on a text component that is using a model and
	 * view produced by this kit.
	 *
	 * @return the command list
	 */ 
	public Action[] getActions() {
		return defaultActions;
	}


	/**
	 * Inserts content from the given stream, which will be 
	 * treated as plain text.  This method is overridden merely
	 * so we can increase the number of characters read at a time.
	 * 
	 * @param in  The stream to read from
	 * @param doc The destination for the insertion.
	 * @param pos The location in the document to place the
	 *   content >= 0.
	 * @exception IOException on any I/O error
	 * @exception BadLocationException if pos represents an invalid
	 *   location within the document.
	*/
	public void read(Reader in, Document doc, int pos) 
				throws IOException, BadLocationException {

		char[] buff = new char[READBUFFER_SIZE];
		int nch;
		boolean lastWasCR = false;
		boolean isCRLF = false;
		boolean isCR = false;
		int last;
		boolean wasEmpty = (doc.getLength() == 0);

		// Read in a block at a time, mapping \r\n to \n, as well as single
		// \r's to \n's. If a \r\n is encountered, \r\n will be set as the
		// newline string for the document, if \r is encountered it will
		// be set as the newline character, otherwise the newline property
		// for the document will be removed.
		while ((nch = in.read(buff, 0, buff.length)) != -1) {
			last = 0;
			for (int counter = 0; counter < nch; counter++) {
				switch (buff[counter]) {
					case '\r':
						if (lastWasCR) {
							isCR = true;
							if (counter == 0) {
								doc.insertString(pos, "\n", null);
								pos++;
							}
							else {
								buff[counter - 1] = '\n';
							}
						}
						else {
							lastWasCR = true;
						}
						break;
					case '\n':
						if (lastWasCR) {
							if (counter > (last + 1)) {
								doc.insertString(pos, new String(buff, last,
												counter - last - 1), null);
								pos += (counter - last - 1);
							}
							// else nothing to do, can skip \r, next write will
							// write \n
							lastWasCR = false;
							last = counter;
							isCRLF = true;
						}
						break;
					default:
						if (lastWasCR) {
							isCR = true;
							if (counter == 0) {
								doc.insertString(pos, "\n", null);
								pos++;
							}
							else {
								buff[counter - 1] = '\n';
							}
							lastWasCR = false;
						}
						break;
				} // End of switch (buff[counter]).
			} // End of for (int counter = 0; counter < nch; counter++).

			if (last < nch) {
				if(lastWasCR) {
					if (last < (nch - 1)) {
						doc.insertString(pos, new String(buff, last,
										nch - last - 1), null);
						pos += (nch - last - 1);
					}
				}
				else {
					doc.insertString(pos, new String(buff, last,
									nch - last), null);
					pos += (nch - last);
				}
			}

		} // End of while ((nch = in.read(buff, 0, buff.length)) != -1).

		if (lastWasCR) {
			doc.insertString(pos, "\n", null);
			isCR = true;
		}

		if (wasEmpty) {
			if (isCRLF) {
				doc.putProperty(EndOfLineStringProperty, "\r\n");
			}
			else if (isCR) {
				doc.putProperty(EndOfLineStringProperty, "\r");
			}
			else {
				doc.putProperty(EndOfLineStringProperty, "\n");
			}
		}

	}

	/**
	 * Moves the caret to the beginning of the document.
	 */
	public static class BeginAction extends RecordableTextAction {

 		private boolean select;

		public BeginAction(String name, boolean select) {
			super(name);
			this.select = select;
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			if (select)
				textArea.moveCaretPosition(0);
			else
				textArea.setCaretPosition(0);
		}

		public final String getMacroID() {
			return getName();
		}

	}


	/**
	 * Toggles the position of the caret between the beginning of the line,
	 * and the first non-whitespace character on the line.
	 */
	public static class BeginLineAction extends RecordableTextAction {

 		private Segment currentLine = new Segment(); // For speed.
 		private boolean select;

		public BeginLineAction(String name, boolean select) {
			super(name);
			this.select = select;
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {

			int newPos = 0;

			try {

				// Is line wrap enabled?
				if (textArea.getLineWrap()) {
					int offs = textArea.getCaretPosition();
					// TODO:  Replace Utilities call with custom version
					// to cut down on all of the modelToViews, as each call
					// causes TokenList => expensive!
					int begOffs = Utilities.getRowStart(textArea, offs);
					// TODO: line wrap doesn't currently toggle between
					// the first non-whitespace char and the actual start
					// of the line line the no-line-wrap version does.
					newPos = begOffs;
				}

				// No line wrap - optimized for performance!
				else {

					// We use the elements instead of calling
					// getLineOfOffset(), etc. to speed things up just a
					// tad (i.e. micro-optimize).
					int caretPosition = textArea.getCaretPosition();
					Document document = textArea.getDocument();
					Element map = document.getDefaultRootElement();
					int currentLineNum = map.getElementIndex(caretPosition);
					Element currentLineElement = map.getElement(currentLineNum);
					int currentLineStart = currentLineElement.getStartOffset();
					int currentLineEnd = currentLineElement.getEndOffset();
					int count = currentLineEnd - currentLineStart;
					if (count>0) { // If there are chars in the line...
						document.getText(currentLineStart, count, currentLine);
						int firstNonWhitespace = getFirstNonWhitespacePos();
						firstNonWhitespace = currentLineStart +
								(firstNonWhitespace - currentLine.offset);
						if (caretPosition!=firstNonWhitespace) {
							newPos = firstNonWhitespace;
						}
						else {
							newPos = currentLineStart;
						}
					}
					else { // Empty line (at end of the document only).
						newPos = currentLineStart;
					}

				}

				if (select) {
					textArea.moveCaretPosition(newPos);
				}
				else {
					textArea.setCaretPosition(newPos);
				}
				//e.consume();

			} catch (BadLocationException ble) {
				/* Shouldn't ever happen. */
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				ble.printStackTrace();
			}

		}

		private final int getFirstNonWhitespacePos() {
			int offset = currentLine.offset;
			int end = offset + currentLine.count - 1;
			int pos = offset;
			char[] array = currentLine.array;
			char currentChar = array[pos];
			while ((currentChar=='\t' || currentChar==' ') && (++pos<end))
				currentChar = array[pos];
			return pos;
		}

		public final String getMacroID() {
			return getName();
		}

	}


	/**
	 * Positions the caret at the beginning of the word.
	 */
	protected static class BeginWordAction extends RecordableTextAction {

 		private boolean select;

		protected BeginWordAction(String name, boolean select) {
			super(name);
			this.select = select;
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			try {
				int offs = textArea.getCaretPosition();
				int begOffs = getWordStart(textArea, offs);
				if (select)
					textArea.moveCaretPosition(begOffs);
				else
					textArea.setCaretPosition(begOffs);
			} catch (BadLocationException ble) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
		}

		public final String getMacroID() {
			return getName();
		}

		protected int getWordStart(RTextArea textArea, int offs)
										throws BadLocationException {
			return Utilities.getWordStart(textArea, offs);
		}

	}


	/**
	 * Action for copying text.
	 */
	public static class CopyAction extends RecordableTextAction {

 
		public CopyAction() {
			super(DefaultEditorKit.copyAction);
		}

		public CopyAction(String name, Icon icon, String desc,
					Integer mnemonic, KeyStroke accelerator) {
			super(name, icon, desc, mnemonic, accelerator);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			textArea.copy();
			textArea.requestFocusInWindow();
		}

		public final String getMacroID() {
			return DefaultEditorKit.copyAction;
		}

	}


	/**
	 * Action for cutting text.
	 */
	public static class CutAction extends RecordableTextAction {

		public CutAction() {
			super(DefaultEditorKit.cutAction);
		}

		public CutAction(String name, Icon icon, String desc,
					Integer mnemonic, KeyStroke accelerator) {
			super(name, icon, desc, mnemonic, accelerator);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			textArea.cut();
			textArea.requestFocusInWindow();
		}

		public final String getMacroID() {
			return DefaultEditorKit.cutAction;
		}

	}

	/**
	 * Deletes the character of content that follows the current caret
	 * position.
	 */
	public static class DeleteNextCharAction extends RecordableTextAction {

		public DeleteNextCharAction() {
			super(DefaultEditorKit.deleteNextCharAction, null, null,
												null, null);
		}

		public DeleteNextCharAction(String name, Icon icon, String desc,
					Integer mnemonic, KeyStroke accelerator) {
			super(name, icon, desc, mnemonic, accelerator);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {

			boolean beep = true;
			if ((textArea != null) && (textArea.isEditable())) {
				try {
					Document doc = textArea.getDocument();
					Caret caret = textArea.getCaret();
					int dot = caret.getDot();
					int mark = caret.getMark();
					if (dot != mark) {
						doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
						beep = false;
					}
					else if (dot < doc.getLength()) {
						int delChars = 1;
						if (dot < doc.getLength() - 1) {
							String dotChars = doc.getText(dot, 2);
							char c0 = dotChars.charAt(0);
							char c1 = dotChars.charAt(1);
							if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
								c1 >= '\uDC00' && c1 <= '\uDFFF') {
								delChars = 2;
							}
						}
						doc.remove(dot, delChars);
						beep = false;
					}
				} catch (BadLocationException bl) {
				}
			}

			if (beep)
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);

			textArea.requestFocusInWindow();

		}

		public final String getMacroID() {
			return DefaultEditorKit.deleteNextCharAction;
		}

	}


	/**
	 * Deletes the character of content that precedes the current caret
	 * position.
	 */
	public static class DeletePrevCharAction extends RecordableTextAction {
 
		public DeletePrevCharAction() {
			super(deletePrevCharAction);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {

			boolean beep = true;
			if ((textArea != null) && (textArea.isEditable())) {
				try {
					Document doc = textArea.getDocument();
					Caret caret = textArea.getCaret();
					int dot = caret.getDot();
					int mark = caret.getMark();
					if (dot != mark) {
						doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
						beep = false;
					}
					else if (dot > 0) {
						int delChars = 1;
						if (dot > 1) {
							String dotChars = doc.getText(dot - 2, 2);
							char c0 = dotChars.charAt(0);
							char c1 = dotChars.charAt(1);
							if (c0 >= '\uD800' && c0 <= '\uDBFF' &&
								c1 >= '\uDC00' && c1 <= '\uDFFF') {
								delChars = 2;
							}
						}
						doc.remove(dot - delChars, delChars);
						beep = false;
					}
				} catch (BadLocationException bl) {
				}
			}

			if (beep)
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);

		}

		public final String getMacroID() {
			return DefaultEditorKit.deletePrevCharAction;
		}

	}


	/**
	 * Moves the caret to the end of the document.
	 */
	public static class EndAction extends RecordableTextAction {

 		private boolean select;

		public EndAction(String name, boolean select) {
			super(name);
			this.select = select;
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			int dot = getVisibleEnd(textArea);
			if (select)
				textArea.moveCaretPosition(dot);
			else
				textArea.setCaretPosition(dot);
		}

		public final String getMacroID() {
			return getName();
		}

		protected int getVisibleEnd(RTextArea textArea) {
			return textArea.getDocument().getLength();
		}

	}


	/**
	 * Positions the caret at the end of the line.
	 */
	public static class EndLineAction extends RecordableTextAction {

 		private boolean select;

		public EndLineAction(String name, boolean select) {
			super(name);
			this.select = select;
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			int offs = textArea.getCaretPosition();
			int endOffs = 0;
			try {
				if (textArea.getLineWrap()) {
					// Must check per character, since one logical line may be
					// many physical lines.
					// FIXME:  Replace Utilities call with custom version to
					// cut down on all of the modelToViews, as each call causes
					// a getTokenList => expensive!
					endOffs = Utilities.getRowEnd(textArea, offs);
				}
				else {
					Element root = textArea.getDocument().getDefaultRootElement();
					int line = root.getElementIndex(offs);
					endOffs = root.getElement(line).getEndOffset() - 1;
				}
				if (select) {
					textArea.moveCaretPosition(endOffs);
				}
				else {
					textArea.setCaretPosition(endOffs);
				}
			} catch (Exception ex) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
		}

		public final String getMacroID() {
			return getName();
		}

	}


	/**
	 * Positions the caret at the end of the word.
	 */
	protected static class EndWordAction extends RecordableTextAction {

 		private boolean select;

		protected EndWordAction(String name, boolean select) {
			super(name);
			this.select = select;
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			try {
				int offs = textArea.getCaretPosition();
				int endOffs = getWordEnd(textArea, offs);
				if (select)
					textArea.moveCaretPosition(endOffs);
				else
					textArea.setCaretPosition(endOffs);
			} catch (BadLocationException ble) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
		}

		public final String getMacroID() {
			return getName();
		}

		protected int getWordEnd(RTextArea textArea, int offs)
									throws BadLocationException {
			return Utilities.getWordEnd(textArea, offs);
		}

	}

	/**
	 * Action for when the user presses the Enter key.
	 */
	public static class InsertBreakAction extends RecordableTextAction {

 
		public InsertBreakAction() {
			super(DefaultEditorKit.insertBreakAction);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			if (!textArea.isEditable() || !textArea.isEnabled()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				return;
			}
			textArea.replaceSelection("\n");
		}

		public final String getMacroID() {
			return DefaultEditorKit.insertBreakAction;
		}

		/*
		 * Overridden for Sun bug 4515750.  Sun fixed this in a more complicated
		 * way, but I'm not sure why.  See BasicTextUI#getActionMap() and
		 * BasicTextUI.TextActionWrapper.
		 */
		public boolean isEnabled() {
			JTextComponent tc = getTextComponent(null);
			return (tc==null || tc.isEditable()) ? super.isEnabled() : false;
		}

	}

	/**
	 * Places a tab character into the document. If there is a selection, it
	 * is removed before the tab is added.
	 */
	public static class InsertTabAction extends RecordableTextAction {

 
		public InsertTabAction() {
			super(insertTabAction);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			if (!textArea.isEditable() || !textArea.isEnabled()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				return;
			}
			textArea.replaceSelection("\t");
		}

		public final String getMacroID() {
			return DefaultEditorKit.insertTabAction;
		}

	}

	/**
	 * Action to move the selection and/or caret. Constructor indicates
	 * direction to use.
	 */
	public static class NextVisualPositionAction extends RecordableTextAction {

		private boolean select;
		private int direction;

		public NextVisualPositionAction(String nm, boolean select, int dir) {
			super(nm);
			this.select = select;
			this.direction = dir;
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {

			Caret caret = textArea.getCaret();
			int dot = caret.getDot();

			/*
			 * Move to the beginning/end of selection on a "non-shifted"
			 * left- or right-keypress.  We shouldn't have to worry about
			 * navigation filters as, if one is being used, it let us get
			 * to that position before.
			 */
			if (!select) {
				switch (direction) {
					case SwingConstants.EAST:
						int mark = caret.getMark();
						if (dot!=mark) {
							caret.setDot(Math.max(dot, mark));
							return;
						}
						break;
					case SwingConstants.WEST:
						mark = caret.getMark();
						if (dot!=mark) {
							caret.setDot(Math.min(dot, mark));
							return;
						}
						break;
					default:
				}
			}

			Position.Bias[] bias = new Position.Bias[1];
			Point magicPosition = caret.getMagicCaretPosition();

			try {

				if(magicPosition == null &&
					(direction == SwingConstants.NORTH ||
					direction == SwingConstants.SOUTH)) {
					Rectangle r = textArea.modelToView(dot);
					magicPosition = new Point(r.x, r.y);
				}

				NavigationFilter filter = textArea.getNavigationFilter();

				if (filter != null) {
					dot = filter.getNextVisualPositionFrom(textArea, dot,
								Position.Bias.Forward, direction, bias);
				}
				else {
					dot = textArea.getUI().getNextVisualPositionFrom(
								textArea, dot,
								Position.Bias.Forward, direction, bias);
				}
				if (select)
					caret.moveDot(dot);
				else
					caret.setDot(dot);

				if(magicPosition != null &&
					(direction == SwingConstants.NORTH ||
					direction == SwingConstants.SOUTH)) {
						caret.setMagicCaretPosition(magicPosition);
				}

			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}

		}

		public final String getMacroID() {
			return getName();
		}

    }

	/**
	 * Action for pasting text.
	 */
	public static class PasteAction extends RecordableTextAction {

 
		public PasteAction() {
			super(DefaultEditorKit.pasteAction);
		}

		public PasteAction(String name, Icon icon, String desc,
					Integer mnemonic, KeyStroke accelerator) {
			super(name, icon, desc, mnemonic, accelerator);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			textArea.paste();
			textArea.requestFocusInWindow();
		}

		public final String getMacroID() {
			return DefaultEditorKit.pasteAction;
		}

	}

	/**
	 * Re-does the last action undone.
	 */
	public static class RedoAction extends RecordableTextAction {

 
		public RedoAction() {
			super(rtaRedoAction);
		}

		public RedoAction(String name, Icon icon, String desc,
					Integer mnemonic, KeyStroke accelerator) {
			super(name, icon, desc, mnemonic, accelerator);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			if (textArea.isEnabled() && textArea.isEditable()) {
				textArea.redoLastAction();
				textArea.requestFocusInWindow();
			}
		}

		public final String getMacroID() {
			return rtaRedoAction;
		}

	}

	/**
	 * Selects the entire document.
	 */
	public static class SelectAllAction extends RecordableTextAction {

 
		public SelectAllAction() {
			super(selectAllAction);
		}

		public SelectAllAction(String name, Icon icon, String desc,
					Integer mnemonic, KeyStroke accelerator) {
			super(name, icon, desc, mnemonic, accelerator);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			Document doc = textArea.getDocument();
			textArea.setCaretPosition(0);
			textArea.moveCaretPosition(doc.getLength());
		}

		public final String getMacroID() {
			return DefaultEditorKit.selectAllAction;
		}

	}


	/**
	 * Selects the line around the caret.
	 */
	public static class SelectLineAction extends RecordableTextAction {

 		private Action start;
		private Action end;

		public SelectLineAction() {
			super(selectLineAction);
			start = new BeginLineAction("pigdog", false);
			end = new EndLineAction("pigdog", true);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			start.actionPerformed(e);
			end.actionPerformed(e);
		}

		public final String getMacroID() {
			return DefaultEditorKit.selectLineAction;
		}

	}


	/**
	 * Selects the word around the caret.
	 */
	public static class SelectWordAction extends RecordableTextAction {

 		protected Action start;
		protected Action end;

		public SelectWordAction() {
			super(selectWordAction);
			createActions();
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			start.actionPerformed(e);
			end.actionPerformed(e);
		}

		protected void createActions() {
			start = new BeginWordAction("pigdog", false);
			end = new EndWordAction("pigdog", true);
		}

		public final String getMacroID() {
			return DefaultEditorKit.selectWordAction;
		}

	}

	/**
	 * Undoes the last action done.
	 */
	public static class UndoAction extends RecordableTextAction {

 		public UndoAction() {
			super(rtaUndoAction);
		}

		public UndoAction(String name, Icon icon, String desc,
					Integer mnemonic, KeyStroke accelerator) {
			super(name, icon, desc, mnemonic, accelerator);
		}

		public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
			if (textArea.isEnabled() && textArea.isEditable()) {
				textArea.undoLastAction();
				textArea.requestFocusInWindow();
			}
		}

		public final String getMacroID() {
			return rtaUndoAction;
		}

	}

}