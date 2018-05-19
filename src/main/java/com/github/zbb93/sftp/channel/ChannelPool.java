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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.zbb93.sftp.channel;

import com.github.zbb93.sftp.connection.SSHException;
import org.jetbrains.annotations.NotNull;

/**
 * Maintains a thread safe pool of channels that can be used to interact with a remote server.
 */
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

	/**
	 * Returns the provided channel to the channel pool.
	 *
	 * @param channel channel to return to the pool.
	 */
	void returnChannel(final @NotNull Channel channel);

	/**
	 * Updates the current working directory on the remote server. This will not affect any channels until the next time
	 * they are removed from the pool.
	 *
	 * @param targetDirectory new working directory on remote server.
	 */
	void setWorkingDirectory(final @NotNull String targetDirectory);

	/**
	 * @return current working directory on the remote server.
	 */
	String getWorkingDirectory();

	/**
	 * Disconnects all channels in the channel pool.
	 *
	 * @throws SSHException if an error occurs disconnecting from the SSH server.
	 */
	void close() throws SSHException;
}
