package com.aggregator.provider;


import com.aggregator.model.CurrencyRate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aggregator.utils.DomUtils.*;
import static com.aggregator.utils.FIleUtils.deleteContentOfFile;

public class XmlCurrencyProvider implements CurrencyProvider {

    private static ArrayList<CurrencyRate> resultList;

    private static void nextNode(Node node) {
        if (node != null) {
            int type = node.getNodeType();
            switch (type) {
                case Node.DOCUMENT_NODE:
                    nextNode(((Document) node).getDocumentElement());
                    break;
                case Node.ELEMENT_NODE:
                    if (node.getNodeName().equals("rate")) {
                        String code = getChildValue(node, "code");
                        Double buy = Double.valueOf(Objects.requireNonNull(getChildValue(node, "buy")));
                        Double sell = Double.valueOf(Objects.requireNonNull(getChildValue(node, "sell")));
                        resultList.add(new CurrencyRate(code, buy, sell));
                    }
                    passChild(node);
                    break;
            }
        }
    }

    private static void passChild(Node nd) {
        NodeList childNodes = nd.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
            nextNode(childNodes.item(i));
    }

    public List<CurrencyRate> getData(File file) {
        resultList = new ArrayList<>();
        if(file.length() != 0) {
            Document doc = createDocument(file);
            nextNode(doc);
        }
        return resultList;
    }

    public void updateBuyPrice(File file, String code, Double newValue) {
        updatePrice(file, code, newValue, "buy");
    }

    public void updateSellPrice(File file, String code, Double newValue) {
        updatePrice(file, code, newValue, "sell");
    }

    private void updatePrice(File file, String code, Double newValue, String tag) {
        Document doc = createDocument(file);
        NodeList rates = doc.getElementsByTagName("rate");
        Element element;

        for (int i = 0; i < rates.getLength(); i++) {
            element = (Element) rates.item(i);
            Node node = element.getElementsByTagName("code").item(0);

            if (node.getFirstChild().getNodeValue().equals(code)) {
                findTagAndSetValue(node, tag, newValue);
                try {
                    saveUpdateToXml(file, doc);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteRatesForBank(File file) {
        deleteContentOfFile(file);
    }
}
