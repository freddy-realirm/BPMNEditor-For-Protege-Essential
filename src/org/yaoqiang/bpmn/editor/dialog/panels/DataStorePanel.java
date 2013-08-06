package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.data.DataStore;

/**
 * DataStorePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DataStorePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel itemSubjectRefPanel;
	protected XMLPanel namePanel;
	protected XMLCheckboxPanel isUnlimitedPanel;
	protected XMLPanel capacityPanel;

	public DataStorePanel(BPMNPanelContainer pc, DataStore owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		itemSubjectRefPanel = new XMLComboPanel(pc, owner.get("itemSubjectRef"), null, BPMNModelUtils.getAllItemDefinitions(owner), true, false, true);
		namePanel = new XMLTextPanel(pc, owner.get("name"), true);
		isUnlimitedPanel = new XMLCheckboxPanel(pc, owner.get("isUnlimited"), false);
		isUnlimitedPanel.getCheckBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (isUnlimitedPanel.isSelected()) {
					capacityPanel.setEnabled(false);
				} else {
					capacityPanel.setEnabled(true);
				}
			}
		});
		JPanel idItemPanel = new JPanel();
		idItemPanel.setLayout(new BoxLayout(idItemPanel, BoxLayout.X_AXIS));
		idItemPanel.add(new XMLTextPanel(pc, owner.get("id"), 120, 26, false));
		idItemPanel.add(itemSubjectRefPanel);
		this.add(idItemPanel);

		capacityPanel = new XMLTextPanel(pc, owner.get("capacity"), 60, 26, true);
		JPanel nameCapacityPanel = new JPanel();
		nameCapacityPanel.setLayout(new BoxLayout(nameCapacityPanel, BoxLayout.X_AXIS));
		nameCapacityPanel.add(namePanel);
		nameCapacityPanel.add(isUnlimitedPanel);
		nameCapacityPanel.add(capacityPanel);
		this.add(nameCapacityPanel);
		if (isUnlimitedPanel.isSelected()) {
			capacityPanel.setEnabled(false);
		}
	}

	public void saveObjects() {
		namePanel.saveObjects();
		isUnlimitedPanel.saveObjects();
		if (!isUnlimitedPanel.isSelected()) {
			capacityPanel.saveObjects();
		}
		itemSubjectRefPanel.saveObjects();
		super.saveObjects();
	}

}
