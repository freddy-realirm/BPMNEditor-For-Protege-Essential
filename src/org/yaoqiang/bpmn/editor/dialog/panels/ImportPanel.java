package org.yaoqiang.bpmn.editor.dialog.panels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;

import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLLocationPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.util.EditorUtils;
import org.yaoqiang.bpmn.model.BPMNModelCodec;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.core.common.ItemDefinition;
import org.yaoqiang.bpmn.model.elements.core.common.Message;
import org.yaoqiang.bpmn.model.elements.core.foundation.RootElements;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Import;
import org.yaoqiang.graph.io.bpmn.BPMNCodecUtils;
import org.yaoqiang.graph.util.GraphUtils;

/**
 * ImportPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ImportPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel importTypePanel;
	protected XMLTextPanel namespacePanel;
	protected XMLLocationPanel locationPanel;

	public ImportPanel(BPMNPanelContainer pc, final Import owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		List<String> choices = new ArrayList<String>();
		choices.add("http://www.w3.org/TR/wsdl20/");
		choices.add("http://schemas.xmlsoap.org/wsdl/");
		choices.add("http://www.w3.org/2001/XMLSchema");
		choices.add("http://www.omg.org/spec/BPMN/20100524/MODEL");
		importTypePanel = new XMLComboPanel(pc, owner.get("importType"), null, choices, false, true, true);
		namespacePanel = new XMLTextPanel(pc, owner.get("namespace"));
		locationPanel = new XMLLocationPanel(getPanelContainer(), owner.get("location"), "location");

		this.add(locationPanel);
		this.add(importTypePanel);
		this.add(namespacePanel);
	}

	public final XMLComboPanel getImportTypePanel() {
		return importTypePanel;
	}

	public final XMLTextPanel getNamespacePanel() {
		return namespacePanel;
	}

	public void saveObjects() {
		String location = locationPanel.getText();
		if (location.length() == 0) {
			return;
		}
		locationPanel.saveObjects();
		namespacePanel.saveObjects();
		importTypePanel.saveObjects();

		Document document = EditorUtils.parseXml(null, location);
		if (document != null) {
			String nsName = "";
			String nsuri = namespacePanel.getText();
			String namespace = document.getDocumentElement().getAttribute("targetNamespace");
			if (namespace != null) {
				nsuri = namespace;
				((Import) owner).setNamespace(namespace);
			}
			Definitions bpmnModel = BPMNModelUtils.getDefinitions(getOwner());
			Map<String, String> nss = bpmnModel.getNamespaces();
			if (!nss.containsKey(nsuri)) {
				String name = "ns";
				int n = 1;
				while (nss.containsValue(name + n)) {
					n++;
				}
				nsName = name + n;
				nss.put(nsuri, nsName);
			} else {
				nsName = nss.get(nsuri);
			}

			String importType = BPMNEditorUtils.getXmlFileType(document);
			((Import) owner).setImportType(importType);

			if ("http://www.omg.org/spec/BPMN/20100524/MODEL".equals(importType)) {
				Definitions defs = new Definitions();
				new BPMNModelCodec().decode(document.getDocumentElement(), defs);
				BPMNEditor.getGraph().getExternalDefinitions().put(defs.getTargetNamespace(), defs);
				BPMNEditor.getGraph().getExternalDefinitions()
						.putAll(BPMNEditorUtils.getExternalDefinitions(new File(EditorUtils.getFilePath(null, location)).getParent(), defs));
			}

			List<String> types = new ArrayList<String>();
			if (GraphUtils.isWSDL11File(importType)) {
				types = BPMNEditorUtils.getWSDLMessages(document);
			} else if (GraphUtils.isWSDL20File(importType)) {
				types = BPMNEditorUtils.getWSDLTypes(document);
			} else if (BPMNEditorUtils.isXSDFile(importType)) {
				types = BPMNEditorUtils.getXSDElements(document);
			}
			for (String t : types) {
				String type = nsName + ":" + t;
				if (BPMNModelUtils.hasItemDefinition(type, bpmnModel)) {
					continue;
				} else {
					ItemDefinition itemDefinition = (ItemDefinition) bpmnModel.generateRootElement(RootElements.TYPE_ITEM_DEFINITION);
					itemDefinition.setStructureRef(type);
					bpmnModel.addRootElement(itemDefinition);
					if (GraphUtils.isWSDL11File(importType) || GraphUtils.isWSDL20File(importType)) {
						Message message = (Message) bpmnModel.generateRootElement(RootElements.TYPE_MESSAGE);
						message.setName("Message " + message.getId().substring(4));
						// message.setItemRef("tns:" + itemDefinition.getId());
						message.setItemRef(itemDefinition.getId());
						bpmnModel.addRootElement(message);
					}
				}
				bpmnModel.addType(type);
			}
			if (!types.isEmpty()) {
				BPMNCodecUtils.generateInterfaceForWSDL(document, bpmnModel, importType, nsName);
			}
		}
		super.saveObjects();
	}

}
