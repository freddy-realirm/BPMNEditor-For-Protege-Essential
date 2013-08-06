/*
 * 02/17/2009
 *
 * Gutter.java - Manages line numbers, icons, etc. on the left-hand side of
 * an RTextArea.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rtextarea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;


/**
 * The gutter is the component on the left-hand side of the text area that
 * displays optional information such as line numbers, fold regions, and icons
 * (for bookmarks, debugging breakpoints, error markers, etc.).<p>
 *
 * Icons can be added on a per-line basis to visually mark syntax errors, lines
 * with breakpoints set on them, etc.  To add icons to the gutter, you must
 * first call {@link RTextScrollPane#setIconRowHeaderEnabled(boolean)} on the
 * parent scroll pane, to make the icon area visible.  Then, you can add icons
 * that track either lines in the document, or offsets, via
 * {@link #addLineTrackingIcon(int, Icon)} and
 * {@link #addOffsetTrackingIcon(int, Icon)}, respectively.  To remove an
 * icon you've added, use {@link #removeTrackingIcon(GutterIconInfo)}.<p>
 *
 * In addition to support for arbitrary per-line icons, this component also
 * has built-in support for displaying icons representing "bookmarks;" that is,
 * lines a user can cycle through via F2 and Shift+F2.  Bookmarked lines are
 * toggled via Ctrl+F2.  In order to enable bookmarking, you must first assign
 * an icon to represent a bookmarked line, then actually enable the feature:
 * 
 * <pre>
 * Gutter gutter = scrollPane.getGutter();
 * gutter.setBookmarkIcon(new ImageIcon("bookmark.png"));
 * gutter.setBookmarkingEnabled(true);
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 * @see GutterIconInfo
 */
public class Gutter extends JPanel {

	/**
	 * The color used to highlight active line ranges if none is specified.
	 */
	public static final Color DEFAULT_ACTIVE_LINE_RANGE_COLOR =
												new Color(51, 153, 255);

	/**
	 * The text area.
	 */
	private RTextArea textArea;

	/**
	 * Renders line numbers.
	 */
	private LineNumberList lineNumberList;

	/**
	 * Shows lines that are code-foldable.
	 */
	private FoldIndicator foldIndicator;

	/**
	 * Listens for events in our text area.
	 */
	private TextAreaListener listener;


	/**
	 * Constructor.
	 *
	 * @param textArea The parent text area.
	 */
	public Gutter(RTextArea textArea) {

		listener = new TextAreaListener();
		setTextArea(textArea);
		setLayout(new BorderLayout());
		if (this.textArea!=null) {
			// Enable line numbers our first time through if they give us
			// a text area.
			setLineNumbersEnabled(true);
			if (this.textArea instanceof RSyntaxTextArea) {
				RSyntaxTextArea rsta = (RSyntaxTextArea)this.textArea;
				setFoldIndicatorEnabled(rsta.isCodeFoldingEnabled());
			}
		}

		setBorder(new GutterBorder(0, 0, 0, 1)); // Assume ltr

		Color bg = null;
		if (textArea!=null) {
			bg = textArea.getBackground(); // May return null if image bg
		}
		setBackground(bg!=null ? bg : Color.WHITE);

	}

	/**
	 * Returns the color of the "border" line.
	 *
	 * @return The color.
	 * @see #setBorderColor(Color)
	 */
	public Color getBorderColor() {
		return ((GutterBorder)getBorder()).getColor();
	}


	/**
	 * Returns the background color used by the (default) fold icons.
	 *
	 * @return The background color.
	 * @see #setFoldBackground(Color)
	 */
	public Color getFoldBackground() {
		return foldIndicator.getFoldIconBackground();
	}


	/**
	 * Returns the foreground color of the fold indicator.
	 *
	 * @return The foreground color of the fold indicator.
	 * @see #setFoldIndicatorForeground(Color)
	 */
	public Color getFoldIndicatorForeground() {
		return foldIndicator.getForeground();
	}


	/**
	 * Returns the color to use to paint line numbers.
	 *
	 * @return The color used when painting line numbers.
	 * @see #setLineNumberColor(Color)
	 */
	public Color getLineNumberColor() {
		return lineNumberList.getForeground();
	}


	/**
	 * Returns the font used for line numbers.
	 *
	 * @return The font used for line numbers.
	 * @see #setLineNumberFont(Font)
	 */
	public Font getLineNumberFont() {
		return lineNumberList.getFont();
	}


	/**
	 * Returns the starting line's line number.  The default value is
	 * <code>1</code>.
	 *
	 * @return The index
	 * @see #setLineNumberingStartIndex(int)
	 */
	public int getLineNumberingStartIndex() {
		return lineNumberList.getLineNumberingStartIndex();
	}


