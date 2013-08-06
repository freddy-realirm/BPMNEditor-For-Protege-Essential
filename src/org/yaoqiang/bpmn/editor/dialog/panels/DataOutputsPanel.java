package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.events.CatchEvent;

/**
 * DataOutputsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DataOutputsPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	public DataOutputsPanel(BPMNPanelContainer pc, CatchEvent owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("isCollection");
		columnsToShow.add("itemSubjectRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner, "dataOutputs", null, columnsToShow, owner.getDataOutputList(), 500, 100, true, false));

		columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("targetRef");
		columnsToShow.add("sourceRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner, "dataOutputAssociations", "dataOutputAssociations", columnsToShow, owner.getDataOutputAssociationList(), 500, 100, true, false));

	}

	public void saveObjects() {

	}

}
