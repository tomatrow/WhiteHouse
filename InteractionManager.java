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

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseEvent;

class InteractionManager implements MouseListener
{
	private RoomManager manager;
	private int x;
	private int y;
	
	public InteractionManager (RoomManager manager)
	{
		this.manager = manager;
	}
	
	public void mouseDoubleClick (MouseEvent e)
	{
	}
	
	public void mouseDown (MouseEvent e)
	{
		x = e.x;
		y = e.y;
	}
	
	public void mouseUp (MouseEvent e)
	{
		if (x == e.x && y == e.y)
			mouseClick(e);
	}
	
	public void mouseClick (MouseEvent e)
	{
		Room room = manager.findRoom(e.x, e.y);
		if (room != null)
			manager.select(room);
		else
			manager.newRoom(e.x, e.y, null, null);
	}
}