	/**
	 * Returns <code>true</code> if the line numbers are enabled and visible.
	 *
	 * @return Whether or not line numbers are visible.
	 */
	public boolean getLineNumbersEnabled() {
		for (int i=0; i<getComponentCount(); i++) {
			if (getComponent(i)==lineNumberList) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Returns whether tool tips are displayed showing the contents of
	 * collapsed fold regions when the mouse hovers over a +/- icon.
	 *
	 * @return Whether these tool tips are displayed.
	 * @see #setShowCollapsedRegionToolTips(boolean)
	 */
	public boolean getShowCollapsedRegionToolTips() {
		return foldIndicator.getShowCollapsedRegionToolTips();
	}

	/**
	 * Returns whether the fold indicator is enabled.
	 *
	 * @return Whether the fold indicator is enabled.
	 * @see #setFoldIndicatorEnabled(boolean)
	 */
	public boolean isFoldIndicatorEnabled() {
		for (int i=0; i<getComponentCount(); i++) {
			if (getComponent(i)==foldIndicator) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Sets the color for the "border" line.
	 *
	 * @param color The new color.
	 * @see #getBorderColor()
	 */
	public void setBorderColor(Color color) {
		((GutterBorder)getBorder()).setColor(color);
		repaint();
	}


	/**
	 * {@inheritDoc}
	 */
	public void setComponentOrientation(ComponentOrientation o) {
		// Reuse the border to preserve its color.
		if (o.isLeftToRight()) {
			((GutterBorder)getBorder()).setEdges(0, 0, 0, 1);
		}
		else {
			((GutterBorder)getBorder()).setEdges(0, 1, 0, 0);
		}
		super.setComponentOrientation(o);
	}


	/**
	 * Sets the icons to use to represent collapsed and expanded folds.
	 *
	 * @param collapsedIcon The collapsed fold icon.  This cannot be
	 *        <code>null</code>.
	 * @param expandedIcon The expanded fold icon.  This cannot be
	 *        <code>null</code>.
	 */
	public void setFoldIcons(Icon collapsedIcon, Icon expandedIcon) {
		if (foldIndicator!=null) {
			foldIndicator.setFoldIcons(collapsedIcon, expandedIcon);
		}
	}


	/**
	 * Toggles whether the fold indicator is enabled.
	 *
	 * @param enabled Whether the fold indicator should be enabled.
	 * @see #isFoldIndicatorEnabled()
	 */
	public void setFoldIndicatorEnabled(boolean enabled) {
		if (foldIndicator!=null) {
			if (enabled) {
				add(foldIndicator, BorderLayout.LINE_END);
			}
			else {
				remove(foldIndicator);
			}
			revalidate();
		}
	}


	/**
	 * Sets the background color used by the (default) fold icons.
	 *
	 * @param bg The new background color.
	 * @see #getFoldBackground()
	 */
	public void setFoldBackground(Color bg) {
		if (bg==null) {
			bg = FoldIndicator.DEFAULT_FOLD_BACKGROUND;
		}
		foldIndicator.setFoldIconBackground(bg);
	}


	/**
	 * Sets the foreground color used by the fold indicator.
	 *
	 * @param fg The new fold indicator foreground.
	 * @see #getFoldIndicatorForeground()
	 */
	public void setFoldIndicatorForeground(Color fg) {
		if (fg==null) {
			fg = FoldIndicator.DEFAULT_FOREGROUND;
		}
		foldIndicator.setForeground(fg);
	}


	/**
	 * Sets the color to use to paint line numbers.
	 *
	 * @param color The color to use when painting line numbers.
	 * @see #getLineNumberColor()
	 */
	public void setLineNumberColor(Color color) {
		lineNumberList.setForeground(color);
	}


	/**
	 * Sets the font used for line numbers.
	 *
	 * @param font The font to use.  This cannot be <code>null</code>.
	 * @see #getLineNumberFont()
	 */
	public void setLineNumberFont(Font font) {
		if (font==null) {
			throw new IllegalArgumentException("font cannot be null");
		}
		lineNumberList.setFont(font);
	}


	/**
	 * Sets the starting line's line number.  The default value is
	 * <code>1</code>.  Applications can call this method to change this value
	 * if they are displaying a subset of lines in a file, for example.
	 *
	 * @param index The new index.
	 * @see #getLineNumberingStartIndex()
	 */
	public void setLineNumberingStartIndex(int index) {
		lineNumberList.setLineNumberingStartIndex(index);
	}


	/**
	 * Toggles whether or not line numbers are visible.
	 *
	 * @param enabled Whether or not line numbers should be visible.
	 * @see #getLineNumbersEnabled()
	 */
	void setLineNumbersEnabled(boolean enabled) {
		if (lineNumberList!=null) {
			if (enabled) {
				add(lineNumberList);
			}
			else {
				remove(lineNumberList);
			}
			revalidate();
		}
	}


	/**
	 * Toggles whether tool tips should be displayed showing the contents of
	 * collapsed fold regions when the mouse hovers over a +/- icon.
	 *
	 * @param show Whether to show these tool tips.
	 * @see #getShowCollapsedRegionToolTips()
	 */
	public void setShowCollapsedRegionToolTips(boolean show) {
		if (foldIndicator!=null) {
			foldIndicator.setShowCollapsedRegionToolTips(show);
		}
	}


	/**
	 * Sets the text area being displayed.  This will clear any tracking
	 * icons currently displayed.
	 *
	 * @param textArea The text area.
	 */
	void setTextArea(RTextArea textArea) {

		RTextAreaEditorKit kit = (RTextAreaEditorKit)textArea.getUI().
												getEditorKit(textArea);
		if (this.textArea!=null) {
			listener.uninstall();
		}

		if (lineNumberList==null) {
			lineNumberList = kit.createLineNumberList(textArea);
		}
		else {
			lineNumberList.setTextArea(textArea);
		}
		
		if (foldIndicator==null) {
			foldIndicator = new FoldIndicator(textArea);
		}
		else {
			foldIndicator.setTextArea(textArea);
		}

		listener.install(textArea);
		this.textArea = textArea;

	}


	/**
	 * The border used by the gutter.
	 */
	private static class GutterBorder extends EmptyBorder {

		private Color color;

		public GutterBorder(int top, int left, int bottom, int right) {
			super(top, left, bottom, right);
			color = new Color(221, 221, 221);
		}

		public Color getColor() {
			return color;
		}

		public void paintBorder(Component c, Graphics g, int x, int y,
								int width, int height) {
			g.setColor(color);
			if (left==1) {
				g.drawLine(0,0, 0,height);
			}
			else {
				g.drawLine(width-1,0, width-1,height);
			}
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public void setEdges(int top, int left, int bottom, int right) {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}

	}


	/**
	 * Listens for the text area resizing.
	 */
	/*
	 * This is necessary to keep child components the same height as the text
	 * area.  The worse case is when the user toggles word-wrap and it changes
	 * the height of the text area. In that case, if we listen for the
	 * "lineWrap" property change, we get notified BEFORE the text area
	 * decides on its new size, thus we cannot resize properly.  We listen
	 * instead for ComponentEvents so we change size after the text area has
	 * resized.
	 */
	private class TextAreaListener extends ComponentAdapter
						implements DocumentListener, PropertyChangeListener {

		private boolean installed;

		public void changedUpdate(DocumentEvent e) {}

		public void componentResized(java.awt.event.ComponentEvent e) {
			revalidate();
		}

		protected void handleDocumentEvent(DocumentEvent e) {
			for (int i=0; i<getComponentCount(); i++) {
				AbstractGutterComponent agc =
							(AbstractGutterComponent)getComponent(i);
				agc.handleDocumentEvent(e);
			}
		}

		public void insertUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void install(RTextArea textArea) {
			if (installed) {
				uninstall();
			}
			textArea.addComponentListener(this);
			textArea.getDocument().addDocumentListener(this);
			textArea.addPropertyChangeListener(this);
			if (textArea instanceof RSyntaxTextArea) {
				RSyntaxTextArea rsta = (RSyntaxTextArea)textArea;
				rsta.getFoldManager().addPropertyChangeListener(this);
			}
			installed = true;
		}

		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			if (RSyntaxTextArea.CODE_FOLDING_PROPERTY.equals(name)) {
				boolean foldingEnabled = ((Boolean)e.getNewValue()).
															booleanValue();
				if (lineNumberList!=null) { // Its size depends on folding
					//lineNumberList.revalidate();
					lineNumberList.updateCellWidths();
				}
				setFoldIndicatorEnabled(foldingEnabled);
			}

			// If code folds are updated...
			else if (FoldManager.PROPERTY_FOLDS_UPDATED.equals(name)) {
				repaint();
			}

		}

		public void removeUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
		}

		public void uninstall() {
			if (installed) {
				textArea.removeComponentListener(this);
				textArea.getDocument().removeDocumentListener(this);
				if (textArea instanceof RSyntaxTextArea) {
					RSyntaxTextArea rsta = (RSyntaxTextArea)textArea;
					rsta.getFoldManager().removePropertyChangeListener(this);
				}
				installed = false;
			}
		}

	}


}