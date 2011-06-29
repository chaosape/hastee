/*
 * generated by Xtext
 */
package net.sf.hastee.ui.outline;

import net.sf.hastee.st.Group;
import net.sf.hastee.st.TopDeclaration;

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
		for (TopDeclaration object : group.getMembers()) {
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
	protected boolean _isLeaf(TopDeclaration namedObject) {
		return true;
	}

}
