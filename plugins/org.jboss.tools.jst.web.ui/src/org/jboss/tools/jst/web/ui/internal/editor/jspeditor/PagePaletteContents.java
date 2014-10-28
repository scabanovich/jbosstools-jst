/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jst.web.ui.internal.editor.jspeditor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.jboss.tools.common.model.ui.views.palette.PaletteContents;
import org.jboss.tools.jst.web.kb.internal.JQueryMobileRecognizer;
import org.jboss.tools.jst.web.kb.taglib.ITagLibRecognizer;
import org.jboss.tools.jst.web.ui.WebUiPlugin;
import org.jboss.tools.jst.web.ui.palette.model.PaletteModel;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class PagePaletteContents extends PaletteContents {
	IEditorPart editorPart;
	Map<String, CategoryVersion> categoryVersions = new HashMap<String, CategoryVersion>();
	Set<String> recognizedCategories = new HashSet<String>();

	public PagePaletteContents(IEditorPart editorPart) {
		super(editorPart);
		this.editorPart = editorPart;

		categoryVersions.put("jQuery Mobile", new CategoryVersion(new JQueryVersionComputer()));

		computeVersions();
		computeRecognized();
	}

	public IEditorPart getEditorPart() {
		return editorPart;
	}

	public IFile getFile() {
		if(editorPart != null) {
			IEditorInput input = editorPart.getEditorInput();
			if (input instanceof IFileEditorInput) {
				return ((IFileEditorInput)input).getFile();
			}
		}
		return null;
	}

	public boolean update() {
		boolean r = super.update();
		if(computeVersions()) {
			computeRecognized();
			r = true;
		}		
		return r;
	}

	public void dispose() {
		PaletteModel.disposeInstance(this);
		editorPart = null;
	}

	boolean computeVersions() {
		boolean result = false;
		IFile file = getFile();
		String[] types = getNatureTypes();
		if(types.length > 0 && PaletteModel.TYPE_HTML5.equals(types[0])) {
			if(file != null) {
				for (String categoryName: categoryVersions.keySet()) {
					CategoryVersion v = categoryVersions.get(categoryName);
					if(v.computeVersion()) {
						result = true;
					}
				}
			}
		}
		
		return result;
	}

	boolean computeRecognized() {
		boolean result = false;
		IFile file = getFile();
		String[] types = getNatureTypes();
		if(types.length > 0 && PaletteModel.TYPE_HTML5.equals(types[0])) {
			if(file != null) {
				Set<String> rc = new HashSet<String>();
				Map<String, ITagLibRecognizer> rs = getTaglibRecognizers();
				for (Map.Entry<String, ITagLibRecognizer> e: rs.entrySet()) {
					String name = e.getKey();
					ITagLibRecognizer r = e.getValue();
					if(r.isProbablyUsed(file)) {
						rc.add(name);
					}
				}
				if(!setsAreEqual(recognizedCategories, rc)) {
					recognizedCategories = rc;
					result = true;
				}
			}
		}
		
		return result;
	}

	boolean mapsAreEqual(Map<String, String> map1, Map<String, String> map2) {
		if(map1.size() != map2.size()) {
			return false;
		}
		for (String k: map2.keySet()) {
			if(!map1.containsKey(k)) {
				return false;
			}
			if(!map1.get(k).equals(map2.get(k))) {
				return false;
			}
		}
		return true;
	}

	boolean setsAreEqual(Set<String> map1, Set<String> map2) {
		if(map1.size() != map2.size()) {
			return false;
		}
		for (String k: map2) {
			if(!map1.contains(k)) {
				return false;
			}
		}
		return true;
	}

	public String getVersion(String category) {
		CategoryVersion v = categoryVersions.get(category);
		return v == null ? null : v.getVersion();
	}

	public boolean isRecognized(String category) {
		return recognizedCategories.contains(category);
	}

	public void setPreferredVersion(String category, String version) {
		CategoryVersion v = categoryVersions.get(category);
		if(v != null && v.preferredVersion != version) {
			v.preferredVersion = version;
		}
	}

	class CategoryVersion {
		VersionComputer versionComputer;
		String detectedVersion = null;
		String preferredVersion = null;

		public CategoryVersion(VersionComputer versionComputer) {
			this.versionComputer = versionComputer;
		}

		public String getVersion() {
			return(preferredVersion != null) ? preferredVersion : detectedVersion;
		}

		public boolean computeVersion() {
			String newVersion = versionComputer.computeVersion(getFile());
			boolean isNew = !isSame(newVersion);
			if(isNew) {
				preferredVersion = null;
				detectedVersion = newVersion;
			}
			return isNew;
		}

		private boolean isSame(String newVersion) {
			return (detectedVersion == null) ? newVersion == null : detectedVersion.equals(newVersion);
		}		 
	}

	static String TAGLIB_RECOGNIZERS = "org.jboss.tools.jst.web.ui.taglibRecognizers";
	static Map<String, ITagLibRecognizer> taglibRecognizers = null;

	static Map<String, ITagLibRecognizer> getTaglibRecognizers() {
		if(taglibRecognizers == null) {
			Map<String, ITagLibRecognizer> tr = new HashMap<String, ITagLibRecognizer>();
			IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(TAGLIB_RECOGNIZERS);
		    for(IExtension extension: point.getExtensions()) {
		        for(IConfigurationElement configurationElement: extension.getConfigurationElements()) {
		        	String name = configurationElement.getAttribute("name");
		        	try {
		        		Object o = configurationElement.createExecutableExtension("class");
		        		tr.put(name, (ITagLibRecognizer)o);
		        	} catch (CoreException e) {
		        		WebUiPlugin.getDefault().logError("Cannot load taglib recognizer for " + name, e);
		        	}
		        }
		    }
		    taglibRecognizers = tr;
		}
		return taglibRecognizers;
	}

}

interface VersionComputer {
	public String computeVersion(IFile file);
}

class JQueryVersionComputer implements VersionComputer {
	static String prefix = "jquery.mobile-";

	@Override
	public String computeVersion(IFile file) {
		return JQueryMobileRecognizer.getVersion(file);
	}
}