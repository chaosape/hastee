/*
 * Copyright (c) 2011, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * This file is part of Hastee.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.hastee;

import net.sf.hastee.st.Element;
import net.sf.hastee.st.Template;
import net.sf.hastee.st.TextContents;

/**
 * This class defines utility methods.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class Util {

	public static String toString(Template template) {
		StringBuilder builder = new StringBuilder();
		for (Element element : template.getElements()) {
			if (element instanceof TextContents) {
				TextContents contents = (TextContents) element;
				builder.append(contents.getContents());
			}
		}
		return builder.toString();
	}

}
