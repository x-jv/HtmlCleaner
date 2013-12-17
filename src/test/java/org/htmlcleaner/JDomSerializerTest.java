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

import java.io.IOException;

import org.junit.Test;

public class JDomSerializerTest extends AbstractHtmlCleanerTest {


	/**
	 * See issue #95
	 */
	@Test
	public void testNPE(){
		String validhtml5StringCode = "<html></html>";
		CleanerProperties props = new CleanerProperties();
		props.setOmitHtmlEnvelope(true);
		TagNode tagNode = new HtmlCleaner(props).clean(validhtml5StringCode);
		new JDomSerializer(props, true).createJDom(tagNode);
	}
	
	/**
	 * See issue 106
	 * @throws IOException
	 */
    @Test
    public void CDATA() throws Exception{
    	cleaner.getProperties().setUseCdataForScriptAndStyle(true);
    	cleaner.getProperties().setOmitCdataOutsideScriptAndStyle(true);
    	String initial = readFile("src/test/resources/test22.html");
    	TagNode tagNode = cleaner.clean(initial);
    	JDomSerializer ser = new JDomSerializer(cleaner.getProperties());
    	ser.createJDom(tagNode);
    }
	/**
	 * See issue 106
	 * @throws IOException
	 */
    @Test
    public void noCDATA() throws Exception{
    	cleaner.getProperties().setUseCdataForScriptAndStyle(false);
    	cleaner.getProperties().setOmitCdataOutsideScriptAndStyle(true);
    	String initial = readFile("src/test/resources/test22.html");
    	TagNode tagNode = cleaner.clean(initial);
    	JDomSerializer ser = new JDomSerializer(cleaner.getProperties());
    	ser.createJDom(tagNode);
    }
}
