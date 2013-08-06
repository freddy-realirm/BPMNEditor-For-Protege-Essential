package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.core.service.Interface;

/**
 * InterfacePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class InterfacePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;

	protected XMLPanel implPanel;

	public InterfacePanel(BPMNPanelContainer pc, Interface owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));
		implPanel = new XMLTextPanel(pc, owner.get("implementationRef"));
		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
		idNamePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idNamePanel.add(namePanel);
		idNamePanel.add(implPanel);
		this.add(idNamePanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("inMessageRef");
		columnsToShow.add("outMessageRef");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getOperations(), "", "operations", columnsToShow, owner.getOperationList(), 500, 120, true,
				false));
	}

	public void saveObjects() {
		namePanel.saveObjects();
		implPanel.saveObjects();
		super.saveObjects();
	}

}
