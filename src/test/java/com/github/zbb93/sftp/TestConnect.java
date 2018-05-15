package com.github.zbb93.sftp;

import com.github.zbb93.sftp.connection.*;
import com.github.zbb93.sftp.session.auth.*;
import org.apache.sshd.client.*;
import org.apache.sshd.client.future.*;
import org.apache.sshd.client.session.*;
import org.junit.*;

import java.io.*;

import static org.hamcrest.CoreMatchers.*;

/**
 * Verifies that it is possible to connect to a remote server using various authentication methods.
 */
public class TestConnect {

	/**
	 * Sanity test to ensure that the SSH server used for testing is properly configured.
	 *
	 * @throws IOException if an error occurs connecting to the SSH server. This most likely indicates a configuration
	 * error in the SshServerTests test suite.
	 */
	@Test
	public void testSanity() throws IOException {
		SshClient client = SshClient.setUpDefaultClient();
		client.start();
		ConnectFuture future = client.connect(SshServerTests.USERNAME, SshServerTests.HOST, SshServerTests.PORT).verify(20000);
		try (ClientSession session = future.getSession()) {
			session.addPasswordIdentity(SshServerTests.PASSWORD);
			session.auth().verify(20000);
			Assert.assertThat("Connection unsuccessful", true, is(session.isAuthenticated()));
		}
	}

	/**
	 * Verifies that we can connect to an SSH server using password authentication.
	 *
	 * @throws SSHException if an error occurs while connecting to the SSH server.
	 */
	@Test
	public void testPasswordConnect() throws SSHException {
		ConnectionParameters connectionParameters = buildPasswordConnectionParameters();
		Connection connection = ConnectionFactory.INSTANCE.getConnection(connectionParameters);
		connection.connect();
		Assert.assertThat("Connection unsuccessful", true, is(connection.isConnected()));
	}

	/**
	 * This test uses an unroutable IP address to ensure that connection timeout is working as expected.
	 *
	 * @throws SSHException if an unexpected error occurs while connecting to the SSH server.
	 */
	@Test
	public void testConnectionTimeout() throws SSHException {
		ConnectionParameters connectionParameters = buildPasswordConnectionParameters("10.255.255.1", 1);
		Connection connection = ConnectionFactory.INSTANCE.getConnection(connectionParameters);
		try {
			connection.connect();
			Assert.fail("Connection did not timeout.");
		} catch (SSHException e) {
			Assert.assertThat("Unexpected SSHException occurred.", e.getMessage(), containsString("timeout"));
		}
	}

	/**
	 * Constructs a ConnectionParameters object to be used to connect to the test SSH server. The host, port, username,
	 * and password are constructed in the test suite.
	 *
	 * @return ConnectionParameters Object that can be used to build a Connection to the test SSH server.
	 */
	private ConnectionParameters buildPasswordConnectionParameters() {
		return buildPasswordConnectionParameters(SshServerTests.HOST, 0);
	}

	private ConnectionParameters buildPasswordConnectionParameters(final String host, final int timeout) {
		Authentication passwordAuthentication = AuthenticationFactory.INSTANCE.authenticationFor(SshServerTests.USERNAME,
				SshServerTests.PASSWORD);
		ConnectionParameters.Builder builder = new ConnectionParameters.Builder(host, passwordAuthentication,
				SshServerTests.PORT);
		builder.setTimeout(timeout);
		return builder.build();
	}
}
