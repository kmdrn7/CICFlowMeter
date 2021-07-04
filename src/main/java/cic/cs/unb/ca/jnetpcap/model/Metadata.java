package cic.cs.unb.ca.jnetpcap.model;

public class Metadata {

    private String hostname;
    private String ip_address;
    private String serial;

    public String getHostname() {
        return hostname;
    }

    public String getIp_address() {
        return ip_address;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setIP_address(String ip) {
        this.ip_address = ip;
    }
}

