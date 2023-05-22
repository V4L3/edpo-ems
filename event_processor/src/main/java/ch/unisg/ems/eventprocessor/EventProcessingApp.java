package ch.unisg.ems.eventprocessor;

import ch.unisg.ems.eventprocessor.topology.EmsTopology;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.state.HostInfo;

import java.util.Properties;

class EventProcessingApp {
  public static void main(String[] args) {
    Topology topology = EmsTopology.build();

    // set the required properties for running Kafka Streams
    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");

    // build the topology and start streaming!
    KafkaStreams streams = new KafkaStreams(topology, config);

    streams.setUncaughtExceptionHandler(ex -> {
      System.out.println("Kafka-Streams uncaught exception occurred. Stream will be replaced with new thread:" + ex.toString());
      return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
    });

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    System.out.println("Starting Event streams");
    streams.start();

    // start the REST service
    HostInfo hostInfo = new HostInfo("localhost", 7071);
    MonitorService service = new MonitorService(hostInfo, streams);
    service.start();
  }
}
