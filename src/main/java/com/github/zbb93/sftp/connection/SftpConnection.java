package com.github.zbb93.sftp.connection;

import com.github.zbb93.sftp.session.*;
import org.jetbrains.annotations.*;

import java.io.*;

/**
 * SftpConnection uses the SFTP protocol to implement the operations defined by the Connection Interface.
 */
class SftpConnection implements Connection {

	/**
	 * Used to open SFTP channels on the SSH server. This is the root connection to the SSH server.
	 */
	private final @NotNull RemoteSession session;

	/**
	 * @param connectionParameters contains parameters required to connect to SSH server.
	 * @throws SSHException if the host obtained from the ConnectionProviders cannot be resolved
	 */
	SftpConnection(final @NotNull ConnectionParameters connectionParameters) throws SSHException {
		this.session = createSession(connectionParameters);
	}

	/**
	 * Passes the provided ConnectionParameters to the RemoteSessionFactory to obtain the RemoteSession Object that will
	 * be used to connect to the SSH server.
	 *
	 * @param parameters to pass to the RemoteSessionFactory
	 * @return RemoteSession Object that is ready to connect to the SSH server.
	 * @throws SSHException if the host obtained from the ConnectionProviders cannot be resolved.
	 */
	@NotNull
	private RemoteSession createSession(final @NotNull ConnectionParameters parameters) throws SSHException {
		return RemoteSessionFactory.INSTANCE.getSession(parameters);
	}

	@Override
	public void connect() throws SSHException {
		session.connect();
	}

	@Override
	public boolean isConnected() {
		return session.isConnected();
	}

	/**
	 * Disconnects the RemoteSession from the SSH server.
	 * @throws IOException if an error occurs disconnecting from the SSH server.
	 */
	@Override
	public void close() throws IOException {
		session.close();
	}
}
