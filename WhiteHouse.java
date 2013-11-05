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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.custom.ScrolledComposite;

import java.nio.file.Paths;
import java.nio.file.Path;

public class WhiteHouse
{
    public static void main(String [] args)
    {		
		// Create window
		final Display display = new Display();
		final Shell shell = new Shell(display);
		Rectangle area = shell.getClientArea();
		shell.setText("WhiteHouse");

		final ScrolledComposite scroll = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		final Canvas canvas = new Canvas(scroll, SWT.NONE);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		scroll.setContent(canvas);
		canvas.setBounds(area);
		scroll.setBounds(area);
	
		// Open FileDialog to select transcript
		Path transcript;
		if (args.length > 0)
		{
			transcript = Paths.get(args[0]);
		} else
		{
			FileDialog dialog = new FileDialog (shell, SWT.OPEN);
			String filterPath = System.getProperty("user.dir");
			dialog.setFilterPath(filterPath);
			transcript = Paths.get(dialog.open());
		}
		
		// Create RoomManager and start thread to watch transcript file
		final RoomManager manager = new RoomManager(shell, canvas);
		final TranscriptWatcher watcher = new TranscriptWatcher(transcript, manager, canvas);
		watcher.start();
		
        InteractionManager listener = new InteractionManager(manager);
		canvas.addMouseListener(listener);
        canvas.addMouseMoveListener(listener);
		
		// Create and register PaintManager
		final PaintManager paint = new PaintManager(manager);
		canvas.addPaintListener(paint);
		
		final Rose rose = new Rose(shell, manager);
		Rectangle bounds = rose.getBounds();
		rose.setLocation(area.width-bounds.width, area.height-bounds.height);
		rose.moveAbove(null);
		
		// Start event loop
		shell.open();
		shell.addListener(SWT.Resize, new Listener ()
		{
			public void handleEvent (Event e)
			{
				Rectangle area = shell.getClientArea();
				Rectangle bounds = rose.getBounds();
				scroll.setBounds(area);
				rose.setLocation(area.width-bounds.width, area.height-bounds.height);
			}
		});
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		
		// Exit
		display.dispose();
		watcher.stop = true;
	}
}