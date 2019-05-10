package com.tymls.server.model;

public enum SysFuncID {

  /** 不需要 */
  NULL(0),

  /** 只需要登录就可以使用的功能 */
  NEED_LOGIN(1),

  /** 单设备登陆 */
  SSO(2);

  protected int intValue;

  public int getValue() {
    return intValue;
  }

  private SysFuncID(int value) {
    this.intValue = value;
  }
}
