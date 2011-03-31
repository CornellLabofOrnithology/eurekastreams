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
package org.eurekastreams.web.client.ui.pages.widget;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.model.PersonalInformationModel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarDisplayPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Background;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The Eureka Connect "profile badge widget" - displays a person's avatar and information.
 */
public class UserProfileBadgeWidget extends Composite
{
    /**
     * Constructor.
     * 
     * @param accountId
     *            Unique ID of person to display.
     */
    public UserProfileBadgeWidget(final String accountId)
    {
        final FlowPanel widget = new FlowPanel();
        initWidget(widget);

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        widget.add(new AvatarDisplayPanel(EntityType.PERSON, event.getResponse().getEntityId(), event
                                .getResponse().getAvatarId(), Size.Normal, Background.White));
                    }
                });

        PersonalInformationModel.getInstance().fetch(accountId, false);
    }
}
