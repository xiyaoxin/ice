/*******************************************************************************
 * Copyright (c) 2015- UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Initial API and implementation and/or initial documentation - 
 *   Jordan Deyton
 *******************************************************************************/
package org.eclipse.ice.viz.service.paraview;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.eclipse.ice.viz.service.IPlot;
import org.eclipse.ice.viz.service.AbstractVizService;
import org.eclipse.ice.viz.service.connections.ConnectionManager;
import org.eclipse.ice.viz.service.connections.ConnectionTable;
import org.eclipse.ice.viz.service.connections.IConnectionAdapter;
import org.eclipse.ice.viz.service.connections.paraview.ParaViewConnectionAdapter;
import org.eclipse.ice.viz.service.paraview.proxy.IParaViewProxy;
import org.eclipse.ice.viz.service.paraview.proxy.IParaViewProxyFactory;
import org.eclipse.ice.viz.service.paraview.proxy.IParaViewProxyFactoryRegistry;
import org.eclipse.ice.viz.service.preferences.CustomScopedPreferenceStore;

import com.kitware.vtk.web.VtkWebClient;

/**
 * This class is responsible for providing a service to connect to (or launch)
 * either local or remote instances of ParaView. Additionally, it can be used to
 * create {@link ParaViewPlot}s that can render files supported by ParaView
 * within an SWT-based application.
 * 
 * @see ParaViewPlot
 * @see #createPlot(URI)
 * 
 * @author Jordan Deyton
 *
 */
public class ParaViewVizService extends AbstractVizService {

	/**
	 * The current instance of the viz service. This instance was created when
	 * the OSGi viz service was instantiated.
	 */
	private static ParaViewVizService instance;

	/**
	 * The manager for all of the ParaView connections.
	 */
	private final ConnectionManager<VtkWebClient> connections;

	/**
	 * The registry of factories used to get {@link IParaViewProxy}s for
	 * manipulating and rendering files with ParaView.
	 */
	private IParaViewProxyFactoryRegistry proxyFactoryRegistry;

	/**
	 * The default constructor.
	 * <p>
	 * <b>Note:</b> Only OSGi should call this method!
	 * </p>
	 */
	public ParaViewVizService() {
		// Update the instance to point to this viz service (there should be
		// only one).
		instance = this;

		// Set up the connection manager.
		connections = new ConnectionManager<VtkWebClient>() {
			@Override
			protected CustomScopedPreferenceStore getPreferenceStore() {
				return (CustomScopedPreferenceStore) ParaViewVizService.this
						.getPreferenceStore();
			}

			@Override
			protected ConnectionTable createConnectionTable() {
				return new ConnectionTable();
			}

			@Override
			protected IConnectionAdapter<VtkWebClient> createConnectionAdapter() {
				return new ParaViewConnectionAdapter();
			}
		};

		// // Add supported ExodusII file format extensions.
		// supportedExtensions.add("ex");
		// supportedExtensions.add("e");
		// supportedExtensions.add("exo");
		// supportedExtensions.add("ex2");
		// supportedExtensions.add("exii");
		// supportedExtensions.add("gen");
		// supportedExtensions.add("exodus");
		// supportedExtensions.add("nemesis");
		// // Add supported Silo file format extensions.
		// supportedExtensions.add("silo");

		return;
	}

	/**
	 * Gets the current instance of the viz service. This instance was created
	 * by OSGi.
	 * <p>
	 * <b>Note:</b> This method is only intended to be used by the preference
	 * page to notify the service when the preferences have changed.
	 * </p>
	 * 
	 * @return The current instance of the viz service.
	 */
	protected static ParaViewVizService getInstance() {
		return instance;
	}

	/**
	 * This method notifies the service that the preferences have changed. Any
	 * connections that have changed should be reset.
	 */
	protected void preferencesChanged(Map<String, String> changedKeys,
			Set<String> addedKeys, Set<String> removedKeys) {
		connections.preferencesChanged(changedKeys, addedKeys, removedKeys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ice.client.widgets.viz.service.IVizService#getName()
	 */
	@Override
	public String getName() {
		return "ParaView";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ice.client.widgets.viz.service.IVizService#getVersion()
	 */
	@Override
	public String getVersion() {
		return "0.0";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ice.client.widgets.viz.service.IVizService#
	 * hasConnectionProperties()
	 */
	@Override
	public boolean hasConnectionProperties() {
		// Do nothing yet.
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ice.client.widgets.viz.service.IVizService#
	 * getConnectionProperties()
	 */
	@Override
	public Map<String, String> getConnectionProperties() {
		// Do nothing yet.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ice.client.widgets.viz.service.IVizService#
	 * setConnectionProperties(java.util.Map)
	 */
	@Override
	public void setConnectionProperties(Map<String, String> props) {
		// Do nothing yet.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ice.client.widgets.viz.service.IVizService#connect()
	 */
	@Override
	public boolean connect() {
		return connections.connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ice.client.widgets.viz.service.IVizService#disconnect()
	 */
	@Override
	public boolean disconnect() {
		return connections.disconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ice.client.widgets.viz.service.IVizService#createPlot(java
	 * .net.URI)
	 */
	@Override
	public IPlot createPlot(URI uri) throws Exception {
		// Check the extension.
		super.createPlot(uri);

		ParaViewPlot plot = null;

		// Create the plot.
		plot = new ParaViewPlot(this);
		// Associate the plot with the connection.
		connections.addClient(plot);
		// Set the data source for the file.
		plot.setDataSource(uri);

		return plot;
	}

	/**
	 * Sets the {@link IParaViewProxyFactory} registry. This registry should be
	 * queried for a factory, which will then be used to create an
	 * {@link IParaViewProxy} when performing operations on a supported file
	 * type.
	 * <p>
	 * <b>Note:</b> This method should only be called by OSGi!
	 * </p>
	 * 
	 * @param registry
	 *            The new registry.
	 */
	public void setProxyFactoryRegistry(IParaViewProxyFactoryRegistry registry) {
		if (registry != null && registry != proxyFactoryRegistry) {
			proxyFactoryRegistry = registry;
			// Update the supported file types.
			supportedExtensions.clear();
			supportedExtensions.addAll(registry.getExtensions());
		}
		return;
	}

	/**
	 * Unsets the {@link IParaViewProxyFactory} registry if the argument
	 * matches.
	 * <p>
	 * <b>Note:</b> This method should only be called by OSGi!
	 * </p>
	 * 
	 * @param registry
	 *            The old registry.
	 */
	public void unsetProxyFactoryRegistry(IParaViewProxyFactoryRegistry registry) {
		if (registry == proxyFactoryRegistry) {
			proxyFactoryRegistry = null;
			// The file types are no longer supported.
			supportedExtensions.clear();
		}
		return;
	}
}