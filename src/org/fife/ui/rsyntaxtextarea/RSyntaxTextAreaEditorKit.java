/*
 * 08/29/2004
 *
 * RSyntaxTextAreaEditorKit.java - The editor kit used by RSyntaxTextArea.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import javax.swing.text.Document;

import org.fife.ui.rtextarea.RTextAreaEditorKit;


/**
 * An extension of <code>RTextAreaEditorKit</code> that adds functionality for
 * programming-specific stuff.  There are currently subclasses to handle:
 *
 * <ul>
 *   <li>Toggling code folds.</li>
 *   <li>Aligning "closing" curly braces with their matches, if the current
 *       programming language uses curly braces to identify code blocks.</li>
 *   <li>Copying the current selection as RTF.</li>
 *   <li>Block indentation (increasing the indent of one or multiple lines)</li>
 *   <li>Block un-indentation (decreasing the indent of one or multiple lines)
 *       </li>
 *   <li>Inserting a "code template" when a configurable key (e.g. a space) is
 *       pressed</li>
 *   <li>Decreasing the point size of all fonts in the text area</li>
 *   <li>Increasing the point size of all fonts in the text area</li>
 *   <li>Moving the caret to the "matching bracket" of the one at the current
 *       caret position</li>
 *   <li>Toggling whether the currently selected lines are commented out.</li>
 *   <li>Better selection of "words" on mouse double-clicks for programming
 *       languages.</li>
 *   <li>Better keyboard navigation via Ctrl+arrow keys for programming
 *       languages.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 0.5
 */
public class RSyntaxTextAreaEditorKit extends RTextAreaEditorKit {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the default document used by <code>RSyntaxTextArea</code>s.
	 *
	 * @return The document.
	 */
	public Document createDefaultDocument() {
		return new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE);
	}
	
}

