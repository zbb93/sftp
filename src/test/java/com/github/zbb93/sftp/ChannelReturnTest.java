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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

/**
 * These tests ensure that the methods defined in the Connection interface that take a Channel from the ChannelPool
 * return the Channel. This is accomplished by mocking the ChannelPool and verifying that the ChannelPool#returnChannel
 * method was invoked.
 *
 * All methods in this class that are testing a Connection method should be named testMethodname where Methodname
 * is the name of the method being tested. There is a test that verifies all methods are tested using reflection and
 * a regular expression. If a method does not utilize a Channel from the ChannelPool it should be added to the
 * EXCLUDED_METHODS Set.
 */
public class ChannelReturnTest {

	private ConnectionFactory factory;
	private MockChannelPoolFactory channelPoolFactory;

	private static final @NotNull Set<String> EXCLUDED_METHODS = Sets.newHashSet(
			"cd", "pwd", "close"
	);

	@Before
	public void setup() throws Exception{
		channelPoolFactory = buildMockChannelPoolFactory();
		factory = mockConnectionFactory();
	}

	private ConnectionFactory mockConnectionFactory() throws Exception {
		ConnectionFactory factory = mock(ConnectionFactory.class);
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		Connection connection = new ConnectionImpl(parameters, channelPoolFactory.getChannelPoolFactory());
		when(factory.getConnection(any(ConnectionParameters.class))).thenReturn(connection);
		return factory;
	}

	private MockChannelPoolFactory buildMockChannelPoolFactory() throws Exception {
		return new MockChannelPoolFactory();
	}

	@Test
	public void testLs() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.ls(".");
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testGet() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.get("", new ByteArrayOutputStream());
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testPut() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.put(Paths.get(""), "test.txt");
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testMkdir() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.mkdir("test");
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testRm() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.rm("test");
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testTestExistsForEveryMethodUsingChannel() {
		final Collection<Method> connectionMethods = Lists.newArrayList(Connection.class.getMethods());
		final Collection<Method> testMethods = Lists.newArrayList(getClass().getMethods());

		final Pattern testMethodPattern = Pattern.compile("test(?<methodName>[A-Z][a-z].*).*");
		final Collection<String> testedMethods = testMethods.stream()
																									.filter(method -> {
																										Matcher matcher = testMethodPattern.matcher(method.getName());
																										return matcher.matches();
																									}).map(method -> {
																										Matcher matcher = testMethodPattern.matcher(method.getName());
																										Preconditions.checkState(matcher.matches(), "Regex is broken.");
																										return matcher.group("methodName").toLowerCase(Locale.ENGLISH);
																									}).collect(Collectors.toList());
		testedMethods.addAll(EXCLUDED_METHODS);

		final List<Method> missingMethods = connectionMethods.stream().filter(method ->
			!(testedMethods.contains(method.getName()))
		).collect(Collectors.toList());
		final String missingMethodNames = missingMethods.stream().map(Method::toString).collect(Collectors.joining(", "));
		Assert.assertThat("The following methods are missing tests: " + missingMethodNames,
											missingMethods.isEmpty(), is(true));
	}

	/**
	 * Mocks a ChannelPoolFactory and maintains a reference to the mocked ChannelPool returned by the factory. This allows
	 * us to verify that the ChannelPool#returnChannel method is invoked.
	 */
	private static class MockChannelPoolFactory {
		private final ChannelPoolFactory factory;
		private final ChannelPool pool;
		MockChannelPoolFactory() throws Exception {
			pool = buildChannelPool();
			factory = buildChannelPoolFactory();
		}

		private ChannelPool buildChannelPool() throws Exception {
			ChannelPool pool = mock(ChannelPool.class);
			Channel channel = mock(Channel.class);
			when(pool.getNextAvailableChannel()).thenReturn(channel);
			return pool;
		}

		private ChannelPoolFactory buildChannelPoolFactory() throws Exception {
			final ChannelPoolFactory factory = mock(ChannelPoolFactory.class);
			when(factory.getChannelPool(any(ConnectionParameters.class))).thenReturn(pool);
			return factory;
		}

		ChannelPoolFactory getChannelPoolFactory() {
			return factory;
		}

		void assertChannelReturned() {
			verify(pool, times(1)).returnChannel(any(Channel.class));
		}
	}
}
