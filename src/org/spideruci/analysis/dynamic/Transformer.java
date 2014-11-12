package org.spideruci.analysis.dynamic;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.spideruci.analysis.statik.instrumentation.Deputy;
import org.spideruci.analysis.statik.instrumentation.SourceLineInstrumenter;
import org.spideruci.util.ByteCodePrinter;
import org.spideruci.util.Constants;

public class Transformer implements ClassFileTransformer {

  public Transformer() {
    super();
  }
  
  public static void premain(String agentArguments, 
      Instrumentation instrumentation) {
    boolean tempGuard = Profiler.$guard1$; 
    Profiler.$guard1$ = true;
    instrumentation.addTransformer(new Transformer());
    Profiler.$guard1$ = tempGuard;
  }
  
  @Override
  public byte[] transform(ClassLoader loader, String className,
      Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
      byte[] classBytes) throws IllegalClassFormatException {
    byte[] instrumentedBytes = null;
    boolean shouldNotInstrument = false;
    
    shouldNotInstrument = shouldNotInstrument(className);
    
    if(shouldNotInstrument) {
      System.out.println("instrumentation skipped for " + className);
      return classBytes;
    }
    
    try {
      SourceLineInstrumenter ins = new SourceLineInstrumenter();
      instrumentedBytes = ins.instrument(className, classBytes, null);
      System.out.println("instrumentation successful for " + className);
    } catch(Exception ex) {
      ByteCodePrinter.printToFile(className, classBytes, instrumentedBytes);
      ex.printStackTrace();
      System.out.println("instrumentation failed for " + className);
      instrumentedBytes = classBytes;
    }
    
    return instrumentedBytes;
  }
  
  private boolean shouldNotInstrument(String className) {
    boolean shouldNotInstrument = false;
    if(className.startsWith(Constants.SPIDER_NAMESPACE)
        || className.startsWith(Constants.SPIDER_NAMESPACE2)) {
      if(className.contains("test")) {
        return false; // shouldInstrument
      } else {
        return true; // shouldNotInstrument
      }
    }
    
    for(String item : Deputy.exclusionList) {
      if(className.startsWith(item)) {
        return true;
      }
    }
    
    return shouldNotInstrument;
  }
  
}
