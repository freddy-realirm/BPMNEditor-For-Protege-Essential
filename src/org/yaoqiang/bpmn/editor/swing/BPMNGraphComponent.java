package org.yaoqiang.bpmn.editor.swing;

import java.awt.Point;
import java.awt.print.PageFormat;
import java.util.Hashtable;

import javax.swing.TransferHandler;

import org.yaoqiang.bpmn.editor.handler.GraphTransferHandler;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.view.Graph;
import org.yaoqiang.util.Constants;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.util.mxCellOverlay;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraphView;

/**
 * BPMNGraphComponent
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNGraphComponent extends GraphComponent {

	private static final long serialVersionUID = 7012899759779759089L;

	public BPMNGraphComponent(Graph graph) {
		this(graph, true);
	}

	public BPMNGraphComponent(Graph graph, boolean setPageFormat) {
		super(graph);
		
		PageFormat pageFormat = getGraph().getModel().setDefaultPageFormat();
		setPageFormat(pageFormat);
		if (setPageFormat) {
			setPageFormat();
		}
	}

	private void setPageFormat() {
		Constants.PAGE_WIDTH = Double.parseDouble(Constants.SETTINGS.getProperty("pageWidth", String.valueOf(11.7 * 72)));
		Constants.PAGE_HEIGHT = Double.parseDouble(Constants.SETTINGS.getProperty("pageHeight", String.valueOf(8.3 * 72)));

		mxConstants.DEFAULT_STARTSIZE = Integer.parseInt(Constants.SETTINGS.getProperty("style_swimlane_title_size", "25"));

		Constants.SWIMLANE_WIDTH = (int) (pageFormat.getWidth() * 1.25 + (getGraph().getModel().getHorizontalPageCount() - 1)
				* (Constants.SWIMLANE_START_POINT + pageFormat.getWidth() * 1.25));

		Constants.SWIMLANE_HEIGHT = (int) (pageFormat.getHeight() * 1.2 + (getGraph().getModel().getPageCount() - 1)
				* (Constants.SWIMLANE_START_POINT + pageFormat.getHeight() * 1.2));
	}

	public BPMNGraph getGraph() {
		return (BPMNGraph) graph;
	}

	protected TransferHandler createTransferHandler() {
		return new GraphTransferHandler();
	}

	public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {
		if (cells.length > 0 && cells[0] instanceof mxICell) {
			mxICell dropCell = (mxICell) cells[0];

			if (getGraph().isFragments(dropCell) && !"org".equals(this.getName())) {
				String imagepath = mxUtils.getString(graph.getCellStyle(dropCell), mxConstants.STYLE_IMAGE);
				int begin = 0;
				if (imagepath.startsWith("File:///")) {
					begin = 8;
				}
				String bpmnfilepath = imagepath.substring(begin, imagepath.lastIndexOf(".")) + ".bpmn";
				BPMNCodec codec = new BPMNCodec(getGraph());
				codec.decode(bpmnfilepath, true, new Point((int) dx - 14, (int) dy - 41), target);
				return null;
			}
		}
		return super.importCells(cells, dx, dy, target, location);
	}

	public String validateGraph(Object cell, Hashtable<Object, Object> context) {
		mxIGraphModel model = graph.getModel();
		mxGraphView view = graph.getView();
		boolean isValid = true;
		int childCount = model.getChildCount(cell);

		for (int i = 0; i < childCount; i++) {
			Object tmp = model.getChildAt(cell, i);
			Hashtable<Object, Object> ctx = context;

			if (graph.isValidRoot(tmp)) {
				ctx = new Hashtable<Object, Object>();
			}

			String warn = validateGraph(tmp, ctx);
			// ==============start==============
			if (Constants.SETTINGS.getProperty("showWarning", "0").equals("0")) {
				setCellWarning(tmp, null);
				setCellAttachmentIcon(tmp);
				continue;
			}
			// ==============end================
			if (warn != null) {
				String html = warn.replaceAll("\n", "<br>");
				int len = html.length();
				setCellWarning(tmp, html.substring(0, Math.max(0, len - 4)));
			} else {
				setCellWarning(tmp, null);
			}
			setCellAttachmentIcon(tmp);// ==============start -- end==============
			isValid = isValid && warn == null;
		}

		// ==============start==============
		if (Constants.SETTINGS.getProperty("showWarning", "0").equals("0")) {
			setCellWarning(cell, null);
			setCellAttachmentIcon(cell);
			return null;
		}
		// ==============end================
		StringBuffer warning = new StringBuffer();

		// Adds error for invalid children if collapsed (children invisible)
		// if (graph.isCellCollapsed(cell) && !isValid) {
		// warning.append(mxResources.get("containsValidationErrors", "Contains Validation Errors") + "\n");
		// }

		// Checks edges and cells using the defined multiplicities
		if (model.isEdge(cell)) {
			String tmp = graph.getEdgeValidationError(cell, model.getTerminal(cell, true), model.getTerminal(cell, false));

			if (tmp != null) {
				warning.append(tmp);
			}
		} else {
			String tmp = graph.getCellValidationError(cell);

			if (tmp != null) {
				warning.append(tmp);
			}
		}

		// Checks custom validation rules
		String err = graph.validateCell(cell, context);

		if (err != null) {
			warning.append(err);
		}

		// Updates the display with the warning icons before any potential
		// alerts are displayed
		if (model.getParent(cell) == null) {
			view.validate();
		}

		return (warning.length() > 0 || !isValid) ? warning.toString() : null;
	}

	public void setCellAttachmentIcon(Object cell) {
		mxCellOverlay overlay = new mxCellOverlay(Constants.ATTACHMENT_ICON, "Attachments");
		if (((mxCell) cell).isVertex() && ((mxCell) cell).getValue() instanceof BaseElement) {
			BaseElement el = (BaseElement) ((mxCell) cell).getValue();
			if (el == null) {
				return;
			}
			if (BPMNModelUtils.hasAttachments(el)) {
				addCellOverlay(cell, overlay);
			} else {
				removeCellOverlayComponent(overlay, cell);
			}
		}
	}

}
