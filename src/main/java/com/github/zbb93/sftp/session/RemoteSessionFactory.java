package com.github.zbb93.sftp.session;

import com.github.zbb93.sftp.connection.ConnectionParameters;
import com.github.zbb93.sftp.connection.SSHException;
import com.github.zbb93.sftp.session.auth.Authentication;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Responsible for selecting the correct RemoteSession implementation to instantiate based on the provided
 * ConnectionParameters Object. Currently the JschRemoteSession is the only implementation of RemoteSession.
 */
@SuppressWarnings("FeatureEnvy") // No need to warn of excessive use of ConnectionParameters.
public final class RemoteSessionFactory {
	/**
	 * Singleton instance that should be used instead of local instances.
	 */
	public static final RemoteSessionFactory INSTANCE = new RemoteSessionFactory();

	private static final Logger LOGGER = Logger.getLogger(RemoteSessionFactory.class.getName());

	/**
	 * RemoteSessionFactory should not be instantiated directly.
	 */
	private RemoteSessionFactory() { }

	/**
	 * Uses the provided ConnectionParameters Object to instantiate a RemoteSession.
	 *
	 * @param connectionParameters ConnectionParameters containing parameters necessary for the RemoteSession to establish
	 *                             a connection with an SSH server.
	 * @return RemoteSession Object that can be used to connect to an SSH server.
	 * @throws SSHException if the host defined by the ConnectionParameters cannot be resolved.
	 */
	public RemoteSession getSession(final @NotNull ConnectionParameters connectionParameters) throws SSHException {
		final String host = connectionParameters.getUrl();
		final int port = connectionParameters.getPort();
		final int timeout = connectionParameters.getTimeout();
		final Authentication authentication = connectionParameters.getAuthentication();
		final String user = authentication.getUser();
		LOGGER.info(String.format("Created JschRemoteSession object for %s@%s", user, host));
		return new JschRemoteSession(host, port, timeout, authentication);
	}
}
