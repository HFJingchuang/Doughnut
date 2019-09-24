package com.doughnut.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.jtblk.client.Remote;
import com.android.jtblk.connection.Connection;
import com.android.jtblk.connection.ConnectionFactory;
import com.doughnut.utils.FileUtil;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.NetUtil;

public class JtServer {

    private static final String STATUS = "OPEN";
    // 生产环境
    private static String server = "wss://s.jingtum.com:5020";
    // 测试环境
    //    private static String server = "ws://ts5.jingtum.com:5020";
    //节点链接状态
    // 是否使用本地签名方式提交交易
    private static Boolean local_sign = true;
    private static Connection conn;
    private static Remote remote;
    private static JtServer instance;
    private static Context mContext;
    static SharedPreferences sharedPreferences;

    private JtServer() {
    }

    public static JtServer getInstance(Context context) {
        if (!NetUtil.isNetworkAvailable(context)) {
            remote = null;
        } else if (remote == null || !TextUtils.equals(remote.getStatus(), STATUS)) {
            mContext = context;
            if (conn != null) {
                conn.close();
            }
            String fileName = mContext.getPackageName() + WConstant.SP_SERVER;
            sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            String nodeUrl = sharedPreferences.getString("nodeUrl", "");
            if (!TextUtils.isEmpty(nodeUrl)) {
                server = nodeUrl;
            }
            try {
                conn = ConnectionFactory.getCollection(server);
                remote = new Remote(conn, local_sign);
                if (!TextUtils.equals(remote.getStatus(), STATUS)) {
                    pollServer();
                }
            } catch (Exception e) {
                remote = null;
                e.printStackTrace();
            }
        }
        if (instance == null) {
            instance = new JtServer();
        }
        return instance;
    }

    /**
     * 节点轮询
     */
    private static synchronized void pollServer() {
        GsonUtil publicNodes = new GsonUtil(FileUtil.getConfigFile(mContext, "publicNode.json"));
        for (int i = 0; i < publicNodes.getLength(); i++) {
            conn.close();
            GsonUtil item = publicNodes.getObject(i);
            String node = item.getString("node", "");
            conn = ConnectionFactory.getCollection(node);
            remote = new Remote(conn, local_sign);
            if (TextUtils.equals(remote.getStatus(), STATUS)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nodeUrl", node);
                editor.apply();
                break;
            }
        }
    }

    /**
     * 切换服务节点
     *
     * @param server
     * @param local_sign
     */
    public void changeServer(String server, boolean local_sign) {
        try {
            conn.close();
            this.server = server;
            this.local_sign = local_sign;
            conn = ConnectionFactory.getCollection(server);
            remote = new Remote(conn, local_sign);
            if (TextUtils.equals(remote.getStatus(), STATUS)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nodeUrl", server);
                editor.apply();
            } else {
                pollServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换服务节点
     *
     * @param server
     */
    public void changeServer(String server) {
        try {
            conn.close();
            this.server = server;
            conn = ConnectionFactory.getCollection(server);
            remote = new Remote(conn, local_sign);
            if (TextUtils.equals(remote.getStatus(), STATUS)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nodeUrl", server);
                editor.apply();
            } else {
                pollServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Remote getRemote() {
        return remote;
    }

    public String getServer() {
        return server;
    }

}
