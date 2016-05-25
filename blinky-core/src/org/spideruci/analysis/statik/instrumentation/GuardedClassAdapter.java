package org.spideruci.analysis.statik.instrumentation;

import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;
import static org.spideruci.analysis.dynamic.Profiler.log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spideruci.analysis.trace.EventBuilder;
import org.spideruci.analysis.trace.TraceEvent;

public class GuardedClassAdapter extends ClassVisitor {
  private String className;

  public GuardedClassAdapter(ClassVisitor cv, String className) {
    super(Opcodes.ASM5, cv);
    this.className = className;
  }
  
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, 
      String signature, String[] exceptions) {
    MethodVisitor mv;
    
    mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    if (mv != null 
        && ((access & Opcodes.ACC_NATIVE) == 0)) {

      TraceEvent methodDecl = EventBuilder.buildMethodDecl(className, access, name+desc);
      mv = new GuardedMethodAdapter(methodDecl, access, name, desc, mv);
      if (log) {
        REAL_OUT.println(methodDecl.getLog());
      }
    }
    return mv;
  }
}