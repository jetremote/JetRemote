/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package edu.cmu.pocketsphinx;

public class Feature {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected Feature(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Feature obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        SphinxBaseJNI.delete_Feature(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public Feature(SWIGTYPE_p_feat_t ptr) {
    this(SphinxBaseJNI.new_Feature(SWIGTYPE_p_feat_t.getCPtr(ptr)), true);
  }

}