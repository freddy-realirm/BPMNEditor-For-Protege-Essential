package org.realirm.bpmn.slotwidget;

import java.io.InputStream;
import java.util.Collection;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.model.GraphModel;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;

public class BPMNSlotWidget extends AbstractSlotWidget {

	private BPMNEditor editor;
	
	private static final long serialVersionUID = 5691538173322501516L;

	public BPMNSlotWidget() {
		//Set Width and Height
		setPreferredColumns(12);
		setPreferredRows(10);
	}

	@Override
	public void initialize() {
		editor = new BPMNEditor(false);

		//Listen for Model Changes
		mxIEventListener trackChanges = new mxIEventListener() {

			@Override
			public void invoke(Object sender, mxEventObject evt) {
				valueChanged();
			}
			
		};
		GraphModel model = editor.getGraphComponent().getGraph().getModel();
		model.addListener(mxEvent.CHANGE, trackChanges);

		//Add Component to Protege
		LabeledComponent labeledComponent = new LabeledComponent(getLabel(), editor, true);
		add(labeledComponent);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection getValues() {
		BPMNGraphComponent graphComponent = editor.getGraphComponent();
		BPMNGraph graph = graphComponent.getGraph();
		BPMNCodec codec = new BPMNCodec(graph);
		String s = mxXmlUtils.getXml(codec.encode().getDocumentElement());
		return CollectionUtilities.createCollection(s);
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setValues(Collection arg0) {
		
		InputStream newBPMN = null;
		
		try {
			if (arg0.isEmpty()) {
				newBPMN = this.getClass().getResourceAsStream("resources/new.bpmn");
				if(newBPMN==null){
					System.out.println("newBPMN = null");
				}else{
					BPMNEditorUtils.openBPMN(editor,newBPMN);
				}
			} else {
				BPMNEditorUtils.openBPMN(editor,
						(String) CollectionUtilities.getFirstItem(arg0));
			}
		} catch (Exception e) {
			if(newBPMN==null){
				System.out.println("newBPMN = null");
			}else{
				BPMNEditorUtils.openBPMN(editor,newBPMN);
			}
		}
	}

	public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
		boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;
		boolean isCardinalitySingle = !cls
				.getTemplateSlotAllowsMultipleValues(slot);
		return isString && isCardinalitySingle;
	}

	public void setEditable(boolean b) {

	}

}
