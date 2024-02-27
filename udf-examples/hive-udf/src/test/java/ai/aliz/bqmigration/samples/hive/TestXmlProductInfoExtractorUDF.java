package ai.aliz.bqmigration.samples.hive;

import org.apache.hadoop.hive.ql.udf.generic.GenericUDF.DeferredJavaObject;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF.DeferredObject;
import org.apache.hadoop.io.Text;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestXmlProductInfoExtractorUDF {

    private XmlProductInfoExtractorUDF udf;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
        udf = new XmlProductInfoExtractorUDF();
    }

    @Test
    public void testEvaluate() throws Exception {
        String xmlContent = new String(getClass().getClassLoader().getResourceAsStream("sample1.xml").readAllBytes());
        Text xmlText = new Text(xmlContent);
        DeferredObject deferredObject = new DeferredJavaObject(xmlText);

        Object response = udf.evaluate(new DeferredObject[] { deferredObject });
        System.out.println(response);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Product Name: SuperWidget, Price: 199.99", response.toString());

    }

}
