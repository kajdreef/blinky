package org.spideruci.analysis.statik.instrumentation;

public class Count {
  private static int CLASS_COUNT = 0;
  private static int METHOD_COUNT = 0;
  private static int LOG_COUNT = 0;
  
  public static int anotherClass() {
    return ++CLASS_COUNT;
  }
  
  public static int anotherMethod() {
    int count = ++METHOD_COUNT;
    if(OfflineInstrumenter.isActive) {
      count *= -1;
    }
    return count;
  }
  
  public static int anotherInsn() {
    int count = ++LOG_COUNT;
    if(OfflineInstrumenter.isActive) {
      count *= -1;
    }
    return count;
  }

}
