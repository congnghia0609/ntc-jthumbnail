/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.thumbnailer.util;

import org.hashids.Hashids;

/**
 *
 * @author nghiatc
 * @since Jul 22, 2015
 */
public class NIdNoise {

    private final static String NSALT = "fdsfasf4454654eas#@%$#&";
    private final static int HASH_LENGTH = 11;
    private final static Hashids hashId = new Hashids(NSALT, HASH_LENGTH);
    
    public static NIdNoise instance = new NIdNoise();
    private NIdNoise(){
        
    }
    
    public static NIdNoise getInstance(){
        return instance;
    }
    
    public static String enNoiseIId(int id){
        if(id < 0){
            throw new IllegalArgumentException("id must more than or equal 0.");
        }
        return hashId.encode(id);
    }
    
    public static int deNoiseIId(String eid){
        if(eid == null || eid.isEmpty()){
            throw new IllegalArgumentException("eid is not null or empty.");
        }
        Integer ret = new Integer(String.valueOf(hashId.decode(eid)[0]));
        return ret;
    }
    
    public static String enNoiseLId(long id){
        if(id < 0){
            throw new IllegalArgumentException("id must more than or equal 0.");
        }
        return hashId.encode(id);
    }
    
    public static long deNoiseLId(String eid){
        if(eid == null || eid.isEmpty()){
            throw new IllegalArgumentException("eid is not null or empty.");
        }
        return hashId.decode(eid)[0];
    }
    
    
//    public static void main(String[] args) {
//        for(int id = 0; id < 100; id++){
//            String eid = NIdNoise.getInstance().enNoiseLId(id);
//            System.out.println("id: " + id + " ---> eid: " + eid);
//            long did = NIdNoise.getInstance().deNoiseLId(eid);
//            System.out.println("eid: " + eid + " --> did: " + did);
//            System.out.println("id == did: " + (id == did));
//        }
//    }
    
}













