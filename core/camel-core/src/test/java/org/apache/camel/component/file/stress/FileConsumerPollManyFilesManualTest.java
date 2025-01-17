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
package org.apache.camel.component.file.stress;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

@Disabled("Manual test")
public class FileConsumerPollManyFilesManualTest extends ContextTestSupport {

    private static final int FILES = 200;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        // do not test on windows
        assumeFalse(isPlatform("windows"));

        super.setUp();

        // create files
        for (int i = 0; i < FILES; i++) {
            template.sendBodyAndHeader(fileUri(), "Message " + i, Exchange.FILE_NAME, "file-" + i + ".txt");
        }
    }

    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }

    @Test
    public void testPollManyFiles() throws Exception {
        // do not test on windows
        assumeFalse(isPlatform("windows"));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(fileUri("?delete=true")).convertBodyTo(String.class).to("mock:result");
            }
        });
        context.start();

        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(FILES);

        assertMockEndpointsSatisfied();
    }

}
