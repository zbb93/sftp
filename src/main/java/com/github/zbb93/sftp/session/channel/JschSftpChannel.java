package com.github.zbb93.sftp.session.channel;

import com.github.zbb93.sftp.connection.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.jcraft.jsch.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JschSftpChannel implements Channel {
	private final @NotNull com.jcraft.jsch.ChannelSftp channel;

	public JschSftpChannel(final @NotNull com.jcraft.jsch.ChannelSftp channel) {
		this.channel = channel;
	}

	@Override
	public void connect() throws SSHException {
		try {
			channel.connect();
		} catch (final JSchException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public @NotNull Collection<String> ls(final @NotNull String path) throws SSHException {
		Preconditions.checkArgument(!path.isEmpty(), "Empty string provided as path");
		final Collection<String> files = Lists.newLinkedList();
		try {
			@SuppressWarnings("rawtypes") final Iterable untypedFileNames = channel.ls(path);
			for (final Object untypedFileName : untypedFileNames) {
				final String fileName = untypedFileName.toString();
				files.add(fileName);
			}
		} catch (final SftpException e) {
			throw new SSHException(e);
		}
		return files;
	}

	@Override
	public void put(final @NotNull Path source, final @NotNull String dest) throws SSHException {
		final String sourceString = source.toString();
		try {
			channel.put(sourceString, dest);
		} catch (final SftpException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException {
		try {
			channel.get(source, outputStream);
		} catch (final SftpException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public void mkdir(final @NotNull String path) throws SSHException {
		try {
			channel.mkdir(path);
		} catch (final SftpException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public String pwd() throws SSHException {
		try {
			return channel.pwd();
		} catch (final SftpException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public void close() {
		channel.disconnect();
	}
}
