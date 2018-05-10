package com.github.zbb93.sftp.session.auth;

import org.jetbrains.annotations.*;

/**
 * Contains logic common to all implementations of the Authentication Interface.
 */
abstract class AbstractAuthentication implements Authentication {

	/**
	 * Username that will be used to connect to the remote server.
	 */
	@NotNull String username;

	/**
	 * @param username the username to connect to the remote server with.
	 */
	AbstractAuthentication(final @NotNull String username) {
		this.username = username;
	}

	@NotNull
	@Override
	public String getUser() {
		return username;
	}
}
