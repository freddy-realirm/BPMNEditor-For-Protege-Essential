package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerEntity;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerRole;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;

/**
 * PartnerPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class PartnerPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;

	protected XMLTablePanel flowElementsPanel;

	public PartnerPanel(BPMNPanelContainer pc, BaseElement owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));

		this.add(new XMLTextPanel(pc, owner.get("id"), false));
		this.add(namePanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("participant");
		XMLElement el = null;
		List<XMLElement> elList = null;
		if (owner instanceof PartnerEntity) {
			el = ((PartnerEntity) owner).getParticipantRefs();
			elList = ((PartnerEntity) owner).getParticipantRefList();
		} else if (owner instanceof PartnerRole) {
			el = ((PartnerRole) owner).getParticipantRefs();
			elList = ((PartnerRole) owner).getParticipantRefList();
		}
		this.add(new XMLTablePanel(getPanelContainer(), el, "", "participantRefs", columnsToShow, elList, 300, 120, true, false));

	}

	public void saveObjects() {
		namePanel.saveObjects();
		super.saveObjects();
	}

}
