package com.github.zbb93.sftp.connection;

import com.diffplug.common.base.*;
import com.github.zbb93.sftp.session.*;
import com.github.zbb93.sftp.session.channel.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * SftpConnection uses the SFTP protocol to implement the operations defined by the Connection Interface.
 */
class SftpConnection implements Connection {

	/**
	 * Used to open SFTP channels on the SSH server. This is the root connection to the SSH server.
	 */
	private final @NotNull RemoteSession session;

	private final @NotNull BlockingQueue<Channel> channels;

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
		session.connect();
		final Collection<Channel> availableChannels = openChannels(session);
		channels.addAll(availableChannels);
	}

	private @NotNull Collection<Channel> openChannels(final @NotNull RemoteSession session) throws SSHException {
		// todo configure size of channel pool - use configured value to determine size of channel pool.
		final int channelPoolSize = 10;
		final Collection<Channel> channels = Lists.newLinkedList();
		for (int i = 0; i < channelPoolSize; i++) {
			final Channel channel = session.getChannel();
			channel.connect();
			channels.add(channel);
		}
		return channels;
	}

	@Override
	public boolean isConnected() {
		return session.isConnected();
	}

	@Override
	public @NotNull Collection<String> ls(final @NotNull String path) throws SSHException, InterruptedException {
		final Channel channel = getNextAvailableChannel();
		return channel.ls(path);
	}

	@Override
	public void put(final @NotNull Path source, final @NotNull String destination) throws SSHException,
			InterruptedException {
		final Channel channel = getNextAvailableChannel();
		channel.put(source, destination);
	}

	@Override
	public void get(final @NotNull String source, final @NotNull OutputStream outputStream) throws SSHException,
			InterruptedException {
		final Channel channel = getNextAvailableChannel();
		channel.get(source, outputStream);
	}

	@Override
	public void mkdir(final @NotNull String name) throws SSHException, InterruptedException {
		final Channel channel = getNextAvailableChannel();
		channel.mkdir(name);
	}

	@Override
	public @NotNull String pwd() throws SSHException, InterruptedException {
		final Channel channel = getNextAvailableChannel();
		return channel.pwd();
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
	public void close() throws IOException {
		session.close();
		closeChannels();
	}

	private void closeChannels() {
		channels.forEach(Errors.rethrow().wrap(Closeable::close));
	}
}
