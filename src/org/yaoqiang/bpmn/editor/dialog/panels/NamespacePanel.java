package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.model.BPMNModelConstants;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.util.Resources;


/**
 * NamespacePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class NamespacePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected String previouskey;
	
	protected XMLTextPanel namePanel;
	
	protected XMLTextPanel locationPanel;

	public NamespacePanel(BPMNPanelContainer pc, Definitions owner, Map.Entry<?, ?> el) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		previouskey = el.getKey().toString();
		namePanel = new XMLTextPanel(pc, owner, "name", el.getValue().toString(), 300, 26, !BPMNModelConstants.READONLY_ELEMENT.contains(el.getKey()) && !el.getKey().toString().startsWith(BPMNModelConstants.BPMN_TARGET_MODEL_NS));
		locationPanel = new XMLTextPanel(pc, owner, "location", el.getKey().toString(), 300, 26, !BPMNModelConstants.READONLY_ELEMENT.contains(el.getKey()) && !el.getKey().toString().startsWith(BPMNModelConstants.BPMN_TARGET_MODEL_NS));

		this.add(namePanel);
		this.add(locationPanel);
		this.add(Box.createVerticalGlue());
	}
	
	public void saveObjects() {
		if (locationPanel.getText().length() != 0 && namePanel.getText().length() != 0) {
			if (((Definitions) owner).getNamespaces().containsValue(namePanel.getText())) {
				JOptionPane.showMessageDialog(null, "An Item with the name '" + namePanel.getText() + "' already exists. Specify a different name.", Resources.get("Warning"), JOptionPane.WARNING_MESSAGE);
				return;
			}
			((Definitions) owner).getNamespaces().remove(previouskey);
			((Definitions) owner).getNamespaces().put(locationPanel.getText(), namePanel.getText());
			
			XMLPanel targetPanel = getParentPanel();
			((XMLTablePanel)targetPanel).fillTableContent(((Definitions) owner).getNamespaces());
		}
	}

}
