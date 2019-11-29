package coap_server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.tcp.netty.TcpServerConnector;

public class CoapServerMain extends CoapServer {

	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
	private static final int TCP_THREADS = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.TCP_WORKER_THREADS);
	private static final int TCP_IDLE_TIMEOUT = NetworkConfig.getStandard()
			.getInt(NetworkConfig.Keys.TCP_CONNECTION_IDLE_TIMEOUT);

	/*
	 * Application entry point.
	 */
	public static void main(String[] args) {

		try {
			// create server
			boolean udp = true;
			boolean tcp = false;
			if (0 < args.length) {
				tcp = args[0].equalsIgnoreCase("coap+tcp:");
				if (tcp) {
					System.out.println("Please Note: the TCP support is currently experimental!");
				}
			}
			CoapServerMain server = new CoapServerMain();
			// add endpoints on all IP addresses
			server.addEndpoints(udp, tcp);
			server.start();

		} catch (SocketException e) {
			System.err.println("Failed to initialize server: " + e.getMessage());
		}
	}

	/**
	 * Add individual endpoints listening on default CoAP port on all IPv4
	 * addresses of all network interfaces.
	 */
	private void addEndpoints(boolean udp, boolean tcp) {
		NetworkConfig config = NetworkConfig.getStandard();
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
			if (udp) {
				CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
				builder.setInetSocketAddress(bindToAddress);
				builder.setNetworkConfig(config);
				addEndpoint(builder.build());
			}
			if (tcp) {
				TcpServerConnector connector = new TcpServerConnector(bindToAddress, TCP_THREADS, TCP_IDLE_TIMEOUT);
				CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
				builder.setConnector(connector);
				builder.setNetworkConfig(config);
				addEndpoint(builder.build());
			}

		}
	}

	/*
	 * Constructor for a new Hello-World server. Here, the resources of the
	 * server are initialized.
	 */
	public CoapServerMain() throws SocketException {

		// provide an instance of a Hello-World resource
		add(new HelloWorldResource());
	}

	/*
	 * Definition of the Hello-World Resource
	 */
	class HelloWorldResource extends CoapResource {

		public HelloWorldResource() {

			// set resource identifier
			super("helloWorld");

			// set display name
			getAttributes().setTitle("Hello-World Resource");
		}

		@Override
		public void handleGET(CoapExchange exchange) {

			// respond to the request
			exchange.respond("Hello World!");
		}
	}
}
