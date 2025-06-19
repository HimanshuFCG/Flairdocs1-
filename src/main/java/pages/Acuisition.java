package pages;

import base.BasePage;
import com.microsoft.playwright.Page;

public class Acuisition extends BasePage   {
    public Acuisition(Page page){
        super(page);
    }

    public void acquisition(){
        page.click("//* span.rtbText:has-text('Acquisition')");
        page.waitForTimeout(1000);
    }

}
