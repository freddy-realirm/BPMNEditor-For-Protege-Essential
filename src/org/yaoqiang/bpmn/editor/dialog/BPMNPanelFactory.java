package org.yaoqiang.bpmn.editor.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaoqiang.bpmn.editor.dialog.panels.AdHocSubProcessPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.AssignmentPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.CallActivityPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.CategoryPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.CategoryValuePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DataAssociationPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DataInOutPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DataInputsPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DataObjectPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DataOutputsPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DataStatePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DataStorePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DefinitionsPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.DocumentationPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ErrorPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ErrorRefPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.EscalationPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.EventDefinitionPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ExpressionPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.FlowElementsPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.GlobalTaskPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ImportPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.InterfacePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.InterfaceRefPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ItemDefinitionPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.LoopCharacteristicsPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.MessagePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.NamespacePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.OperationPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ParticipantPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ParticipantRefPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.PartnerPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ProcessPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.RecurringTimeIntervalPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ResourceAssignmentPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ResourcePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ResourceParameterBindingPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ResourceParameterPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.ResourceRolePanel;
import org.yaoqiang.bpmn.editor.dialog.panels.SignalPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.TaskPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.TimeIntervalPanel;
import org.yaoqiang.bpmn.editor.dialog.panels.TransactionPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLAttribute;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLTextElement;
import org.yaoqiang.bpmn.model.elements.activities.Activity;
import org.yaoqiang.bpmn.model.elements.activities.AdHocSubProcess;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.LoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.ResourceAssignmentExpression;
import org.yaoqiang.bpmn.model.elements.activities.ResourceParameterBinding;
import org.yaoqiang.bpmn.model.elements.activities.ResourceParameterBindings;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRole;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRoles;
import org.yaoqiang.bpmn.model.elements.activities.Task;
import org.yaoqiang.bpmn.model.elements.activities.Transaction;
import org.yaoqiang.bpmn.model.elements.artifacts.Category;
import org.yaoqiang.bpmn.model.elements.artifacts.CategoryValue;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;
import org.yaoqiang.bpmn.model.elements.core.common.BPMNError;
import org.yaoqiang.bpmn.model.elements.core.common.Expression;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.common.ItemDefinition;
import org.yaoqiang.bpmn.model.elements.core.common.Message;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerEntity;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerRole;
import org.yaoqiang.bpmn.model.elements.core.common.Resource;
import org.yaoqiang.bpmn.model.elements.core.common.ResourceParameter;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.core.foundation.Documentation;
import org.yaoqiang.bpmn.model.elements.core.foundation.Documentations;
import org.yaoqiang.bpmn.model.elements.core.foundation.RootElements;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Import;
import org.yaoqiang.bpmn.model.elements.core.service.Interface;
import org.yaoqiang.bpmn.model.elements.core.service.Operation;
import org.yaoqiang.bpmn.model.elements.data.Assignment;
import org.yaoqiang.bpmn.model.elements.data.DataAssociation;
import org.yaoqiang.bpmn.model.elements.data.DataState;
import org.yaoqiang.bpmn.model.elements.data.DataStore;
import org.yaoqiang.bpmn.model.elements.data.ItemAwareElement;
import org.yaoqiang.bpmn.model.elements.data.Properties;
import org.yaoqiang.bpmn.model.elements.events.CatchEvent;
import org.yaoqiang.bpmn.model.elements.events.Escalation;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.events.EventDefinition;
import org.yaoqiang.bpmn.model.elements.events.EventDefinitions;
import org.yaoqiang.bpmn.model.elements.events.Signal;
import org.yaoqiang.bpmn.model.elements.events.ThrowEvent;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.bpmn.model.elements.process.GlobalTask;
import org.yaoqiang.dialog.Panel;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.dialog.PanelFactory;

