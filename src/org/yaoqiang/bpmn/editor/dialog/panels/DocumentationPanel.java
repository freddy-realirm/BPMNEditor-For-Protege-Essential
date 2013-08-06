package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.XMLChoosenPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.core.foundation.Documentation;
import org.yaoqiang.util.Resources;


/**
 * DocumentationPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DocumentationPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel formatPanel;
	protected XMLChoosenPanel textPanel;

	public DocumentationPanel(BPMNPanelContainer pc, final Documentation owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		List<String> choices = new ArrayList<String>();
		choices.add("plainText");
		choices.add("attachment");
		formatPanel = new XMLComboPanel(pc, owner, "type", choices, false, false, true);
		JPanel idFormatPanel = new JPanel();
		idFormatPanel.setLayout(new BoxLayout(idFormatPanel, BoxLayout.X_AXIS));
		idFormatPanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idFormatPanel.add(Box.createHorizontalGlue());
		idFormatPanel.add(formatPanel);
		textPanel = new XMLChoosenPanel(pc, owner, null, 400, 160);

		this.add(idFormatPanel);
		this.add(textPanel);

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object sel = formatPanel.getSelectedItem();
				if (sel.equals(Resources.get("plainText"))) {
					owner.setTextFormat("text/plain");
				} else {
					owner.setTextFormat("");
				}
				textPanel.setChoosen(owner);
				textPanel.repaint();
			}
		};
		formatPanel.addActionListener(al);
	}

	public void saveObjects() {
		Object sel = formatPanel.getSelectedItem();
		if (sel.equals(Resources.get("plainText"))) {
			((Documentation) owner).setTextFormat("text/plain");
		}
		textPanel.saveObjects();
		super.saveObjects();
	}

}
