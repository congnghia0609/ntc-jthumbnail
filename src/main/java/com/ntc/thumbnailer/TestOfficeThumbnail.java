/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ntc.thumbnailer;

import static com.ntc.thumbnailer.TestMain.classExists;
import com.ntc.thumbnailer.msdoc.NExcelConverter;
import com.ntc.thumbnailer.msdoc.NHtmlConverter;
import com.ntc.thumbnailer.msdoc.NPowerpointConverter;
import com.ntc.thumbnailer.msdoc.NWordConverter;
import com.ntc.thumbnailer.thumbnailers.JODExcelConverterThumbnailer;
import com.ntc.thumbnailer.thumbnailers.JODHtmlConverterThumbnailer;
import com.ntc.thumbnailer.thumbnailers.JODPowerpointConverterThumbnailer;
import com.ntc.thumbnailer.thumbnailers.JODWordConverterThumbnailer;
import com.ntc.thumbnailer.thumbnailers.NativeImageThumbnailer;
import com.ntc.thumbnailer.thumbnailers.OpenOfficeThumbnailer;
import com.ntc.thumbnailer.thumbnailers.PDFBoxThumbnailer;
import com.ntc.thumbnailer.thumbnailers.ScratchThumbnailer;
import com.ntc.thumbnailer.util.IOUtil;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Apr 27, 2017
 */
