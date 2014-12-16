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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import org.jdom.CDATA;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;

import com.zanthan.xsltxt.exception.EOFException;
import com.zanthan.xsltxt.exception.EndOfBlockException;
import com.zanthan.xsltxt.exception.RequiredAttributeNotFoundException;
import com.zanthan.xsltxt.exception.StatementNotAllowedException;
import com.zanthan.xsltxt.exception.SyntaxException;
import com.zanthan.xsltxt.exception.TokenNotAllowedException;

/**
 * The base class for all statements in xslt.
 */
public class Statement {

    private static final Category logCat =
        Logger.getLogger(Statement.class);

    static StatementFactory factory =
        StatementFactory.getInstance();

    static String xslNamespaceURI =
        "http://www.w3.org/1999/XSL/Transform";

    /**
     * Used in get and set of optional and required attributes to
     * identify source for set and target for get.
     */
    static final int XML = 1;
    static final int TXT = 2;

    /**
     * The reserved words in txtxslt
     */
    static Set reservedWords = new HashSet();

    // These correspond to xml elements in xslt
    static final String APPLY_TEMPLATES        = "apply";
    static final String APPLY_IMPORTS          = "apply-imports";
    static final String ATTRIBUTE              = "attribute";
    static final String ATTRIBUTE_SET          = "attribute-set";
    static final String CALL                   = "call";
    static final String CDATA                  = "cdata";
    static final String CHOOSE                 = "choose";
    static final String COMMENT                = "comment";
    static final String COPY                   = "copy";
    static final String COPY_OF                = "copy-of";
    static final String DECIMAL_FORMAT         = "decimal";
    static final String ELEMENT                = "element";
    static final String FALLBACK               = "fallback";
    static final String FOR_EACH               = "for-each";
    static final String IF                     = "if";
    static final String IMPORT                 = "import";
    static final String INCLUDE                = "include";
    static final String KEY                    = "key";
    static final String MESSAGE                = "msg";
    // MESSAGE_FATAL is message with the terminate attribute set
    static final String MESSAGE_FATAL          = "msg-fatal";
    // NAMESPACE is not in xslt. txtxslt uses it to handle
    // namespace declarations
    static final String NAMESPACE              = "namespace";
    static final String NAMESPACE_ALIAS        = "namespace-alias";
    static final String NUMBER                 = "number";
    static final String OTHERWISE              = "otherwise";
    static final String OUTPUT                 = "output";
    static final String PARAM                  = "param";
    static final String PRESERVE               = "preserve";
    static final String PROCESSING_INSTRUCTION = "processing-instruction";
    static final String SORT                   = "sort";
    static final String STRIP                  = "strip";
    static final String STYLESHEET             = "stylesheet";
    static final String TEMPLATE               = "tpl";
    static final String TEXT                   = "tx";
    // TEXT_NO_ESCAPE is TEXT with the no escape attribute set
    static final String TEXT_NO_ESCAPE         = "tx-no-esc";
    static final String VALUE                  = "val";
    // VALUE_NO_ESCAPE is VALUE with the no escape attribute set
    static final String VALUE_NO_ESCAPE        = "val-no-esc";
    static final String VARIABLE               = "var";
    static final String WHEN                   = "when";

    // These correspond to attributes on xslt elements. Refered to as
    // fields in the code.
    static final String SORT_SELECT            = ".by";
    static final String CASE_ORDER             = ".case-order";
    static final String CDATA_SECTION_ELEMENTS = ".cdata-section-elements";
    static final String COUNT                  = ".count";
    static final String DATA_TYPE              = ".data-type";
    static final String DECIMAL_SEP            = ".decimal-sep";
    static final String DIGIT                  = ".digit";
    static final String DOCTYPE_PUBLIC         = ".doctype-public";
    static final String DOCTYPE_SYSTEM         = ".doctype-system";
    static final String ENCODING               = ".encoding";
    static final String EXTENSION_PREFIXES     = ".extension-element-prefixes";
    static final String EXCLUDE_PREFIXES       = ".exclude-result-prefixes";
    static final String FROM                   = ".from";
    static final String FORMAT                 = ".format";
    static final String GROUP_SEP              = ".group-sep";
    static final String GROUP_SIZE             = ".group-size";
    static final String ID                     = ".id";
    static final String INFINITY               = ".infinity";
    static final String INDENT                 = ".indent";
    static final String LANG                   = ".lang";
    static final String LETTER_VALUE           = ".letter-value";
    static final String LEVEL                  = ".level";
    static final String MATCH                  = ".match";
    static final String MEDIA_TYPE             = ".media-type";
    static final String METHOD                 = ".method";
    static final String MINUS_SIGN             = ".minus-sign";
    static final String MODE                   = ".mode";
    static final String NAME                   = ".name";
    static final String NAN                    = ".nan";
    static final String OMIT_XML_DECLARATION   = ".omit-xml-declaration";
    static final String ORDER                  = ".order";
    static final String PATTERN_SEP            = ".pattern-sep";
    static final String PERCENT                = ".percent";
    static final String PER_MILLE              = ".per-mille";
    static final String PRIORITY               = ".priority";
    static final String SELECT                 = ".select";
    static final String STANDALONE             = ".standalone";
    static final String USE_ATTRIBUTE_SETS     = ".use-attribute-sets";
    static final String VERSION                = ".version";
    static final String ZERO_DIGIT             = ".zero-digit";

