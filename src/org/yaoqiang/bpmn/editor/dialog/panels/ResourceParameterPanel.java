package org.yaoqiang.bpmn.editor.dialog.panels;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.XMLCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.core.common.ResourceParameter;

/**
 * ResourceParameterPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ResourceParameterPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;	
	protected XMLComboPanel typePanel;
	protected XMLPanel isRequiredPanel;
	
	public ResourceParameterPanel(BPMNPanelContainer pc, ResourceParameter owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		isRequiredPanel = new XMLCheckboxPanel(pc, owner.get("isRequired"), false);
		JPanel idRequiredPanel = new JPanel();
		idRequiredPanel.setLayout(new BoxLayout(idRequiredPanel, BoxLayout.X_AXIS));
		idRequiredPanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idRequiredPanel.add(Box.createHorizontalGlue());
		idRequiredPanel.add(isRequiredPanel);
		this.add(idRequiredPanel);
		
		namePanel = new XMLTextPanel(pc, owner.get("name"));
		typePanel = new XMLComboPanel(pc, owner.get("type"), null, BPMNModelUtils.getAllItemDefinitions(owner), true, false, true);
		JPanel nameTypePanel = new JPanel();
		nameTypePanel.setLayout(new BoxLayout(nameTypePanel, BoxLayout.X_AXIS));
		nameTypePanel.add(namePanel);
		nameTypePanel.add(Box.createHorizontalGlue());
		nameTypePanel.add(typePanel);
		this.add(nameTypePanel);
	}

	public void saveObjects() {
		isRequiredPanel.saveObjects();
		namePanel.saveObjects();
		typePanel.saveObjects();
		super.saveObjects();
	}

}
