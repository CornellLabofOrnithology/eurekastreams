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
package org.eurekastreams.server.persistence.mappers.db;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.service.actions.strategies.ResourceFetcher;

/**
 * Mapper to retrieve theme url from db and convert it to theme css file.
 */
public class ThemeCssDbMapper implements DomainMapper<String, String>
{
    /**
     * Path to xslt resource.
     */
    private String xsltPath;

    /**
     * Fetcher for the XML theme definition.
     */
    private ResourceFetcher xmlFetcher = null;

    /**
     * Theme xml url mapper.
     */
    private DomainMapper<String, String> getThemeXmlUrlByUuidDbMapper;

    /**
     * Constructor.
     * 
     * @param inXsltPath
     *            Path to xslt resource.
     * @param inGetThemeXmlUrlByUuidDbMapper
     *            Theme xml url mapper.
     * @param inXmlFetcher
     *            Fetcher for the XML theme definition.
     */
    public ThemeCssDbMapper(final String inXsltPath, final DomainMapper<String, String> inGetThemeXmlUrlByUuidDbMapper,
            final ResourceFetcher inXmlFetcher)
    {
        xsltPath = inXsltPath;
        getThemeXmlUrlByUuidDbMapper = inGetThemeXmlUrlByUuidDbMapper;
        xmlFetcher = inXmlFetcher;
    }

    /**
     * Retrieve theme url from db and convert it to theme css file.
     * 
     * @param inRequest
     *            the theme uuid
     * @return css file for specified theme.
     */
    @Override
    public String execute(final String inRequest)
    {
        InputStream xsltInputStream = null;
        InputStream xmlInputStream = null;
        try
        {
            // grab theme url from db
            String themeUrl = getThemeXmlUrlByUuidDbMapper.execute(inRequest.toLowerCase());

            // Load the XSLT
            xsltInputStream = getClass().getResourceAsStream(xsltPath);
            Source xsltSource = new StreamSource(xsltInputStream);

            // Create the transformer
            TransformerFactory transFactory = TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transFactory.newTransformer(xsltSource);

            // Transform theme xml to css string
            StreamResult cssResult = new StreamResult(new StringWriter());
            xmlInputStream = xmlFetcher.getInputStream(themeUrl);
            transformer.transform(new StreamSource(xmlInputStream), cssResult);

            return cssResult.getWriter().toString();
        }
        catch (Exception e)
        {
            //@author yardmao-cm325 we dont care that we can't get themes, and for some reason
        								//requests coming from es to es are failing with 400
        	//throw new RuntimeException("Unable to retrieve theme css", e);
        	
        	//return default string css instead
        	return "<Theme><Title>Green Blocks</Title><Description>Green Blocks</Description><AuthorName>Eureka Streams</AuthorName><AuthorEmail>support@eurekastreams.org</AuthorEmail><HeaderBackground><HeaderBackgroundColor>white</HeaderBackgroundColor><HeaderForegroundColor>white</HeaderForegroundColor><HeaderBackgroundImage>/style/images/Start_page_4.jpg</HeaderBackgroundImage></HeaderBackground><ActiveTabBorderColor>#162d23</ActiveTabBorderColor><ActiveTabForegroundColor>white</ActiveTabForegroundColor><InactiveTabBackgroundColor>#355749</InactiveTabBackgroundColor><InactiveTabBorderColor>#162d23</InactiveTabBorderColor><InactiveTabForegroundColor>white</InactiveTabForegroundColor><GadgetTitleBackgroundColor>#5a8675</GadgetTitleBackgroundColor><GadgetTitleForegroundColor>white</GadgetTitleForegroundColor><GadgetBorderColor>#355749</GadgetBorderColor><PageBackground><PageBackgroundColor>#a4d1c1</PageBackgroundColor><PageBackgroundImage/></PageBackground></Theme>";
        }
        finally
        {
            try
            {
                if (xsltInputStream != null)
                {
                    xsltInputStream.close();
                }
                if (xmlInputStream != null)
                {
                    xmlInputStream.close();
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to close streams", e);
            }

        }
    }
}