    static {
        reservedWords.add(APPLY_TEMPLATES);
        reservedWords.add(APPLY_IMPORTS);
        reservedWords.add(ATTRIBUTE);
        reservedWords.add(ATTRIBUTE_SET);
        reservedWords.add(SORT_SELECT);
        reservedWords.add(CALL);
        reservedWords.add(CASE_ORDER);
        reservedWords.add(CDATA);
        reservedWords.add(CDATA_SECTION_ELEMENTS);
        reservedWords.add(CHOOSE);
        reservedWords.add(COPY);
        reservedWords.add(COPY_OF);
        reservedWords.add(COUNT);
        reservedWords.add(DATA_TYPE);
        reservedWords.add(DECIMAL_FORMAT);
        reservedWords.add(DECIMAL_SEP);
        reservedWords.add(DIGIT);
        reservedWords.add(DOCTYPE_PUBLIC);
        reservedWords.add(DOCTYPE_SYSTEM);
        reservedWords.add(ELEMENT);
        reservedWords.add(ENCODING);
        reservedWords.add(EXCLUDE_PREFIXES);
        reservedWords.add(EXTENSION_PREFIXES);
        reservedWords.add(FALLBACK);
        reservedWords.add(FORMAT);
        reservedWords.add(FOR_EACH);
        reservedWords.add(FROM);
        reservedWords.add(GROUP_SEP);
        reservedWords.add(GROUP_SIZE);
        reservedWords.add(ID);
        reservedWords.add(IF);
        reservedWords.add(IMPORT);
        reservedWords.add(INCLUDE);
        reservedWords.add(INDENT);
        reservedWords.add(INFINITY);
        reservedWords.add(KEY);
        reservedWords.add(LANG);
        reservedWords.add(LETTER_VALUE);
        reservedWords.add(LEVEL);
        reservedWords.add(MATCH);
        reservedWords.add(MEDIA_TYPE);
        reservedWords.add(MESSAGE);
        reservedWords.add(MESSAGE_FATAL);
        reservedWords.add(METHOD);
        reservedWords.add(MINUS_SIGN);
        reservedWords.add(MODE);
        reservedWords.add(NAME);
        reservedWords.add(NAMESPACE);
        reservedWords.add(NAMESPACE_ALIAS);
        reservedWords.add(NAN);
        reservedWords.add(NUMBER);
        reservedWords.add(OMIT_XML_DECLARATION);
        reservedWords.add(ORDER);
        reservedWords.add(OTHERWISE);
        reservedWords.add(PARAM);
        reservedWords.add(PATTERN_SEP);
        reservedWords.add(PERCENT);
        reservedWords.add(PER_MILLE);
        reservedWords.add(PRESERVE);
        reservedWords.add(PRIORITY);
        reservedWords.add(PROCESSING_INSTRUCTION);
        reservedWords.add(SELECT);
        reservedWords.add(SORT);
        reservedWords.add(STANDALONE);
        reservedWords.add(STRIP);
        reservedWords.add(STYLESHEET);
        reservedWords.add(TEMPLATE);
        reservedWords.add(TEXT);
        reservedWords.add(TEXT_NO_ESCAPE);
        reservedWords.add(USE_ATTRIBUTE_SETS);
        reservedWords.add(VALUE);
        reservedWords.add(VALUE_NO_ESCAPE);
        reservedWords.add(VARIABLE);
        reservedWords.add(VERSION);
        reservedWords.add(WHEN);
        reservedWords.add(ZERO_DIGIT);
    };

    // These need to be before the initializers so that
    // they are available when the classes referenced
    // in the initializers need them.
    final static HashSet charTemplateChildren = new HashSet();
    final static HashSet templateChildren = new HashSet();
    final static HashSet topLevelChildren = new HashSet();

