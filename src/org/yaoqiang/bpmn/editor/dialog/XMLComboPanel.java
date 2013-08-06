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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLAttribute;
import org.yaoqiang.bpmn.model.elements.XMLComplexChoice;
import org.yaoqiang.bpmn.model.elements.XMLComplexElement;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLEmptyElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;
import org.yaoqiang.bpmn.model.elements.XMLTextElement;
import org.yaoqiang.bpmn.model.elements.activities.ResourceParameterBinding;
import org.yaoqiang.bpmn.model.elements.activities.ScriptTask;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElements;
import org.yaoqiang.bpmn.model.elements.core.common.ItemDefinition;
import org.yaoqiang.bpmn.model.elements.core.common.ResourceParameter;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.events.EventDefinition;
import org.yaoqiang.dialog.PanelContainer;
import org.yaoqiang.util.Resources;


/**
 * XMLComboPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLComboPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	Dimension textDim = new Dimension(120, 27);

	protected JComboBox jcb;

	public XMLComboPanel(PanelContainer pc, XMLElement owner, List<?> choices, boolean hasEmpty, boolean isEditable, boolean isEnabled) {
		this(pc, owner, null, choices, hasEmpty, isEditable, isEnabled);
	}

	public XMLComboPanel(PanelContainer pc, XMLElement owner, String title, List<?> choices, boolean hasEmpty, boolean isEditable, boolean isEnabled) {

		super(pc, owner);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		Definitions defs = BPMNModelUtils.getDefinitions(owner);

		JLabel jl = new JLabel(Resources.get(title == null ? getOwner().toName() : title) + ": ");

		List<XMLElementView> chs = convertToXMLElementViewList(choices, hasEmpty);
		XMLElementView chsn = null;

		if (owner instanceof XMLComplexChoice) {
			XMLElement choosen = ((XMLComplexChoice) owner).getChoosen();
			if (choosen == null) {
				choosen = new XMLEmptyElement();
			} else if (choosen.toName().equals("parameterAssignment")) {
				String id = ((XMLComplexElement) choosen).get("resourceRef").toValue();
				if (id.length() == 0) {
					choosen = new XMLEmptyElement();
				} else {
					int index = id.indexOf(":");
					if (index != -1) {
						id = id.substring(index + 1);
					}
					choosen = defs.getResource(id);
				}
			}
			chsn = new XMLElementView(choosen, XMLElementView.TONAME);
		} else if (owner instanceof ResourceParameterBinding) {
			String resourceId = ((XMLComplexElement) owner.getParent().getParent()).get("resourceRef").toValue();
			String parameterId = ((ResourceParameterBinding) owner).getParameterRef();
			if (resourceId.length() != 0 && parameterId.length() != 0) {
				XMLElement choosen = defs.getResource(resourceId).getResourceParameter(parameterId);
				chsn = new XMLElementView(choosen, XMLElementView.TONAME);
			} else {
				chsn = chs.get(0);
			}
		} else if (owner instanceof ScriptTask) {
			String format = ((ScriptTask) owner).getScriptFormat();
			if (!choices.contains(format)) {
				chs.add(new XMLElementView(format));
			}
			chsn = new XMLElementView(format);
		} else if (owner instanceof EventDefinition) {
			chsn = new XMLElementView(owner, XMLElementView.TONAME);
		} else if (owner instanceof XMLAttribute) {
			String sel = owner.toValue();
			if (getOwner().toName().equals("method") || getOwner().toName().equals("implementation")) {
				if (sel.startsWith("##")) {
					sel = sel.substring(2);
				} else {
					sel = "URI";
				}
				chsn = new XMLElementView(sel);
			} else if (getOwner().toName().equals("structureRef") && owner.getParent() instanceof ItemDefinition) {
				if (sel.length() == 0) {
					chsn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
				} else {
					XMLElement selEl = defs.getRootElement(sel);
					if (selEl == null) {
						chsn = new XMLElementView(sel);
					} else {
						chsn = new XMLElementView(selEl, XMLElementView.TONAME);
					}
					if (!chs.contains(chsn)) {
						chs.add(chsn);
					}
				}
			} else if (getOwner().toName().equals("type") && owner.getParent() instanceof ResourceParameter || getOwner().toName().equals("itemSubjectRef")
					|| getOwner().toName().equals("structureRef") || getOwner().toName().equals("itemRef") || getOwner().toName().equals("signalRef")
					|| getOwner().toName().equals("escalationRef") || getOwner().toName().equals("errorRef") || getOwner().toName().equals("messageRef")
					|| getOwner().toName().equals("operationRef") || getOwner().toName().equals("activityRef")) {
				if (sel.length() == 0) {
					chsn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
				} else {
					if (getOwner().toName().equals("operationRef")) {
						chsn = new XMLElementView(defs.getOperation(sel), XMLElementView.TONAME);
					} else if (getOwner().toName().equals("activityRef")) {
						XMLElement parent = owner.getParent().getParent().getParent();
						FlowElements container = (FlowElements) parent.getParent();
						if (container.getParent() instanceof SubProcess && ((SubProcess) container.getParent()).isTriggeredByEvent()) {
							container = (FlowElements) container.getParent().getParent();
						}
						chsn = new XMLElementView(container.getFlowElement(sel), XMLElementView.TONAME);
					} else {
						XMLElement selEl = defs.getRootElement(sel);
						if (selEl == null) {
							chsn = new XMLElementView(sel);
						} else {
							chsn = new XMLElementView(selEl, XMLElementView.TONAME);
						}
					}
				}
			} else {
				if (!choices.contains(sel) && sel.length() != 0) {
					chs.add(new XMLElementView(sel));
				}
				chsn = new XMLElementView(sel);
			}

		} else if (owner instanceof XMLTextElement) {
			String itemId = owner.toValue();
			if (itemId.length() == 0) {
				chsn = new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME);
			} else {
				int index = itemId.indexOf(":");
				if (index != -1) {
					itemId = itemId.substring(index + 1);
				}
				XMLElement chsnElement = null;
				for (Object o : choices) {
					BaseElement el = (BaseElement) o;
					if (itemId.equals(el.getId())) {
						chsnElement = el;
					}
				}
				chsn = new XMLElementView(chsnElement, XMLElementView.TONAME);
			}
		} else {
			chsn = new XMLElementView(Resources.get(owner == null ? "" : getOwner().toName()));
		}

		jcb = new JComboBox(new Vector<XMLElementView>(chs));
		resetComboDimension(chs);

		if (chsn != null) {
			jcb.setSelectedItem(chsn);
		}

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
		Object sel = getSelectedItem();
		if (owner instanceof XMLComplexChoice) {
			((XMLComplexChoice) owner).setChoosen((XMLElement) sel);
		} else if (owner instanceof XMLAttribute || owner instanceof XMLTextElement) {
			String value = sel.toString();
			if (getOwner().toName().equals("type") && getOwner().getParent() instanceof ResourceParameter || getOwner().toName().equals("itemSubjectRef")
					|| getOwner().toName().equals("structureRef") || getOwner().toName().equals("itemRef") || getOwner().toName().equals("participantRef")
					|| getOwner().toName().equals("activityRef") || getOwner().toName().equals("interfaceRef") || getOwner().toName().equals("operationRef")
					|| getOwner().toName().equals("signalRef") || getOwner().toName().equals("escalationRef") || getOwner().toName().equals("errorRef")
					|| getOwner().toName().equals("messageRef") || getOwner().toName().equals("inMessageRef") || getOwner().toName().equals("outMessageRef")
					|| getOwner().toName().equals("targetRef") || getOwner().toName().equals("sourceRef")) {
				if (sel instanceof XMLEmptyElement) {
					value = "";
				} else if (sel instanceof String) {
				} else {
					// value = "tns:" + ((XMLComplexElement) sel).get("id").toValue();
					value = ((XMLComplexElement) sel).get("id").toValue();
				}
			}
			if (sel instanceof XMLEmptyElement) {
				value = "";
			}
			getOwner().setValue(value);
		}
	}

	public Object getSelectedItem() {
		Object el = null;
		if (jcb.isEditable()) {
			el = jcb.getEditor().getItem();
		} else {
			el = jcb.getSelectedItem();
		}
		if (el instanceof XMLElementView) {
			XMLElementView ev = (XMLElementView) jcb.getSelectedItem();
			if (ev != null) {
				if (ev.getElement() != null) {
					return ev.getElement();
				}
				return ev.getElementString();
			}
		} else {
			if (el != null) {
				return el.toString();
			}
		}
		return "";
	}

	public int getSelectedIndex() {
		return jcb.getSelectedIndex();
	}

	public List<XMLElementView> convertToXMLElementViewList(Collection<?> c, boolean hasEmpty) {
		List<XMLElementView> vec = new ArrayList<XMLElementView>();
		if (hasEmpty) {
			vec.add(new XMLElementView(new XMLEmptyElement(), XMLElementView.TONAME));
		}
		Iterator<?> it = c.iterator();
		while (it.hasNext()) {
			Object next = it.next();
			if (next instanceof XMLElement) {
				XMLElement el = (XMLElement) next;
				vec.add(new XMLElementView(el, XMLElementView.TONAME));
			} else if (owner != null && getOwner().getParent() instanceof XMLExtensionElement) {
				vec.add(new XMLElementView((String) next));
			} else {
				vec.add(new XMLElementView(Resources.get((String) next)));
			}
		}
		return vec;
	}

	public JComboBox getComboBox() {
		return jcb;
	}

	public void setComboDimension(Dimension dim) {
		jcb.setMinimumSize(dim);
		jcb.setMaximumSize(dim);
		jcb.setPreferredSize(dim);
	}

	public void resetComboDimension(List<?> choices) {
		double w = 0;
		if (choices != null) {
			double longest = 0;
			for (int i = 0; i < choices.size(); i++) {
				try {
					w = getFontMetrics(getFont()).stringWidth(choices.get(i).toString());
					if (w > longest)
						longest = w;
				} catch (Exception ex) {
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

	public void setSelectedItem(String item) {
		jcb.setSelectedItem(new XMLElementView(item));
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
		if (o == null || o instanceof XMLEmptyElement) {
			return true;
		}
		if (o instanceof String) {
			return ((String) o).trim().equals("");
		}
		return false;
	}

}
