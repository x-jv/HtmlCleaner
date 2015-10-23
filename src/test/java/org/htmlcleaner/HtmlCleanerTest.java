package org.htmlcleaner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Ignore;
import org.junit.Test;

public class HtmlCleanerTest extends AbstractHtmlCleanerTest {
	

	/**
	 * Test for bug #156
	 * @throws IOException
	 */
	@Test
	public void testStyleIsNotRemoved() throws IOException{
		final String original = "<div><style type=\"text/css\">h1 {color:black;}</style>42</div>";
		final String expected = "<div><style type=\"text/css\">h1 {color:black;}</style>42</div>";

		cleaner.getProperties().setOmitHtmlEnvelope(true);
		TagNode node = cleaner.clean(original);
		StringWriter writer = new StringWriter();
		serializer = new SimpleHtmlSerializer(cleaner.getProperties());
		serializer.write(node, writer, "UTF-8");
		assertEquals(expected, writer.toString());
	}
	
	/**
	 * Test for bug #154
	 * @throws IOException
	 */
	@Test
	public void attributeSerialization() throws IOException{
	    final String original =     "<p data-double-quote-attr=\"foo&quot;bar'baz\" data-single-quote-attr='foo\"bar&apos;baz'>text</p>";
	    final String expectedHtml = "<p data-double-quote-attr=\"foo&quot;bar'baz\" data-single-quote-attr=\"foo&quot;bar'baz\">text</p>";
	    final String expectedXml =  "<p data-double-quote-attr=\"foo&quot;bar&apos;baz\" data-single-quote-attr=\"foo&quot;bar&apos;baz\">text</p>";

	    cleaner.getProperties().setOmitHtmlEnvelope(true);
	    TagNode node = cleaner.clean(original);
	    StringWriter writer = new StringWriter();
	    serializer = new SimpleHtmlSerializer(cleaner.getProperties());
	    serializer.write(node, writer, "UTF-8");
	    assertEquals(expectedHtml, writer.toString());

	    //
	    // TODO this should also work for XML in some cases - I've commented this out for now but will return to it later.
	    //
	    //writer = new StringWriter();
	    //serializer = new SimpleXmlSerializer(cleaner.getProperties());
	    //assertEquals(expectedXml, writer.toString());
	}