    static {
        charTemplateChildren.add(Apply.class);
        charTemplateChildren.add(Call.class);
        charTemplateChildren.add(ApplyImports.class);
        charTemplateChildren.add(ForEach.class);
        charTemplateChildren.add(ValueOf.class);
        charTemplateChildren.add(CopyOf.class);
        charTemplateChildren.add(Number.class);
        charTemplateChildren.add(Choose.class);
        charTemplateChildren.add(IfStatement.class);
        charTemplateChildren.add(Text.class);
        charTemplateChildren.add(Copy.class);
        charTemplateChildren.add(Variable.class);
        charTemplateChildren.add(Message.class);
        charTemplateChildren.add(Fallback.class);
        charTemplateChildren.add(StringStatement.class);
    };

    static {
        templateChildren.addAll(charTemplateChildren);
        templateChildren.add(ProcessingInstruction.class);
        templateChildren.add(Comment.class);
        templateChildren.add(Element.class);
        templateChildren.add(Attribute.class);
        templateChildren.add(XMLStatement.class);
    }

    static {
        topLevelChildren.add(ImportStatement.class);
        topLevelChildren.add(Include.class);
        topLevelChildren.add(Strip.class);
        topLevelChildren.add(Preserve.class);
        topLevelChildren.add(Output.class);
        topLevelChildren.add(Key.class);
        topLevelChildren.add(DecimalFormat.class);
        topLevelChildren.add(AttributeSet.class);
        topLevelChildren.add(Variable.class);
        topLevelChildren.add(Param.class);
        topLevelChildren.add(Template.class);
        topLevelChildren.add(NamespaceAlias.class);
        topLevelChildren.add(StringStatement.class);
        topLevelChildren.add(XMLStatement.class);
    };

    // A description of this statement.
    StatementDescriptor desc = null;

    // The children of this statement
    List children = null;
    // The namespaces introduced by this statement
    List namespaces = null;

    // The values of the required and optional fields for
    // this statement. The label for each field is found
    // in the corresponding position is the corresponding
    // list in the statement descriptor for this statement.
    String[] requiredFieldValues = null;
    String[] optionalFieldValues = null;

    // The name of this statement, if it has one, and if it
    // is to be output in txtxslt using DEFINE.
    //String name = null;

    /**
     * Create a new statement providing only a descriptor.
     *
     */
    Statement(StatementDescriptor desc) {

        this.desc = desc;
    }

    /**
     * Convert the description of this statement to xml and add it as a
     * child of the root passed in.
     *
     * @param name the name of the token recognized for this element
     * @param root the element to add the description to
     */
    public void descriptionToXML(String name, org.jdom.Element root) {
        if (logCat.isDebugEnabled())
            logCat.debug("descriptionToXML(" +
                    name +
                    ", " +
                    root +
                    ")");
        desc.toXML(name, root);
    }

    void setupValueArrays() {
        requiredFieldValues =
            new String[desc.getRequiredFields().size()];
        optionalFieldValues =
            new String[desc.getOptionalFields().size()];
        children = new ArrayList();
        namespaces = new ArrayList();
    }

    void init(org.jdom.Element e)
        throws SyntaxException {

        initNamespaces(e);

        initRequiredFields(e);

        initOptionalFields(e);
    }

    void initNamespaces(org.jdom.Element e)
        throws SyntaxException {

        for (Iterator it = e.getAdditionalNamespaces().iterator(); it.hasNext();) {
            namespaces.add(new Namespace((org.jdom.Namespace)it.next()));
        }
    }

    void initRequiredFields(org.jdom.Element e)
        throws SyntaxException {

        List l = desc.getRequiredFields();
        for (int i = 0; i < requiredFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            String v = getAttributeValue(e, f.getAttributeName());
            if (v == null) {
                logCat.warn("RequiredAttributeNotFoundException");
                throw new RequiredAttributeNotFoundException(getXMLLocalName(),
                        f.getAttributeName());
            }
            setRequiredValue(XML, f.getAttributeName(), i, v);
        }
    }

    void initOptionalFields(org.jdom.Element e)
        throws SyntaxException {

        List l = desc.getOptionalFields();
        for (int i = 0; i < optionalFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            String v = getAttributeValue(e, f.getAttributeName());
            setOptionalValue(XML, f.getAttributeName(), i, v);
        }
    }

    // keep
    void init(Token tok, Lexer lex)
        throws IOException, SyntaxException {

        int startingIndent = tok.getIndent();

        initRequiredFields(lex, startingIndent);

        initOptionalFields(lex, startingIndent);

    }

