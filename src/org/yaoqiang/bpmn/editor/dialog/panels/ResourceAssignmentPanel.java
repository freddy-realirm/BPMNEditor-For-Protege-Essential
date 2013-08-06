package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.elements.activities.ResourceAssignmentExpression;
import org.yaoqiang.util.Resources;


/**
 * ResourceAssignmentPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ResourceAssignmentPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected ExpressionPanel expressionPanel;

	public ResourceAssignmentPanel(BPMNPanelContainer pc, ResourceAssignmentExpression owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(BorderFactory.createTitledBorder(Resources.get("resourceAssignmentExpression")));
		expressionPanel = new ExpressionPanel(getPanelContainer(), owner.getExpressionElement(), null, 260, 100);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		JButton editButton = new JButton(new ImageIcon(ResourceAssignmentPanel.class.getResource("/org/yaoqiang/bpmn/editor/images/add_performer.png")));
		editButton.setMargin(new Insets(2, 2, 2, 2));
		editButton.addActionListener(new AbstractAction("insert") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				getEditor().createBpmnElementDialog().initDialog().editBPMNElement(ResourceAssignmentPanel.this, "selectPerformer", getEditor());
			}
			
		});
		buttonPanel.add(editButton);
		buttonPanel.add(Box.createVerticalGlue());
		this.add(expressionPanel);
		this.add(buttonPanel);
	}

	public void insertPerformer(String performer) {
		expressionPanel.getMtp().appendText(performer);
	}

	public void saveObjects() {
		expressionPanel.saveObjects();
	}

}
