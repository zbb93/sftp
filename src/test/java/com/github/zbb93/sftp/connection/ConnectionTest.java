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

package com.github.zbb93.sftp.connection;

import com.github.zbb93.sftp.channel.Channel;
import com.github.zbb93.sftp.channel.ChannelPool;
import com.github.zbb93.sftp.channel.ChannelPoolFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ConnectionTest {

	private ConnectionFactory factory;
	private MockChannelPoolFactory channelPoolFactory;

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
	public void testLsReturnsConnection() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.ls(".");
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testGetReturnsConnection() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.get("", new ByteArrayOutputStream());
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testPutReturnsConnection() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.put(Paths.get(""), "test.txt");
		channelPoolFactory.assertChannelReturned();
	}

	@Test
	public void testMkdirReturnsConnection() throws Exception {
		ConnectionParameters parameters = mock(ConnectionParameters.class);
		final Connection connection = factory.getConnection(parameters);
		connection.mkdir("test");
		channelPoolFactory.assertChannelReturned();
	}

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
			verify(pool, Mockito.times(1)).returnChannel(any(Channel.class));
		}
	}
}
