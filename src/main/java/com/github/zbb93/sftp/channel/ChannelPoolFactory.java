/*
 * sftp - sftp for java
 * Copyright (C) 2018  Zac Bowen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.zbb93.sftp.channel;

import com.github.zbb93.sftp.connection.ConnectionParameters;
import com.github.zbb93.sftp.connection.SSHException;
import org.jetbrains.annotations.NotNull;

public class ChannelPoolFactory {
	public static final @NotNull ChannelPoolFactory INSTANCE = new ChannelPoolFactory();

	private ChannelPoolFactory() { }

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
