/*
 * 01/27/2004
 *
 * RSyntaxTextArea.java - An extension of RTextArea that adds
 * the ability to syntax highlight certain programming languages.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaUI;



/**
 * An extension of <code>RTextArea</code> that adds syntax highlighting
 * of certain programming languages to its list of features.  Languages
 * currently supported include:
 *
 * <table>
 *  <tr>
 *   <td style="vertical-align: top">
 *    <ul>
 *       <li>ActionScript
 *       <li>Assembler (X86)
 *       <li>BBCode
 *       <li>C
 *       <li>C++
 *       <li>CSS
 *       <li>C#
 *       <li>Clojure
 *       <li>Delphi
 *       <li>Fortran
 *       <li>Groovy
 *       <li>HTML
 *       <li>Java
 *       <li>JavaScript
 *       <li>JSP
 *    </ul>
 *   </td>
 *   <td style="vertical-align: top">
 *    <ul>
 *       <li>Lisp
 *       <li>Lua
 *       <li>Make
 *       <li>MXML
 *       <li>Perl
 *       <li>PHP
 *       <li>Ruby
 *       <li>SAS
 *       <li>Scala
 *       <li>SQL
 *       <li>Tcl
 *       <li>UNIX shell scripts
 *       <li>Windows batch
 *       <li>XML files
 *    </ul>
 *   </td>
 *  </tr>
 * </table>
 *
 * Other added features include:
 * <ul style="columns: 2 12em; column-gap: 1em">
 *    <li>Code folding
 *    <li>Bracket matching
 *    <li>Auto-indentation
 *    <li>Copy as RTF
 *    <li>Clickable hyperlinks (if the language scanner being used supports it)
 *    <li>A pluggable "parser" system that can be used to implement syntax
 *        validation, spell checking, etc.
 * </ul>
 *
 * It is recommended that you use an instance of
 * {@link org.fife.ui.rtextarea.RTextScrollPane} instead of a regular
 * <code>JScrollPane</code> as this class allows you to add line numbers and
 * bookmarks easily to your text area.
 *
 * @author Robert Futrell
 * @version 2.0.2
 */
public class RSyntaxTextArea extends RTextArea implements SyntaxConstants {

	public static final String ANIMATE_BRACKET_MATCHING_PROPERTY	= "RSTA.animateBracketMatching";
	public static final String ANTIALIAS_PROPERTY					= "RSTA.antiAlias";
	public static final String BRACKET_MATCHING_PROPERTY			= "RSTA.bracketMatching";
	public static final String CODE_FOLDING_PROPERTY				= "RSTA.codeFolding";
	public static final String SYNTAX_SCHEME_PROPERTY				= "RSTA.syntaxScheme";
	public static final String SYNTAX_STYLE_PROPERTY				= "RSTA.syntaxStyle";

	private static final Color DEFAULT_BRACKET_MATCH_BG_COLOR		= new Color(234,234,255);
	private static final Color DEFAULT_BRACKET_MATCH_BORDER_COLOR	= new Color(0,0,128);
	private static final Color DEFAULT_SELECTION_COLOR			= new Color(200,200,255);

	/** The key for the syntax style to be highlighting. */
	private String syntaxStyleKey;

	/** The colors used for syntax highlighting. */
	private SyntaxScheme syntaxScheme;

	/**
	 * The rectangle surrounding the "matched bracket" if bracket matching
	 * is enabled.
	 */
	Rectangle match;

	/**
	 * Colors used for the "matched bracket" if bracket matching is enabled.
	 */
	private Color matchedBracketBGColor;
	private Color matchedBracketBorderColor;

	/** The location of the last matched bracket. */
	private int lastBracketMatchPos;

	/** Whether or not bracket matching is enabled. */
	private boolean bracketMatchingEnabled;

	/** Whether or not bracket matching is animated. */
	private boolean animateBracketMatching;

	private BracketMatchingTimer bracketRepaintTimer;

	/** Metrics of the text area's font. */
	private FontMetrics defaultFontMetrics;

