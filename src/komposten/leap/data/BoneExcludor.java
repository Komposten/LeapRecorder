/*
 * Copyright (c) 2014 Jakob Hjelm 
 */
package komposten.leap.data;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class BoneExcludor implements ExclusionStrategy
{
  public static boolean shouldExclude;
  
  @Override
  public boolean shouldSkipClass(Class<?> clazz)
  {
    if (shouldExclude && clazz.equals(Bone[].class))
      return true;
    return false;
  }

  @Override
  public boolean shouldSkipField(FieldAttributes field)
  {
    if (shouldExclude && field.getDeclaredClass().equals(Bone[].class))
      return true;
    return false;
  }
}