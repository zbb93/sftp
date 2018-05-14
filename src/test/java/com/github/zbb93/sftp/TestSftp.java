package com.github.zbb93.sftp;

import com.github.zbb93.sftp.connection.*;
import com.github.zbb93.sftp.session.auth.*;
import org.junit.*;

import java.io.*;
import java.nio.file.*;

import static org.hamcrest.CoreMatchers.*;

public class TestSftp {

	@Test
	public void testFileTransfer() throws IOException, SSHException {
		byte[] content = "hello, world!".getBytes();
		Path tmp = Paths.get("tmp.txt").toAbsolutePath();
		try {
			Files.createFile(tmp);
			Files.write(tmp, content);
			ConnectionParameters params = buildConnectionParameters();
			Connection connection = ConnectionFactory.INSTANCE.getConnection(params);
			connection.connect();
			connection.put(tmp, "test1.txt");
			Assert.assertThat("File not transferred correctly", Files.exists(Paths.get("test1.txt")), is(true));
		} finally {
			Files.deleteIfExists(tmp);
			Files.deleteIfExists(Paths.get("test-transfer/test1.txt"));
		}
	}

	/**
	 * Constructs a ConnectionParameters object to be used to connect to the test SSH server. The Host and Port are
	 * configured in the ConnectionTest Suite and the Authentication is provided by the calling method.
	 *
	 * @return ConnectionParameters Object that can be used to build a Connection to the test SSH server.
	 */
	private ConnectionParameters buildConnectionParameters() {
		Authentication authentication = AuthenticationFactory.INSTANCE.authenticationFor(SshServerTests.USERNAME,
				SshServerTests.PASSWORD);
		ConnectionParameters.Builder builder = new ConnectionParameters.Builder(SshServerTests.HOST, authentication, SshServerTests.PORT);
		return builder.build();
	}
}
