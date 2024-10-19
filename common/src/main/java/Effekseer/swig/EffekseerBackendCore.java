/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package Effekseer.swig;

public class EffekseerBackendCore {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected EffekseerBackendCore(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(EffekseerBackendCore obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(EffekseerBackendCore obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("removal")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        EffekseerCoreJNI.delete_EffekseerBackendCore(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public EffekseerBackendCore() {
    this(EffekseerCoreJNI.new_EffekseerBackendCore(), true);
  }

  public static EffekseerCoreDeviceType GetDevice() {
    return EffekseerCoreDeviceType.swigToEnum(EffekseerCoreJNI.EffekseerBackendCore_GetDevice());
  }

  public static boolean InitializeWithOpenGL() {
    return EffekseerCoreJNI.EffekseerBackendCore_InitializeWithOpenGL();
  }

  public static void Terminate() {
    EffekseerCoreJNI.EffekseerBackendCore_Terminate();
  }

}
