package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.events.ThrowEvent;

/**
 * DataInputsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DataInputsPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	public DataInputsPanel(BPMNPanelContainer pc, ThrowEvent owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("isCollection");
		columnsToShow.add("itemSubjectRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner, "dataInputs", null, columnsToShow, owner.getDataInputList(), 500, 100, true, false));

		columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("targetRef");
		columnsToShow.add("sourceRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner, "dataInputAssociations", "dataInputAssociations", columnsToShow, owner.getDataInputAssociationList(), 500, 100, true, false));

	}

	public void saveObjects() {

	}

}
