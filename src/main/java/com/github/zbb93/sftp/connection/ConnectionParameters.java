/*
 * sftp - sftp for java
 * Copyright (C) 2018  Zac Bowen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.zbb93.sftp.connection;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Contains parameters that are used to obtain a connection to the remote server. This class should be instantiated
 * through the Builder class.
 */
@SuppressWarnings("ClassWithOnlyPrivateConstructors") // Mockito can't mock final classes.
public class ConnectionParameters {

	/**
	 * Defines available methods of authentication.
	 */
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

	/**
	 * Defines the available methods that a Connection can use to interact with the remote server.
	 */
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

	/**
	 * Method the Connection created from this object will use to interact with the remote server.
	 */
	private final @NotNull Provider provider;

	/**
	 * Default Connection provider.
	 */
	private static final @NotNull Provider DEFAULT_PROVIDER = Provider.JSCH;

	/**
	 * URL of the remote server.
	 */
	private final @NotNull String host;

	/**
	 * Username of the account to use on the remote server.
	 */
	private final @NotNull String user;

	/**
	 * Byte array containing the password to authenticate with.
	 */
	private final @NotNull byte[] password;

	/**
	 * Method of authentication.
	 */
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
	 * @param provider method the ChannelPool will use to obtain channels.
	 * @param host the URL of the remote server.
	 * @param user username for the remote user.
	 * @param password password for the remote user.
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

	/**
	 * @return number of channels that will be maintained by the Connection created from these ConnectionParameters.
	 */
	public int getChannelPoolSize() {
		return channelPoolSize;
	}

	/**
	 * @return method of authentication for this connection.
	 */
	public AuthenticationMode getAuthenticationMode() {
		return authenticationMode;
	}

	/**
	 * @return username of the remote user.
	 */
	public @NotNull String getUser() {
		return user;
	}

	/**
	 * @return password of the remote user.
	 */
	public @NotNull byte[] getPassword() {
		return password;
	}

	/**
	 * @return provider that the connection will use to interact with the remote server.
	 */
	public @NotNull Provider getProvider() {
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

		/**
		 * Method of authentication.
		 */
		private final @NotNull AuthenticationMode authenticationMode;

		/**
		 * Username of the account to use on the remote server.
		 */
		private @NotNull String user;

		/**
		 * Byte array containing the password to authenticate with.
		 */
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

		/**
		 * @param provider method that will be used to interact with the remote server.
		 */
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
