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
    private static Connection conn = ConnectionFactory.getCollection(server);
    private static Remote remote = new Remote(conn, local_sign);

    public void changeServer(String server) {
        if (conn != null) {
            this.conn.close();
            this.server = server;
            remote = new Remote(conn, local_sign);
        }
    }


    public Remote getRemote() {
        return remote;
    }
}
