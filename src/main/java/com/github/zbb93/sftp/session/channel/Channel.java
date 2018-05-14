package com.github.zbb93.sftp.session.channel;

import com.github.zbb93.sftp.connection.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * An SFTP connection to a remote server. A single RemoteSession can have multiple SftpChannels.
 */
public interface Channel extends Closeable{
	void connect() throws SSHException;
	@NotNull
	Collection<String> ls(final @NotNull String path) throws SSHException;
	void put(final @NotNull Path source, final @NotNull String dest) throws SSHException;
}
