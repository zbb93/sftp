package com.github.zbb93.sftp.connection;

import com.github.zbb93.sftp.session.auth.*;
import com.google.common.base.*;
import org.jetbrains.annotations.*;

/**
 * Contains parameters that are used to obtain a connection to the remote server. This class should be instantiated
 * through the Builder class.
 */
// todo configure size of channel pool - add parameter for size of channel pool.
public final class ConnectionParameters {
	/**
	 * URL of the remote server.
	 */
	private final @NotNull String url;

	/**
	 * Configures authentication to the remote server for the RemoteSession.
	 */
	private final @NotNull Authentication authentication;

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
	 * @param url the URL of the remote server.
	 * @param authentication configures authentication to the remote server.
	 * @param port the port to connect to on the remote server.
	 * @param timeout amount of time (in seconds) to wait for a response from the remote server during any operation.
	 */
	private ConnectionParameters(final @NotNull String url, final @NotNull Authentication authentication, final int port,
															 final int timeout) {
		this.url = url;
		this.authentication = authentication;
		this.port = port;
		this.timeout = timeout;
	}

	/**
	 * @return the URL of the remote server.
	 */
	public @NotNull String getUrl() {
		return url;
	}

	/**
	 * @return the Authentication Object that will configure authentication to the remote server.
	 */
	public @NotNull Authentication getAuthentication() {
		return authentication;
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

	/**
	 * Instantiates a ConnectionParameters object. Handles the setting of default values for any parameters that are
	 * not explicitly set. Required parameters should be set by this objects constructor and optional values should be
	 * set using a setter method after the builder has been instantiated.
	 */
	public static class Builder {
		/**
		 * URL of the remote server.
		 */
		private final @NotNull String url;

		/**
		 * Configures authentication to the remote server for the RemoteSession.
		 */
		private final @NotNull Authentication authentication;

		/**
		 * Port to connect to on the remote server. This must be an integer value greater than zero.
		 */
		private final int port;

		/**
		 * Seconds to wait for a response from the remote server during any operation. This must be an integer value greater
		 * than zero.
		 */
		private int timeout;

		/**
		 * @param url the URL of the remote server.
		 * @param authentication configures authentication to the remote server.
		 * @param port the port to connect to on the remote server.
		 * @throws IllegalArgumentException if port is not a positive non-zero integer.
		 */
		public Builder(final @NotNull String url, final @NotNull Authentication authentication, final int port) {
			this.url = url;
			this.authentication = authentication;
			Preconditions.checkArgument(port > 0, "Cannot establish a connection to a port that is " +
					"not greater than zero.");
			this.port = port;
			timeout = Integer.MIN_VALUE;
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
		 * @return the timeout value of the ConnectionParameters Object being built.
		 */
		private int getTimeout() {
			return (timeout > -1) ? timeout : DEFAULT_TIMEOUT;
		}

		/**
		 * @return ConnectionParameters object created using the instance variables of this Builder object.
		 * @throws IllegalStateException if required parameters are not set.
		 */
		public @NotNull ConnectionParameters build() {
			final int timeout = getTimeout();
			return new ConnectionParameters(url, authentication, port, timeout);
		}
	}
}
