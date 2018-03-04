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

package com.ntc.thumbnailer.msdoc;

import com.ntc.thumbnailer.ThumbnailerException;
import com.ntc.thumbnailer.thumbnailers.AbstractThumbnailer;
import com.ntc.thumbnailer.thumbnailers.OpenOfficeThumbnailer;
import com.ntc.thumbnailer.util.IOUtil;
import com.ntc.thumbnailer.util.Platform;
import com.ntc.thumbnailer.util.TemporaryFilesManager;
import com.ntc.thumbnailer.util.mime.MimeTypeDetector;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Apr 28, 2017
 */
public abstract class NConverter extends AbstractThumbnailer {
    /** The logger for this class */
	protected static Logger mLog = LoggerFactory.getLogger(NConverter.class);

	/**
	 * JOD Office Manager
	 */
	protected OfficeManager officeManager = null;
	
	/**
	 * JOD Converter
	 */
	protected OfficeDocumentConverter officeConverter = null;

	/**
	 * Thumbnail Extractor for OpenOffice Files
	 */
	protected OpenOfficeThumbnailer ooo_thumbnailer = null;
	
	/**
	 * MimeIdentification
	 */
	protected MimeTypeDetector mimeTypeDetector = null;

	private TemporaryFilesManager temporaryFilesManager = null;

	/**
	 * OpenOffice Home Folder (Configurable)
	 */
	private String openOfficeHomeFolder = null;
	
	
	/**
	 * The Port on which to connect (must be unoccupied)
	 */
	private int OOO_PORT = 8100;
	
	/**
	 * How long may a conversion take? (in ms)
	 */
	private final long JOD_DOCUMENT_TIMEOUT = 12000;
	
	public void setOpenOfficeHomeFolder(String openOfficeHomeFolder) {
		openOfficeHomeFolder = openOfficeHomeFolder;
	}

	public void setOpenOfficeProfileFolder(String paramOpenOfficeProfile)
	{
		if (paramOpenOfficeProfile != null)
			openOfficeTemplateProfileDir = new File(paramOpenOfficeProfile);
	}
	
//	public NConverter() {
//        OOO_PORT = IOUtil.nextFreePort();
//		ooo_thumbnailer = new OpenOfficeThumbnailer();
//		mimeTypeDetector = new MimeTypeDetector();
//		temporaryFilesManager = new TemporaryFilesManager();
//	}
    
    public NConverter(int port) {
        OOO_PORT = port;
		ooo_thumbnailer = new OpenOfficeThumbnailer();
		mimeTypeDetector = new MimeTypeDetector();
		temporaryFilesManager = new TemporaryFilesManager();
        
        connect();
	}
	
	protected File openOfficeTemplateProfileDir = null;
	
	
	/**
	 * Start OpenOffice-Service and connect to it.
	 * (Does not reconnect if already connected.)
	 */
	public void connect() { connect(true); }
	
	/**
	 * Start OpenOffice-Service and connect to it.
	 * @param forceReconnect	Connect even if he is already connected.
	 */
	public void connect(boolean forceReconnect) {
//		if (!forceReconnect && isConnected())
//			return;
        
		DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration()
			.setPortNumber(OOO_PORT)
			.setTaskExecutionTimeout(JOD_DOCUMENT_TIMEOUT);
		
		if (openOfficeHomeFolder != null)
			config.setOfficeHome(openOfficeHomeFolder);
		
		if (openOfficeTemplateProfileDir != null) {
			if (openOfficeTemplateProfileDir.exists())
				config.setTemplateProfileDir(openOfficeTemplateProfileDir);
			else
				mLog.info("No Template Profile Folder found at " + openOfficeTemplateProfileDir.getAbsolutePath() + " - Creating temporary one.");
		} else {
			mLog.info("Creating temporary profile folder...");
        }
        
        mLog.info("=====>>>>> OfficeDocumentConverter OOO_PORT: " + OOO_PORT);
		officeManager = config.buildOfficeManager();
		officeManager.start();
		
		officeConverter = new OfficeDocumentConverter(officeManager);
	}
	
	/**
	 * Check if a connection to OpenOffice is established.
	 * @return	True if connected.
	 */
	public boolean isConnected()
	{
		return officeManager != null && officeManager.isRunning();
	}

	/**
	 * Stop the OpenOffice Process and disconnect.
	 */
	public void disconnect()
	{
		// close the connection
		if (officeManager != null)
			officeManager.stop();
		officeManager = null;
	}
	
    @Override
	public void close() throws IOException
	{
		try {
			try {
				temporaryFilesManager.deleteAllTempfiles();
				ooo_thumbnailer.close();
			} finally {
				disconnect();
			}
		} finally {
			super.close();
		}
	}	
	
	
	
	/**
	 * Generates a thumbnail of Office files.
	 * 
	 * @param input		Input file that should be processed
	 * @param output	File in which should be written
	 * @throws IOException			If file cannot be read/written
	 * @throws ThumbnailerException If the thumbnailing process failed.
	 */
	@Override
	public void generateThumbnail(File input, File output) throws IOException, ThumbnailerException {
		// Connect on first use
//		if (!isConnected())
//			connect();

		File outputTmp = null;
		try {
			outputTmp = File.createTempFile("jodtemp", "." + getStandardOpenOfficeExtension());

			// Naughty hack to circumvent invalid URLs under windows (C:\\ ...)
			if (Platform.isWindows())
				input = new File(input.getAbsolutePath().replace("\\\\", "\\"));

			try {
				officeConverter.convert(input, outputTmp);
			} catch (OfficeException e) {
				throw new ThumbnailerException("Could not convert into OpenOffice-File", e);
			}
			if (outputTmp.length() == 0)
			{
				throw new ThumbnailerException("Could not convert into OpenOffice-File (file was empty)...");
			}

			ooo_thumbnailer.generateThumbnail(outputTmp, output);
		} finally {
			IOUtil.deleteQuietlyForce(outputTmp);
		}
	}

	/**
	 * Generate a Thumbnail of the input file.
	 * (Fix file ending according to MIME-Type).
	 * 
	 * @param input		Input file that should be processed
	 * @param output	File in which should be written
	 * @param mimeType	MIME-Type of input file (null if unknown)
	 * @throws IOException			If file cannot be read/written
	 * @throws ThumbnailerException If the thumbnailing process failed.
	 */
    @Override
	public void generateThumbnail(File input, File output, String mimeType) throws IOException, ThumbnailerException {
		String ext = FilenameUtils.getExtension(input.getName());
		if (!mimeTypeDetector.doesExtensionMatchMimeType(ext, mimeType))
		{
			String newExt;
			if ("application/zip".equals(mimeType))
				newExt = getStandardZipExtension();
			else if ("application/vnd.ms-office".equals(mimeType))
				newExt = getStandardOfficeExtension();
			else
				newExt = mimeTypeDetector.getStandardExtensionForMimeType(mimeType);
			
			input = temporaryFilesManager.createTempfileCopy(input, newExt);
		}

		generateThumbnail(input, output);
	}
	
	protected abstract String getStandardZipExtension();
	protected abstract String getStandardOfficeExtension();
	protected abstract String getStandardOpenOfficeExtension();

    @Override
	public void setImageSize(int thumbWidth, int thumbHeight, int imageResizeOptions) {
		super.setImageSize(thumbWidth, thumbHeight, imageResizeOptions);
		ooo_thumbnailer.setImageSize(thumbWidth, thumbHeight, imageResizeOptions);
	}
    
}
