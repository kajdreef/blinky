package org.spideruci.analysis.trace;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.spideruci.util.MyAssert;

public class TraceReader {
  
  private final Iterator<TraceEvent> traceIterator;
  private final HashMap<Integer, TraceEvent> insnTable;
  private final HashMap<Integer, TraceEvent> methodDeclTable;
  private TraceEvent currentEvent = null;
  
  public TraceReader(final File file) {
    TraceScanner scanner = new TraceScanner(file);
    traceIterator = scanner.iterator();
    insnTable = new HashMap<>();
    methodDeclTable = new HashMap<>();
  }
  
  public TraceEvent readNextExecutedEvent() {
    TraceEvent event = null;
    while(traceIterator.hasNext()) {
      event = traceIterator.next();
      switch(event.getType()) {
      case $$$:
        currentEvent = event;
        return event;
      case $$class$$:
      case $$method$$:
        methodDeclTable.put(event.getId(), event);
        continue;
      case $athrow$:
      case $enter$:
      case $exit$:
      case $invoke$:
      case $line$:
      case $return$:
        insnTable.put(event.getId(), event);
        continue;
      default:
        throw new RuntimeException("Unhandled Event Type: " + event.getType());
      }
    }
    return null;
  }
  
  public boolean isExecutedEventMethodInvocation(TraceEvent execEvent) {
    return execEvent.getExecInsnType() == EventType.$$method$$;
  }
  
  public int getExecutedEventSourceLine(TraceEvent execEvent) {
    TraceEvent insnEvent = getInsnEvent(execEvent);
    int lineNumber = insnEvent.getInsnLine();
    return lineNumber;
  }
  
  public String getExecutedEventOwnerClass(TraceEvent execEvent) {
    TraceEvent declEvent = getDeclEvent(execEvent);
    return declEvent.getDeclOwner();
  }
  
  public String getExecutedEventOwnerMethod(TraceEvent execEvent) {
    TraceEvent declEvent = getDeclEvent(execEvent);
    return declEvent.getDeclName();
  }
  
    private TraceEvent getDeclEvent(TraceEvent execEvent) {
      TraceEvent insnEvent = getInsnEvent(execEvent);
      int declId = insnEvent.getInsnDeclHostId();
      TraceEvent declEvent = methodDeclTable.get(declId);
      return declEvent;
    }
    
    private TraceEvent getInsnEvent(TraceEvent execEvent) {
      if(execEvent.getExecInsnType().isDecl()) {
        return null;
      }
      
      if(execEvent.getType().isInsn()) {
        return execEvent;
      }
      
      int execEventId = Integer.parseInt(execEvent.getExecInsnId());
      TraceEvent insnEvent = insnTable.get(execEventId);
      return insnEvent;
    }

}