package net.sf.hastee.util;

import org.eclipse.emf.ecore.EObject;

/**
 * This class contains helper methods for Ecore models.
 * 
 * @author Matthieu Wipliez
 * @author Herve Yviquel
 * 
 */
public class EcoreHelper {

	/**
	 * Returns the container of <code>ele</code> with the given type, or
	 * <code>null</code> if no such container exists. This method has been
	 * copied from the EcoreUtil2 class of Xtext.
	 * 
	 * @param <T>
	 *            type parameter
	 * @param ele
	 *            an object
	 * @param type
	 *            the type of the container
	 * @return the container of <code>ele</code> with the given type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EObject> T getContainerOfType(EObject ele,
			Class<T> type) {
		if (type.isAssignableFrom(ele.getClass())) {
			return (T) ele;
		}

		if (ele.eContainer() != null) {
			return getContainerOfType(ele.eContainer(), type);
		}

		return null;
	}
}
