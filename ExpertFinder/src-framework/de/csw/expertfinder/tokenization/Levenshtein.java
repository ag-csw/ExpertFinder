/*******************************************************************************
 * This file is part of the Corporate Semantic Web Project at Freie Universitaet Berlin.
 * 
 * This work has been partially supported by the ``InnoProfile-Corporate Semantic Web" project funded by the German Federal
 * Ministry of Education and Research (BMBF) and the BMBF Innovation Initiative for the New German Laender - Entrepreneurial Regions.
 * 
 * http://www.corporate-semantic-web.de/
 * 
 * Freie Universitaet Berlin
 * Copyright (c) 2007-2013
 * 
 * Institut fuer Informatik
 * Working Group Corporate Semantic Web
 * Koenigin-Luise-Strasse 24-26
 * 14195 Berlin
 * 
 * http://www.mi.fu-berlin.de/en/inf/groups/ag-csw/
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA or see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package de.csw.expertfinder.tokenization;

import java.util.List;

import de.csw.expertfinder.document.Word;

/* Levenshtein in Java, from Josh Drew's code at
 * http://joshdrew.com/
 * I wrote this code to help my understanding of
 * the MySQL UDF code, but didn't actually use it
 * in any project. It is completely untested.
 * The MySQL UDF code seems to work very well:
 * http://blog.lolyco.com/sean/2008/08/06/search-suggestions-with-mysql-levenshtein-distance/
 *
 * October 2008 sean at lolyco.com
 * March 29 2009 updated to correct swapped string length assignments
 *   - a single character mid-string deletion would return lim for example:
 *   - damlevlim("speling", "spelling", 3) would return 3 instead of 1
 * April 16 2009 fix for 'limit' handling for invocations like:
 *   - damlevlim('h', 'hello', 2) = 4 - should be 2!
 */

