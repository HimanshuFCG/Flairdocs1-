package pages;
import com.microsoft.playwright.Page;

public class LoginPage {
    public Page page;

    public LoginPage(Page page){
        this.page = page;
    }

    public void login(){
        page.fill("//*[@id='LoginFlairdocs_UserName']", "david");
        page.fill("//*[@id='LoginFlairdocs_Password']", "fdrow20@0");
        page.click("//*[@id='LoginFlairdocs_LoginButton']");
        page.waitForTimeout(1000);
    }
    
}
