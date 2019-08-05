package com.doughnut.wallet;

import com.android.jtblk.client.Remote;
import com.android.jtblk.connection.Connection;
import com.android.jtblk.connection.ConnectionFactory;

public class JtServer {

    // 生产环境
    // static String server = "wss://s.jingtum.com:5020";
    // 测试环境
    private static String server = "ws://ts5.jingtum.com:5020";
    // 是否使用本地签名方式提交交易
    private static Boolean local_sign = true;
    private static Connection conn;
    private static Remote remote;
    private static JtServer instance;

    private JtServer() {
        if (conn != null) {
            this.conn.close();
        }
        conn = ConnectionFactory.getCollection(server);
        remote = new Remote(conn, local_sign);
    }

    public static JtServer getInstance() {
        if (instance == null) {
            instance = new JtServer();
        }
        return instance;
    }

    /**
     * 切换服务节点
     *
     * @param server
     * @param local_sign
     */
    public void changeServer(String server, boolean local_sign) {
        this.instance = null;
        this.server = server;
        this.local_sign = local_sign;
    }

    /**
     * 切换服务节点
     *
     * @param server
     */
    public void changeServer(String server) {
        this.instance = null;
        this.server = server;
    }

    public Remote getRemote() {
        return remote;
    }

}
