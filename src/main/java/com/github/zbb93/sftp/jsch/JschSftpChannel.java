/*
 * sftp - sftp for java
 * Copyright (C) 2018  Zac Bowen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.zbb93.sftp.jsch;

import com.github.zbb93.sftp.Channel;
import com.github.zbb93.sftp.RemoteFile;
import com.github.zbb93.sftp.SSHException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;

public class JschSftpChannel implements Channel {
	private final @NotNull com.jcraft.jsch.ChannelSftp channel;

	private static final @NotNull Logger LOGGER = Logger.getLogger(JschSftpChannel.class.getName());

	public JschSftpChannel(final @NotNull com.jcraft.jsch.ChannelSftp channel) {
		this.channel = channel;
	}

	@Override
	public void connect() throws SSHException {
		LOGGER.info("Opening JSch SFTP channel...");
		try {
			channel.connect();
			LOGGER.info("Channel opened successfully.");
		} catch (final JSchException e) {
			LOGGER.severe("An error occurred while attempting to open channel: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	@Override
	public @NotNull Collection<RemoteFile> ls(final @NotNull String path) throws SSHException {
		LOGGER.info("Using JSch ChannelSftp to obtain listing of directory: " + path);
		Preconditions.checkArgument(!path.isEmpty(), "Empty string provided as path");
		final Collection<String> directoryListing = Lists.newLinkedList();
		try {
			@SuppressWarnings("rawtypes") final Iterable untypedFileNames = channel.ls(path);
			for (final Object untypedFileName : untypedFileNames) {
				final String fileName = untypedFileName.toString();
				directoryListing.add(fileName);
			}
			return RemoteFile.getRemoteFilesForDirectory(directoryListing);
		} catch (final SftpException e) {
			LOGGER.severe("Encountered an error obtaining the directory listing: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	@Override
	public void put(final @NotNull Path source, final @NotNull String dest) throws SSHException {
		LOGGER.info(String.format("Using JSch ChannelSftp to upload file %s to %s", source.toString(), dest));
		final String sourceString = source.toString();
		try {
			channel.put(sourceString, dest);
			LOGGER.info("File transferred successfully.");
		} catch (final SftpException e) {
			LOGGER.severe("An error occurred while uploading the file: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	@Override
	public void rm(final @NotNull String path) throws SSHException {
		try {
			channel.rm(path);
		} catch (final SftpException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException {
		LOGGER.info(String.format("Using JSch ChannelSftp to download file %s", source));
		try {
			channel.get(source, outputStream);
			LOGGER.info("File download successfully initialized");
		} catch (final SftpException e) {
			LOGGER.severe("An error occurred while downloading the file: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	@Override
	public void mkdir(final @NotNull String path) throws SSHException {
		LOGGER.info("Using JSch ChannelSftp to create directory " + path);
		try {
			channel.mkdir(path);
			LOGGER.info("Directory created successfully");
		} catch (final SftpException e) {
			LOGGER.severe("An error occurred while creating the directory: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	@Override
	public String pwd() throws SSHException {
		LOGGER.info("Using JSch ChannelSftp to obtain working directory.");
		try {
			final String workingDirectory = channel.pwd();
			LOGGER.info("Successfully obtained working directory.");
			return workingDirectory;
		} catch (final SftpException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public void cd(final @NotNull String targetDirectory) throws SSHException {
		LOGGER.info("Changing directory to " + targetDirectory);
		try {
			channel.cd(targetDirectory);
		} catch (final SftpException e) {
			LOGGER.severe("Error occurred while attempting to change directories: " + e.getMessage());
			throw new SSHException(e);
		}
	}

	@Override
	public void close() {
		LOGGER.info("Closing JSch ChannelSftp...");
		channel.disconnect();
		LOGGER.info("Closed JSch ChannelSftp.");
	}

	@SuppressWarnings("MagicCharacter")
	@Override
	public String toString() {
		// todo manually pull information from JSch ChannelSftp
		return "JschSftpChannel{" +
					 "channel=" + channel +
					 '}';
	}
}
