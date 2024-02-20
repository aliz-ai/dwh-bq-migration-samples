package ai.aliz.bqmigration.samples.bq;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class TestXmlProductInfoExtractorUDF {

    /**
     * Test method for {@link ai.aliz.bqmigration.samples.bq.XmlProductInfoExtractorUDF#process(java.lang.String, java.util.List)}.
     * @throws Exception 
     */
    @Test
    public void testExtract() throws Exception {
        var xmlContent = new String(getClass().getClassLoader().getResourceAsStream("sample1.xml").readAllBytes());
        var input = new ArrayList<JsonElement>();
        input.add(new JsonPrimitive(xmlContent));
        var response = new XmlProductInfoExtractorUDF().process(null, input);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Product Name: SuperWidget, Price: 199.99", response.getAsString());

    }
   
    
}
