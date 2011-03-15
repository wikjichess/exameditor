/*

Copyright (C) 2011 Steffen Dienst

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package de.elatexam.editor.pages.subtaskdefs;

import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;

/**
 * @author Steffen Dienst
 *
 */
public class EllipsisModel extends AbstractWrapModel<String> {

	private IModel<String> wrapped;
	private final int maxLen;

	/**
	 * @param createLabelModel
	 */
	public EllipsisModel(IModel<String> labelModel, int maxLen) {
		this.wrapped = labelModel;
		this.maxLen = maxLen;
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.model.IWrapModel#getWrappedModel()
	 */
	@Override
	public IModel<?> getWrappedModel() {
		return wrapped;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.wicket.model.AbstractWrapModel#getObject()
	 */
	@Override
	public String getObject() {
		String s = wrapped.getObject();
		String stripped = s.replaceAll("\\<.*?>","");
		if(maxLen<stripped.length())
			return stripped.substring(0,maxLen)+"...";
		else
			return s;
	}

}
