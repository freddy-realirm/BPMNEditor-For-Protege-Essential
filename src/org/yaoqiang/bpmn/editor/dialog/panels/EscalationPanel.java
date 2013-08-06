package org.yaoqiang.bpmn.editor.dialog.panels;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.events.Escalation;

/**
 * EscalationPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class EscalationPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel structureRefPanel;
	protected XMLPanel namePanel;
	protected XMLPanel escalationCodePanel;
	
	public EscalationPanel(BPMNPanelContainer pc, Escalation owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		namePanel = new XMLTextPanel(pc, owner.get("name"), true);
		escalationCodePanel = new XMLTextPanel(pc, owner.get("escalationCode"), true);
		structureRefPanel = new XMLComboPanel(pc, owner.get("structureRef"), null, BPMNModelUtils.getAllItemDefinitions(owner), true, false, true);
		this.add(new XMLTextPanel(pc, owner.get("id"), false));
		this.add(namePanel);		
		this.add(escalationCodePanel);		
		this.add(structureRefPanel);
	}

	public void saveObjects() {
		namePanel.saveObjects();
		escalationCodePanel.saveObjects();
		structureRefPanel.saveObjects();
		super.saveObjects();
	}

}
