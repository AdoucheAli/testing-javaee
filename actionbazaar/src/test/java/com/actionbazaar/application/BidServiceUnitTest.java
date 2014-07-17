package com.actionbazaar.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.actionbazaar.domain.Bid;
import com.actionbazaar.domain.BidRepository;
import com.actionbazaar.repository.MockBidDao;

@RunWith(Arquillian.class)
public class BidServiceUnitTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "actionbazaar-test.war")
                .addClasses(BidService.class, DefaultBidService.class,
                        BidRepository.class, MockBidDao.class, Bid.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @EJB
    private BidService bidService;

    @Test
    public void testAddBid() {
        // Save a new bid.
        Bid bid = new Bid();
        bid.setBidder("rrahman");
        bid.setItem("Test item");
        bid.setAmount(100.50);

        bidService.addBid(bid);

        // Make sure it was correctly saved.
        bid = bidService.getBid(1L);

        assertEquals("nrahman", bid.getBidder());
        assertEquals("Test item", bid.getItem());
        assertEquals(new Double(100.00), bid.getAmount());
    }

    @Test
    public void testUpdateBid() {
        // Update bid.
        Bid bid = bidService.getBid(1L);
        bid.setAmount(101.50);
        bidService.updateBid(bid);

        // Make sure bid was updated.
        bid = bidService.getBid(1L);

        assertEquals("nrahman", bid.getBidder());
        assertEquals("Test item", bid.getItem());
        assertEquals(new Double(100.00), bid.getAmount());
    }

    @Test
    public void testDeleteBid() {
        Bid bid = bidService.getBid(1L);
        bidService.deleteBid(bid);

        assertNotNull(bidService.getBid(1L));
    }
}
