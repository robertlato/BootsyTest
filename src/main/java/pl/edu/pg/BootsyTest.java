package pl.edu.pg;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;


public class BootsyTest {

    public static void main(String[] args) {
//        System.setProperty("webdriver.gecko.driver", "/home/robertlato/Pobrane/geckodriver");
        System.setProperty("webdriver.gecko.driver", "src/main/resources/binaries/geckodriver");
        WebDriver driver = new FirefoxDriver();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            driver.get("https://localhost/index.php");
            driver.manage().window().maximize();

            dodajProdukty(driver, wait, 10);
            usunProdukt(driver);
            rejestrujUzytkownika(driver);
            zatwierdzZamowienie(driver);
            sprawdzStatusZamowienia(driver);

        } catch (InterruptedException  | NoSuchElementException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static void dodajProdukty(WebDriver driver, WebDriverWait wait, int iloscProduktowDoDodania) throws NoSuchElementException, InterruptedException {

//        int liczbaKategorii = driver.findElements(By.className("category")).size();
        int liczbaKategorii = driver.findElements(By.xpath("/html/body/main/header/div[2]/div/div[1]/div[2]/div[1]/ul/li")).size();
        // sout liczba kategorii
//        System.out.println(driver.findElements(By.xpath("/html/body/main/header/div[2]/div/div[1]/div[2]/div[1]/ul/li")).size());
        if (liczbaKategorii != 0) {
            int dodaneProdukty = 0;
            int numerKategorii = 0;


            while (dodaneProdukty < iloscProduktowDoDodania) {
//                driver.findElements(By.className("category")).get(numerKategorii).click();
                driver.findElements(By.xpath("/html/body/main/header/div[2]/div/div[1]/div[2]/div[1]/ul/li")).get(numerKategorii).click();

                int iloscProduktowWKategorii = driver.findElements(By.cssSelector("article.product-miniature")).size();
                int iloscDodanychProduktowZKategorii = 0;

                for (int i = 0; (i < iloscProduktowWKategorii && iloscDodanychProduktowZKategorii < (iloscProduktowDoDodania / 2) && dodaneProdukty < iloscProduktowDoDodania); i++) {
                    //przejdz do produktu
                    wait.until(ExpectedConditions.visibilityOf(driver.findElements(By.cssSelector("article.product-miniature")).get(i)));
                    driver.findElements(By.cssSelector("article.product-miniature")).get(i).click();
//                    System.out.println("Siegam po produkt numer: " + i);
//                    System.out.println("Ilosc produktow w kategorii: " + iloscProduktowWKategorii);

                    // przejdz do dodawania produktu, jezeli nie jest on oznaczony jako
                    // "ostatnie produkty" lub "niedostępny" - czyli z mala iloscia na stanie
                    if (driver.findElements(By.className("product-last-items")).size() == 0 &&
                            driver.findElements(By.className("product-unavailable")).size() == 0) {
                        //dodaj produkt do koszyka
                        //modyfikuj ilosc dodawanych produktow
                        for (int j = 0; j < iloscDodanychProduktowZKategorii; j++) {
                            driver.findElement(By.className("bootstrap-touchspin-up")).click();

                            // przerwij dodawanie, jezeli pokaze sie komunikat o braku lub malej ilosci produktu
                            if (driver.findElements(By.className("product-last-items")).size() != 0 &&
                                    driver.findElements(By.className("product-unavailable")).size() != 0) {
                                break;
                            }
                        }
                        Thread.sleep(2000);
                        if (driver.findElements(By.className("product-last-items")).size() != 0 ||
                                driver.findElements(By.className("product-unavailable")).size() != 0) {
                            wait.until(ExpectedConditions.elementToBeClickable(driver.findElements(By.xpath("/html/body/main/header/div[2]/div/div[1]/div[2]/div[1]/ul/li")).get(numerKategorii)));//.click();
                            driver.findElements(By.xpath("/html/body/main/header/div[2]/div/div[1]/div[2]/div[1]/ul/li")).get(numerKategorii).click();

                            continue;
                        }
                        driver.findElement(By.cssSelector("button.btn-primary")).click();
                        Thread.sleep(1000);
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("btn-secondary"))).click();

                        dodaneProdukty++;
                        iloscDodanychProduktowZKategorii++;

                        wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.id("blockcart-modal"))));
                        //                    wait.until(ExpectedConditions.elementToBeClickable(driver.findElements(By.className("category")).get(numerKategorii)));//.click();
                    }

