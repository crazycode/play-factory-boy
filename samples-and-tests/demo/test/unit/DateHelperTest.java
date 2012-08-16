package unit;

import java.util.Date;
import org.junit.Test;
import play.test.UnitTest;
import util.DateHelper;

public class DateHelperTest extends UnitTest {


    @Test
    public void testBeforeMinuts() {
        long currentTime = new Date().getTime();
        Date beforeTime = DateHelper.beforeMinuts(new Date(), 1);
        long OneDaysBeforeTime = beforeTime.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 59*1000);
    }
    
    @Test
    public void testAfterMinuts() {
        long currentTime = new Date().getTime();
        long afterTime = DateHelper.afterMinuts(new Date(), 1).getTime();
        assertTrue((afterTime - currentTime) > 59*1000); 
    }
    
    
    @Test
    public void testBeforeHours() {
        long currentTime = new Date().getTime();
        Date beforeTime = DateHelper.beforeHours(new Date(), 1);
        long OneDaysBeforeTime = beforeTime.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 59*60*1000); 
    }
    
    @Test
    public void testAfterHours() {
        long currentTime = new Date().getTime();
        long afterTime = DateHelper.afterHours(new Date(), 1).getTime();
        assertTrue((afterTime - currentTime) > 59*60*1000); 
    }
    
    @Test
    public void testBeforeDays() {
        long currentTime = new Date().getTime();
        Date beforeDays = DateHelper.beforeDays(new Date(), 1);
        long OneDaysBeforeTime = beforeDays.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 23*60*60*1000);
    }
    
    @Test
    public void testAfterDays() {
        long currentTime = new Date().getTime();
        long OneDaysAfterTime = DateHelper.afterDays(new Date(), 1).getTime();
        assertTrue((OneDaysAfterTime - currentTime) > 23*60*60*1000); 
    }
}
