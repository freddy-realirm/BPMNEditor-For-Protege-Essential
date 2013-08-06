package org.yaoqiang.bpmn.editor.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.view.Graph;
import org.yaoqiang.util.Resources;


/**
 * BPMNViewComponent
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNViewComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	protected BPMNCodec bpmnCodec;

	protected RSyntaxTextArea bpmnTextArea;

	protected JComboBox searchCB = new JComboBox();
	private JCheckBox caseCB = new JCheckBox(Resources.get("case"));
	private JCheckBox wordCB = new JCheckBox(Resources.get("word"));
	private JCheckBox regexCB = new JCheckBox(Resources.get("regex"));

	protected Component searchPanel;

	public BPMNViewComponent(BPMNGraph graph) {
		setName("BPMNView");
		setLayout(new BorderLayout());

		bpmnCodec = new BPMNCodec(graph);

		add(createCenterComponent(), BorderLayout.CENTER);
		add(createSearchPanel(), BorderLayout.NORTH);
	}

	protected Component createCenterComponent() {
		bpmnTextArea = new RSyntaxTextArea();
		bpmnTextArea.setEditable(false);
		bpmnTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		bpmnTextArea.setCodeFoldingEnabled(true);
		bpmnTextArea.setAntiAliasingEnabled(true);
		RTextScrollPane sp = new RTextScrollPane(bpmnTextArea);
		sp.setFoldIndicatorEnabled(true);

		return sp;
	}

	protected Component createSearchPanel() {
		JPanel sp = new JPanel();
		sp.setLayout(new BoxLayout(sp, BoxLayout.X_AXIS));

		Border emptyb = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		sp.setBorder(emptyb);

		JLabel jl = new JLabel(Resources.get("find") + ": ");

		searchCB.setEditable(true);

		Dimension comboBoxDimension = new Dimension(200, 25);
		searchCB.setMinimumSize(new Dimension(comboBoxDimension));
		searchCB.setMaximumSize(new Dimension(comboBoxDimension));
		searchCB.setPreferredSize(new Dimension(comboBoxDimension));

		JButton prevBtn = new JButton(Resources.get("previous"));
		prevBtn.setToolTipText(Resources.get("previous"));

		final JButton nextBtn = new JButton(Resources.get("next"));
		nextBtn.setToolTipText(Resources.get("next"));
		sp.add(jl);
		sp.add(searchCB);
		sp.add(Box.createHorizontalStrut(5));
		sp.add(prevBtn);
		sp.add(Box.createHorizontalStrut(5));
		sp.add(nextBtn);
		sp.add(Box.createHorizontalStrut(5));
		sp.add(caseCB);
		sp.add(Box.createHorizontalStrut(5));
		sp.add(wordCB);
		sp.add(Box.createHorizontalStrut(5));
		sp.add(regexCB);

		searchCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox jcb = (JComboBox) e.getSource();
				if (jcb.getSelectedItem().toString().length() > 0) {
					nextBtn.doClick(0);
				}
			}
		});

		prevBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				findString(false);
			}
		});

		nextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				findString(true);
			}
		});

		return sp;
	}

	protected void findString(boolean forward) {
		if (searchCB.getSelectedItem() == null)
			return;
		String searchingTxt = searchCB.getSelectedItem().toString();
		addItemToCombo(searchingTxt);

		SearchContext context = new SearchContext();
		context.setSearchFor(searchingTxt);
		context.setMatchCase(caseCB.isSelected());
		context.setRegularExpression(regexCB.isSelected());
		context.setSearchForward(forward);
		context.setWholeWord(wordCB.isSelected());

		boolean found = SearchEngine.find(bpmnTextArea, context);
		if (!found) {
			bpmnTextArea.setCaretPosition(0);
			JOptionPane.showMessageDialog(null, Resources.get("searchFinished"), Resources.get("optionTitle"), JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void addItemToCombo(String searchingTxt) {
		if (searchingTxt == null || searchingTxt.trim().length() == 0)
			return;
		for (int i = 0; i < searchCB.getItemCount(); i++) {
			if (searchCB.getItemAt(i).toString().equals(searchingTxt)) {
				return;
			}
		}
		searchCB.addItem(searchingTxt);
	}

	public void refreshView(Graph graph) {
		String toDisp = "";
		bpmnTextArea.setText(toDisp);

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			try {
			tFactory.setAttribute("indent-number", new Integer(2));
			} catch (IllegalArgumentException e) {
				//ignore
			}
			transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, System.getProperty("file.encoding"));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(bpmnCodec.encode());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(new OutputStreamWriter(baos, "utf-8"));
			transformer.transform(source, result);

			toDisp = baos.toString("UTF-8");
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.toString(), Resources.get("error"), JOptionPane.ERROR_MESSAGE);
		}
		bpmnTextArea.setText(toDisp);
		bpmnTextArea.setCaretPosition(0);
	}
}