/**
 * BPMNPanelFactory
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNPanelFactory extends PanelFactory {

	public BPMNPanelFactory(PanelContainer pc) {
		super(pc);
	}

	public BPMNPanelContainer getPanelContainer() {
		return (BPMNPanelContainer) panelContainer;
	}

	public Panel getPanel(Object el, String type) {
		if (el instanceof Expression) {
			if (((Expression) el).toName().equals("timeDuration")) {
				return new TimeIntervalPanel(getPanelContainer(), (XMLElement) el);
			} else if (((Expression) el).toName().equals("timeCycle")) {
				return new RecurringTimeIntervalPanel(getPanelContainer(), (XMLElement) el);
			} else if (((Expression) el).toName().equals("timeDate")) {
				return new RecurringTimeIntervalPanel(getPanelContainer(), (XMLElement) el);
			}
			return new ExpressionPanel(getPanelContainer(), (XMLElement) el, null, 300, 90);
		} else if (el instanceof DataState) {
			return new DataStatePanel(getPanelContainer(), (DataState) el);
		} else if (el instanceof XMLTextElement) {
			if (((XMLTextElement) el).toName().equals("errorRef")) {
				return new ErrorRefPanel(getPanelContainer(), (XMLTextElement) el);
			} else if (((XMLTextElement) el).toName().equals("interfaceRef")) {
				return new InterfaceRefPanel(getPanelContainer(), (XMLTextElement) el);
			} else if (((XMLTextElement) el).toName().equals("participantRef")) {
				return new ParticipantRefPanel(getPanelContainer(), (XMLTextElement) el);
			}
		} else if (el instanceof Participant) {
			return new ParticipantPanel(getPanelContainer(), (Participant) el);
		} else if (el instanceof ResourceAssignmentExpression) {
			return new ResourceAssignmentPanel(getPanelContainer(), (ResourceAssignmentExpression) el);
		} else if (el instanceof Activity) {
			if (el instanceof Task) {
				if ("dataInputOutputs".equals(type)) {
					return new DataInOutPanel(getPanelContainer(), (Activity) el);
				} else {
					return new TaskPanel(getPanelContainer(), (Task) el);
				}
			} else if (el instanceof CallActivity) {
				if ("dataInputOutputs".equals(type)) {
					return new DataInOutPanel(getPanelContainer(), (Activity) el);
				} else {
					return new CallActivityPanel(getPanelContainer(), (CallActivity) el);
				}
			} else if (el instanceof AdHocSubProcess) {
				return new AdHocSubProcessPanel(getPanelContainer(), (AdHocSubProcess) el);
			} else if (el instanceof Transaction) {
				return new TransactionPanel(getPanelContainer(), (Transaction) el);
			}
		} else if (el instanceof Event) {
			if (el instanceof CatchEvent) {
				if ("dataoutputs".equals(type)) {
					return new DataOutputsPanel(getPanelContainer(), (CatchEvent) el);
				}
			} else if (el instanceof ThrowEvent) {
				if ("datainputs".equals(type)) {
					return new DataInputsPanel(getPanelContainer(), (ThrowEvent) el);
				}
			}
		} else if (el instanceof Definitions) {
			if ("categories".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_CATEGORY, null, columnsToShow,
						((Definitions) el).getCategories(), 350, 150, true, false);
			} else if ("dataStores".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("isUnlimited");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_DATASTORE, null, columnsToShow,
						((Definitions) el).getDataStores(), 350, 150, true, false);
			} else if ("resources".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_RESOURCE, null, columnsToShow,
						((Definitions) el).getResources(), 350, 150, true, false);
			} else if ("eventDefinitions".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("type");
				columnsToShow.add("references");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_EVENT_DEFINITION, null, columnsToShow,
						((Definitions) el).getEventDefinitions(""), 350, 150, true, false);
			} else if ("imports".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("importType");
				columnsToShow.add("location");
				columnsToShow.add("namespace");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getImports(), null, null, columnsToShow, ((Definitions) el).getImportList(),
						600, 120, true, false);
			} else if ("globalTasks".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("type");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_GLOBAL_TASK, null, columnsToShow,
						((Definitions) el).getGlobalTasks(), 350, 150, true, false);
			} else if ("itemDefinitions".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("structureRef");
				columnsToShow.add("itemKind");
				columnsToShow.add("isCollection");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_ITEM_DEFINITION, null, columnsToShow,
						((Definitions) el).getItemDefinitions(), 500, 150, true, false);
			} else if ("messages".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("itemRef");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_MESSAGE, null, columnsToShow,
						((Definitions) el).getMessages(), 500, 150, true, false);
			} else if ("errors".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("errorCode");
				columnsToShow.add("structureRef");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_ERROR, null, columnsToShow,
						((Definitions) el).getErrors(), 500, 150, true, false);
			} else if ("signals".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("structureRef");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_SIGNAL, null, columnsToShow,
						((Definitions) el).getSignals(), 500, 150, true, false);
			} else if ("escalations".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("escalationCode");
				columnsToShow.add("structureRef");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_ESCALATION, null, columnsToShow,
						((Definitions) el).getEscalations(), 500, 150, true, false);
			} else if ("interfaces".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_INTERFACE, null, columnsToShow,
						((Definitions) el).getInterfaces(), 350, 150, true, false);
			} else if ("partners".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("type");
				return new XMLTablePanel(getPanelContainer(), ((Definitions) el).getRootElements(), RootElements.TYPE_PARTNER, null, columnsToShow,
						((Definitions) el).getPartners(), 350, 150, true, false);
			} else if ("namespaces".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("name");
				columnsToShow.add("location");
				return new XMLTablePanel(getPanelContainer(), (Definitions) el, "namespace", null, columnsToShow, ((Definitions) el).getNamespaces(), 450, 150,
						true, false);
			} else {
				return new DefinitionsPanel(getPanelContainer(), (Definitions) el);
			}
		} else if (el instanceof FlowElements) {
			if ("dataobjects".equals(type)) {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("id");
				columnsToShow.add("name");
				columnsToShow.add("itemSubjectRef");
				columnsToShow.add("isCollection");
				return new XMLTablePanel(getPanelContainer(), (XMLElement) el, FlowElements.TYPE_DATAOBJECT, null, columnsToShow,
						((FlowElements) el).getDataObjects(), 500, 150, true, false);
			}
		} else if (el instanceof BPMNProcess) {
			return new ProcessPanel(getPanelContainer(), (BPMNProcess) el);
		} else if (el instanceof GlobalTask) {
			return new GlobalTaskPanel(getPanelContainer(), (GlobalTask) el);
		} else if (el instanceof ItemDefinition) {
			return new ItemDefinitionPanel(getPanelContainer(), (ItemDefinition) el);
		} else if (el instanceof Message) {
			return new MessagePanel(getPanelContainer(), (Message) el);
		} else if (el instanceof BPMNError) {
			return new ErrorPanel(getPanelContainer(), (BPMNError) el);
		} else if (el instanceof Signal) {
			return new SignalPanel(getPanelContainer(), (Signal) el);
		} else if (el instanceof Escalation) {
			return new EscalationPanel(getPanelContainer(), (Escalation) el);
		} else if (el instanceof Interface) {
			return new InterfacePanel(getPanelContainer(), (Interface) el);
		} else if (el instanceof Operation) {
			return new OperationPanel(getPanelContainer(), (Operation) el);
		} else if (el instanceof PartnerEntity || el instanceof PartnerRole) {
			return new PartnerPanel(getPanelContainer(), (BaseElement) el);
		} else if (el instanceof EventDefinition) {
			return new EventDefinitionPanel(getPanelContainer(), (EventDefinition) el, type.length() == 0
					|| !(((BaseElement) el).getParent() instanceof RootElements));
		} else if (el instanceof EventDefinitions) {
			List<String> columnsToShow = new ArrayList<String>();
			columnsToShow.add("id");
			columnsToShow.add("type");
			columnsToShow.add("reference");
			return new XMLTablePanel(getPanelContainer(), (EventDefinitions) el, "", null, columnsToShow,
					((Event) ((EventDefinitions) el).getParent()).getAllEventDefinitionList(), 350, 150, true, false);
		} else if (el instanceof Properties) {
			List<String> columnsToShow = new ArrayList<String>();
			columnsToShow.add("id");
			columnsToShow.add("name");
			columnsToShow.add("itemSubjectRef");
			return new XMLTablePanel(getPanelContainer(), (Properties) el, "", null, columnsToShow, ((Properties) el).getXMLElements(), 350, 150, true, false);
		} else if (el instanceof Documentations) {
			List<String> columnsToShow = new ArrayList<String>();
			columnsToShow.add("id");
			columnsToShow.add("type");
			columnsToShow.add("description");
			return new XMLTablePanel(getPanelContainer(), (Documentations) el, "", null, columnsToShow, ((Documentations) el).getXMLElements(), 500, 150, true,
					false);
		} else if (el instanceof Documentation) {
			if ("text".equals(type)) {
				return new XMLMultiLineTextPanel(getPanelContainer(), (Documentation) el, "text", "text/plain", 350, 100);
			} else if ("file".equals(type)) {
				return new XMLLocationPanel(getPanelContainer(), (Documentation) el, "file");
			} else {
				return new DocumentationPanel(getPanelContainer(), (Documentation) el);
			}
		} else if (el instanceof Resource) {
			return new ResourcePanel(getPanelContainer(), (Resource) el);
		} else if (el instanceof ResourceParameterBindings) {
			boolean hasToolbar = true;
			if (BPMNModelUtils.getDefinitions((XMLElement) el).getResource(type).getResourceParameters().isEmpty()) {
				hasToolbar = false;
			}
			List<String> columnsToShow = new ArrayList<String>();
			columnsToShow.add("parameterRef");
			columnsToShow.add("value");
			return new XMLTablePanel(getPanelContainer(), (ResourceParameterBindings) el, type, "resourceParameterBindings", columnsToShow,
					((ResourceParameterBindings) el).getXMLElements(), 450, 150, hasToolbar, false);
		} else if (el instanceof ResourceParameterBinding) {
			return new ResourceParameterBindingPanel(getPanelContainer(), (ResourceParameterBinding) el, BPMNModelUtils.getDefinitions((XMLElement) el)
					.getResource(type));
		} else if (el instanceof ResourceRoles) {
			List<String> columnsToShow = new ArrayList<String>();
			columnsToShow.add("id");
			columnsToShow.add("type");
			columnsToShow.add("name");
			columnsToShow.add("resourceRef");
			return new XMLTablePanel(getPanelContainer(), (ResourceRoles) el, "potentialOwner", null, columnsToShow, ((ResourceRoles) el).getXMLElements(),
					450, 150, true, false);
		} else if (el instanceof ResourceParameter) {
			return new ResourceParameterPanel(getPanelContainer(), (ResourceParameter) el);
		} else if (el instanceof ResourceRole) {
			return new ResourceRolePanel(getPanelContainer(), (ResourceRole) el);
		} else if (el instanceof ItemAwareElement) {
			if (el instanceof DataStore) {
				return new DataStorePanel(getPanelContainer(), (DataStore) el);
			} else {
				return new DataObjectPanel(getPanelContainer(), (BaseElement) el);
			}
		} else if (el instanceof DataAssociation) {
			return new DataAssociationPanel(getPanelContainer(), (DataAssociation) el);
		} else if (el instanceof Assignment) {
			return new AssignmentPanel(getPanelContainer(), (Assignment) el);
		} else if (el instanceof Category) {
			return new CategoryPanel(getPanelContainer(), (Category) el);
		} else if (el instanceof CategoryValue) {
			if ("selectFlowElements".equals(type)) {
				return new FlowElementsPanel(getPanelContainer(), (CategoryValue) el);
			} else {
				return new CategoryValuePanel(getPanelContainer(), (CategoryValue) el);
			}
		} else if (el instanceof Import) {
			return new ImportPanel(getPanelContainer(), (Import) el);
		} else if (el instanceof LoopCharacteristics) {
			return new LoopCharacteristicsPanel(getPanelContainer(), (LoopCharacteristics) el);
		} else if (el instanceof XMLAttribute) {
			return new XMLTextPanel(getPanelContainer(), (XMLElement) el);
		} else if (el instanceof Map.Entry<?, ?>) {
			return new NamespacePanel(getPanelContainer(), getPanelContainer().getParentDialog().getEditor().getGraphComponent().getGraph().getBpmnModel(),
					(Entry<?, ?>) el);
		}

		return null;
	}

}
