/*******************************************************************************
 * Copyright (c) 2012, 2014 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - Jay Jay Billings,
 *   Jordan H. Deyton, Dasha Gorin, Alexander J. McCaskey, Taylor Patterson,
 *   Claire Saunders, Matthew Wang, Anna Wojtowicz
 *******************************************************************************/
package org.eclipse.ice.client.widgets.geometry;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * <p>
 * Action with a dropdown button and a simple capability for adding child
 * actions
 * </p>
 * 
 * @author Andrew P. Belt
 */
public class DropdownAction extends Action implements IMenuCreator {

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return super.getImageDescriptor();
	}

	/**
	 * The root menu containing all the actions
	 */
	private Menu menu;

	/**
	 * The actions to display when this dropdown action is expanded
	 */
	private List<Action> childActions = new ArrayList<Action>();

	/**
	 * The image descriptor associated with the duplicate action's icon
	 */
	private ImageDescriptor imageDescriptor;

	/**
	 * <p>
	 * Creates a dropdown action with a text label
	 * </p>
	 * 
	 * @param text
	 *            <p>
	 *            The label text to appear on the action button
	 *            </p>
	 */
	public DropdownAction(String text) {

		super(text, IAction.AS_DROP_DOWN_MENU);
		setMenuCreator(this);

	}

	/**
	 * <p>
	 * Creates a dropdown action with an icon label
	 * </p>
	 * 
	 * @param text
	 *            <p>
	 *            The tooltip text
	 *            </p>
	 * @param imageFilename
	 *            <p>
	 *            The filename of the icon image, relative to the OSGi bundle
	 *            location
	 *            </p>
	 */
	public DropdownAction(String text, String imageFilename) {

		this(text);

		// Load the icon image

		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL imagePath = BundleUtility.find(bundle, imageFilename);
		imageDescriptor = ImageDescriptor.createFromURL(imagePath);

	}

	/**
	 * <p>
	 * Cleans up and disposes internal Menu instance
	 * </p>
	 * 
	 */
	@Override
	public void dispose() {

		if (menu != null) {
			menu.dispose();
			menu = null;
		}

	}

	/**
	 * <p>
	 * Returns null in this implementation
	 * </p>
	 * 
	 * @param parent
	 *            <p>
	 *            The parent Menu
	 *            </p>
	 * @return <p>
	 *         The generated menu
	 *         </p>
	 */
	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}

	/**
	 * <p>
	 * Returns the Menu generated by this DropdownAction
	 * </p>
	 * 
	 * @param parent
	 *            <p>
	 *            The parent Control instance
	 *            </p>
	 * @return <p>
	 *         The menu generated by this DropdownAction
	 *         </p>
	 */
	@Override
	public Menu getMenu(Control parent) {

		// Dispose of the old menu

		if (menu != null) {
			menu.dispose();
		}
		// Put all the actions into a new menu

		menu = new Menu(parent);

		for (Action action : childActions) {
			ActionContributionItem item = new ActionContributionItem(action);
			item.fill(menu, -1);
		}

		return menu;

	}

	/**
	 * <p>
	 * Appends an Action to the bottom of the current menu
	 * </p>
	 * 
	 * @param action
	 *            <p>
	 *            The Action to append
	 *            </p>
	 */
	public void addAction(Action action) {

		childActions.add(action);

	}
}