    void initRequiredFields(Lexer lex, int startingIndent)
        throws IOException, SyntaxException {

        List l = desc.getRequiredFields();
        for (int i = 0; i < requiredFieldValues.length; ++i) {
            try {
                Token valueToken = lex.next(startingIndent);
                if (!(valueToken instanceof QuotedToken)) {
                    logCat.warn("TokenNotAllowedException");
                    throw new TokenNotAllowedException(lex.getSourceIdent(),
                            valueToken,
                            "Quoted String");
                }
                StatementDescriptor.FieldDescriptor f =
                    (StatementDescriptor.FieldDescriptor)l.get(i);
                setRequiredValue(TXT, f.getAttributeName(), i, valueToken.getValue());
            } catch (SyntaxException se) {
                se.setContainingStatement(desc.getTXTName());
                throw se;
            }
        }

    }

    void initOptionalFields(Lexer lex, int startingIndent)
        throws IOException, SyntaxException {

        List l = desc.getOptionalFields();
READ:
        while (lex.hasNext()) {
            Token tok = lex.next();
            if ((tok.getIndent() <= startingIndent) ||
                    !(tok instanceof SimpleToken)) {
                lex.push(tok);
                break;
                    }
            String name = tok.getValue();
            for (int i = 0; i < l.size(); ++i) {
                StatementDescriptor.FieldDescriptor f =
                    (StatementDescriptor.FieldDescriptor)l.get(i);
                if ((f.getTXTName() != null) &&
                        f.getTXTName().equals(name)) {
                    Token valueToken = lex.next(startingIndent);
                    if (!(valueToken instanceof QuotedToken)) {
                        logCat.warn("TokenNotAllowedException");
                        throw new TokenNotAllowedException(lex.getSourceIdent(),
                                valueToken,
                                "Quoted String");
                    }
                    setOptionalValue(TXT, f.getTXTName(), i, valueToken.getValue());
                    continue READ;
                        }
            }
            lex.push(tok);
            break;
        }
    }

    // keep
    String getAttributeValue(org.jdom.Element e, String name) {
        String av = e.getAttributeValue(name);
        if ((av != null) && !av.equals(""))
            return av;
        else
            return null;
    }

    // keep
    void setOptionalValue(int source, String attributeName, String value)
        throws SyntaxException {

        int i = indexOf(desc.getOptionalFields(), attributeName);
        if (i == -1) {
            logCat.warn("SyntaxException");
            throw new SyntaxException();
        }
        setOptionalValue(source, attributeName, i, value);
    }

    void setOptionalValue(int source, String attributeName,
            int attributeIndex, String value) {
        optionalFieldValues[attributeIndex] = value;
    }

    // keep
    String getOptionalValue(int target, String attributeName)
        throws SyntaxException {

        int i = indexOf(desc.getOptionalFields(), attributeName);
        if (i == -1) {
            logCat.warn("SyntaxException");
            throw new SyntaxException();
        }
        return getOptionalValue(target, attributeName, i);
    }

    String getOptionalValue(int target, String attributeName,
            int attributeIndex) {
        return optionalFieldValues[attributeIndex];
    }

    // keep
    void setRequiredValue(int source, String attributeName, String value)
        throws SyntaxException {
        int i = indexOf(desc.getRequiredFields(), attributeName);
        if (i == -1) {
            logCat.warn("SyntaxException");
            throw new SyntaxException();
        }
        setRequiredValue(source, attributeName, i, value);
    }

    void setRequiredValue(int source, String attributeName,
            int attributeIndex, String value) {
        requiredFieldValues[attributeIndex] = value;
    }

    // keep
    String getRequiredValue(int target, String attributeName)
        throws SyntaxException {
        int i = indexOf(desc.getRequiredFields(), attributeName);
        if (i == -1) {
            logCat.warn("SyntaxException");
            throw new SyntaxException();
        }
        return getRequiredValue(target, attributeName, i);
    }

    String getRequiredValue(int target, String attributeName,
            int attributeIndex) {
        return requiredFieldValues[attributeIndex];
    }

    // keep
    int indexOf(List l, String attributeName) {

        for (int i = 0; i < l.size(); ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            if (f.getAttributeName().equals(attributeName))
                return i;
        }
        return -1;
    }

    // keep
    void processXMLContent(org.jdom.Element e)
        throws SyntaxException {

        if (logCat.isDebugEnabled())
            logCat.debug("processXMLContent(" +
                    e +
                    ")");
        try {
            for (Iterator it = e.getContent().listIterator();
                    it.hasNext();) {
                Object o = it.next();
                if (logCat.isDebugEnabled())
                    logCat.debug("processXMLContent factory is " +
                            factory);
                if (o instanceof org.jdom.Element) {
                    addChild(factory.getStatement((org.jdom.Element)o));
                } else if (o instanceof org.jdom.Text) {
                    addChild(factory.getStatement(((org.jdom.Text)o).getText()));
                } else if (o instanceof String) {
                    addChild(factory.getStatement((String)o));
                } else if (o instanceof org.jdom.Comment) {
                    addChild(factory.getStatement((org.jdom.Comment)o));
                } else if (o instanceof CDATA) {
                    addChild(factory.getStatement((CDATA)o));
                } else {
                    logCat.warn("Could not convert " + o.getClass().getName());
                }
                    }
        } catch (SyntaxException se) {
            se.setContainingStatement(getXMLLocalName());
            throw se;
        }
    }

