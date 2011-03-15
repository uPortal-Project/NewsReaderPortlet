package org.jasig.portlet.newsreader.processor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/org/jasig/portlet/newsreader/processor/romeProcessorTestContext.xml")
public class RomeNewsProcessorImplTest {

    @Autowired(required = true)
    RomeNewsProcessorImpl processor;

    @Autowired(required = true)
    ApplicationContext context;
    
    Resource testFeed;
    
    @Before
    public void setUp() {
        testFeed = context.getResource("classpath:/org/jasig/portlet/newsreader/processor/testFeed.xml");
    }
    
    @Test
    public void testParsing() throws IOException, IllegalArgumentException, FeedException, PolicyException, ScanException {
        InputStream in = testFeed.getInputStream();
        SyndFeed feed = processor.getFeed(in);
        
        assertEquals(20, feed.getEntries().size());
        NewsFeedItem item = (NewsFeedItem) feed.getEntries().get(0);
        assertEquals("http://d.yimg.com/a/p/ap/20110310/capt.8a3eb82d06714371b5e5f23978453a1e-8a3eb82d06714371b5e5f23978453a1e-0.jpg?x=130&y=86&q=85&sig=PllYTfbufZhPAizTiij5GQ--", item.getImageUrl());
        
        in.close();
    }
    
}
