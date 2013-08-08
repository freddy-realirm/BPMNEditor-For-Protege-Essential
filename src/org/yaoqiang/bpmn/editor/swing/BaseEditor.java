package org.yaoqiang.bpmn.editor.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.util.EditorConstants;
import org.yaoqiang.bpmn.editor.util.EditorUtils;
import org.yaoqiang.bpmn.model.elements.activities.AdHocSubProcess;
import org.yaoqiang.bpmn.model.elements.activities.BusinessRuleTask;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.ReceiveTask;
import org.yaoqiang.bpmn.model.elements.activities.ScriptTask;
import org.yaoqiang.bpmn.model.elements.activities.SendTask;
import org.yaoqiang.bpmn.model.elements.activities.ServiceTask;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.activities.Task;
import org.yaoqiang.bpmn.model.elements.activities.Transaction;
import org.yaoqiang.bpmn.model.elements.artifacts.Group;
import org.yaoqiang.bpmn.model.elements.artifacts.TextAnnotation;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;
import org.yaoqiang.bpmn.model.elements.conversations.CallConversation;
import org.yaoqiang.bpmn.model.elements.conversations.Conversation;
import org.yaoqiang.bpmn.model.elements.conversations.SubConversation;
import org.yaoqiang.bpmn.model.elements.core.common.Message;
import org.yaoqiang.bpmn.model.elements.data.DataInput;
import org.yaoqiang.bpmn.model.elements.data.DataObject;
import org.yaoqiang.bpmn.model.elements.data.DataOutput;
import org.yaoqiang.bpmn.model.elements.data.DataStoreReference;
import org.yaoqiang.bpmn.model.elements.events.BoundaryEvent;
import org.yaoqiang.bpmn.model.elements.events.EndEvent;
import org.yaoqiang.bpmn.model.elements.events.IntermediateCatchEvent;
import org.yaoqiang.bpmn.model.elements.events.IntermediateThrowEvent;
import org.yaoqiang.bpmn.model.elements.events.StartEvent;
import org.yaoqiang.bpmn.model.elements.gateways.ComplexGateway;
import org.yaoqiang.bpmn.model.elements.gateways.EventBasedGateway;
import org.yaoqiang.bpmn.model.elements.gateways.ExclusiveGateway;
import org.yaoqiang.bpmn.model.elements.gateways.InclusiveGateway;
import org.yaoqiang.bpmn.model.elements.gateways.ParallelGateway;
import org.yaoqiang.bpmn.model.elements.humaninteraction.ManualTask;
import org.yaoqiang.bpmn.model.elements.humaninteraction.UserTask;
import org.yaoqiang.bpmn.model.elements.process.Lane;
import org.yaoqiang.dialog.BaseDialog;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.view.Graph;
import org.yaoqiang.graph.view.GraphManager;
import org.yaoqiang.graph.view.SwimlaneManager;
import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;
import org.yaoqiang.util.Utils;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUtils;

