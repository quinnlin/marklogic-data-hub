import {  browser, ExpectedConditions as EC} from 'protractor';
import loginPage from '../../page-objects/auth/login';
import settingsPage from '../../page-objects/settings/settings';
import appPage from '../../page-objects/appPage';
const fs = require('fs-extra');

export default function(tmpDir) {
  describe('Uninstall', () => {
    it ('should go to the settings page', function() {
      appPage.settingsTab.click();
      settingsPage.isLoaded();
    });

    it ('should click the uninstall button', function() {
      settingsPage.uninstallButton.click();
      browser.wait(EC.elementToBeClickable(settingsPage.uninstallConfirmation));
      settingsPage.uninstallConfirmation.click();
    });

    it ('should show the uninstall progress bar', function() {
      browser.wait(EC.visibilityOf(settingsPage.uninstallStatus));
      expect(settingsPage.uninstallStatus.isDisplayed()).toBe(true);
    });

    it ('should uninstall the hub', function() {
      loginPage.isLoadedWithtimeout(200000);
    });

    it ('should remove the temp folder', function() {
      fs.remove(tmpDir);
    });
  });
}
