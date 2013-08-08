package org.yaoqiang.bpmn.editor.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.TransferHandler;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.CheckBoxMenuItem;
import org.yaoqiang.bpmn.editor.action.EditorActions;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.graph.action.GraphActions;
import org.yaoqiang.util.Resources;

import com.mxgraph.swing.mxGraphComponent;

/**
 * BaseMenuBar
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 4060203894740766714L;

	private static CheckBoxMenuItem warningMenuItem;
	private static CheckBoxMenuItem outlineMenuItem;
	protected static JCheckBoxMenuItem rulersMenuItem;
	protected static JCheckBoxMenuItem gridMenuItem;
	private static CheckBoxMenuItem auxiliaryMenuItem;
	private static CheckBoxMenuItem collaborationMenuItem;

	public MenuBar(BPMNEditor editor) {

		populateFileMenu(editor);

		populateEditMenu(editor);

		populateViewMenu(editor);

		populateModelMenu(editor);

		populateDiagramMenu(editor);
		
		populateHelpMenu(editor);

	}

	protected void populateFileMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(Resources.get("file")));

		menu.add(editor.bind("Import (from file)", ModelActions.getAction(ModelActions.OPEN), "/org/yaoqiang/bpmn/editor/images/open.png"));
		menu.add(editor.bind("Import (from URL)", ModelActions.getAction(ModelActions.OPEN_URL), "/org/yaoqiang/bpmn/editor/images/openurl.png"));

		menu.addSeparator();

		menu.add(editor.bind("export (to file)", ModelActions.getSaveAsAction(), "/org/yaoqiang/bpmn/editor/images/save_as.png"));
		menu.add(editor.bind("export (to PNG)", ModelActions.getSaveAsPNG(), "/org/yaoqiang/bpmn/editor/images/png.png"));
		menu.add(editor.bind("export (to OpenDocument text)", ModelActions.getSaveAsODT(), "/org/yaoqiang/bpmn/editor/images/odt.png"));
		menu.add(editor.bind("export (to BPMN fragment)", ModelActions.getSaveAsFragment(), "/org/yaoqiang/bpmn/editor/images/save_as_fragment.png"));

		menu.addSeparator();

		menu.add(editor.bind(Resources.get("pageSetup"), GraphActions.getAction(GraphActions.PAGE_SETUP), "/org/yaoqiang/bpmn/editor/images/page_setup.png"));
		menu.add(editor.bind(Resources.get("print"), GraphActions.getAction(GraphActions.PRINT), "/org/yaoqiang/bpmn/editor/images/printer.png"));
	}

	protected void populateEditMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(Resources.get("edit")));

		menu.add(editor.bind(Resources.get("undo"), EditorActions.getAction(EditorActions.UNDO), "/org/yaoqiang/bpmn/editor/images/undo.png"));
		menu.add(editor.bind(Resources.get("redo"), EditorActions.getAction(EditorActions.REDO), "/org/yaoqiang/bpmn/editor/images/redo.png"));

		menu.addSeparator();

		menu.add(editor.bind(Resources.get("cut"), TransferHandler.getCutAction(), "/org/yaoqiang/bpmn/editor/images/cut.png"));
		menu.add(editor.bind(Resources.get("copy"), TransferHandler.getCopyAction(), "/org/yaoqiang/bpmn/editor/images/copy.png"));
		menu.add(editor.bind(Resources.get("paste"), TransferHandler.getPasteAction(), "/org/yaoqiang/bpmn/editor/images/paste.png"));

		menu.addSeparator();

		menu.add(editor.bind(Resources.get("delete"), GraphActions.getAction(GraphActions.DELETE), "/org/yaoqiang/bpmn/editor/images/delete.png"));

		menu.addSeparator();

		JMenu addmenu = (JMenu) menu.add(new JMenu(Resources.get("addPage")));
		addmenu.add(editor.bind(Resources.get("dirHorizontal"), GraphActions.getAddPageAction(true)));
		addmenu.add(editor.bind(Resources.get("dirVertical"), GraphActions.getAddPageAction(false)));
		JMenu removemenu = (JMenu) menu.add(new JMenu(Resources.get("removePage")));
		removemenu.add(editor.bind(Resources.get("dirHorizontal"), GraphActions.getRemovePageAction(true)));
		removemenu.add(editor.bind(Resources.get("dirVertical"), GraphActions.getRemovePageAction(false)));

		menu.addSeparator();

		menu.add(editor.bind(Resources.get("selectAll"), GraphActions.getAction(GraphActions.SELECT_ALL), "/org/yaoqiang/bpmn/editor/images/select_all.png"));
		menu.add(editor.bind(Resources.get("selectNone"), GraphActions.getAction(GraphActions.SELECT_NONE), "/org/yaoqiang/bpmn/editor/images/select_none.png"));
	}

	protected void populateViewMenu(BPMNEditor editor) {
		JMenu menu = add(new JMenu(Resources.get("view")));
		warningMenuItem = new CheckBoxMenuItem(editor, Resources.get("warning"), "showWarning");
		menu.add(warningMenuItem);
		outlineMenuItem = new CheckBoxMenuItem(editor, Resources.get("outline"), "showOutline");
		menu.add(outlineMenuItem);
		setRulerMenuItem(editor);
		menu.add(rulersMenuItem);
		setGridMenuItem(editor);
		menu.add(gridMenuItem);

		if (editor.getCollaborationPane() != null) {
			collaborationMenuItem = new CheckBoxMenuItem(editor, Resources.get("collaboration"), "enableCollaboration");
			menu.add(collaborationMenuItem);
		}

		auxiliaryMenuItem = new CheckBoxMenuItem(editor, Resources.get("auxiliary"), "showAuxiliary");
		menu.add(auxiliaryMenuItem);

		menu.addSeparator();

		populateGridMenu(editor, menu);

		menu.add(editor.bind(Resources.get("backgroundColor"), GraphActions.getAction(GraphActions.BACKGROUND),
				"/org/yaoqiang/bpmn/editor/images/fillcolor.gif"));

		menu.addSeparator();

		menu.add(editor.bind(Resources.get("autolayout"), GraphActions.getAutoLayoutAction(), "/org/yaoqiang/bpmn/editor/images/auto_layout.png"));
		menu.add(editor.bind(Resources.get("rotateSwimlane"), GraphActions.getAction(GraphActions.ROTATE_SWIMLANE),
				"/org/yaoqiang/bpmn/editor/images/rotate_swimlane.png"));

		menu.addSeparator();

		JMenu submenu = (JMenu) menu.add(new JMenu(Resources.get("zoom")));
		submenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/zoom.png")));

		submenu.add(editor.bind("400%", GraphActions.getScaleAction(4)));
		submenu.add(editor.bind("200%", GraphActions.getScaleAction(2)));
		submenu.add(editor.bind("150%", GraphActions.getScaleAction(1.5)));
		submenu.add(editor.bind("100%", GraphActions.getScaleAction(1)));
		submenu.add(editor.bind("75%", GraphActions.getScaleAction(0.75)));
		submenu.add(editor.bind("50%", GraphActions.getScaleAction(0.5)));

		submenu.addSeparator();

		submenu.add(editor.bind(Resources.get("page"), GraphActions.getAction(GraphActions.ZOOM_FIT_PAGE)));
		submenu.add(editor.bind(Resources.get("width"), GraphActions.getAction(GraphActions.ZOOM_FIT_WIDTH)));
		submenu.add(editor.bind(Resources.get("custom"), GraphActions.getAction(GraphActions.ZOOM_CUSTOM)));

		menu.addSeparator();

		menu.add(editor.bind(Resources.get("zoomIn"), GraphActions.getAction(GraphActions.ZOOM_IN), "/org/yaoqiang/bpmn/editor/images/zoom_in.png"));
		menu.add(editor.bind(Resources.get("actualSize"), GraphActions.getAction(GraphActions.ZOOM_ACTUAL), "/org/yaoqiang/bpmn/editor/images/zoomactual.png"));
		menu.add(editor.bind(Resources.get("zoomOut"), GraphActions.getAction(GraphActions.ZOOM_OUT), "/org/yaoqiang/bpmn/editor/images/zoom_out.png"));
	}

	protected void populateGridMenu(BaseEditor editor, JMenu menu) {
		JMenu submenu = (JMenu) menu.add(new JMenu(Resources.get("gridstyle")));
		submenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/grid.png")));

		submenu.add(editor.bind(Resources.get("gridSize"), EditorActions.getAction(EditorActions.GRID_SIZE)));
		submenu.add(editor.bind(Resources.get("gridColor"), EditorActions.getAction(EditorActions.GRID_COLOR)));

		submenu.addSeparator();

		submenu.add(editor.bind(Resources.get("dot"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_DOT)));
		submenu.add(editor.bind(Resources.get("cross"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_CROSS)));
		submenu.add(editor.bind(Resources.get("line"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_LINE)));
		submenu.add(editor.bind(Resources.get("dashed"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_DASHED)));

		menu.addSeparator();
	}

	protected void populateModelMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(Resources.get("model")));
		menu.add(editor.bind(Resources.get("definitions"), ModelActions.getAction(ModelActions.DEFINITIONS)));
		menu.add(editor.bind(Resources.get("namespaces"), ModelActions.getAction(ModelActions.NAMESPACES)));
		menu.add(editor.bind(Resources.get("imports"), ModelActions.getAction(ModelActions.IMPORTS)));
		menu.add(editor.bind(Resources.get("itemDefinitions"), ModelActions.getAction(ModelActions.ITEM_DEFINITIONS)));
		menu.add(editor.bind(Resources.get("resources"), ModelActions.getAction(ModelActions.RESOURCES)));
		menu.addSeparator();
		menu.add(editor.bind(Resources.get("globalTasks"), ModelActions.getAction(ModelActions.GLOBAL_TASKS)));
		menu.addSeparator();
		menu.add(editor.bind(Resources.get("messages"), ModelActions.getAction(ModelActions.MESSAGES)));
		menu.add(editor.bind(Resources.get("errors"), ModelActions.getAction(ModelActions.ERRORS)));
		menu.add(editor.bind(Resources.get("signals"), ModelActions.getAction(ModelActions.SIGNALS)));
		menu.add(editor.bind(Resources.get("escalations"), ModelActions.getAction(ModelActions.ESCALATIONS)));
		menu.add(editor.bind(Resources.get("interfaces"), ModelActions.getAction(ModelActions.INTERFACES)));
		menu.add(editor.bind(Resources.get("eventDefinitions"), ModelActions.getAction(ModelActions.DEF_EVENT_DEFINITIONS)));
		menu.add(editor.bind(Resources.get("partners"), ModelActions.getAction(ModelActions.PARTNERS)));
		menu.addSeparator();
		menu.add(editor.bind(Resources.get("dataStores"), ModelActions.getAction(ModelActions.DATASTORES)));
		menu.add(editor.bind(Resources.get("categories"), ModelActions.getAction(ModelActions.CATEGORIES)));

	}

	protected void populateDiagramMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(Resources.get("diagram")));
		((BPMNEditor) editor).setDiagramsMenu(menu);
	}

	protected void populateHelpMenu(final BaseEditor editor) {
		JMenu menu = add(new JMenu(Resources.get("help")));

		JMenuItem item = menu.add(new JMenuItem(Resources.get("aboutBPMNEditor")));
		item.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/help.png")));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.about();
			}
		});
	}

	protected void setRulerMenuItem(BaseEditor editor) {
		rulersMenuItem = new CheckBoxMenuItem((BPMNEditor) editor, Resources.get("rulers"), "showRulers");
	}

	protected void setGridMenuItem(BaseEditor editor) {
		gridMenuItem = new CheckBoxMenuItem((BPMNEditor) editor, Resources.get("grid"), "showGrid");
	}

	public static CheckBoxMenuItem getWarningMenuItem() {
		return warningMenuItem;
	}

	public static CheckBoxMenuItem getOutlineMenuItem() {
		return outlineMenuItem;
	}

	public static JCheckBoxMenuItem getRulersMenuItem() {
		return rulersMenuItem;
	}

	public static JCheckBoxMenuItem getGridMenuItem() {
		return gridMenuItem;
	}

	public static CheckBoxMenuItem getCollaborationMenuItem() {
		return collaborationMenuItem;
	}

	public static CheckBoxMenuItem getAuxiliaryMenuItem() {
		return auxiliaryMenuItem;
	}

}
