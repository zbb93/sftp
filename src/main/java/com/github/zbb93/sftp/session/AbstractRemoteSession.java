package com.github.zbb93.sftp.session;

import com.github.zbb93.sftp.session.auth.*;
import org.jetbrains.annotations.*;

/**
 * Contains logic common to all RemoteSession Object.
 */
abstract class AbstractRemoteSession implements RemoteSession {
	/**
	 * URL of the remote server.
	 */
	private final @NotNull String host;

	/**
	 * Port to connect to on the remote server.
	 */
	private final int port;

	/**
	 * Seconds to wait for a response from the remote server during any operation. The default value is 60.
	 */
	private final int timeout;

	/**
	 * Configures authentication to the remote server for the RemoteSession.
	 */
	private final @NotNull Authentication authentication;

	/**
	 * @param host URL of the SSH server to connect to.
	 * @param port port that the SSH server is listening on.
	 * @param timeout amount of time (in seconds) that a connection attempt is allowed to take.
	 * @param authentication Authentication implementation to use to authenticate with the SSH server.
	 */
	AbstractRemoteSession(final @NotNull String host, final int port, final int timeout, final @NotNull Authentication authentication) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.authentication = authentication;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @return Authentication that will be used to authenticate with the remote server.
	 */
	@NotNull
	Authentication getAuthentication() {
		return authentication;
	}
}
