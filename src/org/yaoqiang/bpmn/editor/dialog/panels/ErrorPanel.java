package org.yaoqiang.bpmn.editor.dialog.panels;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.core.common.BPMNError;

/**
 * ErrorPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ErrorPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel structureRefPanel;
	protected XMLPanel namePanel;
	protected XMLPanel errorCodePanel;
	
	public ErrorPanel(BPMNPanelContainer pc, BPMNError owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		namePanel = new XMLTextPanel(pc, owner.get("name"), true);
		errorCodePanel = new XMLTextPanel(pc, owner.get("errorCode"), true);
		structureRefPanel = new XMLComboPanel(pc, owner.get("structureRef"), null, BPMNModelUtils.getAllItemDefinitions(owner), true, false, true);
		this.add(new XMLTextPanel(pc, owner.get("id"), false));
		this.add(namePanel);		
		this.add(errorCodePanel);		
		this.add(structureRefPanel);
	}

	public void saveObjects() {
		namePanel.saveObjects();
		errorCodePanel.saveObjects();
		structureRefPanel.saveObjects();
		super.saveObjects();
	}

}
