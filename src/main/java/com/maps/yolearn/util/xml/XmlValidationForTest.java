/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maps.yolearn.util.xml;

/**
 * @author KOTARAJA
 */

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlValidationForTest {

    private static boolean checkIfNodeExists(Document document, String xpathExpression) throws Exception {
        boolean matches = false;

        // Create XPathFactory object
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xpath = xpathFactory.newXPath();

        try {
            // Create XPathExpression object
            XPathExpression expr = xpath.compile(xpathExpression);

            // Evaluate expression result on XML document
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            if (nodes != null && nodes.getLength() > 0) {
                matches = true;
            }

        } catch (XPathExpressionException e) {

//            System.out.println("exception  " + e.getCause().getMessage());
        }
        return matches;
    }

    private static Object getDocument(InputStream inputStream) throws Exception {

        Document doc = null;
        String resp = "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

//        factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return doc = builder.parse(inputStream);

        } catch (Exception e) {
            resp = e.getMessage();
            return resp;
        }

    }

    public String validaXML(InputStream inputStream) throws Exception {
        String resp = "";

//        Document document = getDocument(inputStream);
        Object object = getDocument(inputStream);
        Document document = null;

        if (object instanceof Document) {
            document = (Document) object;

            String xpathExpression = "";

            List<String> l = new ArrayList<>();

            Element element9 = null;
            NodeList nodes = document.getElementsByTagName("question9");
            for (int i = 0; i < nodes.getLength(); i++) {
                element9 = (Element) nodes.item(i);

            }
            Element element10 = null;
            NodeList nodes1 = document.getElementsByTagName("question10");
            for (int i = 0; i < nodes1.getLength(); i++) {
                element10 = (Element) nodes1.item(i);

            }
            Element element8 = null;
            NodeList nodes2 = document.getElementsByTagName("question8");
            for (int i = 0; i < nodes2.getLength(); i++) {
                element8 = (Element) nodes2.item(i);

            }
            Element element7 = null;
            NodeList nodes3 = document.getElementsByTagName("question7");
            for (int i = 0; i < nodes3.getLength(); i++) {
                element7 = (Element) nodes3.item(i);

            }
            Element element6 = null;
            NodeList nodes4 = document.getElementsByTagName("question6");
            for (int i = 0; i < nodes4.getLength(); i++) {
                element6 = (Element) nodes4.item(i);

            }
            Element element5 = null;
            NodeList nodes5 = document.getElementsByTagName("question5");
            for (int i = 0; i < nodes5.getLength(); i++) {
                element5 = (Element) nodes5.item(i);

            }

            NodeList games = document.getElementsByTagName("questionpara");

            for (int i = 0; i < games.getLength(); i++) {
                Node aNode = games.item(i);

                NamedNodeMap attributes = aNode.getAttributes();

                for (int a = 0; a < attributes.getLength(); a++) {
                    Node theAttribute = attributes.item(a);

                    l.add(theAttribute.getNodeValue());

                }

            }
            List<String> l1 = new ArrayList<>();
            NodeList games1 = document.getElementsByTagName("li");

            for (int i = 0; i < games1.getLength(); i++) {
                Node aNode1 = games1.item(i);

                NamedNodeMap attributes1 = aNode1.getAttributes();

                for (int a = 0; a < attributes1.getLength(); a++) {
                    Node theAttribute1 = attributes1.item(a);

                    l1.add(theAttribute1.getNodeValue());

                }

            }

            String str = "en";
            boolean allMatch = l.stream().allMatch(s -> s.equals(str));

            String str1 = "answer";
            boolean allMatch1 = l1.stream().allMatch(s -> s.equals(str1));

            xpathExpression = "article";
            if (checkIfNodeExists(document, xpathExpression)) {

                xpathExpression = "article/body";
                if (checkIfNodeExists(document, xpathExpression)) {
                    xpathExpression = "article/body/questionbank";
                    if (checkIfNodeExists(document, xpathExpression)) {

                        xpathExpression = "article/body/questionbank/question1";
                        if (checkIfNodeExists(document, xpathExpression)) {
                            xpathExpression = "article/body/questionbank/question1/questionpara/@lang";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "question para tag is missing";

                            }

                            xpathExpression = "article/body/questionbank/question1/options";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "options tag is missing";

                            }
                            xpathExpression = "article/body/questionbank/question1/options/li/@class";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "li class answer tag is missing";

                            }

                            xpathExpression = "article/body/questionbank/question1/solution/p";

                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "solution tag is missing";
                            }

                            if (allMatch) {

                            } else {

                                resp = "In  <questionpara tag content should be with name of en";
                            }
                            if (allMatch1) {

                            } else {

                                resp = "In  <li tag content should be with name of answer";
                            }

                        } else {

                            resp = "question1  tag is missing";

                        }

//                    ---------------------
                        xpathExpression = "article/body/questionbank/question2";
                        if (checkIfNodeExists(document, xpathExpression)) {
                            xpathExpression = "article/body/questionbank/question2/questionpara/@lang";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {
                                resp = "question2 para tag is missing";

                            }

                            xpathExpression = "article/body/questionbank/question2/options";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 2 options tag is missing";
                            }
                            xpathExpression = "article/body/questionbank/question2/options/li/@class";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 2 li class answer tag is missing";
                            }

                            xpathExpression = "article/body/questionbank/question2/solution/p";

                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 2 solution tag is missing";
                            }
                            if (allMatch) {

                            } else {

                                resp = "In  <questionpara tag content should be with name of en";
                            }
                            if (allMatch1) {

                            } else {

                                resp = "In  <li tag content should be with name of answer";
                            }
                        } else {
                            resp = "question2  tag is missing";

                        }

