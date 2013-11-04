/**
 *	Copyright (c) 2013 by Christian Johnson (_c_@mail.com)
 *	
 *	This file is part of WhiteHouse, the Interactive Fiction Mapper.
 *	
 *	WhiteHouse is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	WhiteHouse is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with WhiteHouse.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.redhatter.whitehouse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;
import java.util.Iterator;
import java.awt.Point;

/*
	PaintManager handles any and all
	painting of elements. It is registered
	as a PaintListener for the shell.
*/

public class PaintManager implements PaintListener
{
	final private int SIZE = 20;
	
	private RoomManager manager;
	private boolean lock;
	
	public PaintManager(RoomManager manager)
	{
		this.manager = manager;
		lock = false;
	}
	
	public void paintControl(PaintEvent e)
	{
		if (lock)
			return;
		else
			lock = true;
		
		// Set Colors
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		Rectangle bounds = ((Composite)e.widget).getClientArea();
		
		// Draw the grid, only draws what is needed
		int x, y;
		for (x = e.x / SIZE; x <= bounds.width / SIZE; x++)
			e.gc.drawLine(x * SIZE, e.y, x * SIZE, e.y + e.height);
		
		for (y = e.y / SIZE; y <= bounds.height / SIZE; y++)
			e.gc.drawLine(e.x , y * SIZE, e.x + e.width, y * SIZE);

		e.gc.setLineWidth(2);
		
		// Draw rooms and connections. All of it. Should
		// probably use the clipping.
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));

		Collection<Room> rooms = manager.getFloor(manager.selection.z);
		Iterator<Room> iterator = rooms.iterator();
		while (iterator.hasNext())
			drawRoom(iterator.next(), e.gc);
		
		// Reset flags for text paint
		manager.painted();
		
		lock = false;
	}
	
	private void drawRoom(Room room, GC gc)
	{
		// Test and set flag so as to only draw each room once
		if (room == null || room.paint)
			return;
		
		room.paint = true;

		// Draw room
		gc.fillRectangle(room.x, room.y, room.width, room.height);
		gc.drawString(room.getName(), room.x+10, room.y+10);
		
		if (room == manager.selection)
		{
			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
			gc.setLineWidth(3);
			gc.drawRectangle(room.x, room.y, room.width, room.height);
			gc.setLineWidth(2);
			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		}
		else if (room == manager.location)
		{
			gc.setLineWidth(3);
			gc.drawRectangle(room.x, room.y, room.width, room.height);
			gc.setLineWidth(2);
		}
		else
			gc.drawRectangle(room.x, room.y, room.width, room.height);
		
		if (room.getNeighbor(Compass.UP) != null)
			gc.drawString("\u25B2", room.x+10, room.y+room.height-SIZE);
					
		if (room.getNeighbor(Compass.DOWN) != null)
			gc.drawString("\u25BC", room.x+SIZE, room.y+room.height-SIZE);

		// Draw this room's connections
		drawConnection(room.getConnection(Compass.NORTH), gc);
		drawConnection(room.getConnection(Compass.EAST), gc);
		drawConnection(room.getConnection(Compass.SOUTH), gc);
		drawConnection(room.getConnection(Compass.WEST), gc);
		drawConnection(room.getConnection(Compass.NORTHEAST), gc);
		drawConnection(room.getConnection(Compass.NORTHWEST), gc);
		drawConnection(room.getConnection(Compass.SOUTHEAST), gc);
		drawConnection(room.getConnection(Compass.SOUTHWEST), gc);
	}
	
	// TODO: Use flag so as to only draw each Connection once
	private void drawConnection (Connection connection, GC gc)
	{
		if (connection == null)
			return;
				
		Iterator<Point> iterator = connection.iterator();
		Point a = iterator.next();
		while (iterator.hasNext())
		{
			Point b = iterator.next();
			gc.drawLine(a.x, a.y, b.x, b.y);
			a = b;
		}
	}
}