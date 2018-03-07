/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hops.metadata.hdfs.entity;

import io.hops.metadata.common.FinderType;

public class Ace {
  public enum AceType {
    USER(0),
    GROUP(1);
    /*
      Default Mask and Default Other entries are not allowed
      Therefore, no Mask or Other entries will ever show up in an extended acl.
     */
    
    int value;
    
    AceType(int value){
      this.value = value;
    }
  
    public int getValue() {
      return value;
    }
    
    public static AceType valueOf(int value){
      switch (value){
        case 0:
          return USER;
        case 1:
          return GROUP;
        default:
          throw new RuntimeException("Incorrect value " + value + ", should be 0 or 1.");
      }
    }
  }
  
  public enum Finder implements FinderType<Ace> {
    InodeIdAndId,
    ByInodeId;
    
    @Override
    public Class getType() {
      return Ace.class;
    }
    
    @Override
    public Annotation getAnnotated() {
      switch (this){
        case InodeIdAndId:
          return Annotation.PrimaryKey;
        case ByInodeId:
          return Annotation.PrunedIndexScan;
        default:
          throw new IllegalStateException();
      }
    }
  }
  
  public static class PrimaryKey{
    public final int id;
    public final int inodeId;
    
    public PrimaryKey(int id, int inodeId){
      this.id = id;
      this.inodeId = inodeId;
    }
  
    @Override
    public int hashCode() {
      //todo what is the go to hash mechanism that is consistent with equals?
      return id * inodeId;
    }
  
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof PrimaryKey){
        PrimaryKey other = (PrimaryKey) obj;
        return id == other.id && inodeId == other.inodeId;
      }
      return false;
    }
  }
  
  private int inodeId;
  private int id;
  private String subject;
  private AceType type;
  private boolean isDefault;
  private int permission;
  private int index;
  
  public PrimaryKey getPrimaryKey(){
    return new PrimaryKey(inodeId, id);
  }
  
  public Ace(int inodeId, int id){
    this.inodeId = inodeId;
    this.id = inodeId;
  }
  
  public Ace(int inodeId, int id, String subject, AceType type, boolean isDefault, int permission, int index) {
    this.inodeId = inodeId;
    this.id = id;
    this.subject = subject;
    this.isDefault = isDefault;
    this.permission = permission;
    this.type = type;
    this.index = index;
  }
  

  public int getInodeId() {
    return inodeId;
  }
  
  public void setInodeId(int inodeId){
    this.inodeId = inodeId;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id){
    this.id = id;
  }
  
  
  public String getSubject() {
    return subject;
  }
  
  public AceType getType() {
    return type;
  }
  
  public boolean isDefault() {
    return isDefault;
  }
  
  public int getPermission() {
    return permission;
  }
  
  public int getIndex() {return index;}
}
