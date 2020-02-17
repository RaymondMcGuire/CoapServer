package coap_server;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.BAD_REQUEST;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfig.Keys;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.UdpMulticastConnector;

/**
 * Test server using {@link UdpMulticastConnector}.
 */
public class CoapMultiCastServer {

	public static void main(String[] args) throws UnknownHostException {

		NetworkConfig config = NetworkConfig.getStandard();
		CoapEndpoint endpoint = createEndpoints(config);
		CoapServer server = new CoapServer(config);
		server.addEndpoint(endpoint);
		
		// lightings
		server.add(new IOT_Lightings());
		server.start();
	}

	private static CoapEndpoint createEndpoints(NetworkConfig config) throws UnknownHostException {
		int port = config.getInt(Keys.COAP_PORT);
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		InetSocketAddress localAddress = new InetSocketAddress(addr,port);
        
		Connector connector = new UdpMulticastConnector(localAddress, CoAP.MULTICAST_IPV4);
		return new CoapEndpoint.Builder().setNetworkConfig(config).setConnector(connector).build();
	}

	private static class IOT_Lightings extends CoapResource {

		private int light_id;
		private boolean open;

		private IOT_Lightings() {
			super("iot_lightings");
			getAttributes().setTitle("IOT Lightings");
			light_id = new Random(System.currentTimeMillis()).nextInt(100);
			open = false;
			System.out.println("light id: " + light_id);
		}

        @Override
        public void handleGET(CoapExchange exchange) {
            exchange.respond(String.valueOf(open));
        }

        @Override
        public void handlePUT(CoapExchange exchange) {
            byte[] payload = exchange.getRequestPayload();

            try {
            	String newVal = new String(payload, "UTF-8");
            	System.out.println("Update IOT Light1 status:"+ open+" ->" + newVal);
            	open = Boolean.valueOf(newVal);
                
                exchange.respond(CHANGED, String.valueOf(open));
            } catch (Exception e) {
                e.printStackTrace();
                exchange.respond(BAD_REQUEST, "Invalid String");
            }
        }
	}
}

