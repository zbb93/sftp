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
	// todo implement an Object to parse these Strings and provide access to file attributes.
	Collection<String> ls(final @NotNull String path) throws SSHException;

	/**
	 * Uploads a file to the remote server.
	 *
	 * @param source path to file to upload.
	 * @param destination path on remote server to upload file to.
	 * @throws SSHException if an error occurs while uploading the file.
	 */
	void put(final @NotNull Path source, final @NotNull String destination) throws SSHException;

	/**
	 * Downloads a file from the remote server and writes the output to the provided OutputStream. The OutputStream is
	 * flushed but not closed before the method returns.
	 *
	 * @param source path to file to download from remote server.
	 * @param outputStream OutputStream to write downloaded file to.
	 * @throws SSHException if an error occurs while downloading the file.
	 */
	void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException;

	void mkdir(final @NotNull String path) throws SSHException;
	/**
	 * @return true if this Connection is currently connected to the remote server and false otherwise.
	 */
	boolean isConnected();
}

