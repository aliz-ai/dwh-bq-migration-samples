package ai.aliz.bqmigration.samples.hive;



import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Description(name = "xml_product_info_extractor", value = "_FUNC_(string) - ")
public class XmlProductInfoExtractorUDF extends GenericUDF {

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        checkArgsSize(arguments, 1, 1);
        checkArgPrimitive(arguments, 0);
      
        return PrimitiveObjectInspectorFactory.writableStringObjectInspector;

    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        Text xmlInput = (Text) arguments[0].get();
        if (xmlInput == null) return null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlInput.toString())));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            String productName = xpath.evaluate("/Transaction/Product/Name", doc);
            String productPrice = xpath.evaluate("/Transaction/Product/Price", doc);

            return new Text("Product Name: " + productName + ", Price: " + productPrice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDisplayString(String[] children) {
        return "xml_product_info_extractor";
    }
}