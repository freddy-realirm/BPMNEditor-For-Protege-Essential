package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.ResourceParameterBinding;
import org.yaoqiang.bpmn.model.elements.activities.ResourceParameterBindings;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRole;
import org.yaoqiang.bpmn.model.elements.core.common.Resource;
import org.yaoqiang.bpmn.model.elements.core.common.ResourceParameter;

/**
 * ResourceParameterBindingPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ResourceParameterBindingPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel parameterPanel;

	protected XMLPanel expressionPanel;

	public ResourceParameterBindingPanel(BPMNPanelContainer pc, ResourceParameterBinding owner, Resource resource) {
		super(pc, owner);
		this.setLayout(new BorderLayout());

		XMLElement resourceName = resource.get("name");
		XMLElement resourceId = resource.get("id");
		JPanel sourceParameterPanel = new JPanel();
		sourceParameterPanel.setLayout(new BoxLayout(sourceParameterPanel, BoxLayout.X_AXIS));
		sourceParameterPanel.add(new XMLTextPanel(pc, resourceName.toValue().length() == 0 ? resourceId : resourceName, "resource", false));
		sourceParameterPanel.add(Box.createHorizontalGlue());
		parameterPanel = new XMLComboPanel(pc, owner, "parameterRef", resource.getResourceParameterList(), false, false, true);
		sourceParameterPanel.add(parameterPanel);
		this.add(sourceParameterPanel, BorderLayout.NORTH);
		expressionPanel = new ExpressionPanel(pc, owner.getExpressionElement(), "expression", 360, 100);
		this.add(expressionPanel, BorderLayout.CENTER);
		
	}

	public void saveObjects() {
		ResourceParameterBinding rpb = (ResourceParameterBinding) owner;
		Object obj = parameterPanel.getSelectedItem();
		if (obj instanceof ResourceParameter) {
			rpb.setParameterRef(((ResourceParameter) obj).getId());
		}
		expressionPanel.saveObjects();
		ResourceParameterBindings rpbs = ((ResourceParameterBindings) getOwner().getParent());
		XMLTablePanel parentPanel = (XMLTablePanel) getParentPanel();
		XMLComboPanel resourceRefPanel = ((ResourceRolePanel)parentPanel.getParent().getParent()).getResourceRefPanel();
		if (rpbs.size() == 0) {
			resourceRefPanel.setEnabled(false);
		}
		Resource resource = (Resource) resourceRefPanel.getSelectedItem();
		if (rpb.getId().length() == 0) {
			rpb.setId(rpbs.createId(((ResourceRole)rpbs.getParent().getParent().getParent()).getId() + "_" + resource.getId() + "_B"));
		}
		rpbs.add(rpb);
		parentPanel.addRow(rpb);
	}

}
