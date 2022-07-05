package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

import static com.codeborne.selenide.Selenide.$;

public class QuickFindPage {

    //кнопка быстрого поиска в меню
    public SelenideElement quickSearchMenuButton = $(By.linkText("Быстрый поиск"));
    //элемент с иконкой поиска для контроля перехода на нужную страницу
    public SelenideElement iconSearchPlus;
    //ссылка на документ «Налоговый кодекс Российской Федерации (часть вторая)» в результатах поиска - значение будет определено после клика  на кнопку "Найти"
    public SelenideElement linkToFind;
    //поле поиска
    public SelenideElement quickSearchField = $(By.name("dictFilter"));
    //кнопка "Найти"
    public SelenideElement quickSearchButton = $("button.runsearch.flat");

    @Step("Ожидаемый результат: выполнен переход в Быстрый поиск (кнопка в главном меню)")
    public void openQuickFindPage() {
        //найти кнопку быстрого поиска и кликнуть по ней
        quickSearchMenuButton.click();
        //сохранить элемент в поле класса, чтобы в тесте убедиться, что мы на нужной странице
        iconSearchPlus = $(By.xpath("//div[@class = 'icon searchPlus']"));
        //скрин для отчета
        Allure.addAttachment("Фактический результат", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));
    }

    @Step("Ожидаемый результат: отображена страница с результатами поиска")
    public void sendSearchText(String searchText) {
        //отправить текст поискового запроса в поле поиска
        quickSearchField.sendKeys(searchText);
        //нажать кнпку "Найти"
        quickSearchButton.click();
        //сохранить ссылку на документ «Налоговый кодекс Российской Федерации (часть вторая)»
        linkToFind = $(By.partialLinkText("часть вторая")).shouldBe(Condition.visible);
        //скрин для отчета
        Allure.addAttachment("Фактический результат", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));
    }

    public String getLinkToFindFormatted() {
        //отформатировать текст ссылки для последующего сравнения
        return linkToFind.getText().toLowerCase().replaceAll("([\\r\\n])", " ").replaceAll("2 ", "");
    }
}
