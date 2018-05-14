package com.github.zbb93.sftp.connection;

import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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

	@NotNull
	Collection<String> ls(final @NotNull String path) throws SSHException;

	void put(final @NotNull Path source, final @NotNull String destination) throws SSHException;

	/**
	 * @return true if this Connection is currently connected to the remote server and false otherwise.
	 */
	boolean isConnected();
}

