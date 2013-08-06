package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.XMLCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.elements.activities.Activity;
import org.yaoqiang.bpmn.model.elements.activities.LoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.StandardLoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.data.DataInput;
import org.yaoqiang.bpmn.model.elements.data.DataOutput;
import org.yaoqiang.util.Resources;


/**
 * LoopCharacteristicsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LoopCharacteristicsPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLCheckboxPanel checkboxPanel;

	protected XMLTextPanel textPanel;

	protected XMLComboPanel behaviorPanel;

	protected XMLTextPanel loopDataInputRefPanel;

	protected XMLPanel inputDataItemPanel;

	protected XMLPanel outputDataItemPanel;

	protected XMLMultiLineTextPanel mtp;

	public LoopCharacteristicsPanel(BPMNPanelContainer pc, LoopCharacteristics owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		if (owner instanceof StandardLoopCharacteristics) {
			checkboxPanel = new XMLCheckboxPanel(pc, owner.get("testBefore"), false);
			textPanel = new XMLTextPanel(pc, owner.get("loopMaximum"));
			JPanel r1Panel = new JPanel();
			r1Panel.setLayout(new BoxLayout(r1Panel, BoxLayout.X_AXIS));
			r1Panel.add(textPanel);
			r1Panel.add(checkboxPanel);
			this.add(r1Panel);
			mtp = new XMLMultiLineTextPanel(pc, owner.get("loopCondition"), "loopCondition", 350, 100);
			this.add(mtp);
		} else {
			checkboxPanel = new XMLCheckboxPanel(pc, owner.get("isSequential"), false);
			textPanel = new XMLTextPanel(pc, owner.get("loopCardinality"));
			JPanel r1Panel = new JPanel();
			r1Panel.setLayout(new BoxLayout(r1Panel, BoxLayout.X_AXIS));
			r1Panel.add(textPanel);
			r1Panel.add(checkboxPanel);
			this.add(r1Panel);
			loopDataInputRefPanel = new XMLTextPanel(pc, owner.get("loopDataInputRef"));
			List<String> choices = new ArrayList<String>();
			choices.add("All");
			choices.add("One");
			choices.add("Complex");
			choices.add("None");
			behaviorPanel = new XMLComboPanel(pc, owner.get("behavior"), choices, false, false, true);
			JPanel r2Panel = new JPanel();
			r2Panel.setLayout(new BoxLayout(r2Panel, BoxLayout.X_AXIS));
			r2Panel.add(loopDataInputRefPanel);
			r2Panel.add(behaviorPanel);
			this.add(r2Panel);
			inputDataItemPanel = new DataObjectPanel(pc, (DataInput) owner.get("inputDataItem"));
			inputDataItemPanel.setBorder(BorderFactory.createTitledBorder(Resources.get("inputDataItem")));
			outputDataItemPanel = new DataObjectPanel(pc, (DataOutput) owner.get("outputDataItem"));
			outputDataItemPanel.setBorder(BorderFactory.createTitledBorder(Resources.get("outputDataItem")));
			mtp = new XMLMultiLineTextPanel(pc, owner.get("completionCondition"), "completionCondition", 350, 100);
			this.add(inputDataItemPanel);
			this.add(outputDataItemPanel);
			this.add(mtp);
		}

	}

	public void saveObjects() {
		textPanel.saveObjects();
		mtp.saveObjects();

		if (owner instanceof StandardLoopCharacteristics) {
			checkboxPanel.saveObjects();
		} else {
			behaviorPanel.saveObjects();
			loopDataInputRefPanel.saveObjects();
			inputDataItemPanel.saveObjects();
			outputDataItemPanel.saveObjects();
			
			BPMNGraph graph = getGraphComponent().getGraph();
			boolean isSequentialOld = Boolean.parseBoolean(((LoopCharacteristics) owner).get("isSequential").toValue());
			checkboxPanel.saveObjects();
			boolean isSequentialNew = Boolean.parseBoolean(((LoopCharacteristics) owner).get("isSequential").toValue());
			if (isSequentialOld != isSequentialNew) {
				String loop = "multi_instance";
				String loopImage = "/org/yaoqiang/graph/images/marker_multiple.png";
				if (isSequentialNew) {
					loop = "multi_instance_sequential";
					loopImage = "/org/yaoqiang/graph/images/marker_multiple_sequential.png";
				}
				Object cell = graph.getModel().getCell(((Activity) getOwner().getParent().getParent()).getId());
				graph.setCellStyles("loop", loop, new Object[] { cell });
				graph.setCellStyles("loopImage", loopImage, new Object[] { cell });
				graph.refresh();
			}
		}
	}

}
