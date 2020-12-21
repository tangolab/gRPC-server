package com.news.jobs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JobsApplicationTests {

	@Autowired
	private JobsClient jobsclient;
  
	 @Test
	public void testSayHello() {
	    assertThat(jobsclient.FindJob("NYC"))
		  .isEqualTo("NYC Jobs");  
	}

	@Test
	public void testServerStreaming(){
		jobsclient.ListAllOpenJobs("NYC");
		assertThat("1").isEqualTo("1");
	} 

	@Test
	public void testClientStreaming(){
		assertThat(jobsclient.PerformClientStreaming("location")).isNull();
	}

}
