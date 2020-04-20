package ch.ethz.systems.netbench.xpt.sppifo.ports.SPPIFOTryEqual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;
import ch.ethz.systems.netbench.xpt.tcpbase.PriorityHeader;

// General SPPIFO implementation to be used, for instance, when the ranks are specified from the end-host.
public class TEQueue implements Queue {

	private final ArrayList<ArrayBlockingQueue> queueList;
	// private final ArrayList<Integer> queueStoreNum;
	private final Map queueBounds;
	private ReentrantLock reentrantLock;
	private int ownId;
	private String stepSize;

	public TEQueue(long numQueues, long perQueueCapacity, NetworkDevice ownNetworkDevice, String stepSize) {
		this.queueList = new ArrayList((int) numQueues);
		this.reentrantLock = new ReentrantLock();
		this.queueBounds = new HashMap();

		ArrayBlockingQueue<Packet> fifo;
		for (int i = 0; i < (int) numQueues; i++) {
			fifo = new ArrayBlockingQueue<Packet>((int) perQueueCapacity);
			queueList.add(fifo);
			queueBounds.put(i, 0);
		}
		this.ownId = ownNetworkDevice.getIdentifier();
		this.stepSize = stepSize;
	}

	// Packet dropped and null returned if selected queue exceeds its size
	@Override
	public boolean offer(Object o) {

		// Extract rank from header
		Packet packet = (Packet) o;
		PriorityHeader header = (PriorityHeader) packet;
		int rank = (int) header.getPriority();

		this.reentrantLock.lock();
		boolean returnValue = false;
		try {
			// Mapping based on queue bounds
			int currentQueueBound;
			for (int q = queueList.size() - 1; q >= 0; q--) {
				currentQueueBound = (int) queueBounds.get(q);
				if ((currentQueueBound <= rank) || q == 0) {
					boolean result = queueList.get(q).offer(o);
					if (!result) {
						returnValue = false;
						break;
					} else {

						// Per-packet queue bound adaptation
						queueBounds.put(q, rank);
						int cost = currentQueueBound - rank;

						returnValue = true;
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			this.reentrantLock.unlock();
			return returnValue;
		}
	}

	@Override
	public int size() {
		int size = 0;
		for (int q = 0; q < queueList.size(); q++) {
			size += queueList.get(q).size();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		boolean empty = true;
		for (int q = 0; q < queueList.size(); q++) {
			if (!queueList.get(q).isEmpty()) {
				empty = false;
			}
		}
		return empty;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Iterator iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public Object[] toArray(Object[] objects) {
		return new Object[0];
	}

	@Override
	public boolean add(Object o) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean addAll(Collection collection) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean retainAll(Collection collection) {
		return false;
	}

	@Override
	public boolean removeAll(Collection collection) {
		return false;
	}

	@Override
	public boolean containsAll(Collection collection) {
		return false;
	}

	@Override
	public Object remove() {
		return null;
	}

	@Override
	public Object poll() {
		this.reentrantLock.lock();
		try {
			Packet p;
			for (int q = 0; q < queueList.size(); q++) {
				p = (Packet) queueList.get(q).poll();
				if (p != null) {

					PriorityHeader header = (PriorityHeader) p;
					int rank = (int) header.getPriority();
					// System.out.println("SPPIFO: Dequeued packet with rank" + rank + ", from queue
					// " + q + ". Queue size: " + queueList.get(q).size());

					// Log rank of packet enqueued and queue selected if enabled
					if (SimulationLogger.hasRankMappingEnabled()) {
						SimulationLogger.logRankMapping(this.ownId, rank, q);
					}

					if (SimulationLogger.hasQueueBoundTrackingEnabled()) {
						for (int c = queueList.size() - 1; c >= 0; c--) {
							SimulationLogger.logQueueBound(this.ownId, c, queueList.get(c).size());
						}
					}

					// Check whether there is an inversion: a packet with smaller rank in queue than
					// the one polled
					if (SimulationLogger.hasInversionsTrackingEnabled()) {
						int rankSmallest = 1000;
						for (int i = 0; i <= queueList.size() - 1; i++) {
							Object[] currentQueue = queueList.get(i).toArray();
							if (currentQueue.length > 0) {
								Arrays.sort(currentQueue);
								FullExtTcpPacket currentMin = (FullExtTcpPacket) currentQueue[0];
								if ((int) currentMin.getPriority() < rankSmallest) {
									rankSmallest = (int) currentMin.getPriority();
								}
							}
						}

						if (rankSmallest < rank) {
							SimulationLogger.logInversionsPerRank(this.ownId, rank, 1);
						}
					}

					return p;
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		} finally {
			this.reentrantLock.unlock();
		}
	}

	@Override
	public Object element() {
		return null;
	}

	@Override
	public Object peek() {
		return null;
	}
}