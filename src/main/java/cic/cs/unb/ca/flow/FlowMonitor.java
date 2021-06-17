package cic.cs.unb.ca.flow;

import cic.cs.unb.ca.jnetpcap.BasicFlow;
import cic.cs.unb.ca.jnetpcap.PcapIfWrapper;
import cic.cs.unb.ca.jnetpcap.model.BasicFeature;
import cic.cs.unb.ca.jnetpcap.worker.TrafficFlowWorker;
import cic.cs.unb.ca.kafka.KafkaJsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author ryuka
 */
public class FlowMonitor {
    protected static final Logger logger = LoggerFactory.getLogger(FlowMonitor.class);

    private String topic;
    private String kafka_host;
    private String kafka_port;
    private String netint;
    private final String kafka_client_id = "CICFlowMeter-1";

    private List<PcapIfWrapper> listModel = new ArrayList<>();
    private TrafficFlowWorker mWorker;

    public FlowMonitor(String netint, String kafka_host, String kafka_port, String topic) {
        this.netint = netint;
        this.kafka_host = kafka_host;
        this.kafka_port = kafka_port;
        this.topic = topic;
    }

    public void startTrafficFlow() {
        if (listModel.contains(netint)) {
            logger.info("Using Interface: ", netint);
        }
        if (mWorker != null && !mWorker.isCancelled()) {
            return;
        }

        mWorker = new TrafficFlowWorker(this.netint);
        logger.info("Listen at Interface " + this.netint);
        mWorker.addPropertyChangeListener(event -> {
            TrafficFlowWorker task = (TrafficFlowWorker) event.getSource();
            if ("progress".equals(event.getPropertyName())) {
                logger.debug("Listening in " + event.getSource());
            } else if (TrafficFlowWorker.PROPERTY_FLOW.equalsIgnoreCase(event.getPropertyName())) {
                producer((BasicFlow) event.getNewValue());
            } else if ("state".equals(event.getPropertyName())) {
                switch (task.getState()) {
                    case STARTED:
                        break;
                    case DONE:
                        break;
                }
            }
        });
        mWorker.execute();
    }

    public void producer(BasicFlow flow) {
        BasicFeature msg = flow.dumpFlowFeatures();
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafka_host + ":" + this.kafka_port);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        Producer<String, BasicFeature> kafkaProducer =
            new KafkaProducer<>(
                props,
                new StringSerializer(),
                new KafkaJsonSerializer()
            );
        kafkaProducer.send(new ProducerRecord<>(this.topic, "0", msg));
    }
}
