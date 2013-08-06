package org.yaoqiang.bpmn.editor.action;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.BPMNElementDialog;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.swing.DefaultFileFilter;
import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.util.EditorConstants;
import org.yaoqiang.bpmn.editor.util.EditorUtils;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelConstants;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLAttribute;
import org.yaoqiang.bpmn.model.elements.XMLCollection;
import org.yaoqiang.bpmn.model.elements.XMLComplexElement;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;
import org.yaoqiang.bpmn.model.elements.activities.Activity;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.LoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.MultiInstanceLoopCharacteristics;
import org.yaoqiang.bpmn.model.elements.activities.ReceiveTask;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.activities.Task;
import org.yaoqiang.bpmn.model.elements.choreographyactivities.ChoreographyActivity;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElementsContainer;
import org.yaoqiang.bpmn.model.elements.core.common.FlowNode;
import org.yaoqiang.bpmn.model.elements.core.common.SequenceFlow;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.data.DataInput;
import org.yaoqiang.bpmn.model.elements.data.DataObjectReference;
import org.yaoqiang.bpmn.model.elements.data.DataOutput;
import org.yaoqiang.bpmn.model.elements.events.BoundaryEvent;
import org.yaoqiang.bpmn.model.elements.events.CatchEvent;
import org.yaoqiang.bpmn.model.elements.events.EndEvent;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.events.EventDefinition;
import org.yaoqiang.bpmn.model.elements.events.IntermediateCatchEvent;
import org.yaoqiang.bpmn.model.elements.events.IntermediateThrowEvent;
import org.yaoqiang.bpmn.model.elements.events.LinkEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.StartEvent;
import org.yaoqiang.bpmn.model.elements.events.ThrowEvent;
import org.yaoqiang.bpmn.model.elements.gateways.Gateway;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.bpmn.model.elements.process.GlobalTask;
import org.yaoqiang.bpmn.model.elements.process.Lane;
import org.yaoqiang.graph.canvas.SvgCanvas;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.util.GraphUtils;
import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;
import org.yaoqiang.util.Utils;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;

