package com.bobsgame.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Vector;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import com.bobsgame.ServerMain;
import com.bobsgame.net.BobNet;

public class IndexClientTCP {
	public static Logger log = (Logger)LoggerFactory.getLogger(IndexClientTCP.class);

	private static Bootstrap clientBootstrap;
	private static ChannelFuture channelFuture;
    private static EventLoopGroup workerGroup;

	public IndexClientTCP() {
	}

	public void initBootstrap() {
        workerGroup = new NioEventLoopGroup();
		clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerGroup);
        clientBootstrap.channel(NioSocketChannel.class);
        clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(65536, Delimiters.lineDelimiter()));
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("handler", new IndexClientHandler());
            }
        });

		int serverPort = BobNet.INDEXServerTCPPort;
		String serverAddress = ServerMain.INDEXServerAddress;
		if(new File("/localServer").exists()) {
			serverAddress = "127.0.0.1";
		}

		connectToServer(serverAddress, serverPort);
	}

    private void connectToServer(String host, int port) {
        channelFuture = clientBootstrap.connect(host, port);
    }

	public void send_INDEX_User_Logged_On_This_Server_Log_Them_Off_Other_Servers(long userID) {}
	public void send_INDEX_Tell_All_Servers_To_Tell_FacebookIDs_That_UserID_Is_Online(long userID, String facebookFriendsCSV) {}
	public void send_INDEX_Tell_All_Servers_To_Tell_UserNames_That_UserID_Is_Online(long userID, String userNameFriendsCSV) {}
	public void send_INDEX_Tell_All_Servers_Bobs_Game_Hosting_Room_Update(String s, long userID) {}
	public void send_INDEX_Tell_All_Servers_Bobs_Game_Remove_Room(String s, long userID) {}

	public class IndexClientHandler extends SimpleChannelInboundHandler<String> {
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			log.warn("channelDisconnected to INDEX: ChannelID: "+ctx.channel().id());
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			log.info("channelConnected to INDEX: ChannelID: "+ctx.channel().id());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			log.error("Exception in IndexClientHandler: " + cause.getMessage());
			ctx.close();
		}

		@Override
		public void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
			if(BobNet.debugMode) log.warn("FROM INDEX: " + s);
		}
	}
}
