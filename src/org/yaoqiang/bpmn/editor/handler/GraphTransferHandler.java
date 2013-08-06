package org.yaoqiang.bpmn.editor.handler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.graph.swing.GraphComponent;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

/**
 * GraphTransferHandler
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class GraphTransferHandler extends mxGraphTransferHandler {

	private static final long serialVersionUID = -2842507382192363320L;

	public boolean canImport(JComponent comp, DataFlavor[] flavors)
	{
		for (int i = 0; i < flavors.length; i++)
		{
			if (flavors[i] != null
					&& (flavors[i].equals(mxGraphTransferable.dataFlavor) || flavors[i].isFlavorJavaFileListType())) // ==============start -- end==============
			{
				return true;
			}
		}

		return false;
	}
	
	public boolean importData(JComponent c, Transferable t)
	{
		boolean result = false;

		if (isLocalDrag())
		{
			// Enables visual feedback on the Mac
			result = true;
		}
		else
		{
			try
			{
				updateImportCount(t);

				if (c instanceof GraphComponent)
				{
					GraphComponent graphComponent = (GraphComponent) c;

					if (graphComponent.isEnabled()) {
						if(t.isDataFlavorSupported(mxGraphTransferable.dataFlavor)) {
							mxGraphTransferable gt = (mxGraphTransferable) t.getTransferData(mxGraphTransferable.dataFlavor);
							if (gt.getCells() != null)	{
								result = importGraphTransferable(graphComponent, gt);
							}
						} else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
							// ==============start==============
							List<?> files = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
							if (!files.isEmpty() && files.size() == 1) {
								File file = (File) files.get(0);
								ModelActions.getOpenAction(file.getAbsolutePath()).actionPerformed(new ActionEvent(graphComponent, 0, ""));
							}
							// ==============end================
						}
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return result;
	}
	
	protected boolean importGraphTransferable(mxGraphComponent graphComponent,
			mxGraphTransferable gt)
	{
		boolean result = false;

		try
		{
			mxGraph graph = graphComponent.getGraph();
			double scale = graph.getView().getScale();
			mxRectangle bounds = gt.getBounds();
			double dx = 0, dy = 0;

			// Computes the offset for the placement of the imported cells
			if (location != null && bounds != null)
			{
				mxPoint translate = graph.getView().getTranslate();

				dx = location.getX() - (bounds.getX() + translate.getX())
						* scale;
				dy = location.getY() - (bounds.getY() + translate.getY())
						* scale;

				// Keeps the cells aligned to the grid
				dx = graph.snap(dx / scale);
				dy = graph.snap(dy / scale);
			}
			else
			{
				int gs = graph.getGridSize();

				dx = importCount * gs;
				dy = importCount * gs;
			}

			if (offset != null)
			{
				dx += offset.x;
				dy += offset.y;
			}

			// ==============start==============
			// Paste cells from popup menu
			if (location == null && bounds != null) {
				location = ((GraphComponent) graphComponent).getPasteToPoint();
				if (location != null) {
					mxPoint translate = graph.getView().getTranslate();

					dx = location.getX() - (bounds.getX() + translate.getX()) * scale;
					dy = location.getY() - (bounds.getY() + translate.getY()) * scale;

					// Keeps the cells aligned to the grid
					dx = graph.snap(dx / scale);
					dy = graph.snap(dy / scale);
				}
			}
			// ==============end================
			
			importCells(graphComponent, gt, dx, dy);
			location = null;
			offset = null;
			result = true;

			// Requests the focus after an import
			graphComponent.requestFocus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}
	
}
