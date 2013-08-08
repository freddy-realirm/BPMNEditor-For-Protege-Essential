package org.yaoqiang.bpmn.editor;

import java.awt.Frame;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/**
 * BPMNEditorApplet
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNEditorApplet extends JApplet {

	private static final long serialVersionUID = 1L;

	public void init() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "A java policy needs to be set up to allow the Yaoqiang BPMN Editor applet to run on your computer. \n Visit http://bpmn.sf.net for more information.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void createGUI() {
		BPMNEditor editor = new BPMNEditor(false);
		setContentPane(editor);
		editor.createGraphOverviewWindow((Frame) SwingUtilities.windowForComponent(this), editor.getGraphOverviewComponent());
		String filepath = this.getParameter("filepath");
		if (filepath != null) {
			editor.openFile(filepath);
		}
	}
	
}
