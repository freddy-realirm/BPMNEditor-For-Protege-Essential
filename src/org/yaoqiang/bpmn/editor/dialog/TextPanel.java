package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.yaoqiang.dialog.Panel;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Resources;


/**
 * TextPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class TextPanel extends Panel {

	private static final long serialVersionUID = 1L;

	protected JTextField jtf;

	protected JLabel label;

	public TextPanel(final PanelContainer pc, Object owner, String title, String value, boolean enabled) {
		this(pc, owner, title, false, value, 150, 27, enabled);
	}

	public TextPanel(final PanelContainer pc, Object owner, String title, boolean password, String value, int width, int height, boolean enabled) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		label = new JLabel(Resources.get(title) + ": ");

		if (password) {
			jtf = new JPasswordField();
		} else {
			jtf = new JTextField();
		}
		jtf.setText(value);

		Dimension textDim = new Dimension(width, height);
		jtf.setMinimumSize(new Dimension(textDim));
		jtf.setMaximumSize(new Dimension(textDim));
		jtf.setPreferredSize(new Dimension(textDim));
		jtf.setEnabled(enabled);

		jtf.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				pc.panelChanged();
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
