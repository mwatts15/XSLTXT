;;; xsltxt-mode.el --- Major mode for editing xsltxt files

;; Copyright (C) 2002 Alex Moffat
;; Keywords: xslt
;; CVS: $ID$

;;; This file is not part of GNU Emacs.

;; This file is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation; either version 2, or (at your option)
;; any later version.

;; This file is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with GNU Emacs; see the file COPYING.  If not, write to the
;; Free Software Foundation, Inc., 59 Temple Place - Suite 330,
;; Boston, MA 02111-1307, USA.

;;; Commentary:

;; Purpose
;;
;; Provides a very simple mode for editing xsltxt files.
;; Highlights keywords etc. supports comment-region and uncomment-region.
;; 

(define-derived-mode xsltxt-mode fundamental-mode "xsltxt"
  "Major mode for xsltxt.
\\{xsltxt-mode-map}"
  (make-local-variable 'font-lock-defaults)
  (let ((keywords (list "apply" "apply-imports" "attribute"
			"attribute-set" "call" "cdata" "choose"
			"comment" "copy" "copy-of" "decimal"
			"element" "fallback" "for-each"
			"if" "import" "include" "key" "msg"
			"msg-fatal" "namespace" "namespace-alias"
			"number" "otherwise" "output" "param"
			"preserve" "processing-instruction"
			"sort" "strip" "stylesheet" "tpl" "tx"
			"tx-no-esc" "val" "val-no-esc" "var"
			"when"
			`("\\(\\.\\w+\\)\\s-+" .
			  (1 font-lock-variable-name-face nil nil))
			`("^\\s-*\\(#.*\\)$" .
			  (1 font-lock-comment-face t t)))))
    (setq font-lock-defaults
	  (list keywords nil nil nil nil)))
  (make-local-variable 'comment-use-syntax)
  (setq comment-use-syntax nil)
  (make-local-variable 'comment-start)
  (setq comment-start "#")
  (make-local-variable 'comment-start-skip)
  (setq comment-start-skip "^\\s-*# ")
  (make-local-variable 'xs-jump-target)
  (setq xs-jump-target nil))

(define-key xsltxt-mode-map "\C-c\C-j" 'xs-jump-to)
(define-key xsltxt-mode-map "\C-c\C-r" 'xs-repeat-jump-to)

(defun xs-jump-to ()
  "Jump to a named template or a mode"
  (interactive)
  (bounds-of-thing-at-point `word)
  (let* ((r (bounds-of-thing-at-point 'word))
	 (s (car r))
	 (e (cdr r))
	 (name (buffer-substring-no-properties s e))
	 (pos
	  (save-excursion
	    (forward-line -1)
	    (let ((bol (point)))
	      (goto-char e)
	      (if (re-search-backward
		   (concat "\\(call\\|\\.mode\\)\\s-+\"" name) bol t)
		  (progn
		    (setq xs-jump-target
			  (concat "\\."
				  (if (equal (substring (match-string 1) 0 4) "call")
				      "name"
				    "mode")
				  "\\s-+\""
				  name
				  "\""))
		    (goto-char e)
		    (xs-find-next xs-jump-target))
		(message "Can not find preceeding call or .mode")
		nil)))))
    (if pos
	(goto-char pos))))

(defun xs-repeat-jump-to ()
  "Repeat the last xs-jump-to"
  (interactive)
  (let ((pos
	 (save-excursion
	   (end-of-line 1)
	   (xs-find-next xs-jump-target))))
    (if pos
	(goto-char pos))))

(defun xs-find-next (regexp)
  ""
  (or (xs-find-forward xs-jump-target)
      (progn
	(message "Jump wrapped ...")
	(goto-char (point-min))
	(xs-find-forward xs-jump-target))))

(defun xs-find-forward (regexp)
  (if (re-search-forward regexp (point-max) t)
      (progn
	(forward-line 0)
	(point))
    nil))
