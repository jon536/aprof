/*
 *  Aprof - Java Memory Allocation Profiler
 *  Copyright (C) 2002-2012  Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.aproftest;

import com.devexperts.aprof.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author Dmitry Paraschenko
 */
public class SelfTest {
	public static void main(String[] args) {
		System.out.println("Testing " + Version.get());

		// clearing STATISTICS
		AProfRegistry.makeSnapshot(new Snapshot());

		long time = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			if (i % 10000 == 0)
				System.out.println(i);
			Tracked.a(1);
			Tracked.a(2);
			Tracked.a(3);
			b(1);
			b(2);
			b(3);
			new Entity1();
			new Entity2();
			new Entity3();
			new DupableEntity();
		}
		System.out.printf("SelfTest took %d ms\n", System.currentTimeMillis() - time);

		// retrieving STATISTICS
		Snapshot snapshot = new Snapshot();
		AProfRegistry.makeSnapshot(snapshot);

		for (int i = 0; i < snapshot.getUsed(); i++) {
			Snapshot child = snapshot.getItem(i);
			if (!child.getId().startsWith("com.devexperts.aproftest.SelfTest$Entity")) {
				child.clearDeep();
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(bos);
		Configuration configuration = AProfRegistry.getConfiguration();
		if (configuration == null) {
			System.out.println("SelfTest should be run under Aprof!!!");
			return;
		}
		
		if (!configuration.isLocationTracked(TRACK + ".*")) {
			System.out.println("SelfTest should be run with option track=" + TRACK);
			return;
		}
		new DumpFormatter(configuration).dumpSection(out, snapshot, 0);
		out.flush();
		String result = new String(bos.toByteArray());
		result = result.substring(result.indexOf('\n')).trim();
		result = result.substring(result.indexOf('\n')).trim();

		String receivedResult = result;
		String statistics = STATISTICS.trim();
		for (int lid = 0; ; lid++) {
			if (result.isEmpty() || statistics.isEmpty()) {
				break;
			}
			int rpos = result.indexOf('\n');
			if (rpos < 0)
				rpos = result.length();
			String rline = result.substring(0, rpos).trim();
			result = result.substring(rpos).trim();
			int spos = statistics.indexOf('\n');
			if (spos < 0)
				spos = statistics.length();
			String sline = statistics.substring(0, spos).trim();
			statistics = statistics.substring(spos).trim();

			if (!rline.equals(sline)) {
				System.out.println(lid + ": received: ");
				System.out.println(rline);
				System.out.println(lid + ": expected: ");
				System.out.println(sline);
			}

			if (result.isEmpty() != statistics.isEmpty()) {
				System.out.println("end: received: ");
				System.out.println(rline);
				System.out.println("end: expected: ");
				System.out.println(sline);
			}
		}
		
		if (result.isEmpty() && statistics.isEmpty()) {
			System.out.println("SelfTest passed");
		} else {
			System.err.println("SelfTest failed");
			System.err.println("Expected result:");
			System.err.println(receivedResult);
			System.out.println("SelfTest failed. Collected allocations were printed to stderr");
		}
	}

	private static void b(int i) {
		if (i <= 0) {
			new Entity1();
			new Entity2();
			new Entity3();
			new DupableEntity().dup();
			Entity2[] arr = new Entity2[20];
			arr.clone();
		} else {
			Tracked.a(i - 1);
			b(i - 1);
		}
	}

	static class Tracked extends Entity1 {
		public Tracked() {
			super(new DupableEntity());
		}

		private static void a(int i) {
			if (i <= 0) {
				new Entity1();
				new Entity2();
				new Entity3();
				new DupableEntity().dup();
				Entity2[] arr = new Entity2[20];
				arr.clone();
			} else {
				a(i - 1);
				b(i - 1);
			}
		}
		
		private static DupableEntity wrap(int value) {
			new Entity1();
			new Entity2();
			return new DupableEntity().dup();
		}
	}

	private static class Entity1 {
		public Entity1() {
		}

		public Entity1(DupableEntity e) {
		}
	}

	private static class Entity2 {
		private Entity2() {
			new DupableEntity().dup();
		}
	}

	private static class Entity3 extends Entity1 {
		private Entity3() {
			super(Tracked.wrap(1));
			new DupableEntity().dup();
		}
	}

	private static class DupableEntity implements Cloneable {
		public DupableEntity dup() {
			try {
				return (DupableEntity)clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
	
	private static String TRACK = SelfTest.class.getCanonicalName() + "$" + Tracked.class.getSimpleName();
	
	private static String STATISTICS =
			"com.devexperts.aproftest.SelfTest$DupableEntity: 116,000,000 (49%) bytes in 14,500,000 (49%) objects (avg size 8 bytes)\n" +
			"\tcom.devexperts.aproftest.SelfTest$Entity2.<init>: 46,400,000 (40%) bytes in 5,800,000 (40%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 40,000,000 (86%) bytes in 5,000,000 (86%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 22,400,000 (56%) bytes in 2,800,000 (56%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 17,600,000 (44%) bytes in 2,200,000 (44%) objects\n" +
			"\t\t<unknown>: 3,200,000 (6%) bytes in 400,000 (6%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.wrap: 3,200,000 (6%) bytes in 400,000 (6%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest$Entity3.<init>: 3,200,000 (100%) bytes in 400,000 (100%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest$Entity3.<init>: 23,200,000 (20%) bytes in 2,900,000 (20%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 20,000,000 (86%) bytes in 2,500,000 (86%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 11,200,000 (56%) bytes in 1,400,000 (56%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 8,800,000 (44%) bytes in 1,100,000 (44%) objects\n" +
			"\t\t<unknown>: 3,200,000 (13%) bytes in 400,000 (13%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest$Tracked.wrap: 23,200,000 (20%) bytes in 2,900,000 (20%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 20,000,000 (86%) bytes in 2,500,000 (86%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 11,200,000 (56%) bytes in 1,400,000 (56%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 8,800,000 (44%) bytes in 1,100,000 (44%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.wrap: 3,200,000 (13%) bytes in 400,000 (13%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest$Entity3.<init>: 3,200,000 (100%) bytes in 400,000 (100%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (9%) bytes in 1,400,000 (9%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (100%) bytes in 1,400,000 (100%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.b: 11,200,000 (9%) bytes in 1,400,000 (9%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 8,800,000 (78%) bytes in 1,100,000 (78%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (63%) bytes in 700,000 (63%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 3,200,000 (36%) bytes in 400,000 (36%) objects\n" +
			"\t\t<unknown>: 2,400,000 (21%) bytes in 300,000 (21%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.main: 800,000 (0%) bytes in 100,000 (0%) objects\n" +
			"\n" +
			"com.devexperts.aproftest.SelfTest$Entity1: 46,400,000 (19%) bytes in 5,800,000 (19%) objects (avg size 8 bytes)\n" +
			"\tcom.devexperts.aproftest.SelfTest$Tracked.wrap: 23,200,000 (50%) bytes in 2,900,000 (50%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 20,000,000 (86%) bytes in 2,500,000 (86%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 11,200,000 (56%) bytes in 1,400,000 (56%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 8,800,000 (44%) bytes in 1,100,000 (44%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.wrap: 3,200,000 (13%) bytes in 400,000 (13%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest$Entity3.<init>: 3,200,000 (100%) bytes in 400,000 (100%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (24%) bytes in 1,400,000 (24%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (100%) bytes in 1,400,000 (100%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.b: 11,200,000 (24%) bytes in 1,400,000 (24%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 8,800,000 (78%) bytes in 1,100,000 (78%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (63%) bytes in 700,000 (63%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 3,200,000 (36%) bytes in 400,000 (36%) objects\n" +
			"\t\t<unknown>: 2,400,000 (21%) bytes in 300,000 (21%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.main: 800,000 (1%) bytes in 100,000 (1%) objects\n" +
			"\n" +
			"com.devexperts.aproftest.SelfTest$Entity2: 46,400,000 (19%) bytes in 5,800,000 (19%) objects (avg size 8 bytes)\n" +
			"\tcom.devexperts.aproftest.SelfTest$Tracked.wrap: 23,200,000 (50%) bytes in 2,900,000 (50%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 20,000,000 (86%) bytes in 2,500,000 (86%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 11,200,000 (56%) bytes in 1,400,000 (56%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 8,800,000 (44%) bytes in 1,100,000 (44%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.wrap: 3,200,000 (13%) bytes in 400,000 (13%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest$Entity3.<init>: 3,200,000 (100%) bytes in 400,000 (100%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (24%) bytes in 1,400,000 (24%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (100%) bytes in 1,400,000 (100%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.b: 11,200,000 (24%) bytes in 1,400,000 (24%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 8,800,000 (78%) bytes in 1,100,000 (78%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (63%) bytes in 700,000 (63%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 3,200,000 (36%) bytes in 400,000 (36%) objects\n" +
			"\t\t<unknown>: 2,400,000 (21%) bytes in 300,000 (21%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.main: 800,000 (1%) bytes in 100,000 (1%) objects\n" +
			"\n" +
			"com.devexperts.aproftest.SelfTest$Entity3: 23,200,000 (9%) bytes in 2,900,000 (9%) objects (avg size 8 bytes)\n" +
			"\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (48%) bytes in 1,400,000 (48%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 11,200,000 (100%) bytes in 1,400,000 (100%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (50%) bytes in 700,000 (50%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.b: 11,200,000 (48%) bytes in 1,400,000 (48%) objects\n" +
			"\t\tcom.devexperts.aproftest.SelfTest$Tracked.a: 8,800,000 (78%) bytes in 1,100,000 (78%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.main: 5,600,000 (63%) bytes in 700,000 (63%) objects\n" +
			"\t\t\tcom.devexperts.aproftest.SelfTest.b: 3,200,000 (36%) bytes in 400,000 (36%) objects\n" +
			"\t\t<unknown>: 2,400,000 (21%) bytes in 300,000 (21%) objects\n" +
			"\tcom.devexperts.aproftest.SelfTest.main: 800,000 (3%) bytes in 100,000 (3%) objects";
}