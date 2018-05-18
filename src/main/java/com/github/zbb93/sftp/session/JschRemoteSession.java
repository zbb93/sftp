package com.github.zbb93.sftp.session;

import com.github.zbb93.sftp.connection.SSHException;
import com.github.zbb93.sftp.session.auth.Authentication;
import com.github.zbb93.sftp.session.channel.Channel;
import com.github.zbb93.sftp.session.channel.JschSftpChannel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.logging.Logger;

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

	private static final @NotNull Logger LOGGER = Logger.getLogger(JschRemoteSession.class.getName());

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
		final String user = auth.getUser();
		final String host = getHost();
		LOGGER.info(String.format("Delegating session creation for %s@%s to JSch...", user, host));
		auth.authenticate(this);
		final int timeout = getTimeout();
		try {
			session.connect(1000 * timeout);
			LOGGER.info("Session created succesfully.");
		} catch (final JSchException e) {
			throw new SSHException(e);
		}
	}

	// Currently only called from one site and additonal logging would be redundant.
	@SuppressWarnings("PublicMethodWithoutLogging")
	@Override
	public boolean isConnected() {
		return session.isConnected();
	}

	@Override
	public Channel getChannel() throws SSHException {
		LOGGER.info("Obtaining Channel from JSch...");
		try {
			final ChannelSftp channel = (ChannelSftp) session.openChannel(SFTP_CHANNEL);
			LOGGER.info("Successfully obtained Channel from JSch");
			return new JschSftpChannel(channel);
		} catch (final JSchException e) {
			LOGGER.severe("An error has occurred while attempting to obtain a channel: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	@Override
	// TODO ZB passwords should always live in byte arrays
	public void setPassword(final String password) {
		session.setPassword(password);
		LOGGER.info("Password for remote session has been set.");
	}

	/**
	 * Disconnects the JSch session from the SSH server
	 */
	@Override
	public void close() {
		LOGGER.info("Closing session...");
		session.disconnect();
		LOGGER.info("Session closed.");
	}
}
