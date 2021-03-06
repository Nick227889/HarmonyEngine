package com.harmony.engine.data;

import com.harmony.engine.io.Editor;
import com.harmony.engine.utils.Status;
import com.harmony.engine.utils.gameObjects.GameObject;
import com.harmony.engine.utils.textures.Texture;
import javafx.scene.control.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectData {

    public static String projectName;
    public static String author;
    public static String versionID;

    public static ArrayList<Texture> textures = new ArrayList<>();
    public static ArrayList<GameObject> gameObjects = new ArrayList<>();
    public static HashMap<String, GameObject> hierarchy = new HashMap<>();

    public static void reset() {
        projectName = "";
        author = "";
        versionID = "";
        textures.clear();
        gameObjects.clear();
        hierarchy.clear();
    }

    public static void save(File directory) {
        Status.setCurrentStatus(Status.Type.SAVING);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = dbFactory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element rootElement = document.createElement("Project");
            document.appendChild(rootElement);

            ProjectData.addAttributes(rootElement, document); // Add in the elements

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(document);

            StreamResult file = new StreamResult(new File(directory.getPath() + "/" + directory.getName() + ".hyproj"));

            transformer.transform(source, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Status.setCurrentStatus(Status.Type.READY);
    }

    public static void addAttributes(Element rootElement, Document document) {
        rootElement.appendChild(createElement(document, VALUE, "ProjectName", projectName));
        rootElement.appendChild(createElement(document, VALUE, "Author", author));
        rootElement.appendChild(createElement(document, VALUE, "VersionID", versionID));

        ProjectData.addTextureAttributes(rootElement, document);
        ProjectData.addGameObjectAttributes(rootElement, document);
        ProjectData.addHierarchyAttributes(rootElement, document);
    }

    private static void addTextureAttributes(Element rootElement, Document document) {
        Element texturesElement = createContainerElement(document, "Textures");

        for(Texture texture : ProjectData.textures) {
            texturesElement.appendChild(createTextureElement(document, texture.path, texture.name, texture.id));
        }

        rootElement.appendChild(texturesElement);
    }

    private static void addGameObjectAttributes(Element rootElement, Document document) {
        Element gameObjectsElement = createContainerElement(document, "GameObjects");

        for(GameObject gameObject : gameObjects) {
            gameObjectsElement.appendChild(createGameObjectElement(document, gameObject));
        }

        rootElement.appendChild(gameObjectsElement);
    }

    private static void addHierarchyAttributes(Element rootElement, Document document) {
        Element hierarchyElement = createContainerElement(document, "Hierarchy");

        for(Map.Entry<TreeItem<String>, GameObject> entry : Editor.getHierarchy().entrySet()) {
            hierarchyElement.appendChild(createHObjectElement(document, entry));
        }

        rootElement.appendChild(hierarchyElement);
    }

    public static final String VALUE = "value";
    public static final String TEXTURE = "texture";
    public static final String GAME_OBJECT = "gameObject";
    public static final String H_OBJECT = "hObject";

    public static Element createElement(Document document, String type, String name, String value) {
        Element node = document.createElement(type);

        node.setAttribute("name", name);
        node.setAttribute("value", value);

        return node;
    }

    public static Element createTextureElement(Document document, String path, String name, int id) {
        Element node = document.createElement(TEXTURE);

        node.setAttribute("path", path);
        node.setAttribute("name", name);
        node.setAttribute("id", Integer.toString(id));

        return node;
    }

    public static Element createGameObjectElement(Document document, GameObject gameObject) {
        Element node = document.createElement(GAME_OBJECT);

        node.setAttribute("data", DataUtils.saveGameObject(gameObject));

        return node;
    }

    public static Element createHObjectElement(Document document, Map.Entry<TreeItem<String>, GameObject> entry) {
        Element node = createContainerElement(document, H_OBJECT);

        Element oElement = createGameObjectElement(document, entry.getValue());

        node.setAttribute("key", entry.getKey().getValue());
        node.setAttribute("gameObject", oElement.getAttribute("data"));

        return node;
    }

    public static Element createContainerElement(Document document, String name) { return document.createElement(name); }

    public static void load(File directory) {
        ProjectData.reset();

        File inputFile = new File(directory.getPath() + "/" + directory.getName() + ".hyproj");

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            document.getDocumentElement().normalize();

            // Load in all of the nodes
            loadValueNodes(document.getElementsByTagName(VALUE));
            loadTextureNodes(document.getElementsByTagName(TEXTURE));
            loadGameObjectNodes(document.getElementsByTagName(GAME_OBJECT));
            loadHierarchyNodes(document.getElementsByTagName(H_OBJECT));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadValueNodes(NodeList nList) {
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;

                switch (eElement.getAttribute("name")) {
                    case "ProjectName":
                        projectName = eElement.getAttribute("value");
                        break;
                    case "Author":
                        author = eElement.getAttribute("value");
                        break;
                    case "VersionID":
                        versionID = eElement.getAttribute("value");
                        break;
                }
            }
        }

    }

    private static void loadTextureNodes(NodeList nList) {
        for(int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                Texture texture = new Texture(eElement.getAttribute("path").trim(),
                        eElement.getAttribute("name").trim(),
                        Integer.parseInt(eElement.getAttribute("id").trim()));
                textures.add(texture.id, texture);
            }
        }
    }

    private static void loadGameObjectNodes(NodeList nList) {
        for(int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;

                GameObject gameObject = DataUtils.loadGameObject(eElement.getAttribute("data"));

                if(gameObject != null) gameObjects.add(gameObject);
            }
        }
    }

    private static void loadHierarchyNodes(NodeList nList) {
        for(int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;

                hierarchy.put(eElement.getAttribute("key"), DataUtils.loadGameObject(eElement.getAttribute("gameObject")));
            }
        }
    }
}