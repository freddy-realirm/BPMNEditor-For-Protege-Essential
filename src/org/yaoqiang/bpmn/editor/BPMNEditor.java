package org.yaoqiang.bpmn.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.yaoqiang.bpmn.editor.dialog.BPMNElementDialog;
import org.yaoqiang.bpmn.editor.swing.AlignToolBar;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.swing.BPMNViewComponent;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.swing.MainToolBar;
import org.yaoqiang.bpmn.editor.swing.MenuBar;
import org.yaoqiang.bpmn.editor.swing.Palettes;
import org.yaoqiang.bpmn.editor.swing.TabbedPane;
import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.util.EditorConstants;
import org.yaoqiang.bpmn.editor.view.BPMNEditorManager;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelParsingErrors.ErrorMessage;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.dialog.WarningDialog;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;

/**
 * BPMNEditor
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNEditor extends BaseEditor {

	private static final long serialVersionUID = -6561623072112577140L;

	static {
		try {
			spellChecker = Class.forName("com.inet.jortho.SpellChecker");
		} catch (ClassNotFoundException e) {
		}
	}

	protected static Class<?> spellChecker;

	protected static String availableLocales = "";

	protected BPMNElementDialog bpmnElementDialog;

	protected Palettes fragmentsPalette;

	protected BPMNViewComponent bpmnView;

	protected JMenu diagramsMenu;

	protected MainToolBar mainToolBar;

	public BPMNEditor() {
		this(true);
	}

	public BPMNEditor(boolean createFrame) {
		initEditor();
		
		bpmnElementDialog = new BPMNElementDialog(this);

		BPMNEditorManager bpmnEditorManager = new BPMNEditorManager(this);
		undoManager.addListener(mxEvent.UNDO, bpmnEditorManager.getHandler());
		undoManager.addListener(mxEvent.REDO, bpmnEditorManager.getHandler());

		bpmnView = new BPMNViewComponent((BPMNGraph) graph);

		installPanel();
		initFragmentsPalette();

		if (createFrame) {
			frame = createFrame(new MenuBar(this));
		}
	}

	public BPMNElementDialog getBpmnElementDialog() {
		return bpmnElementDialog;
	}

	public BPMNElementDialog createBpmnElementDialog() {
		return new BPMNElementDialog(this);
	}

	protected GraphComponent createGraphComponent(String name) {
		GraphComponent graphComponent = null;
		if (graph != null) {
			graphComponent = new BPMNGraphComponent((BPMNGraph) graph);
		} else {
			graphComponent = new BPMNGraphComponent(new BPMNGraph(new GraphModel(Constants.VERSION)));
		}
		if (name != null) {
			graphComponent.setName(name);
		}
		return graphComponent;
	}

	protected void installGraphComponentHandlers(GraphComponent graphComponent) {
		super.installGraphComponentHandlers(graphComponent);
	}

	protected void initFragmentsPalette() {
		fragmentsPalette = insertPalette(Resources.get("fragments"));
		fragmentsPalette.addListener(mxEvent.SELECT, eventListener);
		if (Constants.SETTINGS.getProperty("showPatterns", "1").equals("1")) {
			int[] nums = { 1, 2, 3, 4, 5, 6, 7 };
			for (int n : nums) {
				fragmentsPalette.addPatternTemplate(n);
			}
		}

		File fragmentsDir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_FRAGMENTS_DIR);

		for (File imgFile : fragmentsDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".png");
			}
		})) {
			fragmentsPalette.addFragmentTemplate(imgFile);
		}
	}

	protected void installPanel() {
		centerPane = new TabbedPane(this);

		centerPane.add(Resources.get("newDiagram"), graphComponent);

		centerPane.add(Resources.get("bpmn"), bpmnView);

		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(new AlignToolBar(this, JToolBar.VERTICAL), BorderLayout.WEST);
		rightPane.add(centerPane, BorderLayout.CENTER);
		boolean enableCollaboration = true;
		try {
			collaborationPane = (JPanel) BPMNEditor.class.getClassLoader().loadClass("org.yaoqiang.collaboration.MainPanel").newInstance();
		} catch (ClassNotFoundException e) {
			enableCollaboration = false;
		} catch (InstantiationException e) {
			enableCollaboration = false;
		} catch (IllegalAccessException e) {
			enableCollaboration = false;
		}
		if (enableCollaboration && Constants.SETTINGS.getProperty("enableCollaboration", "0").equals("1")) {
			rightPane.add(collaborationPane, BorderLayout.EAST);
		}

		JComponent leftPane = palettesPane;
		if (Constants.SETTINGS.getProperty("showFloatingOutline", Constants.OS.startsWith("Windows") ? "1" : "0").equals("0")) {
			leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphOverview, palettesPane);
			if (Constants.SETTINGS.getProperty("showOutline", "1").equals("1")) {
				((JSplitPane) leftPane).setDividerLocation(180);
			} else {
				((JSplitPane) leftPane).setDividerLocation(0);
			}
			((JSplitPane) leftPane).setDividerSize(6);
			((JSplitPane) leftPane).setBorder(null);
		}

		mainToolBar = new MainToolBar(this, JToolBar.HORIZONTAL);
		createMainPane(mainToolBar, leftPane, rightPane);
	}

	public JPanel getCollaborationPane() {
		return collaborationPane;
	}

	public BPMNGraphComponent getGraphComponent() {
		return (BPMNGraphComponent) graphComponent;
	}

	public static BPMNGraph getGraph() {
		return (BPMNGraph) graph;
	}

	public BPMNViewComponent getBpmnView() {
		return bpmnView;
	}

	public Palettes getFragmentsPalette() {
		return fragmentsPalette;
	}

	public JMenu getDiagramsMenu() {
		return diagramsMenu;
	}

	public void setDiagramsMenu(JMenu diagramsMenu) {
		this.diagramsMenu = diagramsMenu;
	}

	public MainToolBar getMainToolBar() {
		return mainToolBar;
	}

	public void status(String msg) {
		statusBar.setText(msg);
	}

	public void insertGraphComponent(mxCell cell) {
		String calledElement = ((CallActivity) cell.getValue()).getCalledElement();
		if (calledElement.equals("callProcess")) {
			BPMNProcess process = graph.getModel().getBpmnModel().createProcess(false);
			calledElement = process.getId();
			((CallActivity) cell.getValue()).setCalledElement(calledElement);
		}
		insertGraphComponent(calledElement, ((CallActivity) cell.getValue()).getName());
	}

	public void insertGraphComponent(String id, String value) {
		mxCell diagramCell = (mxCell) graph.getModel().getCell(id);
		GraphComponent com = graphComponents.get(id);
		if (com == null) {
			if (diagramCell == graph.getModel().getChildAt(graph.getModel().getRoot(), 0)) {
				com = graphComponent;
			} else {
				com = createGraphComponent(id);
				graphComponents.put(id, com);
				if (diagramCell == null) {
					diagramCell = new mxCell();
					diagramCell.setId(id);
					mxCell root = (mxCell) graph.getModel().getRoot();
					root.insert(diagramCell, root.getChildCount());
					graph.getModel().getCells().put(id, diagramCell);
				}
				com.setLastViewRoot(diagramCell);
			}
		}
		diagramCell.setValue(value);
		currentGraphComponent = com;
		graph.getView().setCurrentRoot(com.getLastViewRoot());
		graph.clearSelection();

		if (com != graphComponent) {
			centerPane.add(value, com);
			installGraphComponentHandlers(currentGraphComponent);
			installGraphComponentListeners(currentGraphComponent);
		}

		centerPane.setSelectedComponent(com);

		refreshGraphOverview();
	}

	public void removeGraphComponent(String name) {
		super.removeGraphComponent(name);
		((JRadioButtonMenuItem) this.getDiagramsMenu().getMenuComponent(0)).setSelected(true);
	}

	public void resetDiagramName() {
		String name = null;
		if ("BPMNEditor".equals(currentGraphComponent.getName())) {
			if (((BPMNGraph) graph).getBpmnModel().getFirstBPMNDiagram() != null) {
				name = ((BPMNGraph) graph).getBpmnModel().getFirstBPMNDiagram().getName();
			}
			if (name == null || name.length() == 0) {
				name = Resources.get("newDiagram");
			}
			centerPane.setTitleAt(0, name);
		} else {
			name = graph.getCellValue(currentGraphComponent.getName());
			centerPane.setTabTitle(currentGraphComponent.getName(), name);
		}
	}

	public void error(List<ErrorMessage> errorMessages) {
		Window window = SwingUtilities.windowForComponent(this);
		if (window != null && window instanceof Frame) {
			Object[][] data = new Object[errorMessages.size()][3];
			for (int i = 0; i < errorMessages.size(); i++) {
				ErrorMessage err = errorMessages.get(i);
				data[i][0] = err.getType();
				data[i][1] = err.getLineNumber();
				data[i][2] = err.getMessage();
			}
			String[] columnNames = { Resources.get("type"), Resources.get("lineNumber"), Resources.get("description") };

			JTable table = new JTable(data, columnNames);
			JScrollPane scrollPane = new JScrollPane(table);
			table.setPreferredScrollableViewportSize(new Dimension(300, 200));
			table.getColumnModel().getColumn(0).setMinWidth(60);
			table.getColumnModel().getColumn(0).setPreferredWidth(60);
			table.getColumnModel().getColumn(0).setMaxWidth(60);
			table.getColumnModel().getColumn(1).setMinWidth(0);
			table.getColumnModel().getColumn(1).setPreferredWidth(0);
			table.getColumnModel().getColumn(1).setMaxWidth(0);
			table.getColumnModel().getColumn(2).setPreferredWidth(300);

			JDialog dialog = new JDialog((Frame) window);
			dialog.setPreferredSize(new Dimension(500, 150));
			dialog.setTitle(Resources.get("schemaError"));
			dialog.add(scrollPane);
			dialog.pack();
			dialog.setModal(false);
			dialog.setLocationRelativeTo(window);
			dialog.setVisible(true);
		}
	}

	public void configure() {
		super.configure();
		File fdir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_FRAGMENTS_DIR);
		if (!fdir.exists()) {
			try {
				fdir.mkdir();
			} catch (Exception exc) {
			}
		}
		File ddir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_DICTIONARY_DIR);
		if (ddir.exists()) {
			for (File dictFile : ddir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".dict");
				}
			})) {
				availableLocales += dictFile.getName().substring(11, 13) + ",";
			}
			if (availableLocales.endsWith(",")) {
				availableLocales = availableLocales.substring(0, availableLocales.length() - 1);
			}
		} else {
			try {
				ddir.mkdir();
			} catch (Exception exc) {
			}
		}

		if (spellChecker != null) {
			try {
				Class<?> fudClass = Class.forName("com.inet.jortho.FileUserDictionary");
				Constructor<?> c = fudClass.getConstructor(String.class);
				Object fudObject = c.newInstance(ddir.getPath());
				spellChecker.getMethod("setUserDictionaryProvider", fudClass.getInterfaces()[0]).invoke(null, fudObject);
				spellChecker.getMethod("registerDictionaries", URL.class, String.class, String.class, String.class).invoke(null, ddir.toURI().toURL(),
						availableLocales, locale.getLanguage(), ".dict");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Resources.add(EditorConstants.RESOURCES_URI + "language/editor", locale);
		Resources.add(EditorConstants.RESOURCES_URI + "language/element", locale);
		Resources.add(EditorConstants.RESOURCES_URI + "language/warning", locale);

	}

	public void setCurrentFile(File file) {
		currentFile = file;
	}

	public void restart() {
		super.restart();
		Component left = getMainPane().getLeftComponent();
		if (left instanceof JSplitPane && Constants.SETTINGS.getProperty("showOutline", "1").equals("1")) {
			((JSplitPane) left).setDividerLocation(180);
		}
		createFrame(new MenuBar(this)).setVisible(true);
	}

	public static void main(String[] args) {
		if (invalidJRE()) {
			System.exit(1);
			return;
		}

		if (args.length > 1) {
			List<String> argList = Arrays.asList(args);
			if (argList.contains("--export")) {
				String file = "";
				if (args.length == 3) {
					file = args[2];
				}
				BPMNEditorUtils.export(args[0], file);
				System.exit(1);
				return;
			}
		}

		BPMNEditor editor = new BPMNEditor();
		editor.showFrame(true);

		if (args.length == 1) {
			editor.openFile(args[0]);
		}

		if (spellChecker != null) {
			if (availableLocales.length() == 0 && Constants.SETTINGS.getProperty("warningNoDictionary", "1").equals("1")) {
				new WarningDialog("warningNoDictionary").setVisible(true);
			}
		}

	}
}
