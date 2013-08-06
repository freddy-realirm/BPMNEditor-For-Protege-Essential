package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.core.foundation.RootElements;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.util.Resources;

/**
 * CallActivityPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class CallActivityPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLTextPanel idPanel;

	protected XMLComboPanel calledElementPanel;

	protected String calledElement;

	public CallActivityPanel(BPMNPanelContainer pc, CallActivity owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		idPanel = new XMLTextPanel(pc, owner.get("id"), false);
		this.add(idPanel);

		Definitions defs = BPMNModelUtils.getDefinitions(owner);
		calledElement = owner.getCalledElement();
		Set<String> choices = new HashSet<String>();
		Object cell = BPMNEditor.getGraph().getModel().getCell(owner.getId());
		if (BPMNEditor.getGraph().getModel().isCallProcess(cell)) {
			choices = defs.getProcessIds();
			if (!choices.contains(calledElement)) {
				choices.add(calledElement);
			}
			for (Entry<String, Definitions> entry : BPMNEditor.getGraph().getExternalDefinitions().entrySet()) {
				String ns = BPMNEditor.getGraph().getBpmnModel().getNamespaces().get(entry.getKey());
				for (String pid : entry.getValue().getProcessIds()) {
					choices.add(ns + ":" + pid);
				}
			}
			BPMNProcess process = BPMNModelUtils.getDefaultProcess(defs);
			if (process != null) {
				choices.remove(process.getId());
			}
		} else {
			choices = defs.getGlobalTaskIds(getGlobalTaskType(cell));
			if (!choices.contains(calledElement)) {
				choices.add(calledElement);
			}
			for (Entry<String, Definitions> entry : BPMNEditor.getGraph().getExternalDefinitions().entrySet()) {
				String ns = BPMNEditor.getGraph().getBpmnModel().getNamespaces().get(entry.getKey());
				for (String gid : entry.getValue().getGlobalTaskIds(getGlobalTaskType(cell))) {
					choices.add(ns + ":" + gid);
				}
			}
		}

		calledElementPanel = new XMLComboPanel(pc, owner.get("calledElement"), "calledElement", Arrays.asList(choices.toArray()), false, true, true);
		this.add(calledElementPanel);
	}

	public void saveObjects() {
		String newCalledElement = calledElementPanel.getSelectedItem().toString();
		if (newCalledElement.length() > 0) {
			if (newCalledElement.equals(idPanel.getText())) {
				JOptionPane
						.showMessageDialog(null, Resources.get("WarningCallActivityCannotCallItself"), Resources.get("Warning"), JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (!newCalledElement.equals(calledElement)) {
				calledElementPanel.saveObjects();
			}
		}
	}

	public String getGlobalTaskType(Object cell) {
		String type = RootElements.TYPE_GLOBAL_TASK;
		GraphModel model = BPMNEditor.getGraph().getModel();
		if (model.isUserTask(cell)) {
			type = RootElements.TYPE_GLOBAL_USER_TASK;
		} else if (model.isScriptTask(cell)) {
			type = RootElements.TYPE_GLOBAL_SCRIPT_TASK;
		} else if (model.isManualTask(cell)) {
			type = RootElements.TYPE_GLOBAL_MANUAL_TASK;
		} else if (model.isBusinessRuleTask(cell)) {
			type = RootElements.TYPE_GLOBAL_BUSINESS_RULE_TASK;
		}
		return type;
	}

}
