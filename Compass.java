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

/*
	A type class to signify what direction things are.
*/

package com.github.redhatter.whitehouse;

public enum Compass
{
	NORTH,
	EAST,
	SOUTH,
	WEST,
	NORTHEAST,
	NORTHWEST,
	SOUTHEAST,
	SOUTHWEST,
	UP,
	DOWN,
	NONE;
	
	public static Compass invert (Compass dir)
	{
		switch (dir)
		{
			case NORTH:
				return Compass.SOUTH;
			case EAST:
				return Compass.WEST;
			case SOUTH:
				return Compass.NORTH;
			case WEST:
				return Compass.EAST;
			case NORTHEAST:
				return Compass.SOUTHWEST;
			case NORTHWEST:
				return Compass.SOUTHEAST;
			case SOUTHEAST:
				return Compass.NORTHWEST;
			case SOUTHWEST:
				return Compass.NORTHEAST;
			case UP:
				return Compass.DOWN;
			case DOWN:
				return Compass.UP;
		}
		
		return dir;
	}
	
	public static Compass fromString (String command)
	{
		if (command.startsWith("go "))
			command = command.substring(3);
		
		switch (command.toLowerCase())
		{
			case "n":
			case "north":
				return Compass.NORTH;
			case "e":
			case "east":
				return Compass.EAST;
			case "s":
			case "south":
				return Compass.SOUTH;
			case "w":
			case "west":
				return Compass.WEST;
			case "ne":
			case "northeast":
				return Compass.NORTHEAST;
			case "nw":
			case "northwest":
				return Compass.NORTHWEST;
			case "se":
			case "southeast":
				return Compass.SOUTHEAST;
			case "sw":
			case "southwest":
				return Compass.SOUTHWEST;
			case "u":
			case "up":
				return Compass.UP;
			case "d":
			case "down":
				return Compass.DOWN;
			case "l":
			case "look":
				return Compass.NONE;
			default:
				return null;
		}
	}
	
	public static String toString (Compass compass)
	{
		switch (compass)
		{
			case NORTH:
				return "north";
			case EAST:
				return "east";
			case SOUTH:
				return "south";
			case WEST:
				return "west";
			case NORTHEAST:
				return "northeast";
			case NORTHWEST:
				return "northwest";
			case SOUTHEAST:
				return "southeast";
			case SOUTHWEST:
				return "southwest";
			case UP:
				return "up";
			case DOWN:
				return "down";
			case NONE:
			default:
				return null;
		}
	}
}