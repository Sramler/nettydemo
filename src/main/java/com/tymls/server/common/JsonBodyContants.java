package com.tymls.server.common;
/**
 * 对json中常用节点使用常量值管理
 *
 * @author luke
 */
public class JsonBodyContants {
  // 以下是json请求和响应时的关键key值
  public static final String REQUEST_NODE = "request";

  public static final String RESPONSE_NODE = "response";

  public static final String CONTENT_NODE = "content";

  public static final String PAGEINDEX_NODE = "pageIndex";

  public static final String PAGESIZE_NODE = "pageSize";

  public static final String TOKEN_NODE = "token";

  public static final String TOTAL_NODE = "total";

  public static final String FILE_NODE = "files";

  public static final String CODE_NODE = "code";

  public static final String ERRMSG_NODE = "errmsg";

  public static final String STATUS_NODE = "status";

  public static final String MESSAGE_NODE = "message";

  public static final String MSG_NODE = "msg";

  public static final String SUCCESS_NODE = "success";
  // 以下是request节点中常用的节点名称
  public static final String TYPE_NODE = "type";

  public static final String FROMSYSTEM_NODE = "fromsystem";

  public static final String TRANSACTIONID_NODE = "transactionid";

  public static final String SIGN_NODE = "sign";

  public static final String OBJECTID_NODE = "objectid";

  public static final String EQUIPMENT_ID = "equipmentId";
  /** app当前版本号 */
  public static final String VERSION_NODE = "version";
  /** 排序节点名称,排序节点内容中：字段名称：field{与字段名称匹配}, 排序方式：value{ASC,DESC} */
  public static final String RECORD_ORDER_NODE = "order";
  /** 升序字段key */
  public static final String ORDER_ = "order";
  /** 升序降序 */
  public static final String ORDER_DESC_ = "orderDesc_";
  /** -----以下是content中的对象引导节点，所有节点都从这儿取--------------------------------------------- */
  public static final String CAR_NODE = "car";

  public static final String CARS_NODE = "cars";

  public static final String STATION_NODE = "station";
  public static final String STATIONS_NODE = "stations";

  public static final String CARTYPE_NODE = "cartype";
  public static final String CARTYPES_NODE = "cartypes";

  public static final String REQUESTTYPES_NODE = "requesttypes";

  public static final String DATA_NODE = "data";
  public static final String DATAS_NODE = "datas";

  public static final String ORDER_NODE = "order";
  public static final String ORDERS_NODE = "orders";

  public static final String PILEBOX_NODE = "pileBox";

  public static final String TERMINAL_NODE = "terminal";
  public static final String TERMINALS_NODE = "terminals";

  public static final String CHARGE_NODE = "charge";
  public static final String CHARGES_NODE = "charges";

  public static final String MEMBER_NODE = "member";

  public static final String TRADEINFO_NODE = "trade";

  public static final String RECHARGE_NODE = "recharge";

  public static final String ESRECHARGE_NODE = "esRecharge";

  public static final String ESPREPAY_NODE = "esPrepay";
  /** 一些统计信息节点，可以用 */
  public static final String COUNTINFO_NODE = "countInfo";

  public static final String MANUAL_NODE = "manual";
  public static final String MANUALS_NODE = "manuals";

  public static final String PUBLICMSG_NODE = "publicMsg";
  public static final String PUBLICMSGS_NODE = "publicMsgs";

  public static final String COUPONSREC_NODE = "couponRec";
  public static final String COUPONSRECS_NODE = "couponRecs";

  public static final String PRICESETTINGS_NODE = "priceSettings";

  public static final String ORDERFEE_NODE = "orderFee";
  public static final String ORDERFEES_NODE = "orderFees";

  public static final String ORDER_RENEW = "orderrenew";

  public static final String ORDERAMT_NODE = "orderAmt";

  public static final String INVOICE_NODE = "invoice";
  public static final String INVOICES_NODE = "invoices";

  public static final String CARTBOXLOG_NODE = "carTboxLog";

  public static final String ORDERCOMMENT_NODE = "orderComment";

  public static final String ORDERPROCESS_NODE = "orderProcess";
  public static final String ORDERPROCESSES_NODE = "orderProcesses";

  public static final String MMPAYTRANLIST_NODE = "mmPayTranList";

  public static final String SERVER_NODE = "server";
  public static final String SERVERS_NODE = "servers";

  public static final String SYSSETTINGS_NODE = "syssettings";

  public static final String STATIONCOUNT_NODE = "stationCount";

  public static final String ORDERPAY_NODE = "orderPay";
  public static final String ORDERPAYS_NODE = "orderPays";

  public static final String ORDERENFUND_NODE = "orderRefund";

  public static final String APPLY_INFO = "applyInfo";

  public static final String AUDIT_INFO = "auditInfo";

  public static final String ORGAN_INFO = "organInfo";
  /** 站点可用充电终端 */
  public static final String STATISTICAL_NODE = "statistical";

  /** json模板架子 */
  public static final String JSON_RESPONSE_TEMPLATE =
      "{\""
          + CODE_NODE
          + "\":\"_CODE_\",\""
          + CONTENT_NODE
          + "\":_CONTENT_,\""
          + RESPONSE_NODE
          + "\": _RESPONSE_}";

  /** 如果只是响应操作成功，不需要返回其他数据，则用这个json返回 */
  public static final String JSON_SUCEESS_RESPONSE = "{\"" + CODE_NODE + "\":\"0\"}";

  /** 异常编码的统一格式 */
  public static final String JSON_ERROR_TEMPLATE =
      "{\"" + CODE_NODE + "\":\"_CODE_\",\"" + ERRMSG_NODE + "\":\"_ERRMSG_\"}";
  /** 其他费用总额 */
  public static final String PHONECODE_NODE = "phoneCode";

  public static final String ACCESSTOKEN_NODE = "accessToken";

  public static final String SEND_COUPON = "send_coupon";

  public static final String AUDIT_PASS = "audit_pass";

  public static final String GIVE_COUPONS = "give_coupons";

  public static final String GET_COUPONS = "get_coupons";

  /** 给充电桩响应的编码节点 */
  public static final String RETCODE_NODE = "ret";
  /** 剩余金额 */
  public static final String CASH_BALANCE = "cashBalance";
  /** 锁定金额 */
  public static final String CASH_LOCK = "cashLock";
  /** 可用金额 */
  public static final String CASH_AVAIL = "cashAvail";
  /** id2name时的key值 */
  public static final String FIELD_NAMES = "fieldNames";
}
