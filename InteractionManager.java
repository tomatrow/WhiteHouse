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

import java.awt.Shape;
import java.awt.Rectangle;

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Control;

class InteractionManager implements MouseListener, MouseMoveListener
{
	private enum Mode
	{
		NONE,
		DRAG,
		CONNECTION
	}
	
	private RoomManager manager;
    private Shape element;
	private int x;
	private int y;
    private int diffX;
    private int diffY;
    private Rectangle clip;
    private Mode mode;
	
	public InteractionManager (RoomManager manager)
	{
		this.manager = manager;
        mode = Mode.NONE;
	}
	
	public void mouseDoubleClick (MouseEvent e)
	{
	}
	
	public void mouseDown (MouseEvent e)
	{
		x = e.x;
		y = e.y;
		
		Shape temp = manager.findElement(x, y);
				
		if (mode == Mode.CONNECTION && temp instanceof Room)
		{
			Room room = (Room)temp;
		
			Rectangle top = new Rectangle(room.x, room.y, room.width, 20);
			Rectangle right = new Rectangle(room.x + room.width - 20, room.y, 20, room.height);
			Rectangle bottom = new Rectangle(room.x, room.y + room.height - 20, room.width, 20);
			Rectangle left = new Rectangle(room.x, room.y, 20, room.height);
			
			if (top.contains(e.x, e.y) && left.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.NORTHWEST);
			else if (top.contains(e.x, e.y) && right.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.NORTHEAST);
			else if (bottom.contains(e.x, e.y) && left.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.SOUTHWEST);
			else if (bottom.contains(e.x, e.y) && right.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.SOUTHEAST);
			else if (top.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.NORTH);
			else if (right.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.EAST);
			else if (bottom.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.SOUTH);
			else if (left.contains(e.x, e.y))
				((Connection)element).setb(room, Compass.WEST);
		}

		
        element = temp;
        if (element != null)
		{
			manager.selection = element;
			mode = (mode == Mode.NONE) ? Mode.DRAG : mode;
			
			if (element instanceof Room)
			{
				Room room = (Room)element;
				diffX = x - room.x;
				diffY = y - room.y;
				clip = new Rectangle(room);
				for (Compass dir : Compass.values())
				{
					Connection connection = room.getConnection(dir);
					if (connection != null)
						clip.add(connection.getBounds());
				}
			}
		}
        else
			manager.newRoom(e.x, e.y, "Untitled", null);
		
		((Control)e.widget).redraw();
	}
	
	public void mouseUp (MouseEvent e)
	{
        if (element instanceof Room)
        {
            ((Room)element).snap();
            ((Control)e.widget).redraw();
        }
		
		if (x == e.x && y == e.y)
			mouseClick(e);
		else
		{
			element = null;
			mode = Mode.NONE;
		}
        
        x = -1;
        y = -1;
	}
    
    public void mouseMove(MouseEvent e)
    {
        if (mode == Mode.DRAG && element instanceof Room)
        {
            Room room = (Room)element;
            room.x = e.x - diffX;
            room.y = e.y - diffY;
            clip.add(room);
            ((Control)e.widget).redraw(clip.x-5, clip.y-5, clip.width+10, clip.height+10, false);
        }
		else if (mode == Mode.CONNECTION && element instanceof Connection)
		{
			Connection connection = (Connection)element;
			connection.setb(e.x, e.y);
			Rectangle clip = connection.getBounds();
			((Control)e.widget).redraw(clip.x-5, clip.y-5, clip.width+10, clip.height+10, false);
		}
    }
	
	public void mouseClick(MouseEvent e)
	{	
		if (mode == Mode.CONNECTION && element instanceof Room)
			mode = Mode.NONE;
        else if (element instanceof Room)
		{	
			Room room = (Room)element;
			
			Rectangle top = new Rectangle(room.x, room.y, room.width, 20);
			Rectangle right = new Rectangle(room.x + room.width - 20, room.y, 20, room.height);
			Rectangle bottom = new Rectangle(room.x, room.y + room.height - 20, room.width, 20);
			Rectangle left = new Rectangle(room.x, room.y, 20, room.height);
			
			Connection connection = null;
			if (top.contains(e.x, e.y) && left.contains(e.x, e.y))
				connection = new Connection(room, Compass.NORTHWEST);
			else if (top.contains(e.x, e.y) && right.contains(e.x, e.y))
				connection = new Connection(room, Compass.NORTHEAST);
			else if (bottom.contains(e.x, e.y) && left.contains(e.x, e.y))
				connection = new Connection(room, Compass.SOUTHWEST);
			else if (bottom.contains(e.x, e.y) && right.contains(e.x, e.y))
				connection = new Connection(room, Compass.SOUTHEAST);
			else if (top.contains(e.x, e.y))
				connection = new Connection(room, Compass.NORTH);
			else if (right.contains(e.x, e.y))
				connection = new Connection(room, Compass.EAST);
			else if (bottom.contains(e.x, e.y))
				connection = new Connection(room, Compass.SOUTH);
			else if (left.contains(e.x, e.y))
				connection = new Connection(room, Compass.WEST);
			
			element = connection;
				
			mode = Mode.CONNECTION;
		}
		else if (mode == Mode.CONNECTION && element instanceof Connection)
		{
			Connection connection = (Connection)element;
			connection.add(e.x, e.y);
		}
	}
}