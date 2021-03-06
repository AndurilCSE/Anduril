package com.project.traceability.manager;

import com.project.NLP.SourceCodeToXML.WriteToXML;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openide.util.Exceptions;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.project.NLP.file.operations.FilePropertyName;
import com.project.traceability.GUI.HomeGUI;

public class RelationManager {

    public static void createXML(List<String> relationNodes) {
        try {

            List<String> hasToaddRelationBodes = removeDuplicate(relationNodes, readAll());
            //System.out.println(hasToaddRelationBodes.toString());
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentFactory
                    .newDocumentBuilder();

            // define root elements
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("Relations");
            document.appendChild(rootElement);
            System.out.println("start");

            // System.out.println(relationNodes.size());
            for (int i = 0, j = 1; i < hasToaddRelationBodes.size(); i++, j++) {

                // define school elements
                Element school = document.createElement("Relation");
                rootElement.appendChild(school);

                // add attributes to school
                Attr attribute = document.createAttribute("id");
                attribute.setValue("" + j + "");
                school.setAttributeNode(attribute);
                // System.out.println(relationNodes.get(i));
                Element firstname = document.createElement("SourceNode");
                firstname.appendChild(document.createTextNode(hasToaddRelationBodes
                        .get(i)));
                school.appendChild(firstname);

                Element relationName = document.createElement("RelationPath");
                relationName.appendChild(document.createTextNode(hasToaddRelationBodes
                        .get(++i)));
                school.appendChild(relationName);

                // lastname elements
                Element lastname = document.createElement("TargetNode");
                lastname.appendChild(document.createTextNode(hasToaddRelationBodes
                        .get(++i)));
                school.appendChild(lastname);
            }

            // creating and writing to xml file
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(
                    new File(
                            HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "Relations.xml").getPath());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(domSource, streamResult);

            System.out.println("File saved to specified path!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static void createXML(String projectPath) {

        String filePath = null;
        if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
            filePath = HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "VERSION_Relations.xml";
        } else {
            filePath = HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "Relations.xml";
        }

        File file = new File(filePath);

        if (file.exists()) {
            addRelationsXML(filePath);
        } else {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            addRelationsXML(filePath);
        }
    }

    public static void addRelationsXML(String filePath) throws IllegalArgumentException, DOMException, TransformerFactoryConfigurationError {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentFactory
                    .newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("Relations");
            document.appendChild(rootElement);
            //System.out.println("start");

            // creating and writing to xml file
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(filePath);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(domSource, streamResult);

            //System.out.println("File saved to specified path!");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (TransformerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void addLinks(List<String> relationNodes) {

        List<String> hasToaddRelationBodes = removeDuplicate(relationNodes, readAll());
        System.out.println("Size " + hasToaddRelationBodes.size());
//        List<String> hasToaddRelationBodes = relationNodes;
        String filePath = null;
        if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
            filePath = HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "VERSION_Relations.xml";
        } else {
            filePath = HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "Relations.xml";
        }
        File file = new File(filePath);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = (Document) dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList artefactNodeList = doc.getElementsByTagName("Relations");
            for (int i = 0; i < artefactNodeList.getLength(); i++) {

                Node artefactNode = (Node) artefactNodeList.item(i);
                Node relationNode;
                if (artefactNode.getNodeType() == Node.ELEMENT_NODE) {

                    NodeList artefactElementList = doc
                            .getElementsByTagName("Relation");
                    int newId = 0;
                    Node artefactElementNode = null;
                    Element element = null;
                    if (artefactElementList.getLength() == 0) {
                        newId = 1;
                        relationNode = artefactNode;
                    } else {
                        artefactElementNode = (Node) artefactElementList
                                .item(artefactElementList.getLength() - 1);
                        Element artefact = (Element) artefactElementNode;
                        String artefact_id = artefact.getAttribute("id");
                        int numId = Integer.parseInt(artefact_id);
                        newId = ++numId;
                        relationNode = artefactElementNode.getParentNode();
                    }
                    for (int j = 0; j < hasToaddRelationBodes.size(); j++) {

                        element = doc.createElement("Relation");
                        relationNode.appendChild(element);

                        // add attributes to school
                        Attr attribute = doc.createAttribute("id");
                        attribute.setValue("" + newId++ + "");
                        element.setAttributeNode(attribute);
                        Element firstname = doc.createElement("SourceNode");

                        firstname.appendChild(doc.createTextNode(hasToaddRelationBodes
                                .get(j++)));
                        element.appendChild(firstname);

                        Element relationName = doc
                                .createElement("RelationPath");
                        relationName.appendChild(doc
                                .createTextNode(hasToaddRelationBodes.get(j++)));
                        element.appendChild(relationName);

                        // lastname elements
                        Element lastname = doc.createElement("TargetNode");

                        lastname.appendChild(doc.createTextNode(hasToaddRelationBodes
                                .get(j)));
                        element.appendChild(lastname);

                        if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
                            j++;
                            //status element
                            Element status = doc.createElement("Status");

                            status.appendChild(doc.createTextNode(hasToaddRelationBodes
                                    .get(j)));
                            element.appendChild(status);
                        }
                    }

                }
            }
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            // System.out.println(PropertyFile.xmlFilePath);
            StreamResult result = new StreamResult(new File(filePath).getPath());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(source, result);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    public static List<String> readAll() {

        File file = null;
        if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
            file = new File(HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "VERSION_Relations.xml");
        } else {
            file = new File(HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "Relations.xml");
        }
        List<String> existingNodes = new ArrayList<String>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();

