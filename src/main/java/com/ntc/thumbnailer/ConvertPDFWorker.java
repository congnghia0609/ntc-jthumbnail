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
import com.ntc.thumbnailer.util.IOUtil;
import com.ntc.configer.NConfig;
import com.ntc.jackson.JsonUtils;
import com.ntc.rabbit.consumer.ConsumerRBProcess;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since May 8, 2017
 */
public class ConvertPDFWorker extends ConsumerRBProcess {
    private Logger logger = LoggerFactory.getLogger(ConvertPDFWorker.class);
    private final static String routingKey = NConfig.getConfig().getString("worker.pdf.topic", "doc_to_pdf");
    //"amqp://username:password@localhost:5672/";
    private final static String amqpUrl = NConfig.getConfig().getString("rbconsumer.amqp_url", "");
    private final int numPort = NConfig.getConfig().getInt("worker.pdf.numport", 10);
    private OfficeManager officeManager;
    private OfficeDocumentConverter converter;
    
    public ConvertPDFWorker() {
        super(routingKey, amqpUrl);
        
        int [] arrPort = new int[numPort];
        for(int i=0; i < numPort; i++){
            int port = IOUtil.nextFreePort();
            arrPort[i] = port;
        }
        System.out.println("ConvertPDFWorker.arrPort: " + Arrays.toString(arrPort));
        officeManager = new DefaultOfficeManagerConfiguration().setPortNumbers(arrPort).buildOfficeManager();
        officeManager.start();

        converter = new OfficeDocumentConverter(officeManager);
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
            System.out.println(" [PDF] Received '" + routingKey + "':'" + mapData + "'");
            
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

                        String pathOut = Common.DIR_PDF + basename + ".pdf";
                        System.out.println("pathOut: " + pathOut);
                        File out = new File(pathOut);
                        
                        converter.convert(in, out);
                        
                        System.out.println("SUCCESS: PDF created: " + out.getAbsolutePath());
                    }
                }
            }
            System.out.println("=============ConvertPDFWorker Run Time: " + (System.nanoTime() - start) + " ns");
        } catch (Exception e) {
            logger.error("ConvertPDFWorker.execute: " + e.getMessage(), e);
        }
    }
    
}
