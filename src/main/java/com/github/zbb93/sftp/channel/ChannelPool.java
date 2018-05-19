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

import com.github.zbb93.sftp.connection.SSHException;
import org.jetbrains.annotations.NotNull;

public interface ChannelPool extends AutoCloseable {
	/**
	 * Establishes a connection to the remote server and populates the channel pool.
	 *
	 * @throws SSHException if an error occurs connecting to the remote server.
	 */
	void initialize() throws SSHException;

	/**
	 * Obtains the next available channel from the channel queue. If no channels are available the method blocks until
	 * a channel is available.
	 *
	 * @return channel from the channel queue.
	 * @throws InterruptedException if interrupted while waiting for an available channel.
	 * @throws SSHException if an error occurs communicating with the remote server.
	 */
	@NotNull Channel getNextAvailableChannel() throws SSHException, InterruptedException;

	void returnChannel(final @NotNull Channel channel);

	void setWorkingDirectory(final @NotNull String targetDirectory);

	String getWorkingDirectory();

	void close() throws SSHException;
}
