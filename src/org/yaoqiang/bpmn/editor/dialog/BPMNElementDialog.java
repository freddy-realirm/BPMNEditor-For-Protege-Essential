package org.yaoqiang.bpmn.editor.dialog;

import java.util.Map;

import javax.swing.JOptionPane;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.ResourceParameterBinding;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRole;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRoles;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerEntity;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerRole;
import org.yaoqiang.bpmn.model.elements.data.Assignment;
import org.yaoqiang.bpmn.model.elements.data.DataAssociation;
import org.yaoqiang.bpmn.model.elements.data.ItemAwareElement;
import org.yaoqiang.bpmn.model.elements.events.EventDefinition;
import org.yaoqiang.dialog.BaseDialog;
import org.yaoqiang.dialog.Panel;
import org.yaoqiang.util.Resources;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxUndoableEdit;

/**
 * BPMNElementDialog
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNElementDialog extends BaseDialog {

	private static final long serialVersionUID = 1044213777653754956L;

	protected BaseEditor editor;

	public BPMNElementDialog(BPMNEditor editor) {
		this.editor = editor;
	}

	public BPMNElementDialog initDialog() {
		return (BPMNElementDialog) super.initDialog();
	}

	public BPMNElementDialog initDialog(boolean save) {
		return (BPMNElementDialog) super.initDialog(save);
	}

	public BPMNElementDialog initDialog(boolean save, String okButton) {
		return (BPMNElementDialog) super.initDialog(save, okButton);
	}

	public BPMNPanelContainer getPanelContainer() {
		return (BPMNPanelContainer) panelContainer;
	}

	public XMLPanel getParentPanel() {
		return (XMLPanel) parentPanel;
	}

	public BPMNEditor getEditor() {
		return (BPMNEditor) editor;
	}

	public void editBPMNElement(Object el) {
		editObject(null, el, "");
	}

	public void editBPMNElement(XMLPanel parentPanel, Object el) {
		editObject(parentPanel, el, "");
	}

	public void editBPMNElement(XMLPanel parentPanel, String type, Object el) {
		editObject(parentPanel, el, type);
	}

	public void editBPMNElement(Object el, String type) {
		editObject(null, el, type);
	}

	public void editObject(Panel parentPanel, Object el, String type) {
		this.parentPanel = parentPanel;
		panelContainer.setActiveObject(el, type);
		String title = "";
		if (el instanceof Map.Entry<?, ?>) {
			title = Resources.get("namespace");
		} else if (el instanceof ResourceRoles) {
			title = Resources.get("resourceAssignment");
		} else if (el instanceof ResourceRole) {
			title = Resources.get("resource");
		} else if (el instanceof ResourceParameterBinding) {
			title = Resources.get("resourceParameterBinding");
		} else if (el instanceof CallActivity) {
			title = Resources.get("callActivityOrProcess");
		} else {
			if (el instanceof EventDefinition || el instanceof DataAssociation || el instanceof Assignment || el instanceof PartnerEntity
					|| el instanceof PartnerRole) {
				title = Resources.get(((XMLElement) el).toName());
			} else {
				if ((type == null || type.length() == 0 || type.startsWith("addon")) && el instanceof XMLElement || el instanceof ItemAwareElement) {
					title = Resources.get(((XMLElement) el).toName());
				} else {
					title = Resources.get(type);
				}
			}
		}
		setTitle(title);

		setDialogVisible();
	}

	protected void init() {
		panelContainer = new BPMNPanelContainer(this);
	}

	public void save() {
		panelContainer.apply();
		if (save) {
			editor.getGraphComponent().getGraph().getModel().fireEvent(new mxEventObject(mxEvent.CHANGE, "edit", new mxUndoableEdit(null)));
		}
		dispose();
	}

	public void close() {
		if (panelContainer.isModified() && save) {
			int sw = panelContainer.showModifiedWarning();
			if (sw == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (sw == JOptionPane.YES_OPTION) {
				editor.getGraphComponent().getGraph().getModel().fireEvent(new mxEventObject(mxEvent.CHANGE, "edit", new mxUndoableEdit(null)));
			}
		}
		setVisible(false);
		dispose();
	}

}
