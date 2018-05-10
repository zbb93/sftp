package com.github.zbb93.sftp.connection;

import java.io.*;

/**
 * Implementations of this class are responsible for interacting with an SSH server and performing operations
 * like transferring files to as well as copying, deleting, and renaming files on the remote server.
 */
public interface Connection extends Closeable {
	/**
	 * Establish a connection to the remote server.
	 * @throws SSHException if an error occurs while connecting to the remote server.
	 */
	void connect() throws SSHException;

	/**
	 * @return true if this Connection is currently connected to the remote server and false otherwise.
	 */
	boolean isConnected();
}
