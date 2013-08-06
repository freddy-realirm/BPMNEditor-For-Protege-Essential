package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.List;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLTextElement;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerEntity;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerRole;

/**
 * ParticipantRefPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ParticipantRefPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel partPanel;

	public ParticipantRefPanel(BPMNPanelContainer pc, XMLTextElement owner) {
		super(pc, owner);
		XMLElement chn = BPMNModelUtils.getDefinitions(owner).getParticipant(owner.toValue());
		List<XMLElement> choices = BPMNModelUtils.getDefinitions(owner).getParticipants();
		List<XMLElement> refList = null;
		XMLElement parent = owner.getParent().getParent();
		if (parent instanceof PartnerEntity) {
			refList = ((PartnerEntity) parent).getRefParticipantList();
		} else if (parent instanceof PartnerRole) {
			refList = ((PartnerRole) parent).getRefParticipantList();
		}
		choices.removeAll(refList);
		if (refList.contains(chn)) {
			choices.add(chn);
		}
		partPanel = new XMLComboPanel(pc, owner, null, choices, false, false, true);
		this.add(partPanel);
	}

	public void saveObjects() {
		partPanel.saveObjects();
		if (!partPanel.isEmpty()) {
			super.saveObjects();
		}
	}

}
