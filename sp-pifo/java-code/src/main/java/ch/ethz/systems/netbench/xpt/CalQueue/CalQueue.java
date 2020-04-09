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
	private ArrayList<Packet> pktQueue;
	private int calNum;
	private int round;
	private int pktsNum;

	public CalQueue() {
		pktsNum = 0;
		pktQueue = new ArrayList<Packet>(1000);
	}

	public CalQueue(int calendarNum, long perQueueCapacity, NetworkDevice ownNetworkDevice, String stepSize) {
		this.ownId = ownNetworkDevice.getIdentifier();
		ArrayBlockingQueue<Packet> fifo;
		for (int i = 0; i < calendarNum; i++) {
			fifo = new ArrayBlockingQueue<Packet>((int) perQueueCapacity);
			calQueue.add(fifo);
		}
		this.calNum = calendarNum;
		this.round = 0;
		this.pktsNum = 0;
	}

	@Override
	public int size() {
		return pktsNum;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return (pktsNum == 0);
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
			if (this.pktsNum <= 500) {
				this.pktQueue.add(pkt);
				pktsNum += 1;
				returnValue = true;
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
		Packet pkt = this.pktQueue.get(0);
		this.pktQueue.remove(0);
		pktsNum -= 1;
		return pkt;
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