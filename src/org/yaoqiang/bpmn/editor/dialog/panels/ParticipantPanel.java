package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;

/**
 * ParticipantPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ParticipantPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;
	
	public ParticipantPanel(BPMNPanelContainer pc, Participant owner) {
		super(pc, owner);
		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("interface");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getInterfaceRefs(), "", "interfaceRefs", columnsToShow, owner.getInterfaceRefList(), 300, 120, true,
				false));
	}

}
