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

import com.google.common.collect.Lists;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a suite of tests that need to be run against an SSH server. The methods executed before and after the tests
 * set up and tear down the SSH server used for testing.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		TestConnection.class,
		TestSftp.class
})
public final class SshServerTests {

	/**
	 * SSH server used for testing.
	 */
	private static final SshServer server = SshServer.setUpDefaultServer();

	/**
	 * Host of the SSH server used for testing.
	 */
	static final @NotNull String HOST = "localhost";

	/**
	 * Port of the SSH server used for testing.
	 */
	static final int PORT = 4000;

	/**
	 * Username used to connect to the SSH server.
	 */
	static final @NotNull String USERNAME = "test";

	/**
	 * Password used in conjunction with the above username to connect to the SSH server.
	 */
	static final @NotNull byte[] PASSWORD = "test".getBytes();

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
