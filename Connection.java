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
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/*
	Represents the connection between rooms.
*/

class Connection implements Shape
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
		// Calculate starting and ending points based on
		// connected rooms
		int xa = 0;
		int xb = 0;
		int ya = 0;
		int yb = 0;
		switch (aSide)
		{
			case NORTH:
				xa = (a.x + a.width / 2);
				ya = a.y;
				xb = xa;
				yb = (b == null) ? ya - 1 : (b.y + b.height);
				break;
			case EAST:
				xa = (a.x + a.width);
				ya = (a.y + a.height / 2);
				xb = (b == null) ? xa + 1 : b.x;
				yb = ya;
				break;
			case SOUTH:
				xa = (a.x + a.width / 2);
				ya = (a.y + a.height);
				xb = xa;
				yb = (b == null) ? ya + 1 : b.y;
				break;
			case WEST:
				xa = a.x;
				ya = (a.y + a.height / 2);
				xb = (b == null) ? xa - 1 : (b.x + b.width);
				yb = ya;
				break;
			case NORTHEAST:
				xa = (a.x + a.width);
				ya = a.y;
				xb = (b == null) ? xa + 1 : b.x;
				yb = (b == null) ? ya - 1 : (b.y + b.height);
				break;
			case NORTHWEST:
				xa = a.x;
				ya = a.y;
				xb = (b == null) ? xa - 1 : (b.x + b.width);
				yb = (b == null) ? ya - 1 : (b.y + b.height);
				break;
			case SOUTHEAST:
				xa = (a.x + a.width);
				ya = (a.y + a.height);
				xb = (b == null) ? xa + 1 : b.x;
				yb = (b == null) ? ya + 1 : b.y;
				break;
			case SOUTHWEST:
				xa = a.x;
				ya = (a.y + a.height);
				xb = (b == null) ? xa - 1 : (b.x + b.width);
				yb = (b == null) ? ya + 1 : b.y;
				break;
		}
		
		List<Point> list = new LinkList(segments);
		list.add(0, new Point(xa, ya));
		list.add(new Point(xb, yb));

		return list.iterator();
	}
	
	@Override
	public Rectangle getBounds()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public Rectangle2D getBounds2D()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public boolean contains(double x, double y)
	{
		return contains(new Point2D.Double(x, y));
	}
	
	@Override
	public boolean contains(Point2D c)
	{
		Iterator<Point> iterator = iterator();
		Point a = iterator.next();
		while (iterator.hasNext())
		{
			Point b = iterator.next();
			if (a.distance(c) + b.distance(c) == a.distance(b))
				return true;
			
			a = b;
		}
		
		return false;
	}
	
	@Override
	public boolean intersects(double x, double y, double w, double h)
	{
		return intersects(new Rectangle2D.Double(x, y, w, h));
	}
	
	@Override
	public boolean intersects(Rectangle2D r)
	{
		Iterator<Point> iterator = segments.iterator();
		Point a = iterator.next();
		while (iterator.hasNext())
		{
			Point b = iterator.next();
			if (r.intersectsLine(a.x, a.y, b.x, b.y))
				return true;
			
			a = b;
		}
		
		return false;
	}
	
	@Override
	public boolean contains(double x, double y, double w, double h)
	{
		return false;
	}
	
	@Override
	public boolean contains(Rectangle2D r)
	{
		return false;
	}
	
	@Override
	public PathIterator getPathIterator(AffineTransform at)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}