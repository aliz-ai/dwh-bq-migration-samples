package ai.aliz.bqmigration.samples.bq;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class TestXmlProductInfoExtractorUDF {

    private XmlProductInfoExtractorUDF udf;

    @BeforeEach
    public void setUp() throws Exception {
        udf = new XmlProductInfoExtractorUDF();
    }

    @Test
    public void testExtract() throws Exception {
        var xmlContent = new String(getClass().getClassLoader().getResourceAsStream("sample1.xml").readAllBytes());
        var input = new ArrayList<JsonElement>();
        input.add(new JsonPrimitive(xmlContent));

        var response = udf.process(null, input);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Product Name: SuperWidget, Price: 199.99", response.getAsString());
    }
}
