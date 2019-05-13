package com.tymls.server;

import com.tymls.server.model.HandlerMethod;
import com.tymls.server.model.HttpResponse;
import com.tymls.server.vo.AppConstant;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class AbstractHttpHandler implements HttpHandler {

  /** 处理器对外提供的所有公共方法 */
  protected Map<String, MethodDefine> methodMap = null;

  protected static class MethodDefine {
    public Method method;
    public HandlerMethod annotion;

    public MethodDefine(Method method) {
      this.method = method;
      this.annotion = method.getAnnotation(HandlerMethod.class);
    }

    public String geturi() {
      if (annotion != null && StringUtils.isNotBlank(annotion.uri())) return annotion.uri();
      else return this.method.getName();
    }
  }

  @Override
  public void doHandle(HttpHandlerContext ctx) throws Exception {
    MethodDefine methodDefine = this.getMethod(ctx);
    HttpHandlerContext ctx1 = ctx;
    boolean isException = false;
    // 检查session以及调用此方法的权限
    if (false) {
      // if (methodDefine.annotion != null) {
      // TODO session 和权限校验
      ctx.writeToChannel();
      return;
      //      if (!ctx.checkSession(methodDefine.annotion.func().getValue())) {
      //        ctx.writeToChannel();
      //        return;
      //      }
    }
    try {
      ctx1 = (HttpHandlerContext) methodDefine.method.invoke(this, new Object[] {ctx});
    } catch (IllegalAccessException | IllegalArgumentException e) {
      e.printStackTrace();
      ctx1.setResponse(
          HttpResponse.FatalError,
          String.format(
              "网址调用%s错误,访问%s.%s 出错 /n %s",
              ctx.getUri(),
              this.getClass().getName(),
              methodDefine.method.getName(),
              e.toString()));
    } catch (InvocationTargetException e) {
      Throwable innerException = e.getTargetException();
      if (innerException instanceof ISRuntimeException) {
        ISRuntimeException runtimeException = (ISRuntimeException) innerException;
        ctx1.setResponse(runtimeException.getErrorCode().getValue(), runtimeException.getMessage());
      } else {
        isException = true;
        String errorMsg = (innerException).getMessage();
        if (AppConstant.IS_RUNTIME) {
          // TODO 错误日志
          // log.error(errorMsg, innerException);
        } else {
          innerException.printStackTrace();
        }
        if (errorMsg != null) {
          ctx1.setError("访问: " + ctx.getUri() + "\n " + errorMsg);
        } else {
          ctx1.setFatalError(
              "网址: "
                  + ctx.getUri()
                  + "\n 代码: "
                  + innerException.getStackTrace()[0].toString()
                  + "\n 错误: "
                  + innerException.toString());
        }
      }
    } finally {
      if (ctx1 != null) { // 记录系统日志
        // TODO 记录日志
        // ctx1.addSysLog(isException);
      }
      if (ctx1 != null && ctx.getResponseCount() == 0) {
        ctx1.writeToChannel();
      }
    }
  }

  protected MethodDefine getMethod(HttpHandlerContext ctx) {
    if (methodMap == null) buildMap();
    String methodName = ctx.getMethodName();
    MethodDefine methodDefine = this.methodMap.get(methodName);
    if (methodDefine == null) {
      throw new ISRuntimeException(
          "网址%s错误，类 %s 中不存在方法 %s ", ctx.getUri(), this.getClass().getName(), methodName);
    }
    return methodDefine;
  }

  protected void buildMap() {
    methodMap = new HashMap<String, MethodDefine>();
    Method[] methods = this.getClass().getMethods();
    for (Method method : methods) {
      // 必须是公共方法
      if (!Modifier.isPublic(method.getModifiers())) continue;
      Class<?>[] params = method.getParameterTypes();
      // 方法的参数只能是SHttpHandlerContext
      if (params.length != 1 || params[0] != HttpHandlerContext.class) continue;
      MethodDefine handlermethod = new MethodDefine(method);
      methodMap.put(handlermethod.geturi(), handlermethod);
    }
  }
}
