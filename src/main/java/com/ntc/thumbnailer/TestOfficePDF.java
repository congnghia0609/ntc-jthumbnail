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

import com.ntc.thumbnailer.util.IOUtil;
import java.io.File;
import java.util.Arrays;
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
public class TestOfficePDF {
    private static Logger logger = LoggerFactory.getLogger(ThumbnailOfficeWorker.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            // /usr/lib/libreoffice
            // /usr/lib/openoffice"
//            OfficeManager officeManager = new DefaultOfficeManagerConfiguration().setOfficeHome("/usr/lib/libreoffice")
//                                            .setConnectionProtocol(OfficeConnectionProtocol.PIPE)
//                                            .setPipeNames("office1", "office2")
//                                            .setTaskExecutionTimeout(30000L)
//                                            .buildOfficeManager();
//            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
//            File in = new File("/data/files/tmp/BI0.doc");
//            File out = new File("/data/files/tmp/BI0.pdf");
//            converter.convert(in, out);
//            System.out.println("Done...");

            int numPort = 1;
            int [] arrPort = new int[numPort];
            for(int i=0; i < numPort; i++){
                int port = IOUtil.nextFreePort();
                arrPort[i] = port;
            }
            System.out.println("arrPort: " + Arrays.toString(arrPort));
            
            
            OfficeManager officeManager = new DefaultOfficeManagerConfiguration().setPortNumbers(arrPort).buildOfficeManager();
            officeManager.start();

            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
//            File in = new File("/data/files/tmp/BI.odt");
//            File out = new File("/data/files/pdf/BI.pdf");
            
//            File in = new File("/data/files/tmp/ntc.html");
//            File out = new File("/data/files/pdf/ntc.pdf");
            
            File in = new File("/data/files/tmp/lua.txt");
            File out = new File("/data/files/pdf/lua.pdf");
            converter.convert(in, out);
            System.out.println("Done...");

            officeManager.stop();
        } catch (Exception e) {
            logger.error("ThumbnailOfficeWorker: " + e.getMessage(), e);
        }
    }
    
    public static boolean classExists(String qualifiedClassname) {
		try {
			Class.forName(qualifiedClassname);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

}