public class TestOfficeThumbnail {
    protected static Logger log = LoggerFactory.getLogger(TestOfficeThumbnail.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //testNV();
            
            //testHtml();
            
            testTXT();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class RunTask implements Runnable {
        public int begin = 0;
        public ThumbnailerManager thumbnailer;

        public RunTask(int begin) {
            try {
                this.thumbnailer = new ThumbnailerManager();

                if (classExists("com.ntc.thumbnailer.thumbnailers.NativeImageThumbnailer"))
                    thumbnailer.registerThumbnailer(new NativeImageThumbnailer());

                thumbnailer.registerThumbnailer(new OpenOfficeThumbnailer());
                thumbnailer.registerThumbnailer(new PDFBoxThumbnailer());

                try {
                    int port = IOUtil.nextFreePort();
                    System.out.println("RunTask.port: " + port);
                    thumbnailer.registerThumbnailer(new JODWordConverterThumbnailer(port));
                    thumbnailer.registerThumbnailer(new JODExcelConverterThumbnailer(port));
                    thumbnailer.registerThumbnailer(new JODPowerpointConverterThumbnailer(port));
                    thumbnailer.registerThumbnailer(new JODHtmlConverterThumbnailer(port));
                } catch (Exception e) {
                    log.error("Could not initialize JODConverter:", e);
                }

                thumbnailer.registerThumbnailer(new ScratchThumbnailer());
                
                thumbnailer.setImageSize(200, 200, 0);
                thumbnailer.setThumbnailFolder("thumbs/");

                this.begin = begin;
            } catch (Exception e) {
            }
        }
        
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" Start...");
            String inFile = "";
            String outFile = "";
            try {
                for(int i = begin; i < begin + 5; i++){
                    inFile = "/data/files/tmp/BI" + i + ".doc";
                    outFile = "/data/files/thumb/BI" + i + ".png";
                    File in = new File(inFile);
                    File out = new File(outFile);
                    thumbnailer.generateThumbnail(in, out);
                    
                    System.out.println("SUCCESS: Thumbnail created: " + out.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+" End.|||");
        }
    }
    
    public static void test1(){
        try {
            ThumbnailerManager thumbnailer = new ThumbnailerManager();

            loadExistingThumbnailers(thumbnailer);

            thumbnailer.setImageSize(200, 200, 0);
            thumbnailer.setThumbnailFolder("thumbs/");

            File in = new File("/data/files/tmp/BI0.doc");
            File out = new File("/data/files/thumb/BI0.png");
            if (out == null)
                out = thumbnailer.createThumbnail(in);
            else
                thumbnailer.generateThumbnail(in, out);

            System.out.println("SUCCESS: Thumbnail created:\n" + out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void test2(){
        try {
            ThumbnailerManager thumbnailer = new ThumbnailerManager();

            loadExistingThumbnailers(thumbnailer);

            thumbnailer.setImageSize(200, 200, 0);
            thumbnailer.setThumbnailFolder("thumbs/");

            File in = new File("/data/files/tmp/BI1.doc");
            File out = new File("/data/files/thumb/BI1.png");
            if (out == null)
                out = thumbnailer.createThumbnail(in);
            else
                thumbnailer.generateThumbnail(in, out);

            System.out.println("SUCCESS: Thumbnail created:\n" + out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected static void loadExistingThumbnailers(ThumbnailerManager thumbnailer) {

		if (classExists("com.ntc.thumbnailer.thumbnailers.NativeImageThumbnailer"))
			thumbnailer.registerThumbnailer(new NativeImageThumbnailer());

		thumbnailer.registerThumbnailer(new OpenOfficeThumbnailer());
		thumbnailer.registerThumbnailer(new PDFBoxThumbnailer());
		
		try {
            int port = IOUtil.nextFreePort();
			thumbnailer.registerThumbnailer(new JODWordConverterThumbnailer(port));
			thumbnailer.registerThumbnailer(new JODExcelConverterThumbnailer(port));
			thumbnailer.registerThumbnailer(new JODPowerpointConverterThumbnailer(port));
			thumbnailer.registerThumbnailer(new JODHtmlConverterThumbnailer(port));
		} catch (Exception e) {
			log.error("Could not initialize JODConverter:", e);
		}

		thumbnailer.registerThumbnailer(new ScratchThumbnailer());
	}
    
    public static void testN(){
        try {
            
            ExecutorService executor = Executors.newFixedThreadPool(5);
//            for (int i = 0; i < 10; i++) {
//                Runnable worker = new WorkerThread("" + i);
//                executor.execute(worker);
//            }
            Thread t1 = new Thread(new RunTask(0));
            Thread t2 = new Thread(new RunTask(5));
            Thread t3 = new Thread(new RunTask(10));
            Thread t4 = new Thread(new RunTask(15));
            executor.execute(t1);
            executor.execute(t2);
            executor.execute(t3);
            executor.execute(t4);
            
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("Finished all threads");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//=============================================================================//
    
    public static class RunTask2 implements Runnable {
        public int begin = 0;
        public ThumbnailerManager thumbnailer;

        public RunTask2(int begin) {
            try {
                this.thumbnailer = new ThumbnailerManager();

                if (classExists("com.ntc.thumbnailer.thumbnailers.NativeImageThumbnailer"))
                    thumbnailer.registerThumbnailer(new NativeImageThumbnailer());

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
                    
                    port = IOUtil.nextFreePort();
                    System.out.println("NHtmlConverter.port: " + port);
                    thumbnailer.registerThumbnailer(new NHtmlConverter(port));
                } catch (Exception e) {
                    log.error("Could not initialize JODConverter:", e);
                }
                
                thumbnailer.registerThumbnailer(new ScratchThumbnailer());
                
                thumbnailer.setImageSize(200, 200, 0);
                thumbnailer.setThumbnailFolder("thumbs/");

                this.begin = begin;
            } catch (Exception e) {
            }
        }
        
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" Start...");
            String inFile = "";
            String outFile = "";
            try {
                for(int i = begin; i < begin + 5; i++){
                    long start = System.nanoTime();
                    inFile = "/data/files/tmp/BI" + i + ".doc";
                    outFile = "/data/files/thumb/BI" + i + ".png";
                    File in = new File(inFile);
                    File out = new File(outFile);
                    thumbnailer.generateThumbnail(in, out);
                    System.out.println("=============Run Time: " + (System.nanoTime() - start) + " ns");
                    System.out.println("SUCCESS: Thumbnail created: " + out.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+" End.|||");
        }
    }
    
    public static void testNV(){
        try {
            
            ExecutorService executor = Executors.newFixedThreadPool(5);
            Thread t1 = new Thread(new RunTask2(0));
            Thread t2 = new Thread(new RunTask2(5));
            Thread t3 = new Thread(new RunTask2(10));
            Thread t4 = new Thread(new RunTask2(15));
            executor.execute(t1);
            executor.execute(t2);
            executor.execute(t3);
            executor.execute(t4);
            
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("Finished all threads");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void testHtml(){
        try {
            ThumbnailerManager thumbnailer = new ThumbnailerManager();

            if (classExists("com.ntc.thumbnailer.thumbnailers.NativeImageThumbnailer"))
                thumbnailer.registerThumbnailer(new NativeImageThumbnailer());

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

                port = IOUtil.nextFreePort();
                System.out.println("NHtmlConverter.port: " + port);
                thumbnailer.registerThumbnailer(new NHtmlConverter(port));
            } catch (Exception e) {
                log.error("Could not initialize JODConverter:", e);
            }

            thumbnailer.registerThumbnailer(new ScratchThumbnailer());

            thumbnailer.setImageSize(200, 200, 0);
            thumbnailer.setThumbnailFolder("thumbs/");
            
            long start = System.nanoTime();
            String inFile = "/data/files/tmp/ntc.html";
            String outFile = "/data/files/thumb/ntc.png";
            File in = new File(inFile);
            File out = new File(outFile);
            thumbnailer.generateThumbnail(in, out);
            System.out.println("=============Run Time: " + (System.nanoTime() - start) + " ns");
            System.out.println("SUCCESS: Thumbnail created: " + out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void testTXT(){
        try {
            ThumbnailerManager thumbnailer = new ThumbnailerManager();

            if (classExists("com.ntc.thumbnailer.thumbnailers.NativeImageThumbnailer"))
                thumbnailer.registerThumbnailer(new NativeImageThumbnailer());

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

                port = IOUtil.nextFreePort();
                System.out.println("NHtmlConverter.port: " + port);
                thumbnailer.registerThumbnailer(new NHtmlConverter(port));
            } catch (Exception e) {
                log.error("Could not initialize JODConverter:", e);
            }

            thumbnailer.registerThumbnailer(new ScratchThumbnailer());

            thumbnailer.setImageSize(200, 200, 0);
            thumbnailer.setThumbnailFolder("thumbs/");
            
            long start = System.nanoTime();
            String inFile = "/data/files/tmp/lua.txt";
            String outFile = "/data/files/thumb/lua.png";
            File in = new File(inFile);
            File out = new File(outFile);
            thumbnailer.generateThumbnail(in, out);
            System.out.println("=============Run Time: " + (System.nanoTime() - start) + " ns");
            System.out.println("SUCCESS: Thumbnail created: " + out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
