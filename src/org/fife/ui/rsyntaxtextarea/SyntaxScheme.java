/*
 * 02/26/2004
 *
 * SyntaxScheme.java - The set of colors and tokens used by an RSyntaxTextArea
 * to color tokens.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.text.StyleContext;


/**
 * The set of colors and styles used by an <code>RSyntaxTextArea</code> to
 * color tokens.  You can use this class to programmatically set the fonts
 * and colors used in an RSyntaxTextArea, but for more powerful, externalized
 * control, consider using {@link Theme}s instead.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see Theme
 */
public class SyntaxScheme implements Cloneable, TokenTypes {

	private Style[] styles;

	/**
	 * Creates a color scheme that either has all color values set to
	 * a default value or set to <code>null</code>.
	 *
	 * @param useDefaults If <code>true</code>, all color values will
	 *        be set to default colors; if <code>false</code>, all colors
	 *        will be initially <code>null</code>.
	 */
	public SyntaxScheme(boolean useDefaults) {
		styles = new Style[NUM_TOKEN_TYPES];
		if (useDefaults) {
			restoreDefaults(null);
		}
	}


	/**
	 * Creates a default color scheme.
	 *
	 * @param baseFont The base font to use.  Keywords will be a bold version
	 *        of this font, and comments will be an italicized version of this
	 *        font.
	 */
	public SyntaxScheme(Font baseFont) {
		this(baseFont, true);
	}


	/**
	 * Creates a default color scheme.
	 *
	 * @param baseFont The base font to use.  Keywords will be a bold version
	 *        of this font, and comments will be an italicized version of this
	 *        font.
	 * @param fontStyles Whether bold and italic should be used in the scheme
	 *        (vs. all tokens using a plain font).
	 */
	public SyntaxScheme(Font baseFont, boolean fontStyles) {
		styles = new Style[NUM_TOKEN_TYPES];
		restoreDefaults(baseFont, fontStyles);
	}


	/**
	 * Returns a deep copy of this color scheme.
	 *
	 * @return The copy.
	 */
	public Object clone() {
		SyntaxScheme shcs = null;
		try {
			shcs = (SyntaxScheme)super.clone();
		} catch (CloneNotSupportedException cnse) { // Never happens
			cnse.printStackTrace();
			return null;
		}
		shcs.styles = new Style[NUM_TOKEN_TYPES];
		for (int i=0; i<NUM_TOKEN_TYPES; i++) {
			Style s = styles[i];
			if (s!=null) {
				shcs.styles[i] = (Style)s.clone();
			}
		}
		return shcs;
	}


	/**
	 * Tests whether this color scheme is the same as another color scheme.
	 *
	 * @param otherScheme The color scheme to compare to.
	 * @return <code>true</code> if this color scheme and
	 *         <code>otherScheme</code> are the same scheme;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object otherScheme) {

		// No need for null check; instanceof takes care of this for us,
		// i.e. "if (!(null instanceof Foo))" evaluates to "true".
		if (!(otherScheme instanceof SyntaxScheme)) {
			return false;
		}

		Style[] otherSchemes = ((SyntaxScheme)otherScheme).styles;

		int length = styles.length;
		for (int i=0; i<length; i++) {
			if (styles[i]==null) {
				if (otherSchemes[i]!=null) {
					return false;
				}
			}
			else if (!styles[i].equals(otherSchemes[i])) {
				return false;
			}
		}
		return true;

	}



	/**
	 * Returns the specified style.
	 *
	 * @param index The index of the style.
	 * @return The style.
	 * @see #setStyle(int, Style)
	 * @see #getStyleCount()
	 */
	public Style getStyle(int index) {
		return styles[index];
	}


	/**
	 * Returns the number of styles.
	 *
	 * @return The number of styles.
	 * @see #getStyle(int)
	 */
	public int getStyleCount() {
		return styles.length;
	}


	/**
	 * This is implemented to be consistent with {@link #equals(Object)}.
	 * This is a requirement to keep FindBugs happy.
	 *
	 * @return The hash code for this object.
	 */
	public int hashCode() {
		// Keep me fast.  Iterating over *all* syntax schemes contained is
		// probably much slower than a "bad" hash code here.
		int hashCode = 0;
		int count = styles.length;
		for (int i=0; i<count; i++) {
			if (styles[i]!=null) {
				hashCode ^= styles[i].hashCode();
				break;
			}
		}
		return hashCode;
	}



	void refreshFontMetrics(Graphics2D g2d) {
		// It is assumed that any rendering hints are already applied to g2d.
		for (int i=0; i<styles.length; i++) {
			Style s = styles[i];
			if (s!=null) {
				s.fontMetrics = s.font==null ? null :
								g2d.getFontMetrics(s.font);
			}
		}
	}


