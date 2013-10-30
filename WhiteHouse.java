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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.FileDialog;

import java.nio.file.Paths;
import java.nio.file.Path;

public class WhiteHouse
{
    public static void main(String [] args)
    {		
		// Create window
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		shell.setText("WhiteHouse");
	
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
		RoomManager manager = new RoomManager(shell);
		TranscriptWatcher watcher = new TranscriptWatcher(transcript, manager, shell);
		watcher.start();
		
		// Create and register PaintManager
		PaintManager paint = new PaintManager(manager);
		shell.addPaintListener(paint);
		
		// Start event loop
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		
		// Exit
		display.dispose();
		watcher.stop = true;
	}
}