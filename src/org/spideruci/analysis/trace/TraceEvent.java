package org.spideruci.analysis.trace;

import org.spideruci.analysis.trace.eventprops.ArrayInsnExecPropNames;
import org.spideruci.analysis.trace.eventprops.ControlFlowPropNames;
import org.spideruci.analysis.trace.eventprops.DeclPropNames;
import org.spideruci.analysis.trace.eventprops.EnterExecPropNames;
import org.spideruci.analysis.trace.eventprops.FieldInsnExecPropNames;
import org.spideruci.analysis.trace.eventprops.InsnExecPropNames;
import org.spideruci.analysis.trace.eventprops.InsnPropNames;
import org.spideruci.analysis.trace.eventprops.InvokeInsnExecPropNames;
import org.spideruci.analysis.trace.eventprops.VarInsnExecPropNames;
import org.spideruci.analysis.util.MyAssert;

/**
 * TODO: rename to Event.
 * @author vpalepu
 *
 */
@SuppressWarnings("rawtypes")
public class TraceEvent implements MethodDecl {
  
  private static final String SEP = ",";
  
  private final int id;
  private final EventType type;
  private final String[] propValues;
  private final Enum[] propNames;
  private String[] ipds;
  
  public static TraceEvent copy(TraceEvent e) {
    TraceEvent copy = new TraceEvent(e.id, e.type, e.propNames);
    
    for(int i = 0; i < e.propValues.length; i += 1) {
      copy.propValues[i] = e.propValues[i];
    }
    
    return copy;
  }
  
  public static TraceEvent createControlFlowEvent(int id) {
    return new TraceEvent(id, EventType.$flow$, ControlFlowPropNames.values);
  }
  
