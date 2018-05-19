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
package com.github.zbb93.sftp.connection;

import com.github.zbb93.sftp.channel.Channel;
import com.github.zbb93.sftp.channel.ChannelPool;
import com.github.zbb93.sftp.channel.ChannelPoolFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * ConnectionImpl uses the SFTP protocol to implement the operations defined by the Connection Interface.
 */
class ConnectionImpl implements Connection {

	/**
	 * Obtains Channels from the remote server.
	 */
	private final @NotNull ChannelPool channelPool;

	private static final @NotNull Logger LOGGER = Logger.getLogger(ConnectionImpl.class.getName());

	/**
	 * @param connectionParameters contains parameters required to connect to SSH server.
	 * @param channelPoolFactory instance of the ChannelPoolFactory. Builds the ChannelPool.
	 * @throws SSHException if the host obtained from the ConnectionProviders cannot be resolved
	 */
	ConnectionImpl(final @NotNull ConnectionParameters connectionParameters,
								 final ChannelPoolFactory channelPoolFactory) throws SSHException {
		channelPool = channelPoolFactory.getChannelPool(connectionParameters);
	}

	@Override
	public @NotNull Collection<String> ls(final @NotNull String path) throws SSHException, InterruptedException {
		LOGGER.info("Obtaining directory listing for directory: " + path);
		final Channel channel = channelPool.getNextAvailableChannel();
		final Collection<String> listing = channel.ls(path);
		returnChannel(channel);
		LOGGER.info("Successfully obtained directory listing.");
		return listing;
	}

	@Override
	public void put(final @NotNull Path source, final @NotNull String destination) throws SSHException,
			InterruptedException {
		LOGGER.info(String.format("Uploading file. \nSource: %s\nDestination: %s", source.toString(),
															destination));
		final Channel channel = channelPool.getNextAvailableChannel();
		channel.put(source, destination);
		returnChannel(channel);
		LOGGER.info("File uploaded successfully.");
	}

	@Override
	public void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException,
			InterruptedException {
		LOGGER.info("Initializing download of file " + source);
		final Channel channel = channelPool.getNextAvailableChannel();
		channel.get(source, outputStream);
		returnChannel(channel);
		LOGGER.info("Download initialized successfully.");
	}

	@Override
	public void mkdir(final @NotNull String name) throws SSHException, InterruptedException {
		LOGGER.info("Creating directory " + name);
		final Channel channel = channelPool.getNextAvailableChannel();
		channel.mkdir(name);
		returnChannel(channel);
		LOGGER.info("Directory created successfully.");
	}

	@Override
	public @NotNull String pwd() {
		LOGGER.info("Obtaining working directory.");
		final String workingDirectory = channelPool.getWorkingDirectory();
		LOGGER.info("Working directory is " + workingDirectory);
		return workingDirectory;
	}

	@Override
	public void cd(final @NotNull String targetDirectory) {
		LOGGER.info("Setting working directory for future operations to " + targetDirectory);
		channelPool.setWorkingDirectory(targetDirectory);
		LOGGER.info("Working directory successfully changed to " + channelPool.getWorkingDirectory());
	}

	private void returnChannel(final @NotNull Channel channel) {
		channelPool.returnChannel(channel);
	}
	/**
	 * Disconnects the RemoteSession from the SSH server.
	 * @throws IOException if an error occurs disconnecting from the SSH server.
	 */
	@Override
	public void close() throws SSHException {
		LOGGER.info("Disconnecting from remote server...");
		channelPool.close();
		LOGGER.info("Successfully disconnected from remote server.");
	}
}
