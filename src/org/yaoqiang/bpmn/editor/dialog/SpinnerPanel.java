package org.yaoqiang.bpmn.editor.dialog;

import java.awt.Dimension;
import java.util.Calendar;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * SpinnerPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class SpinnerPanel extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;

	protected JSpinner spinner;

	protected int min;
	protected int max;
	protected int value;

	public SpinnerPanel() {
		this("", 0, 0, Integer.MAX_VALUE, 50, 27);
	}

	public SpinnerPanel(String name, int value, int width) {
		this(name, value, 0, Integer.MAX_VALUE, width, 27);
	}

	public SpinnerPanel(String name, int value, int min, int width) {
		this(name, value, min, Integer.MAX_VALUE, width, 27);
	}

	public SpinnerPanel(int value, int min, int max, int width) {
		this("", value, min, max, width, 27);
	}

	public SpinnerPanel(String name, int value, int min, int max, int width) {
		this(name, value, min, max, width, 27);
	}

	public SpinnerPanel(String name, int value, int min, int max, int width, int height) {
		this.setName(name);
		Dimension dim = new Dimension(width, height);

		this.min = min;
		if (max < min) {
			max = min;
		}
		this.max = max;

		this.value = value;
		if (value < min) {
			value = min;
		} else if (value > max) {
			value = max;
		}
		SpinnerModel spinnerModel = null;
		if (name.equals("year")) {
			spinnerModel = new SpinnerDateModel();
		} else if (name.equals("time")) {
			spinnerModel = new SpinnerDateModel();
		} else {
			spinnerModel = new SpinnerNumberModel(value, min, max, 1);
		}
		spinner = new JSpinner(spinnerModel);
		if (name.equals("year")) {
			JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy");
			spinner.setEditor(editor);
		} else if (name.equals("time")) {
			JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm:ss");
			spinner.setEditor(editor);
		}
		spinner.setPreferredSize(dim);
		spinner.addChangeListener(this);
		add(spinner);

	}

	public Object getValue() {
		return getSpinner().getValue();
	}

	public void setValue(int value) {
		Object obj = null;
		if (getName().equals("year")) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, value);
			obj = calendar.getTime();
		} else {
			obj = Integer.valueOf(value);
		}
		getSpinner().setValue(obj);
	}

	public final JSpinner getSpinner() {
		return spinner;
	}

	public void stateChanged(ChangeEvent e) {
		String name = getName();
		if (name.length() != 0) {
			firePropertyChange(name, null, getValue());
		}
	}

}
