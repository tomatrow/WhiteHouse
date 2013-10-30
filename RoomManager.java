/**
 *	Copyright (c) 2013 by Christian Johnson (_c_@mail.com)
 *	
 *	This file is part of MappingCubed, the Interactive Fiction Mapper.
 *	
 *	MappingCubed is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	MappingCubed is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with MappingCubed.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.MessageBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.Rectangle;

/*
	RoomManager handles connecting the
	Rooms and Connections and treating
	them as a whole	strcture.
*/

public class RoomManager
{
	// List of all rooms, what we draw on,
	// and the space between rooms
	private Collection<Room> rooms = new HashSet<Room>();
	private Shell shell;
	private int SPACE = 2;
	
	// Room the player is currently in
	public Room location;
	
	public RoomManager (Shell shell)
	{
		this.shell = shell;
	}

	// Create room with no connections and no neighbors
	// TODO: Should also find room
	public Room look (String name, String desc)
	{
		if (rooms.isEmpty())
		{
			Room room = new Room();
			rooms.add(room);
			room.setName(name);
			room.setDesc(desc);
			findStubs(room, desc);
			room.x = SPACE;
			room.y = SPACE;
			location = room;
		} else
		{
			Room look = findRoom(name, desc);
			if (look != null)
				location = look;
			else
			{
				Room room = new Room();
				rooms.add(room);
				room.setName(name);
				room.setDesc(desc);
				findStubs(room, desc);
				room.x = SPACE;
				room.y = SPACE;
				clear(room);
				location = room;
			}	
		}
		
		return location;
	}
	
