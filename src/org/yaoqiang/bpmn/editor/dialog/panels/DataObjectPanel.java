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
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.core.common.CallableElement;
import org.yaoqiang.bpmn.model.elements.core.common.ItemDefinition;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.data.DataInput;
import org.yaoqiang.bpmn.model.elements.data.DataObject;
import org.yaoqiang.bpmn.model.elements.data.DataOutput;
import org.yaoqiang.bpmn.model.elements.data.DataState;
import org.yaoqiang.bpmn.model.elements.data.InputOutputSpecification;
import org.yaoqiang.bpmn.model.elements.data.Property;
import org.yaoqiang.bpmn.model.elements.events.CatchEvent;
import org.yaoqiang.bpmn.model.elements.events.ThrowEvent;

/**
 * DataObjectPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DataObjectPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;
	protected XMLComboPanel itemSubjectRefPanel;
	protected XMLCheckboxPanel isCollectionPanel;
	protected DataStatePanel statePanel;

	protected boolean actOwner = true;

	public DataObjectPanel(BPMNPanelContainer pc, BaseElement owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		if (owner instanceof DataObject || owner.getParent().getParent().getParent() instanceof CallableElement) {
			actOwner = false;
		}

		namePanel = new XMLTextPanel(pc, owner.get("name"));
		itemSubjectRefPanel = new XMLComboPanel(pc, owner.get("itemSubjectRef"), "itemSubjectRef", BPMNModelUtils.getAllItemDefinitions(owner), true, false, true);
		if (!(owner instanceof Property)) {
			isCollectionPanel = new XMLCheckboxPanel(pc, owner.get("isCollection"), false);
			itemSubjectRefPanel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (itemSubjectRefPanel.isEmpty()) {
						isCollectionPanel.setEnabled(true);
					} else {
						Object sel = itemSubjectRefPanel.getSelectedItem();
						isCollectionPanel.setSelected(sel instanceof ItemDefinition && ((ItemDefinition) sel).isCollection());
						isCollectionPanel.setEnabled(false);
					}
				}
			});
		}

		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));

		if (owner instanceof DataObject) {
			idNamePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
			idNamePanel.add(namePanel);
		} else {
			idNamePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
			idNamePanel.add(itemSubjectRefPanel);
			if (!(owner instanceof Property) && !actOwner) {
				idNamePanel.add(isCollectionPanel);
			}
		}
		this.add(idNamePanel);

		JPanel collectionTypePanel = new JPanel();
		collectionTypePanel.setLayout(new BoxLayout(collectionTypePanel, BoxLayout.X_AXIS));
		if (owner instanceof DataObject) {
			collectionTypePanel.add(itemSubjectRefPanel);
			collectionTypePanel.add(isCollectionPanel);
		} else {
			collectionTypePanel.add(namePanel);
			if (!(owner instanceof Property)) {
				if (!actOwner) {
					statePanel = new DataStatePanel(pc, (DataState) owner.get("dataState"));
					collectionTypePanel.add(statePanel);
				} else if (actOwner) {
					collectionTypePanel.add(isCollectionPanel);
				}
			}
		}

		this.add(collectionTypePanel);
		if (!(owner instanceof Property)) {
			if (itemSubjectRefPanel.isEmpty()) {
				isCollectionPanel.setEnabled(true);
			} else {
				Object sel = itemSubjectRefPanel.getSelectedItem();
				isCollectionPanel.setSelected(sel instanceof ItemDefinition && ((ItemDefinition) sel).isCollection());
				isCollectionPanel.setEnabled(false);
			}
		}
	}

	public void saveObjects() {
		BaseElement dataObject = (BaseElement) owner;
		String dataObjectId = dataObject.getId();
		XMLElement parent = getOwner().getParent().getParent();
		if (dataObjectId.length() == 0) {
			String id = null;
			if (dataObject instanceof DataInput) {
				id = "inputDI" + ((BaseElement) parent.getParent()).getId();
			} else if (dataObject instanceof DataOutput) {
				id = "outputDI" + ((BaseElement) parent.getParent()).getId();
			}
			dataObject.setId(id);
		}
		if (dataObject instanceof DataInput) {
			if (parent instanceof InputOutputSpecification) {
				((InputOutputSpecification) parent).addDataInputRef(dataObjectId);
			} else if (parent instanceof ThrowEvent) {
				((ThrowEvent) parent).addDataInputRef(dataObjectId);
			}
		} else if (dataObject instanceof DataOutput) {
			if (parent instanceof InputOutputSpecification) {
				((InputOutputSpecification) parent).addDataOutputRef(dataObjectId);
			} else if (parent instanceof CatchEvent) {
				((CatchEvent) parent).addDataOutputRef(dataObjectId);
			}
		}

		namePanel.saveObjects();
		if (!(dataObject instanceof DataObject) && !(owner instanceof Property) && !actOwner) {
			statePanel.saveObjects();
		}

		if (!(owner instanceof Property)) {
			isCollectionPanel.saveObjects();
		}

		itemSubjectRefPanel.saveObjects();
		super.saveObjects();
	}

}
