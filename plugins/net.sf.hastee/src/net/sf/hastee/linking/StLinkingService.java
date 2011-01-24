package net.sf.hastee.linking;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hastee.st.Attribute;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.TemplateNamed;
import net.sf.hastee.st.StFactory;
import net.sf.hastee.st.StPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.util.OnChangeEvictingCache;

/**
 * This class defines a linking service for built-in functions/procedures and
 * FSM states.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class StLinkingService extends DefaultLinkingService {

	private Map<String, TemplateNamed> functions;

	private Attribute i0;

	private Resource stubsResource = null;

	/**
	 * Creates a new ST linking service which creates builtin functions.
	 */
	public StLinkingService() {
		functions = new HashMap<String, TemplateNamed>();

		addFunction("first");
		addFunction("last");
		addFunction("rest");
		addFunction("trunc");
		addFunction("strip");
		addFunction("length");

		i0 = StFactory.eINSTANCE.createAttribute();
		i0.set_name("i0");
	}

	/**
	 * Adds a new function to the built-in functions map with the given
	 * parameters types and return type.
	 * 
	 * @param name
	 *            function name
	 * @param parameters
	 *            types of function parameters
	 * @param returnType
	 *            return type
	 */
	private void addFunction(String name) {
		TemplateNamed template;
		template = StFactory.eINSTANCE.createTemplateNamed();
		template.setName(name);
		functions.put(name, template);
	}

	/**
	 * Returns a singleton if <code>name</code> is a builtin function, and an
	 * empty list otherwise.
	 * 
	 * @param context
	 *            the context in which a function is referenced.
	 * @param name
	 *            function name
	 * @return a list
	 */
	private List<EObject> getBuiltinFunction(EObject context, String name) {
		TemplateNamed function = functions.get(name);
		if (function != null) {
			// Attach the stub to the resource that's being parsed
			Resource res = makeResource(context.eResource());
			res.getContents().add(function);

			return Collections.singletonList((EObject) function);
		}

		return Collections.emptyList();
	}

	private List<EObject> getImplicitAttribute(EObject context, String s) {
		// a TemplateNamed may reference attributes from other templates that
		// can call it
		TemplateNamed template = EcoreUtil2.getContainerOfType(context,
				TemplateNamed.class);
		Group group = (Group) template.eContainer();
		OnChangeEvictingCache.CacheAdapter cache = new OnChangeEvictingCache()
				.getOrCreate(group);
		Map<TemplateNamed, Map<String, Attribute>> map = cache
				.get("AttributeMap");
		if (map == null) {
			map = new ImplicitAttributeSolver().buildAttributeMap(group);
			cache.set("AttributeMap", map);
		}

		Map<String, Attribute> implicitAttrs = map.get(template);
		if (implicitAttrs != null) {
			Attribute attr = implicitAttrs.get(s);
			if (attr != null) {
				return Collections.singletonList((EObject) attr);
			}
		}

		if (s.equals("i0") || s.equals("i1")) {
			Resource res = makeResource(context.eResource());
			res.getContents().add(i0);
			return Collections.singletonList((EObject) i0);
		}

		return Collections.emptyList();
	}

	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference ref,
			AbstractNode node) {
		List<EObject> result = super.getLinkedObjects(context, ref, node);
		if (result != null && !result.isEmpty()) {
			return result;
		}

		final EClass requiredType = ref.getEReferenceType();
		final String s = getCrossRefNodeAsString(node);
		if (requiredType != null && s != null) {
			if (StPackage.Literals.TEMPLATE_NAMED.isSuperTypeOf(requiredType)) {
				return getBuiltinFunction(context, s);
			} else if (StPackage.Literals.ATTRIBUTE.isSuperTypeOf(requiredType)) {
				return getImplicitAttribute(context, s);
			}
		}

		return Collections.emptyList();
	}

	/**
	 * Use a temporary 'child' resource to hold created stubs. The real resource
	 * URI is used to generate a 'temporary' resource to be the container for
	 * stub EObjects.
	 * 
	 * @param source
	 *            the real resource that is being parsed
	 * @return the cached reference to a resource named by the real resource
	 *         with the added extension 'xmi'
	 */
	private Resource makeResource(Resource source) {
		if (null != stubsResource)
			return stubsResource;
		URI stubURI = source.getURI().appendFileExtension("xmi");

		stubsResource = source.getResourceSet().getResource(stubURI, false);
		if (null == stubsResource) {
			// TODO find out if this should be cleaned up so as not to clutter
			// the project.
			stubsResource = source.getResourceSet().createResource(stubURI);
		}

		return stubsResource;
	}

}
