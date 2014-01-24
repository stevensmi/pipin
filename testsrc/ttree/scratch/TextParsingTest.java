package ttree.scratch;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;

import org.junit.Test;

import ttree.scratch.protocol.TextParsing;

public class TextParsingTest {

	@Test
	public final void testQuoteIfWs() {
		assertEquals("hello", TextParsing.quoteIfWs("hello"));
		assertEquals("\"\"", TextParsing.quoteIfWs(""));
		assertEquals("\" \"", TextParsing.quoteIfWs(" "));
		assertEquals("\"hello world\"", TextParsing.quoteIfWs("hello world"));
		assertEquals("\" hello world \"", TextParsing.quoteIfWs(" hello world "));
		assertEquals("\" hello\"", TextParsing.quoteIfWs(" hello"));
		assertEquals("\"hello \"", TextParsing.quoteIfWs("hello "));
		assertEquals("\"hello\tworld\"", TextParsing.quoteIfWs("hello\tworld"));
	}

	@Test
	public final void testQuotedText() {
		assertEquals(null, TextParsing.quotedText(new Scanner("")));
		assertEquals(null, TextParsing.quotedText(new Scanner("\"")));
		assertEquals(null, TextParsing.quotedText(new Scanner("\"")));
		assertEquals("", TextParsing.quotedText(new Scanner("\"\"")));
		assertEquals("hello world", TextParsing.quotedText(new Scanner("hello world")));
		assertEquals("hello world", TextParsing.quotedText(new Scanner("\"hello world")));
		assertEquals("hello world", TextParsing.quotedText(new Scanner("hello world\"")));
		assertEquals("hello world", TextParsing.quotedText(new Scanner("\"hello world\"")));
	}

	@Test
	public final void testWsDelimitedText() {
		assertEquals("hello", TextParsing.nextText(new Scanner("hello")));
		assertEquals("hello", TextParsing.nextText(new Scanner("hello world")));
		assertEquals("hello", TextParsing.nextText(new Scanner(" hello world")));
		assertEquals(null, TextParsing.nextText(new Scanner(" ")));
		assertEquals(null, TextParsing.nextText(new Scanner("\t")));
	}

}
