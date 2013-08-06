package org.yaoqiang.bpmn.editor.dialog.panels;

import javax.swing.BoxLayout;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.data.Assignment;
import org.yaoqiang.bpmn.model.elements.data.DataAssociation;
import org.yaoqiang.bpmn.model.elements.data.DataInput;
import org.yaoqiang.bpmn.model.elements.data.DataInputAssociation;

/**
 * AssignmentPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class AssignmentPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLMultiLineTextPanel fromPanel;
	protected XMLMultiLineTextPanel toPanel;

	public AssignmentPanel(BPMNPanelContainer pc, Assignment owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		fromPanel = new XMLMultiLineTextPanel(pc, owner.get("from"), "from", 380, 80);
		toPanel = new XMLMultiLineTextPanel(pc, owner.get("to"), "to", 380, 80);

		if (toPanel.getText().length() == 0) {
			DataAssociation dataAssociation = owner.getParent().getParent();
			if (dataAssociation instanceof DataInputAssociation) {
				String targetRef = dataAssociation.getTargetRef();
				if (targetRef.length() == 0) {
					DataAssociationPanel daPanel = (DataAssociationPanel) getParentPanel().getParent();
					targetRef = ((DataInput) daPanel.getTargetRefPanel().getSelectedItem()).getId();
				}
				toPanel.setText(targetRef);
			}
		}
		this.add(new XMLTextPanel(pc, owner.get("id"), false));
		this.add(fromPanel);
		this.add(toPanel);
	}

	public void saveObjects() {
		fromPanel.saveObjects();
		toPanel.saveObjects();
		super.saveObjects();
	}

}