public class Levenshtein
{
	public static int lev(List<Word> s, List<Word> t)
	{
		int n0 = s.size();
		int m0 = t.size();
		int n = n0 + 1;
		int m = m0 + 1;
		
		if (m == 1)
			return n - 1;
		if (n == 1)
			return m -1;
		int[] d = new int[m * n];
		for (int i = 0; i < n; i++)
			d[i] = i;
		int k = n;
		for (int i = 1; i < m; i++)
		{
			d[k] = i;
			k += n;
		}
		int f = 0, g = 0, h = 0, min = 0, b = 0, c = 0, cost = 0;
		for (int i = 1; i < n; i++)
		{
			k = i;
			f = 0;
			for (int j = 1; j < m; j++)
			{
				h = k;
				k += n;
				min = d[h] + 1;
				b = d[k - 1] + 1;
				if (g < n0 && f < m0)
					cost = s.get(g).getWord().equals(t.get(f).getWord())?0:1;
				else
					cost = 1;
				c = d[h - 1] + cost;
				if (b < min)
					min = b;
				if (c < min)
					min = c;
				d[k] = min;
				/*
				System.out.println("i=" + i + ", j=" + j);
				for (int v = 0; v < m; v++)
				{
					for (int w = 0; w < n; w++)
						System.out.print(d[v * n + w] + " ");
					System.out.println();
				}
				*/
				f = j;
			}
			g = i;
		}
		return d[k];

	}
	public static int levlim(List<Word> s, List<Word> t, int limit)
	{
		int n0 = s.size();
		int m0 = t.size();
		int n = n0 + 1;
		int m = m0 + 1;
		
		if (m == 1)
			return n - 1;
		if (n == 1)
			return m -1;
		int[] d = new int[m * n];
		for (int i = 0; i < n; i++)
			d[i] = i;
		int k = n;
		for (int i = 1; i < m; i++)
		{
			d[k] = i;
			k += n;
		}
		int f = 0, g = 0, h = 0, min = 0, b = 0, c = 0, best = 0, cost = 0;
		for (int i = 1; i < n; i++)
		{
			k = i;
			f = 0;
			best = limit;
			for (int j = 1; j < m; j++)
			{
				h = k;
				k += n;
				min = d[h] + 1;
				b = d[k - 1] + 1;
				if (g < n0 && f < m0)
					cost = s.get(g).getWord().equals(t.get(f).getWord())?0:1;
				else
					cost = 1;
				c = d[h - 1] + cost;
				if (b < min)
					min = b;
				if (c < min)
					min = c;
				d[k] = min;
				/*
				System.out.println("i=" + i + ", j=" + j);
				for (int v = 0; v < m; v++)
				{
					for (int w = 0; w < n; w++)
						System.out.print(d[v * n + w] + " ");
					System.out.println();
				}
				*/
				if (min < best)
					best = min;
				f = j;
			}
			if (best >= limit)
				return limit;
			g = i;
		}
		if (d[k] >= limit)
			return limit;
		else
			return d[k];

	}
	public static int damlev(List<Word> s, List<Word> t)
	{
		int l1 = s.size();
		int l2 = t.size();
		int n = l1 + 1;
		int m = l2 + 1;
		if (m == 1)
			return n - 1;
		if (n == 1)
			return m -1;
		int[] d = new int[m * n];
		int k = 0;
		for (int i = 0; i < n; i++)
			d[i] = i;
		k = n;
		for (int i = 1; i < m; i++)
		{
			d[k] = i;
			k += n;
		}
		int f = 0, g = 0, h = 0, min = 0, b = 0, c = 0, cost = 0, tr = 0;
		for (int i = 1; i < n; i++)
		{
			k = i;
			f = 0;
			for (int j = 1; j < m; j++)
			{
				h = k;
				k += n;
				min = d[h] + 1;
				b = d[k - 1] + 1;
				if (g < l1 && f < l2)
					if (s.get(g).getWord().equals(t.get(f).getWord()))
						cost = 0;
					else
					{
						cost = 1;
						/* Sean's transposition */
						if (j < l2 && i < l1)
								if (s.get(i).getWord().equals(t.get(f).getWord()) && s.get(g).getWord().equals(t.get(j).getWord()))
								{
									tr = d[(h) - 1]/* + 1*/; // transposition yields deletion cost at next iteration?
									if (tr < min)
										min = tr;
								}
					}
				else
					cost = 1;
				c = d[h - 1] + cost;
				if (b < min)
					min = b;
				if (c < min)
					min = c;
				d[k] = min;
				/*
				System.out.println("i=" + i + ", j=" + j);
				for (int v = 0; v < m; v++)
				{
					for (int w = 0; w < n; w++)
						System.out.print(d[v * n + w] + " ");
					System.out.println();
				}
				*/
				f = j;
			}
			g = i;
		}
		return d[k];
	}
	public static int damlevlim(List<Word> s, List<Word> t, int limit)
	{
		int l1 = s.size();
		int l2 = t.size();
		int m = l1 + 1;
		int n = l2 + 1;
		if (m == 1)
			return (l2 < limit)?l2:limit;
		if (n == 1)
			return (l1 < limit)?l1:limit;
		// declare at instance level to avoid new
		// int[] d = new int[m * n];
		int[] d = new int[m * n];
		for (int i = 0; i < n; i++)
			d[i] = i;
		int k = n;
		for (int i = 1; i < m; i++)
		{
			d[k] = i;
			k += n;
		}
		int f = 0, g = 0, h = 0, min = 0, b = 0, best = 0, c = 0, cost = 0, tr = 0;
		for (int i = 1; i < n; i++)
		{
			k = i;
			f = 0;
			best = limit;
			for (int j = 1; j < m; j++)
			{
				h = k;
				k += n;
				min = d[h] + 1;
				b = d[k - 1] + 1;
				cost = 1;
				if (g < l1 && f < l2)
					if (s.get(g).getWord().equals(t.get(f).getWord()))
						cost = 0;
					/* Sean's transposition */
				if (cost == 1)
					if (i < l1 && j < l2)
							if (s.get(i).getWord().equals(t.get(f).getWord()) && s.get(g).getWord().equals(t.get(j).getWord()))
							{
								tr = d[h - 1]; // transposition yields deletion cost at next iteration?
								if (tr < min)
									min = tr;
							}
				// System.out.println(i + "," + j + "," + cost + "," + best + " ");
				c = d[h - 1] + cost;
				if (b < min)
					min = b;
				if (c < min)
				{
					d[k] = c;
					if (c < best)
						best = c;
				}
				else
				{
					d[k] = min;
					if (min < best)
						best = min;
				}
				/*
				System.out.println("i=" + i + ", j=" + j);
				for (int v = 0; v < m; v++)
				{
					for (int w = 0; w < n; w++)
						System.out.print(d[v * n + w] + " ");
					System.out.println();
				}
				System.out.println("best=" + best);
				*/
				f = j;
			}
			if (best >= limit)
				return limit;
			g = i;
		}
		if (d[k] >= limit)
			return limit;
		else
			return d[k];
	}
}
