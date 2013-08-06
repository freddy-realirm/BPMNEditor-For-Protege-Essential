package org.yaoqiang.bpmn.editor.action;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.swing.MenuBar;
import org.yaoqiang.bpmn.editor.util.EditorConstants;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.util.GraphUtils;
import org.yaoqiang.graph.view.Graph;
import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;
import org.yaoqiang.util.Utils;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;

/**
 * EditorActions
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class EditorActions extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public static final int UNDO = 108;
	public static final int REDO = 109;

	public static final int EDIT_CALLED_PROCESS = 201;
	public static final int CLOSE_CALLED_PROCESS = 202;

	public static final int GRID = 301;
	public static final int GRID_STYLE = 302;
	public static final int GRID_COLOR = 303;
	public static final int GRID_SIZE = 304;

	public static final int FONT_STYLE = 401;
	public static final int COLOR = 402;
	public static final int KEY_VALUE = 404;

	protected int type;

	private String stringValue;

	private String stringValue2;

	private double doubleValue;

	protected static String lastDir;

	public EditorActions(int type) {
		this.type = type;
	}

	public static EditorActions getAction(int type) {
		return new EditorActions(type);
	}

	public static EditorActions getGridStyleAction(int value) {
		return new EditorActions(GRID_STYLE).setDoubleValue(value);
	}

	public static EditorActions getFontStyleAction(int value) {
		return new EditorActions(FONT_STYLE).setDoubleValue(value);
	}

	public static EditorActions getColorAction(String name, String key) {
		return new EditorActions(COLOR).setStringValue(name).setStringValue2(key);
	}

	public static EditorActions getKeyValueAction(String key, String value) {
		return new EditorActions(KEY_VALUE).setStringValue(key).setStringValue2(value);
	}

	public static EditorActions getCloseCalledProcessAction(String name) {
		return new EditorActions(CLOSE_CALLED_PROCESS).setStringValue(name);
	}

	public void actionPerformed(ActionEvent e) {
		BPMNEditor editor = getEditor(e);
		// BaseDialog dialog = editor.getDialog();
		GraphComponent graphComponent = editor.getGraphComponent();
		Graph graph = graphComponent.getGraph();
		GraphModel model = graph.getModel();

		mxCell cell = (mxCell) graph.getSelectionCell();
		if (graph.isChoreography(cell) || graph.isSubChoreography(cell)) {
			cell = GraphUtils.getChoreographyActivity(graph, cell);
		}
		XMLElement el = null;
		if (cell != null) {
			el = (XMLElement) model.getValue(cell);
		}

		if (type == UNDO) {
			editor.getUndoManager().undo();
			editor.getBpmnView().refreshView(graph);
		} else if (type == REDO) {
			editor.getUndoManager().redo();
			editor.getBpmnView().refreshView(graph);
		} else if (type == GRID) {
			boolean enabled = !graph.isGridEnabled();
			graph.setGridEnabled(enabled);
			graphComponent.setGridVisible(enabled);
			for (GraphComponent com : editor.getGraphComponents().values()) {
				com.setGridVisible(enabled);
			}
			editor.getCurrentGraphComponent().repaint();
			MenuBar.getGridMenuItem().setSelected(enabled);
		} else if (type == GRID_STYLE) {
			graphComponent.setGridStyle((int) doubleValue);
			for (GraphComponent com : editor.getGraphComponents().values()) {
				com.setGridStyle((int) doubleValue);
			}
			editor.getCurrentGraphComponent().repaint();
		} else if (type == GRID_COLOR) {
			Color newColor = JColorChooser.showDialog(graphComponent, Resources.get("gridColor"), graphComponent.getGridColor());
			if (newColor != null) {
				graphComponent.setGridColor(newColor);
				for (GraphComponent com : editor.getGraphComponents().values()) {
					com.setGridColor(newColor);
				}
				editor.getCurrentGraphComponent().repaint();
			}
		} else if (type == GRID_SIZE) {
			double size = 0;
			String value = (String) JOptionPane.showInputDialog(graphComponent, Resources.get("value"), Resources.get("gridSize"), JOptionPane.PLAIN_MESSAGE,
					null, null, graph.getGridSize());

			if (value != null) {
				size = Double.parseDouble(value);
			}
			if (size > 0) {
				graph.setGridSize((int) size);
				editor.getCurrentGraphComponent().repaint();
			}
		} else if (type == FONT_STYLE) {
			model.beginUpdate();
			try {
				if (el != null) {
					graphComponent.stopEditing(false);
					graphComponent.getGraph().toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE, (int) doubleValue);
					GraphUtils.setElementStyles(graph, mxConstants.STYLE_FONTSTYLE);
				}
			} finally {
				model.endUpdate();
			}
		} else if (type == COLOR) {
			if (!graph.isSelectionEmpty()) {
				Color newColor = JColorChooser.showDialog(graphComponent, stringValue, null);
				if (newColor != null) {
					int index = EditorConstants.LAST_FILLCOLOR.indexOf(newColor);
					if (index > -1) {
						EditorConstants.LAST_FILLCOLOR.remove(index);
					} else {
						EditorConstants.LAST_FILLCOLOR.remove(EditorConstants.LAST_FILLCOLOR.size() - 1);
					}
					EditorConstants.LAST_FILLCOLOR.add(0, newColor);
					editor.getMainToolBar().getColorCombo().setModel(new DefaultComboBoxModel(EditorConstants.LAST_FILLCOLOR.toArray()));
					String colorString = "";
					for (Color c : EditorConstants.LAST_FILLCOLOR) {
						colorString += mxUtils.hexString(c) + ",";
					}
					Utils.saveToConfigureFile("lastFileColor", colorString);
					graph.setCellStyles(stringValue2, mxUtils.hexString(newColor));
					GraphUtils.setElementStyles(graph, stringValue2);
				}
			}
		} else if (type == KEY_VALUE) {
			graph.setCellStyles(stringValue, stringValue2, new Object[] { cell });
			GraphUtils.setElementStyles(graph, stringValue);
		} else if (type == EDIT_CALLED_PROCESS) {
			editor.insertGraphComponent((mxCell) graph.getSelectionCell());
		} else if (type == CLOSE_CALLED_PROCESS) {
			editor.removeGraphComponent(stringValue);
		}
		graphComponent.requestFocusInWindow();
	}

	protected void openXmlPng(BaseEditor editor, File file) throws IOException {
		Map<String, String> text = mxPngTextDecoder.decodeCompressedText(new FileInputStream(file));

		if (text != null) {
			String value = text.get("YGraphModel");
			if (value == null) {
				value = text.get("mxGraphModel");
			}
			if (value == null) {
				value = text.get("GraphModel");
			}
			if (value != null) {
				Document document = mxXmlUtils.parseXml(URLDecoder.decode(value, "UTF-8"));
				mxCodec codec = new mxCodec(document);
				codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());
				editor.setCurrentFile(file);
				resetEditor(editor, false);
				return;
			}
		}

		JOptionPane.showMessageDialog(editor, Resources.get("imageContainsNoDiagramData"));
	}

	protected void saveXmlPng(BaseEditor editor, String filename, Color bg) throws IOException {
		mxGraphComponent graphComponent = editor.getGraphComponent();
		mxGraph graph = graphComponent.getGraph();

		// Creates the image for the PNG file
		BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, bg, graphComponent.isAntiAlias(), null, true, graphComponent.getCanvas());

		// Creates the URL-encoded XML data
		mxCodec codec = new mxCodec();
		String xml = URLEncoder.encode(mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
		mxPngEncodeParam param = mxPngEncodeParam.getDefaultEncodeParam(image);
		param.setCompressedText(new String[] { "YGraphModel", xml });

		// Saves as a PNG file
		FileOutputStream outputStream = new FileOutputStream(new File(filename));
		try {
			mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream, param);

			if (image != null) {
				encoder.encode(image);

				editor.setModified(false);
				editor.setCurrentFile(new File(filename));
			} else {
				JOptionPane.showMessageDialog(graphComponent, Resources.get("noImageData"));
			}
		} finally {
			outputStream.close();
		}
	}

	protected void resetEditor(BaseEditor editor, boolean resetPageFormat) {
		GraphComponent graphComponent = editor.getGraphComponent();
		Graph graph = graphComponent.getGraph();
		GraphModel model = graph.getModel();
		if (resetPageFormat) {
			PageFormat pageFormat = model.getPageFormat();
			Paper paper = pageFormat.getPaper();
			paper.setSize(Constants.PAGE_HEIGHT, Constants.PAGE_WIDTH);
			pageFormat.setPaper(paper);
			model.setPageFormat(pageFormat);
		}

		graphComponent.setPageFormat(model.getPageFormat());
		graphComponent.setVerticalPageCount(model.getPageCount());
		graphComponent.setHorizontalPageCount(model.getHorizontalPageCount());
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground(model.getBackgroundColor());

		editor.setModified(resetPageFormat);
		editor.getUndoManager().clear();
		editor.getGraphComponent().zoomAndCenter();
	}

	public EditorActions setStringValue(String value) {
		this.stringValue = value;
		return this;
	}

	public EditorActions setStringValue2(String value) {
		this.stringValue2 = value;
		return this;
	}

	public EditorActions setDoubleValue(double value) {
		this.doubleValue = value;
		return this;
	}

	public final BPMNEditor getEditor(ActionEvent e) {
		if (e.getSource() instanceof Component) {
			Component component = (Component) e.getSource();

			while (component != null && !(component instanceof BPMNEditor)) {
				component = component.getParent();
			}

			return (BPMNEditor) component;
		}

		return null;
	}
}
