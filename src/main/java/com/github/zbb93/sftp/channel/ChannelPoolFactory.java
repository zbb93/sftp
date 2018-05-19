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

package com.github.zbb93.sftp.channel;

import com.github.zbb93.sftp.connection.ConnectionParameters;
import com.github.zbb93.sftp.connection.SSHException;
import org.jetbrains.annotations.NotNull;

/**
 * Takes a ConnectionParameters object and based on the parameters determines an implementation of ChannelPool to
 * return.
 */
@SuppressWarnings("FeatureEnvy")
public class ChannelPoolFactory {
	/**
	 * Global singleton that should be used to create ChannelPool objects.
	 */
	public static final @NotNull ChannelPoolFactory INSTANCE = new ChannelPoolFactory();

	private ChannelPoolFactory() { }

	/**
	 * Creates a ChannelPool from the provided ConnectionParameters.
	 *
	 * @param params ConnectionParameters to build the ChannelPool with.
	 * @return ready to use ChannelPool.
	 * @throws SSHException if an error occurs while establishing the connection or opening channels.
	 */
	public ChannelPool getChannelPool(final @NotNull ConnectionParameters params) throws SSHException {
		final ConnectionParameters.Provider provider = params.getProvider();
		final ChannelPool pool;
		if (provider == ConnectionParameters.Provider.JSCH) {
			pool = getJschChannelPool(params);
		} else {
			throw new IllegalStateException("Unrecognized provider: " + provider);
		}
		pool.initialize();
		return pool;
	}

	/**
	 * Creates a ChannelPool that opens channels using JSch.
	 *
	 * @param params ConnectionParameters to build the ChannelPool with.
	 * @return ready to use ChannelPool.
	 * @throws SSHException if an error occurs while establishing the connection or opening channels.
	 */
	private ChannelPool getJschChannelPool(final @NotNull ConnectionParameters params) throws SSHException {
		final String host = params.getHost();
		final String user = params.getUser();
		final byte[] password = params.getPassword();
		final int port = params.getPort();
		final int timeout = params.getTimeout();
		final int poolSize = params.getChannelPoolSize();
		return new JschChannelPool(host, user, password, port, timeout, poolSize);
	}
}
