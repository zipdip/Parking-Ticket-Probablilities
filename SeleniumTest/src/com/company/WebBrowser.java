package com.company;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class WebBrowser {
    private WebDriver driver;
    private final String defaultAddress;

    //Constructor
    //Input: Default Address
    //Output: Instance of WebBrowser
    public WebBrowser(String address){
        defaultAddress = address;
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\zachz\\IdeaProjects\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    //searchByCase
    //Inputs: case number as String
    //Outputs: List<String> that = [date, time, address, reason]
    public List<String> searchByCase(String caseNo) throws Exception{
        List<String> res = new ArrayList<String>();
        //Attempt to reset driver if cannot go to search by case field
        if (!goToCaseSearch()) {
            resetDriver();
            if (!goToCaseSearch())
                throw new Exception("CannotAccessSearchByCase");
        }
        WebElement searchBox = driver.findElement(By.name("searchForm$caseNumberCtl"));
        searchBox.sendKeys(caseNo);
        WebElement sumbitBtn = driver.findElement(By.name("searchForm$submitCmd"));
        sumbitBtn.click();

        //go to catch if there is no data presented
        try {
            List<WebElement> allRows = driver.findElement(By.id("casesCtl_gridCtl")).findElements(By.tagName("tr"));
            for (WebElement row : allRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() > 0 && cells.get(0).getText().equals(caseNo)) {
                    res.add(cells.get(4).getText());    //date
                    res.add(cells.get(5).getText());    //time
                    res.add(cells.get(6).getText());    //address
                    res.add(cells.get(3).getText());    //reason
                    break;
                }
            }
        }catch (org.openqa.selenium.NoSuchElementException e){
            e.printStackTrace();
            return res;
        }
        return res;
    }

    //goToCaseSearch()
    //Inputs: None
    //Outputs: True if successfully reached the search by case page, false otherwise
    private boolean goToCaseSearch(){
        try{
            driver.get(defaultAddress);
            WebElement currLink = driver.findElement(By.linkText("Parking and Red Light Camera Search"));
            currLink.click();
            currLink = driver.findElement(By.linkText("By Case"));
            currLink.click();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //resetDriver
    //Inputs: none
    //Outputs: none
    //This method closes the current window then creates a new ChromeDriver
    public void resetDriver(){
        driver.close();
        driver = new ChromeDriver();
    }

    //Close the browser
    public void closeBrowser(){
        driver.close();
    }
}