	// Create new room, or move along structure in direction
	public Room move (final Compass dir, final String name, final String desc)
	{
		// No room has been created, create a new one
		if (rooms.isEmpty())
		{
			Room room = new Room();
			rooms.add(room);
			room.setName(name);
			room.setDesc(desc);
			findStubs(room, desc);
			room.x = SPACE;
			room.y = SPACE;
			location = room;
		}
		// New Room in direction
		else if (location.getNeighbor(dir) == null)
		{
			Room room = new Room();
			rooms.add(room);
			room.setName(name);
			room.setDesc(desc);
			findStubs(room, desc);
			
			// Calculate location
			switch (dir)
			{
				case NORTH:
					room.x = location.x;
					room.y = location.y - room.height - SPACE;
					room.z = location.z;
					break;
				case EAST:
					room.x = location.x + location.width + SPACE;
					room.y = location.y;
					room.z = location.z;
					break;
				case SOUTH:
					room.x = location.x;
					room.y = location.y + location.height + SPACE;
					room.z = location.z;
					break;
				case WEST:
					room.x = location.x - room.width - SPACE;
					room.y = location.y;
					room.z = location.z;
					break;
				case NORTHEAST:
					room.x = location.x + location.width + SPACE;
					room.y = location.y - room.height - SPACE;
					room.z = location.z;
					break;
				case NORTHWEST:
					room.x = location.x - room.width - SPACE;
					room.y = location.y - room.height - SPACE;
					room.z = location.z;
					break;
				case SOUTHEAST:
					room.x = location.x + location.width + SPACE;
					room.y = location.y + location.height + SPACE;
					room.z = location.z;
					break;
				case SOUTHWEST:
					room.x = location.x - room.width - SPACE;
					room.y = location.y + location.height + SPACE;
					room.z = location.z;
					break;
				case UP:
					room.x = location.x;
					room.y = location.y;
					room.z = location.z + 1;
					break;
				case DOWN:
					room.x = location.x;
					room.y = location.y;
					room.z = location.z - 1;
					break;
			}
			
			// Fix any overlaps, set connecting neighbors,
			// and move to Room
			shift(room, dir);
			location.setNeighbor(dir, room);
			location = room;
		} else
		{
			// Nothing needs to be created, move to room
			final Room room = findRoom(name, desc);
			if (room == location.getNeighbor(dir))
				location = location.getNeighbor(dir);
			else
			{
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						MessageBox alert = new MessageBox(shell, SWT.YES|SWT.NO);
						alert.setMessage("There is already a room in the direction you moved. Would you like to replace it with "+name+"?");
						if (alert.open() == SWT.YES)
						{
							if (room != null)
							{
								location.setNeighbor(dir, room);
								location = room;
							} else
							{
								Room room = new Room();
								rooms.add(room);
								room.setName(name);
								room.setDesc(desc);
								findStubs(room, desc);
								location.setNeighbor(dir, room);
								location = room;
							}
						}
					}
				});
			}
		}

		return location;
	}
	
	// Reset flags
	public void painted ()
	{
		Iterator<Room> iterator = rooms.iterator();
		while (iterator.hasNext())
		{
			iterator.next().paint = false;
		}
	}
	
	private Room findRoom (String name, String desc)
	{
		Iterator<Room> iterator = rooms.iterator();
		while (iterator.hasNext())
		{
			Room room = iterator.next();
			if (room.getName().equals(name) && room.getDesc().equals(desc))
				return room;
		}
		
		return null;
	}
	
	private void findStubs (Room room, String desc)
	{
		desc = desc.toLowerCase();
		for (Compass dir : Compass.values())
		{
			String sDir = Compass.toString(dir);
			if (sDir == null)
				return;

			int index = desc.indexOf(sDir);
			if (room.getConnection(dir) == null && index != -1
				&& (index == 0 || Character.getType(desc.charAt(index-1)) != Character.LETTER_NUMBER)
				&& (index+sDir.length() == desc.length() 
					|| Character.getType(desc.charAt(index+sDir.length())) != Character.LETTER_NUMBER))
			{
				Connection stub = new Connection();
				stub.a = room;
				stub.aSide = dir;
				room.setConnection(dir, stub);
			}
		}
	}
	
	// Fix overlap by shifting all rooms on that side
	private void shift (Room room, Compass dir)
	{
		// Fix rooms out side of canvas
		if (room.x < 0)
		{
			room.x = 2;
			dir = Compass.invert(dir);
		}
		
		if (room.y < 0)
		{
			room.y = 2;
			dir = Compass.invert(dir);
		}
		
		// Test every other room on this level for intersection
		Iterator<Room> iterator = rooms.iterator();
		while (iterator.hasNext())
		{
			Room element = iterator.next();
			if (element == room || element.z != room.z)
				continue;

			if (element.intersects(room))
			{
				// We can not use element to compair as we
				// change it with the others
				Rectangle compair = new Rectangle(element);
				
				// Element overlaps, shift all rooms on that side
				Rectangle i = element.intersection(room);
				Iterator<Room> shifter = rooms.iterator();
				while (shifter.hasNext())
				{
					Room shift = shifter.next();
					if (shift == room || shift.z != room.z)
						continue;
					
					// Calculate what direction and how much
					// to shift
					switch (dir)
					{
						case NORTH:
							if (shift.y <= compair.y)
								shift.y -= i.height + SPACE;
							break;
						case EAST:
							if (shift.x >= compair.x)
								shift.x += i.width + SPACE;
							break;
						case SOUTH:
							if (shift.y >= compair.y)
								shift.y += i.height + SPACE;
							break;
						case WEST:
							if (shift.x <= compair.x)
								shift.x  -= i.width + SPACE;
							break;
						case NORTHEAST:
							if (shift.y <= compair.y && shift.x >= compair.x)
							{
								shift.x += i.width + SPACE;
								shift.y -= i.height + SPACE;
							}
							break;
						case NORTHWEST:
							if (shift.y <= compair.y && shift.x <= compair.x)
							{
								shift.x  -= i.width + SPACE;
								shift.y -= i.height + SPACE;
							}
							break;
						case SOUTHEAST:
							if (shift.y >= compair.y && shift.x >= compair.x)
							{
								shift.x += i.width + SPACE;
								shift.y += i.height + SPACE;
							}
							break;
						case SOUTHWEST:
							if (shift.y >= compair.y && shift.x <= compair.x)
							{
								shift.x  -= i.width + SPACE;
								shift.y += i.height + SPACE;
							}
							break;
					}
				}
			}
		}
	}
	
	private void clear (Room room)
	{		
		// Test every other room on this level for intersection
		Iterator<Room> iterator = rooms.iterator();
		while (iterator.hasNext())
		{
			Room element = iterator.next();
			if (element == room || element.z != room.z)
				continue;
			
			// Element overlaps, shift room restart loop
			if (element.intersects(room))
			{
				room.y += element.intersection(room).height + SPACE;
				clear(room);
				return;
			}
		}
	}
}