    void processTOKContent(int startingIndent, Lexer lex)
        throws IOException, SyntaxException {

        try {
            while (lex.hasNext()) {
                Token tok = lex.next();
                //		if ((tok instanceof CommentToken) ||
                if (tok.getIndent() > startingIndent) {
                    addChild(factory.getStatement(tok, lex));
                } else {
                    lex.push(tok);
                    break;
                }
            }
        } catch (SyntaxException se) {
            se.setContainingStatement(desc.getTXTName());
            se.setLineNumber(lex.getLineNumber());
            se.setIndent(lex.getIndent());
            throw se;
        }
    }

    void requireToken(int startingIndent, Lexer lex, String tokenValue)
        throws IOException, SyntaxException {

        if (!lex.hasNext()) {
            logCat.warn("EOFException");
            throw new EOFException();
        }

        Token tok = lex.next();
        if (tok.getIndent() <= startingIndent) {
            logCat.warn("EndOfBlockException");
            throw new EndOfBlockException(lex.getSourceIdent(), tok, startingIndent);
        }

        if (!tok.getValue().equals(tokenValue)) {
            logCat.warn("TokenNotAllowedException");
            throw new TokenNotAllowedException(lex.getSourceIdent(), tok, PARAM);
        }
    }

    /**
     * Adds children allowed by the set stored in the statement
     * descriptor plus XMLComment and Namespace
     */
    void addChild(Statement stat)
        throws SyntaxException {

        if (logCat.isDebugEnabled())
            logCat.debug("addChild(" + stat + ")");

        if (stat == null)
            return;
        if (desc.getAllowedChildren().contains(stat.getClass())) {
            children.add(stat);
        } else 	if (stat instanceof XMLComment) {
            children.add(stat);
        } else if (stat instanceof Namespace) {
            namespaces.add(stat);
        } else {
            logCat.warn("StatementNotAllowedException adding " +
                    stat +
                    " to " +
                    getClass().getName());
            StatementNotAllowedException ex =
                new StatementNotAllowedException(stat);
            ex.setContainingStatement(desc.getTXTName() +
                    " " +
                    desc.getXMLName());
        }
    }

    String getTXTName()
        throws SyntaxException {

        return desc.getTXTName();
    }

    void outputTXTName(Outputter out)
        throws SyntaxException {

        out.output(getTXTName());
        out.indent();
        out.maybeBreak();
    }

    // keep
    public void outputTXT(Outputter out)
        throws SyntaxException {

        if (desc == null)
            logCat.error("desc is null in " + getClass());

        outputTXTName(out);

        outputTXTRequiredFields(out);

        outputTXTOptionalFields(out);

        outputTXTNamespaces(out);

        outputTXTChildren(out);

        out.dedent();
        out.newline();
    }

    void outputTXTRequiredFields(Outputter out) {
        List l = desc.getRequiredFields();
        for (int i = 0; i < requiredFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            out.outputPaddedWithBreak(fqs(getRequiredValue(TXT, f.getTXTName(), i)));
        }
    }

    void outputTXTOptionalFields(Outputter out) {
        List l = desc.getOptionalFields();
        for (int i = 0; i < optionalFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            String v = getOptionalValue(TXT, f.getTXTName(), i);
            if (v != null)
                out.outputPaddedWithBreak(f.getTXTName(),
                        fqs(v));
        }
    }

    void outputTXTNamespaces(Outputter out) {
        if (namespaces.isEmpty())
            return;
        out.newline();
        for (Iterator it = namespaces.iterator(); it.hasNext();) {
            ((Namespace)it.next()).outputTXT(out);
        }
    }

    // keep
    void outputTXTChildren(Outputter out)
        throws SyntaxException {
        outputTXTChildren(children, out);
    }

    // keep
    void outputTXTChildren(List kids, Outputter out)
        throws SyntaxException {
        if (kids.isEmpty())
            return;
        out.newline();
        for (Iterator it = kids.iterator(); it.hasNext();) {
            ((Statement)it.next()).outputTXT(out);
        }
    }

