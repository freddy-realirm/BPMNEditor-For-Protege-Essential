package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.List;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLTextElement;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;

/**
 * InterfaceRefPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class InterfaceRefPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel interfacePanel;

	public InterfaceRefPanel(BPMNPanelContainer pc, XMLTextElement owner) {
		super(pc, owner);
		XMLElement chn = BPMNModelUtils.getDefinitions(owner).getInterface(owner.toValue());
		List<XMLElement> choices = BPMNModelUtils.getDefinitions(owner).getInterfaces();
		List<XMLElement> refList = ((Participant) owner.getParent().getParent()).getRefInterfaceList();
		choices.removeAll(refList);
		if (refList.contains(chn)) {
			choices.add(chn);
		}
		interfacePanel = new XMLComboPanel(pc, owner, null, choices, false, false, true);
		this.add(interfacePanel);
	}

	public void saveObjects() {
		interfacePanel.saveObjects();
		if (!interfacePanel.isEmpty()) {
			super.saveObjects();
		}
	}

}
