package com.bobsgame.stunserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import com.bobsgame.STUNServerMain;
import com.bobsgame.net.BobNet;

public class STUNServerUDP {
	public static Logger log = (Logger)LoggerFactory.getLogger(STUNServerUDP.class);

	public STUNServerUDP() {
		this(BobNet.STUNServerUDPPort);
	}

	public STUNServerUDP(int port) {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioDatagramChannel.class)
			 .option(ChannelOption.SO_BROADCAST, true)
			 .handler(new STUNServerHandler());

			b.bind(port).sync().channel().closeFuture().await();
		} catch (Exception e) {
			log.error("STUN Server error: " + e.getMessage());
		} finally {
			group.shutdownGracefully();
		}
	}

	public class STUNServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
			String s = packet.content().toString(CharsetUtil.UTF_8);
			if(s.endsWith(BobNet.endline)) {
				s = s.substring(0, s.length() - BobNet.endline.length());
			}
			STUNServerMain.totalConnections++;
			if(s.startsWith(BobNet.STUN_Request)) {
				incomingSTUNRequest(ctx, s, packet.sender());
			}
		}

		private void incomingSTUNRequest(ChannelHandlerContext ctx, String s, InetSocketAddress sender) {
            // Implementation
		}
	}

	public void update() {}
	public int getSTUNRequestListSize() { return 0; }

	public class STUNRequest {
		public long userID1 = -1;
		public long userID2 = -1;
		public long lastHeardFromTime = 0;
		public int user1Port = -1;
		public int user2Port = -1;
		public InetSocketAddress userIP1 = null;
		public InetSocketAddress userIP2 = null;

		public STUNRequest(long userID1, long userID2, InetSocketAddress userIP1, int user1Port) {
			lastHeardFromTime = System.currentTimeMillis();
			this.userID1 = userID1;
			this.userID2 = userID2;
			this.userIP1 = userIP1;
			this.user1Port = user1Port;
		}
	}
}
