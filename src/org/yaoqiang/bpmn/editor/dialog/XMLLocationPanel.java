package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.dialog.panels.ImportPanel;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.util.EditorUtils;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.core.foundation.Documentation;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Resources;


/**
 * XMLLocationPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLLocationPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	private XMLPanel source = this;

	protected JTextField jtf;

	protected JButton jb;

	protected static String lastDir;

	protected File file = null;

	protected String fileName = "";

	public XMLLocationPanel(PanelContainer pc, final XMLElement owner, final String type) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		Dimension textDim = new Dimension(250, 26);

		JLabel jl = new JLabel(Resources.get(type == null ? owner.toName() : type) + ": ");

		jtf = new JTextField();
		jtf.setText(owner.toValue());
		file = new File(EditorUtils.getFilePath(null, owner.toValue()));
		if (file.exists() && file.isFile()) {
			lastDir = file.getParent();
		}
		jtf.setMinimumSize(new Dimension(textDim));
		jtf.setMaximumSize(new Dimension(textDim));
		jtf.setPreferredSize(new Dimension(textDim));

		jtf.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				getPanelContainer().panelChanged();
			}
		});

		jtf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				JTextField c = (JTextField) evt.getSource();
				String location = c.getText();
				if (location.startsWith("http") || location.startsWith("ftp")) {
					try {
						Document document = EditorUtils.parseXml(new URL(location).openStream());
						ImportPanel parentPanel = (ImportPanel) source.getParent();
						XMLComboPanel importTypePanel = parentPanel.getImportTypePanel();
						importTypePanel.setSelectedItem(BPMNEditorUtils.getXmlFileType(document));
						String namespace = document.getDocumentElement().getAttribute("targetNamespace");
						if (namespace != null) {
							parentPanel.getNamespacePanel().setText(namespace);
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
					}
				}

			}
		});

		jb = new JButton(Resources.get("select"));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		if ("file".equals(type)) {
			mainPanel.add(Box.createVerticalStrut(20), BorderLayout.NORTH);
		}
		mainPanel.add(jl, BorderLayout.WEST);
		mainPanel.add(jtf, BorderLayout.CENTER);
		mainPanel.add(jb, BorderLayout.EAST);
		this.add(mainPanel, BorderLayout.NORTH);

		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String filepath = "";
				String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");
				JFileChooser fc = new JFileChooser(wd);
				int rc = fc.showDialog(null, Resources.get("selectFile"));
				if (rc == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					lastDir = file.getParent();
					filepath = file.getAbsolutePath();
					fileName = file.getName();
				}
				if (filepath != null && filepath.length() > 0) {
					jtf.setText(BPMNEditorUtils.getRelativeFilePath(filepath));
					getPanelContainer().panelChanged();
					if (type.equals("location")) {
						try {
							ImportPanel parentPanel = (ImportPanel) source.getParent();
							XMLComboPanel importTypePanel = parentPanel.getImportTypePanel();

							Document document = EditorUtils.parseXml(new FileInputStream(filepath));
							String importType = BPMNEditorUtils.getXmlFileType(document);
							if ("http://www.omg.org/spec/BPMN/20100524/MODEL".equals(importType)) {
								String id = document.getDocumentElement().getAttribute("id");
								if (BPMNModelUtils.getDefinitions(owner).getId().equals(id)) {
									jtf.setText("");
									JOptionPane.showMessageDialog(null, Resources.get("cannotImportSameDefinitionsFile"), Resources.get("Warning"),
											JOptionPane.WARNING_MESSAGE);
									return;
								}
							}
							importTypePanel.setSelectedItem(importType);
							String namespace = document.getDocumentElement().getAttribute("targetNamespace");
							if (namespace != null) {
								parentPanel.getNamespacePanel().setText(namespace);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	public boolean isEmpty() {
		return getText().equals("");
	}

	public void saveObjects() {
		String text = getText();
		if (owner instanceof Documentation) {
			String format = BPMNEditorUtils.getMediaType(text);
			((Documentation) owner).setTextFormat(format);
			getOwner().setValue(getText());
		} else {
			getOwner().setValue(text);
		}
	}

	public JTextField getTextField() {
		return jtf;
	}

	public String getText() {
		return jtf.getText().trim();
	}

	public final File getSelectedFile() {
		return file;
	}

	public void requestFocus() {
		jtf.requestFocus();
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jtf.setEnabled(b);
		jb.setEnabled(b);
	}

}
