package com.github.zbb93.sftp.session;

import com.github.zbb93.sftp.connection.*;
import com.github.zbb93.sftp.session.auth.*;
import com.github.zbb93.sftp.session.channel.Channel;
import com.github.zbb93.sftp.session.channel.*;
import com.jcraft.jsch.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * This implementation of RemoteSession utilizes the JSch library to connect to an SSH server.
 */
class JschRemoteSession extends AbstractRemoteSession {
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

	/**
	 * @param host URL of the SSH server to connect to.
	 * @param port port that the SSH server is listening on.
	 * @param timeout amount of time (in seconds) that a connection attempt is allowed to take.
	 * @param authentication Authentication implementation to use to authenticate with the SSH server.
	 * @throws SSHException if the host cannot be resolved.
	 */
	JschRemoteSession(final @NotNull String host, final int port, final int timeout,
										final @NotNull Authentication authentication) throws SSHException {
		super(host, port, timeout, authentication);
		final JSch jsch = new JSch();
		final String username = authentication.getUser();
		try {
			session = jsch.getSession(username, host, port);
		} catch (final JSchException e) {
			throw new SSHException(e);
		}
	}

	/**
	 * Configures Authentication with the remote server and initiates an SSH connection with the remote server using a
	 * JSch Session Object.
	 *
	 * @throws SSHException if an error occurs while connecting to the remote server.
	 */
	@Override
	public void connect() throws SSHException {
		final Authentication auth = getAuthentication();
		auth.authenticate(this);
		final int timeout = getTimeout();
		try {
			session.connect(1000 * timeout);
		} catch (final JSchException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public boolean isConnected() {
		return session.isConnected();
	}

	@Override
	public Channel getChannel() throws SSHException {
		try {
			final ChannelSftp channel = (ChannelSftp) session.openChannel(SFTP_CHANNEL);
			return new JschSftpChannel(channel);
		} catch (final JSchException e) {
			throw new SSHException(e);
		}
	}

	@Override
	// TODO ZB passwords should always live in byte arrays
	public void setPassword(final String password) {
		session.setPassword(password);
	}

	/**
	 * Disconnects the JSch session from the SSH server
	 */
	@Override
	public void close() {
		session.disconnect();
	}
}
