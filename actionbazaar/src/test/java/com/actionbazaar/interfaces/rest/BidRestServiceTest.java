package com.actionbazaar.interfaces.rest;

import com.actionbazaar.application.BidService;
import com.actionbazaar.application.DefaultBidService;
import com.actionbazaar.domain.Bid;
import com.actionbazaar.domain.BidRepository;
import com.actionbazaar.infrastructure.database.DefaultBidRepository;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BidRestServiceTest {

    private static Long bidId;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "actionbazaar-test.war")
                .addClasses(BidRestService.class, RestConfiguration.class,
                        BidService.class, DefaultBidService.class,
                        BidRepository.class, DefaultBidRepository.class, Bid.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("test-weblogic.xml", "weblogic.xml")
                .addAsResource("test-persistence.xml",
                        "META-INF/persistence.xml");
    }

    @Test
    @InSequence(1)
    public void testAddBid() {
        WebTarget target = ClientBuilder.newClient()
                .target("http://localhost:7001/actionbazaar-test/rest/bids");
        // Save a new bid.
        Bid bid = new Bid();

        bid.setBidder("nrahman");
        bid.setItem("Test item");
        bid.setAmount(130.75);

        bid = target.request("application/json").post(Entity.json(bid), Bid.class);

        // Make sure it was correctly saved.
        bidId = bid.getId();

        bid = target.path("{id}").resolveTemplate("id", bidId)
                .request("application/json").get(Bid.class);

        assertEquals("nrahman", bid.getBidder());
        assertEquals("Test item", bid.getItem());
        assertEquals(new Double(130.75), bid.getAmount());
    }

    @Test
    @InSequence(2)
    public void testUpdateBid() {
        WebTarget target = ClientBuilder.newClient()
                .target("http://localhost:7001/actionbazaar-test/rest/bids/{id}")
                .resolveTemplate("id", bidId);

        // Update bid.
        Bid bid = target.request("application/json").get(Bid.class);

        bid.setAmount(150.50);

        target.request().put(Entity.json(bid));

        // Make sure bid was updated.
        bid = target.request("application/json").get(Bid.class);

        assertEquals("nrahman", bid.getBidder());
        assertEquals("Test item", bid.getItem());
        assertEquals(new Double(150.50), bid.getAmount());
    }

    @Test
    @InSequence(3)
    public void testDeleteBid() {
        WebTarget target = ClientBuilder.newClient()
                .target("http://localhost:7001/actionbazaar-test/rest/bids/{id}")
                .resolveTemplate("id", bidId);

        target.request().delete();

        assertNull(target.request("application/json").get(Bid.class));
    }
}
