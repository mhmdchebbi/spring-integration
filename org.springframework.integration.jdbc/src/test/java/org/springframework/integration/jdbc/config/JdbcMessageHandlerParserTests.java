package org.springframework.integration.jdbc.config;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class JdbcMessageHandlerParserTests {

	private SimpleJdbcTemplate jdbcTemplate;
	
	private MessageChannel channel;
	
	private ConfigurableApplicationContext context;
	
	@Test
	public void testSimpleInboundChannelAdapter(){
		setUp("handlingWithJdbcOperationsJdbcOutboundChannelAdapterTest.xml", getClass());
		Message<?> message = MessageBuilder.withPayload("foo").setHeader("business.key", "FOO").build();
		channel.send(message);
		Map<String, Object> map = this.jdbcTemplate.queryForMap("SELECT * from FOOS");
		assertEquals("Wrong id", "FOO", map.get("ID"));
		assertEquals("Wrong id", "foo", map.get("name"));
	}

	@Test
	public void testDollarHeaderInboundChannelAdapter(){
		setUp("handlingDollarHeaderJdbcOutboundChannelAdapterTest.xml", getClass());
		Message<?> message = MessageBuilder.withPayload("foo").build();
		channel.send(message);
		Map<String, Object> map = this.jdbcTemplate.queryForMap("SELECT * from FOOS");
		assertEquals("Wrong id", message.getHeaders().getId().toString(), map.get("ID"));
		assertEquals("Wrong id", "foo", map.get("name"));
	}

	@Test
	public void testMapPayloadInboundChannelAdapter(){
		setUp("handlingMapPayloadJdbcOutboundChannelAdapterTest.xml", getClass());
		Message<?> message = MessageBuilder.withPayload(Collections.singletonMap("foo", "bar")).build();
		channel.send(message);
		Map<String, Object> map = this.jdbcTemplate.queryForMap("SELECT * from FOOS");
		assertEquals("Wrong id", message.getHeaders().getId().toString(), map.get("ID"));
		assertEquals("Wrong id", "bar", map.get("name"));
	}

	@After
	public void tearDown(){
		if(context != null){
			context.close();
		}
	}
	
	public void setUp(String name, Class<?> cls){
		context = new ClassPathXmlApplicationContext(name, cls);
		jdbcTemplate = new SimpleJdbcTemplate(this.context.getBean("dataSource",DataSource.class));
		channel = this.context.getBean("target", MessageChannel.class);
	}
	
}
