package com.github.zbb93.sftp.channel;

/**
 * An SFTP connection to a remote server. A single RemoteSession can have multiple SftpChannels.
 */
public interface Channel {
	void connect();
	void disconnect();
}
