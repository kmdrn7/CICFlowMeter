package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.jnetpcap.*;
import cic.cs.unb.ca.jnetpcap.model.BasicFeature;
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

import java.io.File;
import java.util.Properties;

public class Cmd {

    private static final String DividingLine = "-------------------------------------------------------------------------------";
    private static String[] animationChars = new String[]{"|", "/", "-", "\\"};

    private static String kafka_topic = "asdf";
    private static String kafka_host = "192.168.100.29";
    private static String kafka_port = "29092";

    public static void main(String[] args) {
        long flowTimeout = 120000000L;
        long activityTimeout = 5000000L;
        String pcapPath = "/media/kmdr7/Seagate/DATASETS/IOT-23/CTU-Honeypot-Capture-4-1/2018-10-25-14-06-32-192.168.1.132.pcap";
        File in = new File(pcapPath);
        readPcapFile(in.getPath(), flowTimeout, activityTimeout);
    }

    private static void readPcapFile(String inputFile, long flowTimeout, long activityTimeout) {
        String fileName = FilenameUtils.getName(inputFile);
        FlowGenerator flowGen = new FlowGenerator(true, flowTimeout, activityTimeout);
        flowGen.addFlowListener(new FlowListener());
        PacketReader packetReader = new PacketReader(inputFile);
        System.out.println(String.format("Working on... %s", fileName));
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
        System.out.println(String.format("Packet stats: Total=%d,Valid=%d,Discarded=%d", nTotal, nValid, nDiscarded));
        System.out.println(DividingLine);
    }

    static class FlowListener implements FlowGenListener {

        private Producer<String, BasicFeature> kafkaProducer;

        public FlowListener() {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Cmd.kafka_host + ":" + Cmd.kafka_port);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            kafkaProducer = new KafkaProducer<>(
                props,
                new StringSerializer(),
                new KafkaJsonSerializer()
            );
        }

        @Override
        public void onFlowGenerated(BasicFlow flow) {
            BasicFeature msg = flow.dumpFlowFeatures();
            kafkaProducer.send(new ProducerRecord<>(Cmd.kafka_topic, "0", msg));
        }
    }
}
