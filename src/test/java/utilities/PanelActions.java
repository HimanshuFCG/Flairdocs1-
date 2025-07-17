    package utilities;

    import com.microsoft.playwright.Page;
    import com.microsoft.playwright.Locator;
    import org.apache.log4j.Logger;
    import com.microsoft.playwright.options.WaitForSelectorState;

    public class PanelActions {
        
            private final Page page;
            private static final Logger log = Logger.getLogger(PanelActions.class);
        
            public PanelActions(Page page) {
                this.page = page;
            }
        
            public void openPanel(String panelTitle) {
                try {
                    if ("Project Information".equals(panelTitle)) {
                        Locator expandBtn = page.locator("#ctl00_Main_DynamicContent1_projinfo_AcqProjectProfile_ColProjectprofile____img");
                        expandBtn.waitFor(new Locator.WaitForOptions().setTimeout(30000).setState(WaitForSelectorState.VISIBLE));
                        if (expandBtn.isVisible() && expandBtn.isEnabled()) {
                            expandBtn.click(new Locator.ClickOptions().setForce(true));
                            log.info("Clicked expand for 'Project Information'");
                        } else {
                            log.warn("Expand button for 'Project Information' not visible or not enabled");
                            throw new RuntimeException("Expand button for 'Project Information' not visible or not enabled");
                        }
                        Locator panel = page.locator("#ctl00_Main_DynamicContent1_projinfo_AcqProjectProfile_ColProjectprofile____title");
                        page.waitForFunction(
                            "el => !el.className.includes('panelclosed')",
                            panel.first(),
                            new Page.WaitForFunctionOptions().setTimeout(30000)
                        );
                        page.waitForSelector(".loading", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(20000));
                        log.info("Panel 'Project Information' is fully open.");
                    } else {
                        // Check if panel is already open
                        Locator openPanel = page.locator("div.collapsible-panel-main-open-close:not(.panelclosed)", new Page.LocatorOptions().setHasText(panelTitle));
                        if (openPanel.count() > 0) {
                            log.info("Panel '" + panelTitle + "' is already open. Skipping expand.");
                            return;
                        }
                        // Otherwise, find the collapsed panel and expand
                        Locator collapsedPanel = page.locator("div.collapsible-panel-main-open-close.panelclosed", new Page.LocatorOptions().setHasText(panelTitle));
                        collapsedPanel.waitFor(new Locator.WaitForOptions().setTimeout(30000).setState(WaitForSelectorState.VISIBLE));
                        if (collapsedPanel.first().isVisible() && collapsedPanel.first().isEnabled()) {
                            collapsedPanel.first().click(new Locator.ClickOptions().setForce(true));
                            log.info("Clicked expand for panel: " + panelTitle);
                        } else {
                            log.warn("Expand button for panel '" + panelTitle + "' not visible or not enabled");
                            throw new RuntimeException("Expand button for panel '" + panelTitle + "' not visible or not enabled");
                        }
                        Locator openPanelLocator = page.locator("div.collapsible-panel-main-open-close", new Page.LocatorOptions().setHasText(panelTitle));
                        page.waitForFunction(
                            "el => !el.className.includes('panelclosed')",
                            openPanelLocator.first(),
                            new Page.WaitForFunctionOptions().setTimeout(30000)
                        );
                        page.waitForSelector(".loading", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(20000));
                        log.info("Panel '" + panelTitle + "' is fully open.");
                    }
                } catch (Exception e) {
                    log.error("Could not open panel '" + panelTitle + "' - " + e.getMessage());
                    throw new RuntimeException("Could not open panel '" + panelTitle + "'", e);
                }
            }
            
            public void closePanel(String panelTitle) {
                try {
                    if ("Project Information".equals(panelTitle)) {
                        Locator collapseBtn = page.locator("#ctl00_Main_DynamicContent1_projinfo_AcqProjectProfile_ColProjectprofile____img");
                        collapseBtn.waitFor(new Locator.WaitForOptions().setTimeout(30000).setState(WaitForSelectorState.VISIBLE));
                        if (collapseBtn.isVisible() && collapseBtn.isEnabled()) {
                            collapseBtn.click(new Locator.ClickOptions().setForce(true));
                            log.info("Clicked collapse for 'Project Information'");
                        } else {
                            log.warn("Collapse button for 'Project Information' not visible or not enabled");
                            throw new RuntimeException("Collapse button for 'Project Information' not visible or not enabled");
                        }
                        Locator panel = page.locator("#ctl00_Main_DynamicContent1_projinfo_AcqProjectProfile_ColProjectprofile____title");
                        page.waitForFunction(
                            "el => el.className.includes('panelclosed')",
                            panel.first(),
                            new Page.WaitForFunctionOptions().setTimeout(30000)
                        );
                        log.info("Panel 'Project Information' is fully closed.");
                    } else {
                        Locator panel = page.locator("div.collapsible-panel-main-open-close:not(.panelclosed)", new Page.LocatorOptions().setHasText(panelTitle));
                        panel.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
                        if (panel.first().isVisible() && panel.first().isEnabled()) {
                            panel.first().click(new Locator.ClickOptions().setForce(true));
                            log.info("Clicked collapse for panel: " + panelTitle);
                        } else {
                            log.warn("Collapse button for panel '" + panelTitle + "' not visible or not enabled");
                            throw new RuntimeException("Collapse button for panel '" + panelTitle + "' not visible or not enabled");
                        }
                        Locator closedPanel = page.locator("div.collapsible-panel-main-open-close", new Page.LocatorOptions().setHasText(panelTitle));
                        page.waitForFunction(
                            "el => el.className.includes('panelclosed')",
                            closedPanel.first(),
                            new Page.WaitForFunctionOptions().setTimeout(30000)
                        );
                        log.info("Panel '" + panelTitle + "' is fully closed.");
                    }
                } catch (Exception e) {
                    log.error("Could not close panel '" + panelTitle + "' - " + e.getMessage());
                    throw new RuntimeException("Could not close panel '" + panelTitle + "'", e);
                }
            }
        }
