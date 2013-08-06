package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.util.Resources;

/**
 * ProcessPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ProcessPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLTextPanel idPanel;
	protected XMLPanel namePanel;
	protected XMLComboPanel processTypePanel;
	protected XMLCheckboxPanel isExecutablePanel;
	protected XMLCheckboxPanel isClosedPanel;

	public ProcessPanel(BPMNPanelContainer pc, BPMNProcess owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));
		idPanel = new XMLTextPanel(pc, owner.get("id"));
		List<String> choices = new ArrayList<String>();
		choices.add(BPMNProcess.ProcessType.None.toString());
		choices.add(BPMNProcess.ProcessType.Private.toString());
		choices.add(BPMNProcess.ProcessType.Public.toString());
		processTypePanel = new XMLComboPanel(pc, owner.get("processType"), null, choices, false, false, true);
		isExecutablePanel = new XMLCheckboxPanel(pc, owner.get("isExecutable"), false);
		isClosedPanel = new XMLCheckboxPanel(pc, owner.get("isClosed"), false);

		JPanel exeClosePanel = new JPanel();
		exeClosePanel.setLayout(new BoxLayout(exeClosePanel, BoxLayout.X_AXIS));
		exeClosePanel.add(isExecutablePanel);
		exeClosePanel.add(Box.createHorizontalGlue());
		exeClosePanel.add(isClosedPanel);

		this.add(idPanel);
		this.add(namePanel);
		this.add(processTypePanel);
		this.add(exeClosePanel);
	}

	public void saveObjects() {
		Definitions definitions = getGraphComponent().getGraph().getBpmnModel();
		String oldId = ((BaseElement) owner).getId();
		String newId = idPanel.getText();
		if (newId.length() > 0 && !oldId.equals(newId) && definitions.getProcess(newId) == null) {
			Participant participant = BPMNModelUtils.getParticipantByProcessId(oldId, definitions);
			if (participant != null) {
				participant.setProcessRef(newId);
			}
			idPanel.saveObjects();
		} else if (definitions.getProcess(newId) != null && !oldId.equals(newId)) {
			JOptionPane.showMessageDialog(null, Resources.get("warningElementWithTheSameIdAlreadyExists"), Resources.get("WarningConflict"),
					JOptionPane.WARNING_MESSAGE);
		}
		namePanel.saveObjects();
		processTypePanel.saveObjects();
		isExecutablePanel.saveObjects();
		isClosedPanel.saveObjects();
	}

}
