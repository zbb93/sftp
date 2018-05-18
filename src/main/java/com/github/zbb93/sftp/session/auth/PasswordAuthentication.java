package com.github.zbb93.sftp.session.auth;

import com.github.zbb93.sftp.session.*;
import org.jetbrains.annotations.*;

/**
 * Adds a password to a RemoteSession Object that will be used to Authenticate with a remote server.
 */
class PasswordAuthentication extends AbstractAuthentication {
	private final @NotNull String password;

	/**
	 * @param username the username to connect to the remote server with.
	 * @param password the password to use when connecting to the remote server.
	 */
	PasswordAuthentication(final @NotNull String username, final @NotNull String password) {
		super(username);
		this.password = password;
	}

	/**
	 * Sets the password of the provided RemoteSession.
	 *
	 * @param session the RemoteSession to configure authentication for.
	 */
	@Override
	public void authenticate(final @NotNull RemoteSession session) {
		session.setPassword(password);
	}
}
