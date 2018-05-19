package com.github.zbb93.sftp;

import com.github.zbb93.sftp.connection.Connection;
import com.github.zbb93.sftp.connection.ConnectionFactory;
import com.github.zbb93.sftp.connection.ConnectionParameters;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;

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
	 * Constructs a ConnectionParameters object to be used to connect to the test SSH server. The Host and Port are
	 * configured in the ConnectionTest Suite and the Authentication is provided by the calling method.
	 *
	 * @return ConnectionParameters Object that can be used to build a Connection to the test SSH server.
	 */
	private ConnectionParameters buildConnectionParameters() {
		final ConnectionParameters.Builder builder = new ConnectionParameters.Builder(
				SshServerTests.HOST, SshServerTests.USERNAME, SshServerTests.PASSWORD, SshServerTests.PORT
		);
		return builder.build();
	}
}
