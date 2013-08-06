package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.SpinnerPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.date.CalendarPanel;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.util.Resources;


/**
 * TimeIntervalPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class TimeIntervalPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected SpinnerPanel yearPanel = new SpinnerPanel(0, 0, 99, 45);
	protected SpinnerPanel monthPanel = new SpinnerPanel(0, 0, 12, 45);
	protected SpinnerPanel dayPanel = new SpinnerPanel(0, 0, 30, 45);
	protected SpinnerPanel hourPanel = new SpinnerPanel(0, 0, 24, 45);
	protected SpinnerPanel minutePanel = new SpinnerPanel(0, 0, 60, 45);
	protected SpinnerPanel secondPanel = new SpinnerPanel(0, 0, 60, 45);
	protected JTextField sf;
	protected JTextField ef;

	protected boolean dateSelected;

	public TimeIntervalPanel(final BPMNPanelContainer pc, XMLElement owner) {
		this(pc, owner, true);
	}

	public TimeIntervalPanel(final BPMNPanelContainer pc, XMLElement owner, boolean enabled) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pc.panelChanged();
			}
		};

		yearPanel.getSpinner().addChangeListener(changeListener);
		monthPanel.getSpinner().addChangeListener(changeListener);
		dayPanel.getSpinner().addChangeListener(changeListener);
		hourPanel.getSpinner().addChangeListener(changeListener);
		minutePanel.getSpinner().addChangeListener(changeListener);
		secondPanel.getSpinner().addChangeListener(changeListener);

		JLabel pl = new JLabel(Resources.get("duration") + ": ");
		JLabel yearLabel = new JLabel(Resources.get("year") + ",");
		JLabel monthLabel = new JLabel(Resources.get("month") + ",");
		JLabel dayLabel = new JLabel(Resources.get("day") + ",");
		JLabel hourLabel = new JLabel(Resources.get("hour") + ",");
		JLabel minuteLabel = new JLabel(Resources.get("minute") + ",");
		JLabel secondLabel = new JLabel(Resources.get("second"));
		JPanel durationPanel = new JPanel();
		durationPanel.setLayout(new BoxLayout(durationPanel, BoxLayout.X_AXIS));
		durationPanel.add(pl);
		durationPanel.add(yearPanel);
		durationPanel.add(yearLabel);
		durationPanel.add(monthPanel);
		durationPanel.add(monthLabel);
		durationPanel.add(dayPanel);
		durationPanel.add(dayLabel);
		durationPanel.add(hourPanel);
		durationPanel.add(hourLabel);
		durationPanel.add(minutePanel);
		durationPanel.add(minuteLabel);
		durationPanel.add(secondPanel);
		durationPanel.add(secondLabel);
		this.add(durationPanel);

		final JPopupMenu popup = new JPopupMenu() {
			private static final long serialVersionUID = 1L;

			public void setVisible(boolean b) {
				Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
				if (b || (!b && dateSelected) || ((isCanceled != null) && !b && isCanceled.booleanValue())) {
					super.setVisible(b);
				}
			}
		};
		final CalendarPanel jcalendar = new CalendarPanel(null, BPMNEditor.locale);
		jcalendar.getDayChooser().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("day")) {
					dateSelected = true;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					String dateString = sdf.format(jcalendar.getDate());
					if (jcalendar.getDate().getTime() == 0) {
						dateString = "";
						jcalendar.setDate(new Date());
					}
					String target = popup.getInvoker().getName();
					if (target.equals("start")) {
						sf.setText(dateString);
					} else {
						ef.setText(dateString);
					}
					popup.setVisible(false);
					pc.panelChanged();
				}
			}
		});

		popup.add(jcalendar);

		Action dateTimeChooserAction = new AbstractAction("dateTimeChooser") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				JButton calendarButton = (JButton) ae.getSource();
				int x = calendarButton.getWidth() - (int) popup.getPreferredSize().getWidth();
				int y = calendarButton.getY() + calendarButton.getHeight();
				popup.show(calendarButton, x, y);
				dateSelected = false;
			}
		};

		JLabel sl = new JLabel(Resources.get("start") + ": ");
		sf = new JTextField();
		sf.setMaximumSize(new Dimension(140, 26));
		sf.setPreferredSize(new Dimension(140, 26));
		sf.setEditable(false);
		JButton startButton = new JButton(new ImageIcon(TimeIntervalPanel.class.getResource("/org/yaoqiang/bpmn/editor/images/calendar.gif")));
		startButton.setName("start");
		startButton.setMaximumSize(new Dimension(31, 27));
		startButton.setPreferredSize(new Dimension(31, 27));
		startButton.setToolTipText(Resources.get("selectDateTime"));
		startButton.addActionListener(dateTimeChooserAction);
		JLabel el = new JLabel(Resources.get("end") + ": ");
		ef = new JTextField();
		ef.setMaximumSize(new Dimension(140, 26));
		ef.setPreferredSize(new Dimension(140, 26));
		ef.setEditable(false);
		JButton endButton = new JButton(new ImageIcon(TimeIntervalPanel.class.getResource("/org/yaoqiang/bpmn/editor/images/calendar.gif")));
		endButton.setName("end");
		endButton.setMaximumSize(new Dimension(31, 27));
		endButton.setPreferredSize(new Dimension(31, 27));
		endButton.setToolTipText(Resources.get("selectDateTime"));
		endButton.addActionListener(dateTimeChooserAction);
		JPanel startEndPanel = new JPanel();
		startEndPanel.setLayout(new BoxLayout(startEndPanel, BoxLayout.X_AXIS));
		startEndPanel.add(sl);
		startEndPanel.add(sf);
		startEndPanel.add(startButton);
		startEndPanel.add(Box.createHorizontalStrut(20));
		startEndPanel.add(el);
		startEndPanel.add(ef);
		startEndPanel.add(endButton);
		startEndPanel.add(Box.createHorizontalStrut(150));
		startEndPanel.add(Box.createHorizontalGlue());
		this.add(startEndPanel);

	}

	public void saveObjects() {
		EventDefinitionPanel parentPanel = (EventDefinitionPanel) getParentPanel();
		String expr = buildExpression();
		if (expr.length() != 0) {
			parentPanel.getExpressionTextPanel().setText(expr);
		}
	}

	public String buildExpression() {
		StringBuilder duration = new StringBuilder("P");
		String year = yearPanel.getSpinner().getModel().getValue().toString();
		if (!year.equals("0")) {
			duration.append(year);
			duration.append("Y");
		}
		String month = monthPanel.getSpinner().getModel().getValue().toString();
		if (!month.equals("0")) {
			duration.append(month);
			duration.append("M");
		}
		String day = dayPanel.getSpinner().getModel().getValue().toString();
		if (!day.equals("0")) {
			duration.append(day);
			duration.append("D");
		}
		duration.append("T");
		String hour = hourPanel.getSpinner().getModel().getValue().toString();
		if (!hour.equals("0")) {
			duration.append(hour);
			duration.append("H");
		}
		String minute = minutePanel.getSpinner().getModel().getValue().toString();
		if (!minute.equals("0")) {
			duration.append(minute);
			duration.append("M");
		}
		String second = secondPanel.getSpinner().getModel().getValue().toString();
		if (!second.equals("0")) {
			duration.append(second);
			duration.append("S");
		}
		if (duration.toString().endsWith("T")) {
			duration.deleteCharAt(duration.length() - 1);
		}
		if (duration.toString().endsWith("P")) {
			duration.deleteCharAt(duration.length() - 1);
		}

		String expression = duration.toString();
		String start = sf.getText();
		String end = ef.getText();
		if (expression.length() == 0) {
			if (start.length() == 0 || end.length() == 0) {
				JOptionPane.showMessageDialog(null, "Invalid time interval!", Resources.get("Warning"), JOptionPane.WARNING_MESSAGE);
				return "";
			} else {
				expression = start + "/" + end;
			}
		} else {
			if (start.length() != 0 && end.length() != 0) {
				JOptionPane.showMessageDialog(null, "Invalid time interval!", Resources.get("Warning"), JOptionPane.WARNING_MESSAGE);
				return "";
			} else {
				if (start.length() != 0) {
					expression = start + "/" + expression;
				} else if (end.length() != 0) {
					expression = expression + "/" + end;
				}
			}
		}

		return expression;
	}

}