	/**
	 * This is to test issue #157
	 * @throws IOException
	 */
	@Test
	public void math() throws IOException{
		String initial = "<math><math></math></math>";
		String expected = "<html><head /><body><math></math><math></math></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
		cleaner.getProperties().setNamespacesAware(true);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
        
		initial = "<mtable><mtr></mtr></mtable>";
		expected = "<html><head /><body><mtable><mtr></mtr></mtable></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
		cleaner.getProperties().setNamespacesAware(true);
        cleaned = cleaner.clean(initial);
        output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	
	/**
	 * This is to test issue #131
	 * @throws IOException
	 */
	@Ignore // We should fix this, but it isn't critical
	@Test
	public void moveTableContent() throws IOException{
		String initial = "<table><tbody><h2>hi</h2><tr><td></td></tr></tbody></table>";
		String expected = "<html><head /><body><h2>hi</h2><table><tbody><tr><td></td></tr></tbody></table></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	/**
	 * This is to test issue #136
	 * @throws IOException
	 */
	@Test
	public void emptyXmlns() throws IOException{
		String initial = "<html><head><meta xmlns=\"\" name=\"a\" content=\"1\"><meta xmlns=\"\" name=\"b\" content=\"2\"><meta xmlns=\"\" name=\"c\" content=\"3\"><meta xmlns=\"\" name=\"d\" content=\"4\"></head><body></body></html>";
		String expected = "<html><head><meta name=\"a\" content=\"1\" /><meta name=\"b\" content=\"2\" /><meta name=\"c\" content=\"3\" /><meta name=\"d\" content=\"4\" /></head><body></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
		cleaner.getProperties().setNamespacesAware(true);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	
	/**
	 * This is to test issue #139
	 * @throws IOException
	 */
	@Test
	public void optGroupTest() throws IOException{
		String initial = "<select><optgroup><option>x</option></optgroup></select>";
		String expected = "<html><head /><body><select><optgroup><option>x</option></optgroup></select></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	/**
	 * This is to test issue #149
	 * @throws IOException 
	 */
	@Test
	public void rbTest() throws IOException{
		
		String initial = "<p><br /> <br /><rb></rb><rtc><rt></rtc><br /></p>";
		String expected = "<html><head /><body><p><br /> <br /><br /><ruby><rb /><rtc><rt /></rtc></ruby></p></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	// See bug #147
	@Test
	public void testCorrectUlStructure(){
		String initial = "<UL><LI>1</LI><LI>2</LI><UL></LI></UL><LI>3</LI></UL>";
		String expected = "<html><head /><body><ul><li>1</li><li>2</li><ul></ul><li>3</li></ul></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	// See bug #145
	@Test
	@Ignore // We do want to fix this, but its not critical
	public void testCorrectTableStructure(){
		String initial = "<table><tr><td><div><td></div></td> </tr></table>";
		String expected = "<html><head /><body><table><tbody><tr><td><div>/div></td><td></td></tr></tbody></table></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	// See bug #146
	@Test
	public void testMissingTr(){
		String initial = "<table><td>banana</td></table>";
		String expected = "<html><head /><body><table><tbody><tr><td>banana</td></tr></tbody></table></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
    }
	
	// See bug #129
	@Test
	public void testLegend(){
		String initial = "<form><legend>banana";
		String expected = "<html><head /><body><form><fieldset><legend>banana</legend></fieldset></form></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
    }
	
	// See bug #126
	@Test
	public void testFragment(){
		String initial = "<table><rt><td>";
		String expected = "<html><head /><body><ruby><rt /></ruby><table><tbody><tr><td></td></tr></tbody></table></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
    }
	
	// See bug #140
	@Test
	public void testSource(){
		String initial = "<source />";
		String expected = "<html><head /><body><audio><source /></audio></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
    }
	
	@Test
	public void testTwiddleTR(){
		String initial = "<table><tr><td>test</td></rt></table>";
		String expected = "<html><head /><body><table><tbody><tr><td>test</td></tr></tbody></table></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
    }
	
	@Test
	public void testMissingRuby(){
		String initial = "<rt>test</rt>";
		String expected = "<html><head /><body><ruby>"+initial+"</ruby></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
    }
	
	@Test
	public void testMissingRt(){
		String initial = "<rp>(</rp>ㄏㄢˋ<rp>)</rp>";
		String expected = "<html><head /><body><ruby>"+initial+"</ruby></body></html>";
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);	
	}
	
	/**
	 * Label tag - see Bug #138
	 */
	@Test
	public void testLabel(){
		String initial = "<form><label for=\"male\">Male</label><input type=\"radio\" name=\"sex\" id=\"male\" value=\"male\" /><label for=\"female\">Female</label><input type=\"radio\" name=\"sex\" id=\"female\" value=\"female\" /><input type=\"submit\" value=\"Submit\" /></form>";
		String expected = "<html><head /><body>"+initial+"</body></html>";
		cleaner.getProperties().setNamespacesAware(true);
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);		
	}
	
	/**
	 * Option tags have two fatal tags - see Bug #137
	 */
	@Test
	public void testSelect(){
		String initial = "<select><option>test1</option></select>";
		String expected = "<html><head /><body><select><option>test1</option></select></body></html>";
		cleaner.getProperties().setNamespacesAware(true);
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);		
	}
	
	/**
	 * This is to test that we don't get an NPE with a malformed HTTPS XHTML namespace. See issue #133
	 */
	@Test
	public void testNPEWithHttpsNamespace(){
		String initial="<html xmlns=\"https://www.w3.org/1999/xhtml\"><head></head><body><SPAN><BR></SPAN><EM></EM></body></html>";
		String expected="<html xmlns=\"http://www.w3.org/1999/xhtml\"><head /><body><span><br /></span><em></em></body></html>";
		cleaner.getProperties().setNamespacesAware(true);
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);		
	}
	
	/**
	 * This is to test issue #132
	 * @throws IOException 
	 */
	@Test
	public void classCastTest() throws IOException{
		String initial = readFile("src/test/resources/test30.html");
		TagNode node = cleaner.clean(initial);
	}
	
	/**
	 * This is to test issue #93
	 */
	@Test
	public void closingDiv(){
		//
		// Check that when a tag is self-closing, we close it and start again rather than
		// let it remain open and enclose the following tags
		//
		String initial = "<div id=\"y\"/><div id=\"z\">something</div>";
		String expected = "<html>\n<head />\n<body><div id=\"y\"></div><div id=\"z\">something</div></body></html>";
        TagNode cleaned = cleaner.clean(initial);
        String output = serializer.getAsString(cleaned);
        assertEquals(expected, output);
        
        //
        // This should also result in the same output
        //
        initial = "<div id=\"y\"></div><div id=\"z\">something</div>";
        cleaned = cleaner.clean(initial);
        output = serializer.getAsString(cleaned);
        assertEquals(expected, output);
	}


    /**
     * This is to test issue #67
     */
    @Test
    public void testXmlNoExtraWhitesapce(){
        CleanerProperties cleanerProperties = new CleanerProperties();
        cleanerProperties.setOmitXmlDeclaration(false);
        cleanerProperties.setOmitDoctypeDeclaration(false);
        cleanerProperties.setIgnoreQuestAndExclam(true);
        cleanerProperties.setAddNewlineToHeadAndBody(false);
 
    	HtmlCleaner theCleaner = new HtmlCleaner(cleanerProperties);

        String initial = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head /><body><p>test</p></body></html>\n";
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head /><body><p>test</p></body></html>";

        TagNode cleaned = theCleaner.clean(initial);
                
        Serializer theSerializer = new SimpleXmlSerializer(theCleaner.getProperties());
        String output = theSerializer.getAsString(cleaned);
        assertEquals(expected, output);
    }
    
    /**
     * Test for #2901.
     */
    @Test
	public void testWhitespaceInHead() throws IOException {
		String initial = readFile("src/test/resources/Real_1.html");
		String expected = readFile("src/test/resources/Expected_1.html");
		assertCleaned(initial, expected);
	}

	/**
	 * Mentioned in #2901 - we should eliminate the first <tr>
	 * TODO: Passes but not with ideal result.
	 */
    @Test
	public void testUselessTr() throws IOException {
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
		String start = "<html><head /><body><table>";
		String end = "</body></html>";
		assertCleaned(start + "<tr><tr><td>stuff</td></tr>" + end,
				//start+"<tbody><tr><td>stuff</td></tr></tbody></table>" + end // "ideal" output
				start + "<tbody><tr /><tr><td>stuff</td></tr><tr></tr></tbody></table>" + end // actual
		);
	}

	/**
	 * Collapsing empty tr to <tr />
	 */
    @Test
	public void testUselessTr2() throws IOException {
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
		String start = "<html><head /><body><table>";
		String end = "</table></body></html>";
		assertCleaned(start + "<tr> </tr><tr><td>stuff</td></tr>" + end,
				start + "<tbody><tr /><tr><td>stuff</td></tr></tbody>" + end);
	}

	/**
	 * For #2940
	 */
    @Test
	public void testCData() throws IOException {
		cleaner.getProperties().setAddNewlineToHeadAndBody(false);
		String start = "<html><head>";
		String end = "</head><body>1</body></html>";
		assertCleaned(start + "<style type=\"text/css\">/*<![CDATA[*/\n#ampmep_188 { }\n/*]]>*/</style>" + end,
				start + "<style type=\"text/css\">/*<![CDATA[*/\n#ampmep_188 { }\n/*]]>*/</style>" + end);
	}

	/**
	 * Report in issue #64 as causing issues.
	 * @throws Exception
	 */
    @Test
	public void testChineseParsing() throws Exception {
	    String initial = readFile("src/test/resources/test-chinese-issue-64.html");
	    TagNode node = cleaner.clean(initial);
	    final TagNode[] imgNodes = node.getElementsByName("img", true);
	    assertEquals(5, imgNodes.length);
	}
    
    /**
     * Report in issue #70 as causing issues.
     * @throws Exception
     */
    @Test
    public void testOOME_70() throws Exception {
        String initial = readFile("src/test/resources/oome_70.html");
        TagNode node = cleaner.clean(initial);
        final TagNode[] imgNodes = node.getElementsByName("img", true);
        assertEquals(17, imgNodes.length);
    }

    @Test
    public void testOOME_59() throws Exception {
        String in = "<html><body><table><fieldset><legend>";
        CleanerProperties cp = new CleanerProperties();
        cp.setOmitUnknownTags(true);
        HtmlCleaner c = new HtmlCleaner(cp);
        TagNode root = c.clean(in);
        assertEquals(1, root.getElementsByName("legend", true).length);
    }
    
    /**
     * Check that we no longer require block-level restrictions for anchors, as per HTML5. See issue #82
     * @throws IOException
     */
	@Test
	public void noAnchorBlockLevelRestriction() throws IOException{
        
		String initial = readFile("src/test/resources/test24.html");
		String expected = readFile("src/test/resources/test24_expected.html"); 
		
		assertCleaned(initial,expected);
	}
}
