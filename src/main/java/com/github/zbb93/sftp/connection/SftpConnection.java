package com.github.zbb93.sftp.connection;

import com.github.zbb93.sftp.channel.Channel;
import com.github.zbb93.sftp.channel.ChannelPool;
import com.github.zbb93.sftp.channel.ChannelPoolFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * SftpConnection uses the SFTP protocol to implement the operations defined by the Connection Interface.
 */
class SftpConnection implements Connection {

	/**
	 * Obtains Channels from the remote server.
	 */
	private final @NotNull ChannelPool channelPool;

	private static final @NotNull Logger LOGGER = Logger.getLogger(SftpConnection.class.getName());

	/**
	 * @param connectionParameters contains parameters required to connect to SSH server.
	 * @throws SSHException if the host obtained from the ConnectionProviders cannot be resolved
	 */
	SftpConnection(final @NotNull ConnectionParameters connectionParameters) throws SSHException {
		channelPool = ChannelPoolFactory.INSTANCE.getChannelPool(connectionParameters);
	}

	@Override
	public @NotNull Collection<String> ls(final @NotNull String path) throws SSHException, InterruptedException {
		LOGGER.info("Obtaining directory listing for directory: " + path);
		final Channel channel = channelPool.getNextAvailableChannel();
		final Collection<String> listing = channel.ls(path);
		channelPool.returnChannel(channel);
		LOGGER.info("Successfully obtained directory listing.");
		return listing;
	}

	@Override
	public void put(final @NotNull Path source, final @NotNull String destination) throws SSHException,
			InterruptedException {
		LOGGER.info(String.format("Uploading file. \nSource: %s\nDestination: %s", source.toString(),
															destination));
		final Channel channel = channelPool.getNextAvailableChannel();
		channel.put(source, destination);
		channelPool.returnChannel(channel);
		LOGGER.info("File uploaded successfully.");
	}

	@Override
	public void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException,
			InterruptedException {
		LOGGER.info("Initializing download of file " + source);
		final Channel channel = channelPool.getNextAvailableChannel();
		channel.get(source, outputStream);
		channelPool.returnChannel(channel);
		LOGGER.info("Download initialized successfully.");
	}

	@Override
	public void mkdir(final @NotNull String name) throws SSHException, InterruptedException {
		LOGGER.info("Creating directory " + name);
		final Channel channel = channelPool.getNextAvailableChannel();
		channel.mkdir(name);
		channelPool.returnChannel(channel);
		LOGGER.info("Directory created successfully.");
	}

	@Override
	public @NotNull String pwd() throws SSHException, InterruptedException {
		LOGGER.info("Obtaining working directory.");
		final Channel channel = channelPool.getNextAvailableChannel();
		final String workingDirectory = channel.pwd();
		channelPool.returnChannel(channel);
		LOGGER.info("Working directory is " + workingDirectory);
		return workingDirectory;
	}

	@Override
	public void cd(final @NotNull String targetDirectory) {
		LOGGER.info("Setting working directory for future operations to " + targetDirectory);
		channelPool.setWorkingDirectory(targetDirectory);
		LOGGER.info("Working directory successfully changed to " + channelPool.getWorkingDirectory());
	}

	/**
	 * Disconnects the RemoteSession from the SSH server.
	 * @throws IOException if an error occurs disconnecting from the SSH server.
	 */
	@Override
	public void close() throws SSHException {
		LOGGER.info("Disconnecting from remote server...");
		channelPool.close();
		LOGGER.info("Successfully disconnected from remote server.");
	}
}
