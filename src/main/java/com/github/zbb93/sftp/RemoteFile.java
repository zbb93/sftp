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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A file on the remote server.
 */
@SuppressWarnings("ClassWithoutLogger") // POJO that does not need logging.
class RemoteFile {
	/**
	 * Relative name of the file.
	 */
	private final @NotNull String name;

	/**
	 * The owner of this file.
	 */
	private final @NotNull String owner;

	/**
	 * Primary group of this files owner. This is the group that group permissions apply to.
	 */
	private final @NotNull String group;

	/**
	 * File size in bytes.
	 */
	private final long size;

	/**
	 * Flag that indicates whether this file is a directory.
	 */
	private final boolean directory;

	/**
	 * Character used to indicate that a file is a directory in a UNIX file listing.
	 */
	private static final char DIRECTORY_FLAG = 'd';

	@SuppressWarnings("LongLine")
	private static final @NotNull Pattern FILE_LISTING = Pattern.compile(
			"[dl-][rwx-]{9} +\\d+ +(?<owner>\\w+) +(?<group>\\w+) +(?<size>\\d+) \\w+ [0123][0-9] \\d{2}:\\d{2} (?<name>[A-Za-z.]+)"
	);

	private RemoteFile(final @NotNull String fileName, final @NotNull String owner, final @NotNull String group,
										 final long size, final boolean directory) {
		name = fileName;
		this.owner = owner;
		this.group = group;
		this.size = size;
		this.directory = directory;
	}

	@SuppressWarnings("WeakerAccess")
	public @NotNull String getName() {
		return name;
	}

	@SuppressWarnings("WeakerAccess")
	public @NotNull String getOwner() {
		return owner;
	}

	@SuppressWarnings("WeakerAccess")
	public @NotNull String getGroup() {
		return group;
	}

	@SuppressWarnings("WeakerAccess")
	public long getSize() {
		return size;
	}

	@SuppressWarnings("WeakerAccess")
	public boolean isDirectory() {
		return directory;
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
													 .map(RemoteFile::parseFileListing)
													 .collect(Collectors.toList());
	}

	/**
	 * Uses the FILE_LISTING Pattern to create a RemoteFile object from a UNIX file listing.
	 *
	 * @param fileListing UNIX file listing to parse
	 * @return RemoteFile created from the provided file listing.
	 */
	private static RemoteFile parseFileListing(final @NotNull CharSequence fileListing) {
		final Matcher matcher = FILE_LISTING.matcher(fileListing);
		Preconditions.checkArgument(matcher.matches());
		final long size = Long.parseLong(matcher.group("size"));
		final String owner = matcher.group("owner");
		final String group = matcher.group("group");
		final String fileName = matcher.group("name");
		final boolean directory = fileListing.charAt(0) == DIRECTORY_FLAG;
		return new RemoteFile(fileName, owner, group, size, directory);
	}

	@Override
	public int hashCode() {
		int hashCode = 13 * name.hashCode();
		hashCode *= owner.hashCode();
		hashCode *= group.hashCode();
		hashCode *= directory ? 5 : 7;
		hashCode *= Long.hashCode(size);
		return hashCode;
	}

	@Override
	@SuppressWarnings("MethodWithMultipleReturnPoints")
	public boolean equals(final Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof RemoteFile)) {
			return false;
		}

		final RemoteFile other = (RemoteFile) o;
		return name.equals(other.getName());
	}

	@Override
	@SuppressWarnings("MagicCharacter")
	public String toString() {
		return "RemoteFile{" +
					 "name='" + name + '\'' +
					 ", owner='" + owner + '\'' +
					 ", group='" + group + '\'' +
					 ", size=" + size +
					 ", directory=" + directory +
					 '}';
	}
}
