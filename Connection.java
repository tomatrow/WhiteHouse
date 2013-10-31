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

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.awt.Point;

/*
	Represents the connection between rooms.
*/

class Connection
{
	private List<Point> segments = new LinkedList<Point>();
	
	public Room a;
	public Compass aSide;
	public Room b;
	public Compass bSide;
	
	public boolean add (Point point)
	{
		return segments.add(point);
	}
	
	public Iterator<Point> iterator ()
	{
		return segments.iterator();
	}
	
	public boolean isDirect ()
	{
		return segments.isEmpty();
	}
}