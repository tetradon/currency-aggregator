package com.aggregator.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class DomUtils {

    private DomUtils() {
    }

    public static void findTagAndSetValue(final Node node, final String tag,
                                          final Double newValue) {
        Node nextSibling = getNextSiblingElement(node);
        if (Objects.requireNonNull(nextSibling).getNodeName().equals(tag)) {
            nextSibling.getFirstChild().setNodeValue(String.valueOf(newValue));
        } else {
            Objects.requireNonNull(getNextSiblingElement(nextSibling))
                    .getFirstChild()
                    .setNodeValue(String.valueOf(newValue));
        }
    }

    public static void saveUpdateToXml(final File file, final Document doc)
            throws TransformerException {
        Transformer transformer = TransformerFactory
                .newInstance()
                .newTransformer();
        Result output = new StreamResult(file);
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
    }

    public static Document createDocument(final File file) {
        Document d = null;
        try {
            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = df.newDocumentBuilder();
            d = parser.parse(file);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return d;
    }


    public static String getChildValue(final Node node, final String name) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if ((childNodes.item(i).getNodeName()).equals(name)) {
                if (childNodes.item(i).getFirstChild() == null) {
                    return null;
                }
                return childNodes.item(i).getFirstChild().getNodeValue().trim();
            }
        }
        return null;
    }


    private static Element getNextSiblingElement(final Node node) {
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) sibling;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }
}
