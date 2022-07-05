import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import pages.QuickFindPage;
import pages.SearchResultPage;
import pages.StartPage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SelenideTest {

    @BeforeAll
    public static void setUp() {
        //конфигурация WebDriver
        closeWebDriver();
        Configuration.browser = "chrome";
        Configuration.fastSetValue = true;
        Configuration.timeout = 5000;
        Configuration.browserSize = "1600x1280";
        Configuration.downloadsFolder = "target/test-result/downloads";
        Configuration.reportsFolder = "target/test-result/reports";
        SelenideLogger
                .addListener("AllureSelenide", new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false)
                        .includeSelenideSteps(false)
                );

    }

    //константы со значениями тестовых данных
    final String startPageURL = "http://www.consultant.ru/cons/";
    final String searchText = "Налоговый часть";
    //инициализация экземпляров PageObject
    static StartPage startPage = new StartPage();
    static QuickFindPage quickFindPage = new QuickFindPage();
    static SearchResultPage searchResultPage = new SearchResultPage();

    @Test
    @DisplayName("Открыть браузер (Chrome) на странице http://www.consultant.ru/cons/.")
    @Order(1)
    public void startPageTest() {

        startPage.openStartPage(startPageURL);
        //не очень хорошая практика использовать assert'ы при наличии Selenide с его .shouldBe() методами, но тут тривиальный случай
        assertEquals("КонсультантПлюс - Стартовая страница", startPage.title);

    }

    @Test
    @DisplayName("Перейти в Быстрый поиск (кнопка в главном меню).")
    @Order(2)
    public void openQuickFindPageTest() {

        //обратиться к методу перехода по кнопке
        quickFindPage.openQuickFindPage();
        //проверить, что переход успешен
        quickFindPage.iconSearchPlus.shouldBe(Condition.visible);

    }

    @Test
    @DisplayName("В поисковой строке напечатать «Налоговый часть» и нажать кнопку «Найти».")
    @Order(3)
    public void typeTextQuickFindPageTest() {

        quickFindPage.sendSearchText(searchText);
        //пример того, как делать не следует - в нашем случае тесты запускаются в определенном порядке, благодаря аннотации @Order
        //но в реальной жизни порядок запуска тестов будет выбирать JUnit/TestNG, поэтому состояние объекта $ (SelenideElement) может потеряться
        //обращаться к $ прямо в тесте - это плохая практика - лучше инкапсулировать элементы либо в поля класса страницы (PageObject),
        //либо обращаться к ним через публичные методы, как это сделано далее
        $(By.partialLinkText("часть вторая")).shouldBe(Condition.visible);

    }

    @Test
    @DisplayName("В списке найденных документов открыть документ «Налоговый кодекс Российской Федерации (часть вторая)».")
    @Order(4)
    public void openSearchResultTest() {

        //название документа из списка в шаге 3, отформатировано для последующего сравнения
        String compareString = quickFindPage.getLinkToFindFormatted();

        //переход по ссылке «Налоговый кодекс Российской Федерации (часть вторая)»
        searchResultPage.openSearchResultPage(quickFindPage.linkToFind.getAttribute("href"));

        //Сверить полное название открытого документа с названием из списка в шаге 3.
        //не придумал, как получить полное название открытого документа откуда-то, кроме заголовка
        //видел, что оно есть в тексте, но там какая-то жесть с iframe, достать оттуда не получилось
        //assertEquals(title().replaceAll(" - КонсультантПлюс", "").toLowerCase().replaceAll("([\\r\\n])", " "), compareString);

        //UPD - нашел как переключиться на нужный iframe
        //switchTo().frame($x("(//div[@class='textContainer visible']//iframe)"));

        //и еще раз
        //Сверить полное название открытого документа с названием из списка в шаге 3.
        searchResultPage.compareDocumentTitle(searchResultPage.documentTitle.getText().toLowerCase(), compareString);

        //Проверить наличие запроса «Налоговый часть» в поисковой строке.
        searchResultPage.checkSearchForm(searchText, searchResultPage.getSearchFormText());

    }

    @Test
    @DisplayName("В статье «145. Освобождение от исполнения обязанностей налогоплательщика» в п.3 нажать на ссылку «абзаце первом пункта 1». Проверить, что желтая cтрелка переместилась на первый абзац п.1 этой статьи.")
    @Order(5)

    public void clickHrefTest() {
        //поскольку текст документа подгружается по мере скролла, необходим такой шаг
        searchResultPage.scrollToLoadContent();

        /*
        тут требуется пояснение
        у меня довольно поверхностные знания относительно фронтенда, особенно в части динамических элементов,
        про существование псевдоэлементов я вообще узнал только в ходе выполнения этого задания, поэтому механику появления желтой стрелки я осознал не в полной мере
        я нашел css-property 'background-image', в котором содержится URL картинки со стрелкой, это property пустое без указания псевдоэлемента ':before' и непустое, если указать псевдоэлемент
        если выполнить в консоли devTools:
        window.getComputedStyle(document.querySelector("#p293")).getPropertyValue('background-image')
        получим
        'none'
        а выполнив:
        window.getComputedStyle(document.querySelector("#p293"), ':before').getPropertyValue('background-image')
        получим
        'url("data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICAKPHBhdGggZmlsbD0iI0ZGQTgwMCIgZD0iTTE4IDEyTDcgMjQgNyAweiI+PC9wYXRoPjwvc3ZnPg==")'
        однако, наличие или отсутствие URL картинки никак не зависит от того, кликнули мы по ссылке или нет

        в ходе дальнейших исследований было обнаружено property 'content', содержимое которого изменялось в зависимости от того, кликнули ли мы по ссылке или нет, поэтому я завязался на него
        подозреваю, что этот пункт выполнен некорректно
        понимания, как это работает, у меня, к сожалению, так и не появилось
        */
        searchResultPage.checkYellowMarker();

    }

    @Test
    @DisplayName("Выделить указанный абзац и вывести его в отчет.")
    @Order(6)
    public void selectAndSaveParagraphTest() {
        //выделить указанный абзац и вывести его в отчет
        searchResultPage.selectAndSaveParagraph();

    }

    @AfterAll
    @DisplayName("Закрыть браузер")
    //завершить работу WebDriver и закрыть браузер
    public static void tearDown() {
        Selenide.closeWebDriver();
    }

}
