/*
 * 03/07/2012
 *
 * FoldingAwareIconRowHeader - Icon row header that paints itself correctly
 * even when code folding is enabled.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.event.DocumentEvent;

import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rtextarea.AbstractGutterComponent;


/**
 * A row header component that takes code folding into account when painting
 * itself.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FoldingAwareIconRowHeader extends AbstractGutterComponent {

	/**
	 * The width of this component.
	 */
	protected int width;

	/**
	 * Used in {@link #paintComponent(Graphics)} to prevent reallocation on
	 * each paint.
	 */
	protected Rectangle visibleRect;

	/**
	 * Used in {@link #paintComponent(Graphics)} to prevent reallocation on
	 * each paint.
	 */
	protected Insets textAreaInsets;
	
	/**
	 * Constructor.
	 *
	 * @param textArea The parent text area.
	 */
	public FoldingAwareIconRowHeader(RSyntaxTextArea textArea) {
		super(textArea);
		visibleRect = new Rectangle();
		width = 16;
	}

	public void handleDocumentEvent(DocumentEvent e) {
		int newLineCount = textArea.getLineCount();
		if (newLineCount!=currentLineCount) {
			currentLineCount = newLineCount;
			repaint();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void paintComponent(Graphics g) {

		// When line wrap is not enabled, take the faster code path.
		if (textArea==null) {
			return;
		}
		RSyntaxTextArea rsta = (RSyntaxTextArea)textArea;
		FoldManager fm = rsta.getFoldManager();
		if (!fm.isCodeFoldingSupportedAndEnabled()) {
			super.paintComponent(g);
			return;
		}

		visibleRect = g.getClipBounds(visibleRect);
		if (visibleRect==null) { // ???
			visibleRect = getVisibleRect();
		}
		//System.out.println("IconRowHeader repainting: " + visibleRect);
		if (visibleRect==null) {
			return;
		}

		g.setColor(getBackground());
		g.fillRect(0,visibleRect.y, width,visibleRect.height);


		textAreaInsets = textArea.getInsets(textAreaInsets);

	}

}