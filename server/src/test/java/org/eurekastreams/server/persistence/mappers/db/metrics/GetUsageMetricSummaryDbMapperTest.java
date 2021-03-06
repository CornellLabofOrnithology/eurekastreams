/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.UsageMetricStreamSummaryRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetUsageMetricSummaryDbMapper.
 * 
 */
public class GetUsageMetricSummaryDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private final GetUsageMetricSummaryDbMapper sut = new GetUsageMetricSummaryDbMapper();

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary").executeUpdate();
        getEntityManager().flush();

        List<DailyUsageSummary> results = sut.execute(new UsageMetricStreamSummaryRequest(3, null));
        assertEquals(0, results.size());
    }

    /**
     * Test.
     */
    @Test
    public void testValues()
    {
        final long nowInMs = new Date().getTime();
        final long msInDay = 86400000L;

        final long count = 15;
        // clear table.
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary").executeUpdate();
        getEntityManager().flush();

        // put in two records, one with 10 for values, one with 20
        for (int i = 1; i <= 2; i++)
        {
            DailyUsageSummary foo = new DailyUsageSummary();
            foo.setMessageCount(count * i);
            foo.setPageViewCount(count * i);
            foo.setStreamContributorCount(count * i);
            foo.setStreamViewCount(count * i);
            foo.setStreamViewerCount(count * i);
            foo.setUniqueVisitorCount(count * i);
            foo.setUsageDate(new Date(nowInMs - i * msInDay));
            foo.setAvgActivityResponseTime(count * i);
            foo.setUsageDateTimeStampInMs(1308238511000L);
            foo.setStreamViewStreamScopeId(null);

            getEntityManager().persist(foo);
            getEntityManager().flush();
            assertTrue(foo.getId() > 0);
        }

        // throw in some specific stream stats that will be ignored
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(nowInMs - 1 * msInDay), 1308238511000L, 3L, 1L,
                        2L, 5L));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(nowInMs - 1 * msInDay), 1308238511000L, 4L, 1L,
                        2L, 5L));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(nowInMs - 1 * msInDay), 1308238511000L, 5L, 1L,
                        2L, 5L));

        getEntityManager().flush();
        getEntityManager().clear();

        // execute sut - 10 day window
        final int ten = 10;
        List<DailyUsageSummary> results = sut.execute(new UsageMetricStreamSummaryRequest(ten, null));

        // verfiy row count and averages.
        assertEquals(2, results.size());
        assertEquals(count * 2, results.get(0).getMessageCount());
        assertEquals(count * 1, results.get(1).getMessageCount());
    }

    /**
     * Test.
     */
    @Test
    public void testValuesForSpecificStream()
    {
        final long nowInMs = new Date().getTime();
        final long msInDay = 86400000L;

        final long count = 100;

        final long streamViewScopeId = 372L;

        // clear table.
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary").executeUpdate();
        getEntityManager().flush();

        // put in two records, one with 100 for values, one with 200
        for (int i = 1; i <= 2; i++)
        {
            DailyUsageSummary foo = new DailyUsageSummary();
            foo.setMessageCount(count * i);
            foo.setPageViewCount(count * i);
            foo.setStreamContributorCount(count * i);
            foo.setStreamViewCount(count * i);
            foo.setStreamViewerCount(count * i);
            foo.setUniqueVisitorCount(count * i);
            foo.setUsageDate(new Date(nowInMs - (8 * 2 + i) * msInDay));
            foo.setAvgActivityResponseTime(count * i);
            foo.setStreamViewStreamScopeId(streamViewScopeId);
            foo.setUsageDateTimeStampInMs(1308238511000L);

            getEntityManager().persist(foo);
            getEntityManager().flush();
            assertTrue(foo.getId() > 0);
        }

        // throw in some stats that don't pertain to this stream that will be ignored
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(nowInMs - 6 * msInDay), 1308238511000L,
                        streamViewScopeId + 1, 1L, 2L, 5L));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(nowInMs - 7 * msInDay), 1308238511000L,
                        streamViewScopeId + 1, 1L, 2L, 5L));
        getEntityManager().persist(
                new DailyUsageSummary(1, 2, 3, 4, 5, 6, 7, new Date(nowInMs - 8 * msInDay), 1308238511000L, null, 1L,
                        2L, 5L));

        getEntityManager().flush();
        getEntityManager().clear();

        // execute sut.
        final int twenty = 20;
        List<DailyUsageSummary> results = sut.execute(new UsageMetricStreamSummaryRequest(twenty, streamViewScopeId));

        // verfiy row count and averages.
        assertEquals(2, results.size());
        assertEquals(count * 2, results.get(0).getMessageCount());
        assertEquals(count * 1, results.get(1).getMessageCount());
    }

}