    // keep
    public void outputXML(Outputter out)
        throws SyntaxException {
        if (desc == null)
            logCat.error("desc is null in " + getClass());

        out.output("<");
        out.output(getXMLQualifiedName());
        out.indent();

        outputXMLRequiredFields(out);

        outputXMLOptionalFields(out);

        outputXMLNamespaces(out);

        handleXMLChildren(out);
    }

    String getXMLLocalName() {
        return desc.getXMLName();
    }

    String getXMLQualifiedName() {
        return "xsl:" + getXMLLocalName();
    }

    String getNamespace(NamespaceTracker tracker) {
        return xslNamespaceURI;
    }

    void outputXMLRequiredFields(Outputter out) {
        List l = desc.getRequiredFields();
        for (int i = 0; i < requiredFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            out.outputAttribute(f.getAttributeName(),
                    getRequiredValue(XML, f.getAttributeName(), i));
        }
    }

    // keep
    void outputXMLOptionalFields(Outputter out) {
        List l = desc.getOptionalFields();
        for (int i = 0; i < optionalFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            String v = getOptionalValue(XML, f.getAttributeName(), i);
            if (v != null)
                out.outputAttribute(f.getAttributeName(),
                        v);
        }
    }

    void outputXMLNamespaces(Outputter out) {
        if (namespaces.isEmpty())
            return;
        for (Iterator it = namespaces.iterator(); it.hasNext();) {
            ((Namespace)it.next()).outputXML(out);
        }
    }

    /**
     * Decide whether the
     */
    void handleXMLChildren(Outputter out)
        throws SyntaxException {

        if (!hasChildren()) {
            out.output("/>");
            out.dedent();
            out.newline();
        } else {
            out.output(">");
            out.newline();

            outputXMLChildren(out);

            out.dedent();
            out.output("</");
            out.output(getXMLQualifiedName());
            out.output(">");
            out.newline();
        }
    }

    void outputXMLChildren(Outputter out)
        throws SyntaxException {
        outputXMLChildren(children, out);
    }

    // keep
    void outputXMLChildren(List kids, Outputter out)
        throws SyntaxException {
        if (kids.isEmpty())
            return;
        out.newline();
        for (Iterator it = kids.iterator(); it.hasNext();) {
            ((Statement)it.next()).outputXML(out);
        }
    }

    void outputXML(ContentHandler handler,
            NamespaceTracker tracker)
        throws SyntaxException {

        // First namespace stuff
        startPrefixMapping(handler, tracker);

        AttributesImpl attributes = new AttributesImpl();

        addXMLRequiredFields(attributes, tracker);

        addXMLOptionalFields(attributes, tracker);

        outputElement(handler,
                tracker,
                attributes);

        // End the namespaces
        endPrefixMapping(handler, tracker);
    }

    void startPrefixMapping(ContentHandler handler,
            NamespaceTracker tracker)
        throws SyntaxException {

        if (logCat.isDebugEnabled())
            logCat.debug("startPrefixMapping() namespaces " +
                    namespaces);
        if ((namespaces != null) && !namespaces.isEmpty()) {
            for (Iterator it = namespaces.iterator(); it.hasNext();) {
                ((Namespace)it.next()).startPrefixMapping(handler, tracker);
            }
        }
    }

    void addXMLRequiredFields(AttributesImpl attributes,
            NamespaceTracker tracker) {

        List l = desc.getRequiredFields();
        for (int i = 0; i < requiredFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            // Note use of TXT in call to getRequiredValue
            // below, it's meant to be here. HrefStatement
            // needs this as it must output the TXT value for
            // hfef here so that the URIResolver can feed the
            // correct file in.
            attributes.addAttribute("",
                    f.getAttributeName(),
                    "",
                    "CDATA",
                    getRequiredValue(TXT, f.getAttributeName(), i));
        }
    }

    void addXMLOptionalFields(AttributesImpl attributes,
            NamespaceTracker tracker) {
        List l = desc.getOptionalFields();
        for (int i = 0; i < optionalFieldValues.length; ++i) {
            StatementDescriptor.FieldDescriptor f =
                (StatementDescriptor.FieldDescriptor)l.get(i);
            // Note use of TXT in call to getRequiredValue
            // below, it's meant to be here to be consistent with
            // the code in addXMLRequiredFields.
            String v = getOptionalValue(TXT, f.getAttributeName(), i);
            if (v != null)
                attributes.addAttribute("",
                        f.getAttributeName(),
                        "",
                        "CDATA",
                        v);
        }
    }

