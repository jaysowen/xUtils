/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lidroid.xutils.http.client.callback;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringDownloadHandler {

    public Object handleEntity(HttpEntity entity, RequestCallBackHandler callback, String charset) throws IOException {
        if (entity == null) return null;

        long total = entity.getContentLength();
        long current = 0;

        if (callback != null && !callback.updateProgress(total, current, true)) {
            return null;
        }

        InputStream ins = null;
        StringBuilder sb = new StringBuilder();
        try {
            ins = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins, charset));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                current += line.getBytes(charset).length;
                if (callback != null) {
                    if (!callback.updateProgress(total, current, false)) {
                        throw new IOException("stop");
                    }
                }
            }
            if (callback != null) {
                callback.updateProgress(total, current, true);
            }
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (Exception e) {
                }
            }
        }
        return sb.toString();
    }

}
