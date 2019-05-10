package com.tymls.server;

// EVMRI-GEN 自动生成 id = 6044888

/**
 * 用户相应值
 *
 * @author admin
 */
public enum ResponseCode {

  /** */
  Hint(-5, "提示通知用户知晓"),
  /** */
  NeedLogin(-4, "需要重新登录"),
  /** 运行状态不应该出现，错误信息提示用户 */
  Bug(-3, "内部错误"),
  /** */
  NeedConfirm(-2, "提示用户确认"),
  /** 一般错误，错误信息提示给用户即可 */
  Error(-1, "错误"),
  /** */
  OK(0, "成功"),
  /** */
  Success(9, "最后执行成功"),
  /** */
  User1(11, "自定义级别1"),
  /** */
  User2(12, "自定义级别2"),
  /** */
  User3(13, "自定义级别3"),
  /** */
  User4(14, "自定义级别4");

  protected int intValue;
  protected String textValue;

  public int getValue() {
    return intValue;
  }

  public String getText() {
    return textValue;
  }

  private ResponseCode(int value, String name) {
    this.intValue = value;
    this.textValue = name;
    EnumHelper.register(ResponseCode.class, this, value, name);
  }

  public static ResponseCode forValue(String value) {
    return (ResponseCode) EnumHelper.forValue(ResponseCode.class, value);
  }

  public static ResponseCode forValue(int value) {
    return (ResponseCode) EnumHelper.forValue(ResponseCode.class, value);
  }

  public static ResponseCode forValue(Integer value) {
    return (ResponseCode) EnumHelper.forValue(ResponseCode.class, value);
  }

  public static String forText(int value) {
    return EnumHelper.forText(ResponseCode.class, value);
  }

  public static String forText(Integer value) {
    return EnumHelper.forText(ResponseCode.class, value);
  }

  public static String forText(String value) {
    return EnumHelper.forText(ResponseCode.class, value);
  }
  /**
   * 根据名称查询枚举类型
   *
   * @param enumName 名称
   * @return 枚举类型
   */
  public static ResponseCode nameToValue(String enumName) {
    return Enum.valueOf(ResponseCode.class, enumName);
  }
  /* Start User Code === */

  /* End User Code === */

}
