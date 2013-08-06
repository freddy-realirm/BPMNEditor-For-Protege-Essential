package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.core.common.Resource;

/**
 * ResourcePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ResourcePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLTextPanel namePanel;

	public ResourcePanel(BPMNPanelContainer pc, Resource owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new XMLTextPanel(pc, owner.get("name"));

		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
		idNamePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idNamePanel.add(Box.createHorizontalGlue());
		idNamePanel.add(namePanel);
		this.add(idNamePanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("type");
		columnsToShow.add("isRequired");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getResourceParameters(), "", "resourceParameters", columnsToShow, owner.getResourceParameterList(), 400,
				150, true, false));
	}

	public void saveObjects() {
		if (namePanel.getText().trim().length() == 0) {
			return;
		}
		namePanel.saveObjects();
		super.saveObjects();
	}

}
