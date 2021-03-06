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
public class NExcelConverter extends NConverter {

    public NExcelConverter(int port) {
        super(port);
    }

    @Override
    protected String getStandardZipExtension() {
        return "xlsx";
    }

    @Override
    protected String getStandardOfficeExtension() {
        return "xls";
    }

    @Override
    protected String getStandardOpenOfficeExtension() {
        return "ods";
    }

    /**
     * Get a List of accepted File Types.
     * All Spreadsheet Office Formats that OpenOffice understands are accepted.
     * 
     * @return MIME-Types
     * @see http://www.artofsolving.com/opensource/jodconverter/guide/supportedformats
     */
    @Override
	public String[] getAcceptedMIMETypes() {
		return new String[]{
				"application/vnd.ms-excel",
				"application/vnd.openxmlformats-officedocument.spreadsheetml",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
		/*		"application/vnd.ms-office", // xls?
				"application/zip" // xlsx? */
		};
	}
}