//        ---------------------------------
                        xpathExpression = "article/body/questionbank/question3";
                        if (checkIfNodeExists(document, xpathExpression)) {
                            xpathExpression = "article/body/questionbank/question3/questionpara/@lang";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 3 question para tag is missing";
                            }

                            xpathExpression = "article/body/questionbank/question3/options";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 3 options tag is missing";
                            }
                            xpathExpression = "article/body/questionbank/question3/options/li/@class";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 3 li class answer tag is missing";
                            }

                            xpathExpression = "article/body/questionbank/question3/solution/p";

                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 3 solution tag is missing";
                            }
                            if (allMatch) {

                            } else {

                                resp = "In  <questionpara tag content should be with name of en";
                            }
                            if (allMatch1) {

                            } else {

                                resp = "In  <li tag content should be with name of answer";
                            }

                        } else {

                            resp = " question 3  tag is missing";
                        }

//        ------------------------------------
                        xpathExpression = "article/body/questionbank/question4";
                        if (checkIfNodeExists(document, xpathExpression)) {
                            xpathExpression = "article/body/questionbank/question4/questionpara/@lang";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 4 question para tag is missing";
                            }

                            xpathExpression = "article/body/questionbank/question4/options";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 4 options tag is missing";
                            }
                            xpathExpression = "article/body/questionbank/question4/options/li/@class";
                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 4 li class answer tag is missing";
                            }

                            xpathExpression = "article/body/questionbank/question4/solution/p";

                            if (checkIfNodeExists(document, xpathExpression)) {

                            } else {

                                resp = "in question 4 solution tag is missing";
                            }
                            if (allMatch) {

                            } else {

                                resp = "In <questionpara tag content should be with name of en";
                            }
                            if (allMatch1) {

                            } else {

                                resp = "In  <li tag content should be with name of answer";
                            }
                        } else {

                            resp = " question 4  tag is missing";
                        }

//        -------------------
                        if (element5 == null) {
                            resp = "";
                        } else {
                            xpathExpression = "article/body/questionbank/question5";
                            if (checkIfNodeExists(document, xpathExpression)) {
                                xpathExpression = "article/body/questionbank/question5/questionpara/@lang";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 5 question para tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question5/options";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 5 options tag is missing";
                                }
                                xpathExpression = "article/body/questionbank/question5/options/li/@class";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 5 li class answer tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question5/solution/p";

                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 5 solution tag is missing";
                                }
                                if (allMatch) {

                                } else {

                                    resp = "In  <questionpara tag content should be with name of en";
                                }
                                if (allMatch1) {

                                } else {

                                    resp = "In  <li tag content should be with name of answer";
                                }
                            } else {

                                resp = " question 5  tag is missing";
                            }
                        }

