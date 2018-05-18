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
	public void testFileTransfer() throws Exception {
		final byte[] content = "hello, world!".getBytes();
		final Path tmp = Paths.get("tmp.txt").toAbsolutePath();
		try {
			Files.createFile(tmp);
			Files.write(tmp, content);
			final ConnectionParameters params = buildConnectionParameters();
			try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
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
	public void testDirectoryListing() throws Exception {
		final ConnectionParameters params = buildConnectionParameters();
		try (final Connection connection = ConnectionFactory.INSTANCE.getConnection(params)) {
			connection.connect();
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
				connection.connect();
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
			connection.connect();
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
			connection.connect();
			final String workingDirectory = connection.pwd();
			Assert.assertThat("Unexpected working directory", workingDirectory, is(Paths.get("").toAbsolutePath().toString()));
		}
	}

	/**
	 * Constructs a ConnectionParameters object to be used to connect to the test SSH server. The Host and Port are
	 * configured in the ConnectionTest Suite and the Authentication is provided by the calling method.
	 *
	 * @return ConnectionParameters Object that can be used to build a Connection to the test SSH server.
	 */
	private ConnectionParameters buildConnectionParameters() {
		final Authentication authentication = AuthenticationFactory.INSTANCE.authenticationFor(SshServerTests.USERNAME,
																																													 SshServerTests.PASSWORD);
		final ConnectionParameters.Builder builder = new ConnectionParameters.Builder(SshServerTests.HOST, authentication,
																																									SshServerTests.PORT);
		return builder.build();
	}
}
