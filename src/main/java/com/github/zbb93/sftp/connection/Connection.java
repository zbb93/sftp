package com.github.zbb93.sftp.connection;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Implementations of this class are responsible for interacting with an SSH server and performing operations
 * like transferring files to as well as copying, deleting, and renaming files on the remote server.
 */
public interface Connection extends AutoCloseable {

	/**
	 * Obtains the directory listing of the a directory. Use '.' to list the working directory.
	 *
	 * @param path path to the directory to list.
	 * @return Collection of Strings where each string corresponds to a file in the directory.
	 * @throws SSHException if an error occurs while obtaining the directory listing.
	 * @throws InterruptedException if interrupted while waiting for an available channel.
	 */
	@NotNull
	// todo implement an Object to parse these Strings and provide access to file attributes.
	Collection<String> ls(final @NotNull String path) throws SSHException, InterruptedException;

	/**
	 * Uploads a file to the remote server.
	 *
	 * @param source path to file to upload.
	 * @param destination path on remote server to upload file to.
	 * @throws SSHException if an error occurs while uploading the file.
	 * @throws InterruptedException if interrupted while waiting for an available channel.
	 */
	void put(final @NotNull Path source, final @NotNull String destination) throws SSHException, InterruptedException;

	/**
	 * Downloads a file from the remote server and writes the output to the provided OutputStream. The OutputStream is
	 * flushed but not closed before the method returns.
	 *
	 * @param source path to file to download from remote server.
	 * @param outputStream OutputStream to write downloaded file to.
	 * @throws SSHException if an error occurs while downloading the file.
	 * @throws InterruptedException if interrupted while waiting for an available channel.
	 */
	void get(final @NotNull String source, final @NotNull OutputStream outputStream)
			throws SSHException, InterruptedException;

	/**
	 * Creates a new directory on the remote server. Note that this method is not able to create multiple
	 * directories at once.
	 *
	 * @param name name of the directory to create.
	 * @throws SSHException if an error occurs while communicating while creating the directory.
	 * @throws InterruptedException if interrupted while waiting for an available channel.
	 */
	// todo validate input to make sure that it does not contain file separators?
	void mkdir(final @NotNull String name) throws SSHException, InterruptedException;

	/**
	 * @return absolute path of the working directory on the remote server.
	 * @throws SSHException if an error occurs while obtaining the working directory.
	 * @throws InterruptedException if interrupted while waiting for an available channel.
	 */
	@NotNull String pwd() throws SSHException, InterruptedException;

	/**
	 *
	 * @param targetDirectory
	 * @throws SSHException
	 */
	void cd(final @NotNull String targetDirectory) throws SSHException, InterruptedException;

	@Override
	void close() throws SSHException;
}

