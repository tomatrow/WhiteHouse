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
	private RoomManager manager;
    private Shape element;
	private int x;
	private int y;
    private int diffX;
    private int diffY;
    private Rectangle clip;
    private boolean drag;
	
	public InteractionManager (RoomManager manager)
	{
		this.manager = manager;
        drag = false;
	}
	
	public void mouseDoubleClick (MouseEvent e)
	{
	}
	
	public void mouseDown (MouseEvent e)
	{
		x = e.x;
		y = e.y;
        element = manager.findElement(x, y);
        if (element != null)
			manager.selection = element;
        else
			manager.newRoom(e.x, e.y, "Untitled", null);
		
		((Control)e.widget).redraw();

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
        drag = true;
	}
	
	public void mouseUp (MouseEvent e)
	{
        if (element instanceof Room)
        {
            ((Room)element).snap();
            ((Control)e.widget).redraw();
        }
        
        x = -1;
        y = -1;
        element = null;
        drag = false;
	}
    
    public void mouseMove(MouseEvent e)
    {
        if (drag && element instanceof Room)
        {
            Room room = (Room)element;
            room.x = e.x - diffX;
            room.y = e.y - diffY;
            clip.add(room);
            ((Control)e.widget).redraw(clip.x-5, clip.y-5, clip.width+10, clip.height+10, false);
        }
    }
}