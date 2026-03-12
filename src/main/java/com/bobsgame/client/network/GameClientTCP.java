package com.bobsgame.client.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ConnectException;

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
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import com.bobsgame.ClientMain;
import com.bobsgame.client.console.Console;
import com.bobsgame.client.engine.EnginePart;
import com.bobsgame.client.engine.game.ClientGameEngine;
import com.bobsgame.net.BobNet;
import com.bobsgame.shared.BobColor;

public class GameClientTCP extends EnginePart {
	public static Logger log = (Logger)LoggerFactory.getLogger(GameClientTCP.class);

	private static Bootstrap clientBootstrap;
	private static ChannelFuture channelFuture;
    private static EventLoopGroup workerGroup;

	public GameClientTCP(ClientGameEngine g) {
		super(g);
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
                pipeline.addLast("handler", new BobsGameClientHandler());
            }
        });
	}

	public class BobsGameClientHandler extends SimpleChannelInboundHandler<String> {
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			log.warn("channelDisconnected from Server: ChannelID: "+ctx.channel().id());
			Console.add("Disconnected from Server.", BobColor.red, 5000);
			setConnectedToServer_S(false);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			log.info("channelConnected to Server: ChannelID: "+ctx.channel().id());
			Console.add("Connected to Server!", BobColor.green, 5000);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			log.error("Exception in BobsGameClientHandler: " + cause.getMessage());
			ctx.close();
		}

		@Override
		public void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
			if(BobNet.debugMode) log.warn("FROM SERVER: " + s);
		}
	}

    private Channel _channel;
    synchronized private void setChannel_S(Channel c) { _channel = c; }
    synchronized private Channel getChannel_S() { return _channel; }

    private boolean _connectedToServer = false;
    synchronized private void setConnectedToServer_S(boolean b) { _connectedToServer = b; }
    synchronized public boolean getConnectedToServer_S() { return _connectedToServer; }

    public void connectToServer(String host, int port) {
        channelFuture = clientBootstrap.connect(host, port);
    }

    public void connectToServer() { connectToServer("127.0.0.1", 1234); }

    public void addQueuedGameSaveUpdateRequest_S(String s) {}
    public void sendMapDataRequestByID(int id) {}
    public void sendMapDataRequestByName(String name) {}
    public void sendInitialGameSaveRequest() {}
    public void sendQueuedGameSaveUpdates() {}
    public void sendLoadEventRequest() {}
    public void sendUpdateFacebookAccountInDBRequest() {}
    public boolean getFacebookAccountUpdateResponseReceived_S() { return false; }
    public boolean getFacebookAccountUpdateResponseWasValid_S() { return false; }
    public void setFacebookAccountUpdateResponseState_S(boolean a, boolean b) {}
    public void sendOnlineFriendListRequest() {}
    public void sendSpriteDataRequestByName(String name) {}
    public void sendSpriteDataRequestByID(int id) {}
    public void send(String s) {}
    public void sendCreateAccountRequest(String a, String b) {}
    public boolean getGotCreateAccountResponse_S() { return false; }
    public void sendFacebookLoginCreateAccountIfNotExist(String a, String b, boolean c) {}
    public boolean getGotFacebookLoginResponse_S() { return false; }
    public boolean getWasFacebookLoginResponseValid_S() { return false; }
    public void setGotFacebookLoginResponse_S(boolean b) {}
    public void sendPasswordRecoveryRequest(String s) {}
    public boolean getGotPasswordRecoveryResponse_S() { return false; }
    public void setGotPasswordRecoveryResponse_S(boolean b) {}
    public void sendLoginRequest(String a, String b, boolean c) {}
    public boolean getAuthorizedOnServer() { return false; }

    public int getUserID_S() { return -1; }
    public String getSessionToken_S() { return ""; }
    public boolean getGotLoginResponse_S() { return false; }
    public boolean getWasLoginResponseValid_S() { return false; }
    public void setGotLoginResponse_S(boolean b) {}
    public void sendReconnectRequest(int id, String token, boolean allowed) {}
    public boolean getGotReconnectResponse_S() { return false; }
    public boolean getWasReconnectResponseValid_S() { return false; }
    public void setGotReconnectResponse_S(boolean b) {}
    public void cleanup() {}
    public void sendServerObjectRequest(com.bobsgame.client.engine.event.ServerObject serverObject) {}
}
