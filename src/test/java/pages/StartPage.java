package pages;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

public class StartPage {

    public String title;

    @Step("Ожидаемый результат: браузер (Chrome) открыт на странице http://www.consultant.ru/cons/.")
    public void openStartPage(String url) {

        Selenide.open(url);
        title = Selenide.title();
        Allure.addAttachment("Фактический результат", new ByteArrayInputStream(Selenide.screenshot(OutputType.BYTES)));

    }

}