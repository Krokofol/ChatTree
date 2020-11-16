package treeChat.node;

public class NodeInfo {

    private String name;
    private Integer percentLoss;
    private Integer port;

    public NodeInfo(String[] args) {
        name = args[0];
        percentLoss = Integer.parseInt(args[1]);
        port = Integer.parseInt(args[2]);
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }
    public Integer getPercentLoss() {
        return percentLoss;
    }
    public Integer getPort() { return port; }
}
