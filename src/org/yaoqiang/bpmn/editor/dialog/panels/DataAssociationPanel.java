package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLTextElements;
import org.yaoqiang.bpmn.model.elements.activities.Activity;
import org.yaoqiang.bpmn.model.elements.activities.LoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.MultiInstanceLoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElementsContainer;
import org.yaoqiang.bpmn.model.elements.data.DataAssociation;
import org.yaoqiang.bpmn.model.elements.data.DataInputAssociation;
import org.yaoqiang.bpmn.model.elements.data.DataObject;
import org.yaoqiang.bpmn.model.elements.data.DataObjectReference;
import org.yaoqiang.bpmn.model.elements.data.DataOutputAssociation;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;

/**
 * DataAssociationPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DataAssociationPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel targetRefPanel;
	protected XMLComboPanel sourceRefPanel;
	protected XMLMultiLineTextPanel mtp;

	protected boolean readonly = false;

	public DataAssociationPanel(BPMNPanelContainer pc, DataAssociation owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		if (getGraphComponent().getGraph().getModel().getCells().containsKey(owner.getId())) {
			readonly = true;
		}

		List<XMLElement> targetRefs = new ArrayList<XMLElement>();
		List<XMLElement> sourceRefs = new ArrayList<XMLElement>();
		List<XMLElement> data = new ArrayList<XMLElement>();
		LoopCharacteristics loopType = null;
		XMLElement parent = owner.getParent().getParent();
		if (parent instanceof Activity) {
			loopType = ((Activity) parent).getLoopCharacteristics();
			data.addAll(((Activity) parent).getAccessibleProperties());
		} else if (parent instanceof Event) {
			data.addAll(((Event) parent).getAccessibleProperties());
		}
		for (XMLElement el : ((FlowElements) parent.getParent()).getAccessibleDataObjects()) {
			data.add(el);
			for (XMLElement doRef : BPMNModelUtils.getAllDataObjectRefs(el)) {
				if (((DataObject) el).getId().equals(((DataObjectReference) doRef).getDataObjectRef())) {
					data.add(doRef);
				}
			}
		}
		data.addAll(BPMNModelUtils.getDefinitions(owner).getDataStores());
		data.addAll(BPMNModelUtils.getAllDataStoreRefs(owner));
		FlowElementsContainer container = BPMNModelUtils.getParentFlowElementsContainer(owner);
		BPMNProcess process = BPMNModelUtils.getParentProcess(owner);
		if (owner instanceof DataInputAssociation) {
			targetRefs = ((DataInputAssociation) owner).getAvailableDataInputs();
			data.addAll(process.getDataInputs());
			if (loopType instanceof MultiInstanceLoopCharacteristics) {
				data.add(((MultiInstanceLoopCharacteristics) loopType).getInputDataItem());
			}
			if (container instanceof SubProcess) {
				LoopCharacteristics subLoopType = ((SubProcess) container).getLoopCharacteristics();
				if (subLoopType instanceof MultiInstanceLoopCharacteristics) {
					data.add(((MultiInstanceLoopCharacteristics) subLoopType).getInputDataItem());
				}
			}
			sourceRefs = data;
		} else {
			sourceRefs = ((DataOutputAssociation) owner).getAvailableDataOutputs();
			data.addAll(process.getDataOutputs());
			if (loopType instanceof MultiInstanceLoopCharacteristics) {
				data.add(((MultiInstanceLoopCharacteristics) loopType).getOutputDataItem());
			}
			if (container instanceof SubProcess) {
				LoopCharacteristics subLoopType = ((SubProcess) container).getLoopCharacteristics();
				if (subLoopType instanceof MultiInstanceLoopCharacteristics) {
					data.add(((MultiInstanceLoopCharacteristics) subLoopType).getOutputDataItem());
				}
			}
			targetRefs = data;
		}

		targetRefPanel = new XMLComboPanel(pc, owner.get("targetRef"), "targetRef", targetRefs, false, false, !readonly);

		XMLElement sourceRefElement = null;
		XMLTextElements sourceRefsElement = owner.getSourceRefs();
		if (sourceRefsElement.isEmpty()) {
			sourceRefElement = sourceRefsElement.generateNewElement();
			sourceRefsElement.add(sourceRefElement);
		} else {
			sourceRefElement = sourceRefsElement.getXMLElements().get(0);
		}
		sourceRefPanel = new XMLComboPanel(pc, sourceRefElement, "sourceRef", sourceRefs, true, false, !readonly);

		JPanel srctgtPanel = new JPanel();
		srctgtPanel.setLayout(new BoxLayout(srctgtPanel, BoxLayout.X_AXIS));
		srctgtPanel.add(new XMLTextPanel(pc, owner.get("id"), 80, 26, false));
		srctgtPanel.add(targetRefPanel);
		srctgtPanel.add(sourceRefPanel);

		this.add(srctgtPanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("from");
		columnsToShow.add("to");
		this.add(new XMLTablePanel(getPanelContainer(), owner.getAssignments(), "assignments", "assignments", columnsToShow, owner.getAssignmentList(), 500,
				100, true, false));

		mtp = new XMLMultiLineTextPanel(pc, owner.get("transformation"), "transformation", 380, 100);
		this.add(mtp);
	}

	public XMLComboPanel getTargetRefPanel() {
		return targetRefPanel;
	}

	public void saveObjects() {
		if (!readonly) {
			targetRefPanel.saveObjects();
			sourceRefPanel.saveObjects();
		}
		mtp.saveObjects();
		super.saveObjects();
	}

}
