package com.github.zbb93.sftp.connection;

import org.jetbrains.annotations.*;

/**
 * Constructs implementations of the Connection Interface.
 */
public final class ConnectionFactory {
	/**
	 * Global singleton.
	 */
	public static final @NotNull ConnectionFactory INSTANCE = new ConnectionFactory();

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
		return new SftpConnection(connectionParameters);
	}
}
