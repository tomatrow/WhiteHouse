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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Rose extends Canvas
{
	final private Image rose = new Image(Display.getDefault(), System.getProperty("user.dir")+"\\compass-rose.png");
	
	final private Rectangle north = new Rectangle(160, 0, 160, 200);
	final private Rectangle west = new Rectangle(0, 200, 160, 140);
	final private Rectangle south = new Rectangle(160, 340, 160, 160);
	final private Rectangle east = new Rectangle(320, 200, 160, 140);
	final private Rectangle northeast = new Rectangle(320, 80, 160, 200);
	final private Rectangle northwest = new Rectangle(0, 0, 160, 200);
	final private Rectangle southeast = new Rectangle(320, 340, 160, 160);
	final private Rectangle southwest = new Rectangle(0, 340, 160, 160);
	final private Rectangle up = new Rectangle(414, 0, 86, 80);
	final private Rectangle down = new Rectangle(340, 0, 74, 80);
	
	public Rose (Composite parent, final RoomManager manager)
	{
		super(parent, SWT.NONE);
		Rectangle b = rose.getBounds();
		setSize(b.width, b.height);
	
		addPaintListener(new PaintListener ()
		{
			public void paintControl(PaintEvent e)
			{
				e.gc.drawImage(rose, 0, 0);
			}
		});
		
		addMouseListener(new MouseAdapter ()
		{
			public void mouseDown (MouseEvent e)
			{
				if (north.contains(e.x, e.y))
					manager.select(Compass.NORTH);
				else if (east.contains(e.x, e.y))
					manager.select(Compass.EAST);
				else if (south.contains(e.x, e.y))
					manager.select(Compass.SOUTH);
				else if (west.contains(e.x, e.y))
					manager.select(Compass.WEST);
				else if (northeast.contains(e.x, e.y))
					manager.select(Compass.NORTHEAST);
				else if (northwest.contains(e.x, e.y))
					manager.select(Compass.NORTHWEST);
				else if (southeast.contains(e.x, e.y))
					manager.select(Compass.SOUTHEAST);
				else if (southwest.contains(e.x, e.y))
					manager.select(Compass.SOUTHWEST);
				else if (up.contains(e.x, e.y))
					manager.select(Compass.UP);
				else if (down.contains(e.x, e.y))
					manager.select(Compass.DOWN);
			}
		});
	}
}