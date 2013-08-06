package org.yaoqiang.bpmn.editor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.swing.Palettes;

public class EditorUtils {

	public static void writeGzipFile(String contents, String filename) throws IOException {
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(filename));
		byte[] data = contents.getBytes("UTF-8");
		out.write(data, 0, data.length);
		out.flush();
		out.close();
	}

	public static Document parseGzipFile(String filename) throws IOException {
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(filename));
		return parseXml(in);
	}

	public static Document parseXml(String parentPath, String location) {
		Document document = null;
		if (location.startsWith("http") || location.startsWith("ftp")) {
			try {
				document = parseXml(new URL(location).openStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
			}
		} else {
			try {
				document = parseXml(new FileInputStream(getFilePath(parentPath, location)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return document;
	}

	public static Document parseXml(InputStream in) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			return docBuilder.parse(in);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Not a valid XML file!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	public static String getFilePath(String parentPath, String location) {
		String filepath = location;
		File file = new File(location);
		if (file.exists() && file.isFile()) {
			return filepath;
		} else {
			if (parentPath == null && BaseEditor.getCurrentFile() != null) {
				parentPath = BaseEditor.getCurrentFile().getParent();
			}
			if (parentPath != null) {
				filepath = parentPath + File.separator + location;
				file = new File(filepath);
				if (file.exists() && file.isFile()) {
					return filepath;
				}
			}
		}
		return filepath;
	}

	public static void addArtifact(Palettes palettes, String name, String path) {
		String imagePath = path + name;
		ImageIcon icon = new ImageIcon(imagePath);
		palettes.addTemplate(name.substring(0, name.lastIndexOf(".")), icon, "image;image=File:///" + imagePath, icon.getIconWidth(), icon.getIconHeight(), "");
	}

}
