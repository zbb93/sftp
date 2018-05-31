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

/**
 * POJO used to build a Connection.
 */
@SuppressWarnings("ClassWithoutLogger") // POJO does not need logger
class RemoteHost {

	/**
	 * URL of the remote server.
	 */
	private final @NotNull String url;

	/**
	 * Port to connect to on the remote server.
	 */
	private final int port;

	/**
	 * Seconds to wait for a response from the remote server during any operation. The default value is 60.
	 */
	private final int timeout;

	RemoteHost(final @NotNull String url, final int port, final int timeout) {
		this.url = url;
		this.port = port;
		this.timeout = timeout;
	}

	@NotNull String getUrl() {
		return url;
	}

	int getPort() {
		return port;
	}

	int getTimeout() {
		return timeout;
	}

	@SuppressWarnings("MagicCharacter")
	@Override
	public String toString() {
		return "RemoteHost{" +
					 "url='" + url + '\'' +
					 ", port=" + port +
					 ", timeout=" + timeout +
					 '}';
	}
}
