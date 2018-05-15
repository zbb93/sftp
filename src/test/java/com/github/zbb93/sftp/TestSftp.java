package com.github.zbb93.sftp;

import com.github.zbb93.sftp.connection.*;
import com.github.zbb93.sftp.session.auth.*;
import org.junit.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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
			try (Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
				connection.connect();
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
	public void testDirectoryListing() throws IOException, SSHException {
		ConnectionParameters params = buildConnectionParameters();
		try (Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
			connection.connect();
			Collection<String> directoryListing = connection.ls(".");
			int workingDirFileCount = (int) Files.list(Paths.get("")).count();
			// We add two to the working dir file count because the directory listing contains entries for '.' and '..'
			Assert.assertThat("Incorrect file count in working directory", directoryListing.size(),
					is(workingDirFileCount + 2));
		}
	}

	@Test
	public void testUploadFile() throws IOException, SSHException {
		byte[] content = "hello, world!".getBytes();
		Path tmp = Paths.get("tmp.txt").toAbsolutePath();
		try {
			Files.createFile(tmp);
			Files.write(tmp, content);
			ConnectionParameters params = buildConnectionParameters();
			try (Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
				connection.connect();
				connection.put(tmp, "test1.txt");
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					connection.get("test1.txt", out);
					byte[] remoteContent = out.toByteArray();
					Assert.assertThat("Downloaded file does not match uploaded file.", Arrays.equals(content, remoteContent),
							is(true));
				}
			}
		} finally {
			Files.deleteIfExists(tmp);
			Files.deleteIfExists(Paths.get("test1.txt"));
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
