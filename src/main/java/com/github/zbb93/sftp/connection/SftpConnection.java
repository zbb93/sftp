package com.github.zbb93.sftp.connection;

import com.diffplug.common.base.Errors;
import com.github.zbb93.sftp.session.RemoteSession;
import com.github.zbb93.sftp.session.RemoteSessionFactory;
import com.github.zbb93.sftp.session.channel.Channel;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * SftpConnection uses the SFTP protocol to implement the operations defined by the Connection Interface.
 */
class SftpConnection implements Connection {

	/**
	 * Used to open SFTP channels on the SSH server. This is the root connection to the SSH server.
	 */
	private final @NotNull RemoteSession session;

	private final @NotNull BlockingQueue<Channel> channels;

	private static final @NotNull Logger LOGGER = Logger.getLogger(SftpConnection.class.getName());

	/**
	 * @param connectionParameters contains parameters required to connect to SSH server.
	 * @throws SSHException if the host obtained from the ConnectionProviders cannot be resolved
	 */
	SftpConnection(final @NotNull ConnectionParameters connectionParameters) throws SSHException {
		session = createSession(connectionParameters);
		// todo configure size of channel pool - set size of queue.
		channels = Queues.newLinkedBlockingQueue();
	}

	/**
	 * Passes the provided ConnectionParameters to the RemoteSessionFactory to obtain the RemoteSession Object that will
	 * be used to connect to the SSH server.
	 *
	 * @param parameters to pass to the RemoteSessionFactory
	 * @return RemoteSession Object that is ready to connect to the SSH server.
	 * @throws SSHException if the host obtained from the ConnectionProviders cannot be resolved.
	 */
	private @NotNull RemoteSession createSession(final @NotNull ConnectionParameters parameters) throws SSHException {
		return RemoteSessionFactory.INSTANCE.getSession(parameters);
	}

	@Override
	public void connect() throws SSHException {
		LOGGER.info("Initializing connection...");
		session.connect();
		final Collection<Channel> availableChannels = openChannels(session);
		channels.addAll(availableChannels);
		LOGGER.info("Connection initialized successfully.");
	}

	private @NotNull Collection<Channel> openChannels(final @NotNull RemoteSession session) throws SSHException {
		// todo configure size of channel pool - use configured value to determine size of channel pool.
		final int channelPoolSize = 10;
		LOGGER.info("Opening channels. Pool size = " + channelPoolSize);
		final Collection<Channel> channels = Lists.newLinkedList();
		for (int i = 0; i < channelPoolSize; i++) {
			final Channel channel = session.getChannel();
			channel.connect();
			channels.add(channel);
		}
		LOGGER.info("Channels successfully opened");
		return channels;
	}

	@Override
	public boolean isConnected() {
		LOGGER.info("Checking connection status...");
		final boolean connected = session.isConnected();
		if (connected) {
			LOGGER.info("Connection is currently active.");
		} else {
			LOGGER.info("Connection is not currently active.");
		}
		return connected;
	}

	@Override
	public @NotNull Collection<String> ls(final @NotNull String path) throws SSHException, InterruptedException {
		LOGGER.info("Obtaining directory listing for directory: " + path);
		final Channel channel = getNextAvailableChannel();
		final Collection<String> listing = channel.ls(path);
		channels.add(channel);
		LOGGER.info("Successfully obtained directory listing.");
		return listing;
	}

	@Override
	public void put(final @NotNull Path source, final @NotNull String destination) throws SSHException,
			InterruptedException {
		LOGGER.info(String.format("Uploading file. \nSource: %s\nDestination: %s", source.toString(),
															destination));
		final Channel channel = getNextAvailableChannel();
		channel.put(source, destination);
		channels.add(channel);
		LOGGER.info("File uploaded successfully.");
	}

	@Override
	public void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException,
			InterruptedException {
		LOGGER.info("Initializing download of file " + source);
		final Channel channel = getNextAvailableChannel();
		channel.get(source, outputStream);
		channels.add(channel);
		LOGGER.info("Download initialized successfully.");
	}

	@Override
	public void mkdir(final @NotNull String name) throws SSHException, InterruptedException {
		LOGGER.info("Creating directory " + name);
		final Channel channel = getNextAvailableChannel();
		channel.mkdir(name);
		channels.add(channel);
		LOGGER.info("Directory created successfully.");
	}

	@Override
	public @NotNull String pwd() throws SSHException, InterruptedException {
		LOGGER.info("Obtaining working directory.");
		final Channel channel = getNextAvailableChannel();
		final String workingDirectory = channel.pwd();
		channels.add(channel);
		LOGGER.info("Working directory is " + workingDirectory);
		return workingDirectory;
	}

	/**
	 * Obtains the next available channel from the channel queue. If no channels are available the method blocks until
	 * a channel is available.
	 *
	 * @return channel from the channel queue.
	 * @throws InterruptedException if interrupted while waiting for an available channel.
	 */
	private @NotNull Channel getNextAvailableChannel() throws InterruptedException {
		Preconditions.checkState(!channels.isEmpty(),
														 "Connection#connect should be invoked before invoking other methods.");
		return channels.take();
	}

	/**
	 * Disconnects the RemoteSession from the SSH server.
	 * @throws IOException if an error occurs disconnecting from the SSH server.
	 */
	@Override
	public void close() throws SSHException {
		LOGGER.info("Disconnecting from remote server...");
		session.close();
		closeChannels();
		LOGGER.info("Successfully disconnected from remote server.");
	}

	private void closeChannels() {
		channels.forEach(Errors.rethrow().wrap(Closeable::close));
	}
}
