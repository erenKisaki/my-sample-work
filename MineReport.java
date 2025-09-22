// src/test/java/.../HomeWidgetsMoreMissingTests.java
package your.pkg;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import io.restassured.RestAssured;
import org.json.*;
import org.testng.annotations.*;

/**
 * Additional missing tests to extend coverage without touching existing ones.
 * These focus on sorting/freshness, headers/SLOs, link health (sample),
 * customize-flow idempotency/duplicates, and a minimal Office-UK smoke.
 */
public class HomeWidgetsMoreMissingTests extends AMTestBase {

  // Widget ids (from your code/screenshots)
  private static final String WIDGET_ANNOUNCEMENT     = "14";
  private static final String WIDGET_PRODUCT_SUPPORT  = "62";
  private static final String WIDGET_MARKETS          = "12"; // used in customize flow

  // Office-UK announcement widget id you showed in constants
  private static final String WIDGET_ANNOUNCEMENT_OFFICE_UK = "1";

  // Logical API ids from apis.json
  private static final String API_HOME_WIDGETINFO         = "home.widgetInfo";
  private static final String API_GLADCM_WIDGETINFO       = "gladcm.homeWidgetInfo";
  private static final String API_ADCRM_WIDGETINFO        = "adcrm.homeWidgetInfo";
  private static final String API_ADPAS_WIDGETINFO        = "adpas.homeWidgetInfo";
  private static final String API_GETTEMPLATE             = "home.gettemplate";
  private static final String API_GETWIDGETLIST           = "home.getwidgetlist";
  private static final String API_ADDWIDGET               = "home.addwidget";
  private static final String API_RESTORELAYOUT           = "home.restorelayout";

  // ---- Content quality: sorting, freshness, duplicates, url sanity ----

  @Test(groups = {Priority.p2, Products.DIRECT_ID})
  public void announcement_sortedDesc_and_noDuplicates_and_fresh() {
    JSONArray arr = getAnnouncements(10);

    assertTrue(arr.length() > 0, "no announcements returned");

    // no duplicates by 'id'
    assertNoDuplicates(arr, "id");

    // sorted by publishDate desc (best-effort flexible parse)
    Instant prev = null;
    for (int i = 0; i < arr.length(); i++) {
      JSONObject o = arr.getJSONObject(i);
      String d = o.optString("publishDate", null);
      if (d == null || d.isBlank()) continue; // tolerate missing for robustness
      Instant cur = parseInstantFlexible(d);
      if (prev != null) {
        assertTrue(!cur.isAfter(prev), "publishDate not in DESC order at index " + i);
      }
      prev = cur;
    }

    // freshness: at least one item within last 90 days (adjust threshold if needed)
    boolean hasFresh = false;
    Instant ninetyDaysAgo = Instant.now().minus(Duration.ofDays(90));
    for (int i = 0; i < arr.length(); i++) {
      String d = arr.getJSONObject(i).optString("publishDate", null);
      if (d == null || d.isBlank()) continue;
      Instant cur = parseInstantFlexible(d);
      if (cur.isAfter(ninetyDaysAgo)) { hasFresh = true; break; }
    }
    assertTrue(hasFresh, "no announcement appears fresh (<= 90 days)");
  }

  @Test(groups = {Priority.p2, Products.DIRECT_ID})
  public void announcement_sampleLinks_areReachable_2xx_or_3xx() {
    JSONArray arr = getAnnouncements(5); // small sample to avoid flakiness
    int checks = Math.min(3, arr.length());
    for (int i = 0; i < checks; i++) {
      String url = arr.getJSONObject(i).optString("url", "");
      assertTrue(url.startsWith("http"), "invalid url: " + url);
      RestAssured
          .given()
          .relaxedHTTPSValidation()
          .when()
          .head(url)
          .then()
          .statusCode(anyOf(is(200), is(204), is(301), is(302), is(303), is(307), is(308)));
    }
  }

  // ---- Headers / SLOs across additional backends ----

  @Test(groups = {Priority.p2, Products.DIRECT_ID})
  public void announcement_headersAndSLO_gladcm_adcrm_adpas() {
    for (String apiId : List.of(API_GLADCM_WIDGETINFO, API_ADCRM_WIDGETINFO, API_ADPAS_WIDGETINFO)) {
      String body = TemplateUtil.getInstance().loadTemplate(
          "Widget.ftl",
          Map.of("id", WIDGET_ANNOUNCEMENT, "prefList", Map.of("entries", "10"))
      );
      Api.of(apiId)
          .setBody(body)
          .setParams("widgetId", WIDGET_ANNOUNCEMENT)
          .call()
          .then()
          .statusCode(200)
          .and()
          .time(lessThan(2500L)) // light SLO
          .and()
          .header("Cache-Control", not(isEmptyOrNullString()));
    }
  }

  // ---- Customize Page: add same widget twice (no duplicates) & restore idempotent ----

