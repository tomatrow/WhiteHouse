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

import java.util.Map;
import java.util.HashMap;
import java.awt.Rectangle;

public class Room extends Rectangle
{
	// Additional properties not contained in Rectangle
	private String name;
	private String desc;
	public int z;
	
	// Flag to prevent multiple paints per paint event
	public boolean paint = false;

	// List of things connected
	private Map<Compass,Room> neighbors = new HashMap<Compass,Room>(10);
	private Map<Compass,Connection> connections = new HashMap<Compass,Connection>(10);

	public Room ()
	{
		width = 120;
		height = 80;
	}
	
	public void setNeighbor (Compass dir, Room room)
	{
		// Link Room, create Connection and connect it
		neighbors.put(dir, room);
		Connection connection = new Connection();
		connection.a = this;
		connection.aSide = dir;
		setConnection(dir, connection);
		
		// Link self, connect Connection
		dir = Compass.invert(dir);
		room.neighbors.put(dir,this);
		connection.b = room;
		connection.bSide = dir;
		room.setConnection(dir, connection);
	}
	
	/* Simple getters and setters */
	
	public void setName (String name)
	{
		this.name = name;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void setDesc (String desc)
	{
		this.desc = desc;
	}
	
	public String getDesc ()
	{
		return desc;
	}
	
	public Room getNeighbor (Compass dir)
	{
		return neighbors.get(dir);
	}
	
	public void setConnection (Compass dir, Connection connection)
	{
		connections.put(dir, connection);
	}
	
	public Connection getConnection (Compass dir)
	{
		return connections.get(dir);
	}
}