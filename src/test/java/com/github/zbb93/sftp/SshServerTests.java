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
import java.util.logging.ConsoleHandler;
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
	static final @NotNull String PASSWORD = "test";

	/**
	 * Configures the SFTP server to be used for testing.
	 *
	 * @throws IOException if an error occurs configuring the server.
	 */
	@BeforeClass
	public static void setUp() throws IOException {
		configureLogger();
		server.setHost(HOST);
		server.setPort(PORT);
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		server.setPasswordAuthenticator((username, password, session) ->
																				username.equals(USERNAME) && password.equals(PASSWORD));
		server.setSubsystemFactories(Lists.newArrayList(new SftpSubsystemFactory()));
		server.start();
	}

	private static void configureLogger() {
		final Logger globalLogger = Logger.getLogger("");
		globalLogger.setLevel(Level.FINEST);
		final Handler handler = new ConsoleHandler();
		handler.setLevel(Level.FINEST);
		globalLogger.addHandler(handler);
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
