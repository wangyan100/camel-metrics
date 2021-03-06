package org.apache.camel.metrics;

import static org.apache.camel.metrics.MetricsComponent.HEADER_HISTOGRAM_VALUE;
import static org.apache.camel.metrics.MetricsComponent.HEADER_METRIC_NAME;
import static org.apache.camel.metrics.MetricsComponent.HEADER_PERFIX;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MetricsComponentRouteTest extends CamelTestSupport {

    @Produce(uri = "direct:start-1")
    protected ProducerTemplate template1;

    @Produce(uri = "direct:start-2")
    protected ProducerTemplate template2;

    @Test
    public void testMetrics() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        template1.sendBody(new Object());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMessageContentDelivery() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        String body = "Message Body";
        String header1 = "Header 1";
        String header2 = "Header 2";
        Object value1 = new Date();
        Object value2 = System.currentTimeMillis();
        mock.expectedBodiesReceived(body);
        mock.expectedHeaderReceived(header1, value1);
        mock.expectedHeaderReceived(header2, value2);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(header1, value1);
        headers.put(header2, value2);
        template1.sendBodyAndHeaders(body, headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testHeaderRemoval() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        Object body = new Object();
        Date now = new Date();

        mock.expectedBodiesReceived(body);
        mock.expectedHeaderReceived("." + HEADER_PERFIX, "value");
        mock.expectedHeaderReceived("date", now);

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(HEADER_METRIC_NAME, "a name");
        headers.put(HEADER_HISTOGRAM_VALUE, 34L);
        headers.put(HEADER_PERFIX + "notExistingHeader", "?");
        headers.put("." + HEADER_PERFIX, "value");
        headers.put("date", now);

        template2.sendBodyAndHeaders(body, headers);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start-1")
                        .to("metrics:timer:T?action=start")
                        .to("metrics:A")
                        .to("metrics:counter://B")
                        .to("metrics:counter:C?increment=19291")
                        .to("metrics:counter:C?decrement=19292")
                        .to("metrics:counter:C")
                        .to("metrics:meter:D")
                        .to("metrics:meter:D?mark=90001")
                        .to("metrics:histogram:E")
                        .to("metrics:timer:T")
                        .to("metrics:histogram:E?value=12000000031")
                        .to("metrics:timer:T?action=stop")
                        .to("mock:result");

                from("direct:start-2")
                        .to("metrics:meter:F?mark=88")
                        .to("mock:result");
            }
        };
    }
}
