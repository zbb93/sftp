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

package com.github.zbb93.sftp.channel;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A file on the remote server.
 */
public class RemoteFile {
	/**
	 * Relative name of the file.
	 */
	private final @NotNull String name;

	private static final @NotNull Pattern FILE_LISTING = Pattern.compile(
			"[a-z-]{10} +\\d+ +\\w+ +\\w+ +\\d+ \\w+ [0123][0-9] \\d{2}:\\d{2} ([A-Za-z.]+)"
	);

	RemoteFile(final @NotNull String fileName) {
		name = fileName;
	}

	/**
	 * Convenience method to obtain a Collection of RemoteFiles parsed from the directory listing returned by JSch.
	 *
	 * @param directoryListing Collection of String file listings.
	 * @return Collection of RemoteFiles created by parsing the file listings.
	 */
	static Collection<RemoteFile> getRemoteFilesForDirectory(final @NotNull Collection<String> directoryListing) {
		return directoryListing.stream()
													 .filter(fileListing -> (FILE_LISTING.matcher(fileListing).matches()))
													 .map(fileListing -> {
														 Matcher matcher = FILE_LISTING.matcher(fileListing);
														 matcher.matches();
														 String fileName = matcher.group(1);
														 return new RemoteFile(fileName);
													 })
													 .collect(Collectors.toList());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return name.equals(o);
	}
}