  public static TraceEvent createInvokeInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, InvokeInsnExecPropNames.values);
  }
  
  public static TraceEvent createEnterExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, EnterExecPropNames.values);
  }
  
  public static TraceEvent createFieldInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, FieldInsnExecPropNames.values);
  }
  
  public static TraceEvent createVarInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, VarInsnExecPropNames.values);
  }
  
  /**
   * 
   * @param id
   * @return
   */
  public static TraceEvent createArrayInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, ArrayInsnExecPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for the 
   * execution of a static instruction, and its properties are specified
   * with {@link InsnExecPropNames}.
   * @param id Unique id for the event.
   * @return
   */
  public static TraceEvent createInsnExecEvent(int id) {
    return new TraceEvent(id, EventType.$$$, InsnExecPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a static instruction 
   * in a method definition, as specified by {@link InsnPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createInsnEvent(int id, EventType type) {
    return new TraceEvent(id, type, InsnPropNames.values);
  }
  
  /**
   * Creates a trace event that can store properties for a method or class
   * declaration, as specified by {@link DeclPropNames}.
   * @param id Unique id for the event.
   * @param type The type of this event (can be null).
   * @return
   */
  public static TraceEvent createDeclEvent(int id, EventType type) {
    return new TraceEvent(id, type, DeclPropNames.values);
  }
  
  public static TraceEvent valueOf(String eventString) {
    String[] split = eventString.split(SEP);
    String typeString = split[0];
    int id = Integer.parseInt(split[1]);
    
    final int offset = 2;
    
    TraceEvent event;
    EventType type = EventType.valueOf(typeString);
    if(type.isExec()) {
      int eventDescPos = offset + InsnExecPropNames.INSN_EVENT_TYPE.ordinal();
      String insnType = eventString.split(",")[eventDescPos];
      
      switch(EventType.valueOf(insnType)) {
      case $invoke$:
        event = TraceEvent.createInvokeInsnExecEvent(id);
        break;
      case $enter$:
        event = TraceEvent.createEnterExecEvent(id);
        break;
//      case $var$:
//        event = TraceEvent.createVarInsnExecEvent(id);
//        break;
      case $field$:
        event = TraceEvent.createFieldInsnExecEvent(id);
        break;
      case $arrayload$:
      case $arraystore$:
        event = TraceEvent.createArrayInsnExecEvent(id);
        break;
      default:
        event = TraceEvent.createInsnExecEvent(id);
      }
    } else if(type.isDecl()) {
      event = TraceEvent.createDeclEvent(id, type);
    } else if(type.isInsn()) {
      event = TraceEvent.createInsnEvent(id, type);
    } else if(type.isFlow()) {
      event = TraceEvent.createControlFlowEvent(id);
    } else {
      throw new RuntimeException("Unfamiliar event: " + eventString);
    }
    
    MyAssert.assertThat(event.getPropCount() <= (split.length  - offset), 
        String.valueOf(split.length) + " event-string:" + eventString);
    
    for(int i = offset; i < split.length; i += 1) {
      event.setProp(i - offset, split[i]);
    }
    
    return event;
  }
  
  private TraceEvent(int id, EventType type, Enum[] propNames) {
    if(propNames == null || propNames.length <= 0) {
      throw new UnsupportedOperationException();
    }
    
    this.id = id;
    this.type = type;
    this.propNames = propNames;
    this.propValues = new String[propNames.length];
    this.ipds = null;
  }

  public int getId() {
    return this.id;
  }

  public EventType getType() {
    return this.type;
  }
  
  public void ipdsAre(String[] ipds) {
    this.ipds = ipds;
  }
  
  public String[] ipds() {
    return this.ipds;
  }

  public String getLog() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(type == null ? "" : type).append(SEP);
    buffer.append(id).append(SEP);
    int lastIndex = propValues.length - 1;
    for(int i = 0; i < lastIndex; i += 1) {
      String value = propValues[i];
      buffer.append(value == null ? "" : value).append(SEP);
    }
    buffer.append(propValues[lastIndex]);
    
    return buffer.toString();
  }
  
  public int getPropCount() {
    return this.propValues.length;
  }

  public String getProp(int index) {
    return propValues[index];
  }

  public String getProp(final Enum propName) {
    if(!enumChecksOut(propName)) 
      return null;
    int index = propName.ordinal();
    return propValues[index];
  }

  public String getPropName(int index) {
    return propNames[index].toString();
  }

  public void setProp(int index, String propValue) {
    propValues[index] = propValue;
  }

  public void setProp(final Enum propName, final String propValue) {
    if(enumChecksOut(propName))
      setProp(propName.ordinal(), propValue);
  }
  
    private boolean enumChecksOut(final Enum propName) {
      if(propName == null
          || propName.getDeclaringClass() != propNames[0].getDeclaringClass())
        return false;
      return true;
    }
    
  public String getDeclName() {
    return getProp(DeclPropNames.NAME);
  }
  
  public String getDeclAccess() {
    return getProp(DeclPropNames.ACCESS);
  }
  
  public String getDeclOwner() {
    return getProp(DeclPropNames.OWNER);
  }
  
  public int getInsnLine() {
    return Integer.parseInt(getProp(InsnPropNames.LINE_NUMBER));
  }
  
  public int getInsnByteIndex() {
    return Integer.parseInt(getProp(InsnPropNames.BYTECODE_INDEX));
  }
  
  public int getInsnDeclHostId() {
    return Integer.parseInt(getProp(InsnPropNames.DECL_HOST_ID));
  }
  
  public String getInsnFieldOwner() {
    return getProp(InsnPropNames.OPERAND1);
  }
  
  public String getInsnFieldName() {
    return getProp(InsnPropNames.OPERAND2);
  }
  
  public String getInsnFieldDesc() {
    return getProp(InsnPropNames.OPERAND3);
  }
  
  public String getInsnNewType() {
    return getProp(InsnPropNames.OPERAND1);
  }
  
  public String getExecThreadId() {
    final int index = InsnExecPropNames.THREAD_ID.ordinal();
    return getProp(index);
  }
  
  public String getExecTimestamp() {
    final int index = InsnExecPropNames.TIMESTAMP.ordinal();
    return getProp(index);
  }
  
  public String getExecInsnDynHost() {
    final int index = InsnExecPropNames.DYN_HOST_ID.ordinal();
    return getProp(index);
  }
  
  public String getExecInsnId() {
    final int index = InsnExecPropNames.INSN_EVENT_ID.ordinal();
    return getProp(index);
  }
  
  public EventType getExecInsnType() {
    final int index = InsnExecPropNames.INSN_EVENT_TYPE.ordinal();
    return EventType.valueOf(getProp(index));
  }
  
  public String getInvokeRuntimeSign() {
    return getProp(InvokeInsnExecPropNames.RUNTIME_SIGNATURE);
  }
  
  public String getEnterRuntimeSign() {
    return getProp(EnterExecPropNames.RUNTIME_SIGNATURE);
  }
  
  @Override
  public String toString() {
    return getLog();
  }

}
