/*
 * sftp - sftp for java
 * Copyright (C) 2018  Zac Bowen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.zbb93.sftp;

import com.github.zbb93.sftp.connection.Connection;
import com.github.zbb93.sftp.connection.ConnectionFactory;
import com.github.zbb93.sftp.connection.ConnectionParameters;
import com.github.zbb93.sftp.connection.SSHException;
import com.google.common.collect.Lists;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

/**
 * This is a suite of tests that need to be run against an SSH server. The methods executed before and after the tests
 * set up and tear down the SSH server used for testing.
 */
public final class SshServerIT {

	/**
	 * SSH server used for testing.
	 */
	private static final SshServer server = SshServer.setUpDefaultServer();

	/**
	 * Host of the SSH server used for testing.
	 */
	private static final @NotNull String HOST = "localhost";

	/**
	 * Port of the SSH server used for testing.
	 */
	private static final int PORT = 4000;

	/**
	 * Username used to connect to the SSH server.
	 */
	private static final @NotNull String USERNAME = "test";

	/**
	 * Password used in conjunction with the above username to connect to the SSH server.
	 */
	private static final @NotNull byte[] PASSWORD = "test".getBytes();

	private static final int DEFAULT_TIMEOUT = 20000;

	/**
	 * Configures the SFTP server to be used for testing.
	 *
	 * @throws IOException if an error occurs configuring the server.
	 */
	@BeforeClass
	public static void setUp() throws IOException {
//		configureLogger();
		server.setHost(HOST);
		server.setPort(PORT);
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		server.setPasswordAuthenticator((username, password, session) ->
																				username.equals(USERNAME) && password.equals(new String(PASSWORD)));
		server.setSubsystemFactories(Lists.newArrayList(new SftpSubsystemFactory()));
		server.start();
	}

	private static void configureLogger() {
		final Logger globalLogger = Logger.getLogger("");
		globalLogger.setLevel(Level.FINEST);
		final Collection<Handler> handlers = Lists.newArrayList(globalLogger.getHandlers());
		handlers.forEach(handler -> handler.setLevel(Level.FINEST));
	}

