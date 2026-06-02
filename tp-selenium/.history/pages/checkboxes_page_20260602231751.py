from selenium.webdriver.common.by import By


class CheckboxesPage:

    URL = "/checkboxes"
    CHECKBOXES = (By.CSS_SELECTOR, "input[type='checkbox']")

    def __init__(self, driver, base_url):
        self.driver   = driver
        self.base_url = base_url

    def open(self):
        self.driver.get(self.base_url + self.URL)
        return self

    def get_checkboxes(self):
        return self.driver.find_elements(*self.CHECKBOXES)

    def est_cochee(self, index):
        return self.get_checkboxes()[index].is_selected()

    def cocher(self, index):
        cb = self.get_checkboxes()[index]
        if not cb.is_selected():
            cb.click()
        return self

    def decocher(self, index):
        cb = self.get_checkboxes()[index]
        if cb.is_selected():
            cb.click()
        return self