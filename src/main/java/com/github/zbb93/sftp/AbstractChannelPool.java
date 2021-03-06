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

import com.diffplug.common.base.Errors;
import com.google.common.collect.Queues;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * This implementation of ChannelPool contains no logic related to actually obtaining the channels from the remote
 * server. It is responsible solely for maintaining the channel pool.
 */
public abstract class AbstractChannelPool implements ChannelPool {
	private final int poolSize;
	private final @NotNull BlockingQueue<Channel> channelPool;
	private @NotNull String workingDirectory;

	private static final @NotNull Logger LOGGER = Logger.getLogger(AbstractChannelPool.class.getName());
	private static final byte NULL_BYTE = (byte) '\0';
	@SuppressWarnings("HardcodedFileSeparator")
	private static final char UNIX_FILE_SEPARATOR = '/';

	protected AbstractChannelPool(final int poolSize) {
		this.poolSize = poolSize;
		workingDirectory = "";
		channelPool = Queues.newLinkedBlockingQueue(poolSize);
	}

	@Override
	public void initialize() throws SSHException {
		LOGGER.info("Initializing connection pool...");
		connect();
		initializeChannels();
		LOGGER.info("Connection pool initialized successfully.");
	}

	protected abstract void connect() throws SSHException;

	private void initializeChannels() throws SSHException {
		Channel channel = getChannel();
		workingDirectory = channel.pwd();
		do {
			channelPool.add(channel);
			channel = getChannel();
		} while (channelPool.remainingCapacity() > 0);
	}

	protected abstract Channel getChannel() throws SSHException;

	@Override
	public @NotNull Channel getNextAvailableChannel() throws SSHException, InterruptedException {
		LOGGER.info("Waiting on next available channel...");
		// TODO add timeout option for time to wait for channel
		final Channel channel = channelPool.take();
		// TODO track working directory on the Channel and update when it is returned if it does not match channel pool.
		channel.cd(workingDirectory);
		LOGGER.info("Successfully obtained channel.");
		return channel;
	}

	@Override
	public void returnChannel(final @NotNull Channel channel) {
		channelPool.add(channel);
	}

	@Override
	public void setWorkingDirectory(final @NotNull String targetDirectory) {
		if (isAbsolute(targetDirectory)) {
			workingDirectory = targetDirectory;
		} else {
			if (!workingDirectory.endsWith(String.valueOf(UNIX_FILE_SEPARATOR))) {
				workingDirectory += UNIX_FILE_SEPARATOR;
			}
			workingDirectory += targetDirectory;
		}
	}

	@Override
	public @NotNull String getWorkingDirectory() {
		return workingDirectory;
	}

	private boolean isAbsolute(final @NotNull CharSequence targetDirectory) {
		return targetDirectory.charAt(0) == UNIX_FILE_SEPARATOR;
	}
	/**
	 * Disconnects all channels in the channel pool.
	 *
	 * @throws SSHException if an error occurs disconnecting from the SSH server.
	 */
	@Override
	public void close() throws SSHException {
		LOGGER.info("Disconnecting from remote server...");
		channelPool.forEach(
				Errors.rethrow()
							.wrap(AutoCloseable::close)
		);
		LOGGER.info("Successfully disconnected from remote server.");
	}

	protected void clearByteArray(final @NotNull byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = NULL_BYTE;
		}
	}

	protected int getPoolSize() {
		return poolSize;
	}
}
