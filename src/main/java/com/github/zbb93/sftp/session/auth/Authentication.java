package com.github.zbb93.sftp.session.auth;

import com.github.zbb93.sftp.session.*;
import org.jetbrains.annotations.*;

/**
 * Implementations of this Interface are responsible for configuring a method of authentication with an SSH server for
 * a RemoteSession Object.
 */
public interface Authentication {
	/**
	 * Configures a method of authentication with an SSH server for a RemoteSession Object.
	 * @param session the RemoteSession to configure authentication for.
	 */
	void authenticate(final @NotNull RemoteSession session);

	/**
	 * @return the username to use when connecting to the remote server.
	 */
	String getUser();
}
