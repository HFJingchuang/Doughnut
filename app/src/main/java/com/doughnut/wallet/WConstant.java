package com.doughnut.wallet;

/**
 * 钱包相关常量
 */
public class WConstant {

    // SWT
    final static String CURRENCY_SWT = "SWT";

    // 关系类型：信任
    final static String RELATION_TRUST = "trust";

    // 关系类型：冻结
    final static String RELATION_FREEZE = "freeze";

    // 交易返回值：成功
    final static String RESULT_OK = "0";

    // 钱包基础冻结SWT
    final static Integer RESERVED = 20;

    // 钱包操作冻结SWT（挂单，新增TOKEN）
    final static Integer FREEZED = 5;

}
