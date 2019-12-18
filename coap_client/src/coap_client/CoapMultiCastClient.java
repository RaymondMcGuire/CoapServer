package coap_client;

import java.io.File;
import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfig.Keys;
import org.eclipse.californium.core.network.config.NetworkConfigDefaultHandler;
import org.eclipse.californium.elements.exception.ConnectorException;

/**
 * Test client configured to support multicast requests.
 */
public class CoapMultiCastClient {

	/**
	 * File name for network configuration.
	 */
	private static final File CONFIG_FILE = new File("CaliforniumMulticast.properties");
	/**
	 * Header for network configuration.
	 */
	private static final String CONFIG_HEADER = "Californium CoAP Properties file for Multicast Client";
	/**
	 * Special network configuration defaults handler.
	 */
	private static NetworkConfigDefaultHandler DEFAULTS = new NetworkConfigDefaultHandler() {

		@Override
		public void applyDefaults(NetworkConfig config) {
			config.setInt(Keys.MULTICAST_BASE_MID, 65000);
		}

	};

	public static void main(String args[]) {

		NetworkConfig config = NetworkConfig.createWithFile(CONFIG_FILE, CONFIG_HEADER, DEFAULTS);

		CoapEndpoint endpoint = new CoapEndpoint.Builder().setNetworkConfig(config).build();
		CoapClient client = new CoapClient();

		client.setURI("coap://127.0.0.2:5685/helloWorld");
		client.setEndpoint(endpoint);

		Request request = Request.newGet();
		request.setType(Type.CON);

		CoapResponse response = null;
		try {
			// sends an uni-cast request
			response = client.advanced(request);
		} catch (ConnectorException | IOException e) {
			System.err.println("Error occurred while sending request: " + e);
		}

		if (response != null) {
			System.out.println(Utils.prettyPrint(response));
		} else {
			System.out.println("No response received.");
		}

		client.setURI("coap://" + CoAP.MULTICAST_IPV4.getHostAddress() + "/helloWorld");
		Request multicastRequest = Request.newGet();
		multicastRequest.setType(Type.NON);
		// sends a multicast request
		client.advanced(handler, multicastRequest);
		while (handler.waitOn(2000))
			;
		client.shutdown();
	}

	private static final MultiCoapHandler handler = new MultiCoapHandler();

	private static class MultiCoapHandler implements CoapHandler {

		private boolean on;

		public synchronized boolean waitOn(long timeout) {
			on = false;
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
			return on;
		}

		private synchronized void on() {
			on = true;
			notifyAll();
		}

		@Override
		public void onLoad(CoapResponse response) {
			on();
			System.out.println(response.advanced().getSourceContext().getPeerAddress());
			System.out.println(Utils.prettyPrint(response));
		}

		@Override
		public void onError() {
			System.err.println("error");
		}
	};
}
