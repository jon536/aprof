/*
 * Aprof - Java Memory Allocation Profiler
 * Copyright (C) 2002-2014  Devexperts LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.aprof;

/**
 * @author Roman Elizarov
 */
public class TestClassNames {
	public static void main(String[] args) {
		System.out.println(int.class.getName());
		System.out.println(int[].class.getName());
		System.out.println(int[][].class.getName());
		System.out.println(long.class.getName());
		System.out.println(long[].class.getName());
		System.out.println(long[][].class.getName());
		System.out.println(Object.class.getName());
		System.out.println(Object[].class.getName());
		System.out.println(Object[][].class.getName());
	}
}
