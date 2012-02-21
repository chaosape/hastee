/*
 * Copyright (c) 2012, Synflow
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
package net.sf.hastee.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.xtext.ui.editor.preferences.LanguageRootPreferencePage;

/**
 * This class defines preferences for ST (at the moment only strict mode is
 * available).
 * 
 * @author Matthieu Wipliez
 * 
 */
public class StPreferencePage extends LanguageRootPreferencePage {

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor("strict", "&Strict mode:",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		getPreferenceStore().setDefault("strict", true);
	}

}