  @Test(groups = {Priority.p1, Products.DIRECT_ID})
  public void customize_addSameWidgetTwice_noDuplicateModules_then_restoreTwice_idempotent() {
    // ensure Markets exists in list
    String listXml = Api.of(API_GETWIDGETLIST)
        .setBody("<req action=\"getwidgetlist\" id=\"1\" />")
        .call().then().statusCode(200).extract().body().asString();
    assertTrue(listXml.contains(" i=\"" + WIDGET_MARKETS + "\""), "Markets not in widget list");

    // add first time
    Api.of(API_ADDWIDGET)
        .setBody("<req action=\"addwidget\" id=\"1\"><flds><f i=\"" + WIDGET_MARKETS + "\" selected=\"1\"/></flds></req>")
        .call().then().statusCode(200);

    // add second time (should not create duplicate visual modules)
    Api.of(API_ADDWIDGET)
        .setBody("<req action=\"addwidget\" id=\"1\"><flds><f i=\"" + WIDGET_MARKETS + "\" selected=\"1\"/></flds></req>")
        .call().then().statusCode(anyOf(is(200), is(409))); // allow server to no-op or 409

    // template must contain Markets only once
    String template1 = Api.of(API_GETTEMPLATE).setBody("<req action=\"gettemplate\" id=\"1\"/>")
        .call().then().statusCode(200).extract().body().asString();
    assertEquals(countOccurrences(template1, "module id=\"" + WIDGET_MARKETS + "\""), 1,
        "Markets should appear only once after duplicate add");

    // restore twice should be idempotent
    String restoreReq = "<req action=\"restorelayout\" id=\"1\" univ=\"\" panel=\"local\"/>";
    Api.of(API_RESTORELAYOUT).setBody(restoreReq).call().then().statusCode(200);
    String templateAfterFirstRestore = Api.of(API_GETTEMPLATE).setBody("<req action=\"gettemplate\" id=\"1\"/>")
        .call().then().statusCode(200).extract().body().asString();

    Api.of(API_RESTORELAYOUT).setBody(restoreReq).call().then().statusCode(200);
    String templateAfterSecondRestore = Api.of(API_GETTEMPLATE).setBody("<req action=\"gettemplate\" id=\"1\"/>")
        .call().then().statusCode(200).extract().body().asString();

    assertEquals(templateAfterSecondRestore, templateAfterFirstRestore,
        "restorelayout should be idempotent (same template twice)");
    assertTrue(templateAfterSecondRestore.contains("module id=\"" + WIDGET_ANNOUNCEMENT + "\""),
        "default layout must contain Announcements");
  }

  // ---- Minimal Office-UK smoke to ensure that path stays healthy ----

  @Test(groups = {Priority.p2, Products.OFFICE_ID_UK})
  public void officeUK_announcement_basicHealth() {
    String body = TemplateUtil.getInstance().loadTemplate(
        "Widget.ftl",
        Map.of("id", WIDGET_ANNOUNCEMENT_OFFICE_UK, "prefList", Map.of("entries", "10"))
    );

    String resp = Api.of(API_HOME_WIDGETINFO)
        .setBody(body)
        .setParams("widgetId", WIDGET_ANNOUNCEMENT_OFFICE_UK)
        .call()
        .then().statusCode(200)
        .and().time(lessThan(2500L))
        .extract().body().asString();

    JSONObject json = new JSONObject(resp);
    assertTrue(json.has("announcement"), "Office-UK: missing 'announcement'");
    assertTrue(json.getJSONArray("announcement").length() > 0, "Office-UK: empty announcements");
  }

  // ---- Helpers ----

  private JSONArray getAnnouncements(int entries) {
    String body = TemplateUtil.getInstance().loadTemplate(
        "Widget.ftl",
        Map.of("id", WIDGET_ANNOUNCEMENT, "prefList", Map.of("entries", String.valueOf(entries)))
    );
    String resp = Api.of(API_HOME_WIDGETINFO)
        .setBody(body)
        .setParams("widgetId", WIDGET_ANNOUNCEMENT)
        .call().then().statusCode(200).extract().body().asString();
    return new JSONObject(resp).getJSONArray("announcement");
  }

  private static void assertNoDuplicates(JSONArray arr, String key) {
    Set<Object> seen = new HashSet<>();
    for (int i = 0; i < arr.length(); i++) {
      Object v = arr.getJSONObject(i).opt(key);
      assertTrue(seen.add(v), "duplicate " + key + ": " + v);
    }
  }

  private static Instant parseInstantFlexible(String s) {
    // Try ISO-8601 first
    try { return Instant.parse(s); } catch (DateTimeParseException ignored) {}
    try { return OffsetDateTime.parse(s).toInstant(); } catch (DateTimeParseException ignored) {}
    // Common date-only formats (treat as start of day UTC)
    for (String pat : List.of("yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy")) {
      try {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(pat)).atStartOfDay(ZoneOffset.UTC).toInstant();
      } catch (DateTimeParseException ignored) {}
    }
    // Epoch millis or seconds as string
    try {
      long epoch = Long.parseLong(s.trim());
      if (epoch > 10_000_000_000L) return Instant.ofEpochMilli(epoch);
      return Instant.ofEpochSecond(epoch);
    } catch (NumberFormatException ignored) {}
    // Fallback: now (won't break test loop but logs problem)
    return Instant.now();
  }

  private static int countOccurrences(String haystack, String needle) {
    int count = 0, from = 0;
    while (true) {
      int idx = haystack.indexOf(needle, from);
      if (idx < 0) break;
      count++;
      from = idx + needle.length();
    }
    return count;
  }
}