//        -----------------------------------------------
                        if (element6 == null) {
                            resp = "";
                        } else {
                            xpathExpression = "article/body/questionbank/question6";
                            if (checkIfNodeExists(document, xpathExpression)) {
                                xpathExpression = "article/body/questionbank/question6/questionpara/@lang";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 6 question para tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question6/options";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 6 options tag is missing";
                                }
                                xpathExpression = "article/body/questionbank/question6/options/li/@class";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 6 li class answer tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question6/solution/p";

                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 6 solution tag is missing";
                                }
                                if (allMatch) {

                                } else {

                                    resp = "In <questionpara tag content should be with name of en";
                                }
                                if (allMatch1) {

                                } else {

                                    resp = "In  <li tag content should be with name of answer";
                                }
                            } else {

                                resp = " question 6  tag is missing";
                            }

                        }

//        ---------------------------------
                        if (element7 == null) {
                            resp = "";
                        } else {
                            xpathExpression = "article/body/questionbank/question7";
                            if (checkIfNodeExists(document, xpathExpression)) {
                                xpathExpression = "article/body/questionbank/question7/questionpara/@lang";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 7 question para tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question7/options";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 7 options tag is missing";
                                }
                                xpathExpression = "article/body/questionbank/question7/options/li/@class";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 7 li class answer tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question7/solution/p";

                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 7 solution tag is missing";
                                }
                                if (allMatch) {

                                } else {

                                    resp = " <questionpara tag content should be with name of en";
                                }
                                if (allMatch1) {

                                } else {

                                    resp = "in  <li tag content should be with name of answer";
                                }
                            } else {

                                resp = " question 7  tag is missing";
                            }

                        }

//        ----------------------------
                        if (element8 == null) {
                            resp = "";
                        } else {
                            xpathExpression = "article/body/questionbank/question8";
                            if (checkIfNodeExists(document, xpathExpression)) {
                                xpathExpression = "article/body/questionbank/question8/questionpara/@lang";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 8 question para tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question8/options";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 8 options tag is missing";
                                }
                                xpathExpression = "article/body/questionbank/question6/options/li/@class";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 8 li class answer tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question6/solution/p";

                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 8 solution tag is missing";
                                }
                                if (allMatch) {

                                } else {

                                    resp = "In <questionpara tag content should be with name of en";
                                }
                                if (allMatch1) {

                                } else {

                                    resp = "In  <li tag content should be with name of answer";
                                }
                            } else {

                                resp = " question 8  tag is missing";
                            }
                        }

                        //////////Extra
//        ------------------------------
                        if (element9 == null) {
                            resp = "";
                        } else {
                            xpathExpression = "article/body/questionbank/question9";
                            if (checkIfNodeExists(document, xpathExpression)) {
                                xpathExpression = "article/body/questionbank/question9/questionpara/@lang";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 9 question para tag is missing";

                                }

                                xpathExpression = "article/body/questionbank/question9/options";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 9 options tag is missing";
                                }
                                xpathExpression = "article/body/questionbank/question9/options/li/@class";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 9 li class answer tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question9/solution/p";

                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 9 solution tag is missing";
                                }
                                if (allMatch) {

                                } else {

                                    resp = "In  <questionpara tag content should be with name of en";
                                }
                                if (allMatch1) {

                                } else {

                                    resp = "In  <li tag content should be with name of answer";
                                }
                            } else {

                                resp = "question 9   tag is missing";

                            }
                        }

//        -----------------------------
                        if (element10 == null) {
                            resp = "";
                        } else {
                            xpathExpression = "article/body/questionbank/question10";
                            if (checkIfNodeExists(document, xpathExpression)) {
                                xpathExpression = "article/body/questionbank/question10/questionpara/@lang";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 10 question para tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question10/options";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 10 options tag is missing";
                                }
                                xpathExpression = "article/body/questionbank/question10/options/li/@class";
                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 10 li class answer tag is missing";
                                }

                                xpathExpression = "article/body/questionbank/question10/solution/p";

                                if (checkIfNodeExists(document, xpathExpression)) {

                                } else {

                                    resp = "in question 10 solution tag is missing";
                                }
                                if (allMatch) {

                                } else {

                                    resp = "In <questionpara tag content should be with name of en";
                                }
                                if (allMatch1) {

                                } else {

                                    resp = "In  <li tag content should be with name of answer";
                                }
                            } else {

                                resp = "question 10 tag is missing";

                            }
                        }

                    } else {

                        resp = "question para tag is missing";
                    }

                } else {

                    resp = "root body tag is missing";
                }

            } else {

                resp = "root artical tag is missing";
            }


        } else if (object instanceof String) {

            resp = (String) object;
        }
        return resp;

    }


}
