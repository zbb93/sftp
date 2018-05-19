package com.github.zbb93.sftp.connection;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Contains parameters that are used to obtain a connection to the remote server. This class should be instantiated
 * through the Builder class.
 */
public final class ConnectionParameters {
	public String getUser() {
		return user;
	}

	public byte[] getPassword() {
		return password;
	}

	public enum AuthenticationMode {
		PASSWORD("Password");

		private final @NotNull String mode;

		AuthenticationMode(final @NotNull String mode) {
			this.mode = mode;
		}

		@Override
		@SuppressWarnings("PublicMethodWithoutLogging")
		public String toString() {
			return mode;
		}
	}

	public enum Provider {
		JSCH("JSch");

		private final @NotNull String provider;

		Provider(final @NotNull String provider) {
			this.provider = provider;
		}

		@Override
		public String toString() {
			return provider;
		}
	}

	private final @NotNull Provider provider;

	private static final @NotNull Provider DEFAULT_PROVIDER = Provider.JSCH;

	/**
	 * URL of the remote server.
	 */
	private final @NotNull String host;

	private final @NotNull String user;

	private final @NotNull byte[] password;

	private final @NotNull AuthenticationMode authenticationMode;
	/**
	 * Port to connect to on the remote server.
	 */
	private final int port;

	/**
	 * Seconds to wait for a response from the remote server during any operation. The default value is 60.
	 */
	private final int timeout;

	/**
	 * Default timeout value in seconds.
	 */
	private static final int DEFAULT_TIMEOUT = 60;

	/**
	 * Number of channels for the connection to maintain on the remote server. These can be used for concurrent
	 * operations.
	 */
	private final int channelPoolSize;

	/**
	 * Default channel pool size.
	 */
	private static final int DEFAULT_CHANNEL_POOL_SIZE = 10;

	/**
	 * @param provider
	 * @param host the URL of the remote server.
	 * @param user
	 * @param password
	 * @param port the port to connect to on the remote server.
	 * @param timeout amount of time (in seconds) to wait for a response from the remote server during any operation.
	 */
	private ConnectionParameters(final @NotNull Provider provider, final @NotNull String host, final @NotNull String user,
															 final @NotNull byte[] password, final int port, final int timeout,
															 final int channelPoolSize) {
		this.provider = provider;
		this.host = host;
		this.user = user;
		this.password = password;
		authenticationMode = AuthenticationMode.PASSWORD;
		this.port = port;
		this.timeout = timeout;
		this.channelPoolSize = channelPoolSize;
	}

	/**
	 * @return the URL of the remote server.
	 */
	public @NotNull String getHost() {
		return host;
	}

	/**
	 * @return the port to connect to on the remote server.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the amount of time (in seconds) to wait for a response from the remote server during any operation.
	 */
	public int getTimeout() {
		return timeout;
	}

	public int getChannelPoolSize() {
		return channelPoolSize;
	}

	public AuthenticationMode getAuthenticationMode() {
		return authenticationMode;
	}

	public Provider getProvider() {
		return provider;
	}

	/**
	 * Instantiates a ConnectionParameters object. Handles the setting of default values for any parameters that are
	 * not explicitly set. Required parameters should be set by this objects constructor and optional values should be
	 * set using a setter method after the builder has been instantiated.
	 */
	public static class Builder {
		private @NotNull Provider provider;

		/**
		 * URL of the remote server.
		 */
		private final @NotNull String host;

		private final @NotNull AuthenticationMode authenticationMode;

		private @NotNull String user;

		private @NotNull byte[] password;

		/**
		 * Port to connect to on the remote server. This must be an integer value greater than zero.
		 */
		private final int port;

		/**
		 * Seconds to wait for a response from the remote server during any operation. This must be a positive integer
		 * value.
		 */
		private int timeout;

		/**
		 * Number of channels for the connection to maintain on the remote server. These can be used for concurrent
		 * operations. Must be an integer value greater than zero. The default value is 10.
		 */
		private int channelPoolSize;

		/**
		 * @param host the URL of the remote server.
		 * @param user
		 * @parm password
		 * @param port the port to connect to on the remote server.
		 * @throws IllegalArgumentException if port is not a positive non-zero integer.
		 */
		public Builder(final @NotNull String host, final @NotNull String user, final @NotNull byte[] password,
									 final int port) {
			provider = DEFAULT_PROVIDER;
			this.host = host;
			authenticationMode = AuthenticationMode.PASSWORD;
			this.user = user;
			this.password = password;
			Preconditions.checkArgument(port > 0, "Cannot establish a connection to a port that is " +
					"not greater than zero.");
			this.port = port;
			timeout = DEFAULT_TIMEOUT;
			channelPoolSize = DEFAULT_CHANNEL_POOL_SIZE;
		}

		/**
		 * Sets the timeout for the ConnectionParameters Object being built.
		 *
		 * @param timeout amount of time (in seconds) to wait for a response from the remote server during any operation.
		 * @throws IllegalArgumentException if timeout is not a non-negative integer value.
		 */
		public void setTimeout(final int timeout) {
			Preconditions.checkArgument(timeout >= 0, "Timeout must be a positive integer value.");
			this.timeout = timeout;
		}

		/**
		 * Sets the channel pool size of the ConnectionParameters Object being built.
		 *
		 * @param channelPoolSize desired size of channel pool.
		 * @throws IllegalArgumentException if channel pool size is not greater than zero.
		 */
		public void setChannelPoolSize(final int channelPoolSize) {
			Preconditions.checkArgument(channelPoolSize > 0,
																	"Channel pool size must be an integer value greater than zero.");
			this.channelPoolSize = channelPoolSize;
		}

		public void setProvider(final @NotNull Provider provider) {
			this.provider = provider;
		}

		/**
		 * @return ConnectionParameters object created using the instance variables of this Builder object.
		 * @throws IllegalStateException if required parameters are not set.
		 */
		public @NotNull ConnectionParameters build() {
			ConnectionParameters parameters;
			if (authenticationMode == AuthenticationMode.PASSWORD) {
				parameters = new ConnectionParameters(provider, host, user, password, port, timeout, channelPoolSize);
			} else {
				throw new IllegalStateException("Unrecognized authentication mode: " + authenticationMode);
			}
			return parameters;
		}
	}
}