	/** Manages running the parser. */
	private ParserManager parserManager;

	private FoldManager foldManager;

	/** Cached desktop anti-aliasing hints, if anti-aliasing is enabled. */
	private Map aaHints;

private int lineHeight;		// Height of a line of text; same for default, bold & italic.
private int maxAscent;

	/**
	 * Constructor.
	 */
	public RSyntaxTextArea() {
		init();
	}


	/**
	 * Constructor.
	 * 
	 * @param doc The document for the editor.
	 */
	public RSyntaxTextArea(RSyntaxDocument doc) {
		super(doc);
		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param text The initial text to display.
	 */
	public RSyntaxTextArea(String text) {
		super(text);
		init();
	}


	/**
	 * Constructor.
	 * 
	 * @param rows The number of rows to display.
	 * @param cols The number of columns to display.
	 * @throws IllegalArgumentException If either <code>rows</code> or
	 *         <code>cols</code> is negative.
	 */
	public RSyntaxTextArea(int rows, int cols) {
		super(rows, cols);
		init();
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
	public RSyntaxTextArea(String text, int rows, int cols) {
		super(text, rows, cols);
		init();
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
	public RSyntaxTextArea(RSyntaxDocument doc, String text,int rows,int cols) {
		super(doc, text, rows, cols);
		init();
	}


	/**
	 * Creates a new <code>RSyntaxTextArea</code>.
	 *
	 * @param textMode Either <code>INSERT_MODE</code> or
	 *        <code>OVERWRITE_MODE</code>.
	 */
	public RSyntaxTextArea(int textMode) {
		super(textMode);
		init();
	}

	/**
	 * Updates the font metrics the first time we're displayed.
	 */
	public void addNotify() {

		super.addNotify();

		// We know we've just been connected to a screen resource (by
		// definition), so initialize our font metrics objects.
		refreshFontMetrics(getGraphics2D(getGraphics()));

		// Re-start parsing if we were removed from one container and added
		// to another
		if (parserManager!=null) {
			parserManager.restartParsing();
		}

	}


	/**
	 * Adds the parser to "validate" the source code in this text area.  This
	 * can be anything from a spell checker to a "compiler" that verifies
	 * source code.
	 *
	 * @param parser The new parser.  A value of <code>null</code> will
	 *        do nothing.
	 * @see #getParser(int)
	 * @see #getParserCount()
	 * @see #removeParser(Parser)
	 */
	public void addParser(Parser parser) {
		if (parserManager==null) {
			parserManager = new ParserManager(this);
		}
		parserManager.addParser(parser);
	}


	/**
	 * Recalculates the height of a line in this text area and the
	 * maximum ascent of all fonts displayed.
	 */
	private void calculateLineHeight() {

		lineHeight = maxAscent = 0;

		// Each token style.
		for (int i=0; i<syntaxScheme.getStyleCount(); i++) {
			Style ss = syntaxScheme.getStyle(i);
			if (ss!=null && ss.font!=null) {
				FontMetrics fm = getFontMetrics(ss.font);
				int height = fm.getHeight();
				if (height>lineHeight)
					lineHeight = height;
				int ascent = fm.getMaxAscent();
				if (ascent>maxAscent)
					maxAscent = ascent;
			}
		}

		// The text area's (default) font).
		Font temp = getFont();
		FontMetrics fm = getFontMetrics(temp);
		int height = fm.getHeight();
		if (height>lineHeight) {
			lineHeight = height;
		}
		int ascent = fm.getMaxAscent();
		if (ascent>maxAscent) {
			maxAscent = ascent;
		}

	}


	/**
	 * Removes all parsers from this text area.
	 *
	 * @see #removeParser(Parser)
	 */
	public void clearParsers() {
		if (parserManager!=null) {
			parserManager.clearParsers();
		}
	}


	/**
	 * Returns the document to use for an <code>RSyntaxTextArea</code>
	 *
	 * @return The document.
	 */
	protected Document createDefaultModel() {
		return new RSyntaxDocument(SYNTAX_STYLE_NONE);
	}

	/**
	 * Returns the a real UI to install on this text area.
	 *
	 * @return The UI.
	 */
	protected RTextAreaUI createRTextAreaUI() {
		return new RSyntaxTextAreaUI(this);
	}


	/**
	 * If the caret is on a bracket, this method finds the matching bracket,
	 * and if it exists, highlights it.
	 */
	protected final void doBracketMatching() {

		// We always need to repaint the "matched bracket" highlight if it
		// exists.
		if (match!=null) {
			repaint(match);
		}

		// If a matching bracket is found, get its bounds and paint it!
		int pos = RSyntaxUtilities.getMatchingBracketPosition(this);
		if (pos>-1 && pos!=lastBracketMatchPos) {
			try {
				match = modelToView(pos);
				if (match!=null) { // Happens if we're not yet visible
					if (getAnimateBracketMatching()) {
						bracketRepaintTimer.restart();
					}
					repaint(match);
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace(); // Shouldn't happen.
			}
		}
		else if (pos==-1) {
			// Set match to null so the old value isn't still repainted.
			match = null;
			bracketRepaintTimer.stop();
		}
		lastBracketMatchPos = pos;

	}


	/**
	 * Notifies all listeners that a caret change has occurred.
	 *
	 * @param e The caret event.
	 */
	protected void fireCaretUpdate(CaretEvent e) {
		super.fireCaretUpdate(e);
		if (isBracketMatchingEnabled()) {
			doBracketMatching();
		}
	}

	/**
	 * Called whenever a fold is collapsed or expanded.  This causes the
	 * text editor to revalidate.  This method is here because of poor design
	 * and should be removed.
	 *
	 * @param fold The fold that was collapsed or expanded.
	 */
	public void foldToggled(Fold fold) {
		match = null; // TODO: Update the bracket rect rather than hide it
		possiblyUpdateCurrentLineHighlightLocation();
		revalidate();
		repaint();
	}


	/**
	 * Returns whether bracket matching should be animated.
	 *
	 * @return Whether bracket matching should be animated.
	 * @see #setAnimateBracketMatching(boolean)
	 */
	public boolean getAnimateBracketMatching() {
		return animateBracketMatching;
	}


	/**
	 * Returns whether anti-aliasing is enabled in this editor.
	 *
	 * @return Whether anti-aliasing is enabled in this editor.
	 * @see #setAntiAliasingEnabled(boolean)
	 * @see #getFractionalFontMetricsEnabled()
	 */
	public boolean getAntiAliasingEnabled() {
		return aaHints!=null;
	}


	/**
	 * Returns the background color for tokens of the specified type.
	 *
	 * @param type The type of token.
	 * @return The background color to use for that token type.  If
	 *         this value is <code>null</code> then this token type
	 *         has no special background color.
	 * @see #getForegroundForToken(Token)
	 */
	public Color getBackgroundForTokenType(int type) {
		// Don't default to this.getBackground(), as Tokens simply don't
		// paint a background if they get a null Color.
		return syntaxScheme.getStyle(type).background;
	}

	
	/**
	 * Returns the default bracket-match background color.
	 *
	 * @return The color.
	 * @see #getDefaultBracketMatchBorderColor
	 */
	public static final Color getDefaultBracketMatchBGColor() {
		return DEFAULT_BRACKET_MATCH_BG_COLOR;
	}


	/**
	 * Returns the default bracket-match border color.
	 *
	 * @return The color.
	 * @see #getDefaultBracketMatchBGColor
	 */
	public static final Color getDefaultBracketMatchBorderColor() {
		return DEFAULT_BRACKET_MATCH_BORDER_COLOR;
	}


	/**
	 * Returns the default selection color for this text area.  This
	 * color was chosen because it's light and <code>RSyntaxTextArea</code>
	 * does not change text color between selected/unselected text for
	 * contrast like regular <code>JTextArea</code>s do.
	 *
	 * @return The default selection color.
	 */
	public static Color getDefaultSelectionColor() {
		return DEFAULT_SELECTION_COLOR;
	}


	/**
	 * Returns the "default" syntax highlighting color scheme.  The colors
	 * used are somewhat standard among syntax highlighting text editors.
	 *
	 * @return The default syntax highlighting color scheme.
	 * @see #restoreDefaultSyntaxScheme()
	 * @see #getSyntaxScheme()
	 * @see #setSyntaxScheme(SyntaxScheme)
	 */
	public SyntaxScheme getDefaultSyntaxScheme() {
		return new SyntaxScheme(getFont());
	}

	/**
	 * Returns the fold manager for this text area.
	 *
	 * @return The fold manager.
	 */
	public FoldManager getFoldManager() {
		return foldManager;
	}


	/**
	 * Returns the font for tokens of the specified type.
	 *
	 * @param type The type of token.
	 * @return The font to use for that token type.
	 * @see #getFontMetricsForTokenType(int)
	 */
	public Font getFontForTokenType(int type) {
		Font f = syntaxScheme.getStyle(type).font;
		return f!=null ? f : getFont();
	}


	/**
	 * Returns the font metrics for tokens of the specified type.
	 *
	 * @param type The type of token.
	 * @return The font metrics to use for that token type.
	 * @see #getFontForTokenType(int)
	 */
	public FontMetrics getFontMetricsForTokenType(int type) {
		FontMetrics fm = syntaxScheme.getStyle(type).fontMetrics;
		return fm!=null ? fm : defaultFontMetrics;
	}


	/**
	 * Returns the foreground color to use when painting a token.
	 *
	 * @param t The token.
	 * @return The foreground color to use for that token.  This
	 *         value is never <code>null</code>.
	 * @see #getBackgroundForTokenType(int)
	 */
	public Color getForegroundForToken(Token t) {
		return getForegroundForTokenType(t.type);
	}


	/**
	 * Returns the foreground color to use when painting a token.  This does
	 * not take into account whether the token is a hyperlink.
	 *
	 * @param type The token type.
	 * @return The foreground color to use for that token.  This
	 *         value is never <code>null</code>.
	 * @see #getForegroundForToken(Token)
	 */
	public Color getForegroundForTokenType(int type) {
		Color fg = syntaxScheme.getStyle(type).foreground;
		return fg!=null ? fg : getForeground();
	}


	/**
	 * Returns a <code>Graphics2D</code> version of the specified graphics
	 * that has been initialized with the proper rendering hints.
	 *
	 * @param g The graphics context for which to get a
	 *        <code>Graphics2D</code>.
	 * @return The <code>Graphics2D</code>.
	 */
	private final Graphics2D getGraphics2D(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		if (aaHints!=null) {
			g2d.addRenderingHints(aaHints);
		}
		return g2d;
	}

	/**
	 * Returns the last visible offset in this text area.  This may not be the
	 * length of the document if code folding is enabled.
	 *
	 * @return The last visible offset in this text area.
	 */
	public int getLastVisibleOffset() {
		if (isCodeFoldingEnabled()) {
			int lastVisibleLine = foldManager.getLastVisibleLine();
			if (lastVisibleLine<getLineCount()-1) { // Not the last line
				try {
					return getLineEndOffset(lastVisibleLine) - 1;
				} catch (BadLocationException ble) { // Never happens
					ble.printStackTrace();
				}
			}
		}
		return getDocument().getLength();
	}


	/**
	 * Returns the height to use for a line of text in this text area.
	 *
	 * @return The height of a line of text in this text area.
	 */
	public int getLineHeight() {
		//System.err.println("... getLineHeight() returning " + lineHeight);
		return lineHeight;
	}


	/**
	 * Gets the color used as the background for a matched bracket.
	 *
	 * @return The color used.  If this is <code>null</code>, no special
	 *         background is painted behind a matched bracket.
	 * @see #setMatchedBracketBGColor
	 * @see #getMatchedBracketBorderColor
	 */
	public Color getMatchedBracketBGColor() {
		return matchedBracketBGColor;
	}


	/**
	 * Gets the color used as the border for a matched bracket.
	 *
	 * @return The color used.
	 * @see #setMatchedBracketBorderColor
	 * @see #getMatchedBracketBGColor
	 */
	public Color getMatchedBracketBorderColor() {
		return matchedBracketBorderColor;
	}


	/**
	 * Returns the matched bracket's rectangle, or <code>null</code> if there
	 * is currently no matched bracket.  Note that this shouldn't ever be
	 * called by the user.
	 *
	 * @return The rectangle surrounding the matched bracket.
	 */
	public final Rectangle getMatchRectangle() {
		return match;
	}


	/**
	 * Overridden to return the max ascent for any font used in the editor.
	 *
	 * @return The max ascent value.
	 */
	public int getMaxAscent() {
		return maxAscent;
	}


	/**
	 * Returns the specified parser.
	 *
	 * @param index The {@link Parser} to retrieve.
	 * @return The <code>Parser</code>.
	 * @see #getParserCount()
	 * @see #addParser(Parser)
	 */
	public Parser getParser(int index) {
		return parserManager.getParser(index);
	}


	/**
	 * Returns the number of parsers operating on this text area.
	 *
	 * @return The parser count.
	 * @see #addParser(Parser)
	 */
	public int getParserCount() {
		return parserManager==null ? 0 : parserManager.getParserCount();
	}


	/**
	 * Returns what type of syntax highlighting this editor is doing.
	 *
	 * @return The style being used, such as
	 *         {@link SyntaxConstants#SYNTAX_STYLE_JAVA}.
	 * @see #setSyntaxEditingStyle(String)
	 * @see SyntaxConstants
	 */
	public String getSyntaxEditingStyle() {
		return syntaxStyleKey;
	}


	/**
	 * Returns all of the colors currently being used in syntax highlighting
	 * by this text component.
	 *
	 * @return An instance of <code>SyntaxScheme</code> that represents
	 *         the colors currently being used for syntax highlighting.
	 * @see #setSyntaxScheme(SyntaxScheme)
	 */
	public SyntaxScheme getSyntaxScheme() {
		return syntaxScheme;
	}


	/**
	 * Returns a list of tokens representing the given line.
	 *
	 * @param line The line number to get tokens for.
	 * @return A linked list of tokens representing the line's text.
	 */
	public Token getTokenListForLine(int line) {
		return ((RSyntaxDocument)getDocument()).getTokenListForLine(line);
	}


	/**
	 * Returns whether the specified token should be underlined.
	 * A token is underlined if its syntax style includes underlining,
	 * or if it is a hyperlink and hyperlinks are enabled.
	 *
	 * @param t The token.
	 * @return Whether the specified token should be underlined.
	 */
	public boolean getUnderlineForToken(Token t) {
		return t.isHyperlink() ||
				syntaxScheme.getStyle(t.type).underline;
	}

	/**
	 * Called by constructors to initialize common properties of the text
	 * editor.
	 */
	protected void init() {
		// Set some RSyntaxTextArea default values.
		syntaxStyleKey = SYNTAX_STYLE_NONE;
		setMatchedBracketBGColor(getDefaultBracketMatchBGColor());
		setMatchedBracketBorderColor(getDefaultBracketMatchBorderColor());
		setBracketMatchingEnabled(true);
		setAnimateBracketMatching(true);
		lastBracketMatchPos = -1;
		setSelectionColor(getDefaultSelectionColor());
		setBackground(Color.WHITE);
		
		foldManager = new FoldManager(this);

		restoreDefaultSyntaxScheme();

	}

	/**
	 * Returns whether or not bracket matching is enabled.
	 *
	 * @return <code>true</code> iff bracket matching is enabled.
	 * @see #setBracketMatchingEnabled
	 */
	public final boolean isBracketMatchingEnabled() {
		return bracketMatchingEnabled;
	}


	/**
	 * Returns whether code folding is enabled.  Note that only certain
	 * languages support code folding; those that do not will ignore this
	 * property.
	 *
	 * @return Whether code folding is enabled.
	 * @see #setCodeFoldingEnabled(boolean)
	 */
	public boolean isCodeFoldingEnabled() {
		return foldManager.isCodeFoldingEnabled();
	}

	/**
	 * Returns the token at the specified position in the model.
	 *
	 * @param offs The position in the model.
	 * @return The token, or <code>null</code> if no token is at that
	 *         position.
	 * @see #viewToToken(Point)
	 */
	private Token modelToToken(int offs) {
		if (offs>=0) {
			try {
				int line = getLineOfOffset(offs);
				Token t = getTokenListForLine(line);
				while (t!=null && t.isPaintable()) {
					if (t.containsPosition(offs)) {
						return t;
					}
					t = t.getNextToken();
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace(); // Never happens
			}
		}
		return null;
	}


	/**
	 * The <code>paintComponent</code> method is overridden so we
	 * apply any necessary rendering hints to the Graphics object.
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(getGraphics2D(g));
	}


	private void refreshFontMetrics(Graphics2D g2d) {
		// It is assumed that any rendering hints are already applied to g2d.
		defaultFontMetrics = g2d.getFontMetrics(getFont());
		syntaxScheme.refreshFontMetrics(g2d);
		if (getLineWrap()==false) {
			// HORRIBLE HACK!  The un-wrapped view needs to refresh its cached
			// longest line information.
			SyntaxView sv = (SyntaxView)getUI().getRootView(this).getView(0);
			sv.calculateLongestLine();
		}
	}

	/**
	 * Overridden so we stop this text area's parsers, if any.
	 */
	public void removeNotify() {
		if (parserManager!=null) {
			parserManager.stopParsing();
		}
		super.removeNotify();
	}


	/**
	 * Removes a parser from this text area.
	 *
	 * @param parser The {@link Parser} to remove.
	 * @return Whether the parser was found and removed.
	 * @see #clearParsers()
	 * @see #addParser(Parser)
	 * @see #getParser(int)
	 */
	public boolean removeParser(Parser parser) {
		boolean removed = false;
		if (parserManager!=null) {
			removed = parserManager.removeParser(parser);
		}
		return removed;
	}


	/**
	 * Sets the colors used for syntax highlighting to their defaults.
	 *
	 * @see #setSyntaxScheme(SyntaxScheme)
	 * @see #getSyntaxScheme()
	 * @see #getDefaultSyntaxScheme()
	 */
	public void restoreDefaultSyntaxScheme() {
		setSyntaxScheme(getDefaultSyntaxScheme());
	}


	/**
	 * Sets whether bracket matching should be animated.  This fires a property
	 * change event of type {@link #ANIMATE_BRACKET_MATCHING_PROPERTY}.
	 *
	 * @param animate Whether to animate bracket matching.
	 * @see #getAnimateBracketMatching()
	 */
	public void setAnimateBracketMatching(boolean animate) {
		if (animate!=animateBracketMatching) {
			animateBracketMatching = animate;
			if (animate && bracketRepaintTimer==null) {
				bracketRepaintTimer = new BracketMatchingTimer();
			}
			firePropertyChange(ANIMATE_BRACKET_MATCHING_PROPERTY,
								!animate, animate);
		}
	}


	/**
	 * Sets whether anti-aliasing is enabled in this editor.  This method
	 * fires a property change event of type {@link #ANTIALIAS_PROPERTY}.
	 *
	 * @param enabled Whether anti-aliasing is enabled.
	 * @see #getAntiAliasingEnabled()
	 */
	public void setAntiAliasingEnabled(boolean enabled) {

		boolean currentlyEnabled = aaHints!=null;

		if (enabled!=currentlyEnabled) {

			if (enabled) {
				aaHints = RSyntaxUtilities.getDesktopAntiAliasHints();
				// If the desktop query method comes up empty, use the standard
				// Java2D greyscale method.  Note this will likely NOT be as
				// nice as what would be used if the getDesktopAntiAliasHints()
				// call worked.
				if (aaHints==null) {
					aaHints = new HashMap();
					aaHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				}
			}
			else {
				aaHints = null;
			}

			// We must be connected to a screen resource for our graphics
			// to be non-null.
			if (isDisplayable()) {
				refreshFontMetrics(getGraphics2D(getGraphics()));
			}
			firePropertyChange(ANTIALIAS_PROPERTY, !enabled, enabled);
			repaint();

		}

	}


	/**
	 * Sets whether bracket matching is enabled.  This fires a property change
	 * event of type {@link #BRACKET_MATCHING_PROPERTY}.
	 *
	 * @param enabled Whether or not bracket matching should be enabled.
	 * @see #isBracketMatchingEnabled()
	 */
	public void setBracketMatchingEnabled(boolean enabled) {
		if (enabled!=bracketMatchingEnabled) {
			bracketMatchingEnabled = enabled;
			repaint();
			firePropertyChange(BRACKET_MATCHING_PROPERTY, !enabled, enabled);
		}
	}


	/**
	 * Sets whether code folding is enabled.  Note that only certain
	 * languages will support code folding out of the box.  Those languages
	 * which do not support folding will ignore this property.<p>
	 * This method fires a property change event of type
	 * {@link #CODE_FOLDING_PROPERTY}.
	 *
	 * @param enabled Whether code folding should be enabled.
	 * @see #isCodeFoldingEnabled()
	 */
	public void setCodeFoldingEnabled(boolean enabled) {
		if (enabled!=foldManager.isCodeFoldingEnabled()) {
			foldManager.setCodeFoldingEnabled(enabled);
			firePropertyChange(CODE_FOLDING_PROPERTY, !enabled, enabled);
		}
	}


	/**
	 * Sets the document used by this text area.  This is overridden so that
	 * only instances of {@link RSyntaxDocument} are accepted; for all
	 * others, an exception will be thrown.
	 *
	 * @param document The new document for this text area.
	 * @throws IllegalArgumentException If the document is not an
	 *         <code>RSyntaxDocument</code>.
	 */
	public void setDocument(Document document) {
		if (!(document instanceof RSyntaxDocument))
			throw new IllegalArgumentException("Documents for " +
					"RSyntaxTextArea must be instances of " +
					"RSyntaxDocument!");
		super.setDocument(document);
	}

	/**
	 * Sets the highlighter used by this text area.
	 *
	 * @param h The highlighter.
	 * @throws IllegalArgumentException If <code>h</code> is not an instance
	 *         of {@link RSyntaxTextAreaHighlighter}.
	 */
	public void setHighlighter(Highlighter h) {
		if (!(h instanceof RSyntaxTextAreaHighlighter)) {
			throw new IllegalArgumentException("RSyntaxTextArea requires " +
				"an RSyntaxTextAreaHighlighter for its Highlighter");
		}
		super.setHighlighter(h);
	}

	/**
	 * Sets the color used as the background for a matched bracket.
	 *
	 * @param color The color to use.  If this is <code>null</code>, then no
	 *        special background is painted behind a matched bracket.
	 * @see #getMatchedBracketBGColor
	 * @see #setMatchedBracketBorderColor
	 * @see #setPaintMarkOccurrencesBorder(boolean)
	 */
	public void setMatchedBracketBGColor(Color color) {
		matchedBracketBGColor = color;
		if (match!=null)
			repaint();
	}


	/**
	 * Sets the color used as the border for a matched bracket.
	 *
	 * @param color The color to use.
	 * @see #getMatchedBracketBorderColor
	 * @see #setMatchedBracketBGColor
	 */
	public void setMatchedBracketBorderColor(Color color) {
		matchedBracketBorderColor = color;
		if (match!=null)
			repaint();
	}


	/**
	 * Sets what type of syntax highlighting this editor is doing.  This method
	 * fires a property change of type {@link #SYNTAX_STYLE_PROPERTY}.
	 *
	 * @param styleKey The syntax editing style to use, for example,
	 *        {@link SyntaxConstants#SYNTAX_STYLE_NONE} or
	 *        {@link SyntaxConstants#SYNTAX_STYLE_JAVA}.
	 * @see #getSyntaxEditingStyle()
	 * @see SyntaxConstants
	 */
	public void setSyntaxEditingStyle(String styleKey) {
		if (styleKey==null) {
			styleKey = SYNTAX_STYLE_NONE;
		}
		if (!styleKey.equals(syntaxStyleKey)) {
			String oldStyle = syntaxStyleKey;
			syntaxStyleKey = styleKey;
			((RSyntaxDocument)getDocument()).setSyntaxStyle(styleKey);
			firePropertyChange(SYNTAX_STYLE_PROPERTY, oldStyle, styleKey);
		}

	}


	/**
	 * Sets all of the colors used in syntax highlighting to the colors
	 * specified.  This uses a shallow copy of the color scheme so that
	 * multiple text areas can share the same color scheme and have their
	 * properties changed simultaneously.<p>
	 *
	 * This method fires a property change event of type
	 * {@link #SYNTAX_SCHEME_PROPERTY}.
	 *
	 * @param scheme The instance of <code>SyntaxScheme</code> to use.
	 * @see #getSyntaxScheme()
	 */
	public void setSyntaxScheme(SyntaxScheme scheme) {

		// NOTE:  We don't check whether colorScheme is the same as the
		// current scheme because DecreaseFontSizeAction and
		// IncreaseFontSizeAction need it this way.
		// FIXME:  Find a way around this.

		SyntaxScheme old = this.syntaxScheme;
		this.syntaxScheme = scheme;

		// Recalculate the line height.  We do this here instead of in
		// refreshFontMetrics() as this method is called less often and we
		// don't need the rendering hints to get the font's height.
		calculateLineHeight();

		if (isDisplayable()) {
			refreshFontMetrics(getGraphics2D(getGraphics()));
		}

		// Updates the margin line.
		updateMarginLineX();

		// Force the current line highlight to be repainted, even though
		// the caret's location hasn't changed.
		forceCurrentLineHighlightRepaint();

		// So encompassing JScrollPane will have its scrollbars updated.
		revalidate();

		firePropertyChange(SYNTAX_SCHEME_PROPERTY, old, this.syntaxScheme);

	}

	/**
	 * Returns the token at the specified position in the view.
	 *
	 * @param p The position in the view.
	 * @return The token, or <code>null</code> if no token is at that
	 *         position.
	 * @see #modelToToken(int)
	 */
	/*
	 * TODO: This is a little inefficient.  This should convert view
	 * coordinates to the underlying token (if any).  The way things currently
	 * are, we're calling getTokenListForLine() twice (once in viewToModel()
	 * and once here).
	 */
	private Token viewToToken(Point p) {
		return modelToToken(viewToModel(p));
	}


	/**
	 * A timer that animates the "bracket matching" animation.
	 */
	private class BracketMatchingTimer extends Timer implements ActionListener {

		private int pulseCount;

		public BracketMatchingTimer() {
			super(20, null);
			addActionListener(this);
			setCoalesce(false);
		}

		public void actionPerformed(ActionEvent e) {
			if (isBracketMatchingEnabled()) {
				if (match!=null) {
					if (pulseCount<5) {
						pulseCount++;
						match.x--;
						match.y--;
						match.width += 2;
						match.height += 2;
						repaint(match.x,match.y, match.width,match.height);
					}
					else if (pulseCount<7) {
						pulseCount++;
						match.x++;
						match.y++;
						match.width -= 2;
						match.height -= 2;
						repaint(match.x-2,match.y-2, match.width+5,match.height+5);
					}
					else {
						stop();
						pulseCount = 0;
					}
				}
			}
		}

		public void start() {
			match.x += 3;
			match.y += 3;
			match.width -= 6;
			match.height -= 6; // So animation can "grow" match
			pulseCount = 0;
			super.start();
		}

	}

}