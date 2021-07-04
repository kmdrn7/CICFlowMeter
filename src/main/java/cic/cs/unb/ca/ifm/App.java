package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.jnetpcap.*;
import cic.cs.unb.ca.jnetpcap.model.BasicFeature;
import cic.cs.unb.ca.jnetpcap.model.Metadata;
import cic.cs.unb.ca.jnetpcap.worker.FlowGenListener;
import cic.cs.unb.ca.kafka.KafkaJsonSerializer;
import org.apache.commons.io.FilenameUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jnetpcap.PcapClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Properties;

public class App {

	public static final Logger logger = LoggerFactory.getLogger(App.class);

	private static final String DividingLine = "-------------------------------------------------------------------------------";
	private static final String kafka_topic = System.getenv("KAFKA_TOPIC");
	private static final String kafka_host = System.getenv("KAFKA_HOST");
	private static final String kafka_port = System.getenv("KAFKA_PORT");
	private static final String device_serial = System.getenv("DEVICE_SERIAL");

	public static void main(String[] args) {
		try {
			if (System.getenv("SILENT_MODE").equals("0")){
				printSystemEnvironmentVariable();
			}
		} catch (Exception e) {}
		long flowTimeout = 120000000L;
		long activityTimeout = 5000000L;
		String pcapPath = args[0];
		File in = new File(pcapPath);
		readPcapFile(in.getPath(), flowTimeout, activityTimeout);
	}

	public static void printSystemEnvironmentVariable() {
		System.out.println("Running with configurations :::::::::::::");
		System.out.println("=========================================");
		System.out.println("Kafka Host : " + kafka_host);
		System.out.println("Kafka Port : " + kafka_port);
		System.out.println("Kafka Topic : " + kafka_topic);
		System.out.println("Device Serial : " + device_serial);
		System.out.println("=========================================");
		System.out.println("::::::::::::: ===========================");
	}

	private static void readPcapFile(String inputFile, long flowTimeout, long activityTimeout) {
		String fileName = FilenameUtils.getName(inputFile);
		FlowGenerator flowGen = new FlowGenerator(true, flowTimeout, activityTimeout);
		flowGen.addFlowListener(new App.FlowListener());
		PacketReader packetReader = new PacketReader(inputFile);
		System.out.printf("Working on... %s%n", fileName);
		int nValid = 0;
		int nTotal = 0;
		int nDiscarded = 0;
		while (true) {
			try {
				BasicPacketInfo basicPacket = packetReader.nextPacket();
				nTotal++;
				if (basicPacket != null) {
					flowGen.addPacket(basicPacket);
					nValid++;
				} else {
					nDiscarded++;
				}
			} catch (PcapClosedException e) {
				break;
			}
		}
		System.out.printf("Packet stats: Total=%d,Valid=%d,Discarded=%d%n", nTotal, nValid, nDiscarded);
		System.out.println(DividingLine);
	}

	static class FlowListener implements FlowGenListener {

		private final Producer<String, BasicFeature> producer;
		private Metadata meta;

		public FlowListener() {
			meta = new Metadata();
			meta.setSerial(System.getenv("DEVICE_SERIAL"));
			Properties props = new Properties();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, App.kafka_host + ":" + App.kafka_port);
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			producer = new KafkaProducer<>(
				props,
				new StringSerializer(),
				new KafkaJsonSerializer()
			);
		}

		@Override
		public void onFlowGenerated(BasicFlow flow) {
			BasicFeature message = flow.dumpFlowFeatures();
			message.setMetadata(meta);
			producer.send(new ProducerRecord<>(App.kafka_topic, "0", message));
		}
	}
}
