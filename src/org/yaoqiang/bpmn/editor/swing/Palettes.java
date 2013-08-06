package org.yaoqiang.bpmn.editor.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.Task;
import org.yaoqiang.bpmn.model.elements.events.Event;
import org.yaoqiang.bpmn.model.elements.gateways.Gateway;
import org.yaoqiang.graph.util.TooltipBuilder;
import org.yaoqiang.util.Constants;
import org.yaoqiang.util.Resources;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

/**
 * Palettes
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class Palettes extends JPanel {

	private static final long serialVersionUID = 7771113885935187066L;

	protected JLabel selectedEntry = null;

	protected mxEventSource eventSource = new mxEventSource(this);

	public Palettes(String name) {
		setName(name);
		setBackground(new Color(120, 200, 175));
		setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

		// Clears the current selection when the background is clicked
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				clearSelection();
			}
		});

		// Shows a nice icon for drag and drop but doesn't import anything
		setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1448567513824983453L;

			public boolean canImport(JComponent comp, DataFlavor[] flavors) {
				return true;
			}
		});
	}

	public void clearSelection() {
		setSelectionEntry(null, null);
	}

	public void setSelectionEntry(JLabel entry, mxGraphTransferable t) {
		JLabel previous = selectedEntry;
		selectedEntry = entry;

		if (previous != null) {
			previous.setBorder(null);
			previous.setOpaque(false);
		}

		if (selectedEntry != null) {
			selectedEntry.setBorder(PaletteBorder.getSharedInstance());
			selectedEntry.setOpaque(true);
		}

		eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry", selectedEntry, "transferable", t, "previous", previous));
	}

	public void setPreferredCols(int count, int extra) {
		int cols = Math.max(1, count);
		if (getName().equals(Resources.get("fragments"))) {
			int height = 80 + extra;
			for (Component c : getComponents()) {
				height += c.getHeight();
			}
			setPreferredSize(new Dimension(cols * 195, height));
		} else {
			setPreferredSize(new Dimension(cols * 55, (getComponentCount() * 55 / cols) + 50));
		}
		revalidate();
	}

	public void setPreferredWidth(int width) {
		if (getName().equals(Resources.get("fragments"))) {
			setPreferredCols(1, 0);
		} else {
			int cols = Math.max(1, width / 55);
			setPreferredCols(cols, 0);
		}
	}

	public void addChoreographyTemplate(final String name, ImageIcon icon, String style) {
		mxCell cTask = new mxCell("", new mxGeometry(0, 0, 93, 95), style);
		cTask.setVertex(true);
		cTask.setConnectable(false);
		addTemplate(name, icon, cTask);
	}

	public void addTemplate(final String name, ImageIcon icon, String style, Object value) {
		addTemplate(name, icon, style, 85, 55, value);
	}

	public void addEventTemplate(final String name, ImageIcon icon, String style, Event event) {
		String size = Constants.SETTINGS.getProperty("style_" + style + "_size", "32");
		addTemplate(name, icon, style, Integer.parseInt(size), Integer.parseInt(size), event);
	}

	public void addTaskTemplate(final String name, ImageIcon icon, Task task) {
		String width = Constants.SETTINGS.getProperty("style_task_width", "85");
		String height = Constants.SETTINGS.getProperty("style_task_height", "55");
		addTemplate(name, icon, "task", Integer.parseInt(width), Integer.parseInt(height), task);
	}

	public void addCallActivityTemplate(final String name, ImageIcon icon, CallActivity callActivity) {
		String width = Constants.SETTINGS.getProperty("style_task_width", "85");
		String height = Constants.SETTINGS.getProperty("style_task_height", "55");
		String style = "callTask";
		if (name.equals("callProcess")) {
			style = "callProcess";
		}
		addTemplate(name, icon, style, Integer.parseInt(width), Integer.parseInt(height), callActivity);
	}

	public void addGatewayTemplate(final String name, ImageIcon icon, Gateway gateway) {
		String size = Constants.SETTINGS.getProperty("style_gateway_size", "42");
		String style = "gateway";
		if (name.equals("exclusiveGatewayWithIndicator")) {
			style = "exclusiveGatewayWithIndicator";
		} else if (name.equals("exclusiveGateway")) {
			style = "exclusiveGateway";
		}
		addTemplate(name, icon, style, Integer.parseInt(size), Integer.parseInt(size), gateway);
	}

	public void addDataObjectTemplate(final String name, ImageIcon icon, Object value) {
		String width = Constants.SETTINGS.getProperty("style_dataObject_width", "29");
		String height = Constants.SETTINGS.getProperty("style_dataObject_height", "38");
		addTemplate(name, icon, "dataObject", Integer.parseInt(width), Integer.parseInt(height), value);
	}

	public void addDataStoreTemplate(final String name, ImageIcon icon, Object value) {
		String width = Constants.SETTINGS.getProperty("style_dataStore_width", "35");
		String height = Constants.SETTINGS.getProperty("style_dataStore_height", "30");
		addTemplate(name, icon, "dataStore", Integer.parseInt(width), Integer.parseInt(height), value);
	}

	public void addSubprocessTemplate(final String name, ImageIcon icon, Object value) {
		String width = Constants.SETTINGS.getProperty("style_subprocess_width", "400");
		String height = Constants.SETTINGS.getProperty("style_subprocess_height", "250");
		addTemplate(name, icon, "subprocess", Integer.parseInt(width), Integer.parseInt(height), 85, 55, value);
	}

	public void addMessageTemplate(final String name, ImageIcon icon, String style, Object value) {
		String width = Constants.SETTINGS.getProperty("style_message_width", "35");
		String height = Constants.SETTINGS.getProperty("style_message_height", "30");
		addTemplate(name, icon, style, Integer.parseInt(width), Integer.parseInt(height), value);
	}

	public void addConversationTemplate(final String name, ImageIcon icon, String style, Object value) {
		String width = Constants.SETTINGS.getProperty("style_conversation_width", "40");
		String height = Constants.SETTINGS.getProperty("style_conversation_height", "35");
		addTemplate(name, icon, style, Integer.parseInt(width), Integer.parseInt(height), value);
	}

	public void addEdgeTemplate(final String name, ImageIcon icon, String style, int width, int height, Object value) {
		mxGeometry geometry = new mxGeometry(0, 0, width, height);
		geometry.setTerminalPoint(new mxPoint(0, height), true);
		geometry.setTerminalPoint(new mxPoint(width, 0), false);
		geometry.setRelative(true);

		mxCell cell = new mxCell(value, geometry, style);
		cell.setEdge(true);

		addTemplate(name, icon, cell);
	}

	public void addPatternTemplate(int num) {
		String name = "wcp" + num;
		String imagePath = "/org/yaoqiang/bpmn/editor/patterns/" + name + ".png";
		URL url = Palettes.class.getResource(imagePath);
		if (url == null) {
			return;
		}
		ImageIcon icon = new ImageIcon(url);
		String style = "pattern=" + name + ";image;image=" + imagePath;
		mxCell cell = new mxCell(Resources.get(name + "desc"), new mxGeometry(0, 0, icon.getIconWidth(), icon.getIconHeight()), style);
		cell.setVertex(true);
		addTemplate(name, icon, cell);
	}

	public void addFragmentTemplate(File imgFile) {
		String name = imgFile.getName().substring(0, imgFile.getName().lastIndexOf("."));
		ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
		String style = "fragment=" + name + ";image;image=File:///" + imgFile.getAbsolutePath();
		mxCell cell = new mxCell(name, new mxGeometry(0, 0, icon.getIconWidth(), icon.getIconHeight()), style);
		cell.setVertex(true);
		addTemplate(name, icon, cell);
	}

	public void addTemplate(final String name, ImageIcon icon, String style, int width, int height, Object value) {
		mxCell cell = new mxCell(value, new mxGeometry(0, 0, width, height), style);
		cell.setVertex(true);

		addTemplate(name, icon, cell);
	}

	public void addTemplate(final String name, ImageIcon icon, String style, int width, int height, int altwidth, int altheight, Object value) {
		mxGeometry geo = new mxGeometry(0, 0, width, height);
		geo.setAlternateBounds(new mxRectangle(0, 0, altwidth, altheight));

		mxCell cell = new mxCell(value, geo, style);
		cell.setVertex(true);

		addTemplate(name, icon, cell);
	}

	public void addTemplate(final String name, ImageIcon icon, final mxCell cell) {
		mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
		final mxGraphTransferable t = new mxGraphTransferable(new Object[] { cell }, bounds);

		// Scales the image if it's too large for the library
		if (icon != null) {
			if (icon.getIconWidth() > 195) {
				int width = 195;
				int cols = Integer.parseInt(Constants.SETTINGS.getProperty("paletteCols", "4"));
				if (cols < 4) {
					width = 145;
				} else if (cols == 6) {
					width = 140;
				}
				icon = new ImageIcon(icon.getImage().getScaledInstance(width, width * icon.getIconHeight() / icon.getIconWidth(), Image.SCALE_SMOOTH));
			}
		}

		final JLabel entry = new JLabel(icon);
		entry.setPreferredSize(new Dimension(icon.getIconWidth() + 18, icon.getIconHeight() + 18));
		entry.setBackground(Palettes.this.getBackground().brighter());
		entry.setFont(new Font(entry.getFont().getFamily(), 0, 11));

		entry.setVerticalTextPosition(JLabel.BOTTOM);
		entry.setHorizontalTextPosition(JLabel.CENTER);
		entry.setIconTextGap(0);
		if (cell.getStyle().startsWith("pattern=") || cell.getStyle().startsWith("fragment=")) {
			entry.setToolTipText(cell.getValue().toString());
		} else {
			entry.setToolTipText(TooltipBuilder.getQuickHintsForElement(name));
		}
		entry.setText(Resources.get(name));

		entry.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				setSelectionEntry(entry, t);
				if (Palettes.this.getName().equals(Resources.get("fragments")) && cell.getStyle().startsWith("fragment=") && e.isPopupTrigger()) {
					final JLabel label = (JLabel) e.getSource();
					JPopupMenu popup = new JPopupMenu();
					JMenuItem item = new JMenuItem(Resources.get("delete"));
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String imagepath = cell.getStyle().substring(cell.getStyle().lastIndexOf("image=File:///") + 14);
							String bpmnfilepath = imagepath.substring(0, imagepath.lastIndexOf(".")) + ".bpmn";
							new File(imagepath).delete();
							new File(bpmnfilepath).delete();
							Palettes.this.remove(label);
							Palettes.this.setPreferredCols(1, 0);
						}
					});
					popup.add(item);

					popup.show(label, e.getX(), e.getY());
				}
			}

		});

		// Install the handler for dragging nodes into a graph
		DragGestureListener dragGestureListener = new DragGestureListener() {
			public void dragGestureRecognized(DragGestureEvent e) {
				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(), t, null);
			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(entry, DnDConstants.ACTION_COPY, dragGestureListener);

		add(entry);
	}

	/**
	 * @param eventName
	 * @param listener
	 * @see com.mxgraph.util.mxEventSource#addListener(java.lang.String,
	 *      com.mxgraph.util.mxEventSource.mxIEventListener)
	 */
	public void addListener(String eventName, mxIEventListener listener) {
		eventSource.addListener(eventName, listener);
	}

	/**
	 * @return
	 * @see com.mxgraph.util.mxEventSource#isEventsEnabled()
	 */
	public boolean isEventsEnabled() {
		return eventSource.isEventsEnabled();
	}

	/**
	 * @param listener
	 * @see com.mxgraph.util.mxEventSource#removeListener(com.mxgraph.util.mxEventSource.mxIEventListener)
	 */
	public void removeListener(mxIEventListener listener) {
		eventSource.removeListener(listener);
	}

	/**
	 * @param eventName
	 * @param listener
	 * @see com.mxgraph.util.mxEventSource#removeListener(java.lang.String,
	 *      com.mxgraph.util.mxEventSource.mxIEventListener)
	 */
	public void removeListener(mxIEventListener listener, String eventName) {
		eventSource.removeListener(listener, eventName);
	}

	/**
	 * @param eventsEnabled
	 * @see com.mxgraph.util.mxEventSource#setEventsEnabled(boolean)
	 */
	public void setEventsEnabled(boolean eventsEnabled) {
		eventSource.setEventsEnabled(eventsEnabled);
	}

}
