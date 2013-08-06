package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.XMLCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.common.ItemDefinition;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.data.DataObject;
import org.yaoqiang.bpmn.model.elements.data.DataObjectReference;

import com.mxgraph.model.mxCell;

/**
 * ItemDefinitionPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ItemDefinitionPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLComboPanel kindPanel;
	protected XMLComboPanel structureRefPanel;
	protected XMLPanel isCollectionPanel;

	public ItemDefinitionPanel(BPMNPanelContainer pc, ItemDefinition owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		List<String> choices = new ArrayList<String>();
		choices.add("Information");
		choices.add("Physical");
		kindPanel = new XMLComboPanel(pc, owner.get("itemKind"), null, choices, false, false, true);

		isCollectionPanel = new XMLCheckboxPanel(pc, owner.get("isCollection"), false);
		JPanel idKindPanel = new JPanel();
		idKindPanel.setLayout(new BoxLayout(idKindPanel, BoxLayout.X_AXIS));
		idKindPanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idKindPanel.add(Box.createHorizontalGlue());
		idKindPanel.add(kindPanel);
		this.add(idKindPanel);

		structureRefPanel = new XMLComboPanel(pc, owner.get("structureRef"), null, BPMNModelUtils.getDefinitions(owner).getTypes(), false, true, true);

		JPanel collectionTypePanel = new JPanel();
		collectionTypePanel.setLayout(new BoxLayout(collectionTypePanel, BoxLayout.X_AXIS));
		collectionTypePanel.add(structureRefPanel);
		collectionTypePanel.add(Box.createHorizontalGlue());
		collectionTypePanel.add(isCollectionPanel);
		this.add(collectionTypePanel);
	}

	public void saveObjects() {
		if (!structureRefPanel.isEmpty()) {
			kindPanel.saveObjects();
			structureRefPanel.saveObjects();
			boolean isCollectionOld = ((ItemDefinition)owner).isCollection();
			isCollectionPanel.saveObjects();
			boolean isCollectionNew = ((ItemDefinition)owner).isCollection();
			if (isCollectionOld != isCollectionNew) {
				BPMNGraph graph = getGraphComponent().getGraph();
				for (XMLElement o : BPMNModelUtils.getAllDataObjects(BPMNModelUtils.getDefinitions(getOwner()),((ItemDefinition)owner).getId())) {
					BaseElement data = (BaseElement) o;
					boolean isColOld = Boolean.parseBoolean(data.get("isCollection").toValue());
					data.set("isCollection", String.valueOf(isCollectionNew));
					boolean isColNew = Boolean.parseBoolean(data.get("isCollection").toValue());
					if (isColOld != isColNew) {
						String style = " ";
						if (isColNew) {
							style = "/org/yaoqiang/graph/images/marker_multiple.png";
						}
						String dataId = data.getId();
						if (data instanceof DataObject) {
							for (DataObjectReference dataObjectRef : ((FlowElements)data.getParent()).getDataObjectRefs(dataId)) {
								mxCell cell = (mxCell) graph.getModel().getCell(dataObjectRef.getId());
								graph.setCellStyles("loopImage", style, new Object[] { cell });
							}
						} else {
							mxCell cell = (mxCell) graph.getModel().getCell(dataId);
							graph.setCellStyles("loopImage", style, new Object[] { cell });
						}
					}
				}
				graph.refresh();
			}
			super.saveObjects();
		}
	}

}