	/**
	 * Restores all colors and fonts to their default values.
	 *
	 * @param baseFont The base font to use when creating this scheme.  If
	 *        this is <code>null</code>, then a default monospaced font is
	 *        used.
	 */
	public void restoreDefaults(Font baseFont) {
		restoreDefaults(baseFont, true);
	}


	/**
	 * Restores all colors and fonts to their default values.
	 *
	 * @param baseFont The base font to use when creating this scheme.  If
	 *        this is <code>null</code>, then a default monospaced font is
	 *        used.
	 * @param fontStyles Whether bold and italic should be used in the scheme
	 *        (vs. all tokens using a plain font).
	 */
	public void restoreDefaults(Font baseFont, boolean fontStyles) {

		// Colors used by tokens.
		Color comment			= new Color(0,128,0);
		Color docComment		= new Color(164,0,0);
		Color keyword			= Color.BLUE;
		Color function			= new Color(173,128,0);
		Color preprocessor		= new Color(128,64,64);
		Color regex				= new Color(0,128,164);
		Color variable			= new Color(255,153,0);
		Color literalNumber		= new Color(100,0,200);
		Color literalString		= new Color(220,0,156);
		Color error			= new Color(148,148,0);

		// (Possible) special font styles for keywords and comments.
		if (baseFont==null) {
			baseFont = RSyntaxTextArea.getDefaultFont();
		}
		Font commentFont = baseFont;
		Font keywordFont = baseFont;
		if (fontStyles) {
			// WORKAROUND for Sun JRE bug 6282887 (Asian font bug in 1.4/1.5)
			StyleContext sc = StyleContext.getDefaultStyleContext();
			Font boldFont = sc.getFont(baseFont.getFamily(), Font.BOLD,
					baseFont.getSize());
			Font italicFont = sc.getFont(baseFont.getFamily(), Font.ITALIC,
					baseFont.getSize());
			commentFont = italicFont;//baseFont.deriveFont(Font.ITALIC);
			keywordFont = boldFont;//baseFont.deriveFont(Font.BOLD);
		}

		styles[COMMENT_EOL]				= new Style(comment, null, commentFont);
		styles[COMMENT_MULTILINE]			= new Style(comment, null, commentFont);
		styles[COMMENT_DOCUMENTATION]		= new Style(docComment, null, commentFont);
		styles[COMMENT_KEYWORD]			= new Style(new Color(255,152,0), null, commentFont);
		styles[COMMENT_MARKUP]			= new Style(Color.gray, null, commentFont);
		styles[RESERVED_WORD]				= new Style(keyword, null, keywordFont);
		styles[RESERVED_WORD_2]			= new Style(keyword, null, keywordFont);
		styles[FUNCTION]					= new Style(function);
		styles[LITERAL_BOOLEAN]			= new Style(literalNumber);
		styles[LITERAL_NUMBER_DECIMAL_INT]	= new Style(literalNumber);
		styles[LITERAL_NUMBER_FLOAT]		= new Style(literalNumber);
		styles[LITERAL_NUMBER_HEXADECIMAL]	= new Style(literalNumber);
		styles[LITERAL_STRING_DOUBLE_QUOTE]	= new Style(literalString);
		styles[LITERAL_CHAR]				= new Style(literalString);
		styles[LITERAL_BACKQUOTE]			= new Style(literalString);
		styles[DATA_TYPE]				= new Style(new Color(0,128,128));
		styles[VARIABLE]					= new Style(variable);
		styles[REGEX]						= new Style(regex);
		styles[ANNOTATION]				= new Style(Color.gray);
		styles[IDENTIFIER]				= new Style(null);
		styles[WHITESPACE]				= new Style(Color.gray);
		styles[SEPARATOR]				= new Style(Color.RED);
		styles[OPERATOR]					= new Style(preprocessor);
		styles[PREPROCESSOR]				= new Style(Color.gray);
		styles[MARKUP_TAG_DELIMITER]		= new Style(Color.RED);
		styles[MARKUP_TAG_NAME]			= new Style(Color.BLUE);
		styles[MARKUP_TAG_ATTRIBUTE]		= new Style(new Color(63,127,127));
		styles[MARKUP_TAG_ATTRIBUTE_VALUE]= new Style(literalString);
		styles[MARKUP_PROCESSING_INSTRUCTION] = new Style(preprocessor);
		styles[MARKUP_CDATA]				= new Style(variable);
		styles[ERROR_IDENTIFIER]			= new Style(error);
		styles[ERROR_NUMBER_FORMAT]		= new Style(error);
		styles[ERROR_STRING_DOUBLE]		= new Style(error);
		styles[ERROR_CHAR]				= new Style(error);

	}

}