package com.github.zbb93.sftp;

import com.google.common.collect.*;
import org.apache.sshd.server.*;
import org.apache.sshd.server.keyprovider.*;
import org.apache.sshd.server.subsystem.sftp.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;

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
	static final @NotNull String PASSWORD = "test";

	/**
	 * Configures the SFTP server to be used for testing.
	 *
	 * @throws IOException if an error occurs configuring the server.
	 */
	@BeforeClass
	public static void setUp() throws IOException {
		server.setHost(HOST);
		server.setPort(PORT);
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		server.setPasswordAuthenticator((username, password, session) ->
																				username.equals(USERNAME) && password.equals(PASSWORD));
		server.setSubsystemFactories(Lists.newArrayList(new SftpSubsystemFactory()));
		server.start();
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
