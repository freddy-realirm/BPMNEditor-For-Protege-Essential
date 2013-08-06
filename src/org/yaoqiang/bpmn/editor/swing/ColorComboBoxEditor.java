package org.yaoqiang.bpmn.editor.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;

import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.util.GraphUtils;
import org.yaoqiang.util.Resources;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;

/**
 * ColorComboBoxEditor
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ColorComboBoxEditor implements ComboBoxEditor {

	final protected JButton editor;

	public ColorComboBoxEditor(final GraphComponent graphComponent, Color initialColor) {
		editor = new JButton("");
		editor.setToolTipText(Resources.get("fillColor"));
		editor.setBackground(initialColor);
		editor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = (Color) getItem();
				if (color != null) {
					graphComponent.getGraph().setCellStyles(mxConstants.STYLE_FILLCOLOR, mxUtils.hexString(color));
					GraphUtils.setElementStyles(graphComponent.getGraph(), mxConstants.STYLE_FILLCOLOR);
					graphComponent.requestFocusInWindow();
				}
			}
		});
	}

	public void addActionListener(ActionListener l) {
	}

	public Component getEditorComponent() {
		return editor;
	}

	public Object getItem() {
		return editor.getBackground();
	}

	public void removeActionListener(ActionListener l) {
	}

	public void selectAll() {
	}

	public void setItem(Object newValue) {
		editor.setBackground((Color) newValue);
	}

}
