package com.github.zbb93.sftp;

import com.github.zbb93.sftp.connection.*;
import com.github.zbb93.sftp.session.auth.*;
import org.apache.sshd.client.*;
import org.apache.sshd.client.future.*;
import org.apache.sshd.client.session.*;
import org.jetbrains.annotations.*;
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
		ConnectFuture future = client.connect(SshServerTests.USERNAME, SshServerTests.HOST, SshServerTests.PORT);
		future.verify();
		try (ClientSession session = future.getSession()) {
			session.addPasswordIdentity(SshServerTests.PASSWORD);
			session.auth().verify(60000);
			Assert.assertThat("Connection unsuccessful", true, is(session.isAuthenticated()));
		}
	}

	/**
	 * Verifies that we can connect to an SSH server using password authentication.
	 *
	 * @throws SSHException if an error occurs while connection to the SSH server.
	 */
	@Test
	public void testPasswordConnect() throws SSHException {
		Authentication passwordAuthentication = AuthenticationFactory.INSTANCE.authenticationFor(SshServerTests.USERNAME,
				SshServerTests.PASSWORD);
		ConnectionParameters connectionParameters = buildConnectionParameters(passwordAuthentication);
		Connection connection = ConnectionFactory.INSTANCE.getConnection(connectionParameters);
		connection.connect();
		Assert.assertThat("Connection unsuccessful", true, is(connection.isConnected()));
	}

	/**
	 * Constructs a ConnectionParameters object to be used to connect to the test SSH server. The Host and Port are
	 * configured in the ConnectionTest Suite and the Authentication is provided by the calling method.
	 *
	 * @param authentication Authentication to be used when connecting to the SSH server.
	 * @return ConnectionParameters Object that can be used to build a Connection to the test SSH server.
	 */
	private ConnectionParameters buildConnectionParameters(final @NotNull Authentication authentication) {
		ConnectionParameters.Builder builder = new ConnectionParameters.Builder(SshServerTests.HOST, authentication, SshServerTests.PORT);
		return builder.build();
	}
}
