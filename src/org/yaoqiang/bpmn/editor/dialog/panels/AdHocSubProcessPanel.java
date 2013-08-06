package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.XMLCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.activities.AdHocSubProcess;

/**
 * AdHocSubProcessPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class AdHocSubProcessPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;
	
	protected XMLComboPanel orderingPanel;

	protected XMLCheckboxPanel cancelRemainingPanel;

	protected XMLPanel expr;

	public AdHocSubProcessPanel(BPMNPanelContainer pc, AdHocSubProcess owner) {
		super(pc, owner);
		this.setLayout(new BorderLayout());

		List<String> choices = new ArrayList<String>();
		choices.add("Parallel");
		choices.add("Sequential");
		orderingPanel = new XMLComboPanel(pc, owner.getOrderingAttribute(), "ordering", choices, false, false, true);
		orderingPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object sel = orderingPanel.getSelectedItem();
				if (sel.equals("Sequential")) {
					cancelRemainingPanel.setSelected(false);
					cancelRemainingPanel.setEnabled(false);
				} else {
					cancelRemainingPanel.setEnabled(true);
					cancelRemainingPanel.setSelected(true);
				}
			}
		});
		cancelRemainingPanel = new XMLCheckboxPanel(pc, owner.get("cancelRemainingInstances"), true);
		if (orderingPanel.getSelectedItem().equals("Sequential")) {
			cancelRemainingPanel.setSelected(false);
			cancelRemainingPanel.setEnabled(false);
		}
		JPanel orderCancelPanel = new JPanel();
		orderCancelPanel.setLayout(new BoxLayout(orderCancelPanel, BoxLayout.X_AXIS));
		orderCancelPanel.add(orderingPanel);
		orderCancelPanel.add(Box.createHorizontalGlue());
		orderCancelPanel.add(cancelRemainingPanel);
		expr = new XMLMultiLineTextPanel(pc, owner.getCompletionCondition(), "completionCondition", 350, 90);
		this.add(orderCancelPanel, BorderLayout.NORTH);
		this.add(expr, BorderLayout.CENTER);
		this.add(Box.createVerticalGlue(), BorderLayout.SOUTH);
	}
	
	public void saveObjects() {
		orderingPanel.saveObjects();
		cancelRemainingPanel.saveObjects();
		expr.saveObjects();
	}
	
	
}
