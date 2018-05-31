/*
 * sftp - sftp for java
 * Copyright (C) 2018  Zac Bowen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.zbb93.sftp;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Constructs implementations of the Connection Interface.
 */
@SuppressWarnings("ClassWithOnlyPrivateConstructors") // Mockito cannot mock final classes.
public class ConnectionFactory {
	/**
	 * Global singleton.
	 */
	public static final @NotNull ConnectionFactory INSTANCE = new ConnectionFactory();

	private static final @NotNull Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

	/**
	 * This object should not be instantiated directly. Use the singleton instead.
	 */
	private ConnectionFactory() { }

	/**
	 * Builds an SFTP connection using the provided ConnectionParameters.
	 * @param connectionParameters parameters used to configure the returned Connection.
	 * @return Connection object configured using the provided ConnectionParameters.
	 * @throws SSHException if the host of the ConnectionParameters cannot be resolved.
	 */
	public Connection getConnection(final @NotNull ConnectionParameters connectionParameters) throws SSHException {
		final RemoteHost remoteHost = connectionParameters.getRemoteHost();
		final String url = remoteHost.getUrl();
		final String user = connectionParameters.getUser();
		LOGGER.info(String.format("Obtaining connection for %s@%s", user, url));
		return new ConnectionImpl(connectionParameters, ChannelPoolFactory.INSTANCE);
	}
}
