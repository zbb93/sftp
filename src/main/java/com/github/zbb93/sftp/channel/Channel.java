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

package com.github.zbb93.sftp.channel;

import com.github.zbb93.sftp.connection.SSHException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;

/**
 * An SFTP connection to a remote server. A single RemoteSession can have multiple SftpChannels.
 */
public interface Channel extends AutoCloseable{
	void connect() throws SSHException;
	@NotNull Collection<String> ls(final @NotNull String path) throws SSHException;
	void put(final @NotNull Path source, final @NotNull String dest) throws SSHException;
	void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException;
	void mkdir(final @NotNull String path) throws SSHException;
	String pwd() throws SSHException;
	void cd(final @NotNull String targetDirectory) throws SSHException;
	@Override
	void close() throws SSHException;
}
