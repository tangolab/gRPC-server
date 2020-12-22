package com.news.jobs;

import javax.annotation.PostConstruct;

import com.news.jobs.grpc.server.AreaVisited;
import com.news.jobs.grpc.server.Coordinates;
import com.news.jobs.grpc.server.Criteria;
import com.news.jobs.grpc.server.Job;
import com.news.jobs.grpc.server.JobsServiceGrpc;
//import com.news.jobs.grpc.server.JobsServiceGrpc.JobsServiceBlockingStub;
import com.news.jobs.grpc.server.JobsServiceGrpc.JobsServiceStub;

/* import com.news.jobs.grpc.helloworld.Greeting;
import com.news.jobs.grpc.helloworld.HelloWorldServiceGrpc;
import com.news.jobs.grpc.helloworld.Person;
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

@Component
public class JobsClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobsClient.class);

  private JobsServiceGrpc.JobsServiceBlockingStub jobsServiceBlockingStub;

  @PostConstruct
  private void init() {
    LOGGER.info("Client initialized");
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8002).usePlaintext().build();
    jobsServiceBlockingStub = JobsServiceGrpc.newBlockingStub(managedChannel);
  }

  public String FindJob(String location){
    Criteria request = Criteria.newBuilder().setLocation(location).build();
    Job job = jobsServiceBlockingStub.getTopJob(request);
    return job.getDescription();
  } 
  
  public String ListAllOpenJobs(String location){
    Criteria request = Criteria
      .newBuilder()
      .setLocation(location)
      .setChunkSize(5)
      .setInterval(2000)
      .build();
    jobsServiceBlockingStub
      .getOpenJobs(request)
      .forEachRemaining(result -> {
          System.out.println(result.getDescription());
      });
    return location;
  }

  public String PerformClientStreaming(String location){
    LOGGER.info("Starting streaming test");
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8002).usePlaintext().build();

    JobsServiceStub asyncJobsrvc = JobsServiceGrpc.newStub(managedChannel);

    StreamObserver<Coordinates> requestObserver = asyncJobsrvc.sendMouseMove(new StreamObserver<AreaVisited>(){

      @Override
      public void onNext(AreaVisited value) {
        System.out.println(value.getMap());
      }

      @Override
      public void onError(Throwable t) {
        LOGGER.error("onError", t);
      }

      @Override
      public void onCompleted() {
        System.out.println("all done");
      }
      
    });

    requestObserver.onNext(Coordinates
                              .newBuilder()
                              .setLeft(1)
                              .setTop(2)
                              .build());

    requestObserver.onNext(Coordinates
                              .newBuilder()
                              .setLeft(2)
                              .setTop(4)
                              .build());
    requestObserver.onCompleted();

    return null;
  }
}