package com.github.zbb93.sftp;

import com.github.zbb93.sftp.connection.Connection;
import com.github.zbb93.sftp.connection.ConnectionFactory;
import com.github.zbb93.sftp.connection.ConnectionParameters;
import com.github.zbb93.sftp.connection.SSHException;
import com.github.zbb93.sftp.session.auth.Authentication;
import com.github.zbb93.sftp.session.auth.AuthenticationFactory;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

/**
 * Tests for implementations of the Connection interface.
 */
public class TestConnection {
	private static final long TEST_TIMEOUT = 20000L;

	/**
	 * Sanity test to ensure that the SSH server used for testing is properly configured.
	 *
	 * @throws IOException if an error occurs connecting to the SSH server. This most likely indicates a configuration
	 * error in the SshServerTests test suite.
	 */
	@Test
	public void testSanity() throws IOException {
		final long connectionTimeoutMs = TEST_TIMEOUT;
		final SshClient client = SshClient.setUpDefaultClient();
		client.start();
		final ConnectFuture connectFuture = client.connect(SshServerTests.USERNAME, SshServerTests.HOST, SshServerTests.PORT);
		final ConnectFuture future = connectFuture.verify(connectionTimeoutMs);
		try (final ClientSession session = future.getSession()) {
			session.addPasswordIdentity(SshServerTests.PASSWORD);
			final AuthFuture authFuture = session.auth();
			authFuture.verify(connectionTimeoutMs);
			Assert.assertThat("Connection unsuccessful", true, is(session.isAuthenticated()));
		}
	}

	/**
	 * Verifies that we can connect to an SSH server using password authentication.
	 *
	 * @throws SSHException if an error occurs while connecting to the SSH server.
	 */
	@Test
	public void testPasswordConnect() throws Exception {
		final ConnectionParameters connectionParameters = buildPasswordConnectionParameters();
		try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(connectionParameters)) {
			connection.connect();
			Assert.assertThat("Connection unsuccessful", true, is(connection.isConnected()));
		}
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
			connection.connect();
			Assert.fail("Connection did not timeout.");
		} catch (final SSHException e) {
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
		final Authentication passwordAuthentication = AuthenticationFactory.INSTANCE.authenticationFor(
				SshServerTests.USERNAME, SshServerTests.PASSWORD
		);
		final ConnectionParameters.Builder builder = new ConnectionParameters.Builder(host, passwordAuthentication,
																																									SshServerTests.PORT);
		builder.setTimeout(timeout);
		return builder.build();
	}
}
