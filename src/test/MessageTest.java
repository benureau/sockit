package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

import sockit.Client;
import sockit.InboundMessage;
import sockit.OutboundMessage;
import sockit.Server;


public class MessageTest {

	private int count = 10000;
	private Random gen = new Random();
	private boolean useSocket = false;

	public void testHeader(){
		int n = (int) (Math.random() * count);
		int size = (n * 5) + OutboundMessage.HEADER_SIZE;
		OutboundMessage om = new OutboundMessage();
		int gentype = gen.nextInt();
		om.setType(gentype);
		try {
			for(int i = 0 ; i < n ; i++)
				om.appendInt(i);
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			assertTrue("Error size", im.getLength() == size);
			assertTrue("Error type", im.getType() == gentype);
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO exception");
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testBoolean() {
		OutboundMessage om = new OutboundMessage();
		ArrayList<Boolean> b = new ArrayList<Boolean>();
		for(int i = 0 ; i < count ; i ++){
			b.add(gen.nextBoolean());
		}
		try {

			for(int i = 0 ; i < count ; i ++)
				om.appendBoolean(b.get(i));
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			for(int i = 0 ; i < count ; i ++)
				assertTrue("Read fail", im.readBoolean() == b.get(i).booleanValue());

		} catch (IOException e) {
			e.printStackTrace();
			fail("IO exception");
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testAppendString() {
		OutboundMessage om = new OutboundMessage();
		ArrayList<String> b = new ArrayList<String>();
		for(int i = 0 ; i < count ; i ++){
			b.add(UUID.randomUUID().toString());
		}
		try {

			for(int i = 0 ; i < count ; i ++)
				om.appendString(b.get(i));
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			for(int i = 0 ; i < count ; i ++)
				assertTrue("Read fail", im.readString().compareTo(b.get(i)) == 0);

		} catch (IOException e) {
			fail("IO exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testDouble() {
		OutboundMessage om = new OutboundMessage();
		ArrayList<Double> b = new ArrayList<Double>();
		for(int i = 0 ; i < count ; i ++){
			b.add(gen.nextDouble());
		}
		try {

			for(int i = 0 ; i < count ; i ++)
				om.appendDouble(b.get(i));
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			for(int i = 0 ; i < count ; i ++)
				assertTrue("Read fail", im.readDouble() == b.get(i).doubleValue());

		} catch (IOException e) {
			fail("IO exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testFloat() {
		OutboundMessage om = new OutboundMessage();
		ArrayList<Float> b = new ArrayList<Float>();
		for(int i = 0 ; i < count ; i ++){
			b.add(gen.nextFloat());
		}
		try {

			for(int i = 0 ; i < count ; i ++)
				om.appendFloat(b.get(i));
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			for(int i = 0 ; i < count ; i ++)
				assertTrue("Read fail", im.readFloat() == b.get(i).floatValue());

		} catch (IOException e) {
			fail("IO exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testInt() {
		OutboundMessage om = new OutboundMessage();
		ArrayList<Integer> b = new ArrayList<Integer>();
		for(int i = 0 ; i < count ; i ++){
			b.add(gen.nextInt());
		}
		try {

			for(int i = 0 ; i < count ; i ++)
				om.appendInt(b.get(i));
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			for(int i = 0 ; i < count ; i ++)
				assertTrue("Read fail", im.readInt() == b.get(i).intValue());

		} catch (IOException e) {
			fail("IO exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testLong() {
		OutboundMessage om = new OutboundMessage();
		ArrayList<Long> b = new ArrayList<Long>();
		for(int i = 0 ; i < count ; i ++){
			b.add(gen.nextLong());
		}
		try {

			for(int i = 0 ; i < count ; i ++)
				om.appendLong(b.get(i));
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			for(int i = 0 ; i < count ; i ++)
				assertTrue("Read fail", im.readLong() == b.get(i).longValue());

		} catch (IOException e) {
			fail("IO exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testList() {
		OutboundMessage om = new OutboundMessage();
		ArrayList<Long> b = new ArrayList<Long>();
		for(int i = 0 ; i < count ; i ++){
			b.add(gen.nextLong());
		}
		try {
			om.appendList(b);
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			ArrayList<Object> c = (ArrayList<Object>) im.readArrayList();
			assertTrue("Read fail", b.size() == c.size());
			int i = 0;
			for (Object object : c) {
				assertTrue("Read fail", ((Long) object).longValue() == b.get(i++).longValue());
			}
		} catch (IOException e) {
			fail("IO exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	@Test
	public void testObjectIO() {
		int t = gen.nextInt();
		OutboundMessage om = new OutboundMessage();
		om.setType(t);
		Double d = new Double(Math.PI);
		Integer i = new Integer(count);
		try {
			om.appendElement(d);
			om.appendElement(i);
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO exception");
		}
		InboundMessage im = new InboundMessage(om.getBytes());
		try {
			Object o1 = im.readElement();
			Object o2 = im.readElement();
			int type = im.getType();
			assertTrue("Error type", type == t);
			assertTrue("Error double", ((Double) o1).doubleValue() == Math.PI);
			assertTrue("Error integer", ((Integer) o2).intValue() == count);
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}
	
	@Test
	public void testMap() {
		OutboundMessage om = new OutboundMessage();
		HashMap<String, Object> b = new HashMap<String, Object>();
		for(int i = 0 ; i < count ; i ++){
			b.put(UUID.randomUUID().toString(), (Object) gen.nextDouble());
		}
		try {
			om.appendMap(b);
			InboundMessage im;
			if(!useSocket)
				im = new InboundMessage(om.getBytes());
			else
				im = testClientServer(om);
			HashMap<String, Object> c = (HashMap<String, Object>) im.readMap();
			assertTrue("Read fail", b.size() == c.size());
			int i = 0;
			for (Entry<String, Object> entry : c.entrySet()) {
				//
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO exception");
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("IO exception");
		}
	}

	private InboundMessage testClientServer(OutboundMessage om) throws InterruptedException{
		int port = 1025 + (int) (Math.random() * (54000 - 1025));
		Server s = new Server();
		s.start(port);
		Client c = new Client();
		Thread.sleep(500);
		while(!c.connect("127.0.0.1", port))
			Thread.sleep(500);
		c.send(om);
		boolean watch = true;
		int cpt = 0;
		while(watch == true && cpt < 10){
			if(s.getNumberOfMessages() > 0)
				watch = false;
			else{
				Thread.sleep(500);
				cpt ++;
				if(cpt == 10)
					fail("Fail in client/Server");
			}
		}
		InboundMessage im = s.receive();
		c.connect("127.0.0.1", port);
		c.disconnect();
		s.stop();
		return im;
	}
	
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main(
	         "test.MessageTest");            
	}
}
