package com.tymls.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ISRuntimeException extends RuntimeException {
  private static final long serialVersionUID = -1L;

  private ResponseCode errorCode;

  public ISRuntimeException(ResponseCode errorCode, String msg) {
    super(msg);
    this.errorCode = errorCode;
    if (errorCode.getValue() < ResponseCode.OK.getValue()) {
      log.info("AT:{}", Thread.currentThread().getStackTrace()[2]);
      log.info(msg);
    }
  }

  public ISRuntimeException(String msg) {
    this(ResponseCode.Error, msg);
  }

  public ISRuntimeException(String msg, Object... args) {
    this(ResponseCode.Error, String.format(msg, args));
  }

  public ISRuntimeException(ResponseCode errorCode, String msg, Object... args) {
    this(errorCode, String.format(msg, args));
  }
}
