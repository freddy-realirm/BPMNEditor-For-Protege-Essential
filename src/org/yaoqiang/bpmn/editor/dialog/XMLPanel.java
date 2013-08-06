package org.yaoqiang.bpmn.editor.dialog;

import java.awt.Container;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.model.elements.XMLCollection;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;
import org.yaoqiang.dialog.Panel;
import org.yaoqiang.dialog.PanelContainer;

/**
 * XMLPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public XMLPanel(PanelContainer pc, XMLElement owner) {
		super(pc, owner);
	}

	public void saveObjects() {
		XMLPanel parentPanel = getParentPanel();
		if (parentPanel != null && !(owner instanceof XMLCollection)) {
			if (getOwner().getParent() instanceof XMLCollection) {
				((XMLCollection) getOwner().getParent()).add(getOwner());
			} else if (getOwner().getParent() instanceof XMLExtensionElement) {
				XMLExtensionElement parent = (XMLExtensionElement) getOwner().getParent();
				if (!parent.contains((XMLExtensionElement) owner)) {
					parent.addChildElement((XMLExtensionElement) owner);
				}
			}
			((XMLTablePanel) parentPanel).addRow(getOwner());
		}
	}

	public BPMNPanelContainer getPanelContainer() {
		return (BPMNPanelContainer) panelContainer;
	}

	public XMLElement getOwner() {
		return (XMLElement) owner;
	}

	public BPMNElementDialog getDialog() {
		for (Container p = getPanelContainer().getParent(); p != null; p = p.getParent()) {
			if (p instanceof BPMNElementDialog) {
				return (BPMNElementDialog) p;
			}
		}
		return null;
	}

	public BPMNEditor getEditor() {
		if (getDialog() != null) {
			return getDialog().getEditor();
		}
		return null;
	}

	public BPMNGraphComponent getGraphComponent() {
		if (getEditor() != null) {
			return getEditor().getGraphComponent();
		}
		return null;
	}

	public XMLPanel getParentPanel() {
		if (getDialog() != null) {
			return getDialog().getParentPanel();
		}
		return null;
	}

}
