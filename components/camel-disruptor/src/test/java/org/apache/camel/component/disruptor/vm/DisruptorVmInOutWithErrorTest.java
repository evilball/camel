/**
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
package org.apache.camel.component.disruptor.vm;

import org.junit.Test;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.vm.AbstractVmTestSupport;

/**
 * @version
 */
public class DisruptorVmInOutWithErrorTest extends AbstractVmTestSupport {

    @Test
    public void testInOutWithError() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);

        try {
            template2.requestBody("direct:start", "Hello World", String.class);
            fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            assertEquals("Damn I cannot do this", e.getCause().getMessage());
        }

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("disruptor-vm:foo").transform(constant("Bye World"))
                        .throwException(new IllegalArgumentException("Damn I cannot do this"))
                        .to("mock:result");
            }
        };
    }

    @Override
    protected RouteBuilder createRouteBuilderForSecondContext() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to("disruptor-vm:foo");
            }
        };
    }
}