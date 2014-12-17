" Vim syntax file
" Language: XSLTXT
" Maintainer: Mark Watts
" Latest Revision: 16 Dec 2014

if exists("b:current_syntax")
    finish
else
    syntax clear
endif

setlocal iskeyword+=-
syn include @xmlSyn $VIMRUNTIME/syntax/xml.vim
" Keywords
syn keyword xslElementKeyword param tpl for-each val when otherwise var if apply call attribute tx-no-esc choose import number tx msg copy-of copy
syn keyword xslHeaderKeyword namespace output stylesheet
"" Matches
"syn match syntaxElementMatch 'regexp' contains=syntaxElement1 nextgroup=syntaxElement2 skipwhite
syn match xmlElement '<[^>]\+>' contains=@xmlSyn
syn match xslComment '#.*' contains=xmlTodo
syn match xslAttribute '\.[^ ]*' contains=xmlAttrib
syn region  xslString start=+"+ end=+"+
syn keyword xmlTodo         contained TODO FIXME XXX

"" Regions
"syn region syntaxElementRegion start='x' end='y'

hi def link xslElementKeyword        Statement
hi def link xslHeaderKeyword         PreProc
hi def link xmlElement               Constant
hi def link xslComment               Comment
hi def link xmlTodo                  TODO
hi def link xslString		         String
hi def link xslAttribute		     Statement
