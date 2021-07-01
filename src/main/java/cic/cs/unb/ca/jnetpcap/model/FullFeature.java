package cic.cs.unb.ca.jnetpcap.model;

import cic.cs.unb.ca.jnetpcap.model.feature.*;

public class FullFeature {

    // BASIC FEATURE
    // ============================================
    private String flow_id;
    private String src_ip;
    private long src_port;
    private String dst_ip;
    private long dst_port;
    private long protocol;
    private String timestamp;
    private String label;

    // EXTRACTED FEATURE
    // ============================================
    private long flow_duration;

    // TotalPacketFeature
    private long forward;
    private double length_of_forward;
    private long backward;
    private double length_of_backward;

    // StatisticsPacket fwd_packet_length
    private double fwd_packet_length_max;
    private double fwd_packet_length_min;
    private double fwd_packet_length_mean;
    private double fwd_packet_length_std;
    private double bwd_packet_length_max;
    private double bwd_packet_length_min;
    private double bwd_packet_length_mean;
    private double bwd_packet_length_std;

    private double flow_bytes_per_second;
    private double flow_pkts_per_second;

    // StatisticsPacket flow_IAT
    private double flow_IAT_max;
    private double flow_IAT_min;
    private double flow_IAT_mean;
    private double flow_IAT_std;

    // StatisticsPacket fwd_IAT
    private double fwd_IAT_max;
    private double fwd_IAT_min;
    private double fwd_IAT_mean;
    private double fwd_IAT_std;
    private double fwd_IAT_total;

    // StatisticsPacket bwd_IAT
    private double bwd_IAT_max;
    private double bwd_IAT_min;
    private double bwd_IAT_mean;
    private double bwd_IAT_std;
    private double bwd_IAT_total;

    private double fwd_PSH_flags;
    private double bwd_PSH_flags;
    private double fwd_URG_flags;
    private double bwd_URG_flags;
    private double fwd_header_length;
    private double bwd_header_length;
    private double fwd_packets_per_second;
    private double bwd_packets_per_second;

    // StatisticsPacket packet_lenght
    private double packet_lenght_max;
    private double packet_lenght_min;
    private double packet_lenght_mean;
    private double packet_lenght_std;
    private double packet_length_variance;

    // FlagCountFeature flagCount;
    private int flagCount_fin;
    private int flagCount_syn;
    private int flagCount_rst;
    private int flagCount_psh;
    private int flagCount_ack;
    private int flagCount_ugr;
    private int flagCount_cwr;
    private int flagCount_ece;

    private double download_upload_ratio;
    private double average_packet_size;
    private double fwd_segment_size_avg;
    private double bwd_segment_size_avg;

    // AvgBulkFeature fwd_bulk;
    private long fwd_bulk_bytes_per_bulk;
    private long fwd_bulk_packet_per_bulk;
    private long fwd_bulk_bulk_rate;

    // AvgBulkFeature bwd_bulk;
    private long bwd_bulk_bytes_per_bulk;
    private long bwd_bulk_packet_per_bulk;
    private long bwd_bulk_bulk_rate;

    // SubFlowFeature fwd_subflow;
    private long fwd_subflow_packets;
    private long fwd_subflow_bytes;

    // SubFlowFeature bwd_subflow;
    private long bwd_subflow_packets;
    private long bwd_subflow_bytes;

    private long fwd_win_bytes;
    private long bwd_win_bytes;
    private long fwd_act_data_pkts;
    private long fwd_seg_size_min;

    // StatisticsPacket ActivePacket;
    private double ActivePacket_max;
    private double ActivePacket_min;
    private double ActivePacket_mean;
    private double ActivePacket_std;

    // StatisticsPacket IdlePacket;
    private double IdlePacket_max;
    private double IdlePacket_min;
    private double IdlePacket_mean;
    private double IdlePacket_std;

    public FullFeature(BasicFeature f) {
        this.flow_id = f.getFlow_id();
        this.src_ip = f.getSrc_ip();
        this.src_port = f.getSrc_port();
        this.dst_ip = f.getDst_ip();
        this.dst_port = f.getDst_port();
        this.protocol = f.getProtocol();
        this.timestamp = f.getTimestamp();
        this.label = f.getLabel();
        this.flow_duration = f.getExtractFeature().getFlow_duration();
        this.forward = f.getExtractFeature().getTotalPacketFeature().getForward();
        this.length_of_forward = f.getExtractFeature().getTotalPacketFeature().getLength_of_forward();
    }
}
