/**
 *	Copyright (c) 2013 by Christian Johnson <_c_@mail.com>
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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Canvas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/*
 	TranscriptWatcher polls the transcript
	file for changes, then processes them.
	It extends Thread so as to allow the
	event thread to continue.
*/

public class TranscriptWatcher extends java.lang.Thread
{
	// Proper title regex: First letter of every word above
	// three letters, and first letter of line, is cap
	final private String nameRegex = "^[A-Z][a-zA-Z']*(?: [a-z]{1,3}| [A-Z][a-z']{3,})*$";
	
	private RoomManager manager;
	private Path transcript;
	private Canvas canvas;
	
	// Flag to stop thread (Thread.stop() method is unsafe)
	public volatile boolean stop;
	
	public TranscriptWatcher (Path transcript, RoomManager manager, Canvas canvas)
	{
		this.transcript = transcript;
		this.manager = manager;
		this.canvas = canvas;
		stop = false;
	}

	public void run ()
	{
		// Process old commands before we start polling for
		// new ones
		try
		{
			// Read transcript into array
			String[] lines = Files.readAllLines(transcript, Charset.defaultCharset()).toArray(new String[0]);
			
			// Find command prompts and process extract response text
			int index = -1;
			for (int i = 0; i < lines.length; i++)
			{	
				if (!lines[i].isEmpty() && lines[i].charAt(0) == '>')
				{
					if (index != -1)
					{
						Compass dir = Compass.fromString(lines[index].substring(1));
						if (dir != null)
						{
							// Find Room name and description then create room
							String[] text = Arrays.copyOfRange(lines, index, i-1, String[].class);
							int name = extractRoomName(text);
							if (name > 0)
							{
								String desc = extractParagraph(Arrays.copyOfRange(text, name, text.length-1, String[].class));
																
								if (dir == Compass.NONE)
									manager.look(text[name], desc);
								else
									manager.move(dir, text[name], desc);
							}
						}
					}
					
					index = i;
				}
			}
		}
		catch (IOException x)
			{System.out.println(x);}
		
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
				{canvas.redraw();}
		});

		
		// Originally tried to use WatchService to monitor the
		// file but it turns out that WatchService was only
		// being notified when the transcript was done. I
		// suspect it has something to do with the timestamp
		// not being updated. So instead we poll the file
		// size for changes every second.
		
		try
		{
			// Set the inital size of transcript file
			long size = Files.size(transcript);
			
			// Setting the flag stop to true will cause the while
			// check to fail and the thread to exit
			while (!stop)
			{
				try{sleep(1000);}
				catch (InterruptedException x)
					{System.out.println(x);}
				
				// Check for changes
				if (size == Files.size(transcript))
					continue;
				else
					size = Files.size(transcript);
				
				// Extract last command and resulting text
				String tail = tail(transcript);
				String[] text = tail.split("\n");
		
				// String to Compass type
				Compass dir = Compass.fromString(text[0]);
				if (dir == null)
					continue;
				
				int name = extractRoomName(text);
				if (name < 0)
					continue;
				
				String desc = extractParagraph(Arrays.copyOfRange(text, name, text.length-1, String[].class));
				
				if (dir == Compass.NONE)
					manager.look(text[name], desc);
				else
					manager.move(dir, text[name], desc);
				
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
						{canvas.redraw();}
				});
			}
		} catch (IOException x)
			{System.out.println(x);}
	}
	
	private int extractRoomName (String[] text)
	{
		for (int i = 1; i < text.length; i++)
		{
			String name = text[i];
			
			// The line before a room name will ether be a prompt,
			// or only white spaces (blank line).
			if (!(i == 1 || text[i-1].trim().length() == 0))
				continue;
			
			// Strip suffixes such as those in "Bedroom, on the bed",
			// "Bedroom (on the bed)", or "Bedroom - on the bed".
			name.replaceFirst("[,(\\[{-].{0,30}$", "");
			
			name = name.trim();
			
			// Is a proper title.
			if (name.matches(nameRegex))
			{
				text[i] = name;
				return i;
			}
		}
		
		return -1;
	}
	
	private String extractParagraph (String[] text)
	{
		StringBuilder paragraph = new StringBuilder();
		for (int i = 1; i < text.length; i++)
		{
			if (text[i].trim().isEmpty() || text[i].charAt(0) == '>')
				break;
			else
				paragraph.append(text[i].trim());
			
			if (text[i].length() < 65)
				break;
		}
		
		return paragraph.toString();
	}
	
	// TO DO: update to java 7's new nio.file api
	private static String tail (Path path)
	{
		RandomAccessFile fileHandler = null;
		try {
			fileHandler = new RandomAccessFile(path.toFile(), "r" );
			long fileLength = fileHandler.length() - 1;
			StringBuilder sb = new StringBuilder();
	
			for(long filePointer = fileLength; filePointer != -1; filePointer--){
				fileHandler.seek( filePointer );
				char readByte = (char)fileHandler.readByte();
	
				if( readByte == '>' ) {
					if( filePointer == fileLength ) {
						continue;
					} else {
						break;
					}
				}
	
				sb.append( ( char ) readByte );
			}
	
			String lastLine = sb.reverse().toString();
			return lastLine;
		} catch( java.io.FileNotFoundException e ) {
			e.printStackTrace();
			return null;
		} catch( java.io.IOException e ) {
			e.printStackTrace();
			return null;
		} finally {
			if (fileHandler != null )
				try {
					fileHandler.close();
				} catch (IOException e) {
					/* ignore */
				}
		}
	}
}