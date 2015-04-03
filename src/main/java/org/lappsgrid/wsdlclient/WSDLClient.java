/**********************************************************************************************************************
 Copyright [2014] [Chunqi SHI (chunqi.shi@hotmail.com)]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **********************************************************************************************************************/
package org.lappsgrid.wsdlclient;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.Proxy;
import java.net.URLConnection;
import java.util.AbstractList;
import java.util.List;
import java.net.MalformedURLException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * Provide interface for mapping identity to services.
 *
 * @author Chunqi Shi(chqshi@gmail.com)
 *
 */
public class WSDLClient {

    public static void copy(InputStream is, Writer writer) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ( (line = br.readLine()) != null)
            writer.write(line);
        br.close();
        is.close();
        writer.close();
    }

    Service service = null;
    Call call = null;
    ServiceConf conf = null;

    final public ServiceConf getConf() {
        return conf;
    }


    String wsdl = null;
    XPathUtils xpath = null;

    public void init(String url) throws WSDLClientException {
        try {
            init(new URL(url));
        }catch(MalformedURLException e){
            throw new WSDLClientException(e);
        }
    }

    public void init(URL url) throws WSDLClientException {
        Service service = new Service();
        try {
            call = (Call) service.createCall();
        }catch(Exception e){
            throw new WSDLClientException(e);
        }
        conf = new ServiceConf();
        try {
            StringWriter writer = new StringWriter();
            copy(url.openStream(), writer);
            conf.setWsdlAddress(url.toString());
            wsdl = writer.toString();
            xpath = XPathUtils.newInstance(wsdl);
            conf.setSoapAddress(xpath
                    .getText("definitions/service/port/address/@location"));
        } catch (Exception e) {
            throw new WSDLClientException(e);
        }
    }

    public void init(File file) throws WSDLClientException {
        Service service = new Service();
        try {
            call = (Call) service.createCall();
        }catch(Exception e){
            throw new WSDLClientException(e);
        }
        conf = new ServiceConf();
        try {
            StringWriter writer = new StringWriter();
            copy(new FileInputStream(file), writer);
            conf.setWsdlAddress(file.getAbsolutePath());
            wsdl = writer.toString();
            xpath = XPathUtils.newInstance(wsdl);
            conf.setSoapAddress(xpath
                    .getText("definitions/service/port/address/@location"));
        } catch (Exception e) {
            throw new WSDLClientException(e);
        }
    }

    public void init(URL url, Proxy proxy) throws WSDLClientException {
        Service service = new Service();
        try {
            call = (Call) service.createCall();
        }catch(Exception e){
            throw new WSDLClientException(e);
        }
        conf = new ServiceConf();
        try {
            StringWriter writer = new StringWriter();
            URLConnection con = url.openConnection(proxy);
            copy(con.getInputStream(), writer);
            conf.setWsdlAddress(url.toString());
            wsdl = writer.toString();
            xpath = XPathUtils.newInstance(wsdl);
            conf.setSoapAddress(xpath
                    .getText("definitions/service/port/address/@location"));
        } catch (Exception e) {
            throw new WSDLClientException(e);
        }
    }

    public void register(@SuppressWarnings("rawtypes") Class cls, QName qname) {
//        call.registerTypeMapping(cls, qname, BeanSerializerFactory.class,
//                BeanDeserializerFactory.class);
        call.registerTypeMapping(cls, qname,
                new org.apache.axis.encoding.ser.BeanSerializerFactory(cls, qname),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(cls, qname));
    }


    public void authorize(String username, String password) {
        call.setUsername(username);
        call.setPassword(password);
        conf.setUsername(username);
        conf.setPassword(password);
    }

    public Object callService(String namespace, String operationName, Object... params)
            throws MalformedURLException, RemoteException {
        call.setTargetEndpointAddress(new URL(conf.getSoapAddress()));
        String uri = new URL(new URL(namespace), operationName).toString();
        System.out.println(uri);
        call.setSOAPActionURI (uri);
//        SOAPHeaderElement header = new SOAPHeaderElement (" "," SystemInital ");
//        header.setNamespaceURI (" ");
//        header.addChildElement (_requestXml);
//        call.addHeader (header);
        return call.invoke(namespace, operationName, params);
    }


    public Object callService(QName operationName, Object... params)
            throws MalformedURLException, RemoteException {
        call.setTargetEndpointAddress(new URL(conf.getSoapAddress()));
        // prepare operator & parameters.
        // method.
        String uri = new URL(new URL(operationName.getNamespaceURI()), operationName.getLocalPart()).toString();
//        System.out.println(uri);
        call.setSOAPActionURI (uri);
        call.setOperationName(operationName);
        // parameters
        return call.invoke(params);
    }

    public String[] getParameterOrder(String operation) {
        String list = null;
        if (operation == null) {
            list = xpath
                    .getText("definitions/portType/operation/@parameterOrder");

        } else {
            list = xpath
                    .getText("definitions/portType/operation[@name='"
                    + operation + "']/@parameterOrder");
        }
        if (list == null || list.length() == 0) {
            return new String []{};
        }
        // System.out.println("list=" + list + ".");
        return list.split("\\s+");
    }

    public static class XPathUtils {
        public static XPathUtils newInstance(String obj) {
            XPathUtils instance = new XPathUtils();
            try {
                instance.init(obj);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return instance;
        }

        public void init(String obj) throws SAXException, IOException,
                ParserConfigurationException {
            if (obj != null) {
                if (obj.indexOf('<') > -1) {
                    this.doc = parse(new InputSource(new StringReader(obj)));
                } else {
                    this.xmlPath = obj;
                    this.doc = parse(obj);
                }
            }
        }

        public String getXmlPath() {
            return xmlPath;
        }

        public Document getDoc() {
            return doc;
        }

        String xmlPath;
        Document doc;

        public String getText(String xpath) {
            return getText(doc, xpath);
        }

        public Node getNode(String xpath) {
            return getNode(doc, xpath);
        }

        public List<Node> getNodes(String xpath) {
            return getNodes(doc, xpath);
        }

        public Map<String, String> getAttrs(String xpath) {
            return getAttrs(getNode(xpath));
        }

        public static Document parse(String xmlPath) throws SAXException,
                IOException, ParserConfigurationException {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(xmlPath);
        }

        public static Document parse(InputSource is) throws SAXException,
                IOException, ParserConfigurationException {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(is);
        }

        public static XPathExpression xpath(String xpath)
                throws XPathExpressionException {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            XPathExpression xPathExpression = xPath.compile(xpath);
            return xPathExpression;
        }

        public static String getText(Node node, String xpath) {
            if (node == null || xpath == null) {
                return null;
            }
            try {
                XPathExpression xPathExpression = xpath(xpath);
                String result = (String) xPathExpression.evaluate(node,
                        XPathConstants.STRING);
                if (result != null) {
                    return result.trim();
                } else {
                    return null;
                }
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Node getNode(Node node, String xpath) {
            if (node == null || xpath == null) {
                return null;
            }
            try {
                XPathExpression xPathExpression = xpath(xpath);
                return (Node) xPathExpression.evaluate(node,
                        XPathConstants.NODE);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static List<Node> getNodes(Node node, String xpath) {
            if (node == null || xpath == null) {
                return null;
            }
            try {
                XPathExpression xPathExpression = xpath(xpath);
                final NodeList nodes = (NodeList) xPathExpression
                        .evaluate(node, XPathConstants.NODESET);

                if (nodes == null) {
                    return null;
                }

                return new AbstractList<Node>() {
                    @Override
                    public Node get(int index) {
                        return nodes.item(index);
                    }

                    @Override
                    public int size() {
                        return nodes.getLength();
                    }
                };
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Map<String, String> getAttrs(Node node) {
            if (node == null) {
                return null;
            }
            Map<String, String> map = new HashMap<String, String>();
            NamedNodeMap nnm = node.getAttributes();
            for (int i = 0; i < nnm.getLength(); i++) {
                Attr attr = (Attr) nnm.item(i);
                map.put(attr.getName(), attr.getValue().trim());
            }
            return map;
        }
    }

    public static class ServiceConf {

        public String getWsdlAddress() {
            return wsdlAddress;
        }

        public void setWsdlAddress(String wsdlAddress) {
            this.wsdlAddress = wsdlAddress;
        }

        protected String name;
        protected String username;
        protected String password;
        protected String wsdlAddress;
        protected String soapAddress;

        public String getSoapAddress() {
            return this.soapAddress;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setSoapAddress(String soapAddress) {
            this.soapAddress = soapAddress;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String toString() {
            return toString(this);
        }

        public static String toString(Object obj) {
            StringBuilder result = new StringBuilder();
            result.append("{");
            // determine fields declared in this class only (no fields of
            // superclass)
            Field[] fields = obj.getClass().getDeclaredFields();
            // print field names paired with their values
            for (Field field : fields) {
                try {
                    if (Modifier.isStatic(field.getModifiers())
                            || Modifier.isFinal(field.getModifiers())
                            || Modifier.isPrivate(field.getModifiers())) {
                        continue;
                    }
                    result.append(field.getName());
                    result.append(":");
                    // requires access to private field:
                    result.append(field.get(obj));
                } catch (IllegalAccessException ex) {
                    System.out.println(ex);
                }
                result.append(";\t");
            }
            result.append("}.");
            return result.toString();
        }
    }
}
