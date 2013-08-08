package org.yaoqiang.bpmn.editor.view;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.view.Graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;

/**
 * BPMNEditorManager
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNEditorManager extends mxEventSource {

	protected BPMNEditor editor;

	protected GraphComponent graphComponent;

	protected Graph graph;

	protected GraphModel model;

	public BPMNEditorManager(BPMNEditor editor) {
		if (this.graph != null) {
			this.graph.removeListener(handler);
		}
		this.editor = editor;
		this.graphComponent = editor.getCurrentGraphComponent();
		this.graph = graphComponent.getGraph();
		this.model = graphComponent.getGraph().getModel();

		if (this.graph != null) {
			this.graph.addListener(mxEvent.LABEL_CHANGED, handler);
			this.graph.addListener(mxEvent.CELLS_REMOVED, handler);
		}
	}

	public final mxIEventListener getHandler() {
		return handler;
	}

	protected mxIEventListener handler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
			String eventName = evt.getName();
			Object[] cells = (Object[]) evt.getProperty("cells");
			mxCell cell = (mxCell) graph.getSelectionCell();
			if (cell == null) {
				if (cells != null && cells.length > 0) {
					cell = (mxCell) cells[0];
				}
			}

			model.beginUpdate();
			try {
				if (eventName.equals(mxEvent.LABEL_CHANGED)) {
					if (model.isCallProcess(cell)) {
						CallActivity callActivity = (CallActivity) cell.getValue();
						String calledElement = callActivity.getCalledElement();
						mxCell diagramCell = (mxCell) model.getCell(calledElement);
						if (diagramCell != null) {
							diagramCell.setValue(callActivity.getName());
						}
						if (editor.getGraphComponents().get(calledElement) != null) {
							editor.getCenterPane().setTabTitle(calledElement, callActivity.getName());
						}
					}
				} else if (eventName.equals(mxEvent.CELLS_REMOVED)) {
					for (Object c : cells) {
						if (model.isCallProcess(c)) {
							CallActivity callActivity = (CallActivity) cell.getValue();
							GraphComponent graphComponent = editor.getGraphComponent(callActivity.getCalledElement());
							editor.getCenterPane().remove(graphComponent);
						}
					}
				} else if (eventName.equals(mxEvent.UNDO) || eventName.equals(mxEvent.REDO)) {
					mxUndoableEdit edit = (mxUndoableEdit) evt.getProperty("edit");
					if (edit != null) {
						for (mxUndoableChange change : edit.getChanges()) {
							if (change instanceof mxGraphModel.mxValueChange) {
								mxCell c = (mxCell) ((mxGraphModel.mxValueChange) change).getCell();
								if (model.isCallProcess(c)) {
									CallActivity callActivity = (CallActivity) cell.getValue();
									String calledElement = callActivity.getCalledElement();
									mxCell diagramCell = (mxCell) model.getCell(calledElement);
									if (diagramCell != null) {
										diagramCell.setValue(callActivity.getName());
									}
									if (editor.getGraphComponent(calledElement) != null) {
										editor.getCenterPane().setTabTitle(calledElement, callActivity.getName());
									}
								}
							}
						}
					}
				}
			} finally {
				model.endUpdate();
			}
		}

	};

}
