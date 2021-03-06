package org.apache.camel.metrics.timer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.apache.camel.Producer;
import org.apache.camel.metrics.timer.TimerEndpoint.TimerAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.codahale.metrics.MetricRegistry;

@RunWith(MockitoJUnitRunner.class)
public class TimerEndpointTest {

    private static final String METRICS_NAME = "metrics.name";

    @Mock
    private MetricRegistry registry;

    private TimerEndpoint endpoint;

    private InOrder inOrder;

    @Before
    public void setUp() throws Exception {
        endpoint = new TimerEndpoint(registry, METRICS_NAME);
        inOrder = Mockito.inOrder(registry);
    }

    @After
    public void tearDown() throws Exception {
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testTimerEndpoint() throws Exception {
        assertThat(endpoint, is(notNullValue()));
        assertThat(endpoint.getRegistry(), is(registry));
        assertThat(endpoint.getMetricsName(), is(METRICS_NAME));
    }

    @Test
    public void testCreateProducer() throws Exception {
        Producer producer = endpoint.createProducer();
        assertThat(producer, is(notNullValue()));
        assertThat(producer, is(instanceOf(TimerProducer.class)));
    }

    @Test
    public void testGetAction() throws Exception {
        assertThat(endpoint.getAction(), is(nullValue()));
    }

    @Test
    public void testSetAction() throws Exception {
        assertThat(endpoint.getAction(), is(nullValue()));
        endpoint.setAction(TimerAction.start);
        assertThat(endpoint.getAction(), is(TimerAction.start));
    }

    @Test
    public void testCreateEndpointUri() throws Exception {
        assertThat(endpoint.createEndpointUri(), is(TimerEndpoint.ENDPOINT_URI));
    }
}
