package com.github.zbb93.sftp.session;

import com.github.zbb93.sftp.connection.*;
import org.jetbrains.annotations.*;

/**
 * Responsible for selecting the correct RemoteSession implementation to instantiate based on the provided
 * ConnectionParameters Object. Currently the JschRemoteSession is the only implementation of RemoteSession.
 */
public class RemoteSessionFactory {
	/**
	 * Singleton instance that should be used instead of local instances.
	 */
	public static RemoteSessionFactory INSTANCE = new RemoteSessionFactory();

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
		String host = connectionParameters.getUrl();
		int port = connectionParameters.getPort();
		int timeout = connectionParameters.getTimeout();
		return new JschRemoteSession(host, port, timeout, connectionParameters.getAuthentication());
	}
}
