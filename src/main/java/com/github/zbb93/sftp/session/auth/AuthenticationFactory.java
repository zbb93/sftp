package com.github.zbb93.sftp.session.auth;

import org.jetbrains.annotations.NotNull;

/**
 * Creates an AbstractAuthentication object using the provided parameters. Currently only username / password is the
 * only supported authentication option. This class cannot be instantiated directly and should be accessed through
 * the static INSTANCE object.
 */
@SuppressWarnings("ClassWithoutLogger")
public final class AuthenticationFactory {
	/**
	 * Static instance of the AuthenticationFactory.
	 */
	public static final @NotNull AuthenticationFactory INSTANCE = new AuthenticationFactory();

	/**
	 * This object should not be instantiated directly. Use the global singleton instead.
	 */
	private AuthenticationFactory() { }

	/**
	 * Provides an Authentication Object that will configure a RemoteSession to connect using the provided username and
	 * password.
	 *
	 * @param username the username to connect to the remote server with.
	 * @param password the password to use when connecting to the remote server.
	 * @return Authentication object that will configure a RemoteSession to connect with the provided username and
	 * password.
	 */
	public @NotNull Authentication authenticationFor(final @NotNull String username, final @NotNull String password) {
		return new PasswordAuthentication(username, password);
	}
}
