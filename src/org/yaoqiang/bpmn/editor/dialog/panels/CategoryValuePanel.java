package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.artifacts.CategoryValue;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElement;
import org.yaoqiang.util.Resources;

/**
 * CategoryValuePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class CategoryValuePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLTextPanel valuePanel;
	
	protected XMLTablePanel flowElementsPanel;

	public CategoryValuePanel(BPMNPanelContainer pc, CategoryValue owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		valuePanel = new XMLTextPanel(pc, owner.get("value"));
		JPanel idValuePanel = new JPanel();
		idValuePanel.setLayout(new BoxLayout(idValuePanel,BoxLayout.X_AXIS));
		idValuePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idValuePanel.add(Box.createHorizontalGlue());
		idValuePanel.add(valuePanel);
		this.add(idValuePanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(createToolbarButton(selectAction));
		buttonPanel.add(Box.createHorizontalGlue());
		this.add(buttonPanel);
		
		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("type");
		columnsToShow.add("name");
		flowElementsPanel = new XMLTablePanel(getPanelContainer(), owner, "", "categorizedFlowElements", columnsToShow, owner.getCategorizedFlowElements(),350, 300, false, false);
		this.add(flowElementsPanel);

	}

	public void saveObjects() {
		if (valuePanel.getText().trim().length() == 0) {
			return;
		}
		valuePanel.saveObjects();
		super.saveObjects();
		
		List<XMLElement> oldList = ((CategoryValue) owner).getCategorizedFlowElements(false);
		for (XMLElement el: oldList) {
			((FlowElement)el).removeCategoryValueRef(((CategoryValue) owner).getId());
		}
		List<XMLElement> newList = flowElementsPanel.getAllElements();
		for (XMLElement el: newList) {
			((FlowElement)el).addCategoryValueRef(((CategoryValue) owner).getId());
		}
		((CategoryValue) owner).setCategorizedFlowElements(newList);
	}

	public JButton createToolbarButton(Action a) {
		String actionName = (String) a.getValue(Action.NAME);
		JButton b = new JButton(Resources.get(actionName));
		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(Resources.get(actionName));
		return b;
	}
	
	protected Action selectAction = new AbstractAction("selectFlowElements") {
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed(ActionEvent ae) {
			getEditor().createBpmnElementDialog().initDialog().editBPMNElement(flowElementsPanel, "selectFlowElements", owner);
		}
	};
}
