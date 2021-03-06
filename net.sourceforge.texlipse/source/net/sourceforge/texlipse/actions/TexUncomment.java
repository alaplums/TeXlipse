/*
 * $Id$
 *
 * Copyright (c) 2004-2005 by the TeXlapse Team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.sourceforge.texlipse.actions;

import net.sourceforge.texlipse.TexlipsePlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Laura Takkinen
 *
 * Listens uncomment actions.
 */
public class TexUncomment implements IEditorActionDelegate {
	private IEditorPart targetEditor;
	private static TexSelections selection;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = targetEditor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		selection = new TexSelections(getTextEditor());
		uncomment();
	}
	
	/**
	 * This function returns the text editor.
	 */
	private ITextEditor getTextEditor() {
		if (targetEditor instanceof ITextEditor) {
			return (ITextEditor) targetEditor;
		} else {
			throw new RuntimeException("Expecting text editor. Found:"+targetEditor.getClass().getName());
		}
	}

	/**
	 * Uncomments selected lines of text.
	 */
	private void uncomment() {
        StringBuffer strbuf = new StringBuffer();
        selection.selectCompleteLines();

        try {
            // For each line, comment them out
            for (int i = selection.getStartLineIndex(); i <= selection.getEndLineIndex(); i++) {
                String line = selection.getLine(i);
                
                //we may want to remove comments that have leading whitespace
                line = line.trim();
                if (line.startsWith("% ")) {
                    strbuf.append(line.replaceFirst("% ", "") + (i < selection.getEndLineIndex() ? selection.getEndLineDelim() : ""));
                } else if (line.startsWith("%")) {
                        strbuf.append(line.replaceFirst("%", "") + (i < selection.getEndLineIndex() ? selection.getEndLineDelim() : ""));
                } else {
                    strbuf.append(line + (i < selection.getEndLineIndex() ? selection.getEndLineDelim() : ""));
                }
            }
            // Replace the text with the modified information
            selection.getDocument().replace(selection.getStartLine().getOffset(), selection.getSelLength(), strbuf.toString());
            
        } catch (Exception e) {
        	TexlipsePlugin.log("TexUncomment.uncomment(): ", e);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof TextSelection) {
			action.setEnabled(true);
			return;
		}
		action.setEnabled( targetEditor instanceof ITextEditor);
	}
}
