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

package com.ntc.thumbnailer.common;

import com.ntc.configer.NConfig;

/**
 *
 * @author nghiatc
 * @since May 4, 2017
 */
public class Common {
    public static final String DIR_THUMB = NConfig.getConfig().getString("dbfiles.dir_thumb", "/data/files/thumb/");
    public static final String DIR_PDF = NConfig.getConfig().getString("dbfiles.dir_pdf", "/data/files/pdf/");
    
    
}
