package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.yaoqiang.dialog.Panel;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Resources;

/**
 * ComboPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ComboPanel extends Panel {

	private static final long serialVersionUID = 1L;

	Dimension textDim = new Dimension(120, 27);

	protected String key;

	protected JComboBox jcb;

	public ComboPanel(PanelContainer pc, Object owner, String key, Collection<?> choices, boolean hasEmpty, boolean isEditable, boolean isEnabled) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.key = key;

		JLabel jl = new JLabel(Resources.get(key) + ": ");

		List<Object> chs = new ArrayList<Object>(choices);
		jcb = new JComboBox(new Vector<Object>(chs));
		resetComboDimension(chs);

		jcb.setEditable(isEditable);
		jcb.setEnabled(isEnabled);

		jcb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				getPanelContainer().panelChanged();
			}

		});

		if (isEditable) {
			jcb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					getPanelContainer().panelChanged();
				}
			});

			jcb.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					getPanelContainer().panelChanged();
				}
			});
		}

		this.add(jl, BorderLayout.WEST);
		this.add(Box.createHorizontalGlue(), BorderLayout.EAST);
		this.add(jcb, BorderLayout.CENTER);

	}

	public void saveObjects() {

	}

	public Object getSelectedItem() {
		Object obj = null;
		if (jcb.isEditable()) {
			obj = jcb.getEditor().getItem();
		} else {
			obj = jcb.getSelectedItem();
		}
		if (obj != null) {
			return obj.toString();
		}
		return "";
	}

	public int getSelectedIndex() {
		return jcb.getSelectedIndex();
	}

	public void resetComboDimension(Collection<?> choices) {
		double w = 0;
		if (choices != null) {
			double longest = 0;
			for (Object ch : choices) {
				w = getFontMetrics(getFont()).stringWidth(ch.toString());
				if (w > longest) {
					longest = w;
				}
			}
			w = longest + 25;
		}
		if (w < textDim.width)
			w = textDim.width;

		Dimension dim = new Dimension((int) w, textDim.height);
		jcb.setMinimumSize(dim);
		jcb.setMaximumSize(dim);
		jcb.setPreferredSize(dim);
	}

	public JComboBox getComboBox() {
		return jcb;
	}

	public void setSelectedItem(String item) {
		jcb.setSelectedItem(item);
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jcb.setEnabled(b);
	}

	public void requestFocus() {
		jcb.requestFocus();
	}

	public void addActionListener(ActionListener l) {
		jcb.addActionListener(l);
	}

	public boolean isEmpty() {
		Object o = getSelectedItem();
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			return ((String) o).trim().equals("");
		}
		return false;
	}

}
