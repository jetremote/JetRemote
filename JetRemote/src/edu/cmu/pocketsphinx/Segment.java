/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package edu.cmu.pocketsphinx;

public class Segment {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected Segment(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Segment obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        PocketSphinxJNI.delete_Segment(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setWord(String value) {
    PocketSphinxJNI.Segment_word_set(swigCPtr, this, value);
  }

  public String getWord() {
    return PocketSphinxJNI.Segment_word_get(swigCPtr, this);
  }

  public void setAscore(int value) {
    PocketSphinxJNI.Segment_ascore_set(swigCPtr, this, value);
  }

  public int getAscore() {
    return PocketSphinxJNI.Segment_ascore_get(swigCPtr, this);
  }

  public void setLscore(int value) {
    PocketSphinxJNI.Segment_lscore_set(swigCPtr, this, value);
  }

  public int getLscore() {
    return PocketSphinxJNI.Segment_lscore_get(swigCPtr, this);
  }

  public void setLback(int value) {
    PocketSphinxJNI.Segment_lback_set(swigCPtr, this, value);
  }

  public int getLback() {
    return PocketSphinxJNI.Segment_lback_get(swigCPtr, this);
  }

  public void setProb(int value) {
    PocketSphinxJNI.Segment_prob_set(swigCPtr, this, value);
  }

  public int getProb() {
    return PocketSphinxJNI.Segment_prob_get(swigCPtr, this);
  }

  public void setStartFrame(int value) {
    PocketSphinxJNI.Segment_startFrame_set(swigCPtr, this, value);
  }

  public int getStartFrame() {
    return PocketSphinxJNI.Segment_startFrame_get(swigCPtr, this);
  }

  public void setEndFrame(int value) {
    PocketSphinxJNI.Segment_endFrame_set(swigCPtr, this, value);
  }

  public int getEndFrame() {
    return PocketSphinxJNI.Segment_endFrame_get(swigCPtr, this);
  }

  public static Segment fromIter(SWIGTYPE_p_ps_seg_t itor) {
    long cPtr = PocketSphinxJNI.Segment_fromIter(SWIGTYPE_p_ps_seg_t.getCPtr(itor));
    return (cPtr == 0) ? null : new Segment(cPtr, false);
  }

  public Segment() {
    this(PocketSphinxJNI.new_segment(), true);
  }

}
