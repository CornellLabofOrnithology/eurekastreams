/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests inserting a stream bookmark.
 */
public class InsertStreamBookmarkExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private InsertStreamBookmarkExecution sut = null;

    /**
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Find person by ID mapper.
     */
    private FindByIdMapper findByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * Setup Fixtures.
     */
    @Before
    public final void setup()
    {
        sut = new InsertStreamBookmarkExecution(findByIdMapper);
    }

    /**
     * Test executing the action.
     */
    @Test
    public final void executeTest()
    {
        final Long personId = 2L;

        final Person person = context.mock(Person.class);
        
        final List<StreamScope> bookmarks = new ArrayList<StreamScope>();
        
        final StreamScope scopeToAdd = new StreamScope();

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(personId));

                oneOf(findByIdMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(person));
                
                oneOf(person).getBookmarks();
                will(returnValue(bookmarks));
                
                allowing(actionContext).getParams();
                will(returnValue(scopeToAdd));
            }
        });
        
        sut.execute(actionContext);
        
        Assert.assertTrue(bookmarks.contains(scopeToAdd));

        context.assertIsSatisfied();
    }
}