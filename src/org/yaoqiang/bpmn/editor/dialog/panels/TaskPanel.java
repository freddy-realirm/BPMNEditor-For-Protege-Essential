package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.activities.ScriptTask;
import org.yaoqiang.bpmn.model.elements.activities.Task;

/**
 * TaskPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class TaskPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;
	
	protected String type;
	
	protected XMLComboPanel comboPanel;

	protected XMLComboPanel operationPanel;

	protected XMLComboPanel messagePanel;

	protected XMLPanel mtp;
	
	protected XMLTextPanel uriPanel;

	public TaskPanel(BPMNPanelContainer pc, Task owner) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		type = owner.toName();
		if (type.equals("scriptTask")) {
			List<String> choices = new ArrayList<String>();
			choices.add("text/x-groovy");
			choices.add("text/javascript");
			choices.add("http://www.java.com/java");
			choices.add("http://www.mvel.org/2.0");
			comboPanel = new XMLComboPanel(pc, owner.get("scriptFormat"), choices, false, true, true);
			mtp = new XMLMultiLineTextPanel(pc, ((ScriptTask)owner).getScriptElement(), "script", 350, 150);
			this.add(comboPanel, BorderLayout.NORTH);
			this.add(mtp, BorderLayout.CENTER);
			this.add(Box.createVerticalGlue(), BorderLayout.SOUTH);
		} else {
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
			this.add(implPanel, BorderLayout.NORTH);

			if (type.equals("serviceTask") || type.equals("sendTask") || type.equals("receiveTask")) {	
				if (!type.equals("serviceTask")) {
					messagePanel = new XMLComboPanel(pc, owner.get("messageRef"), "messageRef", BPMNModelUtils.getDefinitions(owner).getMessages(), true, false, true);
					this.add(messagePanel, BorderLayout.CENTER);
				}
				operationPanel = new XMLComboPanel(pc, owner.get("operationRef"), "operationRef", BPMNModelUtils.getDefinitions(owner).getOperations(), true, false, true);
				this.add(operationPanel, BorderLayout.SOUTH);
			}
		}
		
	}
	
	public void saveObjects() {
		if (type.equals("scriptTask")) {
			comboPanel.saveObjects();
			mtp.saveObjects();
		} else {
			String impl = "##unspecified";
			Object sel = comboPanel.getSelectedItem();
			if (sel.equals("URI")) {
				impl = uriPanel.getText();
			} else if (sel.equals("WebService")) {
				impl = "##WebService";
			}
			((Task)owner).get("implementation").setValue(impl);
			if (type.equals("serviceTask") || type.equals("sendTask") || type.equals("receiveTask")) {	
				if (!type.equals("serviceTask")) {
					messagePanel.saveObjects();
				}
				operationPanel.saveObjects();
			}
		}
	}
	
	
}
