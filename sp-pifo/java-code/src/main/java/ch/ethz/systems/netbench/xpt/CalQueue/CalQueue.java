package ch.ethz.systems.netbench.xpt.CalQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.tcpbase.PriorityHeader;

public class CalQueue implements Queue {

	private final ReentrantLock reentrantLock = new ReentrantLock();
	private int ownId;
	private ArrayList<ArrayBlockingQueue<Packet>> calQueue;
	private int calNum;
	private int round;

	public CalQueue(int calendarNum, long perQueueCapacity, NetworkDevice ownNetworkDevice, String stepSize) {
		this.ownId = ownNetworkDevice.getIdentifier();
		ArrayBlockingQueue<Packet> fifo;
		for (int i = 0; i < calendarNum; i++) {
			fifo = new ArrayBlockingQueue<Packet>((int) perQueueCapacity);
			calQueue.add(fifo);
		}
		this.calNum = calendarNum;
		this.round = 0;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean add(Object e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(Object o) {
		Packet pkt = (Packet) o;
		PriorityHeader header = (PriorityHeader) pkt;
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
						if (cost > 0) {
							for (int w = queueList.size() - 1; w > q; w--) {
								currentQueueBound = (int) queueBounds.get(w);

								// Update queue bounds
								if (this.stepSize.equals("cost")) {
									queueBounds.put(w, currentQueueBound - cost);
								} else if (this.stepSize.equals("1")) {
									queueBounds.put(w, currentQueueBound - 1);
								} else if (this.stepSize.equals("rank")) {
									queueBounds.put(w, currentQueueBound - rank);
								} else if (this.stepSize.equals("queueBound")) {
									queueBounds.put(w, queueBounds.get(w - 1));
								} else {
									System.out.println("ERROR: SP-PIFO step size not supported.");
								}
							}
						}
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
	public Object remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object peek() {
		// TODO Auto-generated method stub
		return null;
	}

}