/*
 * 11/14/2003
 *
 * RTextArea.java - An extension of JTextArea that adds many features.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rtextarea;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


/**
 * An extension of <code>JTextArea</code> that adds the following features:
 * <ul>
 *    <li>Insert/Overwrite modes (can be toggled via the Insert key)
 *    <li>A right-click popup menu with standard editing options
 *    <li>Macro support
 *    <li>"Mark all" functionality.
 *    <li>A way to change the background to an image (gif/png/jpg)
 *    <li>Highlight the current line (can be toggled)
 *    <li>An easy way to print its text (implements Printable)
 *    <li>Hard/soft (emulated with spaces) tabs
 *    <li>Fixes a bug with setTabSize
 *    <li>Other handy new methods
 * </ul>
 * NOTE:  If the background for an <code>RTextArea</code> is set to a color,
 * its opaque property is set to <code>true</code> for performance reasons.  If
 * the background is set to an image, then the opaque property is set to
 * <code>false</code>.  This slows things down a little, but if it didn't happen
 * then we would see garbage on-screen when the user scrolled through a document
 * using the arrow keys (not the page-up/down keys though).  You should never
 * have to set the opaque property yourself; it is always done for you.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class RTextArea extends RTextAreaBase
								implements Serializable {

	/**
	 * Constant representing insert mode.
	 *
	 * @see #setCaretStyle(int, int)
	 */
	public static final int INSERT_MODE				= 0;

	/**
	 * Constant representing overwrite mode.
	 *
	 * @see #setCaretStyle(int, int)
	 */
	public static final int OVERWRITE_MODE				= 1;


	/*
	 * Constants for all actions.
	 */
	private static final int MIN_ACTION_CONSTANT	= 0;
	public static final int COPY_ACTION				= 0;
	public static final int CUT_ACTION				= 1;
	public static final int DELETE_ACTION			= 2;
	public static final int PASTE_ACTION			= 3;
	public static final int REDO_ACTION				= 4;
	public static final int SELECT_ALL_ACTION		= 5;
	public static final int UNDO_ACTION				= 6;
	private static final int MAX_ACTION_CONSTANT	= 6;

	private static final Color DEFAULT_MARK_ALL_COLOR		= Color.ORANGE;

	/**
	 * The current text mode ({@link #INSERT_MODE} or {@link #OVERWRITE_MODE}).
	 */
	private int textMode;

	private static RecordableTextAction cutAction;
	private static RecordableTextAction copyAction;
	private static RecordableTextAction pasteAction;
	private static RecordableTextAction deleteAction;
	private static RecordableTextAction undoAction;
	private static RecordableTextAction redoAction;
	private static RecordableTextAction selectAllAction;

	private transient RUndoManager undoManager;

	private transient LineHighlightManager lineHighlightManager;

	private int[] carets;		// Index 0=>insert caret, 1=>overwrite.

	/**
	 * Constructor.
	 */
	public RTextArea() {
		init(INSERT_MODE);
	}


	/**
	 * Constructor.
	 *
	 * @param doc The document for the editor.
	 */
	public RTextArea(AbstractDocument doc) {
		super(doc);
		init(INSERT_MODE);
	}


	/**
	 * Constructor.
	 *
	 * @param text The initial text to display.
	 */
	public RTextArea(String text) {
		super(text);
		init(INSERT_MODE);
	}


	/**
	 * Constructor.
	 *
	 * @param rows The number of rows to display.
	 * @param cols The number of columns to display.
	 * @throws IllegalArgumentException If either <code>rows</code> or
	 *         <code>cols</code> is negative.
	 */
	public RTextArea(int rows, int cols) {
		super(rows, cols);
		init(INSERT_MODE);
	}


	/**
	 * Constructor.
	 *
	 * @param text The initial text to display.
	 * @param rows The number of rows to display.
	 * @param cols The number of columns to display.
	 * @throws IllegalArgumentException If either <code>rows</code> or
	 *         <code>cols</code> is negative.
	 */
	public RTextArea(String text, int rows, int cols) {
		super(text, rows, cols);
		init(INSERT_MODE);
	}


	/**
	 * Constructor.
	 *
	 * @param doc The document for the editor.
	 * @param text The initial text to display.
	 * @param rows The number of rows to display.
	 * @param cols The number of columns to display.
	 * @throws IllegalArgumentException If either <code>rows</code> or
	 *         <code>cols</code> is negative.
	 */
	public RTextArea(AbstractDocument doc, String text, int rows, int cols) {
		super(doc, text, rows, cols);
		init(INSERT_MODE);
	}


	/**
	 * Creates a new <code>RTextArea</code>.
	 *
	 * @param textMode Either <code>INSERT_MODE</code> or
	 *        <code>OVERWRITE_MODE</code>.
	 */
	public RTextArea(int textMode) {
		init(textMode);
	}

	/**
	 * Adds a line highlight.
	 *
	 * @param line The line to highlight.  This is zero-based.
	 * @param color The color to highlight the line with.
	 * @throws BadLocationException If <code>line</code> is an invalid line
	 *         number.
	 * @see #removeLineHighlight(Object)
	 * @see #removeAllLineHighlights()
	 */
	public Object addLineHighlight(int line, Color color)
										throws BadLocationException {
		if (lineHighlightManager==null) {
			lineHighlightManager = new LineHighlightManager(this);
		}
		return lineHighlightManager.addLineHighlight(line, color);
	}


	/**
	 * Begins an "atomic edit."  All text editing operations between this call
	 * and the next call to <tt>endAtomicEdit()</tt> will be treated as a
	 * single operation by the undo manager.<p>
	 *
	 * Using this method should be done with great care.  You should probably
	 * wrap the call to <tt>endAtomicEdit()</tt> in a <tt>finally</tt> block:
	 *
	 * <pre>
	 * textArea.beginAtomicEdit();
	 * try {
	 *    // Do editing
	 * } finally {
	 *    textArea.endAtomicEdit();
	 * }
	 * </pre>
	 *
	 * @see #endAtomicEdit()
	 */
	public void beginAtomicEdit() {
		undoManager.beginInternalAtomicEdit();
	}

	/**
	 * Tells whether an undo is possible
	 * 
	 * @see #canRedo()
	 * @see #undoLastAction()
	 */
	public boolean canUndo() {
		return undoManager.canUndo();
	}


	/**
	 * Tells whether a redo is possible
	 * 
	 * @see #canUndo()
	 * @see #redoLastAction()
	 */
	public boolean canRedo() {
		return undoManager.canRedo();
	}


	/**
	 * Returns the caret event/mouse listener for <code>RTextArea</code>s.
	 *
	 * @return The caret event/mouse listener.
	 */
	protected RTAMouseListener createMouseListener() {
		return new RTextAreaMutableCaretEvent(this);
	}

	/**
	 * Creates the actions used in the popup menu and retrievable by
	 * {@link #getAction(int)}.
	 * TODO: Remove these horrible hacks and move localizing of actions into
	 * the editor kits, where it should be!  The context menu should contain
	 * actions from the editor kits.
	 */
	private static void createPopupMenuActions() {

		// Create actions for right-click popup menu.
		// 1.5.2004/pwy: Replaced the CTRL_MASK with the cross-platform version...
		int mod = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		cutAction = new RTextAreaEditorKit.CutAction();
		cutAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, mod));
		copyAction = new RTextAreaEditorKit.CopyAction();
		copyAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mod));
		pasteAction = new RTextAreaEditorKit.PasteAction();
		pasteAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mod));
		deleteAction = new RTextAreaEditorKit.DeleteNextCharAction();
		deleteAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		undoAction = new RTextAreaEditorKit.UndoAction();
		undoAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mod));
		redoAction = new RTextAreaEditorKit.RedoAction();
		redoAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, mod));
		selectAllAction = new RTextAreaEditorKit.SelectAllAction();
		selectAllAction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, mod));

	}

	/**
	 * Returns the a real UI to install on this text area.
	 *
	 * @return The UI.
	 */
	protected RTextAreaUI createRTextAreaUI() {
		return new RTextAreaUI(this);
	}


	/**
	 * Creates an undo manager for use in this text area.
	 *
	 * @return The undo manager.
	 */
	protected RUndoManager createUndoManager() {
		return new RUndoManager(this);
	}


	/**
	 * Removes all undoable edits from this document's undo manager.  This
	 * method also makes the undo/redo actions disabled.
	 */
	/*
	 * NOTE:  For some reason, it appears I have to create an entirely new
	 *        <code>undoManager</code> for undo/redo to continue functioning
	 *        properly; if I don't, it only ever lets you do one undo.  Not
	 *        too sure why this is...
	 */
	public void discardAllEdits() {
		undoManager.discardAllEdits();
		getDocument().removeUndoableEditListener(undoManager);
		undoManager = createUndoManager();
		getDocument().addUndoableEditListener(undoManager);
		undoManager.updateActions();
	}


	/**
	 * Completes an "atomic" edit.
	 *
	 * @see #beginAtomicEdit()
	 */
	public void endAtomicEdit() {
		undoManager.endInternalAtomicEdit();
	}

	/**
	 * Notifies all listeners that a caret change has occurred.
	 *
	 * @param e The caret event.
	 */
	protected void fireCaretUpdate(CaretEvent e) {

		// Decide whether we need to repaint the current line background.
		possiblyUpdateCurrentLineHighlightLocation();

		// Now, if there is a highlighted region of text, allow them to cut
		// and copy.
		if (e!=null && e.getDot()!=e.getMark()) {// && !cutAction.isEnabled()) {
			cutAction.setEnabled(true);
			copyAction.setEnabled(true);
		}

		// Otherwise, if there is no highlighted region, don't let them cut
		// or copy.  The condition here should speed things up, because this
		// way, we will only enable the actions the first time the selection
		// becomes nothing.
		else if (cutAction.isEnabled()) {
			cutAction.setEnabled(false);
			copyAction.setEnabled(false);
		}

		super.fireCaretUpdate(e);

	}


	/**
	 * Removes the "Ctrl+H <=> Backspace" behavior that Java shows, for some
	 * odd reason...
	 */
	private void fixCtrlH() {
		InputMap inputMap = getInputMap();
		KeyStroke char010 = KeyStroke.getKeyStroke("typed \010");
		InputMap parent = inputMap;
		while (parent != null) {
			parent.remove(char010);
			parent = parent.getParent();
		}
		KeyStroke backspace = KeyStroke.getKeyStroke("BACK_SPACE");
		inputMap.put(backspace, DefaultEditorKit.deletePrevCharAction);
	}


	/**
	 * Provides a way to gain access to the editor actions on the right-click
	 * popup menu.  This way you can make toolbar/menu bar items use the actual
	 * actions used by all <code>RTextArea</code>s, so that icons stay
	 * synchronized and you don't have to worry about enabling/disabling them
	 * yourself.<p>
	 * Keep in mind that these actions are shared across all instances of
	 * <code>RTextArea</code>, so a change to any action returned by this
	 * method is global across all <code>RTextArea</code> editors in your
	 * application.
	 *
	 * @param action The action to retrieve, such as {@link #CUT_ACTION}.
	 *        If the action name is invalid, <code>null</code> is returned.
	 * @return The action, or <code>null</code> if an invalid action is
	 *         requested.
	 */
	public static RecordableTextAction getAction(int action) {
		if (action<MIN_ACTION_CONSTANT || action>MAX_ACTION_CONSTANT)
			return null;
		switch (action) {
			case COPY_ACTION:
				return copyAction;
			case CUT_ACTION:
				return cutAction;
			case DELETE_ACTION:
				return deleteAction;
			case PASTE_ACTION:
				return pasteAction;
			case REDO_ACTION:
				return redoAction;
			case SELECT_ALL_ACTION:
				return selectAllAction;
			case UNDO_ACTION:
				return undoAction;
		}
		return null;
	}

	/**
	 * Returns the line highlight manager.
	 *
	 * @return The line highlight manager.  This may be <code>null</code>.
	 */
	LineHighlightManager getLineHighlightManager() {
		return lineHighlightManager;
	}


	/**
	 * Returns the maximum ascent of all fonts used in this text area.  In
	 * the case of a standard <code>RTextArea</code>, this is simply the
	 * ascent of the current font.<p>
	 *
	 * This value could be useful, for example, to implement a line-numbering
	 * scheme.
	 *
	 * @return The ascent of the current font.
	 */
	public int getMaxAscent() {
		return getFontMetrics(getFont()).getAscent();
	}

	/**
	 * Returns the text mode this editor pane is currently in.
	 *
	 * @return Either {@link #INSERT_MODE} or {@link #OVERWRITE_MODE}.
	 * @see #setTextMode(int)
	 */
	public final int getTextMode() {
		return textMode;
	}

	/**
	 * Does the actual dirty-work of replacing the selected text in this
	 * text area (i.e., in its document).  This method provides a hook for
	 * subclasses to handle this in a different way.
	 *
	 * @param content The content to add.
	 */
	protected void handleReplaceSelection(String content) {
		// Call into super to handle composed text (1.5+ only though).
		super.replaceSelection(content);
	}


	/**
	 * Initializes this text area.
	 *
	 * @param textMode The text mode.
	 */
	private void init(int textMode) {

		// NOTE: Our actions are created here instead of in a static block
		// so they are only created when the first RTextArea is instantiated,
		// not before.  There have been reports of users calling static getters
		// (e.g. RSyntaxTextArea.getDefaultBracketMatchBGColor()) which would
		// cause these actions to be created and (possibly) incorrectly
		// localized, if they were in a static block.
		if (cutAction==null) {
			createPopupMenuActions();
		}

		// Install the undo manager.
		undoManager = createUndoManager();
		getDocument().addUndoableEditListener(undoManager);

		carets = new int[2];
		setCaretStyle(INSERT_MODE, ConfigurableCaret.THICK_VERTICAL_LINE_STYLE);
		setCaretStyle(OVERWRITE_MODE, ConfigurableCaret.BLOCK_STYLE);
		setDragEnabled(true);			// Enable drag-and-drop.

		// Set values for stuff the user passed in.
		setTextMode(textMode); // carets array must be initialized first!

		// Fix the odd "Ctrl+H <=> Backspace" Java behavior.
		fixCtrlH();

	}

	/**
	 * {@inheritDoc}
	 */
	public void paste() {
		// Treat paste operations as atomic, otherwise the removal and
		// insertion are treated as two separate undo-able operations.
		beginAtomicEdit();
		try {
			super.paste();
		} finally {
			endAtomicEdit();
		}
	}

	/**
	 * We override this method because the super version gives us an entirely
	 * new <code>Document</code>, thus requiring us to re-attach our Undo
	 * manager.  With this version we just replace the text.
	 */
	public void read(Reader in, Object desc) throws IOException {

		RTextAreaEditorKit kit = (RTextAreaEditorKit)getUI().getEditorKit(this);
		setText(null);
		Document doc = getDocument();
		if (desc != null)
			doc.putProperty(Document.StreamDescriptionProperty, desc);
		try {
			// NOTE:  Resets the "line separator" property.
			kit.read(in, doc, 0);
		} catch (BadLocationException e) {
			throw new IOException(e.getMessage());
		}

	}


	/**
	 * De-serializes a text area.
	 *
	 * @param s The stream to read from.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream s)
						throws ClassNotFoundException, IOException {

		s.defaultReadObject();

		// UndoManagers cannot be serialized without Exceptions.  See
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4275892
		undoManager = createUndoManager();
		getDocument().addUndoableEditListener(undoManager);

		lineHighlightManager = null; // Keep FindBugs happy.

	}


	/**
	 * Attempt to redo the last action.
	 *
	 * @see #undoLastAction()
	 */
	public void redoLastAction() {
		// NOTE:  The try/catch block shouldn't be necessary...
		try {
			if (undoManager.canRedo())
				undoManager.redo();
		} catch (CannotRedoException cre) {
			cre.printStackTrace();
		}
	}


	/**
	 * Removes all line highlights.
	 *
	 * @see #removeLineHighlight(Object)
	 */
	public void removeAllLineHighlights() {
		if (lineHighlightManager!=null) {
			lineHighlightManager.removeAllLineHighlights();
		}
	}


	/**
	 * Removes a line highlight.
	 *
	 * @param tag The tag of the line highlight to remove.
	 * @see #removeAllLineHighlights()
	 * @see #addLineHighlight(int, Color)
	 */
	public void removeLineHighlight(Object tag) {
		if (lineHighlightManager!=null) {
			lineHighlightManager.removeLineHighlight(tag);
		}
	}


	/**
	 * Replaces text from the indicated start to end position with the
	 * new text specified.  Does nothing if the model is null.  Simply
	 * does a delete if the new string is null or empty.
	 * <p>
	 * This method is thread safe, although most Swing methods
	 * are not.<p>
	 * This method is overridden so that our Undo manager remembers it as a
	 * single operation (it has trouble with this, especially for
	 * <code>RSyntaxTextArea</code> and the "auto-indent" feature).
	 *
	 * @param str the text to use as the replacement
	 * @param start the start position >= 0
	 * @param end the end position >= start
	 * @exception IllegalArgumentException  if part of the range is an
	 *  invalid position in the model
	 * @see #insert(String, int)
	 * @see #replaceRange(String, int, int)
	 */
	public void replaceRange(String str, int start, int end) {
		if (end < start)
			throw new IllegalArgumentException("end before start");
		Document doc = getDocument();
		if (doc != null) {
			try {
				// Without this, in some cases we'll have to do two undos
				// for one logical operation (for example, try editing a
				// Java source file in an RSyntaxTextArea, and moving a line
				// with text already on it down via Enter.  Without this
				// line, doing a single "undo" moves all later text up,
				// but the first line moved down isn't there!  Doing a
				// second undo puts it back.
				undoManager.beginInternalAtomicEdit();
				((AbstractDocument)doc).replace(start, end - start,
                               		                     str, null);
			} catch (BadLocationException e) {
				throw new IllegalArgumentException(e.getMessage());
			} finally {
				undoManager.endInternalAtomicEdit();
			}
		}
    }


	/**
	 * This method overrides <code>JTextComponent</code>'s
	 * <code>replaceSelection</code>, so that if <code>textMode</code> is
	 * {@link #OVERWRITE_MODE}, it actually overwrites.
	 *
	 * @param text The content to replace the selection with.
	 */
	public void replaceSelection(String text) {

		// It's legal for null to be used here...
		if (text==null) {
			handleReplaceSelection(text);
			return;
		}

		if (getTabsEmulated() && text.indexOf('\t')>-1) {
			text = replaceTabsWithSpaces(text);
		}

		// If the user wants to overwrite text...
		if (textMode==OVERWRITE_MODE && !"\n".equals(text)) {

			Caret caret = getCaret();
			int caretPos = caret.getDot();
			Document doc = getDocument();
			Element map = doc.getDefaultRootElement();
			int curLine = map.getElementIndex(caretPos);
			int lastLine = map.getElementCount() - 1;

			try {

				// If we're not at the end of a line, select the characters
				// that will be overwritten (otherwise JTextArea will simply
				// insert in front of them).
				int curLineEnd = getLineEndOffset(curLine);
				if (caretPos==caret.getMark() && caretPos!=curLineEnd) {
					if (curLine==lastLine)
						caretPos = Math.min(caretPos+text.length(), curLineEnd);
					else
						caretPos = Math.min(caretPos+text.length(), curLineEnd-1);
					caret.moveDot(caretPos);//moveCaretPosition(caretPos);
				}

			} catch (BadLocationException ble) { // Never happens
				UIManager.getLookAndFeel().provideErrorFeedback(this);
				ble.printStackTrace();
			}

		} // End of if (textMode==OVERWRITE_MODE).

		// Now, actually do the inserting/replacing.  Our undoManager will
		// take care of remembering the remove/insert as atomic if we are in
		// overwrite mode.
		handleReplaceSelection(text);

	}


	private StringBuffer repTabsSB;
	/**
	 * Replaces all instances of the tab character in <code>text</code> with
	 * the number of spaces equivalent to a tab in this text area.<p>
	 *
	 * This method should only be called from thread-safe methods, such as
	 * {@link #replaceSelection(String)}.
	 *
	 * @param text The <code>java.lang.String</code> in which to replace tabs
	 *        with spaces.  This has already been verified to have at least
	 *        one tab character in it.
	 * @return A <code>java.lang.String</code> just like <code>text</code>,
	 *         but with spaces instead of tabs.
	 */
	private final String replaceTabsWithSpaces(String text) {

		String tabText = "";
		int temp = getTabSize();
		for (int i=0; i<temp; i++) {
			tabText += ' ';
		}

		// Common case: User's entering a single tab (pressed the tab key).
		if (text.length()==1) {
			return tabText;
		}

		// Otherwise, there may be more than one tab.  Manually search for
		// tabs for performance, as opposed to using String#replaceAll().
		// This method is called for each character inserted when "replace
		// tabs with spaces" is enabled, so we need to be quick.

		//return text.replaceAll("\t", tabText);
		if (repTabsSB==null) {
			repTabsSB = new StringBuffer();
		}
		repTabsSB.setLength(0);
		char[] array = text.toCharArray(); // Wouldn't be needed in 1.5!
		int oldPos = 0;
		int pos = 0;
		while ((pos=text.indexOf('\t', oldPos))>-1) {
			//repTabsSB.append(text, oldPos, pos); // Added in Java 1.5
			if (pos>oldPos) {
				repTabsSB.append(array, oldPos, pos-oldPos);
			}
			repTabsSB.append(tabText);
			oldPos = pos + 1;
		}
		if (oldPos<array.length) {
			repTabsSB.append(array, oldPos, array.length-oldPos);
		}

		return repTabsSB.toString();

	}



	/**
	 * Sets the properties of one of the actions this text area owns.
	 *
	 * @param action The action to modify; for example, {@link #CUT_ACTION}.
	 * @param name The new name for the action.
	 * @param mnemonic The new mnemonic for the action.
	 * @param accelerator The new accelerator key for the action.
	 */
	public static void setActionProperties(int action, String name,
							char mnemonic, KeyStroke accelerator) {
		setActionProperties(action, name, new Integer(mnemonic), accelerator);
	}


	/**
	 * Sets the properties of one of the actions this text area owns.
	 *
	 * @param action The action to modify; for example, {@link #CUT_ACTION}.
	 * @param name The new name for the action.
	 * @param mnemonic The new mnemonic for the action.
	 * @param accelerator The new accelerator key for the action.
	 */
	public static void setActionProperties(int action, String name,
							Integer mnemonic, KeyStroke accelerator) {

		Action tempAction = null;

		switch (action) {
			case CUT_ACTION:
				tempAction = cutAction;
				break;
			case COPY_ACTION:
				tempAction = copyAction;
				break;
			case PASTE_ACTION:
				tempAction = pasteAction;
				break;
			case DELETE_ACTION:
				tempAction = deleteAction;
				break;
			case SELECT_ALL_ACTION:
				tempAction = selectAllAction;
				break;
			case UNDO_ACTION:
			case REDO_ACTION:
			default:
				return;
		}

		tempAction.putValue(Action.NAME, name);
		tempAction.putValue(Action.SHORT_DESCRIPTION, name);
		tempAction.putValue(Action.ACCELERATOR_KEY, accelerator);
		tempAction.putValue(Action.MNEMONIC_KEY, mnemonic);

	}


	/**
	 * This method is overridden to make sure that instances of
	 * <code>RTextArea</code> only use {@link ConfigurableCaret}s.
	 * To set the style of caret (vertical line, block, etc.) used for
	 * insert or overwrite mode, use {@link #setCaretStyle(int, int)}.
	 *
	 * @param caret The caret to use.  If this is not an instance of
	 *        <code>ConfigurableCaret</code>, an exception is thrown.
	 * @throws IllegalArgumentException If the specified caret is not an
	 *         <code>ConfigurableCaret</code>.
	 * @see #setCaretStyle(int, int)
	 */
	public void setCaret(Caret caret) {
		if (!(caret instanceof ConfigurableCaret)) {
			//throw new IllegalArgumentException("RTextArea needs ConfigurableCaret");
			return;
		}
		super.setCaret(caret);
		if (carets!=null) { // Called by setUI() before carets is initialized
			((ConfigurableCaret)caret).setStyle(carets[getTextMode()]);
		}
	}


	/**
	 * Sets the style of caret used when in insert or overwrite mode.
	 *
	 * @param mode Either {@link #INSERT_MODE} or {@link #OVERWRITE_MODE}.
	 * @param style The style for the caret (such as
	 *        {@link ConfigurableCaret#VERTICAL_LINE_STYLE}).
	 * @see org.fife.ui.rtextarea.ConfigurableCaret
	 */
	public void setCaretStyle(int mode, int style) {
		style = (style>=ConfigurableCaret.MIN_STYLE &&
					style<=ConfigurableCaret.MAX_STYLE ?
						style : ConfigurableCaret.THICK_VERTICAL_LINE_STYLE);
		carets[mode] = style;
		if (mode==getTextMode()) {
			// Will repaint the caret if necessary.
			((ConfigurableCaret)getCaret()).setStyle(style);
		}
	}


	/**
	 * Sets the document used by this text area.
	 *
	 * @param document The new document to use.
	 * @throws IllegalArgumentException If the document is not an instance of
	 *         {@link AbstractDocument}.
	 */
	public void setDocument(Document document) {
		if (!(document instanceof AbstractDocument)) {
			throw new IllegalArgumentException("RTextArea requires " +
				"instances of AbstractDocument for its document");
		}
		if (undoManager!=null) { // First time through, undoManager==null
			Document old = getDocument();
			if (old!=null) {
				old.removeUndoableEditListener(undoManager);
			}
		}
		super.setDocument(document);
		if (undoManager!=null) {
			document.addUndoableEditListener(undoManager);
			discardAllEdits();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void setRoundedSelectionEdges(boolean rounded) {
		if (getRoundedSelectionEdges()!=rounded) {
			super.setRoundedSelectionEdges(rounded); // Fires event.
		}
	}

	/**
	 * Sets the text mode for this editor pane.
	 *
	 * @param mode Either {@link #INSERT_MODE} or {@link #OVERWRITE_MODE}.
	 */
	public void setTextMode(int mode) {

		if (mode!=INSERT_MODE && mode!=OVERWRITE_MODE)
			mode = INSERT_MODE;

		if (textMode != mode) {
			ConfigurableCaret cc = (ConfigurableCaret)getCaret();
			cc.setStyle(carets[mode]);
			textMode = mode;
		}

	}

	/**
	 * Sets the UI used by this text area.  This is overridden so only the
	 * right-click popup menu's UI is updated.  The look and feel of an
	 * <code>RTextArea</code> is independent of the Java Look and Feel, and so
	 * this method does not change the text area itself.  Subclasses (such as
	 * <code>RSyntaxTextArea</code> can call <code>setRTextAreaUI</code> if
	 * they wish to install a new UI.
	 *
	 * @param ui This parameter is ignored.
	 */
	public final void setUI(TextUI ui) {
		// Set things like selection color, selected text color, etc. to
		// laf defaults (if values are null or UIResource instances).
		RTextAreaUI rtaui = (RTextAreaUI)getUI();
		if (rtaui!=null) {
			rtaui.installDefaults();
		}

	}


	/**
	 * Attempt to undo an "action" done in this text area.
	 *
	 * @see #redoLastAction()
	 */
	public void undoLastAction() {
		// NOTE: that the try/catch block shouldn't be necessary...
		try {
			if (undoManager.canUndo())
				undoManager.undo();
		}
		catch (CannotUndoException cre) {
			cre.printStackTrace();
		}
	}

	/**
	 * Serializes this text area.
	 *
	 * @param s The stream to write to.
	 * @throws IOException If an IO error occurs.
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {

		// UndoManagers cannot be serialized without Exceptions.  See
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4275892
		getDocument().removeUndoableEditListener(undoManager);
		s.defaultWriteObject();
		getDocument().addUndoableEditListener(undoManager);

	}


	/**
	 * Modified from <code>MutableCaretEvent</code> in
	 * <code>JTextComponent</code> so that mouse events get fired when the user
	 * is selecting text with the mouse as well.  This class also displays the
	 * popup menu when the user right-clicks in the text area.
	 */
	protected class RTextAreaMutableCaretEvent extends RTAMouseListener {

		protected RTextAreaMutableCaretEvent(RTextArea textArea) {
			super(textArea);
		}

		public void focusGained(FocusEvent e) {
			Caret c = getCaret();
			boolean enabled = c.getDot()!=c.getMark();
			cutAction.setEnabled(enabled);
			copyAction.setEnabled(enabled);
			undoManager.updateActions(); // To reflect this text area.
		}

		public void focusLost(FocusEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				Caret caret = getCaret();
				dot = caret.getDot();
				mark = caret.getMark();
				fireCaretUpdate(this);
			}
		}

		public void mousePressed(MouseEvent e) {
			// WORKAROUND:  Since JTextComponent only updates the caret
			// location on mouse clicked and released, we'll do it on dragged
			// events when the left mouse button is clicked.
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				Caret caret = getCaret();
				dot = caret.getDot();
				mark = caret.getMark();
				fireCaretUpdate(this);
			}
		}

		public void mouseReleased(MouseEvent e) {
		}

	}


}