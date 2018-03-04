/*
 * Copyright 2017 nghiatc.
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

package com.ntc.thumbnailer;

import com.ntc.thumbnailer.common.Common;
import com.ntc.thumbnailer.msdoc.NExcelConverter;
import com.ntc.thumbnailer.msdoc.NHtmlConverter;
import com.ntc.thumbnailer.msdoc.NPowerpointConverter;
import com.ntc.thumbnailer.msdoc.NWordConverter;
import com.ntc.thumbnailer.thumbnailers.NativeImageThumbnailer;
import com.ntc.thumbnailer.thumbnailers.OpenOfficeThumbnailer;
import com.ntc.thumbnailer.thumbnailers.PDFBoxThumbnailer;
import com.ntc.thumbnailer.thumbnailers.ScratchThumbnailer;
import com.ntc.thumbnailer.util.IOUtil;
import com.ntc.configer.NConfig;
import com.ntc.jackson.JsonUtils;
import com.ntc.rabbit.consumer.ConsumerRBProcess;
import java.io.File;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since May 4, 2017
 */
public class ThumbnailOfficeWorker extends ConsumerRBProcess {
    private Logger logger = LoggerFactory.getLogger(ThumbnailOfficeWorker.class);
    private final static String routingKey = NConfig.getConfig().getString("worker.doc.topic", "doc_to_thumb");
    //"amqp://username:password@localhost:5672/";
    private final static String amqpUrl = NConfig.getConfig().getString("rbconsumer.amqp_url", "");
    
    public ThumbnailerManager thumbnailer;
    
    public ThumbnailOfficeWorker() {
        super(routingKey, amqpUrl);
        
        try {
            this.thumbnailer = new ThumbnailerManager();

            if (classExists("com.inspilab.thumbnailer.thumbnailers.NativeImageThumbnailer")){
                thumbnailer.registerThumbnailer(new NativeImageThumbnailer());
            }

            thumbnailer.registerThumbnailer(new OpenOfficeThumbnailer());
            thumbnailer.registerThumbnailer(new PDFBoxThumbnailer());

            try {
                int port = IOUtil.nextFreePort();
                System.out.println("NWordConverter.port: " + port);
                thumbnailer.registerThumbnailer(new NWordConverter(port));

                port = IOUtil.nextFreePort();
                System.out.println("NExcelConverter.port: " + port);
                thumbnailer.registerThumbnailer(new NExcelConverter(port));

                port = IOUtil.nextFreePort();
                System.out.println("NPowerpointConverter.port: " + port);
                thumbnailer.registerThumbnailer(new NPowerpointConverter(port));

                //port = IOUtil.nextFreePort();
                //System.out.println("NHtmlConverter.port: " + port);
                //thumbnailer.registerThumbnailer(new NHtmlConverter(port));
            } catch (Exception e) {
                logger.error("Could not initialize JODConverter:", e);
            }

            thumbnailer.registerThumbnailer(new ScratchThumbnailer());

            thumbnailer.setImageSize(200, 200, 0);
            thumbnailer.setThumbnailFolder("thumbs/");
            
        } catch (Exception e) {
            logger.error("ThumbnailOfficeWorker: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getRoutingKey() {
        return routingKey;
    }

    @Override
    public void execute(byte[] data) {
        try {
            long start = System.nanoTime();
            Map<String, Object> mapData = JsonUtils.Instance.getMapObject(data);
            System.out.println(" [OOF] Received '" + routingKey + "':'" + mapData + "'");

            if(mapData != null && !mapData.isEmpty()){
                String pathFile = (String) (mapData.containsKey("pathFile") ? mapData.get("pathFile") : "");
                int id = (int) (mapData.containsKey("id") ? mapData.get("id") : -1);
                if(pathFile != null && !pathFile.isEmpty()){
                    File in = new File(pathFile);
                    if(in != null && in.exists()){
                        String filename = in.getName();
                        int dot = filename.lastIndexOf('.');
                        String basename = (dot == -1) ? filename : filename.substring(0, dot);
                        String extension = (dot == -1) ? "" : filename.substring(dot+1);

                        String pathOut = Common.DIR_THUMB + basename + ".png";
                        System.out.println("pathOut: " + pathOut);
                        File out = new File(pathOut);
                        
                        thumbnailer.generateThumbnail(in, out);
                        
                        System.out.println("SUCCESS: Thumbnail created: " + out.getAbsolutePath());
                    }
                }
            }
            System.out.println("=============ThumbnailOfficeWorker Run Time: " + (System.nanoTime() - start) + " ns");
        } catch (Exception e) {
            logger.error("ThumbnailOfficeWorker.execute: " + e.getMessage(), e);
        }
    }
    
    public boolean classExists(String qualifiedClassname) {
		try {
			Class.forName(qualifiedClassname);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
    
//    public static void main(String[] args) {
//        String pathFile = "/data/files/tmp/30.png";
//        int dot = pathFile.lastIndexOf('.');
//        String base = (dot == -1) ? pathFile : pathFile.substring(0, dot);
//        String extension = (dot == -1) ? "" : pathFile.substring(dot+1);
//        
//        System.out.println("base: " + base);
//        System.out.println("extension: " + extension);
//        //base: /data/files/tmp/30
//        //extension: png
//    }
    
}
