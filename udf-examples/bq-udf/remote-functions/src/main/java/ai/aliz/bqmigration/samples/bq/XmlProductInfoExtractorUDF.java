package ai.aliz.bqmigration.samples.bq;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import ai.aliz.bqmigration.samples.bq.remotefunction.BQRemoteFunctionRequest;
import ai.aliz.bqmigration.samples.bq.remotefunction.BQRemoteFunctionResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlProductInfoExtractorUDF implements HttpFunction {

    public static final Gson gson = new Gson();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        BQRemoteFunctionResponse functionResponse;

        var contentType = request.getContentType().orElse(null);
        if (contentType == null || contentType != "application/json") {
            throw new IllegalArgumentException("Content-Type must be application/json and not " + contentType);
        }

        BQRemoteFunctionRequest remoteFunctionRequest = gson.fromJson(request.getReader(), BQRemoteFunctionRequest.class);

        log.info("Processing request: {}", remoteFunctionRequest);
        var replies = new ArrayList<JsonElement>();
        functionResponse = BQRemoteFunctionResponse.success(replies);
        for (var call : remoteFunctionRequest.getCalls()) {
            try {
                var result = process(remoteFunctionRequest, call);
                replies.add(result);
                log.debug("Input: {} - Result: {}", call, result);
            } catch (Exception e) {
                var errorMessage = String.format("Error processing request for input %s", call);
                log.error(String.format(errorMessage, call), e);
                String stackTrace = ExceptionUtils.getStackTrace(e);
                functionResponse = BQRemoteFunctionResponse.error(String.format("%s\n%s", errorMessage, stackTrace));
                response.setStatusCode(500);
                break;
            }
        }
        var responseJson = gson.toJson(functionResponse);
        log.info("Sending response: {}", responseJson);
        BufferedWriter writer = response.getWriter();
        writer.write(responseJson);
    }

    protected JsonElement process(BQRemoteFunctionRequest request, List<JsonElement> input) {
        String xmlInput = input.get(0).getAsString();
        if (xmlInput == null) return null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlInput.toString())));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            String productName = xpath.evaluate("/Transaction/Product/Name", doc);
            String productPrice = xpath.evaluate("/Transaction/Product/Price", doc);

            return new JsonPrimitive("Product Name: " + productName + ", Price: " + productPrice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