/**
 * BaseEditor
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BaseEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	public static Locale locale = Locale.getDefault();

	protected static File currentFile;

	protected boolean modified = false;

	public static Connection dbConn = null;

	protected String appTitle;

	protected JFrame frame;

	protected BaseDialog dialog;

	protected JSplitPane mainPane;

	protected TabbedPane centerPane;

	protected JPanel collaborationPane;

	protected Map<String, GraphComponent> graphComponents = new HashMap<String, GraphComponent>();

	protected GraphComponent graphComponent;

	protected GraphComponent currentGraphComponent;

	protected Map<String, mxGraphOutline> graphOverviews = new HashMap<String, mxGraphOutline>();

	protected mxGraphOutline graphOverview;

	protected JDialog graphOverviewWindow;

	protected Palettes elementsPalette;

	protected JTabbedPane palettesPane;

	protected JWindow openSplash;

	protected JLabel statusBar;

	protected static Graph graph;

	protected mxUndoManager undoManager;

	protected mxIEventListener eventListener = new mxIEventListener() {
		public void invoke(Object sender, mxEventObject evt) {
			graph.clearSelection();
		}
	};

	protected MouseWheelListener wheelTracker = new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			mxGraphModel model = currentGraphComponent.getGraph().getModel();
			if (model.getChildCount(model.getRoot()) > 0 && (e.getSource() instanceof mxGraphOutline || e.isControlDown())) {
				BaseEditor.this.mouseWheelMoved(e);
			}
		}

	};

	protected mxIEventListener groupHandler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
			String eventName = evt.getName();
			mxCell cell = (mxCell) graph.getSelectionCell();
			if (cell == null) {
				cell = (mxCell) evt.getProperty("cell");
			}
			if (eventName.equals(Constants.ENTER_GROUP)) {
				String name = BaseEditor.this.getDiagramName();
				if (graph.getModel().isSubProcess(cell)) {
					name += " -> SubProcess: " + cell.getValue();
					BaseEditor.this.setDiagramName(name);
					currentGraphComponent.setLastViewRoot(cell);
				}
			} else if (eventName.equals(Constants.EXIT_GROUP)) {
				String name = BaseEditor.this.getDiagramName();
				if (graph.getModel().isSubProcess(cell)) {
					int index = name.lastIndexOf(" -> SubProcess: ");
					if (index > 0) {
						name = name.substring(0, index);
					}
				}

				mxCell root = (mxCell) graph.getCurrentRoot();
				Object rootCell = graph.getModel().getCell(currentGraphComponent.getName());
				if (root != rootCell && (root == null || graph.getModel().isAncestor(root, rootCell))) {
					graph.getView().setCurrentRoot(rootCell);
					currentGraphComponent.setLastViewRoot(rootCell);
				} else {
					currentGraphComponent.setLastViewRoot(root);
				}

				if (root == null || graph.getModel().isAncestor(root, rootCell)) {
					BaseEditor.this.resetDiagramName();
					return;
				} else if (graph.getModel().isSubProcess(root)) {
					int index = name.lastIndexOf(" -> SubProcess: ");
					if (index == -1 || !name.substring(index + 16).equals(root.getValue())) {
						name += " -> SubProcess: " + root.getValue();
					}
				}
				BaseEditor.this.setDiagramName(name);
			}
		}
	};

	protected void initEditor() {
		setName(getClass().getSimpleName());
		configure();
		initGraphComponent();
		initGraph();
		initGraphOverview();

		dialog = new BaseDialog();

		undoManager = new mxUndoManager(graph);

		GraphManager graphManager = new GraphManager(currentGraphComponent);
		undoManager.addListener(mxEvent.UNDO, graphManager.getHandler());
		undoManager.addListener(mxEvent.REDO, graphManager.getHandler());

		SwimlaneManager swimlaneManager = new SwimlaneManager(currentGraphComponent);
		undoManager.addListener(mxEvent.UNDO, swimlaneManager.getHandler());
		undoManager.addListener(mxEvent.REDO, swimlaneManager.getHandler());

		palettesPane = new JTabbedPane();
		initMainPalette();
		initArtifactPalette();
	}

	protected void createMainPane(JToolBar toolBar, JComponent leftPane, JComponent rightPane) {
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		int cols = Integer.parseInt(Constants.SETTINGS.getProperty("paletteCols", "4"));
		if (cols == 1) {
			mainPane.setDividerLocation(80);
		} else if (cols == 2) {
			mainPane.setDividerLocation(140);
		} else if (cols == 3) {
			mainPane.setDividerLocation(cols * 65);
		} else if (cols == 4) {
			mainPane.setDividerLocation(cols * 62);
		} else {
			mainPane.setDividerLocation(cols * 60);
		}
		mainPane.setDividerSize(6);
		mainPane.setBorder(null);

		setLayout(new BorderLayout());
		add(mainPane, BorderLayout.CENTER);
		add(toolBar, BorderLayout.NORTH);

		statusBar = new JLabel(Resources.get("ready"));
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		if (Constants.SETTINGS.getProperty("showStatusBar", "1").equals("1")) {
			add(statusBar, BorderLayout.SOUTH);
		}
	}

	protected void initGraphComponent() {
		graphComponent = createGraphComponent(getClass().getSimpleName());
		currentGraphComponent = graphComponent;
		installGraphComponentHandlers(currentGraphComponent);
		installGraphComponentListeners(currentGraphComponent);
	}

	protected GraphComponent createGraphComponent(String name) {
		GraphComponent graphComponent = null;
		if (graph != null) {
			graphComponent = new GraphComponent(graph);
		} else {
			graphComponent = new GraphComponent(new Graph(new GraphModel(Constants.VERSION)));
		}
		if (name != null) {
			graphComponent.setName(name);
		}
		return graphComponent;
	}

	protected void initGraph() {
		graph = graphComponent.getGraph();
		graph.addListener(Constants.ENTER_GROUP, groupHandler);
		graph.addListener(Constants.EXIT_GROUP, groupHandler);
		graph.getModel().addListener(mxEvent.CHANGE, new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				setModified(true);
			}
		});
	}

	protected void initGraphOverview() {
		graphOverview = new mxGraphOutline(currentGraphComponent);
		graphOverview.setTripleBuffered(true);
		installGraphOverviewListeners(graphOverview);
	}

	public void insertGraphComponent(mxCell cell) {
		GraphComponent com = graphComponents.get(cell.getId());
		if (com == null) {
			com = createGraphComponent(cell.getId());
			graphComponents.put(cell.getId(), com);
			com.setLastViewRoot(cell);
		}
		currentGraphComponent = com;
		graph.getView().setCurrentRoot(com.getLastViewRoot());
		graph.clearSelection();

		centerPane.add(centerPane.getTabTitle(cell.getId(), (String) cell.getValue()), com);
		centerPane.setSelectedComponent(com);

		installGraphComponentHandlers(currentGraphComponent);
		installGraphComponentListeners(currentGraphComponent);

		refreshGraphOverview();
	}

	public void removeGraphComponent(String name) {
		centerPane.remove(graphComponents.get(name));
		centerPane.setSelectedComponent(graphComponent);
		graph.clearSelection();
		graphOverviews.remove(name);
		refreshGraphOverview();
	}

	public void closeGraphComponents() {
		for (Map.Entry<String, GraphComponent> entry : graphComponents.entrySet()) {
			centerPane.remove(entry.getValue());
		}
		graphComponents.clear();
		graphOverviews.clear();
		graphComponent.setLastViewRoot(null);
		centerPane.setSelectedComponent(graphComponent);
		graph.getView().setCurrentRoot(null);
		graph.clearSelection();
	}

	public void refreshGraphOverview() {
		mxGraphOutline oldGraphOverview = graphOverview;
		graphOverview = graphOverviews.get(currentGraphComponent.getName());
		if (graphOverview == null) {
			initGraphOverview();
			graphOverviews.put(graphOverview.getName(), graphOverview);
		}

		Component left = mainPane.getLeftComponent();
		if (left instanceof JSplitPane) {
			int loc = ((JSplitPane) left).getDividerLocation();
			((JSplitPane) left).setLeftComponent(graphOverview);
			((JSplitPane) left).setDividerLocation(loc);
		} else if (getGraphOverviewWindow() != null) {
			getGraphOverviewWindow().remove(oldGraphOverview);
			getGraphOverviewWindow().add(graphOverview);
			getGraphOverviewWindow().validate();
			currentGraphComponent.zoomAndCenter();
		}
	}

	protected void installGraphComponentHandlers(GraphComponent graphComponent) {
		new mxRubberband(graphComponent);
	}

	protected void installGraphComponentListeners(final GraphComponent graphComponent) {
		graphComponent.addMouseWheelListener(wheelTracker);

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mouseReleased(e);
			}

			public void mouseReleased(MouseEvent e) {
				mxGraphModel model = graphComponent.getGraph().getModel();
				if (e.isPopupTrigger() && model.getChildCount(model.getRoot()) > 0) {
					showGraphPopupMenu(e, graphComponent);
				}
			}
		});

		graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				mouseLocationChanged(e, graphComponent);
			}

			public void mouseMoved(MouseEvent e) {
				mouseDragged(e);
			}
		});

		graphComponent.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '-') {
					graphComponent.zoomOut();
				} else if (e.getKeyChar() == '=' || e.getKeyChar() == '+') {
					graphComponent.zoomIn();
				} else if (e.getKeyChar() == '0') {
					graphComponent.setZoomPolicy(GraphComponent.ZOOM_POLICY_PAGE);
				} else if (e.getKeyChar() == '1') {
					graphComponent.zoomTo(1, graphComponent.isCenterZoom());
				} else if (e.getKeyChar() == '2') {
					graphComponent.zoomTo(2, graphComponent.isCenterZoom());
				} else if (e.getKeyChar() == '3') {
					graphComponent.zoomTo(3, graphComponent.isCenterZoom());
				} else if (e.getKeyChar() == '4') {
					graphComponent.zoomTo(4, graphComponent.isCenterZoom());
				} else if (e.getKeyChar() == '5') {
					graphComponent.zoomTo(0.5, graphComponent.isCenterZoom());
				}
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}
		});
	}

	protected void installGraphOverviewListeners(mxGraphOutline graphOverview) {
		graphOverview.addMouseWheelListener(wheelTracker);

		graphOverview.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getClickCount() == 2) {
					currentGraphComponent.setZoomPolicy(GraphComponent.ZOOM_POLICY_PAGE);
				}
			}

			public void mousePressed(MouseEvent e) {
				mouseReleased(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showOutlinePopupMenu(e);
				}
			}
		});
	}

	protected void initMainPalette() {
		elementsPalette = insertPalette(Resources.get("elements"));
		elementsPalette.addListener(mxEvent.SELECT, eventListener);

		elementsPalette.addTemplate("pool", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/pool.png")), "pool", 990,
				Integer.parseInt(Constants.SETTINGS.getProperty("style_pool_height", "200")), new Participant(Resources.get("participant")));
		elementsPalette.addTemplate("lane", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/lane.png")), "lane", 990,
				Integer.parseInt(Constants.SETTINGS.getProperty("style_pool_height", "200")), new Lane(Resources.get("lane")));
		elementsPalette.addTemplate("verticalPool", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/pool_vertical.png")),
				"verticalPool", Integer.parseInt(Constants.SETTINGS.getProperty("style_swimlane_height", "200")), 700, new Participant(Resources.get("pool")));
		elementsPalette.addTemplate("verticalLane", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/lane_vertical.png")),
				"verticalLane", Integer.parseInt(Constants.SETTINGS.getProperty("style_swimlane_height", "200")), 700, new Lane(Resources.get("lane")));
		elementsPalette.addEventTemplate("startEvent",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/start_event.png")), "startEvent", new StartEvent(
						Resources.get("startEvent")));
		elementsPalette.addEventTemplate("startNonInterruptingEvent",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/start_event_non_interrupting.png")), "startEvent",
				new StartEvent(Resources.get("startEvent"), false));
		elementsPalette.addEventTemplate("endEvent", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/end_event.png")),
				"endEvent", new EndEvent(Resources.get("endEvent")));
		elementsPalette.addTemplate("textAnnotation", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/annotation.png")),
				"annotation", new TextAnnotation(Resources.get("textAnnotation")));
		elementsPalette.addEventTemplate("boundaryEvent",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/intermediate_event.png")), "intermediateEvent",
				new BoundaryEvent(Resources.get("boundaryEvent")));
		elementsPalette.addEventTemplate("boundaryNonInterruptingEvent",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/intermediate_event_non_interrupting.png")),
				"intermediateEvent", new BoundaryEvent(Resources.get("boundaryEvent"), false));
		elementsPalette.addEventTemplate("intermediateThrowEvent",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/intermediate_event.png")), "intermediateEvent",
				new IntermediateThrowEvent(Resources.get("intermediateThrowEvent")));
		elementsPalette.addEventTemplate("intermediateCatchEvent",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/intermediate_event.png")), "intermediateEvent",
				new IntermediateCatchEvent(Resources.get("intermediateCatchEvent")));
		elementsPalette.addTaskTemplate("task", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_no.png")), new Task(
				Resources.get("task")));
		elementsPalette.addTaskTemplate("sendTask", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_send.png")),
				new SendTask(Resources.get("sendTask")));
		elementsPalette.addTaskTemplate("receiveTask",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_receive.png")),
				new ReceiveTask(Resources.get("receiveTask")));
		elementsPalette.addTaskTemplate("serviceTask",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_service.png")),
				new ServiceTask(Resources.get("serviceTask")));
		elementsPalette.addTaskTemplate("userTask", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_user.png")),
				new UserTask(Resources.get("userTask")));
		elementsPalette.addTaskTemplate("manualTask", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_manual.png")),
				new ManualTask(Resources.get("manualTask")));
		elementsPalette.addTaskTemplate("scriptTask", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_script.png")),
				new ScriptTask(Resources.get("scriptTask")));
		elementsPalette.addTaskTemplate("businessRuleTask",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/task_business_rule.png")),
				new BusinessRuleTask(Resources.get("businessRuleTask")));
		elementsPalette.addGatewayTemplate("exclusiveGateway",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway.png")),
				new ExclusiveGateway(Resources.get("exclusiveGateway")));
		elementsPalette.addGatewayTemplate("exclusiveGatewayWithIndicator",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway_exclusive.png")),
				new ExclusiveGateway(Resources.get("exclusiveGateway")));
		elementsPalette.addGatewayTemplate("parallelGateway",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway_parallel.png")),
				new ParallelGateway(Resources.get("parallelGateway")));
		elementsPalette.addGatewayTemplate("inclusiveGateway",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway_inclusive.png")),
				new InclusiveGateway(Resources.get("inclusiveGateway")));
		elementsPalette.addGatewayTemplate("complexGateway",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway_complex.png")),
				new ComplexGateway(Resources.get("complexGateway")));
		elementsPalette.addGatewayTemplate("eventGateway",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway_event.png")),
				new EventBasedGateway(Resources.get("eventGateway")));
		elementsPalette.addGatewayTemplate("eventGatewayInstantiate",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway_event_instantiate.png")), new EventBasedGateway(
						Resources.get("eventGatewayInstantiate"), true));
		elementsPalette.addGatewayTemplate("parallelEventGateway",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/gateway_event_parallel_instantiate.png")),
				new EventBasedGateway(Resources.get("parallelEventGateway"), "Parallel", true));
		elementsPalette.addSubprocessTemplate("subprocess",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/subprocess.png")),
				new SubProcess(Resources.get("subprocess")));
		elementsPalette.addSubprocessTemplate("eventSubProcess",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/subprocess_event.png")),
				new SubProcess(Resources.get("eventSubProcess"), true));
		elementsPalette.addSubprocessTemplate("tranSubProcess",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/subprocess_transaction.png")),
				new Transaction(Resources.get("tranSubProcess")));
		elementsPalette.addSubprocessTemplate("adHocSubProcess",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/subprocess_adhoc.png")),
				new AdHocSubProcess(Resources.get("adHocSubProcess")));
		elementsPalette.addCallActivityTemplate("callActivity",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_activity_no.png")),
				new CallActivity(Resources.get("CallActivity"), "globalTask"));
		elementsPalette.addCallActivityTemplate("callUser",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_activity_user.png")),
				new CallActivity(Resources.get("CallActivity"), "globalUserTask"));
		elementsPalette.addCallActivityTemplate("callManual",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_activity_manual.png")),
				new CallActivity(Resources.get("CallActivity"), "globalManualTask"));
		elementsPalette.addCallActivityTemplate("callScript",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_activity_script.png")),
				new CallActivity(Resources.get("CallActivity"), "globalScriptTask"));
		elementsPalette.addCallActivityTemplate("callBusinessRule",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_activity_business_rule.png")), new CallActivity(
						Resources.get("CallActivity"), "globalBusinessRuleTask"));
		elementsPalette.addCallActivityTemplate("callProcess",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_activity_subprocess.png")), new CallActivity(
						Resources.get("CallActivity"), "callProcess"));
		elementsPalette.addDataObjectTemplate("dataObject",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/dataobject.png")),
				new DataObject(Resources.get("dataObject"), false));
		elementsPalette.addDataObjectTemplate("dataObjects",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/dataobjects.png")),
				new DataObject(Resources.get("dataObject"), true));
		elementsPalette.addDataObjectTemplate("dataInput",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/datainput.png")),
				new DataInput(Resources.get("dataInput"), false));
		elementsPalette.addDataObjectTemplate("dataOutput",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/dataoutput.png")),
				new DataOutput(Resources.get("dataOutput"), false));
		elementsPalette.addDataObjectTemplate("dataInputs",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/datainputs.png")),
				new DataInput(Resources.get("dataInputs"), true));
		elementsPalette.addDataObjectTemplate("dataOutputs",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/dataoutputs.png")),
				new DataOutput(Resources.get("dataOutputs"), true));
		elementsPalette.addDataStoreTemplate("dataStore",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/datastore.png")), new DataStoreReference());
		elementsPalette.addTemplate("group", new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/group.png")), "group", 400,
				250, new Group());
		elementsPalette.addMessageTemplate("initiatingMessage",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/initiating_message.png")), "initiatingMessage",
				new Message());
		elementsPalette.addMessageTemplate("nonInitiatingMessage",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/non_initiating_message.png")), "nonInitiatingMessage",
				new Message());
		elementsPalette.addChoreographyTemplate("choreographyTask",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/choreography_task.png")), "choreography");
		elementsPalette.addChoreographyTemplate("subChoreography",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/choreography_subprocess.png")), "subChoreography");
		elementsPalette.addChoreographyTemplate("callChoreographyTask",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_choreography_task.png")), "callChoreographyTask");
		elementsPalette.addChoreographyTemplate("callChoreography",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_choreography.png")), "callChoreography");
		elementsPalette
				.addConversationTemplate("conversation",
						new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/conversation.png")), "conversation",
						new Conversation(""));
		elementsPalette.addConversationTemplate("subConversation",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/sub_conversation.png")), "subConversation",
				new SubConversation(""));
		elementsPalette.addConversationTemplate("callConversation",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_conversation.png")), "callConversation",
				new CallConversation(""));
		elementsPalette.addConversationTemplate("callCollaboration",
				new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/palettes/call_collaboration.png")), "callCollaboration",
				new CallConversation(""));
	}

	protected void initArtifactPalette() {
		File artifactsDir = new File(Constants.YAOQIANG_USER_HOME + File.separator + EditorConstants.YAOQIANG_ARTIFACTS_DIR);

		for (File dir : artifactsDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		})) {
			Palettes palette = insertPalette(dir.getName());

			for (File f : dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".jpg")
							|| name.toLowerCase().endsWith(".gif");
				}
			})) {
				EditorUtils.addArtifact(palette, f.getName(), f.getParent() + File.separator);
			}
		}
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		boolean oldValue = this.modified;
		this.modified = modified;

		firePropertyChange("modified", oldValue, modified);

		if (oldValue != modified) {
			updateTitle();
		}
	}

	public mxUndoManager getUndoManager() {
		return undoManager;
	}

	public void setCurrentFile(File file) {
		currentFile = file;
		updateTitle();
	}

	public static File getCurrentFile() {
		return currentFile;
	}

	public JFrame getFrame() {
		return frame;
	}

	public BaseDialog getDialog() {
		return dialog;
	}

	public BaseDialog createDialog() {
		return new BaseDialog();
	}

	public JSplitPane getMainPane() {
		return mainPane;
	}

	public TabbedPane getCenterPane() {
		return centerPane;
	}

	public JTabbedPane getPalettesPane() {
		return palettesPane;
	}

	public JLabel getStatusBar() {
		return statusBar;
	}

	public Map<String, GraphComponent> getGraphComponents() {
		return graphComponents;
	}

	public GraphComponent getGraphComponent(String name) {
		return graphComponents.get(name);
	}

	public GraphComponent getGraphComponent() {
		return graphComponent;
	}

	public JDialog getGraphOverviewWindow() {
		return graphOverviewWindow;
	}

	public mxGraphOutline getGraphOverviewComponent() {
		return graphOverview;
	}

	public GraphComponent getCurrentGraphComponent() {
		return currentGraphComponent;
	}

	public void setCurrentGraphComponent(GraphComponent currentGraphComponent) {
		this.currentGraphComponent = currentGraphComponent;
	}

	protected void showOutlinePopupMenu(MouseEvent e) {
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), currentGraphComponent);

		JCheckBoxMenuItem item = new JCheckBoxMenuItem(Resources.get("showLabels"));
		item.setSelected(graphOverview.isDrawLabels());

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphOverview.setDrawLabels(!graphOverview.isDrawLabels());
				graphOverview.repaint();
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(item);
		menu.show(currentGraphComponent, pt.x, pt.y);

		e.consume();
	}

	protected void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			currentGraphComponent.zoomIn();
		} else {
			currentGraphComponent.zoomOut();
		}

		status(Resources.get("scale") + ": " + (int) (100 * currentGraphComponent.getGraph().getView().getScale()) + "%");
	}

	protected void showGraphPopupMenu(MouseEvent e, GraphComponent graphComponent) {
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
		JPopupMenu menu = createGraphPopupMenu(graphComponent, e);
		graphComponent.setPopupPoint(e.getPoint());
		menu.show(graphComponent, pt.x, pt.y);
		e.consume();
	}

	protected JPopupMenu createGraphPopupMenu(GraphComponent graphComponent, MouseEvent e) {
		JPopupMenu menu = new PopupMenu(this, graphComponent, e);
		return menu;
	}

	protected void mouseLocationChanged(MouseEvent e, GraphComponent graphComponent) {
		mxPoint translate = graphComponent.getGraph().getView().getTranslate();
		double scale = graph.getView().getScale();
		status(Resources.get("scale") + ": " + (int) (100 * graphComponent.getGraph().getView().getScale()) + "%" + ", " + Resources.get("location") + ": x="
				+ (e.getX() / scale - translate.getX()) + ", y=" + (e.getY() / scale - translate.getY()));
	}

	public void status(String msg) {
		statusBar.setText(msg);
	}

	public void updateTitle() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window != null && window instanceof JFrame) {
			String title = (currentFile != null) ? currentFile.getAbsolutePath() : Resources.get("newDiagram");
			if (modified) {
				title += "*";
			}
			((JFrame) window).setTitle(title + " - " + appTitle);
		}
	}

	public void progress(boolean start) {
		Window window = SwingUtilities.windowForComponent(this);
		if (Constants.OS.startsWith("Windows")) {
			if (start) {
				openSplash = new JWindow(window);
				openSplash.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				currentGraphComponent.getGraphControl().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				JPanel panel = new JPanel();
				ImageIcon imageIcon = new ImageIcon(BaseEditor.class.getResource("/org/yaoqiang/bpmn/editor/images/loading.gif"));
				panel.add(new JLabel(Resources.get("opening"), imageIcon, JLabel.LEFT));
				panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
				openSplash.add(panel);
				openSplash.pack();
				openSplash.setLocationRelativeTo(window);
				openSplash.setVisible(true);
			} else {
				currentGraphComponent.getGraphControl().setCursor(null);
				if (openSplash != null) {
					openSplash.setCursor(null);
					openSplash.dispose();
					openSplash = null;
				}
			}
		}
	}

	public Palettes insertPalette(String title) {
		final Palettes palette = new Palettes(title);
		final JScrollPane scrollPane = new JScrollPane(palette);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		palettesPane.add(title, scrollPane);

		palettesPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int w = scrollPane.getWidth() - scrollPane.getVerticalScrollBar().getWidth();
				palette.setPreferredWidth(w);
			}

		});

		return palette;
	}

	public void createGraphOverviewWindow(Frame window, mxGraphOutline graphOutline) {
		if (Constants.SETTINGS.getProperty("showFloatingOutline", Constants.OS.startsWith("Windows") ? "1" : "0").equals("1")) {
			graphOverviewWindow = new JDialog(window, Resources.get("Outline"));
			graphOverviewWindow.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					MenuBar.getOutlineMenuItem().setSelected(false);
				}
			});
			graphOverviewWindow.setModal(false);
			graphOverviewWindow.add(graphOutline);

			int x = window.getX();
			int y = window.getY() + (3 * window.getHeight() / 4);
			graphOverviewWindow.setLocation(x, y);
			graphOverviewWindow.setSize(250, 220);

			graphOverviewWindow.setVisible(Constants.SETTINGS.getProperty("showOutline", "1").equals("1"));
		}
	}

	public void removeGraphOverviewWindow() {
		graphOverviewWindow.dispose();
		graphOverviewWindow = null;
	}

	public final class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			int o = JOptionPane.NO_OPTION;
			if (isModified()) {
				o = JOptionPane.showConfirmDialog(null, Resources.get("saveChanges"), Resources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					if (isModified()) {
						ModelActions.getSaveAction().actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), null));
					}
				}
			}
			if (!isModified() || o == JOptionPane.NO_OPTION) {
				System.exit(0);
			}
		}
	}

	public void installWindowListener() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window != null) {
			window.addWindowListener(new BaseEditor.WindowCloser());
		}
	}

	public JFrame createFrame(JMenuBar menuBar) {
		JFrame frame = new JFrame();
		frame.setIconImage(Constants.LOGO_ICON.getImage());
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int xMinus = 20, yMinus = 60;
		frame.setBounds(xMinus / 2, yMinus / 2, screenSize.width - xMinus, screenSize.height - yMinus);
		installWindowListener();

		createGraphOverviewWindow(frame, graphOverview);

		updateTitle();

		return frame;
	}

	public void showFrame(boolean visible) {
		frame.setVisible(visible);
		currentGraphComponent.requestFocusInWindow();
	}

	public void setDiagramName(String name) {
		centerPane.setTabTitle(currentGraphComponent.getName(), name);
	}

	public String getDiagramName() {
		return centerPane.getTabTitle(currentGraphComponent.getName(), "");
	}

	public void resetDiagramName() {
		String name = null;
		if ("GraphEditor".equals(currentGraphComponent.getName())) {
			name = Resources.get("newDiagram");
			if (currentFile != null) {
				name = currentFile.getName();
			}
			centerPane.setTitleAt(0, name);
		} else {
			name = graph.getCellValue(currentGraphComponent.getName());
			centerPane.setTabTitle(currentGraphComponent.getName(), name);
		}
	}

	public void setLookAndFeel(String clazz) {
		Window window = SwingUtilities.windowForComponent(this);
		if (window != null) {
			try {
				UIManager.setLookAndFeel(clazz);
				Utils.saveToConfigureFile("Theme", clazz);
				SwingUtilities.updateComponentTreeUI(window);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public void about() {
		Window window = SwingUtilities.windowForComponent(this);

		if (window != null && window instanceof Frame) {
			AboutDialog about = new AboutDialog((Frame) window);
			about.setModal(true);

			// Centers inside the application frame
			about.setLocationRelativeTo(window);

			// Shows the modal dialog and waits
			about.setVisible(true);
		}
	}

	public static boolean invalidJRE() {
		boolean invalid = false;
		int o = JOptionPane.NO_OPTION;
		String jre = System.getProperty("java.runtime.name", "");
		invalid = jre.startsWith("OpenJDK");
		if (invalid) {
			o = JOptionPane
					.showConfirmDialog(
							null,
							"An incompatible Java version has been detected which has been reported to cause strange bugs.\nPlease install the latest release of the Oracle Java SE Runtime Environment (JRE) from:\nhttp://java.oracle.com or if it is already installed, make sure it is used.\nAborting now?",
							"Java Version Incompatible", JOptionPane.YES_NO_OPTION);
		}
		return o == JOptionPane.YES_OPTION;
	}

	public void restart() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			window.dispose();
			configure();
		}
	}

	public void exit() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window != null) {
			window.dispose();
			System.exit(0);
		}
	}

	public void openFile(String filepath) {
		ModelActions.getOpenAction(filepath).actionPerformed(new ActionEvent(graphComponent, 0, ""));
	}

	public Action bind(String name, final Action action) {
		return bind(name, action, null);
	}

	public Action bind(String name, final Action action, String iconUrl) {
		ImageIcon icon = (iconUrl != null) ? new ImageIcon(BaseEditor.class.getResource(iconUrl)) : null;
		if (icon != null) {
			if (icon.getIconWidth() > 18 || icon.getIconHeight() > 18) {
				icon = new ImageIcon(icon.getImage().getScaledInstance(18, 18, 0));
			}
		}
		Action a = new AbstractAction(name, icon) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(getCurrentGraphComponent(), e.getID(), e.getActionCommand()));
			}
		};
		a.setEnabled(action.isEnabled());
		a.putValue(Action.SHORT_DESCRIPTION, name);
		if (name.equals(Resources.get("new"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		} else if (name.equals(Resources.get("openFile"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		} else if (name.equals(Resources.get("save"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		} else if (name.equals(Resources.get("saveAs"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
		} else if (name.equals(Resources.get("undo"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
		} else if (name.equals(Resources.get("redo"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
		} else if (name.equals(Resources.get("selectAll"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
		} else if (name.equals(Resources.get("editLabel"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ENTER"));
		} else if (name.equals(Resources.get("cut"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		} else if (name.equals(Resources.get("copy"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		} else if (name.equals(Resources.get("paste"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		} else if (name.equals(Resources.get("print"))) {
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control P"));
		} else if (name.equals(Resources.get("delete"))) {
			if (Constants.OS.startsWith("Mac OS")) {
				a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("BACK_SPACE"));
			} else {
				a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));
			}
		}
		return a;
	}

	public void configure() {
		File udir = new File(Constants.YAOQIANG_USER_HOME);
		if (!udir.exists()) {
			try {
				udir.mkdir();
			} catch (Exception exc) {
			}
		}

		File adir = new File(Constants.YAOQIANG_USER_HOME + File.separator + EditorConstants.YAOQIANG_ARTIFACTS_DIR);
		if (!adir.exists()) {
			try {
				adir.mkdir();
			} catch (Exception exc) {
			}
		}

		Constants.SETTINGS = Utils.loadProperties(Constants.YAOQIANG_CONF_FILE);

		EditorConstants.LAST_OPEN_DIR = Constants.SETTINGS.getProperty("lastOpenDir", System.getProperty("user.dir"));

		String lastFileColor = Constants.SETTINGS.getProperty("lastFileColor", "");
		List<String> colorList = Arrays.asList(lastFileColor.split(","));
		int cn = 12;
		for (String color : colorList) {
			if (color.length() < 1) {
				continue;
			}
			cn--;
			EditorConstants.LAST_FILLCOLOR.add(mxUtils.parseColor(color));
		}
		int i = 0;
		while (cn > 0) {
			cn--;
			EditorConstants.LAST_FILLCOLOR.add(EditorConstants.COLORS[i++]);
		}
		String locstring = Constants.SETTINGS.getProperty("Locale", "en");
		if (locstring.equals("zh_CN")) {
			locale = new Locale("zh", "CN");
		} else {
			locale = new Locale(locstring);
		}

		try {
			String def_theme = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
			if (locstring.equals("zh_CN") && !Constants.OS.startsWith("Windows")) {
				def_theme = "javax.swing.plaf.metal.MetalLookAndFeel";
			}
			String theme = Constants.SETTINGS.getProperty("Theme", def_theme);
			try {
				UIManager.setLookAndFeel(theme);
			} catch (Exception ex) {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			}
			javax.swing.plaf.FontUIResource f;
			try {
				if (locstring.equals("zh_CN")) {
					f = new javax.swing.plaf.FontUIResource("AR PL UMing CN", Font.TRUETYPE_FONT, 12);
				} else {
					f = new javax.swing.plaf.FontUIResource("Sans Serif", Font.TRUETYPE_FONT, 12);
				}
			} catch (Exception ex) {
				f = new javax.swing.plaf.FontUIResource("Label.font", Font.PLAIN, 12);
			}
			java.util.Enumeration<?> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value instanceof javax.swing.plaf.FontUIResource) {
					UIManager.put(key, f);
				}
			}
		} catch (Exception e1) {
		}

	}

}
