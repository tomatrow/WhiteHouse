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
		return segments.iterator();
	}
	
	public boolean isDirect ()
	{
		return segments.isEmpty();
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
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public boolean contains(Point2D p)
	{
		throw new UnsupportedOperationException("Not supported yet.");
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
		Point pPoint = iterator.next();
		while (iterator.hasNext())
		{
			Point nPoint = iterator.next();
			if (r.intersectsLine(pPoint.x, pPoint.y, nPoint.x, nPoint.y))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean contains(double x, double y, double w, double h)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public boolean contains(Rectangle2D r)
	{
		throw new UnsupportedOperationException("Not supported yet.");
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