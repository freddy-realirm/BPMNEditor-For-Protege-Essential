package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.activities.GlobalScriptTask;
import org.yaoqiang.bpmn.model.elements.process.GlobalTask;

/**
 * GlobalTaskPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class GlobalTaskPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;
	
	protected String type;

	protected XMLPanel namePanel;
	
	protected XMLComboPanel comboPanel;

	protected XMLPanel mtp;
	
	protected XMLTextPanel uriPanel;

	public GlobalTaskPanel(BPMNPanelContainer pc, GlobalTask owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		type = owner.toName();
		
		namePanel = new XMLTextPanel(pc, owner.get("name"));

		this.add(new XMLTextPanel(pc, owner.get("id"), false));
		this.add(namePanel);

		if (type.equals("globalScriptTask")) {
			List<String> choices = new ArrayList<String>();
			choices.add("text/x-groovy");
			choices.add("text/javascript");
			comboPanel = new XMLComboPanel(pc, owner.get("scriptLanguage"), choices, false, true, true);
			mtp = new XMLMultiLineTextPanel(pc, ((GlobalScriptTask)owner).getScriptElement(), "script", 350, 150);
			this.add(comboPanel);
			this.add(mtp);
			this.add(Box.createVerticalGlue());
		} else if (type.equals("globalUserTask") || type.equals("globalBusinessRuleTask")) {
			final Component emptyPanel = Box.createVerticalStrut(38);
			List<String> choices = new ArrayList<String>();
			choices.add("unspecified");
			choices.add("WebService");
			choices.add("URI");
			comboPanel = new XMLComboPanel(pc, owner.get("implementation"), "implementation", choices, false, false, true);
			comboPanel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Object sel = comboPanel.getSelectedItem();
					if (sel.equals("URI")) {
						emptyPanel.setVisible(false);
						uriPanel.setVisible(true);
					} else {
						uriPanel.setVisible(false);
						emptyPanel.setVisible(true);
					}
				}
			});

			uriPanel = new XMLTextPanel(pc, owner.get("implementation"), "uri", "");
			if (comboPanel.getSelectedItem().equals("URI")) {
				emptyPanel.setVisible(false);
				uriPanel.setVisible(true);
				uriPanel.setText(owner.get("implementation").toValue());
			} else {
				uriPanel.setVisible(false);
				emptyPanel.setVisible(true);
			}
			
			JPanel implPanel = new JPanel();
			implPanel.setLayout(new BoxLayout(implPanel, BoxLayout.Y_AXIS));
			implPanel.add(comboPanel);
			implPanel.add(uriPanel);
			implPanel.add(emptyPanel);
			this.add(implPanel);
		}
		
	}
	
	public void saveObjects() {
		namePanel.saveObjects();
		if (type.equals("globalScriptTask")) {
			comboPanel.saveObjects();
			mtp.saveObjects();
		} else if (type.equals("globalUserTask") || type.equals("globalBusinessRuleTask")) {
			String impl = "##unspecified";
			Object sel = comboPanel.getSelectedItem();
			if (sel.equals("URI")) {
				impl = uriPanel.getText();
			} else if (sel.equals("WebService")) {
				impl = "##WebService";
			}
			((GlobalTask)owner).get("implementation").setValue(impl);
		}
		super.saveObjects();
	}
	
	
}
