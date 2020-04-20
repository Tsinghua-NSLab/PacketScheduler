package ch.ethz.systems.netbench.xpt.CalQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.systems.netbench.core.log.SimulationLogger;
import ch.ethz.systems.netbench.core.network.NetworkDevice;
import ch.ethz.systems.netbench.core.network.Packet;
import ch.ethz.systems.netbench.xpt.tcpbase.FullExtTcpPacket;

public class CalQueue implements Queue<Packet> {

	private final ReentrantLock reentrantLock = new ReentrantLock();
	private int ownId;
	private ArrayList<Queue<Packet>> calQ;
	private Queue<Packet> lastQ;
	private int pktsNum;
	private int round;
	private int SlotNum;
	private int SlotSize;

	public CalQueue(NetworkDevice ownNetworkDevice) {
		ownId = ownNetworkDevice.getIdentifier();
		pktsNum = 0;
		SlotNum = 1000;
		SlotSize = 10;
		calQ = new ArrayList<Queue<Packet>>(SlotNum);
		for (int i = 0; i < SlotNum; i++) {
			Queue<Packet> pkts = new LinkedList<Packet>();
			calQ.add(pkts);
		}
		lastQ = new LinkedList<Packet>();
		pktsNum = 0;
		round = 0;
	}

	@Override
	public int size() {
		return pktsNum;
	}

	@Override
	public boolean isEmpty() {
		return (pktsNum == 0);
	}

	@Override
	public boolean offer(Packet pkt) {
		FullExtTcpPacket header = (FullExtTcpPacket) pkt;
		int rank = (int) header.getPriority();
		SimulationLogger.logInfo("Queue", "" + rank + "  " + ownId);
		rank /= 1000;
		this.reentrantLock.lock();
		boolean returnValue = false;
		try {
			if (rank < SlotNum) {
				if (calQ.get(rank).size() < SlotSize) {
					calQ.get(rank).add(pkt);
					pktsNum++;
					returnValue = true;
					// SimulationLogger.logInfo("Queue", this.ownId + " adds a pkt at " + rank);
				} else {
					// SimulationLogger.logInfo("Queue", this.ownId + " drops a pkt for not enough
					// space at " + rank);
				}
			} else {
				pktsNum++;
				lastQ.add(pkt);
				returnValue = true;
				// SimulationLogger.logInfo("Queue", this.ownId + " drops a pkt for not enough
				// slot with rank:" + rank);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.reentrantLock.unlock();
		}
		return returnValue;
	}

	@Override
	public boolean add(Packet e) {
		// TODO Auto-generated method stub
		return this.offer(e);
	}

	@Override
	public Packet remove() {
		// TODO Auto-generated method stub
		return this.poll();
	}

	@Override
	public Packet poll() {
		if (pktsNum == 0) {
			return null;
		}
		for (Queue<Packet> pktList : calQ) {
			if (pktList.size() == 0) {
				continue;
			}
			Packet pkt = pktList.poll();
			pktsNum -= 1;
			return pkt;
		}
		if (lastQ.size() != 0) {
			pktsNum -= 1;
			return lastQ.poll();
		}
		return null;
	}

	@Override
	public Packet element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Packet peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Packet> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Packet> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}