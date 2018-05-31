/*
 * sftp - sftp for java
 * Copyright (C) 2018  Zac Bowen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.zbb93.sftp;

/**
 * Generic Exception that is thrown when an Exception occurs during interaction with an SSH server.
 */
@SuppressWarnings("ClassWithoutLogger") // Exceptions do not need their own loggers.
class SSHException extends Exception {
	private static final long serialVersionUID = -3334244288075543948L;

	SSHException(final Throwable cause) {
		super(cause);
	}
}
