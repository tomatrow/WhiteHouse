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
		
		// Draw rooms and connections. All of it. Should
		// probably use the clipping.
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		drawRoom(manager.selection, e.gc);
		
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
		gc.fillRectangle(room.x*SIZE, room.y*SIZE, room.width*SIZE, room.height*SIZE);
		gc.drawString(room.getName(), room.x*SIZE+10, room.y*SIZE+10);
		
		if (room == manager.selection)
		{
			gc.setLineWidth(2);
			gc.drawRectangle(room.x*SIZE, room.y*SIZE, room.width*SIZE, room.height*SIZE);
			gc.setLineWidth(1);
		}
		else
			gc.drawRectangle(room.x*SIZE, room.y*SIZE, room.width*SIZE, room.height*SIZE);
		
		if (room.getNeighbor(Compass.UP) != null)
			gc.drawString("\u25B2", (room.x)*SIZE+SIZE/2, (room.y+room.height-1)*SIZE);
					
		if (room.getNeighbor(Compass.DOWN) != null)
			gc.drawString("\u25BC", (room.x+1)*SIZE, (room.y+room.height-1)*SIZE);

		// Draw this room's connections
		drawConnection(room.getConnection(Compass.NORTH), gc);
		drawConnection(room.getConnection(Compass.EAST), gc);
		drawConnection(room.getConnection(Compass.SOUTH), gc);
		drawConnection(room.getConnection(Compass.WEST), gc);
		drawConnection(room.getConnection(Compass.NORTHEAST), gc);
		drawConnection(room.getConnection(Compass.NORTHWEST), gc);
		drawConnection(room.getConnection(Compass.SOUTHEAST), gc);
		drawConnection(room.getConnection(Compass.SOUTHWEST), gc);
		
		// Draw this room's neighoring rooms
		drawRoom(room.getNeighbor(Compass.NORTH), gc);
		drawRoom(room.getNeighbor(Compass.EAST), gc);
		drawRoom(room.getNeighbor(Compass.SOUTH), gc);
		drawRoom(room.getNeighbor(Compass.WEST), gc);
		drawRoom(room.getNeighbor(Compass.NORTHEAST), gc);
		drawRoom(room.getNeighbor(Compass.NORTHWEST), gc);
		drawRoom(room.getNeighbor(Compass.SOUTHEAST), gc);
		drawRoom(room.getNeighbor(Compass.SOUTHWEST), gc);
	}
	
	// TODO: Use flag so as to only draw each Connection once
	private void drawConnection (Connection connection, GC gc)
	{
		if (connection == null)
			return;
		
		// Calculate starting and ending points based on
		// connected rooms
		int xa = 0;
		int xb = 0;
		int ya = 0;
		int yb = 0;
		Room a = connection.a;
		Room b = connection.b;
		switch (connection.aSide)
		{
			case NORTH:
				xa = (a.x + a.width / 2) * SIZE;
				ya = a.y * SIZE;
				xb = xa;
				yb = (b == null) ? ya - SIZE : (b.y + b.height) * SIZE;
				break;
			case EAST:
				xa = (a.x + a.width) * SIZE;
				ya = (a.y + a.height / 2) * SIZE;
				xb = (b == null) ? xa + SIZE : b.x * SIZE;
				yb = ya;
				break;
			case SOUTH:
				xa = (a.x + a.width / 2) * SIZE;
				ya = (a.y + a.height) * SIZE;
				xb = xa;
				yb = (b == null) ? ya + SIZE : b.y * SIZE;
				break;
			case WEST:
				xa = a.x * SIZE;
				ya = (a.y + a.height / 2) * SIZE;
				xb = (b == null) ? xa - SIZE : (b.x + b.width) * SIZE;
				yb = ya;
				break;
			case NORTHEAST:
				xa = (a.x + a.width) * SIZE;
				ya = a.y * SIZE;
				xb = (b == null) ? xa + SIZE : b.x * SIZE;
				yb = (b == null) ? ya - SIZE : (b.y + b.height) * SIZE;
				break;
			case NORTHWEST:
				xa = a.x * SIZE;
				ya = a.y * SIZE;
				xb = (b == null) ? xa - SIZE : (b.x + b.width) * SIZE;
				yb = (b == null) ? ya - SIZE : (b.y + b.height) * SIZE;
				break;
			case SOUTHEAST:
				xa = (a.x + a.width) * SIZE;
				ya = (a.y + a.height) * SIZE;
				xb = (b == null) ? xa + SIZE : b.x * SIZE;
				yb = (b == null) ? ya + SIZE : b.y * SIZE;
				break;
			case SOUTHWEST:
				xa = a.x * SIZE;
				ya = (a.y + a.height) * SIZE;
				xb = (b == null) ? xa - SIZE : (b.x + b.width) * SIZE;
				yb = (b == null) ? ya + SIZE : b.y * SIZE;
				break;
		}			
		
		// There are no segments to draw
		if (connection.isDirect())
			gc.drawLine(xa, ya, xb, yb);
		else
		{
			Iterator<Point> iterator = connection.iterator();
			Point pSegment = iterator.next();
			gc.drawLine(xa, ya, pSegment.x * SIZE, pSegment.y * SIZE);
			while (iterator.hasNext())
			{
				Point nSegment = iterator.next();
				gc.drawLine(pSegment.x * SIZE, pSegment.y * SIZE, nSegment.x * SIZE, nSegment.y * SIZE);
				pSegment = nSegment;
			}
			gc.drawLine(pSegment.x * SIZE, pSegment.y * SIZE, xb, yb);
		}
	}
}