    void outputElement(ContentHandler handler,
            NamespaceTracker tracker,
            AttributesImpl attributes)
        throws SyntaxException {

        String localName = getXMLLocalName();
        String qName = getXMLQualifiedName();
        String namespace = getNamespace(tracker);

        try {
            if (logCat.isDebugEnabled())
                logCat.debug("handler.startElement( " +
                        namespace + ", " +
                        localName + ", " +
                        qName + ", " +
                        attributes + ")");

            handler.startElement(namespace,
                    localName,
                    qName,
                    attributes);

            if (hasChildren())
                outputXMLChildren(handler, tracker);

            handler.endElement(namespace,
                    localName,
                    qName);
        } catch (SAXException se) {
            logCat.warn("SyntaxException");
            throw new SyntaxException(se);
        }
    }

    void endPrefixMapping(ContentHandler handler,
            NamespaceTracker tracker)
        throws SyntaxException {

        if ((namespaces != null) && !namespaces.isEmpty()) {
            for (Iterator it = namespaces.iterator(); it.hasNext();) {
                ((Namespace)it.next()).endPrefixMapping(handler, tracker);
            }
        }
    }

    void outputXMLChildren(ContentHandler handler,
            NamespaceTracker tracker)
        throws SyntaxException {
        outputXMLChildren(children, handler, tracker);
    }

    // keep
    void outputXMLChildren(List kids,
            ContentHandler handler,
            NamespaceTracker tracker)
        throws SyntaxException {
        if (kids.isEmpty())
            return;
        for (Iterator it = kids.iterator(); it.hasNext();) {
            ((Statement)it.next()).outputXML(handler, tracker);
        }
    }

    /**
     * Return true if the statement has an child statements.
     *
     * @return true or false
     */
    boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Return a string wrapped in quotes if it contains a space, a :, a
     * comma, or is a reserved word. You can pass a null value for s in
     * which case you will get a null back.
     *
     * @param s the string to quote
     * @return the string, perhaps surrounded with quotes
     */
    String qs(String s) {
        if (s == null)
            return null;
        if ((s.indexOf(' ') > -1) ||
                (s.indexOf(':') > -1) ||
                (s.indexOf(',') > -1) ||
                (s.indexOf('(') > -1) ||
                (s.indexOf(')') > -1) ||
                reservedWords.contains(s)) {
            return fqs(s);
        } else {
            return s;
        }
    }

    /**
     * Return a string wrapped in quotes. You can pass a null value for s
     * in which case you will get a null back.
     *
     * @param s the string to quote
     * @return the quoted string
     */
    String fqs(String s) {

        if (s == null)
            return null;

        int lastPos = 0;
        int pos = s.indexOf('"');
        if (pos > -1) {
            StringBuffer sb = new StringBuffer();
            sb.append('"');
            while (pos > -1) {
                sb.append(s.substring(lastPos, pos));
                sb.append('\\');
                sb.append('"');
                lastPos = pos + 1;
                if (lastPos == s.length())
                    pos = -1;
                else
                    pos = s.indexOf('"', lastPos);
            }
            if (lastPos != s.length())
                sb.append(s.substring(lastPos));
            sb.append('"');
            if (logCat.isDebugEnabled())
                logCat.debug("fqs(" + s + ") ==> " + sb.toString());
            return sb.toString();
        } else {
            return '"' + s + '"';
        }
    }

    /**
     * Replace any \ in the input string with \\. Returns the
     * original string if it contains no \ characters.
     *
     * @param s the input string
     * @return the output string
     */
    String doubleSlash(String s) {
        if (logCat.isDebugEnabled())
            logCat.debug("doubleSlash(" +
                    s +
                    ")");
        if (s == null)
            return null;
        int lastPos = 0;
        int pos = s.indexOf('\\');
        if (pos == -1)
            return s;

        StringBuffer sb = new StringBuffer();
        while (pos > -1) {
            sb.append(s.substring(lastPos, pos));
            sb.append("\\\\");
            lastPos = pos + 1;
            if (lastPos == s.length())
                pos = -1;
            else
                pos = s.indexOf('\\', lastPos);
        }
        if (lastPos != s.length())
            sb.append(s.substring(lastPos));
        if (logCat.isDebugEnabled())
            logCat.debug("doubleSlash(" + s + ") ==> " + sb.toString());
        return sb.toString();
    }

    /**
     * Replace any of the five characters <, >, ', ", and & with the
     * appropriate xml entity. All xml processors must support these
     * five.
     *
     * @param s the input string
     * @return the output string
     */
    String es(String s) {
        // lt, gt, apos, quot, and amp
        if (s == null)
            return s;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '&':
                    if (entityAtPosition(s, i))
                        sb.append('&');
                    else
                        sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Return true if the & starting at position i in string s is an
     * entity. Taken to be &\S+; (in perl regexp terms)
     *
     * @param s the string
     * @param pos the starting postion
     * @return true or false
     */
    private boolean entityAtPosition(String s, int pos) {
        // Look forward from i. If a space or end of string
        // is found before a ; then return false;
        for (int i = pos; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == ';')
                return true;
            if (Character.isWhitespace(c))
                return false;
        }
        return false;
    }

