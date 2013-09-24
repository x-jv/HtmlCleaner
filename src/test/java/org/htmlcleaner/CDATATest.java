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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class CDATATest {
	
    private static HtmlCleaner cleaner;

    @BeforeClass
    public static void setUp() throws Exception
    {
        cleaner = new HtmlCleaner();
    }

    
    @Test
    public void nonSafeCDATA(){
    	String testData = ""
        	+ "<script type=\"text/javascript\">"
        	+ "<![CDATA[\n"
        	+ "function helloWorld() {\n"
        	+ "};\n"
        	+ "]]>\n"
        	+ "</script>";
        	
        	TagNode cleaned = cleaner.clean(testData);
        	TagNode script = cleaned.findElementByName("script", true);
        	
        	
        	//
        	// We should have a CData node for the CDATA section
        	//
        	assertTrue(script.getAllChildren().get(0) instanceof CData);
        	CData cdata = (CData)script.getAllChildren().get(0);
        	
        	String content = cdata.getContentWithoutStartAndEndTokens();
        	assertEquals("\nfunction helloWorld() {\n};\n", content);
    }
    
    @Test
    public void safeOutput(){
    	String testData = ""
        	+ "<script type=\"text/javascript\">"
        	+ "<![CDATA[\n"
        	+ "function helloWorld() {\n"
        	+ "};\n"
        	+ "]]>\n"
        	+ "</script>";
        	
        	TagNode cleaned = cleaner.clean(testData);
        	TagNode script = cleaned.findElementByName("script", true);
        	
        	
        	//
        	// We should have a CData node for the CDATA section
        	//
        	assertTrue(script.getAllChildren().get(0) instanceof CData);
        	CData cdata = (CData)script.getAllChildren().get(0);
        	
        	String content = cdata.getContentWithoutStartAndEndTokens();
        	assertEquals("\nfunction helloWorld() {\n};\n", content);
        	
        	String safeContent = cdata.getContent();
        	assertEquals("/*<![CDATA[*/\nfunction helloWorld() {\n};\n/*]]>*/", safeContent);
    }
    
    /**
     * For a CDATA section we need to ignore '<' and '>' and keep going to keep the content
     * within a single CData instance.
     */
    @Test
    public void safeCDATAAlternate(){
    	String testData = ""
        	+ "<script type=\"text/javascript\">\n"
        	+ "// <![CDATA[\n"
        	+ "function escapeForXML(origtext) {\n"
        	+ " return origtext.replace(/\\&/g,'&'+'amp;').replace(/</g,'&'+'lt;')\n"
        	+ " .replace(/>/g,'&'+'gt;').replace(/\'/g,'&'+'apos;').replace(/\"/g,'&'+'quot;');"
        	+ "}\n"
        	+ "// ]]>\n"
        	+ "</script>";
        	
        	TagNode cleaned = cleaner.clean(testData);
        	TagNode script = cleaned.findElementByName("script", true);
        	
        	
        	//
        	// We should have a CData node for the CDATA section
        	//
        	assertTrue(script.getAllChildren().get(0) instanceof CData);
        	CData cdata = (CData)script.getAllChildren().get(0);
        	
        	String content = cdata.getContentWithoutStartAndEndTokens();
        	assertEquals("\nfunction escapeForXML(origtext) {\n return origtext.replace(/\\&/g,'&'+'amp;').replace(/</g,'&'+'lt;')\n .replace(/>/g,'&'+'gt;').replace(/'/g,'&'+'apos;').replace(/\"/g,'&'+'quot;');}\n", content);
    }
    
    /**
     * For a CDATA section we need to ignore '<' and '>' and keep going to keep the content
     * within a single CData instance
     */
    @Test
    public void safeCDATA(){
    	String testData = ""
        	+ "<script type=\"text/javascript\">\n"
        	+ "/*<![CDATA[*/\n"
        	+ "function escapeForXML(origtext) {\n"
        	+ " return origtext.replace(/\\&/g,'&'+'amp;').replace(/</g,'&'+'lt;')\n"
        	+ " .replace(/>/g,'&'+'gt;').replace(/\'/g,'&'+'apos;').replace(/\"/g,'&'+'quot;');"
        	+ "}\n"
        	+ "/*]]>*/>\n"
        	+ "</script>";
        	
        	TagNode cleaned = cleaner.clean(testData);
        	TagNode script = cleaned.findElementByName("script", true);
        	
        	
        	//
        	// We should have a CData node for the CDATA section
        	//
        	assertTrue(script.getAllChildren().get(0) instanceof CData);
        	CData cdata = (CData)script.getAllChildren().get(0);
        	
        	String content = cdata.getContentWithoutStartAndEndTokens();
        	assertEquals("\nfunction escapeForXML(origtext) {\n return origtext.replace(/\\&/g,'&'+'amp;').replace(/</g,'&'+'lt;')\n .replace(/>/g,'&'+'gt;').replace(/'/g,'&'+'apos;').replace(/\"/g,'&'+'quot;');}\n", content);
    }
    
    @Test
    public void style(){
    	String testData = "<style type=\"text/css\">/*<![CDATA[*/\n#ampmep_188 { }\n/*]]>*/</style>";
    	TagNode cleaned = cleaner.clean(testData);
    	TagNode style = cleaned.findElementByName("style", true);
    	
    	String content = (((CData)style.getAllChildren().get(0)).getContentWithoutStartAndEndTokens());
    	assertTrue(style.getAllChildren().get(0) instanceof CData);
    	assertEquals("\n#ampmep_188 { }\n", content);

    }

}
