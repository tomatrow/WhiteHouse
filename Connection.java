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
	
	private Room a;
	private Compass aSide;
	private Room b;
	private Compass bSide;
	private int x = -1;
	private int y = -1;
	
	public Connection (Room a, Compass aSide)
	{
		this.a = a;
		this.aSide = aSide;
		a.setConnection(aSide, this);
	}

	public void delete ()
	{
		a.setConnection(aSide, null);
		if (b != null)
			b.setConnection(bSide, null);
	}
	
	public void setb (Room b, Compass bSide)
	{
		this.b = b;
		this.bSide = bSide;
		b.setConnection(bSide, this);
	}
	
	public void setb (int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public boolean add (Point point)
	{
		return segments.add(point);
	}
	
	public boolean add (int x, int y)
	{
		return add(new Point(x, y));
	}

	public Iterator<Point> iterator ()
	{
		// Calculate starting and ending points based on
		// connected rooms.
		// TODO: I'm sure this can be done cleaner.
		int xa = 0;
		int ya = 0;
		switch (aSide)
		{
			case NORTH:
				xa = (a.x + a.width / 2);
				ya = a.y;
				break;
			case EAST:
				xa = (a.x + a.width);
				ya = (a.y + a.height / 2);
				break;
			case SOUTH:
				xa = (a.x + a.width / 2);
				ya = (a.y + a.height);
				break;
			case WEST:
				xa = a.x;
				ya = (a.y + a.height / 2);
				break;
			case NORTHEAST:
				xa = (a.x + a.width);
				ya = a.y;
				break;
			case NORTHWEST:
				xa = a.x;
				ya = a.y;
				break;
			case SOUTHEAST:
				xa = (a.x + a.width);
				ya = (a.y + a.height);
				break;
			case SOUTHWEST:
				xa = a.x;
				ya = (a.y + a.height);
				break;
		}
		
		List<Point> list = new LinkedList<Point>(segments);
		list.add(0, new Point(xa, ya));
		
		int xb = 0;
		int yb = 0;
		switch ((bSide == null) ? Compass.invert(aSide) : bSide)
		{
			case NORTH:
				xb = (b == null) ? (x != -1) ? x : xa : (b.x + b.width / 2); 
				yb = (b == null) ? (y != -1) ? y : ya - 1 : b.y;
				break;
			case EAST:
				xb = (b == null) ? (x != -1) ? x : xa + 1 : (b.x + b.width);
				yb = (b == null) ? (y != -1) ? y : ya: (b.y + b.height / 2);
				break;
			case SOUTH:
				xb = (b == null) ? (x != -1) ? x : xa : (b.x + b.width / 2);
				yb = (b == null) ? (y != -1) ? y : ya + 1 : (b.y + b.height);
				break;
			case WEST:
				xb = (b == null) ? (x != -1) ? x : xa - 1 : b.x;
				yb = (b == null) ? (y != -1) ? y : ya : (b.y + b.height / 2);
				break;
			case NORTHEAST:
				xb = (b == null) ? (x != -1) ? x : xa + 1 : (b.x + b.width);
				yb = (b == null) ? (y != -1) ? y : ya - 1 : b.y;
				break;
			case NORTHWEST:
				xb = (b == null) ? (x != -1) ? x : xa - 1 : b.x;
				yb = (b == null) ? (y != -1) ? y : ya - 1 : b.y;
				break;
			case SOUTHEAST:
				xb = (b == null) ? (x != -1) ? x : xa + 1 : (b.x + b.width);
				yb = (b == null) ? (y != -1) ? y : ya + 1 : (b.y + b.height);
				break;
			case SOUTHWEST:
				xb = (b == null) ? (x != -1) ? x : xa - 1 : b.x;
				yb = (b == null) ? (y != -1) ? y : ya + 1 : (b.y + b.height);
				break;
		}

		list.add(new Point(xb, yb));

		return list.iterator();
	}
	
	@Override
	public Rectangle getBounds()
	{
		Iterator<Point> iterator = iterator();
		Rectangle bounds = new Rectangle(iterator.next());
		while (iterator.hasNext())
			bounds.add(iterator.next());

        return bounds;
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
    
    public boolean contains(double x, double y, int tolerance)
	{
		return contains(new Point2D.Double(x, y), tolerance);
	}
    
    // Returns true if c is within tolerance pixels from
    // the Connection. Treats c as a point in a triangle
    // with each segment as the base.
    public boolean contains(Point2D c, int tolerance)
	{
		Iterator<Point> iterator = iterator();
		Point a = iterator.next();
		while (iterator.hasNext())
		{
			Point b = iterator.next();

            // Calculate the length of all three sides
            double A = a.distance(c);
            double C = b.distance(c);
            double B = a.distance(b);

            // Use Heron's formula to find the height of
            // the triangle (distance from line).
            double S = (A+B+C)/2;
            double area = Math.sqrt(S*(S-A)*(S-B)*(S-C));
            if (area/(B*0.5) < tolerance)
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