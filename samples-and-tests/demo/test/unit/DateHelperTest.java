package unit;

import java.util.Date;

import org.junit.Test;

import play.test.UnitTest;
import util.DateHelper;

public class DateHelperTest extends UnitTest {

    private static final String BIRTHDAY = "2012-08-22 16:35:48";

    @Test
    public void testBeforeMinuts() {
        long currentTime = new Date().getTime();
        Date beforeTime = DateHelper.beforeMinuts(new Date(), 1);
        long OneDaysBeforeTime = beforeTime.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 59 * 1000);
    }

    @Test
    public void testBeforeMinutsByString() {
        long currentTime = DateHelper.t(BIRTHDAY).getTime();
        Date beforeTime = DateHelper.beforeMinuts(BIRTHDAY, 1);
        long OneDaysBeforeTime = beforeTime.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 59 * 1000);
    }

    @Test
    public void testAfterMinuts() {
        long currentTime = new Date().getTime();
        long afterTime = DateHelper.afterMinuts(new Date(), 1).getTime();
        assertTrue((afterTime - currentTime) > 59 * 1000);
    }

    @Test
    public void testAfterMinutsByString() {
        long currentTime = DateHelper.t(BIRTHDAY).getTime();
        long afterTime = DateHelper.afterMinuts(BIRTHDAY, 1).getTime();
        assertTrue((afterTime - currentTime) > 59 * 1000);
    }

    @Test
    public void testBeforeHours() {
        long currentTime = new Date().getTime();
        Date beforeTime = DateHelper.beforeHours(new Date(), 1);
        long OneDaysBeforeTime = beforeTime.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 59 * 60 * 1000);
    }

    @Test
    public void testBeforeHoursByString() {
        long currentTime = DateHelper.t(BIRTHDAY).getTime();
        Date beforeTime = DateHelper.beforeHours(BIRTHDAY, 1);
        long OneDaysBeforeTime = beforeTime.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 59 * 60 * 1000);
    }

    @Test
    public void testAfterHours() {
        long currentTime = new Date().getTime();
        long afterTime = DateHelper.afterHours(new Date(), 1).getTime();
        assertTrue((afterTime - currentTime) > 59 * 60 * 1000);
    }

    @Test
    public void testAfterHoursByString() {
        long currentTime = DateHelper.t(BIRTHDAY).getTime();
        long afterTime = DateHelper.afterHours(BIRTHDAY, 1).getTime();
        assertTrue((afterTime - currentTime) > 59 * 60 * 1000);
    }

    @Test
    public void testBeforeDays() {
        long currentTime = new Date().getTime();
        Date beforeDays = DateHelper.beforeDays(new Date(), 1);
        long OneDaysBeforeTime = beforeDays.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 23 * 60 * 60 * 1000);
    }

    @Test
    public void testBeforeDaysByString() {
        long currentTime = DateHelper.t(BIRTHDAY).getTime();
        Date beforeDays = DateHelper.beforeDays(BIRTHDAY, 1);
        long OneDaysBeforeTime = beforeDays.getTime();
        assertTrue((currentTime - OneDaysBeforeTime) > 23 * 60 * 60 * 1000);
    }

    @Test
    public void testAfterDays() {
        long currentTime = new Date().getTime();
        long OneDaysAfterTime = DateHelper.afterDays(new Date(), 1).getTime();
        assertTrue((OneDaysAfterTime - currentTime) > 23 * 60 * 60 * 1000);
    }

    @Test
    public void testAfterDaysByString() {
        long currentTime = DateHelper.t(BIRTHDAY).getTime();
        long OneDaysAfterTime = DateHelper.afterDays(BIRTHDAY, 1).getTime();
        assertTrue((OneDaysAfterTime - currentTime) > 23 * 60 * 60 * 1000);
    }

    @Test
    public void testGetTimeFromString() throws Exception {
        Date date1 = DateHelper.t(BIRTHDAY);
        assertNotNull(date1);
        Date date2 = DateHelper.t("2012-08-22 16:35");
        assertNotNull(date2);
        Date date3 = DateHelper.t("2012-08-22");
        assertNotNull(date3);
    }

    @Test(expected = RuntimeException.class)
    public void testGetTimeFromStringFail() throws Exception {
        DateHelper.t("Aug 22, 2012");
    }
}
