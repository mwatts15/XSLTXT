/**
 * XSLTXT - An alternative syntax for xslt
 * Copyright (C) 2002 Alex Moffat
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.zanthan.xsltxt;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.zanthan.xsltxt.exception.CharNotAllowedException;
import com.zanthan.xsltxt.exception.EOFException;
import com.zanthan.xsltxt.exception.EndOfBlockException;
import com.zanthan.xsltxt.exception.SyntaxException;

public class Lexer {
    
    private static final Category logCat =
	Logger.getLogger(Lexer.class);

    /**
     * A pushback reader as we need to put a character we've already
     * read back onto the stream.
     */
    private PushbackReader r = null;
    /**
     * The current indentation.
     */
    private int indent = 0;
    /**
     * The current line number
     */
    private int lineNumber = 1;
    /**
     * A stack of tokens.
     */
    private Token savedToken = null;
    /**
     * The comments
     */
    private ArrayList comments = new ArrayList();
    /**
     * The character array used to accumulate characters read from the
     * reader.
     */
    private int charMax = 4096;
    private int charIndex = -1;
    private char[] chars = new char[charMax];
    /**
     * Identifies the source being read by this lexer
     */
    private String sourceIdent = null;
    
    /**
     * Create a new Lexer.
     * @param r the reader to read characters from
     * @param sourceIdent an identifier used in error messages
     */
    public Lexer(Reader r, String sourceIdent) {
	this.r = new PushbackReader(r);
	this.sourceIdent = sourceIdent;
    }

    /**
     * Returns the line number the lexer is currently positioned on.
     * @return the line number
     */
    int getLineNumber() {
	return lineNumber;
    }

    /**
     * Returns the indent of the next character the lexer will read.
     * @return the indent
     */
    int getIndent() {
	return indent;
    }
    
    /**
     * Get the next token from the lexer.
     * @return the next token
     * @throws EOFException if no more tokens are available
     * @throws IOException if there is an io problem
     */
    public Token next()
	throws SyntaxException, IOException {
        if (logCat.isDebugEnabled())
            logCat.debug("next(" +
                      ")");
	
	if (savedToken == null && comments.isEmpty())
	    readToken(true);
	Token t = pop();
	return t;
    }

    /**
     * Get the next token from the lexer provided it starts on an
     * indent greater than startingIndent.
     * @param startingIndent the token must be indented more than this.
     */
    public Token next(int startingIndent)
	throws SyntaxException, IOException {
        if (logCat.isDebugEnabled())
            logCat.debug("next(" +
                      startingIndent +
                      ")");

	Token tok = next();

	if ((tok.getIndent() <= startingIndent) &&
	    !(tok instanceof CommentToken)) {
	    logCat.warn("EndOfBlockException");
	    throw new EndOfBlockException(getSourceIdent(), tok, startingIndent);
	}
	
	return tok;
    }
    
    /**
     * Check if there is another token available.
     * @return true if there is another token, false otherwise
     */
    public boolean hasNext()
	throws IOException, SyntaxException {
        if (logCat.isDebugEnabled())
            logCat.debug("hasNext(" +
                      ")");

	if (savedToken == null && comments.isEmpty()) {
	    return readToken(false);
	} else {
	    return true;
	}
    }

    /**
     * Check if there is another token available with an
     * indentation greater than the indent passed in.
     * @param startingIndent the limiting indent
     * @return true if there is another token, false otherwise
     */
    public boolean hasNext(int startingIndent)
	throws IOException, SyntaxException {

	if (logCat.isDebugEnabled())
	    logCat.debug("hasNext(" +
			 startingIndent +
			 ")");
	if (hasNext()) {
	    Token tok = next();
	    int indent = tok.getIndent();
	    push(tok);
	    if (logCat.isDebugEnabled())
		logCat.debug("hasNext(...) indent " +
			     indent);
	    if ((indent > startingIndent) || (tok instanceof CommentToken))
		return true;
	    else
		return false;
	} else {
	    return false;
	}
    }
    
    /**
     * Push a token back so that next will return it.
     * @param t the token to push back
     */
    void push(Token t) {
	if (t instanceof CommentToken) {
	    comments.add(t);
	} else {
	    if (savedToken != null)
		logCat.error("push(" + t + ") called with savedToken = " + savedToken);
	    else
		savedToken = t;
	    if (!comments.isEmpty()) {
		for (Iterator it = comments.iterator(); it.hasNext();) {
		    ((CommentToken)it.next()).setIndent(savedToken.getIndent());
		}
	    }
	}
    }

    Token pop() {
	if (comments.isEmpty()) {
	    Token temp = savedToken;
	    savedToken = null;
	    return temp;
	} else {
	    return (Token)comments.remove(0);
	}
    }
    
    /**
     * Return a string that identifies the source being processed by
     * this lexer.
     * @return an identifier of some sort
     */
    String getSourceIdent() {
	return sourceIdent;
    }
    
    /**
     * Read the next token from the reader and put it on the
     * bottom of the stack.
     * @throws NoSuchElementException if no token can be read
     * @throws IOException if there is an io problem
     */
    private boolean readToken(boolean throwEOFException)
	throws SyntaxException, IOException {

	boolean readSomething = false;
	while (true) {
	    int i = skipLeadingWhitespace(throwEOFException);
	    if (i == -1)
		break;
	    char c = (char)i;
	    
	    if (c == '"') {
		readQuotedToken(c);
	    } else if (c == '<') {
		readElementToken(c);
	    } else if (c == '#') {
		readCommentToken(c);
	    } else {
		readSimpleToken(c);
	    }
	    readSomething = true;
	    if (c != '#')
		break;
	}

	return readSomething;
    }

    private void readSimpleToken(char c)
	throws IOException, SyntaxException {

	int startOfTokenIndent = indent;
	int startOfTokenLine = lineNumber;

	if ((c == '(') || (c == ')')) {
	    if (charIndex++ == charMax)
		extendCharArray();
	    chars[charIndex] = c;
	    push(new SimpleToken(startOfTokenIndent,
				 startOfTokenLine,
				 new String(chars, 0, ++charIndex)));
	    charIndex = -1;
	    return;
	}
	
	readUnquotedString(c);

	push(new SimpleToken(startOfTokenIndent,
			     startOfTokenLine,
			     new String(chars, 0, ++charIndex)));
	charIndex = -1;
    }

    private void readQuotedToken(char c)
	throws IOException, EOFException {

	int startOfTokenIndent = indent;
	int startOfTokenLine = lineNumber;

	readQuotedString();
	
	push(new QuotedToken(startOfTokenIndent,
			     startOfTokenLine,
			     new String(chars, 0, ++charIndex))); 
	charIndex = -1;
    }

    private void readElementToken(char c) 
	throws IOException, SyntaxException {

	if (logCat.isDebugEnabled())
	    logCat.debug("readElementToken(" + c + ")");
	
	int startOfTokenIndent = indent;
	int startOfTokenLine = lineNumber;
	
	int i = r.read();
	++indent;
	// Read the name
	while (true) {
 	    if (i == -1)
		break;
	    c = (char)i;
	    if (Character.isWhitespace(c)) {
		--indent;
		r.unread(i);
		break;
	    } else if (c == '>') {
		break;
	    }
	    if (charIndex++ == charMax)
		extendCharArray();
	    chars[charIndex] = c;
	    i = r.read();
	    ++indent;
	}

	ElementToken tok =
	    new ElementToken(startOfTokenIndent,
			     startOfTokenLine,
			     new String(chars, 0, ++charIndex));
	
	if (logCat.isDebugEnabled())
	    logCat.debug("readElementToken() now found " +
			 new String(chars, 0, charIndex));
	
	charIndex = -1;
	
	if ((i == -1) || (c == '>')) {
	    push(tok);
	    return;
	}

	// Now read the attributes
	while (true) {
	    c = (char)skipLeadingWhitespace(true);
	    if (logCat.isDebugEnabled())
		logCat.debug("readElementToken() skipped to " + c);
	
	    if (c == '>')
		break;

	    readAttributeName(c);
	    String attributeName = new String(chars, 0, ++charIndex);
	    charIndex = -1;

	    c = (char)skipLeadingWhitespace(true);
	    if (c != '=') {
		logCat.warn("CharNotAllowedException");
		throw new CharNotAllowedException(lineNumber, indent, c, '=');
	    }
	    i = r.read();
	    ++indent;
	    if (i == -1) {
		logCat.warn("EOFException");
		throw new EOFException();
	    }
	    c = (char)i;
	    if (c != '"') {
		logCat.warn("CharNotAllowedException");
		throw new CharNotAllowedException(lineNumber, indent, c, '"');
	    }
	    readQuotedString();
	    String attributeValue = new String(chars, 0, ++charIndex);
	    charIndex = -1;
	    tok.addAttribute(attributeName, attributeValue);
	}

	push(tok);
    }

    private void readCommentToken(char c)
	throws IOException {

	int startOfTokenIndent = indent;
	int startOfTokenLine = lineNumber;

	int i = 0;
	while (true) {
	    i = r.read();
	    ++indent;
	    if (i == -1)
		break;
	    c = (char)i;
	    if (c == '\n') {
		++lineNumber;
		indent = 0;
		break;
	    } else if (c == '\r') {
		++lineNumber;
		indent = 0;
		i = r.read();
		if (i != -1) {
		    c = (char)i;
		    if (c != '\n')
			r.unread(i);
		}
		break;
	    }
	    if (charIndex++ == charMax)
		extendCharArray();
	    chars[charIndex] = c;
	}

	push(new CommentToken(startOfTokenIndent,
			      startOfTokenLine,
			      new String(chars, 0, ++charIndex)));
			      
	charIndex = -1;
    }


    private int skipLeadingWhitespace(boolean throwEOFException) 
	throws EOFException, IOException {

	// Skip leading whitespace
	while (true) {
	    int i = r.read();
	    ++indent;
	    if (i == -1) {
		if (throwEOFException) {
		    logCat.warn("EOFException");
		    throw new EOFException();
		} else {
		    return i;
		}
	    }
	    char c = (char)i;
	    if (Character.isWhitespace(c)) {
		handleNewlineOrTab(c);
	    } else {
		return i;
	    }
	}
    }

    private void handleNewlineOrTab(char c)
	throws IOException {
	
	if (c == '\n') {
	    ++lineNumber;
	    indent = 0;
	} else if (c == '\r') {
	    ++lineNumber;
	    indent = 0;
	    int i = r.read();
	    if (i != -1) {
		c = (char)i;
		if (c != '\n')
		    r.unread(i);
	    }
	} else if (c == '\t') {
	    if (logCat.isInfoEnabled())
		logCat.info("expanding tab at line " +
			    lineNumber +
			    " indent " +
			    indent +
			    " in " +
			    sourceIdent);
	    indent += 7;
	}
    }

    /**
     * Read a string of none whitespace characters. The string ends
     * with the first whitespace character, ", ), or comma.
     * @param c the first character in the string
     */
    private void readUnquotedString(char c)
	throws IOException, EOFException {

	if (charIndex++ == charMax)
	    extendCharArray();
	chars[charIndex] = c;
	
	while (true) {
	    int i = r.read();
	    ++indent;
 	    if (i == -1)
		return;
	    c = (char)i;
	    if (Character.isWhitespace(c)) {
		--indent;
		r.unread(i);
		return;
	    }
	    if ((c == '"') || (c == ')') || (c == ',')) {
		--indent;
		r.unread(i);
		return;
	    }
	    if (charIndex++ == charMax)
		extendCharArray();
	    chars[charIndex] = c;
	}

    }

    /**
     * Read a string of none whitespace characters up to the next
     * whitespace character or =.
     * @param c the first character in the string
     */
    private void readAttributeName(char c)
	throws IOException, EOFException {

	if (charIndex++ == charMax)
	    extendCharArray();
	chars[charIndex] = c;
	
	while (true) {
	    int i = r.read();
	    ++indent;
 	    if (i == -1)
		return;
	    c = (char)i;
	    if (Character.isWhitespace(c)) {
		--indent;
		r.unread(i);
		return;
	    }
	    if (c == '=') {
		--indent;
		r.unread(i);
		return;
	    }
	    if (charIndex++ == charMax)
		extendCharArray();
	    chars[charIndex] = c;
	}

    }

    private void readQuotedString()
	throws IOException, EOFException {

	if (logCat.isDebugEnabled())
	    logCat.debug("starting readQuotedString() line " +
			 lineNumber + " indent " + indent);

	boolean escaped = false;
	while (true) {
	    int i = r.read();
	    if (i == -1) {
		logCat.warn("EOFException");
		throw new EOFException();
	    }
	    ++indent;
	    char c = (char)i;
	    handleNewlineOrTab(c);
	    if (escaped) {
		switch (c) {
		case '"':
		    if (charIndex++ == charMax)
			extendCharArray();
		    chars[charIndex] = '"';
		    break;
		case 'n':
		    if (charIndex++ == charMax)
			extendCharArray();
		    chars[charIndex] = '\n';
		    break;
		case 't':
		    if (charIndex++ == charMax)
			extendCharArray();
		    chars[charIndex] = '\t';
		    break;
		case 'r':
		    if (charIndex++ == charMax)
			extendCharArray();
		    chars[charIndex] = '\r';
		    break;
		case '\\':
		    if (charIndex++ == charMax)
			extendCharArray();
		    chars[charIndex] = '\\';
		    break;
		default:
		    if ((charIndex += 2) >= charMax)
			extendCharArray();
		    chars[charIndex] = '\\';
		    chars[charIndex] = c;
		}
		escaped = false;
	    } else {
		if (c == '"')
		    break;
		else if (c == '\\')
		    escaped = true;
		else {
		    if (charIndex++ == charMax)
			extendCharArray();
		    chars[charIndex] = c;
		}
	    }
	}

	if (logCat.isDebugEnabled())
	    logCat.debug("ending readQuotedString() {{{" +
			 new String(chars, 0, (charIndex + 1)) +
			 "}}} line " +
			 lineNumber +
			 " indent " +
			 indent);
    }

    private void extendCharArray() {
	char[] temp = new char[chars.length * 2];
	System.arraycopy(chars, 0, temp, 0, chars.length);
	chars = temp;
	charMax = chars.length;
    }
}
