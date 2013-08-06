package org.yaoqiang.bpmn.editor.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelCodec;
import org.yaoqiang.bpmn.model.BPMNModelParsingErrors.ErrorMessage;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.bpmndi.BPMNDiagram;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElement;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Import;
import org.yaoqiang.graph.canvas.SvgCanvas;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.io.graphml.GraphMLCodec;
import org.yaoqiang.graph.io.odt.OdtConstants;
import org.yaoqiang.graph.io.vdx.VdxCodec;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.util.GraphUtils;
import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;

/**
 * BPMNEditorUtils
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNEditorUtils {

	public static boolean isXSDFile(String importType) {
		if (importType.equals("http://www.w3.org/2001/XMLSchema")) {
			return true;
		}
		return false;
	}

	public static String getRelativeFilePath(String location) {
		String filepath = location;
		File file = new File(location);
		if (file.exists() && file.isFile()) {
			File tmp = BPMNEditor.getCurrentFile();
			if (tmp != null) {
				if (tmp.getParent().equals(file.getParent())) {
					filepath = file.getName();
				}
			}
		} else {
			return filepath;
		}
		return filepath;
	}

	public static List<String> getWSDLMessages(Document document) {
		List<String> messages = new ArrayList<String>();
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getLocalName() != null && node.getLocalName().equals("message")) {
				messages.add(((Element) node).getAttribute("name"));
			}
		}
		return messages;
	}

	public static List<String> getWSDLTypes(Document document) {
		List<String> elements = new ArrayList<String>();
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getLocalName() != null && node.getLocalName().equals("types")) {
				Node schema = node.getFirstChild().getNextSibling();
				if (schema != null && schema.hasChildNodes()) {
					NodeList elementList = schema.getChildNodes();
					for (int j = 0; j < elementList.getLength(); j++) {
						Node element = elementList.item(j);
						if (element.getLocalName() != null && element.getLocalName().equals("element")) {
							elements.add(((Element) element).getAttribute("name"));
						}
					}
				}
				break;
			}
		}
		return elements;
	}

	public static List<String> getXSDElements(Document document) {
		List<String> elements = new ArrayList<String>();
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getLocalName() != null && node.getLocalName().equals("element")) {
				elements.add(((Element) node).getAttribute("name"));
			}
		}
		return elements;
	}

	public static String getXmlFileType(Document document) {
		String type = "http://www.w3.org/2001/XMLSchema";
		NamedNodeMap attribs = document.getDocumentElement().getAttributes();
		for (int i = 0; i < attribs.getLength(); i++) {
			String uri = attribs.item(i).getNodeValue();
			if ("http://schemas.xmlsoap.org/wsdl/".equals(uri)) {
				type = uri;
				break;
			} else if ("http://www.w3.org/ns/wsdl".equals(uri)) {
				type = "http://www.w3.org/TR/wsdl20/";
				break;
			} else if ("http://www.omg.org/spec/BPMN/20100524/MODEL".equals(uri)) {
				type = uri;
				break;
			}
		}
		return type;
	}

	public static String getMediaType(String suffix) {
		String format = "";
		if (suffix.endsWith(".doc")) {
			format = "application/msword";
		} else if (suffix.endsWith(".docx") || suffix.endsWith(".pptx") || suffix.endsWith(".xlsx")) {
			format = "application/vnd.openxmlformats";
		} else if (suffix.endsWith(".gif")) {
			format = "image/gif";
		} else if (suffix.endsWith(".gz")) {
			format = "application/x-gzip";
		} else if (suffix.endsWith(".jpg") || suffix.endsWith(".jpeg")) {
			format = "image/jpeg";
		} else if (suffix.endsWith(".pdf")) {
			format = "application/pdf";
		} else if (suffix.endsWith(".png")) {
			format = "image/png";
		} else if (suffix.endsWith(".ppt")) {
			format = "application/mspowerpoint";
		} else if (suffix.endsWith(".ps")) {
			format = "application/postscript";
		} else if (suffix.endsWith(".vsd")) {
			format = "application/x-visio";
		} else if (suffix.endsWith(".xls")) {
			format = "application/excel";
		} else if (suffix.endsWith(".zip")) {
			format = "application/zip";
		} else {
			format = "application/octet-stream";
		}
		return format;
	}

	public static Map<String, Definitions> getExternalDefinitions(String parentPath, Definitions definitions) {
		Map<String, Definitions> externalDefinitions = new HashMap<String, Definitions>();
		for (XMLElement i : definitions.getImportList()) {
			Document doc = EditorUtils.parseXml(parentPath, ((Import) i).getLocation());
			if (doc != null) {
				String type = BPMNEditorUtils.getXmlFileType(doc);
				if ("http://www.omg.org/spec/BPMN/20100524/MODEL".equals(type)) {
					Definitions defs = new Definitions();
					new BPMNModelCodec().decode(doc.getDocumentElement(), defs);
					externalDefinitions.put(defs.getTargetNamespace(), defs);
					externalDefinitions.putAll(getExternalDefinitions(new File(EditorUtils.getFilePath(parentPath, ((Import) i).getLocation())).getParent(),
							defs));
				}
			}

		}
		return externalDefinitions;
	}

	public static void refreshDiagramList(BPMNEditor editor, Object diagramCell) {
		JMenu menu = editor.getDiagramsMenu();
		menu.removeAll();
		mxGraphModel model = BPMNEditor.getGraph().getModel();
		ButtonGroup diagramGroup = new ButtonGroup();
		if (diagramCell == null) {
			diagramCell = model.getChildAt(model.getRoot(), 0);
		}
		int num = 1;
		for (Object r : mxGraphModel.getChildren(model, model.getRoot())) {
			mxCell root = (mxCell) r;
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(editor.bind(num++ + ": " + (String) root.getValue(),
					ModelActions.getOpenDiagramAction(root.getId())));
			item.setName(root.getId());
			if (root == diagramCell) {
				item.setSelected(true);
			}
			diagramGroup.add(item);
			menu.add(item);
		}
	}

	public static void insertAdditionalParticipant(BPMNGraph graph, String id, String value, boolean toTop, Object parent) {
		mxGraphModel model = graph.getModel();
		if (model.getCell(id) != null) {
			return;
		}
		double yOffset = 0;
		String style = "";

		mxCell subprocess = GraphUtils.getChoreographyActivity(graph, parent);

		mxGeometry subgeo = model.getGeometry(subprocess);
		if (toTop) {
			yOffset = subgeo.getY() - 1;
			style = "participantAdditionalTop";
		} else {
			yOffset = subgeo.getY() + subgeo.getHeight() - 1;
			style = "participantAdditionalBottom";
		}

		mxCell participantCell = new mxCell(value, new mxGeometry(0, yOffset, Constants.ACTIVITY_WIDTH, Constants.PARTICIPANT_HEIGHT), style);
		participantCell.setId(id);
		participantCell.setVertex(true);
		graph.addCell(participantCell, (mxICell) parent);

		GraphUtils.arrangeChoreography(graph, parent, false);

		graph.refresh();

	}

	public static void createODTFile(BPMNGraphComponent graphComponent, String filename) {
		Map<String, Object> odtFiles = new HashMap<String, Object>();
		BPMNGraph graph = graphComponent.getGraph();

		odtFiles.put("BPMNDefinition_Name", graph.getBpmnModel().getName());

		int diagramNum = 0;
		Object currentRoot = graph.getCurrentRoot();
		graph.getView().setCurrentRoot(null);
		BufferedImage image = createImageForPage(graphComponent, null);
		graph.getView().setCurrentRoot(currentRoot);
		BPMNDiagram bpmnDiagram = graph.getBpmnModel().getFirstBPMNDiagram();
		if (image != null) {
			diagramNum++;
			odtFiles.put("Pictures/BPMNDiagram_1.png", image);
			odtFiles.put("BPMNDiagram_1_Name", bpmnDiagram == null || bpmnDiagram.getName().length() == 0 ? Resources.get("newDiagram") : bpmnDiagram.getName());
		}
		for (XMLElement el : BPMNModelUtils.getAllSubProcesses(graph.getBpmnModel())) {
			Object parent = graph.getModel().getCell(((SubProcess) el).getId());
			if (graph.getModel().isCollapsedSubProcess(parent) && graph.getChildCells(parent).length > 0) {
				graph.getView().setCurrentRoot(parent);
				image = createImageForPage(graphComponent, null);
				graph.getView().setCurrentRoot(currentRoot);
				if (image != null) {
					diagramNum++;
					odtFiles.put("Pictures/BPMNDiagram_" + diagramNum + ".png", image);
					odtFiles.put("BPMNDiagram_" + diagramNum + "_Name", ((SubProcess) el).getName());
				}
			}
		}
		if (graph.getBpmnModel().getBPMNDiagrams().size() > 1) {
			for (XMLElement el : graph.getBpmnModel().getBPMNDiagrams().getXMLElements()) {
				if (el == bpmnDiagram) {
					continue;
				}
				Object parent = graph.getModel().getCell(((BPMNDiagram) el).getBPMNPlane().getBpmnElement());
				image = createImageForPage(graphComponent, graph.getChildCells(parent));
				if (image != null) {
					diagramNum++;
					odtFiles.put("Pictures/BPMNDiagram_" + diagramNum + ".png", image);
					odtFiles.put("BPMNDiagram_" + diagramNum + "_Name", ((BPMNDiagram) el).getName());
				}
			}
		}

		odtFiles.put("mimetype", new ByteArrayInputStream("application/vnd.oasis.opendocument.text".getBytes()));
		odtFiles.put("settings.xml", new ByteArrayInputStream(createODTSettingsXml().getBytes()));
		odtFiles.put("meta.xml", new ByteArrayInputStream(createODTMetaXml().getBytes()));
		try {
			odtFiles.put("styles.xml", new ByteArrayInputStream(createODTStylesXml(new File(filename).getName()).getBytes("UTF-8")));
			odtFiles.put("content.xml", new ByteArrayInputStream(createODTContentXml(graph.getBpmnModel(), odtFiles, diagramNum).getBytes()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		odtFiles.put("META-INF/manifest.xml", new ByteArrayInputStream(createODTManifestXml(diagramNum).getBytes()));
		createZipFile(filename, odtFiles);
	}

	public static String createODTSettingsXml() {
		Document doc = mxDomUtils.createDocument();
		Element root = doc.createElement("office:document-settings");
		doc.appendChild(root);
		root.setAttribute("xmlns:office", OdtConstants.XMLNS_OFFICE);
		root.setAttribute("office:version", "1.1");
		return mxXmlUtils.getXml(doc);
	}

	public static String createODTMetaXml() {
		Document doc = mxDomUtils.createDocument();
		Element root = doc.createElement("office:document-meta");
		doc.appendChild(root);
		root.setAttribute("xmlns:office", OdtConstants.XMLNS_OFFICE);
		root.setAttribute("xmlns:meta", OdtConstants.XMLNS_META);
		root.setAttribute("office:version", "1.1");
		Element meta = doc.createElement("office:meta");
		root.appendChild(meta);
		Element metaChild = doc.createElement("meta:generator");
		metaChild.setTextContent(Resources.get("title") + "/" + Constants.VERSION);
		meta.appendChild(metaChild);
		metaChild = doc.createElement("meta:initial-creator");
		metaChild.setTextContent(Resources.get("title"));
		meta.appendChild(metaChild);
		metaChild = doc.createElement("meta:creation-date");
		metaChild.setTextContent(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
		meta.appendChild(metaChild);
		return mxXmlUtils.getXml(doc);
	}

	public static String createODTStylesXml(String filename) {
		Document doc = null;
		try {
			URL url = BPMNEditorUtils.class.getResource("/org/yaoqiang/bpmn/editor/resources/odt/styles.xml");
			doc = EditorUtils.parseXml(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String xmlstring = mxXmlUtils.getXml(doc);
		xmlstring = xmlstring.replaceFirst("@@FILE_NAME@@", replaceEscapedChars(filename));
		xmlstring = xmlstring.replaceFirst("@@DATE@@", new SimpleDateFormat("d MMM yyyy").format(new Date()));
		return xmlstring;
	}

	public static String createODTContentXml(Definitions bpmnModel, Map<String, Object> files, int diagramNum) {
		Document doc = null;
		try {
			URL url = BPMNEditorUtils.class.getResource("/org/yaoqiang/bpmn/editor/resources/odt/content.xml");
			doc = EditorUtils.parseXml(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String xmlstring = mxXmlUtils.getXml(doc);

		xmlstring = xmlstring.replaceFirst("@@AUTHOR@@", replaceEscapedChars(System.getProperty("user.name")));
		xmlstring = xmlstring.replaceFirst("@@DEFINITION_NAME@@", replaceEscapedChars((String) files.remove("BPMNDefinition_Name")));

		String diagrams = "";
		String diagramItem = "<text:p text:style-name=\"P5\"><draw:frame draw:style-name=\"Graphics\" draw:name=\"@@BPMN_DIAGRAM_ID@@\" text:anchor-type=\"paragraph\" svg:x=\"0in\" svg:y=\"0in\" svg:width=\"@@WIDTH@@in\" svg:height=\"@@HEIGHT@@in\" style:rel-width=\"100%\" style:rel-height=\"100%\"><draw:image xlink:href=\"@@BPMN_DIAGRAM_PATH@@\" xlink:type=\"simple\" xlink:show=\"embed\" xlink:actuate=\"onLoad\"/></draw:frame>\n@@BPMN_DIAGRAM_NAME@@\n</text:p>";
		for (int i = 1; i <= diagramNum; i++) {
			String diagramId = "BPMNDiagram_" + i;
			String diagramPath = "Pictures/BPMNDiagram_" + i + ".png";
			diagrams += diagramItem;
			diagrams = diagrams.replaceFirst("@@BPMN_DIAGRAM_ID@@", diagramId);
			diagrams = diagrams.replaceFirst("@@BPMN_DIAGRAM_PATH@@", diagramPath);
			diagrams = diagrams.replaceFirst("@@BPMN_DIAGRAM_NAME@@",
					(String) "BPMN Diagram " + i + ": " + replaceEscapedChars((String) files.remove(diagramId + "_Name")));
			diagrams = diagrams.replaceFirst("@@WIDTH@@", "" + (double) ((BufferedImage) files.get(diagramPath)).getWidth() / 96);
			diagrams = diagrams.replaceFirst("@@HEIGHT@@", "" + (double) ((BufferedImage) files.get(diagramPath)).getHeight() / 96);
			if (((BufferedImage) files.get(diagramPath)).getHeight() < 880) {
				diagrams += "<text:p text:style-name=\"P5\"/>";
			}
		}
		xmlstring = xmlstring.replaceFirst("@@BPMN_DIAGRAMS@@", diagrams);
		xmlstring = createODTElementsContentXml(bpmnModel, xmlstring);
		return xmlstring;
	}

	public static String createODTElementsContentXml(Definitions bpmnModel, String xmlstring) {
		String elementTable = "";
		String elementRow = "<table:table-row table:style-name=\"TableRow\"><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@TYPE@@</text:p></table:table-cell><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@ID@@</text:p></table:table-cell><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@NAME@@</text:p></table:table-cell><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@DESC@@</text:p></table:table-cell></table:table-row>";
		for (XMLElement el : BPMNModelUtils.getAllFlowElements(bpmnModel)) {
			FlowElement f = (FlowElement) el;
			String desc = "";
			for (XMLElement doc : f.getDocumentations().getXMLElements()) {
				desc += doc.toValue();
			}
			elementTable += elementRow.replaceFirst("@@TYPE@@", f.getClass().getSimpleName()).replaceFirst("@@ID@@", f.getId())
					.replaceFirst("@@NAME@@", replaceEscapedChars(f.getName())).replaceFirst("@@DESC@@", replaceEscapedChars(desc));
		}
		return xmlstring.replaceFirst("@@BPMN_ELEMENTS@@", elementTable);
	}

	public static String createODTManifestXml(int diagramNum) {
		Document doc = null;
		try {
			URL url = BPMNEditorUtils.class.getResource("/org/yaoqiang/bpmn/editor/resources/odt/manifest.xml");
			doc = EditorUtils.parseXml(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String xmlstring = mxXmlUtils.getXml(doc);
		String picEntries = "";
		String picEntry = "<manifest:file-entry manifest:full-path=\"@@BPMN_DIAGRAM_PATH@@\" manifest:media-type=\"image/png\"/>";
		for (int i = 1; i <= diagramNum; i++) {
			if (picEntries.length() > 0) {
				picEntries += "\n\t";
			}
			picEntries += picEntry.replaceFirst("@@BPMN_DIAGRAM_PATH@@", "Pictures/BPMNDiagram_" + i + ".png");
		}
		xmlstring = xmlstring.replaceFirst("@@MANIFEST_FILE_ENTRY@@", picEntries);
		return xmlstring;
	}

	public static BufferedImage createImageForPage(BPMNGraphComponent graphComponent, Object[] cells) {
		BufferedImage after = null;
		BufferedImage image = mxCellRenderer.createBufferedImage(graphComponent.getGraph(), cells, 1, null, graphComponent.isAntiAlias(), null, true,
				graphComponent.getCanvas());
		if (image != null) {
			int w = image.getWidth();
			int h = image.getHeight();
			double imgH = 882.528;
			double imgW = 553.728;
			boolean scale = false;
			boolean rotate = false;

			if (Math.max(w, h) > imgH || Math.min(w, h) > imgW) {
				scale = true;
			}
			if (w > h && w > imgW) {
				rotate = true;
			}

			if (!scale && !rotate) {
				after = image;
			} else {
				AffineTransform at = new AffineTransform();
				double s = 1.0;
				if (rotate) {
					if (scale) {
						s = Math.min(imgH / w, imgW / h);
						at.scale(s, s);
					}
					at.translate(0.5 * h, 0.5 * w);
					at.rotate(Math.PI / 2);
					at.translate(-0.5 * w, -0.5 * h);
					after = new BufferedImage((int) (s * h), (int) (s * w), BufferedImage.TYPE_INT_ARGB);
				} else {
					s = Math.min(imgH / h, imgW / w);
					at.scale(s, s);
					after = new BufferedImage((int) (s * w), (int) (s * h), BufferedImage.TYPE_INT_ARGB);
				}
				AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
				after = rotateOp.filter(image, after);
			}
		}

		return after;
	}

	public static void createZipFile(String filename, Map<String, Object> files) {
		ZipOutputStream out;
		try {
			out = new ZipOutputStream(new FileOutputStream(filename));

			int len;
			byte[] buffer = new byte[1024];

			for (Entry<String, Object> entry : files.entrySet()) {
				out.putNextEntry(new ZipEntry(entry.getKey()));

				if (entry.getValue() instanceof BufferedImage) {
					ImageIO.write((BufferedImage) entry.getValue(), "png", out);
				} else if (entry.getValue() instanceof ByteArrayInputStream) {
					ByteArrayInputStream in = (ByteArrayInputStream) entry.getValue();
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					in.close();
				} else if (entry.getValue() instanceof String) {
					FileInputStream in = new FileInputStream((String) entry.getValue());
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					in.close();
				}

				out.closeEntry();

			}

			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String replaceEscapedChars(String string) {
		return string.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\\", "\\\\");
	}

	public static void export(String bpmnFile, String file) {
		String ext = ".png";
		if (file.length() > 0 && file.endsWith(".bpmn")) {
			System.out.println("Generating BPMN XML file...");
			ext = ".bpmn";
		} else if (file.length() > 0 && file.endsWith(".svg")) {
			System.out.println("Generating BPMN SVG file...");
			ext = ".svg";
		} else {
			System.out.println("Generating BPMN diagram...");
			if (file.length() > 0) {
				if (!file.endsWith(ext)) {
					int index = file.lastIndexOf(".");
					if (index > 0) {
						file = file.substring(0, file.lastIndexOf(".")) + ext;
					} else {
						file = file + ext;
					}
				}
			} else {
				int index = bpmnFile.lastIndexOf(".");
				if (index > 0) {
					file = bpmnFile.substring(0, index) + ext;
				} else {
					file = bpmnFile + ext;
				}
			}
		}

		if (new File(file).exists()) {
			String tmpFile = file;
			int num = 0;
			while (new File(tmpFile).exists()) {
				num++;
				tmpFile = file.substring(0, file.lastIndexOf(".")) + num + ext;
			}
			file = tmpFile;
		}

		BPMNGraph graph = new BPMNGraph(new GraphModel(Constants.VERSION));
		BPMNGraphComponent graphComponent = new BPMNGraphComponent(graph);
		BPMNCodec codec = new BPMNCodec(graph);
		codec.decode(bpmnFile);
		if (codec.isAutolayout()) {
			for (Object pool : graph.getAllPools()) {
				GraphUtils.arrangeSwimlaneSize(graph, pool, false, false, false);
			}
			GraphUtils.arrangeSwimlanePosition(graphComponent);
		}

		if (".png".equalsIgnoreCase(ext)) {
			BufferedImage image = GraphUtils.generateDiagram(graphComponent);
			if (image != null) {
				try {
					ImageIO.write(image, ext.substring(1), new File(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("The BPMN diagram is exported to an image file : " + file);
			}
		} else if (".svg".equalsIgnoreCase(ext)) {
			SvgCanvas canvas = (SvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null, true, new CanvasFactory() {
				public mxICanvas createCanvas(int width, int height) {
					SvgCanvas canvas = new SvgCanvas(mxDomUtils.createSvgDocument(width, height));
					canvas.setEmbedded(true);
					return canvas;
				}
			});
			try {
				mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("The BPMN diagram is exported to an svg file : " + file);
		} else {
			graph.getBpmnModel().setExporter("Yaoqiang BPMN Editor");
			graph.getBpmnModel().setExporterVersion(Constants.VERSION);
			try {
				mxUtils.writeFile(mxXmlUtils.getXml(codec.encode().getDocumentElement()), file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("The BPMN Definitions is exported to an xml file : " + file);
		}
	}

	public static void openGraphML(BPMNEditor editor, File file, Document document) {
		String filename = file.getName();
		filename = filename.substring(0, filename.length() - 8) + ".bpmn";

		if (new File(file.getParent() + File.separator + filename).exists()
				&& JOptionPane.showConfirmDialog(editor, Resources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
			return;
		}

		GraphMLCodec codec = new GraphMLCodec(editor.getGraphComponent(), document);
		codec.decode();
		editor.setCurrentFile(new File(file.getParent() + File.separator + filename));
		resetEditor(editor, true);
	}

	public static void openVdx(BPMNEditor editor, File file, Document document) {
		String filename = file.getName();
		filename = filename.substring(0, filename.length() - 4) + ".bpmn";

		if (new File(file.getParent() + File.separator + filename).exists()
				&& JOptionPane.showConfirmDialog(editor, Resources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
			return;
		}

		VdxCodec codec = new VdxCodec(editor.getGraphComponent(), document);
		int res = codec.decode();
		if (res == -1) {
			JOptionPane.showMessageDialog(editor, Resources.get("warningVdxVendor"), Resources.get("Warning"), JOptionPane.WARNING_MESSAGE);
		}
		editor.setCurrentFile(new File(file.getParent() + File.separator + filename));
		resetEditor(editor, true);
	}

	public static void openBPMN(BPMNEditor editor, Object file) {
		BPMNGraph graph = editor.getGraphComponent().getGraph();
		BPMNCodec codec = new BPMNCodec(graph);
		List<ErrorMessage> errorMessages = null;
		String parentPath = null;
		if (file instanceof File) {
			errorMessages = codec.decode(((File) file).getAbsolutePath());
			parentPath = ((File) file).getParent();
		} else {
			errorMessages = codec.decode(file);
		}

		if (errorMessages != null && errorMessages.size() > 0) {
			editor.error(errorMessages);
			editor.setModified(false);
			return;
		}
		graph.getExternalDefinitions().putAll(BPMNEditorUtils.getExternalDefinitions(parentPath, graph.getBpmnModel()));

		boolean resetPageFormat = true;
		if (graph.getBpmnModel().getFirstBPMNDiagram() != null) {
			if (graph.getBpmnModel().getFirstBPMNDiagram().getDocumentation().startsWith("background=")) {
				resetPageFormat = false;
			}
		}

		if (file instanceof File) {
			editor.setCurrentFile((File) file);
		} else {
			editor.setCurrentFile(null);
		}
		GraphModel model = graph.getModel();
		editor.setDiagramName((String) model.getValue(model.getChildAt(model.getRoot(), 0)));
		BPMNEditorUtils.refreshDiagramList(editor, null);
		if (codec.isAutolayout()) {
			for (Object pool : graph.getAllPools()) {
				GraphUtils.arrangeSwimlaneSize(graph, pool, false, false, false);
			}
			GraphUtils.arrangeSwimlanePosition(editor.getGraphComponent());
		}
		resetEditor(editor, resetPageFormat);
	}

	public static void resetEditor(BPMNEditor editor, boolean resetPageFormat) {
		BPMNGraphComponent graphComponent = editor.getGraphComponent();
		BPMNGraph graph = graphComponent.getGraph();
		GraphModel model = graph.getModel();
		if (resetPageFormat) {
			PageFormat pageFormat = model.getPageFormat();
			Paper paper = pageFormat.getPaper();
			paper.setSize(Constants.PAGE_HEIGHT, Constants.PAGE_WIDTH);
			double margin = 5;
			paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
			pageFormat.setPaper(paper);
			model.setPageFormat(pageFormat);
		}

		graphComponent.setPageFormat(model.getPageFormat());
		graphComponent.setVerticalPageCount(model.getPageCount());
		graphComponent.setHorizontalPageCount(model.getHorizontalPageCount());
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground(model.getBackgroundColor());

		GraphUtils.arrangeAllSwimlaneLength(graph, true);
		graph.refresh();
		editor.setModified(false);
		editor.getUndoManager().clear();
		editor.getGraphComponent().zoomAndCenter();
		editor.getBpmnView().refreshView(graph);
	}

}