                    wait.until(ExpectedConditions.elementToBeClickable(driver.findElements(By.xpath("/html/body/main/header/div[2]/div/div[1]/div[2]/div[1]/ul/li")).get(numerKategorii)));//.click();
                    //                    driver.findElements(By.className("category")).get(numerKategorii).click();
                    driver.findElements(By.xpath("/html/body/main/header/div[2]/div/div[1]/div[2]/div[1]/ul/li")).get(numerKategorii).click();
                }
                if (liczbaKategorii > numerKategorii + 1) {
                    numerKategorii++;
                }
            }
        }
    }

    private static void usunProdukt(WebDriver driver) throws NoSuchElementException, InterruptedException {
        driver.findElement(By.className("shopping-cart")).click();
        Thread.sleep(2000);
        driver.findElements(By.className("remove-from-cart")).get(0).click();
        Thread.sleep(2000);
    }

    private static void rejestrujUzytkownika(WebDriver driver) throws NoSuchElementException, InterruptedException {
        driver.findElement(By.className("user-info")).click();
        driver.findElement(By.className("no-account")).click();
        // nazwa kontaktu
        driver.findElement(By.xpath("/html/body/main/section/div/div/section/section/section/form/section/div[1]/div[1]/label[2]/span/input")).click();
        // imie
        driver.findElement(By.xpath("/html/body/main/section/div/div/section/section/section/form/section/div[2]/div[1]/input")).sendKeys(randomowyCiagZnakow());
        // nazwisko
        driver.findElement(By.xpath("/html/body/main/section/div/div/section/section/section/form/section/div[3]/div[1]/input")).sendKeys(randomowyCiagZnakow());
        // email
        driver.findElement(By.xpath("/html/body/main/section/div/div/section/section/section/form/section/div[4]/div[1]/input")).sendKeys(randomEmail());
        // hasło
        driver.findElement(By.xpath("/html/body/main/section/div/div/section/section/section/form/section/div[5]/div[1]/div/input")).sendKeys(randomowyCiagZnakow());
        // regulamin
        driver.findElement(By.xpath("/html/body/main/section/div/div/section/section/section/form/section/div[9]/div[1]/span/label/input")).click();
        Thread.sleep(2000);
        // zapisz
        driver.findElement(By.className("form-control-submit")).click();
        Thread.sleep(2000);

    }

    private static void zatwierdzZamowienie(WebDriver driver) throws NoSuchElementException, InterruptedException {
        // przejdz do koszyka
        driver.findElement(By.className("active")).click();
        // realizuj zamowienie
        driver.findElement(By.className("checkout")).click();
        // podaj adres
        driver.findElement(By.xpath("/html/body/section/div/section/div/div[1]/section[2]/div/div/form/div/div/section/div[5]/div[1]/input")).sendKeys(randomowyCiagZnakow());
        // podaj kod pocztowy
        driver.findElement(By.xpath("/html/body/section/div/section/div/div[1]/section[2]/div/div/form/div/div/section/div[7]/div[1]/input")).sendKeys(randomowyKodPocztowy());
        // podaj miasto
        driver.findElement(By.xpath("/html/body/section/div/section/div/div[1]/section[2]/div/div/form/div/div/section/div[8]/div[1]/input")).sendKeys(randomowyCiagZnakow());
        // przejdz dalej
        driver.findElement(By.xpath("/html/body/section/div/section/div/div[1]/section[2]/div/div/form/div/div/footer/button")).click();
        Thread.sleep(1000);

        // wybierz jednego z wielu dostawcow
        List<WebElement> dostawcy = driver.findElements(By.className("delivery-option"));
        if (dostawcy.size() > 1) {
            // znajdz element znajdujacy sie w danym elemencie
            dostawcy.get(1).findElement(By.className("custom-radio")).click();
        }
        Thread.sleep(1000);

        // przejdz dalej
        driver.findElement(By.className("delivery-options-list")).findElement(By.className("continue")).click();

        // wybierz platnosc przy odbiorze - znajduje wybrany tekst i go klika
        driver.findElement(By.xpath("//*[text()[contains(.,'odbiorze')]]")).click();
        // zaakceptuj regulamin
        driver.findElement(By.id("conditions_to_approve[terms-and-conditions]")).click();
        Thread.sleep(1000);

        // potwierdz zamowienie
        driver.findElement(By.id("payment-confirmation")).click();
        Thread.sleep(3000);
    }



    private static void sprawdzStatusZamowienia(WebDriver driver) throws NoSuchElementException, InterruptedException {

        driver.findElement(By.className("account")).click();
        driver.findElement(By.id("history-link")).click();
        System.out.println("Status zamowienia to: " + driver.findElement(By.xpath("/html/body/main/section/div/div/section/section/table/tbody/tr/td[4]/span")).getText());
        Thread.sleep(5000);

    }

    private static String randomEmail () {

        return randomowyCiagZnakow() + "@123test.pl";
    }

    private static String randomowyCiagZnakow () {
        int length = 8;
        boolean useLetters = true;
        boolean useNumbers = false;

        return RandomStringUtils.random(length, useLetters, useNumbers);

    }

    private static String randomowyKodPocztowy() {
        boolean useLetters = false;
        boolean useNumbers = true;

        return RandomStringUtils.random(2, useLetters, useNumbers) +
                "-" +
                RandomStringUtils.random(3, useLetters, useNumbers);
    }

}