            if (!file.exists()) {
                file.createNewFile();
                FileWriter fos = new FileWriter(file);

                String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<Relations/>";
                fos.write(contents);
                fos.close();

            }
            Document doc = (Document) dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList artefactNodeList = doc.getElementsByTagName("Relations");
            for (int i = 0; i < artefactNodeList.getLength(); i++) {

                Node artefactNode = (Node) artefactNodeList.item(i);
                Node relationNode;
                if (artefactNode.getNodeType() == Node.ELEMENT_NODE) {

                    NodeList artefactElementList = doc
                            .getElementsByTagName("Relation");
                    for (int j = 0; j < artefactElementList.getLength(); j++) {
                        Node node = (Node) artefactElementList.item(j);
                        Element artefact = (Element) node;
                        String source = artefact
                                .getElementsByTagName("SourceNode").item(0)
                                .getTextContent();
                        String description = artefact
                                .getElementsByTagName("RelationPath").item(0)
                                .getTextContent();
                        String target = artefact
                                .getElementsByTagName("TargetNode").item(0)
                                .getTextContent();
                        existingNodes.add(source);
                        existingNodes.add(description);
                        existingNodes.add(target);
                    }

                    Node artefactElementNode = null;
                    Element element = null;
                    if (artefactElementList.getLength() != 0) {
                        artefactElementNode = (Node) artefactElementList
                                .item(artefactElementList.getLength() - 1);
                        Element artefact = (Element) artefactElementNode;
                        String artefact_id = artefact.getAttribute("id");
                        int numId = Integer.parseInt(artefact_id);

                        relationNode = artefactElementNode.getParentNode();
                    }

                }
            }
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            // System.out.println(PropertyFile.xmlFilePath);
            if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
                file = new File(HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "VERSION_Relations.xml");
            } else {
                file = new File(HomeGUI.projectPath + File.separator + FilePropertyName.XML + File.separator + "Relations.xml");
            }
            StreamResult result = new StreamResult(file.getAbsolutePath());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return existingNodes;
    }

    public static List<String> removeDuplicate(List<String> relationNodes, List<String> existingRelationNode) {
        List<String> relationNode3 = new ArrayList<String>();
        List<String> fullDescOfRelationNode = new ArrayList<>();
        List<String> fullDescOfExistingRelationNode = new ArrayList<>();
        List<String> partial = new ArrayList<>();
        //System.out.println("Size ee " + relationNodes.size());

        //get the full description of rlation "source relationpath target"
        for (int i = 0; i < relationNodes.size();) {
            if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
                existingRelationNode = null;
                partial = relationNodes.subList(i, i + 4);
                String sub = (partial.toString().substring(1, partial.toString().length() - 1));
                fullDescOfRelationNode.add(sub.replaceAll("\\s", ""));
                i = i + 4;
            } else {
                partial = relationNodes.subList(i, i + 3);
                String sub = (partial.toString().substring(1, partial.toString().length() - 1));
                fullDescOfRelationNode.add(sub.replaceAll("\\s", ""));
                i = i + 3;
            }
        }

        //get the full description of existing relation "source relationpath target"
        if (existingRelationNode != null) {
            for (int i = 0; i < existingRelationNode.size(); i += 3) {
                partial = existingRelationNode.subList(i, i + 3);
                fullDescOfExistingRelationNode.add((partial.toString().substring(1, partial.toString().length() - 1)).replaceAll("\\s", ""));
//            System.out.println(partial.toString());
            }
        }

        List<String> intersection = new ArrayList<>(fullDescOfExistingRelationNode);

        //get the common relation in both relation list
        intersection.retainAll(fullDescOfRelationNode);

        //remove the intersection elements from new Relation list
        fullDescOfRelationNode.removeAll(intersection);

        //conver into structured format one by one
        for (int i = 0; i < fullDescOfRelationNode.size(); i++) {
            String[] partialNode = fullDescOfRelationNode.get(i).split(",");
            //System.out.print("PA " + partialNode.length);

            for (int j = 0; j < partialNode.length; j++) {
                relationNode3.add(partialNode[j]);
                if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
                    if (partialNode.length == 3 && j == 2) {
                        relationNode3.add("");
                    }
                }
            }
            //relationNode3.add
        }

        return relationNode3;
    }
}
