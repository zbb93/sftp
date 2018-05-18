package com.github.zbb93.sftp.connection;

/**
 * Generic Exception that is thrown when an Exception occurs during interaction with an SSH server.
 */
// todo this may belong in the sftp package.
public class SSHException extends Exception {
	public SSHException(final Throwable cause) {
		super(cause);
	}
}
