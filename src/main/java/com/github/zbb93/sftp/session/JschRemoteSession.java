package com.github.zbb93.sftp.session;

import com.github.zbb93.sftp.channel.Channel;
import com.github.zbb93.sftp.connection.*;
import com.github.zbb93.sftp.session.auth.*;
import com.jcraft.jsch.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * This implementation of RemoteSession utilizes the Jsch library to connect to an SSH server.
 */
class JschRemoteSession extends AbstractRemoteSession {
	// JSch has a static configuration map that is shared amongst all instances of JSch objects.
	static {
		Hashtable<String, String> config = new Hashtable<>();
		// TODO ZB this should be configurable
		config.put("StrictHostKeyChecking", "no");
		JSch.setConfig(config);
	}

	/**
	 * JSch Session Object. Best thought of as an SSH connection that we can use to obtain an SFTP channel. Before using
	 * ensure that the Session is still connected.
	 */
	protected final @NotNull Session session;

	/**
	 * @param host URL of the SSH server to connect to.
	 * @param port port that the SSH server is listening on.
	 * @param timeout amount of time (in seconds) that a connection attempt is allowed to take.
	 * @param authentication Authentication implementation to use to authenticate with the SSH server.
	 * @throws SSHException if the host cannot be resolved.
	 */
	JschRemoteSession(final @NotNull String host, final int port, final int timeout, final @NotNull Authentication authentication) throws SSHException {
		super(host, port, timeout, authentication);
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(authentication.getUser(), host, port);
		} catch (JSchException e) {
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
		Authentication auth = getAuthentication();
		auth.authenticate(this);
		int timeout = getTimeout();
		try {
			session.connect(1000 * timeout);
		} catch (JSchException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public boolean isConnected() {
		return session.isConnected();
	}

	@Override
	public Channel getChannel() {
		throw new UnsupportedOperationException();
	}

	@Override
	// TODO ZB passwords should always live in byte arrays
	public void setPassword(String password) {
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
