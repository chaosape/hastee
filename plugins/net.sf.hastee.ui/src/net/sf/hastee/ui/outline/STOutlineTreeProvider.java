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
package net.sf.hastee.ui.outline;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.Group;

import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

/**
 * customization of the default outline structure
 * 
 */
public class STOutlineTreeProvider extends DefaultOutlineTreeProvider {

	/**
	 * This method creates nodes for the members of the group, but not for the
	 * group itself.
	 * 
	 * @param parentNode
	 *            parent outline node
	 * @param group
	 *            group
	 */
	protected void _createNode(IOutlineNode parentNode, Group group) {
		for (Declaration object : group.getMembers()) {
			createNode(parentNode, object);
		}
	}

	/**
	 * This method identifies a NamedObject as a leaf node, so no children are
	 * created under it.
	 * 
	 * @param namedObject
	 * @return <code>true</code>
	 */
	protected boolean _isLeaf(Declaration namedObject) {
		return true;
	}

}
