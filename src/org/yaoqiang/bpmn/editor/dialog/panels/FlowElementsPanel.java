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
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.artifacts.CategoryValue;
import org.yaoqiang.util.Resources;


/**
 * FlowElementsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class FlowElementsPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLTablePanel flowElementsPanel;

	public FlowElementsPanel(BPMNPanelContainer pc, CategoryValue owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(Box.createVerticalStrut(5));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(createToolbarButton(selectAction));
		buttonPanel.add(createToolbarButton(deselectAction));
		buttonPanel.add(Box.createHorizontalGlue());
		this.add(buttonPanel);
		
		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("");
		columnsToShow.add("id");
		columnsToShow.add("type");
		columnsToShow.add("name");
		flowElementsPanel = new XMLTablePanel(getPanelContainer(), owner, "", null, columnsToShow, BPMNModelUtils.getAllFlowElements(owner), 350, 300, false, true);
		this.add(flowElementsPanel);

	}

	public void saveObjects() {
		XMLPanel targetPanel = getParentPanel();
		((XMLTablePanel)targetPanel).fillTableContent(flowElementsPanel.getCheckedElements());
	}

	public JButton createToolbarButton(Action a) {
		String actionName = (String) a.getValue(Action.NAME);

		JButton b = new JButton(Resources.get(actionName));

		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(Resources.get(actionName));
		
		return b;
	}
	
	protected Action selectAction = new AbstractAction("selectAll") {
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed(ActionEvent ae) {
			flowElementsPanel.selectAllElements(true);
		}
	};
	
	protected Action deselectAction = new AbstractAction("deselectAll") {
		private static final long serialVersionUID = 1L;
		
		public void actionPerformed(ActionEvent ae) {
			flowElementsPanel.selectAllElements(false);
		}
	};
}