    public Object clone()
        throws CloneNotSupportedException {

        Statement s = (Statement)super.clone();
        s.setupValueArrays();

        return s;
    }

    void startStatement(Token tok,
            Lexer lex,
            ContentHandler handler,
            NamespaceTracker tracker)
        throws SyntaxException, IOException {

        if (logCat.isDebugEnabled())
            logCat.debug("startStatement(" +
                    tok +
                    ", " +
                    lex +
                    ", " +
                    handler +
                    ", " +
                    tracker +
                    ")");

        int startingIndent = tok.getIndent();
        AttributesImpl attributes = new AttributesImpl();

        actOnFirstToken(tok);

        initRequiredFields(lex, startingIndent);
        initOptionalFields(lex, startingIndent);

        //List namespaces = null;

        try {

            //namespaces = startStatementNamespaces(startingIndent, namespaces, lex);
            startStatementNamespaces(startingIndent, lex);

            // Output namespaces
            startPrefixMapping(handler, tracker);

            addXMLRequiredFields(attributes, tracker);
            addXMLOptionalFields(attributes, tracker);

            // Output start element
            String localName = getXMLLocalName();
            String qName = getXMLQualifiedName();
            String namespace = getNamespace(tracker);

            if (logCat.isDebugEnabled())
                logCat.debug("handler.startElement( " +
                        namespace + ", " +
                        localName + ", " +
                        qName + ", " +
                        attributes + ")");

            handler.startElement(namespace,
                    localName,
                    qName,
                    attributes);

            startStatementChildren(startingIndent, lex, handler, tracker);

            if (logCat.isDebugEnabled())
                logCat.debug("handler.endElement( " +
                        namespace + ", " +
                        localName + ", " +
                        qName + ")");

            // Output end element
            handler.endElement(namespace,
                    localName,
                    qName);

            // Output any close namespace things
            //endPrefixMapping(namespaces, handler, tracker);
            endPrefixMapping(handler, tracker);

        } catch (SAXException se) {
            logCat.warn("SyntaxException");
            SyntaxException syne = new SyntaxException(se);
            syne.setLineNumber(lex.getLineNumber());
            syne.setIndent(lex.getIndent());
            throw syne;
        }
    }

    void actOnFirstToken(Token tok)
        throws SyntaxException {

    }

    void startStatementChildren(int startingIndent,
            Lexer lex,
            ContentHandler handler,
            NamespaceTracker tracker)
        throws SyntaxException, IOException {
        if (logCat.isDebugEnabled())
            logCat.debug("startStatementChildren(" +
                    startingIndent +
                    ", " +
                    lex +
                    ", " +
                    handler +
                    ", " +
                    tracker +
                    ")");

        // Process any children
        while (lex.hasNext(startingIndent)) {
            // Create child
            Token tok = lex.next();

            if (logCat.isDebugEnabled())
                logCat.debug("startStatementChildren(...) tok " +
                        tok);
            //Statement stat = modifyChild(factory.getStatementNoClone(tok, lex));
            Statement stat = modifyChild(factory.getStatementNoInit(tok, lex));

            stat.startStatement(tok, lex, handler, tracker);
        }
    }

    //List startStatementNamespaces(int startingIndent, List namespaces, Lexer lex)
    void startStatementNamespaces(int startingIndent, Lexer lex)
        throws SyntaxException, IOException {

        while (lex.hasNext(startingIndent)) {
            Token tok = lex.next();
            // Handle the problem of params
            if (tok.getValue().equals("(")) {
                lex.push(tok);
                break;
            }
            // Check for namespaces
            //Statement stat = factory.getStatementNoClone(tok, lex);
            Statement stat = factory.getStatementNoInit(tok, lex);
            if (stat instanceof Namespace) {
                // 		if (namespaces == null)
                // 		    namespaces = new ArrayList();
                try {
                    stat = (Statement)stat.clone();
                } catch (CloneNotSupportedException cnse) {
                    logCat.warn("CloneNotSupportedException cnse");
                    throw new SyntaxException(cnse);
                }
                stat.init(tok, lex);
                namespaces.add(stat);
            } else {
                // Push back any nonnamespace thing we see
                lex.push(tok);
                // End looking for namespaces
                break;
            }
        }
        //return namespaces;
        return;
    }

    Statement modifyChild(Statement stat) {
        return stat;
    }
}
