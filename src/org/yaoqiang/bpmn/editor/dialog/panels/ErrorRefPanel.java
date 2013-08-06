package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.List;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLTextElement;
import org.yaoqiang.bpmn.model.elements.core.service.Operation;

/**
 * ErrorRefPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ErrorRefPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel errorPanel;

	public ErrorRefPanel(BPMNPanelContainer pc, XMLTextElement owner) {
		super(pc, owner);
		XMLElement chn = BPMNModelUtils.getDefinitions(owner).getError(owner.toValue());
		List<XMLElement> choices = BPMNModelUtils.getDefinitions(owner).getErrors();
		List<XMLElement> refList = ((Operation) owner.getParent().getParent()).getRefErrorList();
		choices.removeAll(refList);
		if (refList.contains(chn)) {
			choices.add(chn);
		}
		errorPanel = new XMLComboPanel(pc, owner, null, choices, false, false, true);
		this.add(errorPanel);
	}

	public void saveObjects() {
		errorPanel.saveObjects();
		if (!errorPanel.isEmpty()) {
			super.saveObjects();
		}
	}

}
