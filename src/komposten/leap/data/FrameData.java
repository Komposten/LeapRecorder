/*
 * Copyright (c) 2014 Jakob Hjelm 
 */
package komposten.leap.data;

import java.util.ArrayList;
import java.util.List;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Matrix;

class FrameData
{
  long          frameId;
  List<Hand>    hands;
  
  public FrameData(Frame frame)
  {
    frameId = frame.id();
    hands   = new ArrayList<Hand>();
    
    for (int i = 0; i < frame.hands().count(); i++)
      hands.add(new Hand(frame.hands().get(i)));
  }
  
  
  
  @Override
  public String toString()
  {
    return "[id=" + frameId + ", " + "handcount=" + hands.size()+ "]";
  }
}



class Hand
{
  public int     id;
  public float[] direction;
  public float[] palmPosition;
  public float[] palmNormal;
  public float[] palmVelocity;
  public Basis   basis;
  public boolean isLeft;
  public boolean isRight;
  List<Finger>   fingers;
  
  public Hand(com.leapmotion.leap.Hand hand)
  {
    id           = hand.id();
    direction    = hand.direction   ().toFloatArray();
    palmPosition = hand.palmPosition().toFloatArray();
    palmNormal   = hand.palmNormal  ().toFloatArray();
    palmVelocity = hand.palmVelocity().toFloatArray();
    basis        = new Basis(hand.basis());
    isLeft       = hand.isLeft();
    isRight      = hand.isRight();
    fingers      = new ArrayList<Finger>();
    
    for (int i = 0; i < hand.fingers().count(); i++)
      fingers.add(new Finger(hand.fingers().get(i)));
  }
}


class Finger
{
  public int     id;
  public float[] direction;
  public float[] tipPosition;
  public float[] tipVelocity;
  public Bone [] bones;
  
  public Finger(com.leapmotion.leap.Finger finger)
  {
    id          = finger.id();
    direction   = finger.direction()  .toFloatArray();
    tipPosition = finger.tipPosition().toFloatArray();
    tipVelocity = finger.tipVelocity().toFloatArray();
    bones       = new Bone[4];
    bones[0]    = new Bone(finger.bone(Type.TYPE_METACARPAL));
    bones[1]    = new Bone(finger.bone(Type.TYPE_PROXIMAL));
    bones[2]    = new Bone(finger.bone(Type.TYPE_INTERMEDIATE));
    bones[3]    = new Bone(finger.bone(Type.TYPE_DISTAL));
  }
}


class Bone
{
  public Type    type;
  public float   length;
  public float   width;
  public float[] direction;
  public float[] center;
  public float[] nextJoint;
  public float[] prevJoint;
  public Basis   basis;
  
  
  
  public Bone(com.leapmotion.leap.Bone bone)
  {
    type      = bone.type();
    length    = bone.length();
    width     = bone.width();
    direction = bone.direction().toFloatArray();
    center    = bone.center()   .toFloatArray();
    nextJoint = bone.nextJoint().toFloatArray();
    prevJoint = bone.prevJoint().toFloatArray();
    basis     = new Basis(bone.basis());
  }
}


class Basis
{
  public float[] origin;
  public float[] xBasis;
  public float[] yBasis;
  public float[] zBasis;
  
  public Basis(Matrix matrix)
  {
    origin = matrix.getOrigin().toFloatArray();
    xBasis = matrix.getXBasis().toFloatArray();
    yBasis = matrix.getYBasis().toFloatArray();
    zBasis = matrix.getZBasis().toFloatArray();
  }
}