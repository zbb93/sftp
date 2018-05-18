package com.github.zbb93.sftp.session.channel;

import com.github.zbb93.sftp.connection.SSHException;
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
	public @NotNull Collection<String> ls(final @NotNull String path) throws SSHException {
		LOGGER.info("Using JSch ChannelSftp to obtain listing of directory: " + path);
		Preconditions.checkArgument(!path.isEmpty(), "Empty string provided as path");
		final Collection<String> files = Lists.newLinkedList();
		try {
			@SuppressWarnings("rawtypes") final Iterable untypedFileNames = channel.ls(path);
			for (final Object untypedFileName : untypedFileNames) {
				final String fileName = untypedFileName.toString();
				files.add(fileName);
			}
		} catch (final SftpException e) {
			LOGGER.severe("Encountered an error obtaining the directory listing: " + e.getMessage());
			throw new SSHException(e);
		}
		return files;
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
	public void close() {
		LOGGER.info("Closing JSch ChannelSftp...");
		channel.disconnect();
		LOGGER.info("Closed JSch ChannelSftp.");
	}
}
