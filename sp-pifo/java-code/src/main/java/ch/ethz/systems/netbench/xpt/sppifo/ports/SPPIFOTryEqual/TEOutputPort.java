package ch.ethz.systems.netbench.xpt.sppifo.ports.SPPIFOTryEqual;

import ch.ethz.systems.netbench.core.network.Link;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.OutputPort;
import ch.ethz.systems.netbench.core.network.Packet;

public class TEOutputPort extends OutputPort {

	public TEOutputPort(NetworkDevice ownNetworkDevice, NetworkDevice targetNetworkDevice, Link link, long numberQueues,
			long sizePerQueuePackets, String stepSize) {
		super(ownNetworkDevice, targetNetworkDevice, link,
				new TEQueue(numberQueues, sizePerQueuePackets, ownNetworkDevice, stepSize));
	}

	/**
	 * Enqueue the given packet.
	 *
	 * @param packet Packet instance
	 */
	@Override
	public void enqueue(Packet packet) {

		// Enqueue packet
		potentialEnqueue(packet);
	}
}
