 /*******************************************************************************
  * Copyright (c) 2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jst.web.kb;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey Kazakov
 */
public class KbMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.jst.web.kb.KbMessages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, KbMessages.class);
	}

	public static String WRONG_BUILDER_ORDER;
	public static String CHANGE_BUILDER_ORDER;

	public static String KBNATURE_NOT_FOUND;
	public static String KBBUILDER_NOT_FOUND;
	public static String KBNATURE_SEPARATOR;
	public static String KBNATURE_LAST_SEPARATOR;
	public static String KBPROBLEM;
	public static String KBPROBLEM_LOCATION;
	public static String ENABLE_KB;
	public static String KBPROBLEM_DEPENDS_ON_JAVA_SINGLE;
	public static String KBPROBLEM_DEPENDS_ON_JAVA_MANY;
	public static String ENABLE_KB_ON_SINGLE;
	public static String ENABLE_KB_ON_MANY;
}