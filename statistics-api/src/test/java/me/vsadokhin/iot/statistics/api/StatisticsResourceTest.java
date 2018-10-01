package me.vsadokhin.iot.statistics.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.domain.GetStatisticsRequest;
import me.vsadokhin.iot.data.exception.GetStatisticsException;
import me.vsadokhin.iot.data.exception.StorageException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class StatisticsResourceTest {

    private StatisticsResource statisticsResource;
    private MetricRepository mockMetricRepository;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockMetricRepository = mock(MetricRepository.class);
        statisticsResource = new StatisticsResource(mockMetricRepository);
    }

    @Test
    public void get_callMetricRepositoryGetStatistics() {
        // setup
        GetStatisticsRequest mockGetStatisticsRequest = mock(GetStatisticsRequest.class);
        
        // act
        statisticsResource.getStatistics(mockGetStatisticsRequest);
        
        // verify
        verify(mockMetricRepository).getStatistics(mockGetStatisticsRequest);
    }

    @Test
    public void get_checkResultBody() {
        // setup
        float value = 1.23F;
        when(mockMetricRepository.getStatistics(any())).thenReturn(value);

        // act
        ResponseEntity result = statisticsResource.getStatistics(new GetStatisticsRequest());

        // verify
        assertThat(result.getBody(), is(value));
    }

    @Test
    public void get_checkResultStatusCode() {
        // act
        ResponseEntity result = statisticsResource.getStatistics(new GetStatisticsRequest());

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void get_metricRepositoryThrowsStorageException_checkResultBody() {
        // setup
        String exceptionMessage = "Something bad happen";
        when(mockMetricRepository.getStatistics(any())).thenThrow(new StorageException(exceptionMessage, null));

        // act
        ResponseEntity result = statisticsResource.getStatistics(new GetStatisticsRequest());

        // verify
        assertThat(result.getBody(), is(exceptionMessage));
    }

    @Test
    public void get_metricRepositoryThrowsStorageException_checkResultStatusCode() {
        // setup
        when(mockMetricRepository.getStatistics(any())).thenThrow(new StorageException("message", null));

        // act
        ResponseEntity result = statisticsResource.getStatistics(new GetStatisticsRequest());

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void get_metricRepositoryThrowsGetStatisticsException_checkResultBody() {
        // setup
        String exceptionMessage = "Something bad happen";
        when(mockMetricRepository.getStatistics(any())).thenThrow(new GetStatisticsException(exceptionMessage));

        // act
        ResponseEntity result = statisticsResource.getStatistics(new GetStatisticsRequest());

        // verify
        assertThat(result.getBody(), is(exceptionMessage));
    }

    @Test
    public void get_metricRepositoryThrowsGetStatisticsException_checkResultStatusCode() {
        // setup
        when(mockMetricRepository.getStatistics(any())).thenThrow(new GetStatisticsException("error!"));

        // act
        ResponseEntity result = statisticsResource.getStatistics(new GetStatisticsRequest());

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
    
}