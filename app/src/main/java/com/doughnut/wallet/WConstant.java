package com.doughnut.wallet;

/**
 * 钱包相关常量
 */
public class WConstant {

    // SWT
    public final static String CURRENCY_SWT = "SWT";

    // 关系类型：信任
    public final static String RELATION_TRUST = "trust";

    // 关系类型：冻结
    public final static String RELATION_FREEZE = "freeze";

    // 交易返回值：成功
    public final static String RESULT_OK = "0";

    // 钱包基础冻结SWT
    public final static Integer RESERVED = 20;

    // 钱包操作冻结SWT（挂单，新增TOKEN）
    public final static Integer FREEZED = 5;

    // 节点服务地址保存文件后缀
    public final static String SP_SERVER = "_server";

}
