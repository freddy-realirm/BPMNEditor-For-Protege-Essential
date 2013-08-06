package org.yaoqiang.bpmn.editor.dialog;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Resources;


/**
 * XMLMultiLineTextPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLMultiLineTextPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected RSyntaxTextArea jta;

	public XMLMultiLineTextPanel(PanelContainer pc, XMLElement owner, String title, int width, int height) {
		this(pc, owner, title, SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT, width, height);
	}

	public XMLMultiLineTextPanel(PanelContainer pc, XMLElement owner, String title, String style, int width, int height) {
		super(pc, owner);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		if (title != null && title.length() != 0) {
			this.setBorder(BorderFactory.createTitledBorder(Resources.get(title)));
		} else {
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}

		Dimension dim = new Dimension(width, height);
		jta = new RSyntaxTextArea();
		jta.setEditable(true);
		jta.setSyntaxEditingStyle(style);
		jta.setCodeFoldingEnabled(true);
		jta.setAntiAliasingEnabled(true);
		if (owner != null) {
			jta.setText(owner.toValue());
		}
		jta.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				getPanelContainer().panelChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
		});

		RTextScrollPane sp = new RTextScrollPane(jta);
		sp.setFoldIndicatorEnabled(true);
		sp.setPreferredSize(dim);
		this.add(sp);
	}

	public void saveObjects() {
		getOwner().setValue(getText());
		if (owner instanceof XMLExtensionElement && getOwner().toValue().length() == 0) {
			((XMLExtensionElement) getOwner().getParent()).removeChildElement((XMLExtensionElement) owner);
		}
	}

	public String getText() {
		return jta.getText().trim();
	}

	public RSyntaxTextArea getTextArea() {
		return jta;
	}

	public void setText(String text) {
		jta.setText(text);
	}

	public void appendText(String txt) {
		int cp = jta.getCaretPosition();
		String ct = jta.getText();
		String text = ct.substring(0, cp) + txt + ct.substring(cp);
		jta.setText(text);
		jta.setCaretPosition(cp + txt.length());
		jta.requestFocus();
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jta.setEditable(b);
		jta.setEnabled(b);
	}

}
