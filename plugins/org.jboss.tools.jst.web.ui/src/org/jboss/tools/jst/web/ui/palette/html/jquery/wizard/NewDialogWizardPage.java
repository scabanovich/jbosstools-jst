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
package org.jboss.tools.jst.web.ui.palette.html.jquery.wizard;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.model.ui.editors.dnd.ValidationException;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.jst.web.ui.palette.html.wizard.AbstractNewHTMLWidgetWizardPage;
import org.jboss.tools.jst.web.ui.palette.html.wizard.WizardMessages;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewDialogWizardPage extends AbstractNewHTMLWidgetWizardPage implements JQueryConstants {

	public NewDialogWizardPage() {
		super("newDialog", WizardMessages.newDialogWizardTitle);
		setDescription(WizardMessages.newDialogWizardDescription);
	}

	protected void createFieldPanel(Composite parent) {
		IFieldEditor title = JQueryFieldEditorFactory.createTitleEditor();
		title.setValue("Dialog");
		addEditor(title, parent);

		IFieldEditor id = JQueryFieldEditorFactory.createIDEditor();
		addEditor(id, parent);

		IFieldEditor close = JQueryFieldEditorFactory.createCloseButtonEditor();
		addEditor(close, parent);
		
		final Shell shell = parent.getShell();
		
		shell.addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent e) {
				Rectangle r = shell.getBounds();
				r.height += 100;
				shell.setBounds(r);
				shell.removeShellListener(this);
			}
		});
		
	}

	public void validate() throws ValidationException {
		String id = getEditorValue(EDITOR_ID_ID);
		if(id != null && !getWizard().isIDAvailable(id)) {
			throw new ValidationException(WizardMessages.errorIDisUsed);
		}
	}
}