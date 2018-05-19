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

import com.github.zbb93.sftp.connection.SSHException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.logging.Logger;

class JschChannelPool extends AbstractChannelPool {
	// JSch has a static configuration map that is shared amongst all instances of JSch objects.
	static {
		final Hashtable<String, String> config = new Hashtable<>(1);
		// TODO ZB this should be configurable
		config.put("StrictHostKeyChecking", "no");
		JSch.setConfig(config);
	}

	/**
	 * JSch Session Object. Best thought of as an SSH connection that we can use to obtain an SFTP channel. Before using
	 * ensure that the Session is still connected.
	 */
	private final @NotNull Session session;

	/**
	 * Provided to JSch Session to obtain an SFTP channel.
	 */
	private static final @NotNull String SFTP_CHANNEL = "sftp";

	private static final @NotNull Logger LOGGER = Logger.getLogger(JschChannelPool.class.getName());

	JschChannelPool(final @NotNull String host, final @NotNull String user, final byte[] password, final int port,
									final int timeout, final int poolSize) throws SSHException {
		super(poolSize);
		session = buildJschSession(host, user, password, port, timeout);
	}

	private Session buildJschSession(final @NotNull String host, final @NotNull String user, final byte[] password,
																	 final int port, final int timeout)
			throws SSHException {
		final JSch jsch = new JSch();
		try {
			final Session session = jsch.getSession(user, host, port);
			session.setTimeout(timeout);
			setPassword(session, password);
			return session;
		} catch (final JSchException e) {
			LOGGER.severe("Unable to establish connection: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	private void setPassword(final @NotNull Session session, final @NotNull byte[] password) {
		session.setPassword(password);
		clearByteArray(password);
	}

	/**
	 * Configures Authentication with the remote server and initiates an SSH connection with the remote server using a
	 * JSch Session Object.
	 *
	 * @throws SSHException if an error occurs while connecting to the remote server.
	 */
	@Override
	public void connect() throws SSHException {
		final String user = session.getUserName();
		final String host = session.getHost();
		LOGGER.info(String.format("Delegating channel creation for %s@%s to JSch...", user, host));
		try {
			session.connect();
			LOGGER.info("Session created succesfully.");
		} catch (final JSchException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public Channel getChannel() throws SSHException {
		LOGGER.info("Obtaining Channel from JSch...");
		try {
			final ChannelSftp channel = (ChannelSftp) session.openChannel(SFTP_CHANNEL);
			channel.connect();
			LOGGER.info("Successfully obtained Channel from JSch");
			return new JschSftpChannel(channel);
		} catch (final JSchException e) {
			LOGGER.severe("An error has occurred while attempting to obtain a channel: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	/**
	 * Disconnects the JSch channel from the SSH server
	 */
	@Override
	public void close() throws SSHException {
		super.close();
		LOGGER.info("Closing channel...");
		session.disconnect();
		LOGGER.info("Session closed.");
	}

	@Override
	@SuppressWarnings({"MagicCharacter", "PublicMethodWithoutLogging"})
	public String toString() {
		return "JschChannelPool{" +
					 "user=" + session.getUserName() + ',' +
					 "host=" + session.getHost() + ',' +
					 "poolSize=" + getPoolSize() +
					 '}';
	}
}
