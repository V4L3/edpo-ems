package ch.unisg.ems.producerproduction;
import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.opencsv.CSVReader;
import org.json.simple.JSONObject;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.Date;

public class ProducerProduction {

    private final static String TOPIC_NAME = "pv_production";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private final static String LOAD_PROFILE_DIRECTORY = "loadprofiles";
    // dataset path, csv file has to be in resources directory
    private final static String DEFAULT_CSV_DATASET = "2022_Production.csv";

    private final static String delimiter = ";";

    public static void main(String[] args) throws Exception {

        String dataset;
        // if dataset is provided as run argument use this dataset, otherwise use default
        if (args.length > 0) {
            dataset = args[0];
        } else {
            dataset = DEFAULT_CSV_DATASET;
        }

        System.out.println("using dataset at path: " + LOAD_PROFILE_DIRECTORY + "/" + dataset);
        // read csv dataset to arraylist
        List<List<String>> records = new ArrayList<List<String>>();

        String filePath = Resources.getResource(LOAD_PROFILE_DIRECTORY + "/" + dataset).getFile();

        // replace file path to work with docker container
        filePath = filePath.replace("file:/app.jar!/", "/app/");

        System.out.println("resources path: " + filePath);
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        }



        // Read Kafka properties file and create Kafka producer with the given properties
        KafkaProducer<byte[], String> producer;
        try (InputStream props = Resources.getResource("application.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(props);
            // Create Kafka producer
            producer = new KafkaProducer<byte[], String>(properties);
        }

        // first line in csv are headers, save to array and drop line
        String[] headers = records.get(0).get(0).split((delimiter));
        records.remove(0);

        byte[] key = null;

        // generate production data, based on csv dataset
        records.forEach((r) -> {
            String[] values = r.get(0).split(delimiter);
            JSONObject jsonObj = new JSONObject();
            for (int i = 0; i < values.length; i++) {
                if (Objects.equals(headers[i], "timestamp")) {
                    jsonObj.put(headers[i], System.currentTimeMillis());
                } else {
                    jsonObj.put(headers[i], values[i]);
                }
            }

            String message = jsonObj.toJSONString(); // gaze.toJSONString();
            System.out.println("Producing - " + message);
            ProducerRecord<byte[], String> record = new ProducerRecord<>(TOPIC_NAME, key, message);
            producer.send(record);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}