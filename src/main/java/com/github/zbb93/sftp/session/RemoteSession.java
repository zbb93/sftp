package com.github.zbb93.sftp.session;

import com.github.zbb93.sftp.connection.SSHException;
import com.github.zbb93.sftp.session.channel.Channel;

/**
 * An SSH connection to a remote server. The session spawns channels that are used to perform
 * SFTP operations. This interface is intended to be the internal version of the Connection Interface.
 */
public interface RemoteSession extends AutoCloseable {
	/**
	 * Establishes an SSH connection with the remote server.
	 *
	 * @throws SSHException if an error occurs while attempting to connect to the server.
	 */
	void connect() throws SSHException;

	/**
	 * @return true if this session is connected to the remote server and false otherwise.
	 */
	boolean isConnected();

	/**
	 * @return an SFTP channel from the SSH server that this session is connected to.
	 * @throws SSHException if an error occurs while attempting to obtain an SFTP channel from the remote server.
	 */
	Channel getChannel() throws SSHException;

	/**
	 * @return timeout (in seconds) for operations with the remote server.
	 */
	int getTimeout();

	/**
	 * @return port that this session will connect to on the remote server.
	 */
	int getPort();

	/**
	 * @param password the password for this session to use when connected to the remote server.
	 */
	void setPassword(String password);

	void close() throws SSHException;
}
