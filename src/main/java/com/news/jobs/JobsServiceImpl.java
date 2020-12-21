package com.news.jobs;

import com.news.jobs.grpc.server.AreaVisited;
import com.news.jobs.grpc.server.Coordinates;
import com.news.jobs.grpc.server.Criteria;
import com.news.jobs.grpc.server.Job;
import com.news.jobs.grpc.server.JobsServiceGrpc.JobsServiceImplBase;

import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.stub.StreamObserver;

 // this will expose HTTP 2.0 listener on 6565
@GrpcService
public class JobsServiceImpl extends JobsServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobsServiceImpl.class);   

  @Override
  public void getOpenJobs(Criteria request, StreamObserver<Job> responseObserver) {
    LOGGER.info("server received {}", request);
    Job job;
    for (int i = 0; i < request.getChunkSize(); i++) {
      job = Job.newBuilder().setDescription("Jobs - " + i).build();
      responseObserver.onNext(job);
      Integer interval = request.getInterval();

      if (interval > 1000) {
        try {
          Thread.sleep(interval);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    responseObserver.onCompleted();
    LOGGER.info("server responded {}");
  }

  @Override
  public void getTopJob(Criteria request, StreamObserver<Job> responseObserver) {
    Job job = Job.newBuilder().setDescription(request.getLocation() + " Jobs").build();
    responseObserver.onNext(job);
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<Coordinates> sendMouseMove(StreamObserver<AreaVisited> responseObserver) {

    StreamObserver<Coordinates> requestObserver = new StreamObserver<Coordinates>() {
      // when a client is streaming data, server maintains state between calls
      String result = ">";
      @Override
      public void onNext(Coordinates value) {
        LOGGER.info("Server {} received {}", this.hashCode(), value);
        result = result + " " + value.getLeft() + " " + value.getTop();
        //grab the data that client is streaming and store it 
      }

      @Override
      public void onError(Throwable t) {
        LOGGER.error("onError", t);
      }

      @Override
      public void onCompleted() {
        //now that the client has completed streaming, send a response to the client 
        LOGGER.info("Appears that the client is done streaming !");
        responseObserver.onNext(AreaVisited
          .newBuilder()
          .setMap(result)
          .build());
        responseObserver.onCompleted();
        LOGGER.info("Final response sent to the client !");
      }
    };
    return requestObserver;
  }

  @Override
  public StreamObserver<Coordinates> mouseStorm(StreamObserver<AreaVisited> responseObserver) {
    StreamObserver<Coordinates> requestObserver = new StreamObserver<Coordinates>(){

      @Override
      public void onNext(Coordinates value) {
        String result = ">";
        LOGGER.info(result + this.hashCode());
        result = result + "(" + value.getLeft() + " " + value.getTop() + ")";
        LOGGER.info(result);
        responseObserver.onNext(AreaVisited
          .newBuilder()
          .setMap(result)
          .build());
          responseObserver.onCompleted();
      }

      @Override
      public void onError(Throwable t) {
        LOGGER.error("onError", t);
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();        
        LOGGER.error("bye");
      }
      
    };
    return requestObserver;
    
  }
}
