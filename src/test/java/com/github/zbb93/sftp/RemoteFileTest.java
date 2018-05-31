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

import com.google.common.collect.Lists;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class RemoteFileTest {

	@Test
	public void testFileParsing() {
		List<String> listing = Lists.newArrayList("-rw-rw-r--   1 zbb      zbb           695 May 14 23:51 .gitignore");
		Collection<RemoteFile> remoteFiles = RemoteFile.getRemoteFilesForDirectory(listing);
		RemoteFile file = remoteFiles.iterator().next();
		Assert.assertThat("Name not parsed correctly.", file.getName(), CoreMatchers.is(".gitignore"));
		Assert.assertThat("Directory incorrectly identified as a file.", file.isDirectory(),
											CoreMatchers.is(false));
		Assert.assertThat("Owner incorrectly parsed.", file.getOwner(), CoreMatchers.is("zbb"));
		Assert.assertThat("Group incorrectly parsed.", file.getGroup(), CoreMatchers.is("zbb"));
		Assert.assertThat("Size incorrectly parsed", file.getSize(), CoreMatchers.is(695L));
	}

	@Test
	public void testDirectoryParsing() {
		List<String> listing = Lists.newArrayList("drwxrwxr-x   4 zbb      zbb          4096 Apr 25 21:31 src");
		Collection<RemoteFile> remoteFiles = RemoteFile.getRemoteFilesForDirectory(listing);
		RemoteFile file = remoteFiles.iterator().next();
		Assert.assertThat("Name not parsed correctly.", file.getName(), CoreMatchers.is("src"));
		Assert.assertThat("Directory incorrectly identified as a file.", file.isDirectory(),
											CoreMatchers.is(true));
		Assert.assertThat("Owner incorrectly parsed.", file.getOwner(), CoreMatchers.is("zbb"));
		Assert.assertThat("Group incorrectly parsed.", file.getGroup(), CoreMatchers.is("zbb"));
		Assert.assertThat("Size incorrectly parsed", file.getSize(), CoreMatchers.is(4096L));
	}
}
