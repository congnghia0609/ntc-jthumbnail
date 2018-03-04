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

/**
 *
 * @author nghiatc
 * @since Apr 28, 2017
 */
public class NWordConverter extends NConverter{

    public NWordConverter(int port) {
        super(port);
    }

    @Override
    protected String getStandardZipExtension() {
        return "docx";
    }

    @Override
    protected String getStandardOfficeExtension() {
        return "doc";
    }

    @Override
    protected String getStandardOpenOfficeExtension() {
        return ".odt";
    }
    
    /**
     * Get a List of accepted File Types.
     * All Text Office Formats that OpenOffice understands are accepted.
     * (txt, rtf, doc, docx, wpd)
     * 
     * @return MIME-Types
     * @see http://www.artofsolving.com/opensource/jodconverter/guide/supportedformats
     */
    @Override
	public String[] getAcceptedMIMETypes() {
		return new String[]{
				"text/plain",
				"text/rtf",
/*				"application/msword", */
				"application/vnd.ms-word",
				"application/vnd.openxmlformats-officedocument.wordprocessingml",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				"application/wordperfect",
		/*		"application/vnd.ms-office", // doc?
				"application/zip" // docx? */
		};
	}
}
