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
package org.eurekastreams.server.action.validation.stream;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for PostActivityCommentValidationStrategy.
 * 
 */
public class PostActivityCommentValidationStrategyTest
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
     * Local instance of the ServiceActionContext object assembled for tests.
     */
    private ServiceActionContext currentActionContext = context.mock(ServiceActionContext.class);

    /**
     * Comment DTO.
     */
    private CommentDTO comment = context.mock(CommentDTO.class);

    /**
     * System under test.
     */
    private PostActivityCommentValidationStrategy sut = new PostActivityCommentValidationStrategy();

    /**
     * Test.
     */
    @Test
    public void testSuccess()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(currentActionContext).getParams();
                will(returnValue(comment));

                oneOf(comment).getBody();
                will(returnValue("commentBody"));
            }
        });

        sut.validate(currentActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testEmptyCommentBody()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(currentActionContext).getParams();
                will(returnValue(comment));

                oneOf(comment).getBody();
                will(returnValue(""));
            }
        });

        sut.validate(currentActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testCommentBodyJustSpaces()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(currentActionContext).getParams();
                will(returnValue(comment));

                oneOf(comment).getBody();
                will(returnValue("   "));
            }
        });

        sut.validate(currentActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testNullContextParams()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(currentActionContext).getParams();
                will(returnValue(null));
            }
        });

        sut.validate(currentActionContext);
        context.assertIsSatisfied();
    }

}
