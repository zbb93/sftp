package com.github.zbb93.sftp.connection;

import com.github.zbb93.sftp.session.auth.Authentication;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Constructs implementations of the Connection Interface.
 */
public final class ConnectionFactory {
	/**
	 * Global singleton.
	 */
	public static final @NotNull ConnectionFactory INSTANCE = new ConnectionFactory();

	private static final @NotNull Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

	/**
	 * This object should not be instantiated directly. Use the singleton instead.
	 */
	private ConnectionFactory() { }

	/**
	 * Builds an SFTP connection using the provided ConnectionParameters.
	 * @param connectionParameters parameters used to configure the returned Connection.
	 * @return Connection object configured using the provided ConnectionParameters.
	 * @throws SSHException if the host of the ConnectionParameters cannot be resolved.
	 */
	public Connection getConnection(final @NotNull ConnectionParameters connectionParameters) throws SSHException {
		final String host = connectionParameters.getUrl();
		final Authentication authentication = connectionParameters.getAuthentication();
		final String user = authentication.getUser();
		LOGGER.info(String.format("Obtaining connection for %s@%s", user, host));
		return new SftpConnection(connectionParameters);
	}
}
