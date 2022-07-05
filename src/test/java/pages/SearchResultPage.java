package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchResultPage {

    //название документа
    public SelenideElement documentTitle = $x("//div[@class = 'ellipsisMain underline']");
    //iframe с контентом документа
    public SelenideElement iframe = $x("(//div[@class='textContainer visible']//iframe)");
    //абзац 1 статьи 145
    public SelenideElement paragraph1 = $x("//div[@id=\"p293\"]");
    //заголовок статьи 145
    public SelenideElement article145 = $x("//div[@id=\"p280\"]");
    //ссылка на абзац 1
    public SelenideElement linkToParagraph1 = $(By.partialLinkText("абзаце первом пункта 1"));

    @Step("Переход по ссылке «Налоговый кодекс Российской Федерации (часть вторая)»")
    public void openSearchResultPage(String url) {

        Selenide.open(url);
        //переключиться в iframe с контентом документа
        switchTo().frame(iframe);
        //получить название документа
        documentTitle.shouldBe(Condition.visible);
        Allure.addAttachment("Фактический результат", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));

    }

    @Step("Сверить полное название открытого документа с названием из списка в шаге 3.")
    public void compareDocumentTitle(String documentTitle, String documentTitleStep3) {

        assertEquals(documentTitle, documentTitleStep3);

    }

    @Step("Проверить наличие запроса «Налоговый часть» в поисковой строке.")
    public void checkSearchForm(String searchText, String searchFormText) {

        assertEquals(searchText, searchFormText);

    }

    public String getSearchFormText () {
        //получить содержимое строки поиска из LocalStorage браузера
        JSONObject json = new JSONObject (Selenide.localStorage().getItem("__storage_CONS_user_history_undefined"));
        return json.getJSONArray("sPlus").get(0).toString();

    }

    @Step("Технический шаг для подгрузки контента")
    public void scrollToLoadContent() {

        //переключить фрейм, без этого не находится iframe, в котором расположен контент документа
        switchTo().defaultContent();
        //переключиться в iframe с контентом документа
        switchTo().frame(iframe);
        //промотать до статьи 145
        article145.scrollIntoView(true);
        //подождать, пока прогрузится текст статьи
        paragraph1.shouldBe(Condition.visible);
        //скрин для отчета
        Allure.addAttachment("Фактический результат", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));

    }

    @Step("Проверить, что желтая cтрелка переместилась на первый абзац п.1 этой статьи.")
    public void checkYellowMarker(){

        //выполняется проверка содержимого property 'content' перед тем как кликнуть по ссылке
        paragraph1.shouldBe(Condition.pseudo(":before", "content", "none"));
        //скрин для отчета
        Allure.addAttachment("Вид абзаца до клика по ссылке", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));
        //выполнить поиск ссылки и кликнуть по ней
        linkToParagraph1.shouldBe(Condition.visible).click();
        //проверка содержимого property 'content' после клика по ссылке
        paragraph1.shouldBe(Condition.pseudo(":before", "content", "\" \""));
        //скрин для отчета
        Allure.addAttachment("Вид абзаца после клика по ссылке", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));
    }

    @Step("Выделить указанный абзац и вывести его в отчет.")
    public void selectAndSaveParagraph() {
        //получить текст абзаца для отчета
        Allure.addAttachment("Результат", "text/plain", paragraph1.getText());
        //не уверен, что именно это имелось ввиду под словом "выделить" в задании, но на всякий случай "выделил" :)
        Selenide.executeJavaScript("document.querySelector(\"#p293\").style.outline = 'auto 1px'");
        //скрин для отчета
        Allure.addAttachment("Фактический результат", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));

    }

}
