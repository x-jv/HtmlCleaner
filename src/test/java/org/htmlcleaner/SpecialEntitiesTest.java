/*  Copyright (c) 2006-2014, the HtmlCleaner Project
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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

public class SpecialEntitiesTest extends AbstractHtmlCleanerTest {
	
	/*
	 * Check that we handle "&;" - see issue #98
	 */
	@Test
	public void twoCharacterEntity() throws IOException{
		this.serializer = new SimpleHtmlSerializer(this.cleaner.getProperties());
		String input = "<html><head></head><body><p>&; test &;</p></body></html>";
		String expected = "<html><head></head><body><p>&amp;; test &amp;;</p></body></html>";
		try {
			assertCleaned(input, expected);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	/*
	 * Check that we don't convert HTML entities like &nbsp; when outputting using a HTML serializer - see bug #118
	 */
	@Test
	public void htmlEntities()  throws IOException{
		String input = "<html><head></head><body><p>&nbsp;&pound;</p></body></html>";
		cleaner.getProperties().setAdvancedXmlEscape(false);
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
		
		TagNode cleaned = cleaner.clean(input);
		Serializer ser = new SimpleHtmlSerializer(cleaner.getProperties());
		StringWriter writer = new StringWriter();
		ser.serialize(cleaned, writer);
		assertEquals(input, writer.toString());
	}

}
