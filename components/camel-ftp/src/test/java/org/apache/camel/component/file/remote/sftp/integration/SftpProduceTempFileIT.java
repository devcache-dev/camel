/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.file.remote.sftp.integration;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIf(value = "org.apache.camel.component.file.remote.services.SftpEmbeddedService#hasRequiredAlgorithms")
public class SftpProduceTempFileIT extends SftpServerTestSupport {

    @Test
    public void testSftpTempFile() {
        template.sendBodyAndHeader("sftp://localhost:{{ftp.server.port}}/{{ftp.root.dir}}"
                                   + "?username=admin&password=admin&tempFileName=temp-${file:name}",
                "Hello World",
                Exchange.FILE_NAME, "hello.txt");

        File file = ftpFile("hello.txt").toFile();
        assertTrue(file.exists(), "File should exist: " + file);
        assertEquals("Hello World", context.getTypeConverter().convertTo(String.class, file));
    }

    @Test
    public void testSftpTempFileNoStartingPath() {
        template.sendBodyAndHeader(
                "sftp://localhost:{{ftp.server.port}}/?username=admin&password=admin&tempFileName=temp-${file:name}",
                "Hello World", Exchange.FILE_NAME,
                "hello.txt");

        File file = new File("hello.txt");
        assertTrue(file.exists(), "File should exist: " + file);
        assertEquals("Hello World", context.getTypeConverter().convertTo(String.class, file));

        // delete file when we are done testing
        FileUtil.deleteFile(file);
    }

}
