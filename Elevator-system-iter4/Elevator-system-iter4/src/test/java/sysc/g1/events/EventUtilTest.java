package sysc.g1.events;

import static org.junit.Assert.*;

import org.junit.Test;


public class EventUtilTest {
	
	EventUtil eventUtil = new EventUtil();
	Event firstEvent = eventUtil.parse("14:05:15.800 2 Up 4");
	Event secondEvent = eventUtil.parse("23:59:15.800 4 Up 5");
	Event thirdEvent = eventUtil.parse("00:00:00.000 3 DOWN 1");
	
	@Test
	public void testingTimeStamp() {
				
		assertEquals("14:05:15.800", firstEvent.getTimestamp().toString());
		assertEquals("23:59:15.800", secondEvent.getTimestamp().toString());
		assertEquals("00:00", thirdEvent.getTimestamp().toString());
	}
	
	@Test
	public void testingSource() {

		assertEquals("ACTOR", firstEvent.getSource().toString());
		assertEquals("ACTOR", secondEvent.getSource().toString());
		assertEquals("ACTOR", thirdEvent.getSource().toString());
		
	}
	
	@Test
	public void testingSourceId() {

		assertEquals("actor", firstEvent.getSourceId().toString());
		assertEquals("actor", secondEvent.getSourceId().toString());
		assertEquals("actor", thirdEvent.getSourceId().toString());
		
	}
	
	@Test
	public void testingEventType() {

		assertEquals(EventType.BUTTON_PRESS, firstEvent.getInfo().type);
		assertEquals(EventType.BUTTON_PRESS, secondEvent.getInfo().type);
		assertEquals(EventType.BUTTON_PRESS, thirdEvent.getInfo().type);
		
	}

}
