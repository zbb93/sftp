package com.github.zbb93.sftp.session.channel;

import com.github.zbb93.sftp.connection.*;
import com.google.common.collect.*;
import com.jcraft.jsch.*;
import org.jetbrains.annotations.*;

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
		} catch (JSchException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public @NotNull Collection<String> ls(@NotNull String path) throws SSHException {
		Collection<String> files = Lists.newLinkedList();
		try {
			Vector untypedFileNames = channel.ls(path);
			for (Object untypedFileName : untypedFileNames) {
				files.add(untypedFileName.toString());
			}
		} catch (SftpException e) {
			throw new SSHException(e);
		}
		return files;
	}

	@Override
	public void put(final @NotNull Path source, final @NotNull String dest) throws SSHException {
		try {
			channel.put(source.toString(), dest);
		} catch (SftpException e) {
			throw new SSHException(e);
		}
	}

	@Override
	public void close() {
		channel.disconnect();
	}
}