	/**
	 * This test uses an unroutable IP address to ensure that connection timeout is working as expected.
	 *
	 * @throws SSHException if an unexpected error occurs while connecting to the SSH server.
	 */
	@Test
	public void testConnectionTimeout() {
		final ConnectionParameters connectionParameters = buildPasswordConnectionParameters("10.255.255.1", 1);
		try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(connectionParameters)) {
			Assert.fail("Connection did not timeout.");
		} catch (final SSHException e) {
			Assert.assertThat("Unexpected SSHException occurred.", e.getMessage(), containsString("timeout"));
		}
	}

	private ConnectionParameters buildPasswordConnectionParameters(final String host, final int timeout) {
		final ConnectionParameters.Builder builder = new ConnectionParameters.Builder(host, SshServerIT.USERNAME,
																																									SshServerIT.PASSWORD,
																																									SshServerIT.PORT);
		builder.setTimeout(timeout);
		return builder.build();
	}

	/**
	 * Sanity test to ensure that the SSH server used for testing is properly configured.
	 *
	 * @throws IOException if an error occurs connecting to the SSH server. This most likely indicates a configuration
	 * error in the SshServerIT test suite.
	 */
	@Test
	public void testSanity() throws IOException {
		final long connectionTimeoutMs = DEFAULT_TIMEOUT * 1000L;
		final SshClient client = SshClient.setUpDefaultClient();
		client.start();
		final ConnectFuture connectFuture = client.connect(SshServerIT.USERNAME, SshServerIT.HOST, SshServerIT.PORT);
		final ConnectFuture future = connectFuture.verify(connectionTimeoutMs);
		try (final ClientSession session = future.getSession()) {
			session.addPasswordIdentity(new String(SshServerIT.PASSWORD));
			final AuthFuture authFuture = session.auth();
			authFuture.verify(connectionTimeoutMs);
			Assert.assertThat("Connection unsuccessful", true, is(session.isAuthenticated()));
		}
	}

	@Test
	public void testFileTransfer() throws Exception {
		final byte[] content = "hello, world!".getBytes();
		final Path tmp = Paths.get("tmp.txt").toAbsolutePath();
		try {
			Files.createFile(tmp);
			Files.write(tmp, content);
			final ConnectionParameters params = buildConnectionParameters();
			try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
				connection.put(tmp, "test1.txt");
				Assert.assertThat("File not transferred correctly", Files.exists(Paths.get("test1.txt")), is(true));
			}
		} finally {
			Files.deleteIfExists(tmp);
			Files.deleteIfExists(Paths.get("test1.txt"));
		}
	}

	@Test
	// todo this test needs to be improved once there is an object for remote files.
	public void testDirectoryListing() throws Exception {
		final ConnectionParameters params = buildConnectionParameters();
		try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
			final Collection<String> directoryListing = connection.ls(".");
			final int workingDirFileCount = (int) Files.list(Paths.get("")).count();
			// We add two to the working dir file count because the directory listing contains entries for '.' and '..'
			Assert.assertThat("Incorrect file count in working directory", directoryListing.size(),
												is(workingDirFileCount + 2));
		}
	}

	@Test
	public void testUploadFile() throws Exception {
		final byte[] content = "hello, world!".getBytes();
		final Path tmp = Paths.get("tmp.txt").toAbsolutePath();
		try {
			Files.createFile(tmp);
			Files.write(tmp, content);
			final ConnectionParameters params = buildConnectionParameters();
			try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
				connection.put(tmp, "test1.txt");
				try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					connection.get("test1.txt", out);
					final byte[] remoteContent = out.toByteArray();
					Assert.assertThat("Downloaded file does not match uploaded file.", Arrays.equals(content, remoteContent),
														is(true));
				}
			}
		} finally {
			Files.deleteIfExists(tmp);
			Files.deleteIfExists(Paths.get("test1.txt"));
		}
	}

	@Test
	public void testRemoteDirectoryCreation() throws Exception {
		final ConnectionParameters params = buildConnectionParameters();
		try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
			final int startingWorkingDirFileCount = (int) Files.list(Paths.get("")).count();
			connection.mkdir("testdir");
			final int finalWorkingDirFileCount = (int) Files.list(Paths.get("")).count();
			// We add two to the working dir file count because the directory listing contains entries for '.' and '..'
			Assert.assertThat("Incorrect file count in working directory", finalWorkingDirFileCount,
												is(startingWorkingDirFileCount + 1));
		} finally {
			Files.deleteIfExists(Paths.get("testdir"));
		}
	}

	@Test
	public void testObtainWorkingDirectory() throws Exception {
		final ConnectionParameters params = buildConnectionParameters();
		try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
			final String workingDirectory = connection.pwd();
			Assert.assertThat("Unexpected working directory", workingDirectory, is(Paths.get("").toAbsolutePath().toString()));
		}
	}

	@Test
	public void testChangeDirectory() throws Exception {
		final ConnectionParameters params = buildConnectionParameters();
		try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
			connection.cd("/");
			String workingDirectory = connection.pwd();
			Assert.assertThat("Failed to change directory", workingDirectory, is("/"));
		}
	}

	/**
	 * Constructs a ConnectionParameters object to be used to connect to the test SSH server.
	 *
	 * @return ConnectionParameters Object that can be used to build a Connection to the test SSH server.
	 */
	private ConnectionParameters buildConnectionParameters() {
		return buildPasswordConnectionParameters(HOST, DEFAULT_TIMEOUT);
	}

	/**
	 * Shuts down the SFTP server used for testing.
	 *
	 * @throws IOException if an exception occurs while shutting down the SFTP server.
	 */
	@AfterClass
	public static void tearDown() throws IOException {
		server.stop(true);
		server.close();
	}
}
