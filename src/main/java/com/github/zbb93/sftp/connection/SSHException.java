package com.github.zbb93.sftp.connection;

/**
 * Generic Exception that is thrown when an Exception occurs during interaction with an SSH server.
 */
public class SSHException extends Exception {
	public SSHException(Throwable cause) {
		super(cause);
	}
}
