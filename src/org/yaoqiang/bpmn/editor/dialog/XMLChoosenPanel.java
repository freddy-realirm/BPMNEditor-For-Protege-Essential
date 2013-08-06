package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import org.yaoqiang.bpmn.model.elements.XMLComplexElement;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLEmptyElement;
import org.yaoqiang.bpmn.model.elements.activities.ResourceAssignmentExpression;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRole;
import org.yaoqiang.bpmn.model.elements.core.common.Resource;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.core.foundation.Documentation;
import org.yaoqiang.dialog.Panel;
import org.yaoqiang.dialog.PanelContainer;

/**
 * XMLChoosenPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLChoosenPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected Panel choosenPanel;

	public XMLChoosenPanel(PanelContainer pc, XMLElement owner, XMLElement choosen, int width, int height) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		setChoosen(choosen);
		setPreferredSize(new Dimension(width, height));
	}

	public void setChoosen(XMLElement choosen) {
		if (choosenPanel != null) {
			remove(0);
		}
		if (choosen != null) {
			String type = "";
			if (owner instanceof ResourceRole) {
				if (choosen instanceof XMLEmptyElement || choosen instanceof ResourceAssignmentExpression) {
					choosen = ((ResourceRole) owner).getResourceAssignTypes().getResourceAssignmentExpression();
				} else {
					if (choosen instanceof Resource) {
						type = ((BaseElement) choosen).getId();
					} else if (choosen.toName().equals("parameterAssignment")) {
						type = ((XMLComplexElement)choosen).get("resourceRef").toValue();
					}
					if (type.length() == 0) {
						choosen = ((ResourceRole) owner).getResourceAssignTypes().getResourceAssignmentExpression();
					} else {
						choosen = ((ResourceRole) owner).getResourceAssignTypes().getResourceParameterBindings();
					}
				}
			} else if (owner instanceof Documentation) {
				String format = ((Documentation) owner).getTextFormat();
				if (format.equals("text/plain")) {
					type = "text";
				} else {
					type = "file";
				}
			}
			choosenPanel = getPanelContainer().getPanelFactory().getPanel(choosen, type);
		} else {
			if (owner instanceof Documentation) {
				String format = ((Documentation) owner).getTextFormat();
				if (format.length() == 0 || format.equals("text/plain")) {
					choosenPanel = new XMLMultiLineTextPanel(getPanelContainer(), getOwner(), "text", "text/plain", 350, 100);
				} else {
					choosenPanel = new XMLLocationPanel(getPanelContainer(), getOwner(), "file");
				}
			} else {
				choosenPanel = new XMLPanel(getPanelContainer(), getOwner());
			}
		}
		add(choosenPanel);
		validate();
	}

	public final Panel getChoosenPanel() {
		return choosenPanel;
	}

	public void saveObjects() {
		choosenPanel.saveObjects();
	}

}
