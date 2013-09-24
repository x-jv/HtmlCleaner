/*  Copyright (c) 2006-2013, the HtmlCleaner Project
    All rights reserved.

    Redistribution and use of this software in source and binary forms,
    with or without modification, are permitted provided that the following
    conditions are met:

    * Redistributions of source code must retain the above
      copyright notice, this list of conditions and the
      following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the
      following disclaimer in the documentation and/or other
      materials provided with the distribution.

    * The name of HtmlCleaner may not be used to endorse or promote
      products derived from this software without specific prior
      written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/

package org.htmlcleaner;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>DOM serializer - creates xml DOM.</p>
 */
public class DomSerializer {
	
    /**
     * The Regex Pattern to recognize a CDATA block.
     */
    private static final Pattern CDATA_PATTERN =
        Pattern.compile("<!\\[CDATA\\[.*(\\]\\]>|<!\\[CDATA\\[)", Pattern.DOTALL);

    protected CleanerProperties props;
    protected boolean escapeXml = true;

    public DomSerializer(CleanerProperties props, boolean escapeXml) {
        this.props = props;
        this.escapeXml = escapeXml;
    }

    public DomSerializer(CleanerProperties props) {
        this(props, true);
    }

    public Document createDOM(TagNode rootNode) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        
        
        Document document;
        
        //
        // Where a DOCTYPE is supplied in the input, ensure that this is in the output DOM. See issue #27
        //
        if (rootNode.getDocType() != null){
        	String qualifiedName = rootNode.getDocType().getPart1();
        	String publicId = rootNode.getDocType().getPublicId();
        	String systemId = rootNode.getDocType().getSystemId();
            DocumentType documentType = impl.createDocumentType(qualifiedName, publicId, systemId);
            document = impl.createDocument(rootNode.getNamespaceURIOnPath(""), qualifiedName, documentType);
        } else {
        	document = builder.newDocument();
        	Element rootElement = document.createElement(rootNode.getName());
        	document.appendChild(rootElement);
        }

        createSubnodes(document, (Element)document.getDocumentElement(), rootNode.getAllChildren());

        return document;
    }
    
    /**
     * Perform CDATA transformations if the user has specified to use CDATA inside scripts and style elements.
     * 
     * @param document the W3C Document to use for creating new DOM elements
     * @param element the W3C element to which we'll add the text content to
     * @param bufferedContent the buffered text content on which we need to perform the CDATA transformations
     * @param item the current HTML Cleaner node being processed
     */
    private void flushContent(Document document, Element element, StringBuffer bufferedContent, Object item)
    {
        if (bufferedContent.length() > 0 && !(item instanceof ContentNode)) {
            // Flush the buffered content
            boolean specialCase = this.props.isUseCdataForScriptAndStyle() && isScriptOrStyle(element);
            String content = bufferedContent.toString();

            if (this.escapeXml && !specialCase) {
                content = Utils.escapeXml(content, this.props, true);
            } else if (specialCase) {
                content = processCDATABlocks(content);
            }

            // Generate a javascript comment in front on the CDATA block so that it works in IE6 and when
            // serving XHTML under a mimetype of HTML.
            if (specialCase) {
                element.appendChild(document.createTextNode("//"));
                element.appendChild(document.createCDATASection("\n" + content + "\n//"));
            } else {
                element.appendChild(document.createTextNode(content));
            }

            bufferedContent.setLength(0);
        }
    }
    
    /**
     * Remove any existing CDATA section and unencode HTML entities that are not inside a CDATA block.
     * 
     * @param content the text input to transform
     * @return the transformed content that will be wrapped inside a CDATA block
     */
    private String processCDATABlocks(String content)
    {
        StringBuffer result = new StringBuffer();
        Matcher matcher = CDATA_PATTERN.matcher(content);
        int cursor = 0;
        while (matcher.find()) {
            result.append(StringEscapeUtils.unescapeHtml4(content.substring(cursor, matcher.start())));
            result.append(content.substring(matcher.start() + 9, matcher.end() - matcher.group(1).length()));
            cursor = matcher.end() - matcher.group(1).length() + 3;
        }
        // Copy the remaining text data in the result buffer
        if (cursor < content.length()) {
            result.append(StringEscapeUtils.unescapeHtml4(content.substring(cursor)));
        }
        // Ensure ther's no invalid <![CDATA[ or ]]> remaining.
        String contentResult = result.toString().replace("<![CDATA[", "").replace("]]>", "");

        return contentResult;
    }

    protected boolean isScriptOrStyle(Element element) {
        String tagName = element.getNodeName();
        return "script".equalsIgnoreCase(tagName) || "style".equalsIgnoreCase(tagName);
    }
    /**
     * encapsulate content with <[CDATA[ ]]> for things like script and style elements
     * @param element
     * @return true if <[CDATA[ ]]> should be used.
     */
    protected boolean dontEscape(Element element) {
        // make sure <script src=..></script> doesn't get turned into <script src=..><[CDATA[]]></script>
        // TODO check for blank content as well.
        return props.isUseCdataForScriptAndStyle() && isScriptOrStyle(element) && !element.hasChildNodes();
    }
    
    private void createSubnodes(Document document, Element element, List tagChildren) {
    	StringBuffer bufferedContent = new StringBuffer();
    	
    	if (tagChildren != null) {
            for(Object item : tagChildren) {
                if (item instanceof CommentNode) {
                    CommentNode commentNode = (CommentNode) item;
                    Comment comment = document.createComment( commentNode.getContent() );
                    element.appendChild(comment);
                } else if (item instanceof ContentNode) {
                    ContentNode contentNode = (ContentNode) item;
                    String content = contentNode.getContent();
                    boolean specialCase = dontEscape(element);
                    if (escapeXml && !specialCase) {
                        content = Utils.escapeXml(content, props, true);
                    }
                    element.appendChild( specialCase ? document.createCDATASection(content) : document.createTextNode(content) );
                } else if (item instanceof TagNode) {
                    TagNode subTagNode = (TagNode) item;
                    Element subelement = document.createElement( subTagNode.getName() );
                    Map attributes =  subTagNode.getAttributes();
                    Iterator entryIterator = attributes.entrySet().iterator();
                    while (entryIterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) entryIterator.next();
                        String attrName = (String) entry.getKey();
                        String attrValue = (String) entry.getValue();
                        if (escapeXml) {
                            attrValue = Utils.escapeXml(attrValue, props, true);
                        }
                        subelement.setAttribute(attrName, attrValue);
                    }

                    // recursively create subnodes
                    createSubnodes(document, subelement, subTagNode.getAllChildren());

                    element.appendChild(subelement);
                } else if (item instanceof List) {
                    List sublist = (List) item;
                    createSubnodes(document, element, sublist);
                }
            }
            flushContent(document, element, bufferedContent, null);
        }
    }

}