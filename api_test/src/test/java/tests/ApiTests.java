package tests;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiTests {

    @Test
    public void verifyLanguageList() {
        String uriLanguageList = "https://languagetool.org/api/v2/languages";
        ValidatableResponse response = given().
                when().
                    get(uriLanguageList).
                then().
                    assertThat().
                        statusCode(200).
                        body("size()", equalTo(63)).
                        body("name", everyItem(notNullValue())).
                        body("code", everyItem(notNullValue())).
                        body("longCode", everyItem(notNullValue())).
                        body("name[0]", equalTo("Arabic")).
                        body("code[0]", equalTo("ar")).
                        body("longCode[0]", equalTo("ar"));
        response.log().body();
    }

    @Test
    public void verifyLanguageCheckResponseCorrect() {
        String uriCheckText = "https://languagetool.org/api/v2/check";
        String testText = "Tset user is testing the API.";
        ValidatableResponse response = given().
                    contentType("application/x-www-form-urlencoded").
                        formParam("text", testText).
                        formParam("language", "en-US").
                when().
                    post(uriCheckText).
                then().
                    assertThat().
                        statusCode(200).
                        body("language.name", equalTo("English (US)")).
                        body("language.code", equalTo("en-US")).
                        body("language.detectedLanguage.code", equalTo("en-US")).
                        body("matches[0].message", equalTo("Possible spelling mistake found.")).
                        body("matches[0].sentence", equalTo(testText)).
                        body("matches[0].rule.category.id", equalTo("TYPOS"));
        response.log().body();
    }

    @Test
    public void verifyWrongLanguageResponse400() {
        String uriCheckText = "https://languagetool.org/api/v2/check";
        String languageCode = "ne-SU";
        ValidatableResponse response = given().
                    contentType("application/x-www-form-urlencoded").
                        formParam("text", "Tset user is testing the API.").
                        formParam("language", languageCode).
                when().
                    post(uriCheckText).
                then().
                    assertThat().
                        statusCode(400).
                        body(startsWith("Error: '" + languageCode + "' is not a language code known to LanguageTool."));
        response.log().body();
    }
}
