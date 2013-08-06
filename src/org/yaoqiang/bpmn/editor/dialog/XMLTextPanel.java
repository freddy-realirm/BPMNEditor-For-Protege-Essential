package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Resources;


/**
 * XMLTextPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLTextPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected JTextField jtf;

	protected JLabel label;

	public XMLTextPanel(PanelContainer pc, XMLElement owner) {
		this(pc, owner, null, null, 150, 27, true);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, String title) {
		this(pc, owner, title, null, 150, 27, true);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, String title, String value) {
		this(pc, owner, title, value, 150, 27, true);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, boolean enabled) {
		this(pc, owner, null, null, 150, 27, enabled);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, String title, boolean enabled) {
		this(pc, owner, title, null, 150, 27, enabled);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, int width, int height) {
		this(pc, owner, null, null, width, height, true);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, int width, int height, boolean enabled) {
		this(pc, owner, null, null, width, height, enabled);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, String title, int width, int height) {
		this(pc, owner, title, null, width, height, true);
	}

	public XMLTextPanel(PanelContainer pc, XMLElement owner, String title, String value, int width, int height, boolean enabled) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		label = new JLabel(Resources.get(title == null ? owner.toName() : title) + ": ");

		jtf = new JTextField();
		jtf.setText(value == null ? owner.toValue() : value);

		Dimension textDim = new Dimension(width, height);
		jtf.setMinimumSize(new Dimension(textDim));
		jtf.setMaximumSize(new Dimension(textDim));
		jtf.setPreferredSize(new Dimension(textDim));
		jtf.setEnabled(enabled);

		jtf.getDocument().addDocumentListener(new DocumentListener() {
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

		this.add(label, BorderLayout.WEST);
		this.add(Box.createHorizontalGlue(), BorderLayout.EAST);
		this.add(jtf, BorderLayout.CENTER);
	}

	public void saveObjects() {
		getOwner().setValue(getText());
		if (owner instanceof XMLExtensionElement && getOwner().toValue().length() == 0) {
			((XMLExtensionElement) getOwner().getParent()).removeChildElement((XMLExtensionElement) owner);
		}
	}

	public JTextField getTextField() {
		return jtf;
	}

	public JLabel getLabel() {
		return label;
	}

	public String getText() {
		return jtf.getText().trim();
	}

	public void setText(String text) {
		jtf.setText(text);
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jtf.setEnabled(b);
	}

}
