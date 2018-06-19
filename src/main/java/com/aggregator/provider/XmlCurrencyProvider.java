package com.aggregator.provider;


import com.aggregator.model.CurrencyRate;
import com.aggregator.utils.DomUtils;
import com.aggregator.utils.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class XmlCurrencyProvider implements CurrencyProvider {

    private static ArrayList<CurrencyRate> resultList;

    private static void nextNode(final Node node) {
        if (node != null) {
            int type = node.getNodeType();
            switch (type) {
                case Node.DOCUMENT_NODE:
                    nextNode(((Document) node).getDocumentElement());
                    break;
                case Node.ELEMENT_NODE:
                    if (node.getNodeName().equals("rate")) {
                        String code = DomUtils.getChildValue(node, "code");
                        Double buy = Double.valueOf(Objects.
                                requireNonNull(DomUtils.getChildValue(node,
                                        "buy")));
                        Double sell = Double.valueOf(Objects.
                                requireNonNull(DomUtils.getChildValue(node,
                                        "sell")));
                        resultList.add(new CurrencyRate(code, buy, sell));
                    }
                    passChild(node);
                    break;
                default: break;
            }
        }
    }

    private static void passChild(final Node nd) {
        NodeList childNodes = nd.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            nextNode(childNodes.item(i));
        }
    }

    public List<CurrencyRate> getData(final File file) {
        resultList = new ArrayList<>();
        if (file.length() != 0) {
            Document doc = DomUtils.createDocument(file);
            nextNode(doc);
        }
        return resultList;
    }

    public void updateBuyPrice(final File file, final String code,
                               final Double newValue) {
        updatePrice(file, code, newValue, "buy");
    }

    public void updateSellPrice(final File file, final String code,
                                final Double newValue) {
        updatePrice(file, code, newValue, "sell");
    }

    private void updatePrice(final File file, final String code,
                             final Double newValue, final String tag) {
        Document doc = DomUtils.createDocument(file);
        NodeList rates = doc.getElementsByTagName("rate");
        Element element;

        for (int i = 0; i < rates.getLength(); i++) {
            element = (Element) rates.item(i);
            Node node = element.getElementsByTagName("code").item(0);

            if (node.getFirstChild().getNodeValue().equals(code)) {
                DomUtils.findTagAndSetValue(node, tag, newValue);
                try {
                    DomUtils.saveUpdateToXml(file, doc);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteRatesForBank(final File file) {
        FileUtils.deleteContentOfFile(file);
    }
}
