package org.yaoqiang.bpmn.editor.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import org.yaoqiang.bpmn.editor.action.EditorActions;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.util.EditorConstants;
import org.yaoqiang.graph.action.GraphActions;
import org.yaoqiang.graph.util.GraphUtils;
import org.yaoqiang.graph.view.Graph;
import org.yaoqiang.util.Resources;
import org.yaoqiang.util.Utils;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraphView;

/**
 * MainToolBar
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class MainToolBar extends JToolBar {

	private static final long serialVersionUID = -8015443128436394471L;

	private boolean ignoreZoomChange = false;

	private JComboBox colorCombo;

	public MainToolBar(final BaseEditor editor, int orientation) {
		super(orientation);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);

		populateFileToolbar(editor);

		addSeparator();

		add(editor.bind(Resources.get("cut"), TransferHandler.getCutAction(), "/org/yaoqiang/bpmn/editor/images/cut.png"));
		add(editor.bind(Resources.get("copy"), TransferHandler.getCopyAction(), "/org/yaoqiang/bpmn/editor/images/copy.png"));
		add(editor.bind(Resources.get("paste"), TransferHandler.getPasteAction(), "/org/yaoqiang/bpmn/editor/images/paste.png"));

		add(editor.bind(Resources.get("delete"), GraphActions.getAction(GraphActions.DELETE), "/org/yaoqiang/bpmn/editor/images/delete.png"));

		addSeparator();

		add(editor.bind(Resources.get("undo"), EditorActions.getAction(EditorActions.UNDO), "/org/yaoqiang/bpmn/editor/images/undo.png"));
		add(editor.bind(Resources.get("redo"), EditorActions.getAction(EditorActions.REDO), "/org/yaoqiang/bpmn/editor/images/redo.png"));

		addSeparator();

		final Graph graph = editor.getCurrentGraphComponent().getGraph();
		final mxGraphView view = graph.getView();
		final JComboBox zoomCombo = new JComboBox(new Object[] { "400%", "200%", "150%", "100%", "75%", "50%" });
		zoomCombo.setEditable(true);
		zoomCombo.setMinimumSize(new Dimension(72, 0));
		zoomCombo.setPreferredSize(new Dimension(72, 26));
		zoomCombo.setMaximumSize(new Dimension(72, 100));
		zoomCombo.setMaximumRowCount(9);
		add(zoomCombo);

		mxIEventListener scaleTracker = new mxIEventListener() {

			public void invoke(Object sender, mxEventObject evt) {
				ignoreZoomChange = true;

				try {
					zoomCombo.setSelectedItem((int) Math.round(100 * view.getScale()) + "%");
				} finally {
					ignoreZoomChange = false;
				}
			}
		};

		// Installs the scale tracker to update the value in the combo box
		// if the zoom is changed from outside the combo box
		view.getGraph().getView().addListener(mxEvent.SCALE, scaleTracker);
		view.getGraph().getView().addListener(mxEvent.SCALE_AND_TRANSLATE, scaleTracker);

		// Invokes once to sync with the actual zoom value
		scaleTracker.invoke(null, null);

		zoomCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mxGraphComponent graphComponent = editor.getCurrentGraphComponent();

				// Zoomcombo is changed when the scale is changed in the diagram
				// but the change is ignored here
				if (!ignoreZoomChange) {
					String zoom = zoomCombo.getSelectedItem().toString();

					if (zoom.equals(Resources.get("page"))) {
						graphComponent.setPageVisible(true);
						graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
					} else if (zoom.equals(Resources.get("width"))) {
						graphComponent.setPageVisible(true);
						graphComponent.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
					} else {
						try {
							zoom = zoom.replace("%", "");
							double scale = Math.min(16, Math.max(0.01, Double.parseDouble(zoom) / 100));
							graphComponent.zoomTo(scale, graphComponent.isCenterZoom());
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(editor, ex.getMessage());
						}
					}
				}
			}
		});

		addSeparator();

		add(editor.bind(Resources.get("bold"), EditorActions.getFontStyleAction(mxConstants.FONT_BOLD), "/org/yaoqiang/bpmn/editor/images/bold.gif"));
		add(editor.bind(Resources.get("italic"), EditorActions.getFontStyleAction(mxConstants.FONT_ITALIC), "/org/yaoqiang/bpmn/editor/images/italic.gif"));

		addSeparator();

		add(editor.bind(Resources.get("left"), EditorActions.getKeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT),
				"/org/yaoqiang/bpmn/editor/images/left.gif"));
		add(editor.bind(Resources.get("center"), EditorActions.getKeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER),
				"/org/yaoqiang/bpmn/editor/images/center.gif"));
		add(editor.bind(Resources.get("right"), EditorActions.getKeyValueAction(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT),
				"/org/yaoqiang/bpmn/editor/images/right.gif"));

		addSeparator();

		add(editor.bind(Resources.get("FontColor"), EditorActions.getColorAction(Resources.get("fontColor"), mxConstants.STYLE_FONTCOLOR),
				"/org/yaoqiang/bpmn/editor/images/fontcolor.gif"));
		add(editor.bind(Resources.get("Stroke"), EditorActions.getColorAction(Resources.get("stroke"), mxConstants.STYLE_STROKECOLOR),
				"/org/yaoqiang/bpmn/editor/images/linecolor.gif"));
		add(editor.bind(Resources.get("FillColor"), EditorActions.getColorAction(Resources.get("fillColor"), mxConstants.STYLE_FILLCOLOR),
				"/org/yaoqiang/bpmn/editor/images/fillcolor.gif"));

		colorCombo = new JComboBox(EditorConstants.LAST_FILLCOLOR.toArray());
		colorCombo.setEditable(true);
		colorCombo.setMinimumSize(new Dimension(50, 0));
		colorCombo.setPreferredSize(new Dimension(50, 26));
		colorCombo.setMaximumSize(new Dimension(50, 100));
		colorCombo.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 5499544291833060088L;
			private final Dimension preferredSize = new Dimension(0, 26);

			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				setToolTipText(value.toString());
				setBackground((Color) value);
				setPreferredSize(preferredSize);
				return this;
			}
		});
		colorCombo.setEditor(new ColorComboBoxEditor(editor.getCurrentGraphComponent(), Color.LIGHT_GRAY));
		add(colorCombo);

		colorCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = (Color) colorCombo.getSelectedItem();
				if (color != null) {
					EditorConstants.LAST_FILLCOLOR.add(0, EditorConstants.LAST_FILLCOLOR.remove(colorCombo.getSelectedIndex()));
					colorCombo.setModel(new DefaultComboBoxModel(EditorConstants.LAST_FILLCOLOR.toArray()));
					String colorString = "";
					for (Color c : EditorConstants.LAST_FILLCOLOR) {
						colorString += mxUtils.hexString(c) + ",";
					}
					Utils.saveToConfigureFile("lastFileColor", colorString);
					graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, mxUtils.hexString(color));
					GraphUtils.setElementStyles(graph, mxConstants.STYLE_FILLCOLOR);
					editor.getCurrentGraphComponent().requestFocusInWindow();
				}
			}
		});

		addSeparator();

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		List<String> fonts = new ArrayList<String>();
		fonts.addAll(Arrays.asList(new String[] { this.getFont().getFamily(), "Arial", "Helvetica", "Verdana", "Times New Roman", "Courier New", "-" }));
		fonts.addAll(Arrays.asList(env.getAvailableFontFamilyNames()));

		final JComboBox fontCombo = new JComboBox(fonts.toArray());
		fontCombo.setEditable(true);
		fontCombo.setMinimumSize(new Dimension(150, 0));
		fontCombo.setPreferredSize(new Dimension(150, 26));
		fontCombo.setMaximumSize(new Dimension(150, 100));
		add(fontCombo);

		fontCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String font = fontCombo.getSelectedItem().toString();

				if (font != null && !font.equals("-")) {
					Graph graph = editor.getCurrentGraphComponent().getGraph();
					graph.setCellStyles(mxConstants.STYLE_FONTFAMILY, font);
					GraphUtils.setElementStyles(graph, mxConstants.STYLE_FONTFAMILY);
				}
			}
		});

		final JComboBox sizeCombo = new JComboBox(new Object[] { "6pt", "8pt", "9pt", "10pt", "11pt", "12pt", "14pt", "18pt", "24pt", "30pt", "36pt", "48pt",
				"60pt" });
		sizeCombo.setEditable(true);
		sizeCombo.setMinimumSize(new Dimension(65, 0));
		sizeCombo.setPreferredSize(new Dimension(65, 26));
		sizeCombo.setMaximumSize(new Dimension(65, 100));
		add(sizeCombo);

		sizeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graph graph = editor.getCurrentGraphComponent().getGraph();
				graph.setCellStyles(mxConstants.STYLE_FONTSIZE, sizeCombo.getSelectedItem().toString().replace("pt", ""));
				GraphUtils.setElementStyles(graph, mxConstants.STYLE_FONTSIZE);
			}
		});

	}

	protected void populateFileToolbar(BaseEditor editor) {
		add(editor.bind("Import (from file)", ModelActions.getAction(ModelActions.OPEN), "/org/yaoqiang/bpmn/editor/images/open.png"));
		add(editor.bind("Import (from URL)", ModelActions.getAction(ModelActions.OPEN_URL), "/org/yaoqiang/bpmn/editor/images/openurl.png"));
		add(editor.bind("export (to file)", ModelActions.getSaveAsAction(), "/org/yaoqiang/bpmn/editor/images/save_as.png"));
		add(editor.bind("export (to PNG)", ModelActions.getSaveAsPNG(), "/org/yaoqiang/bpmn/editor/images/png.png"));
		add(editor.bind("export (to OpenDocument text)", ModelActions.getSaveAsODT(), "/org/yaoqiang/bpmn/editor/images/odt.png"));

		addSeparator();

		add(editor.bind(Resources.get("pageSetup"), GraphActions.getAction(GraphActions.PAGE_SETUP), "/org/yaoqiang/bpmn/editor/images/page_setup.png"));
		add(editor.bind(Resources.get("print"), GraphActions.getAction(GraphActions.PRINT), "/org/yaoqiang/bpmn/editor/images/printer.png"));
	}

	public JComboBox getColorCombo() {
		return colorCombo;
	}

}