/**
 * ModelActions
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ModelActions extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public static final String NEW = "new";
	public static final String OPEN = "open";
	public static final String OPEN_URL = "openURL";
	public static final String SAVE = "save";
	public static final String RELOAD = "reload";

	public static final String DEFINITIONS = "definitions";
	public static final String RESOURCES = "resources";
	public static final String NAMESPACES = "namespaces";
	public static final String CATEGORIES = "categories";
	public static final String DATASTORES = "dataStores";
	public static final String GLOBAL_TASKS = "globalTasks";
	public static final String IMPORTS = "imports";
	public static final String ITEM_DEFINITIONS = "itemDefinitions";
	public static final String MESSAGES = "messages";
	public static final String ERRORS = "errors";
	public static final String SIGNALS = "signals";
	public static final String ESCALATIONS = "escalations";
	public static final String INTERFACES = "interfaces";
	public static final String PARTNERS = "partners";
	public static final String DEF_EVENT_DEFINITIONS = "defEventDefinitions";

	public static final String CONDITION_EXPRESSION = "conditionExpression";
	public static final String DATA_STATE = "dataState";
	public static final String DOCUMENTATION = "documentation";
	public static final String EVENT_DEFINITION = "eventDefinition";
	public static final String EVENT_DEFINITIONS = "eventDefinitions";
	public static final String LOOP_CHARACTERISTICS = "loopCharacteristics";
	public static final String PROCESS_PROPERTIES = "processProperties";
	public static final String DATA_PROPERTIES = "dataProperties";
	public static final String DATA_OBJECTS = "dataobjects";
	public static final String DATA_INOUT = "dataInOut";
	public static final String RESOURCE_ASSIGNMENT = "resourceAssignment";

	public static final String ELEMENT = "element";
	public static final String DIAGRAM_NAME = "diagramName";
	public static final String OPEN_DIAGRAM = "openDiagram";

	public static final String CHANGE_FLOW_ELEMENT_TYPE = "changeFlowElementType";
	public static final String CHANGE_ACTIVITY_LOOP_TYPE = "changeLoopType";
	public static final String CHANGE_ACTIVITY_COMPENSATION_TYPE = "changeCompensationType";
	public static final String CHANGE_RECEIVE_TASK_INSTANTIATE_TYPE = "changeReceiveTaskInstantiateType";
	public static final String CHANGE_EVENT_TYPE = "changeEventType";
	public static final String CHANGE_DATA_COLLECTION_TYPE = "changeDataCollectionType";
	public static final String CHANGE_DATA_TYPE = "changeDataType";
	public static final String DEFAUT_SF = "defaultSequenceFlow";

	private String type = "";

	private String stringValue = "";

	private boolean booleanValue;

	private Object objectValue;

	public ModelActions(String type) {
		this.type = type;
	}

	public static ModelActions getAction() {
		return new ModelActions(ELEMENT);
	}

	public static ModelActions getSaveAction() {
		return new ModelActions(SAVE).setStringValue("0");
	}

	public static ModelActions getSaveAsAction() {
		return new ModelActions(SAVE).setStringValue("1");
	}

	public static ModelActions getSaveAsFragment() {
		return new ModelActions(SAVE).setStringValue("2");
	}

	public static ModelActions getSaveAsPNG() {
		return new ModelActions(SAVE).setStringValue("3");
	}

	public static ModelActions getSaveAsODT() {
		return new ModelActions(SAVE).setStringValue("4");
	}

	public static ModelActions getOpenAction(String filepath) {
		return new ModelActions(OPEN).setStringValue(filepath);
	}

	public static ModelActions getOpenAction(Object object) {
		return new ModelActions(OPEN).setObjectValue(object);
	}

	public static ModelActions getOpenDiagramAction(String diagramId) {
		return new ModelActions(OPEN_DIAGRAM).setStringValue(diagramId);
	}

	public static ModelActions getChangeFlowElementTypeAction(String type) {
		return new ModelActions(CHANGE_FLOW_ELEMENT_TYPE).setStringValue(type);
	}

	public static ModelActions getChangeActivityLoopTypeAction(String type, boolean isSequential) {
		return new ModelActions(CHANGE_ACTIVITY_LOOP_TYPE).setStringValue(type).setBooleanValue(isSequential);
	}

	public static ModelActions getChangeActivityCompensationTypeAction(boolean isForCompensation) {
		return new ModelActions(CHANGE_ACTIVITY_COMPENSATION_TYPE).setBooleanValue(isForCompensation);
	}

	public static ModelActions getChangeReceiveTaskInstantiateTypeAction(boolean instantiate) {
		return new ModelActions(CHANGE_RECEIVE_TASK_INSTANTIATE_TYPE).setBooleanValue(instantiate);
	}

	public static ModelActions getChangeEventTypeAction(String type) {
		return new ModelActions(CHANGE_EVENT_TYPE).setStringValue(type);
	}

	public static ModelActions getChangeDataCollectionType(boolean isCollection) {
		return new ModelActions(CHANGE_DATA_COLLECTION_TYPE).setBooleanValue(isCollection);
	}

	public static ModelActions getChangeDataType(String type) {
		return new ModelActions(CHANGE_DATA_TYPE).setStringValue(type);
	}

	public static ModelActions getDefaultSequenceFlowAction(String edge) {
		return new ModelActions(DEFAUT_SF).setStringValue(edge);
	}

	public static ModelActions getAction(String type) {
		return new ModelActions(type);
	}

	public void actionPerformed(ActionEvent e) {
		final BPMNEditor editor = getEditor(e);
		BPMNElementDialog dialog = editor.getBpmnElementDialog();
		BPMNGraphComponent graphComponent = editor.getGraphComponent();
		BPMNGraph graph = graphComponent.getGraph();
		GraphModel model = graph.getModel();

		mxCell cell = (mxCell) graph.getSelectionCell();
		if (graph.isChoreography(cell) || graph.isSubChoreography(cell)) {
			cell = GraphUtils.getChoreographyActivity(graph, cell);
		}
		XMLElement el = null;
		if (cell != null && model.getValue(cell) instanceof XMLElement) {
			el = (XMLElement) model.getValue(cell);
		}

		Definitions bpmnModel = graph.getBpmnModel();

		if (NEW.equals(type)) {
			int o = JOptionPane.NO_OPTION;
			if (editor.isModified()) {
				o = JOptionPane.showConfirmDialog(null, Resources.get("saveChanges"), Resources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					getSaveAction().actionPerformed(e);
				}
			}
			if (!editor.isModified() || o == JOptionPane.NO_OPTION) {
				model.clear();
				graph.clearBpmnModel();
				graph.getBpmnModel().setId("_" + System.currentTimeMillis());
				graph.getBpmnModel().setTargetNamespace(BPMNModelConstants.BPMN_TARGET_MODEL_NS + graph.getBpmnModel().getId());
				graph.getBpmnModel().getNamespaces().put(graph.getBpmnModel().getTargetNamespace(), "tns");
				editor.setModified(false);
				editor.setCurrentFile(null);
				editor.closeGraphComponents();
				editor.resetDiagramName();
				BPMNEditorUtils.refreshDiagramList(editor, null);
				graphComponent.getViewport().setOpaque(true);
				graphComponent.getViewport().setBackground(Color.WHITE);
				model.setBackgroundColor(Color.WHITE);
				graphComponent.setVerticalPageCount(Integer.parseInt(Constants.SETTINGS.getProperty("pageNumV", "1")));
				graphComponent.setHorizontalPageCount(Integer.parseInt(Constants.SETTINGS.getProperty("pageNumH", "1")));
				model.setPageCount(graphComponent.getVerticalPageCount());
				model.setHorizontalPageCount(graphComponent.getHorizontalPageCount());
				Constants.SWIMLANE_WIDTH = (int) (model.getPageFormat().getWidth() * 1.25 + (model.getHorizontalPageCount() - 1)
						* (Constants.SWIMLANE_START_POINT + model.getPageFormat().getWidth() * 1.25));

				Constants.SWIMLANE_HEIGHT = (int) (model.getPageFormat().getHeight() * 1.2 + (model.getPageCount() - 1)
						* (Constants.SWIMLANE_START_POINT + model.getPageFormat().getHeight() * 1.2));
				graphComponent.zoomAndCenter();
				editor.getBpmnView().refreshView(graph);
				editor.getUndoManager().clear();
			}
		} else if (SAVE.equals(type)) {
			FileFilter selectedFilter = null;
			FileFilter defaultFilter = new DefaultFileFilter(".bpmn", "OMG BPMN 2.0 " + Resources.get("file") + " (.bpmn)");
			FileFilter odtFileFilter = new DefaultFileFilter(".odt", "OpenDocument Text " + Resources.get("file") + " (.odt)");
			FileFilter svgFileFilter = new DefaultFileFilter(".svg", "SVG " + Resources.get("file") + " (.svg)");
			FileFilter vmlFileFilter = new DefaultFileFilter(".html", "VML " + Resources.get("file") + " (.html)");
			FileFilter htmlFileFilter = new DefaultFileFilter(".html", "HTML " + Resources.get("file") + " (.html)");
			String filename = null;
			boolean dialogShown = false;

			if ("1".equals(stringValue) || "4".equals(stringValue) || !"2".equals(stringValue) && BPMNEditor.getCurrentFile() == null) {
				String wd;

				if (EditorConstants.LAST_OPEN_DIR != null) {
					wd = EditorConstants.LAST_OPEN_DIR;
				} else if (BPMNEditor.getCurrentFile() != null) {
					wd = BPMNEditor.getCurrentFile().getParent();
				} else {
					wd = System.getProperty("user.dir");
				}

				JFileChooser fc = new JFileChooser(wd);
				fc.setAcceptAllFileFilterUsed(false);
				if (BPMNEditor.getCurrentFile() != null) {
					String name = BPMNEditor.getCurrentFile().getName();
					if (name.endsWith(".bpmn20.xml")) {
						name = name.substring(0, name.lastIndexOf(".bpmn20.xml"));
					} else {
						name = name.substring(0, name.lastIndexOf("."));
					}
					fc.setSelectedFile(new File(name));
				}

				if ("3".equals(stringValue)) {
					fc.setFileFilter(new DefaultFileFilter(".png", "PNG " + Resources.get("file") + " (.png)"));
				} else if ("4".equals(stringValue)) {
					fc.setFileFilter(new DefaultFileFilter(".odt", "OpenDocument Text " + Resources.get("file") + " (.odt)"));
				} else {
					fc.addChoosableFileFilter(defaultFilter);
					fc.addChoosableFileFilter(odtFileFilter);
					fc.addChoosableFileFilter(svgFileFilter);
					fc.addChoosableFileFilter(vmlFileFilter);
					fc.addChoosableFileFilter(htmlFileFilter);

					// Adds a filter for each supported image format
					Object[] imageFormats = ImageIO.getReaderFormatNames();
					// Finds all distinct extensions
					HashSet<String> formats = new HashSet<String>();
					for (int i = 0; i < imageFormats.length; i++) {
						String ext = imageFormats[i].toString().toLowerCase();
						formats.add(ext);
					}
					imageFormats = formats.toArray();
					for (int i = 0; i < imageFormats.length; i++) {
						String ext = imageFormats[i].toString();
						fc.addChoosableFileFilter(new DefaultFileFilter("." + ext, ext.toUpperCase() + " " + Resources.get("file") + " (." + ext + ")"));
					}
					fc.setFileFilter(defaultFilter);
				}

				int rc = fc.showDialog(null, "3".equals(stringValue) ? Resources.get("savePNGfile") : Resources.get("save"));
				dialogShown = true;
				if (rc != JFileChooser.APPROVE_OPTION) {
					return;
				} else {
					EditorConstants.LAST_OPEN_DIR = fc.getSelectedFile().getParent();
					Utils.saveToConfigureFile("lastOpenDir", EditorConstants.LAST_OPEN_DIR);
				}
				filename = fc.getSelectedFile().getAbsolutePath();
				selectedFilter = fc.getFileFilter();
				if (selectedFilter instanceof DefaultFileFilter) {
					String ext = ((DefaultFileFilter) selectedFilter).getExtension();
					if (!filename.toLowerCase().endsWith(ext)) {
						filename += ext;
					}
				} else if ("3".equals(stringValue)) {
					if (!filename.toLowerCase().endsWith(".png")) {
						filename += ".png";
					}
				}
				if (new File(filename).exists()
						&& JOptionPane.showConfirmDialog(graphComponent, Resources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
					return;
				}
			} else if ("2".equals(stringValue)) {
				if (!editor.getGraphComponents().isEmpty()) {
					JOptionPane.showMessageDialog(graphComponent, Resources.get("cannotSaveMultiDiagramAsFragment"), Resources.get("Warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				final String fdir = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_FRAGMENTS_DIR;
				FileSystemView fsv = new FileSystemView() {
					public File createNewFolder(File containingDir) throws IOException {
						return null;
					}

					public File getHomeDirectory() {
						return createFileObject(fdir);
					}

					public Boolean isTraversable(File f) {
						return f.getAbsolutePath().equals(fdir);
					}

					public File[] getRoots() {
						return new File[] { new File(fdir) };
					}

				};
				JFileChooser fc = new JFileChooser(fdir, fsv);
				fc.setAcceptAllFileFilterUsed(false);
				FileFilter fragmentFilter = new DefaultFileFilter(".bpmn", "OMG BPMN 2.0 " + Resources.get("file") + " (.bpmn)");
				fc.setFileFilter(fragmentFilter);
				int rc = fc.showDialog(null, Resources.get("save"));
				dialogShown = true;
				if (rc != JFileChooser.APPROVE_OPTION) {
					return;
				}
				filename = fc.getSelectedFile().getAbsolutePath();
				selectedFilter = fc.getFileFilter();
				if (selectedFilter instanceof DefaultFileFilter) {
					String ext = ((DefaultFileFilter) selectedFilter).getExtension();
					if (!filename.toLowerCase().endsWith(ext)) {
						filename += ext;
					}
				}
				if (new File(filename).exists()) {
					JOptionPane.showMessageDialog(graphComponent, Resources.get("cannotOverwriteExistingFragment"), Resources.get("Warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else {
				String ext = "";
				if ("3".equals(stringValue)) {
					ext = ".png";
				}
				filename = BPMNEditor.getCurrentFile().getAbsolutePath();
				if (!filename.toLowerCase().endsWith(ext)) {
					if (filename.endsWith(".bpmn20.xml")) {
						filename = filename.substring(0, filename.lastIndexOf(".bpmn20.xml")) + ext;
					} else {
						filename = filename.substring(0, filename.lastIndexOf(".")) + ext;
					}
					if (new File(filename).exists()
							&& JOptionPane.showConfirmDialog(graphComponent, Resources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
						return;
					}
				}
			}

			try {
				String ext = filename.substring(filename.lastIndexOf('.') + 1);
				if (ext.equalsIgnoreCase("svg")) {
					SvgCanvas canvas = (SvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null, true, new CanvasFactory() {
						public mxICanvas createCanvas(int width, int height) {
							SvgCanvas canvas = new SvgCanvas(mxDomUtils.createSvgDocument(width, height));
							canvas.setEmbedded(true);
							return canvas;
						}
					});
					mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), filename);
				} else if (selectedFilter == vmlFileFilter) {
					mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer.createVmlDocument(graph, null, 1, null, null).getDocumentElement()), filename);
				} else if (ext.equalsIgnoreCase("html")) {
					mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer.createHtmlDocument(graph, null, 1, null, null).getDocumentElement()), filename);
				} else if (ext.equalsIgnoreCase("bpmn") || ext.equalsIgnoreCase("xml")) {
					BPMNCodec codec = new BPMNCodec(graph);
					bpmnModel.setExporter("Yaoqiang BPMN Editor");
					bpmnModel.setExporterVersion(Constants.VERSION);
					mxUtils.writeFile(mxXmlUtils.getXml(codec.encode().getDocumentElement()), filename);
					if ("2".equals(stringValue)) {
						BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, graphComponent.isAntiAlias(), null, false,
								graphComponent.getCanvas());
						if (image != null) {
							String imagefilename = filename.substring(0, filename.lastIndexOf(".")) + ".png";
							ImageIO.write(image, "png", new File(imagefilename));
							editor.getFragmentsPalette().addFragmentTemplate(new File(imagefilename));
							editor.getFragmentsPalette().setPreferredCols(1, 195 * image.getHeight() / image.getWidth());
						}
					} else {
						editor.setModified(false);
						editor.setCurrentFile(new File(filename));
					}
				} else if (ext.equalsIgnoreCase("odt")) {
					BPMNEditorUtils.createODTFile(graphComponent, filename);
				} else {
					Color bg = null;
					if ((!ext.equalsIgnoreCase("gif") && !ext.equalsIgnoreCase("png")) || !"3".equals(stringValue)
							&& JOptionPane.showConfirmDialog(graphComponent, Resources.get("transparentBackground")) != JOptionPane.YES_OPTION) {
						bg = graphComponent.getViewport().getBackground();
					}
					double scale = 1;
					if ("1".equals(Constants.SETTINGS.getProperty("exportImageResolution", "0"))) {
						scale = graph.getView().getScale();
					}
					BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, scale, bg, graphComponent.isAntiAlias(), null, true,
							graphComponent.getCanvas());
					if (image != null) {
						ImageIO.write(image, ext, new File(filename));
						if ("3".equals(stringValue) && !dialogShown) {
							JOptionPane.showMessageDialog(graphComponent, "The png file is saved! \n (" + filename + ")");
						}
					} else {
						JOptionPane.showMessageDialog(graphComponent, Resources.get("noImageData"));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(graphComponent, ex.toString(), Resources.get("error"), JOptionPane.ERROR_MESSAGE);
			}
		} else if (OPEN.equals(type) || RELOAD.equals(type) || OPEN_URL.equals(type)) {
			editor.progress(true);
			int o = JOptionPane.NO_OPTION;
			if (editor.isModified()) {
				o = JOptionPane.showConfirmDialog(null, Resources.get("saveChanges"), Resources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					getSaveAction().actionPerformed(e);
				}
			}
			if (!editor.isModified() || o == JOptionPane.NO_OPTION) {
				graph.getView().setCurrentRoot(null);
				if (RELOAD.equals(type) && BPMNEditor.getCurrentFile() != null) {
					stringValue = BPMNEditor.getCurrentFile().getAbsolutePath();
				}
				if (stringValue != null && stringValue.length() != 0) {
					try {
						File file = new File(stringValue);
						Document document = null;
						if (file.exists() && file.isFile()) {
							document = EditorUtils.parseXml(new FileInputStream(stringValue));
						} else {
							BPMNEditorUtils.openBPMN(editor, stringValue);
						}

						if (document != null) {
							String name = document.getDocumentElement().getNodeName();
							if (name.endsWith("definitions")) {
								BPMNEditorUtils.openBPMN(editor, file);
							} else if (name.equals("VisioDocument")) {
								BPMNEditorUtils.openVdx(editor, file, document);
							} else if (name.equals("graphml")) {
								BPMNEditorUtils.openGraphML(editor, file, document);
							}
						}
					} catch (FileNotFoundException e1) {
						editor.progress(false);
						JOptionPane.showMessageDialog(editor.getGraphComponent(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					} catch (Exception e2) {
						editor.progress(false);
						e2.printStackTrace();
					}
				} else if (OPEN_URL.equals(type)) {
					Object url = null;
					url = JOptionPane.showInputDialog(graphComponent, Resources.get("openURLText"), Resources.get("openURL"), JOptionPane.PLAIN_MESSAGE, null,
							null, "http://");
					if (url != null) {
						BPMNEditorUtils.openBPMN(editor, url);
					}
				} else if (objectValue != null) {
					BPMNEditorUtils.openBPMN(editor, objectValue);
				} else {
					String wd = (EditorConstants.LAST_OPEN_DIR != null) ? EditorConstants.LAST_OPEN_DIR : System.getProperty("user.dir");

					JFileChooser fc = new JFileChooser(wd);

					// Adds file filter for supported file format
					FileFilter defaultFilter = new DefaultFileFilter(".bpmn", Resources.get("allSupportedFormats") + " (.bpmn, .bpmn20.xml, .xml)") {
						public boolean accept(File file) {
							String lcase = file.getName().toLowerCase();
							return super.accept(file) || lcase.endsWith(".xml");
						}
					};
					fc.addChoosableFileFilter(defaultFilter);
					fc.addChoosableFileFilter(new DefaultFileFilter(".bpmn", "OMG BPMN 2.0 " + Resources.get("file") + " (.bpmn)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".bpmn20.xml", "OMG BPMN 2.0 " + Resources.get("file") + " for Activiti (.bpmn20.xml)"));

					fc.setFileFilter(defaultFilter);
					int rc = fc.showDialog(null, Resources.get("openFile"));

					if (rc == JFileChooser.APPROVE_OPTION) {
						EditorConstants.LAST_OPEN_DIR = fc.getSelectedFile().getParent();
						Utils.saveToConfigureFile("lastOpenDir", EditorConstants.LAST_OPEN_DIR);
						String filepath = fc.getSelectedFile().getAbsolutePath();
						try {
							Document document = EditorUtils.parseXml(new FileInputStream(filepath));
							if (document != null) {
								String name = document.getDocumentElement().getNodeName();
								if (name.endsWith("definitions")) {
									BPMNEditorUtils.openBPMN(editor, fc.getSelectedFile());
								} else if (name.equals("VisioDocument")) {
									BPMNEditorUtils.openVdx(editor, fc.getSelectedFile(), document);
								} else if (name.equals("graphml")) {
									BPMNEditorUtils.openGraphML(editor, fc.getSelectedFile(), document);
								} else {
									JOptionPane
											.showMessageDialog(
													editor.getGraphComponent(),
													"The selected file is not recognized by Yaoqiang BPMN Editor. \nBecause the file format is unknown by the Editor,\nyou should be sure that the file comes from a trustworthy source.",
													"Unsupported file format", JOptionPane.ERROR_MESSAGE);
								}
							}
						} catch (Exception ex) {
							editor.progress(false);
							ex.printStackTrace();
							JOptionPane.showMessageDialog(editor.getGraphComponent(), ex.getStackTrace(),
									"Please Capture This Error Screen Shots and Submit this BUG.", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				editor.closeGraphComponents();
			}
			editor.progress(false);
		} else if (OPEN_DIAGRAM.equals(type)) {
			mxCell diagramCell = (mxCell) graph.getModel().getCell(stringValue);
			editor.insertGraphComponent(stringValue, (String) diagramCell.getValue());
		} else if (DIAGRAM_NAME.equals(type)) {
			mxCell root = (mxCell) model.getChildAt(model.getRoot(), 0);
			String initial = (String) root.getValue();
			String value = (String) JOptionPane.showInputDialog(graphComponent, Resources.get(DIAGRAM_NAME), Resources.get(DIAGRAM_NAME),
					JOptionPane.PLAIN_MESSAGE, null, null, initial);
			if (value != null) {
				root.setValue(value);
				editor.setDiagramName(value);
				editor.setModified(true);
				int index = editor.getDiagramName().indexOf(" -> SubProcess: ");
				if (index > 0) {
					value += editor.getDiagramName().substring(index);
					editor.setDiagramName(value);
				}

				BPMNEditorUtils.refreshDiagramList(editor, root);
			}
		} else if (ELEMENT.equals(type)) {
			dialog.initDialog().editBPMNElement(el);
		} else if (CONDITION_EXPRESSION.equals(type)) {
			if (el instanceof SequenceFlow) {
				dialog.initDialog().editBPMNElement(((SequenceFlow) el).getConditionExpression());
			}
		} else if (DATA_STATE.equals(type)) {
			if (el instanceof DataObjectReference) {
				dialog.initDialog().editBPMNElement(((DataObjectReference) el).get(type));
			}
		} else if (DOCUMENTATION.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			}
			if (el instanceof BaseElement) {
				dialog.initDialog().editBPMNElement(((BaseElement) el).getDocumentations(), type);
			}
		} else if (EVENT_DEFINITION.equals(type)) {
			if (el instanceof Event) {
				if (((Event) el).getEventDefinitionList().isEmpty()) {
					dialog.initDialog().editBPMNElement(((Event) el).getRefEventDefinition());
				} else {
					dialog.initDialog().editBPMNElement(((Event) el).getEventDefinition());
				}
			}
		} else if (EVENT_DEFINITIONS.equals(type)) {
			if (el instanceof Event) {
				dialog.initDialog().editBPMNElement(((Event) el).getEventDefinitions());
			}
		} else if (DEF_EVENT_DEFINITIONS.equals(type)) {
			dialog.initDialog().editBPMNElement(graph.getBpmnModel(), EVENT_DEFINITIONS);
		} else if (LOOP_CHARACTERISTICS.equals(type)) {
			if (el instanceof Activity) {
				dialog.initDialog().editBPMNElement(((Activity) el).getLoopCharacteristics());
			}
		} else if (PROCESS_PROPERTIES.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(el);
			}
		} else if (DATA_PROPERTIES.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof Activity || el instanceof Event || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(((XMLComplexElement) el).get("properties"), DATA_PROPERTIES);
			}
		} else if (DATA_OBJECTS.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof SubProcess || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(((XMLComplexElement) el).get("flowElements"), DATA_OBJECTS);
			}
		} else if (DATA_INOUT.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			}
			if (el instanceof Activity || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(el, "dataInputOutputs");
			} else if (el instanceof CatchEvent) {
				dialog.initDialog().editBPMNElement(el, "dataoutputs");
			} else if (el instanceof ThrowEvent) {
				dialog.initDialog().editBPMNElement(el, "datainputs");
			}
		} else if (RESOURCE_ASSIGNMENT.equals(type)) {
			if (el == null) {
				el = BPMNModelUtils.getDefaultProcess(bpmnModel);
			} else if (el instanceof Lane) {
				el = BPMNModelUtils.getParentProcess(el);
			} else if (el instanceof Participant) {
				el = bpmnModel.getProcess(((Participant) el).getProcessRef());
			}
			if (el instanceof Activity || el instanceof BPMNProcess) {
				dialog.initDialog().editBPMNElement(((XMLComplexElement) el).get("resources"));
			}
		} else if (type == CHANGE_FLOW_ELEMENT_TYPE) {
			BaseElement value = (BaseElement) model.getValue(cell);
			BaseElement newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
			model.beginUpdate();
			if ("exclusiveGatewayWithIndicator".equals(stringValue) || "exclusiveGateway".equals(stringValue)) {
				model.setStyle(cell, stringValue);
				stringValue = "exclusiveGateway";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
			} else if ("eventGatewayInstantiate".equals(stringValue)) {
				stringValue = "eventBasedGateway";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
				newValue.set("instantiate", "true");
			} else if ("parallelEventGateway".equals(stringValue)) {
				stringValue = "eventBasedGateway";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
				newValue.set("eventGatewayType", "Parallel");
				newValue.set("instantiate", "true");
			} else if ("intermediateThrowEvent".equals(stringValue)) {
				model.setStyle(cell, "intermediateEvent");
			} else if ("endEvent".equals(stringValue)) {
				model.setStyle(cell, stringValue);
			} else if ("eventSubProcess".equals(stringValue)) {
				stringValue = "subProcess";
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement(stringValue);
				newValue.set("triggeredByEvent", "true");
				newValue.add(value.get("flowElements"));
			} else if (value instanceof CallActivity) {
				newValue = (BaseElement) ((FlowElements) value.getParent()).generateFlowElement("callActivity");
				GlobalTask task = null;
				for (XMLElement gt : bpmnModel.getGlobalTasks(stringValue)) {
					task = (GlobalTask) gt;
					break;
				}
				if (task == null) {
					task = bpmnModel.createGlobalTask(stringValue);
				}
				((CallActivity) newValue).setCalledElement(task.getId());
			}
			// TODO:A
			if (value instanceof Activity && newValue instanceof Activity) {
				newValue.add(value.get("properties"));
				newValue.add(value.get("loopCharacteristics"));
			} else if (value instanceof Event && newValue instanceof Event) {
				newValue.add(value.get("properties"));
				newValue.add(((Event) value).getEventDefinitions());
				newValue.add(((Event) value).getEventDefinitionRefs());
			}
			if (value instanceof FlowNode && newValue instanceof FlowNode) {
				newValue.add(((FlowNode) value).getIncomings());
				newValue.add(((FlowNode) value).getOutgoings());
			}
			if (value instanceof SubProcess && newValue instanceof SubProcess) {
				newValue.add(value.get("flowElements"));
			}
			mxGeometry geo = (mxGeometry) cell.getGeometry().clone();
			if (newValue instanceof Gateway && !(value instanceof Gateway)) {
				int size = Integer.parseInt(Constants.SETTINGS.getProperty("style_gateway_size", "42"));
				geo.setRect(geo.getCenterX() - size / 2, geo.getCenterY() - size / 2, size, size);
				model.setGeometry(cell, geo);
			} else if (newValue instanceof Task && !(value instanceof Task)) {
				model.setStyle(cell, stringValue);
				int width = Integer.parseInt(Constants.SETTINGS.getProperty("style_task_width", "85"));
				int height = Integer.parseInt(Constants.SETTINGS.getProperty("style_task_height", "55"));
				geo.setRect(geo.getCenterX() - width / 2, geo.getCenterY() - height / 2, width, height);
				model.setGeometry(cell, geo);
			}
			newValue.setId(value.getId());
			newValue.set("name", value.get("name").toValue());
			model.setValue(cell, newValue);
			model.endUpdate();
		} else if (type == CHANGE_ACTIVITY_LOOP_TYPE) {
			FlowNode value = null;
			if (model.getValue(cell) instanceof Activity) {
				value = (Activity) ((Activity) model.getValue(cell)).clone();
				LoopCharacteristics loopCharacteristics = ((Activity) value).setLoopCharacteristics(stringValue);
				if (loopCharacteristics instanceof MultiInstanceLoopCharacteristics) {
					((MultiInstanceLoopCharacteristics) loopCharacteristics).setIsSequential(booleanValue);
				}
			} else if (model.getValue(cell) instanceof ChoreographyActivity) {
				value = (ChoreographyActivity) ((ChoreographyActivity) model.getValue(cell)).clone();
				((ChoreographyActivity) value).setLoopType(stringValue);
			}
			if (model.getValue(cell) instanceof FlowElementsContainer) {
				value.add(((FlowNode) model.getValue(cell)).get("flowElements"));
			}
			model.setValue(cell, value);
		} else if (type == CHANGE_ACTIVITY_COMPENSATION_TYPE) {
			Activity value = (Activity) ((Activity) model.getValue(cell)).clone();
			value.setIsForCompensation(booleanValue);
			model.setValue(cell, value);
		} else if (type == CHANGE_RECEIVE_TASK_INSTANTIATE_TYPE) {
			ReceiveTask value = (ReceiveTask) ((ReceiveTask) model.getValue(cell)).clone();
			value.setInstantiate(booleanValue);
			model.setValue(cell, value);
		} else if (type == CHANGE_DATA_TYPE) {
			BaseElement value = (BaseElement) model.getValue(cell);
			if (!value.toName().equals(stringValue)) {
				BaseElement newValue = null;
				XMLCollection parentElement = (XMLCollection) ((BaseElement) value).getParent();
				if ("dataInput".equals(stringValue)) {
					newValue = new DataInput(((DataOutput) value).getName(), ((DataOutput) value).isCollection());
				} else {
					newValue = new DataOutput(((DataInput) value).getName(), ((DataInput) value).isCollection());
				}
				newValue.setId(value.getId());
				parentElement.add(newValue);
				model.setValue(cell, newValue);
			}
		} else if (type == CHANGE_DATA_COLLECTION_TYPE) {
			XMLElement value = (XMLElement) ((XMLElement) model.getValue(cell)).clone();
			model.setValue(cell, value);
			if (value instanceof DataObjectReference) {
				((DataObjectReference) value).getRefDataObject().setIsCollection(booleanValue);
			} else {
				((BaseElement) value).set("isCollection", String.valueOf(booleanValue));
			}
		} else if (type == DEFAUT_SF) {
			BaseElement bpmnElement = (BaseElement) model.getValue(cell);
			bpmnElement.set("default", stringValue);
			graph.refresh();
		} else if (type == CHANGE_EVENT_TYPE) {
			Event value = (Event) ((Event) model.getValue(cell)).clone();
			model.beginUpdate();
			String style = null;
			if (stringValue.length() == 0) {
				if (value instanceof StartEvent) {
					style = "startEvent";
				} else if (value instanceof EndEvent) {
					style = "endEvent";
				} else {
					style = "intermediateEvent";
				}
				value.getEventDefinitions().clear();
				value.getEventDefinitionRefs().clear();
			} else if (stringValue.equals("multiple")) {
				if (value instanceof StartEvent) {
					style = "startMultipleEvent";
				} else if (value instanceof IntermediateCatchEvent || value instanceof BoundaryEvent) {
					style = "intermediateMultipleEvent";
				} else if (value instanceof IntermediateThrowEvent) {
					style = "intermediateMultipleThrowEvent";
				} else if (value instanceof EndEvent) {
					style = "endMultipleEvent";
				}
				if (value instanceof CatchEvent) {
					((CatchEvent) value).setParallelMultiple(false);
				}
			} else if (stringValue.equals("parallelMultiple")) {
				if (value instanceof StartEvent) {
					style = "startParallelMultipleEvent";
				} else if (value instanceof IntermediateCatchEvent || value instanceof BoundaryEvent) {
					style = "intermediateParallelMultipleEvent";
				}
				((CatchEvent) value).setParallelMultiple(true);
			} else {
				if (value instanceof StartEvent) {
					style = "startEvent";
				} else if (value instanceof EndEvent) {
					style = "endEvent";
				} else {
					style = "intermediateEvent";
				}
				value.getEventDefinitions().clear();
				value.getEventDefinitionRefs().clear();
				EventDefinition eventDefinition = value.generateEventDefinition(stringValue);
				value.addEventDefinition(eventDefinition);
				if (eventDefinition instanceof LinkEventDefinition) {
					((LinkEventDefinition) eventDefinition).setName(value.getName());
				}
			}
			XMLExtensionElement styleElement = ((BaseElement) value).getExtensionElements().getChildElement("yaoqiang:style");
			if (styleElement != null) {
				for (XMLElement attr : styleElement.toElements()) {
					style += ";" + ((XMLAttribute) attr).toName() + "=" + ((XMLAttribute) attr).toValue();
				}
			}
			model.setStyle(cell, style);
			model.setValue(cell, value);
			model.endUpdate();
		} else {
			dialog.initDialog().editBPMNElement(bpmnModel, type);
		}
		graphComponent.requestFocusInWindow();
	}

	public ModelActions setStringValue(String value) {
		this.stringValue = value;
		return this;
	}

	public ModelActions setObjectValue(Object value) {
		this.objectValue = value;
		return this;
	}

	public ModelActions setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
		return this;
	}

	public BPMNEditor getEditor(ActionEvent e) {
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
