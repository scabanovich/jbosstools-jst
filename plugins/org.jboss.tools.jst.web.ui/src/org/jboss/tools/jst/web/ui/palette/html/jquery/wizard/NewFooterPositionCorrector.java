/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jst.web.ui.palette.html.jquery.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.jboss.tools.jst.web.ui.palette.html.wizard.WizardMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class NewFooterPositionCorrector extends AbstractWidgetPositionCorrector {

	@Override
	protected ITextSelection doCorrectSelection(ITextSelection selection) {
		ElementImpl page = isPage(c) ? (ElementImpl)c : (ElementImpl)getPageParent(c);
		if(page != null && getFooterSection(page) != null) {
			page = null;
		}
		if(page == null) {
			page = getClosestElement(getPagesWithoutFooter(body), offset);
		}
		if(page != null) {
			ElementImpl content = getContentSection(page);
			if(content != null) {
				return new TextSelection(fDocument, content.getEndOffset(), 0);
			}
			ElementImpl header = getHeaderSection(page);
			if(header != null) {
				return new TextSelection(fDocument, header.getEndOffset(), 0);
			}
			return new TextSelection(fDocument, page.getEndStartOffset(), 0);
		}
		return selection;
	}

	public String getWarningMessage(Document document, ITextSelection selection) {
		init(document, selection);
		if(c == null || body == null || getPagesWithoutFooter(body).isEmpty()) {
			return WizardMessages.noPlaceForFooterWarning;
		}
		return null;
	}

	protected static List<ElementImpl> getPagesWithoutFooter(Element body) {
		List<ElementImpl> result = new ArrayList<ElementImpl>();
		for (Element e: getChildrenWithDatarole(body)) {
			if(isPage(e) && getFooterSection(e) == null) {
				result.add((ElementImpl)e);
			}
		}
		return result;
	}

}
