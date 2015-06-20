package ftp.service.nodes;

import org.springframework.data.domain.Persistable;

import java.util.Objects;

public class FtpServerNode implements Persistable<Long> {

    private Long id;
    private final int port;
    private final String ipAddress;

    @Override
    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public FtpServerNode(int port, String ip) {
        this.port = port;
        this.ipAddress = ip;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FtpServerNode(Long id, int port, String ipAddress) {
        this.id = id;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FtpServerNode{");
        sb.append("id=").append(id);
        sb.append(", port=").append(port);
        sb.append(", ipAddress='").append(ipAddress).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        FtpServerNode that = FtpServerNode.class.cast(o);
        return Objects.equals(id, that.id) &&
                Objects.equals(port, that.port) &&
                Objects.equals(ipAddress, that.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, port, ipAddress);
    }

    public int getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

}
