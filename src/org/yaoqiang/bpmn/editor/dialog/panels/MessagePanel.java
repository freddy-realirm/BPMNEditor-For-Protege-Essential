package org.yaoqiang.bpmn.editor.dialog.panels;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.core.common.Message;

/**
 * MessagePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class MessagePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel itemRefPanel;
	protected XMLPanel namePanel;
	
	public MessagePanel(BPMNPanelContainer pc, Message owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"), true);
		itemRefPanel = new XMLComboPanel(pc, owner.get("itemRef"), null, BPMNModelUtils.getAllItemDefinitions(owner), true, false, true);
		this.add(new XMLTextPanel(pc, owner.get("id"), false));
		this.add(namePanel);		
		this.add(itemRefPanel);
	}

	public void saveObjects() {
		namePanel.saveObjects();
		itemRefPanel.saveObjects();
		super.saveObjects();
	}

}
