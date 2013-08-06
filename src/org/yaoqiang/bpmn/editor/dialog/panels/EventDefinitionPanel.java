package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.XMLCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLMultiLineTextPanel;
import org.yaoqiang.bpmn.editor.dialog.BPMNPanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLElementView;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.date.CalendarPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLCollection;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLEmptyElement;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.core.common.Expression;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.events.CompensateEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.ConditionalEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.ErrorEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.EscalationEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.events.EventDefinition;
import org.yaoqiang.bpmn.model.elements.events.EventDefinitions;
import org.yaoqiang.bpmn.model.elements.events.LinkEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.MessageEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.SignalEventDefinition;
import org.yaoqiang.bpmn.model.elements.events.ThrowEvent;
import org.yaoqiang.bpmn.model.elements.events.TimerEventDefinition;
import org.yaoqiang.util.Resources;


/**
 * EventDefinitionPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class EventDefinitionPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	private XMLPanel source = this;

	protected String type;
	protected boolean hasRef;
	protected XMLTextPanel idPanel;
	protected XMLComboPanel eventDefRefPanel;
	protected Object eventDefRef;

	protected XMLComboPanel typePanel;
	protected XMLPanel expr;
	protected JButton btn;
	protected boolean dateSelected;

	protected XMLComboPanel refPanel;
	protected XMLCheckboxPanel booleanPanel;

	public EventDefinitionPanel(final BPMNPanelContainer pc, final EventDefinition owner, final boolean hasRef) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		type = owner.toName();
		this.hasRef = hasRef;
		idPanel = new XMLTextPanel(pc, owner.get("id"), false);
		List<XMLElement> choices = BPMNModelUtils.getDefinitions(owner).getEventDefinitions(type);
		if (owner.getParent().getParent() instanceof Event) {
			List<XMLElement> refList = ((Event) owner.getParent().getParent()).getRefEventDefinitionList();
			choices.removeAll(refList);
			if (refList.contains(owner)) {
				choices.add(owner);
			}
		}
		eventDefRefPanel = new XMLComboPanel(pc, owner, "eventDefRef", choices, true, false, true);
		eventDefRefPanel.setEnabled(hasRef);
		eventDefRef = eventDefRefPanel.getSelectedItem();
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				XMLElementView sel = (XMLElementView) ((JComboBox) ae.getSource()).getSelectedItem();
				if (sel.getElement() instanceof EventDefinition) {
					EventDefinition ed = (EventDefinition) sel.getElement();
					if (ed instanceof TimerEventDefinition) {
						XMLElementView chn = new XMLElementView(((TimerEventDefinition) ed).getTimerTypes().getChoosen(), XMLElementView.TONAME);
						typePanel.getComboBox().removeAllItems();
						Object[] items = typePanel.convertToXMLElementViewList(((TimerEventDefinition) ed).getTimerTypes().getChoices(), false).toArray();
						typePanel.getComboBox().setModel(new DefaultComboBoxModel(items));
						typePanel.getComboBox().setSelectedItem(chn);
						((XMLTextPanel) expr).setText(((TimerEventDefinition) ed).getTimerTypes().getChoosen().toValue());

						typePanel.setEnabled(false);
						((XMLTextPanel) expr).setEnabled(false);
						btn.setEnabled(false);
					} else if (ed instanceof ConditionalEventDefinition) {
						((XMLMultiLineTextPanel) expr).setText(((ConditionalEventDefinition) ed).getConditionString());

						((XMLMultiLineTextPanel) expr).setEnabled(false);
					} else if (ed instanceof CompensateEventDefinition) {
						XMLElement parent = owner.getParent().getParent();
						if (parent instanceof ThrowEvent) {
							XMLElementView chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
							refPanel.getComboBox().setSelectedItem(chn);
							booleanPanel.setSelected(true);

							refPanel.setEnabled(false);
							booleanPanel.setEnabled(false);
						}
					} else if (ed instanceof ErrorEventDefinition) {
						String itemId = ((ErrorEventDefinition) ed).getErrorRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(ed).getRootElement(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						refPanel.setEnabled(false);
					} else if (ed instanceof SignalEventDefinition) {
						String itemId = ((SignalEventDefinition) ed).getSignalRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(ed).getRootElement(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						refPanel.setEnabled(false);
					} else if (ed instanceof EscalationEventDefinition) {
						String itemId = ((EscalationEventDefinition) ed).getEscalationRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(ed).getRootElement(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						refPanel.setEnabled(false);
					} else if (ed instanceof MessageEventDefinition) {
						String itemId = ((MessageEventDefinition) ed).getMessageRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(ed).getRootElement(itemId), XMLElementView.TONAME);
						}
						typePanel.getComboBox().setSelectedItem(chn);
						itemId = ((MessageEventDefinition) ed).getOperationRef();
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(ed).getOperation(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						typePanel.setEnabled(false);
						refPanel.setEnabled(false);
					}
				} else {
					if (type.equals(EventDefinitions.TYPE_TIMER_EVENT_DEFINITION)) {
						typePanel.getComboBox().removeAllItems();
						Object[] items = typePanel.convertToXMLElementViewList(((TimerEventDefinition) owner).getTimerTypes().getChoices(), false).toArray();
						XMLElementView chn = new XMLElementView(((TimerEventDefinition) owner).getTimerTypes().getChoosen(), XMLElementView.TONAME);
						typePanel.getComboBox().setModel(new DefaultComboBoxModel(items));
						typePanel.getComboBox().setSelectedItem(chn);
						((XMLTextPanel) expr).setText(((TimerEventDefinition) owner).getTimerTypes().getChoosen().toValue());

						typePanel.setEnabled(true);
						((XMLTextPanel) expr).setEnabled(true);
						btn.setEnabled(true);
					} else if (type.equals(EventDefinitions.TYPE_CONDITIONAL_EVENT_DEFINITION)) {
						((XMLMultiLineTextPanel) expr).setText(((ConditionalEventDefinition) owner).getConditionString());

						((XMLMultiLineTextPanel) expr).setEnabled(true);
					} else if (type.equals(EventDefinitions.TYPE_COMPENSATE_EVENT_DEFINITION)) {
						XMLElement parent = owner.getParent().getParent();
						if (parent instanceof ThrowEvent) {
							String actId = ((CompensateEventDefinition) owner).getActivityRef();
							XMLElementView chn = null;
							if (actId.length() == 0) {
								chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
							} else {
								chn = new XMLElementView(BPMNModelUtils.getDefinitions(owner).getRootElement(actId), XMLElementView.TONAME);
							}
							refPanel.getComboBox().setSelectedItem(chn);
							booleanPanel.setSelected(((CompensateEventDefinition) owner).waitForCompletion());

							refPanel.setEnabled(true);
							booleanPanel.setEnabled(true);
						}
					} else if (type.equals(EventDefinitions.TYPE_ERROR_EVENT_DEFINITION)) {
						String itemId = ((ErrorEventDefinition) owner).getErrorRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(owner).getRootElement(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						refPanel.setEnabled(true);
					} else if (type.equals(EventDefinitions.TYPE_SIGNAL_EVENT_DEFINITION)) {
						String itemId = ((SignalEventDefinition) owner).getSignalRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(owner).getRootElement(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						refPanel.setEnabled(true);
					} else if (type.equals(EventDefinitions.TYPE_ESCALATION_EVENT_DEFINITION)) {
						String itemId = ((EscalationEventDefinition) owner).getEscalationRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(owner).getRootElement(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						refPanel.setEnabled(true);
					} else if (type.equals(EventDefinitions.TYPE_MESSAGE_EVENT_DEFINITION)) {
						String itemId = ((MessageEventDefinition) owner).getMessageRef();
						XMLElementView chn = null;
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(owner).getRootElement(itemId), XMLElementView.TONAME);
						}
						typePanel.getComboBox().setSelectedItem(chn);
						itemId = ((MessageEventDefinition) owner).getOperationRef();
						if (itemId.length() == 0) {
							chn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
						} else {
							chn = new XMLElementView(BPMNModelUtils.getDefinitions(owner).getOperation(itemId), XMLElementView.TONAME);
						}
						refPanel.getComboBox().setSelectedItem(chn);

						typePanel.setEnabled(true);
						refPanel.setEnabled(true);
					}
				}
			}
		};
		eventDefRefPanel.addActionListener(al);

		if (type.equals(EventDefinitions.TYPE_TIMER_EVENT_DEFINITION)) {
			final TimerEventDefinition timerEventDef = (TimerEventDefinition) owner;
			typePanel = new XMLComboPanel(pc, timerEventDef.getTimerTypes(), "type", timerEventDef.getTimerTypes().getChoices(), false, false, true);
			typePanel.setMaximumSize(new Dimension(130, 36));
			JPanel typeDefPanel = new JPanel();
			typeDefPanel.setLayout(new BoxLayout(typeDefPanel, BoxLayout.X_AXIS));
			typeDefPanel.add(typePanel);
			typeDefPanel.add(Box.createHorizontalGlue());
			if (hasRef) {
				typeDefPanel.add(eventDefRefPanel);
			} else {
				typeDefPanel.add(idPanel);
			}
			this.add(typeDefPanel);

			final JPopupMenu popup = new JPopupMenu() {
				private static final long serialVersionUID = 1L;

				public void setVisible(boolean b) {
					Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
					if (b || (!b && dateSelected) || ((isCanceled != null) && !b && isCanceled.booleanValue())) {
						super.setVisible(b);
					}
				}
			};
			final CalendarPanel calendar = new CalendarPanel(null, BPMNEditor.locale);
			calendar.getDayChooser().addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("day")) {
						dateSelected = true;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						String dateString = sdf.format(calendar.getDate());
						if (calendar.getDate().getTime() == 0) {
							dateString = "";
							calendar.setDate(new Date());
						}
						((XMLTextPanel) expr).setText(dateString);
						popup.setVisible(false);
						pc.panelChanged();
					}
				}
			});
			popup.add(calendar);

			btn = createBuilderButton(new AbstractAction("expressionBuilder") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent ae) {
					if (((Expression) typePanel.getSelectedItem()).toName().equals("timeDate")) {
						JButton calendarButton = (JButton) ae.getSource();
						int x = calendarButton.getWidth() - (int) popup.getPreferredSize().getWidth();
						int y = calendarButton.getY() + calendarButton.getHeight();
						popup.show(calendarButton, x, y);
						dateSelected = false;
					} else {
						getEditor().createBpmnElementDialog().initDialog().editBPMNElement(source, typePanel.getSelectedItem());
					}
				}
			});

			expr = new XMLTextPanel(pc, timerEventDef, "expression", timerEventDef.getTimerTypes().getChoosen().toValue());
			JPanel exprPanel = new JPanel();
			exprPanel.setLayout(new BoxLayout(exprPanel, BoxLayout.X_AXIS));
			exprPanel.add(expr);
			exprPanel.add(Box.createHorizontalGlue());
			exprPanel.add(btn);
			this.add(exprPanel);
		} else if (type.equals(EventDefinitions.TYPE_CONDITIONAL_EVENT_DEFINITION)) {
			expr = new XMLMultiLineTextPanel(pc, ((ConditionalEventDefinition) owner).getConditionExpression(), "expression", 300, 90);
			this.add(idPanel);
			if (hasRef) {
				this.add(eventDefRefPanel);
			}
			this.add(expr);
		} else if (type.equals(EventDefinitions.TYPE_COMPENSATE_EVENT_DEFINITION)) {
			this.add(idPanel);
			if (hasRef) {
				this.add(eventDefRefPanel);
				XMLElement parent = owner.getParent().getParent();
				if (parent instanceof ThrowEvent) {
					FlowElements container = (FlowElements) parent.getParent();
					if (container.getParent() instanceof SubProcess && ((SubProcess) container.getParent()).isTriggeredByEvent()) {
						container = (FlowElements) container.getParent().getParent();
					}
					refPanel = new XMLComboPanel(pc, owner.get("activityRef"), "activityRef", container.getActivitiesForCompensation(), true, false, true);
					booleanPanel = new XMLCheckboxPanel(pc, owner.get("waitForCompletion"), true);
					this.add(refPanel);
					this.add(booleanPanel);
				}
			}
		} else if (type.equals(EventDefinitions.TYPE_ERROR_EVENT_DEFINITION)) {
			final ErrorEventDefinition errorEventDef = (ErrorEventDefinition) owner;
			refPanel = new XMLComboPanel(pc, errorEventDef.get("errorRef"), "errorRef", BPMNModelUtils.getDefinitions(owner).getErrors(), true, false, true);
			this.add(idPanel);
			if (hasRef) {
				this.add(eventDefRefPanel);
			}
			this.add(refPanel);
		} else if (type.equals(EventDefinitions.TYPE_SIGNAL_EVENT_DEFINITION)) {
			final SignalEventDefinition signalEventDef = (SignalEventDefinition) owner;
			refPanel = new XMLComboPanel(pc, signalEventDef.get("signalRef"), "signalRef", BPMNModelUtils.getDefinitions(owner).getSignals(), true, false, true);
			this.add(idPanel);
			if (hasRef) {
				this.add(eventDefRefPanel);
			}
			this.add(refPanel);
		} else if (type.equals(EventDefinitions.TYPE_ESCALATION_EVENT_DEFINITION)) {
			final EscalationEventDefinition escalationEventDef = (EscalationEventDefinition) owner;
			refPanel = new XMLComboPanel(pc, escalationEventDef.get("escalationRef"), "escalationRef", BPMNModelUtils.getDefinitions(owner).getEscalations(),
					true, false, true);
			this.add(idPanel);
			if (hasRef) {
				this.add(eventDefRefPanel);
			}
			this.add(refPanel);
		} else if (type.equals(EventDefinitions.TYPE_CANCEL_EVENT_DEFINITION) || type.equals(EventDefinitions.TYPE_TERMINATE_EVENT_DEFINITION)) {
			this.add(idPanel);
			if (hasRef) {
				this.add(eventDefRefPanel);
			}
		} else if (type.equals(EventDefinitions.TYPE_LINK_EVENT_DEFINITION)) {
			expr = new XMLTextPanel(pc, owner.get("target"), false);
			this.add(idPanel);
			this.add(new XMLTextPanel(pc, owner.get("name"), false));
			if (owner.getParent().getParent() instanceof ThrowEvent) {
				this.add(expr);
			} else {
				List<String> columnsToShow = new ArrayList<String>();
				columnsToShow.add("source");
				this.add(new XMLTablePanel(getPanelContainer(), ((LinkEventDefinition) owner).getSources(), "", "sources", columnsToShow, ((LinkEventDefinition) owner)
						.getSourceList(), 150, 80, false, true));
			}
		} else if (type.equals(EventDefinitions.TYPE_MESSAGE_EVENT_DEFINITION)) {
			final MessageEventDefinition messageEventDef = (MessageEventDefinition) owner;
			typePanel = new XMLComboPanel(pc, messageEventDef.get("messageRef"), "messageRef", BPMNModelUtils.getDefinitions(owner).getMessages(), true, false,
					true);
			refPanel = new XMLComboPanel(pc, messageEventDef.get("operationRef"), "operationRef", BPMNModelUtils.getDefinitions(owner).getOperations(), true,
					false, true);
			this.add(idPanel);
			if (hasRef) {
				this.add(eventDefRefPanel);
			}
			this.add(typePanel);
			this.add(refPanel);
		}

		if (hasRef && !(eventDefRef instanceof XMLEmptyElement)) {
			if (type.equals(EventDefinitions.TYPE_TIMER_EVENT_DEFINITION)) {
				typePanel.setEnabled(false);
				((XMLTextPanel) expr).setEnabled(false);
				btn.setEnabled(false);
			} else if (type.equals(EventDefinitions.TYPE_CONDITIONAL_EVENT_DEFINITION)) {
				((XMLMultiLineTextPanel) expr).setEnabled(false);
			} else if (type.equals(EventDefinitions.TYPE_ERROR_EVENT_DEFINITION) || type.equals(EventDefinitions.TYPE_SIGNAL_EVENT_DEFINITION)
					|| type.equals(EventDefinitions.TYPE_ESCALATION_EVENT_DEFINITION)) {
				refPanel.setEnabled(false);
			} else if (type.equals(EventDefinitions.TYPE_MESSAGE_EVENT_DEFINITION)) {
				typePanel.setEnabled(false);
				refPanel.setEnabled(false);
			} else if (type.equals(EventDefinitions.TYPE_COMPENSATE_EVENT_DEFINITION)) {
				XMLElement parent = owner.getParent().getParent();
				if (parent instanceof ThrowEvent) {
					booleanPanel.setEnabled(false);
					refPanel.setEnabled(false);
				}
			}
		}
	}

	public final XMLComboPanel getEventDefRefPanel() {
		return eventDefRefPanel;
	}

	public final XMLTextPanel getExpressionTextPanel() {
		return (XMLTextPanel) expr;
	}

	public void saveObjects() {
		Object edRef = eventDefRefPanel.getSelectedItem();
		Event event = null;
		XMLElement parent = getOwner().getParent().getParent();
		if (parent instanceof Event) {
			event = (Event) parent;
			String id = ((EventDefinition) owner).getId();
			event.getEventDefinitionRefs().remove(id);
		}

		XMLPanel parentPanel = getParentPanel();
		if (parentPanel == null) {
			event.getEventDefinitions().clear();
		}

		if (edRef instanceof XMLEmptyElement || !hasRef) {
			if (parent instanceof Event) {
				event.addEventDefinition((EventDefinition) owner);
			} else if (parent instanceof Definitions) {
				((XMLCollection) getOwner().getParent()).add(getOwner());
			}
			if (type.equals(EventDefinitions.TYPE_TIMER_EVENT_DEFINITION)) {
				typePanel.saveObjects();
				((Expression) typePanel.getSelectedItem()).setValue(((XMLTextPanel) expr).getText());
			} else if (type.equals(EventDefinitions.TYPE_CONDITIONAL_EVENT_DEFINITION)) {
				((ConditionalEventDefinition) owner).setConditionExpression(((XMLMultiLineTextPanel) expr).getText());
			} else if (type.equals(EventDefinitions.TYPE_ERROR_EVENT_DEFINITION) || type.equals(EventDefinitions.TYPE_SIGNAL_EVENT_DEFINITION)
					|| type.equals(EventDefinitions.TYPE_ESCALATION_EVENT_DEFINITION)) {
				refPanel.saveObjects();
			} else if (type.equals(EventDefinitions.TYPE_MESSAGE_EVENT_DEFINITION)) {
				typePanel.saveObjects();
				refPanel.saveObjects();
			} else if (type.equals(EventDefinitions.TYPE_COMPENSATE_EVENT_DEFINITION)) {
				if (parent instanceof ThrowEvent) {
					booleanPanel.saveObjects();
					refPanel.saveObjects();
				}
			}
		} else {
			((XMLCollection) getOwner().getParent()).remove(((EventDefinition) owner).getId());
			if (parent instanceof Event) {
				event.addEventDefinitionRef(((EventDefinition) edRef).getId());
			}
		}

		if (parentPanel != null) {
			if (edRef instanceof XMLEmptyElement || !hasRef) {
				((XMLTablePanel) parentPanel).addRow(getOwner());
			} else {
				EventDefinition ed = (EventDefinition) ((EventDefinition) edRef).clone();
				ed.setParent(getOwner().getParent());
				int pos = ((XMLTablePanel) parentPanel).getElementRow(owner);
				if (pos != -1) {
					((XMLTablePanel) parentPanel).addRow(ed, pos);
				} else {
					((XMLTablePanel) parentPanel).addRow(ed);
				}
			}
		}
	}

	public JButton createBuilderButton(Action a) {
		String actionName = (String) a.getValue(Action.NAME);
		ImageIcon curIc = new ImageIcon(EventDefinitionPanel.class.getResource("/org/yaoqiang/bpmn/editor/images/calendar.gif"));
		JButton b = new JButton(curIc);
		b.setMaximumSize(new Dimension(31, 27));
		b.setPreferredSize(new Dimension(31, 27));
		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(Resources.get(actionName));
		return b;
	}
}
